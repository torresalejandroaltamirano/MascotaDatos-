from pathlib import Path
import hashlib
p=Path("app/src/main/assets/index.html")
b=p.read_bytes()
def sha(x): return hashlib.sha1(b"blob "+str(len(x)).encode()+b"\0"+x).hexdigest()
src="95dce6d4534c52ef5b48b07ef86b2dd2b9605cc2"
dst="b30af9f795e59866d257adae419a00e185cb9546"
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

# Restaurar Favoritos: conservar la sección, la tarjeta de inicio y el botón
# Guardar de la versión 1.3.10 al actualizar a 1.3.11.
historical_html = historical.decode("utf-8")
home_card = """<div class="card action" onclick="show('favoritos');loadFavorites()"><div class="icon">⭐</div><b>Favoritos</b><div class="small">Avisos guardados</div></div>"""
notifications_card = """<div class="card action" onclick="show('notificaciones');loadNotifications()"><div class="icon">🔔</div><b>Notificaciones</b><div class="small">Avisos de MascotaDatos</div></div>"""
if historical_html.count(home_card) != 1:
    raise SystemExit("Tarjeta Favoritos histórica no encontrada")
change(notifications_card, notifications_card + home_card)

favorites_section = historical_html[historical_html.index('<section id="favoritos"'):historical_html.index('<section id="reportar"')]
if favorites_section.count('<section') != 1 or favorites_section.count('</section>') != 1:
    raise SystemExit("Sección Favoritos inválida")
change('<section id="reportar"', favorites_section + '<section id="reportar"')

favorites_functions = historical_html[historical_html.index('function getFavoriteIds()'):historical_html.index('function showReportFor(id)')]
if favorites_functions.count('function loadFavorites()') != 1:
    raise SystemExit("Funciones históricas de Favoritos inválidas")
change('function showReportFor(id)', favorites_functions + 'function showReportFor(id)')

like_needle = """</span></button> <button class="btn" style="margin-top:8px" onclick="showReportFor('+id+')">🚩 Reportar</button>"""
like_patch = """</span></button> <button class="btn" style="margin-top:8px" onclick="toggleFavorite('+id+',this)">⭐ Guardar</button> <button class="btn" style="margin-top:8px" onclick="showReportFor('+id+')">🚩 Reportar</button>"""
change(like_needle, like_patch)

# Una sola entrada visual de Veterinarias en inicio; el acceso abre ciudades.
duplicate_city_card = """<div class="card action" style="margin-bottom:10px" onclick="show('ciudades')"><div class="icon">🗺️</div><b>Veterinarias por ciudad</b><div class="small">Copiapó, Caldera, Vallenar y otras comunas de Atacama</div></div>"""
change(duplicate_city_card, "")
change("""<div class="card action" onclick="show('ciudades')"><div class="icon">🏥</div><b>Veterinarias</b><div class="small">Dirección, horarios y contacto</div></div>""",
       """<div class="card action" onclick="show('ciudades')"><div class="icon">🏥</div><b>Veterinarias</b><div class="small">Seleccionar ciudad · buscar fichas</div></div>""")
city_search_js = r"""function mdRenderCity(h){
 const box=document.getElementById('cityContent');
 if(!box)return;
 box.innerHTML=h;
 const wrapper=document.createElement('div');
 wrapper.className='search';
 const input=document.createElement('input');
 input.type='search';
 input.placeholder='Buscar veterinaria por nombre o dirección...';
 input.setAttribute('aria-label','Buscar veterinarias en la ciudad');
 input.addEventListener('input',()=>{
   const q=input.value.toLocaleLowerCase('es').normalize('NFD').replace(/[\u0300-\u036f]/g,'').trim();
   box.querySelectorAll('.item').forEach(el=>{
     const txt=(el.textContent||'').toLocaleLowerCase('es').normalize('NFD').replace(/[\u0300-\u036f]/g,'');
     el.style.display=!q||txt.includes(q)?'':'none';
   });
 });
 wrapper.appendChild(input);
 const title=box.querySelector('h2');
 if(title)title.after(wrapper);else box.prepend(wrapper);
}
"""
change("async function city(id){", city_search_js + "async function city(id){")
needle_city = "   box.innerHTML=h;\n   mdCityLegacyCopiapo(id);"
if s.count(needle_city) != 2:
    raise SystemExit("Directorio ciudades cambió; no se modifica a ciegas")
