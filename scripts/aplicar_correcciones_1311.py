from pathlib import Path
import hashlib
p=Path("app/src/main/assets/index.html")
b=p.read_bytes()
def sha(x): return hashlib.sha1(b"blob "+str(len(x)).encode()+b"\0"+x).hexdigest()
src="a29a8b10889b2b4f986cb3252638ea7f588c759f"
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
out=s.encode()
if sha(out)!=dst: raise SystemExit("Result SHA mismatch: "+sha(out))
p.write_bytes(out)
print("PASS: exact corrected source")
