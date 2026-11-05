package com.jiancai.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiancai.common.BusinessException;
import com.jiancai.entity.Category;
import com.jiancai.entity.Merchant;
import com.jiancai.entity.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 商品 CSV 导入导出（UTF-8 BOM，Excel 可直接打开）
 */
@Service
public class ProductCsvService {

    private static final int MAX_ROWS = 2000;
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024L;
    private static final int MAX_ERRORS = 100;
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static final String[] EXPORT_HEADERS = {
            "商户手机号", "商户名称", "商品名称", "品类", "品牌", "产地", "规格", "型号",
            "价格", "单位", "图片", "描述", "状态", "浏览量", "转发量", "电话联系", "创建时间"
    };

    @Autowired
    private ProductService productService;

    @Autowired
    private MerchantService merchantService;

    @Autowired
    private CategoryService categoryService;

    @Transactional
    public Map<String, Object> importCsv(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("请选择要导入的 CSV 文件");
        }
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("文件不能超过 5MB");
        }
        List<List<String>> rows = parseCsv(decode(file));
        if (rows.isEmpty()) {
            throw new BusinessException("文件内容为空");
        }

        List<String> headers = rows.get(0).stream().map(this::normalizeHeader).toList();
        Map<String, Integer> columns = new HashMap<>();
        for (int i = 0; i < headers.size(); i++) {
            columns.putIfAbsent(headers.get(i), i);
        }
        Integer phoneCol = column(columns, "商户手机号", "手机号");
        Integer nameCol = column(columns, "商品名称", "产品名称");
        Integer categoryCol = column(columns, "品类", "分类");
        if (phoneCol == null || nameCol == null || categoryCol == null) {
            throw new BusinessException("表头缺少必填列：商户手机号、商品名称、品类");
        }
        Integer brandCol = column(columns, "品牌");
        Integer originCol = column(columns, "产地");
        Integer specCol = column(columns, "规格");
        Integer typeCol = column(columns, "型号", "类型");
        Integer priceCol = column(columns, "价格", "单价");
        Integer unitCol = column(columns, "单位");
        Integer imagesCol = column(columns, "图片", "图片地址");
        Integer descCol = column(columns, "描述", "简介");

        Map<String, List<Merchant>> merchantsByPhone = merchantService.list().stream()
                .filter(m -> m.getPhone() != null && !m.getPhone().isBlank())
                .collect(Collectors.groupingBy(m -> m.getPhone().trim()));
        Map<String, List<Category>> categoriesByName = categoryService.listAll().stream()
                .collect(Collectors.groupingBy(c -> c.getName().trim()));

        List<Map<String, Object>> errors = new ArrayList<>();
        List<RowProduct> parsed = new ArrayList<>();
        Set<String> fileKeys = new HashSet<>();
        int dataRows = 0;
        for (int i = 1; i < rows.size(); i++) {
            List<String> row = rows.get(i);
            if (row.stream().allMatch(cell -> cell == null || cell.isBlank())) {
                continue;
            }
            dataRows++;
            if (dataRows > MAX_ROWS) {
                throw new BusinessException("单次最多导入 " + MAX_ROWS + " 条商品，请拆分文件");
            }
            int rowNo = i + 1;
            String phone = cell(row, phoneCol);
            String name = cell(row, nameCol);
            String categoryName = cell(row, categoryCol);
            Product product = new Product();
            List<String> rowErrors = new ArrayList<>();

            Merchant merchant = null;
            if (phone.isEmpty()) {
                rowErrors.add("商户手机号不能为空");
            } else {
                List<Merchant> matched = merchantsByPhone.get(phone);
                if (matched == null || matched.isEmpty()) {
                    rowErrors.add("商户手机号 " + phone + " 不存在");
                } else if (matched.size() > 1) {
                    rowErrors.add("手机号 " + phone + " 对应多个商户，无法确定归属");
                } else {
                    merchant = matched.get(0);
                    if (!Integer.valueOf(1).equals(merchant.getStatus())) {
                        rowErrors.add("商户「" + merchant.getName() + "」未通过审核");
                    }
                }
            }
            if (name.isEmpty()) {
                rowErrors.add("商品名称不能为空");
            } else if (name.length() > 128) {
                rowErrors.add("商品名称不能超过 128 字");
            }
            if (categoryName.isEmpty()) {
                rowErrors.add("品类不能为空");
            } else {
                List<Category> matchedCategories = categoriesByName.get(categoryName);
                if (matchedCategories == null || matchedCategories.isEmpty()) {
                    rowErrors.add("品类「" + categoryName + "」不存在");
                } else if (matchedCategories.size() > 1) {
                    rowErrors.add("品类「" + categoryName + "」重名，请先在后台调整品类名称");
                } else {
                    product.setCategoryId(matchedCategories.get(0).getId());
                }
            }
            if (merchant != null) {
                product.setMerchantId(merchant.getId());
            }
            product.setName(name);
            product.setBrand(limit(cell(row, brandCol), 64, "品牌", rowErrors));
            product.setOrigin(limit(cell(row, originCol), 64, "产地", rowErrors));
            product.setSpec(limit(cell(row, specCol), 128, "规格", rowErrors));
            product.setTypeName(limit(cell(row, typeCol), 64, "型号", rowErrors));
            product.setUnit(limit(cell(row, unitCol), 16, "单位", rowErrors));
            product.setDescription(limit(cell(row, descCol), 2000, "描述", rowErrors));

            String priceText = cell(row, priceCol);
            if (!priceText.isEmpty()) {
                try {
                    BigDecimal price = new BigDecimal(priceText);
                    if (price.signum() < 0) {
                        rowErrors.add("价格不能为负数");
                    } else {
                        product.setPrice(price);
                    }
                } catch (NumberFormatException e) {
                    rowErrors.add("价格格式不正确：" + priceText);
                }
            }

            String imagesText = cell(row, imagesCol);
            if (!imagesText.isEmpty()) {
                List<String> images = new ArrayList<>();
                for (String image : imagesText.split(",")) {
                    String url = image.trim();
                    if (url.isEmpty()) {
                        continue;
                    }
                    if (!url.startsWith("/") && !url.startsWith("http://") && !url.startsWith("https://")) {
                        rowErrors.add("图片地址不合法：" + url);
                        break;
                    }
                    images.add(url);
                }
                product.setImages(String.join(",", images));
            }

            if (rowErrors.isEmpty()) {
                String key = product.getMerchantId() + "|" + name.trim() + "|" + product.getSpec();
                if (!fileKeys.add(key)) {
                    rowErrors.add("文件内存在重复商品（同商户、同名称、同规格）");
                }
            }
            if (!rowErrors.isEmpty()) {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("row", rowNo);
                error.put("message", String.join("；", rowErrors));
                errors.add(error);
                if (errors.size() >= MAX_ERRORS) {
                    throw new BusinessException("错误超过 " + MAX_ERRORS + " 条，请修正后重新导入");
                }
                continue;
            }
            parsed.add(new RowProduct(rowNo, product));
        }

        if (dataRows == 0) {
            throw new BusinessException("文件没有商品数据");
        }
        if (errors.isEmpty()) {
            checkDatabaseDuplicates(parsed, errors);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("total", dataRows);
        result.put("errors", errors);
        if (!errors.isEmpty()) {
            result.put("imported", 0);
            return result;
        }
        LocalDateTime now = LocalDateTime.now();
        for (RowProduct item : parsed) {
            Product product = item.product();
            product.setStatus(1);
            product.setViews(0);
            product.setForwards(0);
            product.setCalls(0);
            product.setCreateTime(now);
            product.setUpdateTime(now);
        }
        productService.saveBatch(parsed.stream().map(RowProduct::product).toList());
        result.put("imported", parsed.size());
        return result;
    }

    /** 平台端导出，最多 5000 条 */
    public byte[] exportCsv(String name, Long categoryId, Integer status, Long merchantId) {
        List<Product> products = productService.pageWithInfo(new Page<>(1, 5000),
                name, categoryId, status, merchantId).getRecords();
        return buildCsv(products);
    }

    /** 商户端导出自己的商品，最多 5000 条 */
    public byte[] exportMerchantCsv(Long merchantId, String name, Integer status) {
        List<Product> products = productService.pageWithInfo(new Page<>(1, 5000),
                name, null, status, merchantId).getRecords();
        return buildCsv(products);
    }

    private byte[] buildCsv(List<Product> products) {
        StringBuilder sb = new StringBuilder("\uFEFF");
        sb.append(joinCsv(EXPORT_HEADERS)).append("\r\n");
        for (Product product : products) {
            String[] values = {
                    product.getMerchantPhone(),
                    product.getMerchantName(),
                    product.getName(),
                    product.getCategoryName(),
                    product.getBrand(),
                    product.getOrigin(),
                    product.getSpec(),
                    product.getTypeName(),
                    product.getPrice() == null ? "" : product.getPrice().toPlainString(),
                    product.getUnit(),
                    product.getImages(),
                    product.getDescription(),
                    statusText(product.getStatus()),
                    String.valueOf(product.getViews() == null ? 0 : product.getViews()),
                    String.valueOf(product.getForwards() == null ? 0 : product.getForwards()),
                    String.valueOf(product.getCalls() == null ? 0 : product.getCalls()),
                    product.getCreateTime() == null ? "" : product.getCreateTime().format(TIME_FORMAT)
            };
            sb.append(joinCsv(values)).append("\r\n");
        }
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String joinCsv(String[] values) {
        List<String> cells = new ArrayList<>(values.length);
        for (String value : values) {
            cells.add(csvCell(value));
        }
        return String.join(",", cells);
    }

    private String csvCell(String value) {
        if (value == null) {
            return "\"\"";
        }
        return "\"" + value.replace("\"", "\"\"") + "\"";
    }

    private void checkDatabaseDuplicates(List<RowProduct> parsed, List<Map<String, Object>> errors) {
        Set<Long> merchantIds = parsed.stream().map(item -> item.product().getMerchantId())
                .collect(Collectors.toSet());
        if (merchantIds.isEmpty()) {
            return;
        }
        Map<String, Product> existing = new HashMap<>();
        productService.list(Wrappers.<Product>lambdaQuery()
                        .in(Product::getMerchantId, merchantIds)
                        .select(Product::getMerchantId, Product::getName, Product::getSpec))
                .forEach(p -> existing.put(key(p.getMerchantId(), p.getName(), p.getSpec()), p));
        for (RowProduct item : parsed) {
            Product product = item.product();
            if (existing.containsKey(key(product.getMerchantId(), product.getName(), product.getSpec()))) {
                Map<String, Object> error = new LinkedHashMap<>();
                error.put("row", item.row());
                error.put("message", "该商户已存在同名同规格商品");
                errors.add(error);
                if (errors.size() >= MAX_ERRORS) {
                    return;
                }
            }
        }
    }

    private String key(Long merchantId, String name, String spec) {
        return merchantId + "|" + (name == null ? "" : name.trim()) + "|" + (spec == null ? "" : spec.trim());
    }

    private String decode(MultipartFile file) {
        try {
            String text = new String(file.getBytes(), StandardCharsets.UTF_8);
            if (text.contains("\uFFFD")) {
                text = new String(file.getBytes(), Charset.forName("GBK"));
            }
            if (!text.isEmpty() && text.charAt(0) == '\uFEFF') {
                text = text.substring(1);
            }
            return text;
        } catch (IOException e) {
            throw new BusinessException("读取文件失败：" + e.getMessage());
        }
    }

    /** 支持双引号包裹、转义引号与字段内换行的最小 CSV 解析 */
    private List<List<String>> parseCsv(String text) {
        List<List<String>> rows = new ArrayList<>();
        List<String> row = new ArrayList<>();
        StringBuilder field = new StringBuilder();
        boolean inQuotes = false;
        boolean rowStarted = false;
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < text.length() && text.charAt(i + 1) == '"') {
                        field.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    field.append(c);
                }
            } else if (c == '"') {
                inQuotes = true;
                rowStarted = true;
            } else if (c == ',') {
                row.add(field.toString());
                field.setLength(0);
                rowStarted = true;
            } else if (c == '\n') {
                row.add(field.toString());
                field.setLength(0);
                rows.add(row);
                row = new ArrayList<>();
                rowStarted = false;
            } else if (c != '\r') {
                field.append(c);
                rowStarted = true;
            }
        }
        if (rowStarted || field.length() > 0 || !row.isEmpty()) {
            row.add(field.toString());
            rows.add(row);
        }
        return rows;
    }

    private Integer column(Map<String, Integer> columns, String... names) {
        for (String name : names) {
            Integer index = columns.get(name);
            if (index != null) {
                return index;
            }
        }
        return null;
    }

    private String normalizeHeader(String header) {
        if (header == null) {
            return "";
        }
        String value = header.trim();
        if (!value.isEmpty() && value.charAt(0) == '\uFEFF') {
            value = value.substring(1);
        }
        return value;
    }

    private String cell(List<String> row, Integer index) {
        if (index == null || index >= row.size()) {
            return "";
        }
        String value = row.get(index);
        return value == null ? "" : value.trim();
    }

    private String limit(String value, int maxLength, String label, List<String> errors) {
        if (value != null && value.length() > maxLength) {
            errors.add(label + "不能超过 " + maxLength + " 字");
            return value.substring(0, maxLength);
        }
        return value;
    }

    private String statusText(Integer status) {
        if (status == null) {
            return "";
        }
        return switch (status) {
            case 0 -> "待审核";
            case 1 -> "在售";
            case 2 -> "已拒绝";
            case 3 -> "已下架";
            default -> "未知";
        };
    }

    private record RowProduct(int row, Product product) {
    }
}
