#!/usr/bin/env python3
"""Non-destructive checks for MascotaDatos Android 1.3.11."""
import pathlib
import re
import sys

root = pathlib.Path(__file__).resolve().parents[1]
html_path = root / "app/src/main/assets/index.html"
gradle_path = root / "app/build.gradle.kts"
java_path = root / "app/src/main/java/cl/mascotadatos/app/MainActivity.java"
errors = []
for p in (html_path, gradle_path, java_path):
    if not p.is_file(): errors.append(f"Missing: {p.relative_to(root)}")
if errors:
    print("\n".join(errors))
    sys.exit(1)
html = html_path.read_text(encoding="utf-8")
gradle = gradle_path.read_text(encoding="utf-8")
java = java_path.read_text(encoding="utf-8")
if not re.search(r'versionName\s*=\s*"1\.3\.11"', gradle): errors.append("versionName must be 1.3.11")
if not re.search(r'versionCode\s*=\s*1311\b', gradle): errors.append("versionCode must be 1311")
if not re.search(r'applicationId\s*=\s*"cl\.mascotadatos\.app"', gradle): errors.append("App ID changed")
if "mascotaDatosBack" not in java: errors.append("Android Back bridge missing")
if "window.mascotaDatosBack" not in html: errors.append("Web Back handler absent")
if re.search(r'scrollTo\s*\(\s*\{\s*top\s*:\s*0\s*,\s*behavior\s*:\s*[\x27\x22]smooth', html): errors.append("Forced animated scroll-to-top remains")
from html.parser import HTMLParser
class ElementIds(HTMLParser):
    def __init__(self):
        super().__init__()
        self.ids = []
    def handle_starttag(self, tag, attrs):
        self.ids.extend(v for k,v in attrs if k.lower() == "id" and v)
parsed = ElementIds()
parsed.feed(html)
ids = parsed.ids
dupes = sorted({v for v in ids if ids.count(v) > 1})
if dupes: errors.append("Duplicate HTML IDs: " + ", ".join(dupes[:25]))
if "veterinarias" not in html.lower(): errors.append("Veterinarias screen absent")
if "logo" not in html.lower(): errors.append("Logo refs absent")

# Publication blockers checked against the actual bundled HTML.
if re.search(r"Aplicaci[oó]n en etapa de pruebas", html, re.I): errors.append("Outdated trial-stage banner remains")
if re.search(r"MascotaDatos\\s+v32", html, re.I): errors.append("Outdated v32 label remains")
if '<section id="publicidadSolicitud" class="view">' not in html: errors.append("Advertising screen not navigable")
if "Veterinarias por ciudad" not in html: errors.append("City veterinary access missing")
if errors:
    for e in errors: print("FAIL:", e)
    sys.exit(1)
print("PASS: static checks. Does not prove Android functionality or signature compatibility.")
