from pathlib import Path
import hashlib
p=Path("app/src/main/assets/index.html")
b=p.read_bytes()
def sha(x): return hashlib.sha1(b"blob "+str(len(x)).encode()+b"\0"+x).hexdigest()
src="95dce6d4534c52ef5b48b07ef86b2dd2b9605cc2"
dst="5c834b7903bfdfaa2664f574141a941bdf0763b0"
if sha(b)==dst: print("already corrected"); quit()
if sha(b)!=src: raise SystemExit("Source mismatch, do not overwrite")
s=b.decode()
def change(a,b):
 global s
 if s.count(a)!=1: raise SystemExit("Missing/duplicate source: "+a[:70])
 s=s.replace(a,b,1)
change("<title>MascotaDatos — v32</title>","<title>MascotaDatos</title>")
change("</style>\n</head>", """</style>

<style id="md-1311-final-polish">
/* Correcciones de diseño: no afecta el icono Android del telefono */
.md-logo{width:min(55vw,225px);max-width:225px;height:auto;object-fit:contain;margin:8px auto 12px}
#ciudades .grid>.card.action{display:flex;flex-direction:column;align-items:center;justify-content:center;min-height:132px;text-align:center;gap:4px}
#ciudades .grid>.card.action .icon{height:47px;min-height:47px;font-size:34px;display:flex;align-items:center;justify-content:center}
#ciudades .grid>.card.action img{display:block;max-width:100%;width:98px;height:68px;object-fit:contain;object-position:center;margin:0 auto;border-radius:8px}
</style>
</head>""")
change("  <footer>MascotaDatos v32 · Aplicación en etapa de pruebas.</footer>","  <footer>MascotaDatos · Información y servicios para mascotas.</footer>")
change("function show(id){","function show(id){\n // Entrada única: conserva el directorio histórico sin duplicar pantallas.\n if (id === 'veterinarias') id = 'ciudades';")
change("function city(id){","async function city(id){")
change('<section id="publicidadSolicitud" class="screen">','<section id="publicidadSolicitud" class="view">')

# Recuperar la fotografía oficial ya publicada en la APK 1.3.10.
# La versión de origen se fija por commit y se comprueba con su SHA Git.
# Esto sucede al compilar: NO usa red al abrir MascotaDatos en Android.
import re
from urllib.request import urlopen
historical_url = "https://raw.githubusercontent.com/torresalejandroaltamirano/MascotaDatos-/d1e7049d1c36db9c795deef2b1a5d15969893549/app/src/main/assets/index.html"
with urlopen(historical_url, timeout=30) as response:
    historical = response.read()
if sha(historical) != "2b94273e3e98d7ba246b099b0c60516375d76b13":
    raise SystemExit("La fuente histórica de la Catedral no coincide; detener compilación")
match = re.search(r'<img src="(data:image/jpeg;base64,[^"]+)" alt="Catedral de Copiapó"[^>]*>', historical.decode("utf-8"))
if not match or len(match.group(1)) < 50000:
    raise SystemExit("No fue posible recuperar la imagen original de la Catedral")
old_card = """<div class="card action" onclick="city('copiapo')"><div class="icon">🏙️</div><b>Copiapó</b><div class="small">Directorio principal</div></div>"""
new_card = """<div class="card action" onclick="city('copiapo')"><img src="%s" alt="Catedral de Copiapó" style="width:98px;height:68px;object-fit:contain;object-position:center;display:block;margin:0 auto;border-radius:8px"><b>Copiapó</b><div class="small">Catedral de Copiapó · Directorio principal</div></div>""" % match.group(1)
change(old_card, new_card)
out=s.encode()
if sha(out)!=dst: raise SystemExit("Result SHA mismatch: "+sha(out))
p.write_bytes(out)
print("PASS: exact corrected source")