s = s.replace(needle_city, "   mdRenderCity(h);\n   mdCityLegacyCopiapo(id);")
# Evitar insertar datos personales de otra cuenta en el inicio de sesión.
import re
prefill_pattern = r"function prefillKnownAccount\(\)\{[\s\S]*?\}\n\nfunction showRecovery\(\)\{"
replacement = """function prefillKnownAccount(){
  // Nunca completar un correo privado o ajeno por defecto.
  // Cada persona debe ingresar explícitamente su propia dirección.
}

function showRecovery(){"""
if len(re.findall(prefill_pattern,s))!=1: raise SystemExit("Prefill source no coincide")
s=re.sub(prefill_pattern,lambda _:replacement,s,count=1)
change("function showRecovery(){prefillKnownAccount();", "function showRecovery(){")
# Verificar todas las correcciones históricas ANTES de añadir privacidad.
if sha(s.encode())!=dst: raise SystemExit("Base 1.3.11 incorrecta: "+sha(s.encode()))

# Solicitudes de eliminación: solo contacto, nunca borrado automático ficticio.
privacy_card = """<div class="card" id="md-privacy-contact" style="margin-top:12px"><h3>Privacidad y eliminación de cuenta</h3><p class="small">Puedes solicitar la eliminación de tu cuenta y datos asociados. El envío abre tu aplicación de correo; no borra automáticamente los datos.</p><a class="btn" href="mailto:alttorres@hotmail.com?subject=Solicitud%20de%20eliminacion%20de%20cuenta%20MascotaDatos&body=Solicito%20la%20eliminacion%20de%20mi%20cuenta%20MascotaDatos%20y%20sus%20datos%20asociados.">Solicitar eliminación por correo</a><p class="small">No envíes contraseñas ni códigos de verificación. Soporte: alttorres@hotmail.com</p></div>"""
change('<div id="sessionBox" class="notice">Comprobando sesión…</div>', '<div id="sessionBox" class="notice">Comprobando sesión…</div>'+privacy_card)
# La sección de Seguridad también ofrece contacto sin duplicar la cuenta.
change('<h2>🛡️ Seguridad y privacidad</h2>', '<h2>🛡️ Seguridad y privacidad</h2><p class="small">Consultas de privacidad: <a href="mailto:alttorres@hotmail.com?subject=Consulta%20privacidad%20MascotaDatos">alttorres@hotmail.com</a></p>')
# Solicitud autenticada, se almacena con RLS. No borra datos por sí sola.
change('<div id="sessionBox" class="notice">Comprobando sesión…</div>', '<div id="sessionBox" class="notice">Comprobando sesión…</div>') if False else None
change('Solicitar eliminación por correo</a>', 'Solicitar eliminación por correo</a><button class="btn" type="button" style="margin-top:10px" onclick="mdRequestDeletion()">Registrar solicitud desde mi sesión</button><div class="small" id="md-deletion-status" role="status" aria-live="polite"></div>')
deletion_js = """async function mdRequestDeletion(){
 const box=document.getElementById('md-deletion-status');
 if(!box)return;
 const session=window.mascotaSession;
 if(!session?.user?.id){box.textContent='Inicia sesión en Mi cuenta para registrar una solicitud. También puedes usar el correo de soporte.';return;}
 if(!confirm('¿Quieres registrar una solicitud de eliminación de tu cuenta y datos asociados? No se eliminará nada automáticamente.'))return;
 box.textContent='Registrando solicitud...';
 try{
  const {error}=await sb.from('account_deletion_requests').insert({user_id:session.user.id});
  if(error){
    if(error.code==='23505'){box.textContent='Ya tienes una solicitud pendiente.';return;}
    throw error;
  }
  box.textContent='Solicitud registrada. El equipo revisará tu petición y verificará tu identidad antes de eliminar información.';
 }catch(err){
  box.textContent='No se pudo registrar la solicitud. Intenta nuevamente o escribe a alttorres@hotmail.com.';
 }
}
"""
change('async function logoutAccount(){', deletion_js+'async function logoutAccount(){')
out=s.encode()
if out.count(b'id="md-privacy-contact"')!=1: raise SystemExit("Privacy contact missing or duplicated")
p.write_bytes(out)
print("PASS: exact corrected source")
