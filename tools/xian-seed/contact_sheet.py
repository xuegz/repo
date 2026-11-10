#!/usr/bin/env python3
"""生成联系表（contact sheet），用于人工复核抓取图片是否相关。"""
import argparse
import json
import os

from PIL import Image, ImageDraw, ImageFont

FONT = "C:/Windows/Fonts/msyh.ttc"


def font(size):
    try:
        return ImageFont.truetype(FONT, size)
    except Exception:
        return ImageFont.load_default()


def sheet(items, out_dir, out_path, cols=6, cell=(260, 195)):
    rows = (len(items) + cols - 1) // cols
    w, h = cols * cell[0], rows * (cell[1] + 26)
    canvas = Image.new("RGB", (w, h), "white")
    draw = ImageDraw.Draw(canvas)
    f = font(14)
    for i, it in enumerate(items):
        x = (i % cols) * cell[0]
        y = (i // cols) * (cell[1] + 26)
        p = os.path.join(out_dir, it["file"])
        if os.path.exists(p):
            im = Image.open(p).convert("RGB")
            im.thumbnail(cell)
            canvas.paste(im, (x + (cell[0] - im.width) // 2, y + (cell[1] - im.height) // 2))
        label = it.get("name") or it.get("file", "")
        draw.text((x + 4, y + cell[1] + 3), label[:24], fill="black", font=f)
    os.makedirs(os.path.dirname(out_path), exist_ok=True)
    canvas.save(out_path, "JPEG", quality=85)
    print(out_path, w, "x", h)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default="data.json")
    ap.add_argument("--out", default="../../server/src/main/resources/static/demo/xian")
    ap.add_argument("--review", default="_review")
    args = ap.parse_args()
    with open(args.data, encoding="utf-8") as f:
        data = json.load(f)
    sheet(data["markets"], args.out, os.path.join(args.review, "markets.jpg"))
    sheet(data["merchants"], args.out, os.path.join(args.review, "merchants.jpg"))
    if data.get("market_updates"):
        sheet(data["market_updates"], args.out, os.path.join(args.review, "markets-update.jpg"))
    if data.get("merchant_updates"):
        sheet(data["merchant_updates"], args.out, os.path.join(args.review, "merchants-update.jpg"))
    for i in range(0, len(data["products"]), 24):
        sheet(data["products"][i:i + 24], args.out,
              os.path.join(args.review, "products-%02d.jpg" % (i // 24 + 1)), cols=6)


if __name__ == "__main__":
    main()
