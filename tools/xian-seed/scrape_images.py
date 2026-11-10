#!/usr/bin/env python3
"""从百度图片抓取商家门面/商品配图，统一裁剪尺寸并输出来源清单。

用法：
  python scrape_images.py                 # 全部（已存在的图跳过）
  python scrape_images.py --only markets/ # 只跑市场
  python scrape_images.py --force         # 覆盖重抓
来源清单 manifest.json 记录每张图的标题与原始地址，便于上线前复核/替换。
"""
import argparse
import hashlib
import http.cookiejar
import io
import json
import os
import re
import time
import urllib.parse
import urllib.request

from PIL import Image, ImageOps

UA = ("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
      "(KHTML, like Gecko) Chrome/125.0 Safari/537.36")

SEEN_URLS = set()
SEEN_HASH = set()


class Session:
    def __init__(self):
        cj = http.cookiejar.CookieJar()
        self.op = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(cj))
        self.op.addheaders = [
            ("User-Agent", UA),
            ("Accept-Language", "zh-CN,zh;q=0.9"),
            ("Accept", "text/html,application/json,*/*;q=0.8"),
        ]
        self.op.open("https://image.baidu.com/", timeout=20).read()

    def get(self, url, referer="https://image.baidu.com/", timeout=25):
        req = urllib.request.Request(url, headers={"Referer": referer, "User-Agent": UA})
        with self.op.open(req, timeout=timeout) as resp:
            return resp.read()


def load_manifest(path):
    if not os.path.exists(path):
        return
    try:
        with open(path, encoding="utf-8") as f:
            for row in json.load(f):
                if row.get("source"):
                    SEEN_URLS.add(row["source"])
    except Exception:
        pass


def search(sess, query, pages=3):
    qs = urllib.parse.quote(query)
    items = []
    for pn in range(0, pages * 30, 30):
        url = ("https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&ct=201326592"
               "&fp=result&queryWord=" + qs + "&cl=2&lm=-1&ie=utf-8&oe=utf-8&st=-1"
               "&word=" + qs + "&face=0&istype=2&nc=1&pn=%d&rn=30" % pn)
        raw = sess.get(url, referer="https://image.baidu.com/search/index?tn=baiduimage&word=" + qs)
        # 百度接口返回非标准 JSON（含非法控制符），先清理再解析
        text = raw.decode("utf-8", "ignore").replace("\\'", "'")
        try:
            data = json.loads(text)
        except Exception:
            text = re.sub(r"[\x00-\x1f]", " ", text)
            try:
                data = json.loads(text)
            except Exception:
                break
        rows = data.get("data") or []
        if not rows:
            break
        items.extend(rows)
    return [it for it in items if it.get("middleURL")]


def aspect_ok(w, h, mode):
    if w < 480 or h < 340:
        return False
    ratio = w / h
    if mode == "shop":
        return 1.05 <= ratio <= 2.35
    if mode == "product":
        return 0.66 <= ratio <= 1.5
    return True


def save_image(raw, path, mode):
    img = Image.open(io.BytesIO(raw))
    img = ImageOps.exif_transpose(img).convert("RGB")
    w, h = img.size
    if not aspect_ok(w, h, mode):
        return None
    size = (800, 600) if mode == "shop" else (800, 800)
    img = ImageOps.fit(img, size, method=Image.LANCZOS, centering=(0.5, 0.45))
    os.makedirs(os.path.dirname(path), exist_ok=True)
    img.save(path, "JPEG", quality=82, optimize=True)
    return size


def scrape(sess, item, out_dir, manifest, force=False):
    query = item["query"]
    mode = item.get("mode", "shop")
    rel = item["file"]
    path = os.path.join(out_dir, rel)
    if os.path.exists(path) and not force:
        manifest.append({"file": rel, "query": query, "source": "", "cached": True})
        print("skip", rel)
        return True
    seen_this = set()
    try:
        results = search(sess, query)
    except Exception as e:
        print("search failed", query, e)
        return False
    for it in results:
        w, h = int(it.get("width") or 0), int(it.get("height") or 0)
        if not aspect_ok(w, h, mode):
            continue
        if it.get("isCopyright") == 1 or it.get("is_gif") == 1:
            continue
        src = it["middleURL"]
        if src in SEEN_URLS or src in seen_this:
            continue
        seen_this.add(src)
        try:
            raw = sess.get(src)
            if len(raw) < 8000:
                continue
            digest = hashlib.md5(raw).hexdigest()
            if digest in SEEN_HASH:
                continue
            size = save_image(raw, path, mode)
            if not size:
                continue
            SEEN_URLS.add(src)
            SEEN_HASH.add(digest)
            manifest.append({
                "file": rel, "query": query, "source": src,
                "title": (it.get("fromPageTitleEnc") or "")[:120],
                "host": it.get("fromURLHost") or "",
                "size": list(size),
            })
            print("ok", rel, size, src[:90])
            return True
        except Exception:
            continue
    print("no suitable image", rel, query)
    return False


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument("--data", default="data.json")
    ap.add_argument("--out", default="../../server/src/main/resources/static/demo/xian")
    ap.add_argument("--only", default="", help="只处理包含该前缀的 file")
    ap.add_argument("--manifest", default="manifest.json")
    ap.add_argument("--sleep", type=float, default=0.8)
    ap.add_argument("--force", action="store_true")
    args = ap.parse_args()

    load_manifest(args.manifest)
    sess = Session()
    with open(args.data, encoding="utf-8") as f:
        data = json.load(f)
    manifest = []
    total = 0
    failed = []
    for group in ("markets", "market_updates", "merchants", "merchant_updates", "products"):
        for item in data.get(group, []):
            if args.only and not item["file"].startswith(args.only):
                continue
            total += 1
            if not scrape(sess, item, args.out, manifest, args.force):
                failed.append(item["file"])
            with open(args.manifest, "w", encoding="utf-8") as f:
                json.dump(manifest, f, ensure_ascii=False, indent=2)
            time.sleep(args.sleep)
    print("done", len(manifest), "/", total, "failed", len(failed))
    for f in failed:
        print("FAILED", f)


if __name__ == "__main__":
    main()
