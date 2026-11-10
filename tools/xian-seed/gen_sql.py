#!/usr/bin/env python3
"""把 data.json 生成为 Flyway 种子迁移 SQL（幂等：按名称去重）。"""
import json
import os

OUT = "../../server/src/main/resources/db/migration/V21__seed_xian_markets_merchants_products.sql"


def esc(s):
    return (s or "").replace("'", "''")


def main():
    with open("data.json", encoding="utf-8") as f:
        data = json.load(f)

    lines = [
        "-- 西安市场/品牌门店/商品补充种子数据",
        "-- 市场、门店资料整理自公开资料；联系电话均为演示号码（138001381xx），上线前需替换为商户真实号码；",
        "-- 坐标按市场地址估算，可在后台修正；图片为公开网络图片裁剪，仅作演示，商用前需替换为商户授权图片。",
        "",
        "-- ============ 市场 ============",
    ]
    for m in data["markets"]:
        logo = "/demo/xian/" + m["file"]
        lines.append(
            "INSERT INTO market (name, province, city, address, latitude, longitude, logo, intro, sort, status)\n"
            "SELECT '%s', '%s', '%s', '%s', %.6f, %.6f, '%s', '%s', %d, 1\n"
            "WHERE NOT EXISTS (SELECT 1 FROM market WHERE name = '%s');"
            % (esc(m["name"]), esc(m["province"]), esc(m["city"]), esc(m["address"]),
               m["lat"], m["lng"], logo, esc(m["intro"]), m["sort"], esc(m["name"]))
        )
        lines.append("")

    lines.append("-- ============ 已有市场补充门面图（不覆盖后台已维护图片） ============")
    for m in data.get("market_updates", []):
        logo = "/demo/xian/" + m["file"]
        lines.append(
            "UPDATE market SET logo = '%s' WHERE name = '%s' AND (logo IS NULL OR logo = '');"
            % (logo, esc(m["name"]))
        )
    lines.append("")

    lines.append("-- ============ 已有门店补充门面图（不覆盖后台已维护图片） ============")
    for m in data.get("merchant_updates", []):
        logo = "/demo/xian/" + m["file"]
        lines.append(
            "UPDATE merchant SET logo = '%s' WHERE name = '%s' AND (logo IS NULL OR logo = '');"
            % (logo, esc(m["name"]))
        )
    lines.append("")

    lines.append("-- ============ 品牌门店 ============")
    for m in data["merchants"]:
        logo = "/demo/xian/" + m["file"]
        category = ("(SELECT id FROM category WHERE name = '%s')" % esc(m["category"])
                    if m["category"] else "NULL")
        lines.append(
            "INSERT INTO merchant (name, contact_name, phone, market, market_id, province, city, address, logo, intro, category_id, status, views, forwards, latitude, longitude)\n"
            "SELECT '%s', '%s', '%s', '%s', (SELECT id FROM market WHERE name = '%s'), '%s', '%s', '%s', '%s', '%s', %s, 1, 0, 0, %.6f, %.6f\n"
            "WHERE NOT EXISTS (SELECT 1 FROM merchant WHERE name = '%s');"
            % (esc(m["name"]), esc(m["contact"]), esc(m["phone"]), esc(m["market"]),
               esc(m["market"]), esc(m["province"]), esc(m["city"]), esc(m["address"]),
               logo, esc(m["intro"]), category, m["lat"], m["lng"], esc(m["name"]))
        )
        lines.append("")

    lines.append("-- ============ 商品 ============")
    for p in data["products"]:
        image = "/demo/xian/" + p["file"]
        category = ("(SELECT id FROM category WHERE name = '%s')" % esc(p["category"])
                    if p["category"] else "NULL")
        lines.append(
            "INSERT INTO product (merchant_id, category_id, name, brand, origin, spec, type_name, price, unit, images, description, status, views, forwards)\n"
            "SELECT (SELECT id FROM merchant WHERE name = '%s' ORDER BY id LIMIT 1), %s, '%s', '%s', '%s', '%s', '%s', %.2f, '%s', '%s', '%s', 1, 0, 0\n"
            "WHERE NOT EXISTS (SELECT 1 FROM product WHERE merchant_id = (SELECT id FROM merchant WHERE name = '%s' ORDER BY id LIMIT 1) AND name = '%s');"
            % (esc(p["merchant"]), category, esc(p["name"]), esc(p["brand"]), esc(p["origin"]),
               esc(p["spec"]), esc(p["typeName"]), p["price"], esc(p["unit"]), image,
               esc(p["description"]), esc(p["merchant"]), esc(p["name"]))
        )
        lines.append("")

    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    with open(OUT, "w", encoding="utf-8", newline="\n") as f:
        f.write("\n".join(lines).rstrip() + "\n")
    print("wrote", OUT, len(lines), "lines")


if __name__ == "__main__":
    main()
