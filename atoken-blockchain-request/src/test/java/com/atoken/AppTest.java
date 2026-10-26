package com.atoken;

import static org.junit.Assert.assertTrue;

import com.alibaba.fastjson.JSON;
import com.atoken.blockchain.http.request.Request;
import com.atoken.blockchain.http.request.enums.Method;
import com.atoken.blockchain.utils.DateUtil;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Unit test for simple App.
 */
public class AppTest 
{
    /**
     * Rigorous Test :-)
     */
    @Test
    public void shouldAnswerWithTrue() throws ParseException {
        String temp = "2019-07-03T07:03:56.761921756Z".substring(0, "2019-07-03T07:03:56.761921756Z".indexOf('.')) + "Z";
        System.out.println("temp = " + temp);
        System.out.println("\"yyyy-MM-dd'T'HH:mm:ss'Z'\".substring(\"yyyy-MM-dd'T'HH:mm:ss'Z'\".length() - 3) = " + "yyyy-MM-dd'T'HH:mm:ss'Z'".substring("yyyy-MM-dd'T'HH:mm:ss'Z'".length() - 3));
        Date date1 = DateUtil.strToDate(temp, DateUtil.FORMAT_ALL);
        System.out.println("date1.getTime() / 1000 = " + date1.getTime() / 1000);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
        Date date = simpleDateFormat.parse(temp);
        System.out.println("date.getTime() = " + date.getTime() / 1000);
        System.out.println("new Date().getTime() = " + new Date().getTime() / 1000);
        System.out.println("date = " + date);
        Calendar cal = Calendar.getInstance();
        
    }

    @Test
    public void testTime() {
        List<String> dbValidatorYearYields = readFile("/home/qingyunyu/DFG/workspace/java/atoken-update-parent/atoken-blockchain-request/src/test/java/com/atoken/db.txt");
        String imtokenYearYield = readFileStr("/home/qingyunyu/DFG/workspace/java/atoken-update-parent/atoken-blockchain-request/src/test/java/com/atoken/imtoken.txt");
        List<ValidatorYearYield> validatorYearYields = JSON.parseArray(imtokenYearYield, ValidatorYearYield.class);
        Map<String, Double> validatorYearYieldMap = new HashMap<>();
        validatorYearYields.forEach(validatorYearYield -> validatorYearYieldMap.put(validatorYearYield.getOperatorAddress(), validatorYearYield.getAnnualizedReturns()));
        System.out.println(validatorYearYieldMap);
        Map<String, Double> dbValidatorYearYieldMap = new HashMap<>();
        dbValidatorYearYields.forEach(dbValidatorYearYield -> {
            String[] temps = dbValidatorYearYield.split("\\|");
            dbValidatorYearYieldMap.put(temps[0], Double.valueOf(temps[2]));
        });
        System.out.println("address: db_year_yield: imtoken_year_yield: (db_year_yield - imtoken_year_yield)");

        dbValidatorYearYieldMap.entrySet().forEach(dbValidatorYearYield -> {
            System.out.println(dbValidatorYearYield.getKey() + ": " + dbValidatorYearYield.getValue() + ": " + validatorYearYieldMap.get(dbValidatorYearYield.getKey()) + ": " + (new BigDecimal(dbValidatorYearYield.getValue()).add(new BigDecimal(validatorYearYieldMap.get(dbValidatorYearYield.getKey())).negate())));
        });
    }


    private static List<String> readFile(String fileName) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            List<String> result = new ArrayList<>();
            String tmpStr;
            while((tmpStr = bufferedReader.readLine()) != null) {
                result.add(tmpStr);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(bufferedReader)) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private static String readFileStr(String fileName) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader(fileName));
            StringBuilder entityBuilder = new StringBuilder();
            String tmpStr;
            while((tmpStr = bufferedReader.readLine()) != null) {
                entityBuilder.append(tmpStr);
                entityBuilder.append("\n");
            }
            return entityBuilder.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (Objects.nonNull(bufferedReader)) {
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static class ValidatorYearYield {
        private String operatorAddress;
        private Double annualizedReturns;

        public String getOperatorAddress() {
            return this.operatorAddress;
        }

        public void setOperatorAddress(String operatorAddress) {
            this.operatorAddress = operatorAddress;
        }

        public Double getAnnualizedReturns() {
            return this.annualizedReturns;
        }

        public void setAnnualizedReturns(Double annualizedReturns) {
            this.annualizedReturns = annualizedReturns;
        }
    }

    @Test
    public void text() {
        System.out.println(Integer.valueOf("0xf".substring(2), 16));
    }

    @Test
    public void testReplace() {
        String version = "3.2.87";
        System.out.println("version.replaceAll(\"\\\\.\", \"\") = " + version.replaceAll("\\.", ""));
        System.out.println("3.2.87".split("\\.").length);
        System.out.println(System.getProperties());
    }

    @Test
    public void testJSON() {
        TestJson testJson = new TestJson();
        testJson.setAge(1);
        testJson.setName("1");
        String testJ = JSON.toJSONString(testJson);
        System.out.println("JSON.toJSONString(testJson) = " + testJ);
        TestJson testJson2 = JSON.parseObject(testJ, TestJson.class);
    }

    @Test
    public void testDex() {
        new Request().url("https://api.1inch.exchange/v1.1/exchanges").method(Method.GET).send(String.class);
    }

    public static class TestJson {
        private String name;
        private Integer age;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }
    }

    @Test
    public void test(){
        System.out.println(LocalDateTime.now().toEpochSecond(ZoneOffset.of("+8")));
    }
}
