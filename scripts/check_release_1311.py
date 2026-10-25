#!/usr/bin/env python3
"""Non-destructive checks for MascotaDatos Android 1.3.11."""
import pathlib
import re
import sys

root = pathlib.Path(__file__).resolve().parents[1]
html_path = root / "app/src/main/assets/index.html"
gradle_path = root / "app/build.gradle.kts"
java_path = root / "app/src/main/java/cl/mascotadatos/app/MainActivity.java"
manifest_path = root / "app/src/main/AndroidManifest.xml"
errors = []
for p in (html_path, gradle_path, java_path):
    if not p.is_file(): errors.append(f"Missing: {p.relative_to(root)}")
if errors:
    print("\n".join(errors))
    sys.exit(1)
html = html_path.read_text(encoding="utf-8")
gradle = gradle_path.read_text(encoding="utf-8")
java = java_path.read_text(encoding="utf-8")
if not re.search(r'versionName\s*=\s*"1\.3\.16"', gradle): errors.append("versionName must be 1.3.16")
if not re.search(r'versionCode\s*=\s*1316\b', gradle): errors.append("versionCode must be 1316")
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
if "function showRecovery(){prefillKnownAccount();" in html:
    errors.append("Password recovery must not prefill a known account")
if re.search(r"function prefillKnownAccount\\(\\)\\s*\\{[^}]*\\.value\\s*=", html):
    errors.append("Personal email prefill is prohibited")
if html.count('id="md-privacy-contact"') != 1: errors.append("Account deletion contact missing or duplicate")
if "Solicitar eliminación por correo" not in html: errors.append("Account deletion request button missing")
if "mailto:alttorres@hotmail.com?subject=" not in html: errors.append("Privacy contact mailto missing")
if 'Intent.ACTION_SENDTO' not in java or '"mailto".equalsIgnoreCase(scheme)' not in java: errors.append("Android native mailto intent missing")
if "async function mdRequestDeletion()" not in html: errors.append("Authenticated deletion request handler missing")
if "sb.functions.invoke(\'account_deletion_requests\'" not in html: errors.append("Authenticated deletion API not invoked")
if "sb.from(\'account_deletion_requests\').insert" in html: errors.append("Client must not directly insert arbitrary account deletion IDs")
if "Registrar solicitud desde mi sesión" not in html: errors.append("Deletion request button missing")
if "https://torresalejandroaltamirano.github.io/MascotaDatos-/privacidad.html" not in html: errors.append("Public HTTPS privacy link missing")
if "https://torresalejandroaltamirano.github.io/MascotaDatos-/solicitar-eliminacion-cuenta.html" not in html: errors.append("Public HTTPS account deletion link missing")
if "veterinarias" not in html.lower(): errors.append("Veterinarias screen absent")
# La actualización jamás debe perder Favoritos ni los avisos guardados.
for marker in ('<section id="favoritos" class="view">', 'id="favoritesResults"',
               'function getFavoriteIds()', 'function setFavoriteIds(',
               'function toggleFavorite(', 'async function loadFavorites()',
               "show('favoritos');loadFavorites()"):
    if marker not in html:
        errors.append("Feature regression, Favoritos missing: " + marker)
if html.count('alt="Catedral de Copiapó"') != 1: errors.append("Official Copiapó cathedral image missing or duplicated")
if 'width:98px;height:68px;object-fit:contain' not in html: errors.append("Copiapó cathedral thumbnail not proportionate")
if "logo" not in html.lower(): errors.append("Logo refs absent")
if 'android:icon="@mipmap/ic_launcher"' not in manifest_path.read_text(encoding="utf-8"): errors.append("Official launcher icon not declared in Android manifest")
if 'android:screenOrientation="portrait"' not in manifest_path.read_text(encoding="utf-8"): errors.append("Portrait orientation not preserved from installed 1.3.10")
adaptive_icon_path = root / "app/src/main/res/mipmap-anydpi-v26/ic_launcher.xml"
official_logo_path = root / "app/src/main/res/drawable-nodpi/logo_mascotadatos.png"
if not adaptive_icon_path.is_file() or '@drawable/logo_mascotadatos' not in adaptive_icon_path.read_text(encoding="utf-8"):
    errors.append("Adaptive launcher icon must use the official MascotaDatos logo")
if not official_logo_path.is_file():
    errors.append("Official MascotaDatos drawable missing")

# Publication blockers checked against the actual bundled HTML.
if re.search(r"Aplicaci[oó]n en etapa de pruebas", html, re.I): errors.append("Outdated trial-stage banner remains")
if re.search(r"MascotaDatos\\s+v32", html, re.I): errors.append("Outdated v32 label remains")
if '<section id="publicidadSolicitud" class="view">' not in html: errors.append("Advertising screen not navigable")
if "Veterinarias por ciudad" not in html: errors.append("City veterinary access missing")
if html.count('<div class="card action" onclick="show(\'ciudades\')">') != 1:
    errors.append("Veterinarias home entry must be unique")
if "function mdRenderCity(h)" not in html:
    errors.append("City search UI is missing")
if html.count("mdRenderCity(h);") != 2:
    errors.append("Veterinary city search must render in online and offline cases")
if "Buscar veterinaria por nombre o dirección..." not in html:
    errors.append("Vet search placeholder missing")
if errors:
    for e in errors: print("FAIL:", e)
    sys.exit(1)
print("PASS: static checks. Does not prove Android functionality or signature compatibility.")


