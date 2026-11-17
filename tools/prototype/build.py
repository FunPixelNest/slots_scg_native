"""Builds a single-file prototype: template + assets.js + script.

python3 tools/prototype/build.py <out.html> [template.html] [script.js]
v1 (reference-style): template.html + proto.js     v2 (Felt & Brass): template2.html + proto2.js
"""
import os, sys
HERE = os.path.dirname(os.path.abspath(__file__))
out = sys.argv[1]
tpl = sys.argv[2] if len(sys.argv) > 2 else "template2.html"
js = sys.argv[3] if len(sys.argv) > 3 else "proto2.js"
t = open(os.path.join(HERE, tpl)).read()
t = t.replace("<!--ASSETS-->", "<script>" + open(os.path.join(HERE, "assets.js")).read() + "</script>")
t = t.replace("<!--JS-->", "<script>" + open(os.path.join(HERE, js)).read() + "</script>")
open(out, "w").write(t)
print(out, round(os.path.getsize(out) / 1e6, 2), "MB")
