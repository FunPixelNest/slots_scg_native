"""Builds the single-file redesign prototype: template.html + assets.js + proto.js.

python3 tools/prototype/build.py <out.html>
"""
import os, sys
HERE = os.path.dirname(os.path.abspath(__file__))
t = open(os.path.join(HERE, "template.html")).read()
t = t.replace("<!--ASSETS-->", "<script>" + open(os.path.join(HERE, "assets.js")).read() + "</script>")
t = t.replace("<!--JS-->", "<script>" + open(os.path.join(HERE, "proto.js")).read() + "</script>")
open(sys.argv[1], "w").write(t)
print(sys.argv[1], round(os.path.getsize(sys.argv[1]) / 1e6, 2), "MB")
