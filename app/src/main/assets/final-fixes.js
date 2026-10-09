(function(){
'use strict';
function q(s,r){return (r||document).querySelector(s)}
function qa(s,r){return Array.from((r||document).querySelectorAll(s))}
var mdScroll={}, mdBackStack=[], mdInternalNav=false;
function visibleScreen(){
  return qa('.screen').find(function(s){return s.classList.contains('active') || getComputedStyle(s).display!=='none';});
}
function screenState(){
  var s=visibleScreen(); if(!s||!s.id)return null;
  return {id:s.id,page:window.scrollY||document.documentElement.scrollTop||0,inside:s.scrollTop||0};
}
function restoreState(st){
  if(!st)return;
  requestAnimationFrame(function(){
    var s=q('#'+st.id); if(s)s.scrollTop=st.inside||0;
    window.scrollTo(0,st.page||0);
  });
}
function topNow(){requestAnimationFrame(function(){var s=visibleScreen();if(s)s.scrollTop=0;window.scrollTo(0,0);});}
document.addEventListener('click',function(e){
  var a=e.target.closest('a,button,.btn,.card.action,[role="button"]'); if(!a)return;
  var back=/volver|atrás|atras/i.test((a.textContent||'')+' '+(a.getAttribute('aria-label')||''));
  if(back){
    var prev=mdBackStack.pop(); setTimeout(function(){restoreState(prev);},160); return;
  }
  var before=screenState(); if(!before)return;
  setTimeout(function(){
    var after=screenState();
    if(after && after.id!==before.id){mdBackStack.push(before);topNow();}
  },160);
},true);
window.addEventListener('popstate',function(){var prev=mdBackStack.pop();setTimeout(function(){restoreState(prev);},160);});
// Permite al botón Atrás nativo regresar dentro de la interfaz de una sola página.
window.mascotaDatosGoBack = function(){
  if (!mdBackStack.length) return false;
  var prev=mdBackStack.pop();
  var target=q('#'+prev.id);
  if(!target || typeof window.show!=='function') {
    mdBackStack.push(prev);
    return false;
  }
  window.show(prev.id);
  setTimeout(function(){restoreState(prev);},160);
  return true;
};
// Los cambios de hash no deben desplazar la pantalla sin una navegación explícita.
function patch(){
  // Quitar texto de prueba sin alterar la región.
  qa('header *, .header *, body *').forEach(function(el){
    if(el.children.length===0 && /Región de Atacama\s*·\s*versión de prueba/i.test(el.textContent||'')){
      el.textContent=(el.textContent||'').replace(/\s*·\s*versión de prueba/ig,'');
    }
  });
  // Eliminar el bloque publicitario duplicado agregado al final.
  qa('#publicidad').forEach(function(el,i,arr){
    if(i>0 || el.classList.contains('screen')) el.remove();
  });
  // Servicios: convertir todas las tarjetas en acciones y llevarlas a búsqueda.
  var sec=q('#servicios2');
  if(sec){
    var terms=['peluquería','paseador','cuidador','transporte','hotel','domicilio'];
    qa('.card',sec).slice(0,6).forEach(function(card,i){
      card.classList.add('action'); card.setAttribute('role','button'); card.setAttribute('tabindex','0');
      var go=function(){
        if(typeof window.show==='function') window.show('busquedaGeneral');
        var input=q('#globalSearch'); if(input){input.value=terms[i]; if(typeof window.doGlobalSearch==='function') window.doGlobalSearch();}
      };
      card.onclick=go; card.onkeydown=function(e){if(e.key==='Enter'||e.key===' '){e.preventDefault();go();}};
      if(!q('.small',card)){var d=document.createElement('div');d.className='small';d.textContent='Ver resultados';card.appendChild(d);}
    });
  }
  // Modelo de negocio: hacer accionables Ficha básica y Destacada.
  qa('.card').forEach(function(card){
    var txt=(card.textContent||'').trim();
    if(/Ficha básica/i.test(txt) || /Destacada/i.test(txt)){
      card.classList.add('action','business-option'); card.setAttribute('role','button'); card.setAttribute('tabindex','0');
      var tipo=/Destacada/i.test(txt)?'Publicidad destacada':'Ficha básica gratuita';
      var goBiz=function(){
        var target=q('#publicidadNegocio')||q('#publicidad')||q('#negocio')||q('#registroNegocio');
        if(target && target.id && typeof window.show==='function') window.show(target.id);
        var selects=qa('select');
        var sel=selects.find(function(s){return /publicidad|ficha|destac/i.test((s.innerText||'')+' '+(s.name||'')+' '+(s.id||''));});
        if(sel){
          var opt=Array.from(sel.options).find(function(o){return (o.text||'').toLowerCase().includes(tipo.toLowerCase().split(' ')[0]);});
          if(opt){sel.value=opt.value;sel.dispatchEvent(new Event('change',{bubbles:true}));}
        }
        var form=qa('form').find(function(f){return /Nombre del negocio|Nombre de contacto|promocionar/i.test(f.textContent||'');});
        if(form){form.scrollIntoView({behavior:'smooth',block:'start'});}
      };
      card.onclick=goBiz; card.onkeydown=function(e){if(e.key==='Enter'||e.key===' '){e.preventDefault();goBiz();}};
      if(!q('.action-hint',card)){var d=document.createElement('div');d.className='small action-hint';d.textContent='Toca para solicitar';card.appendChild(d);}
    }
  });
  // Hacer visible Me gusta y Favoritos desde Inicio.
  var favCard=qa('.card.action').find(function(c){return /Favoritos/.test(c.textContent||'')});
  if(favCard){
    var b=q('b',favCard),ic=q('.icon',favCard),sm=q('.small',favCard);
    if(b)b.textContent='Me gusta y Favoritos'; if(ic)ic.textContent='❤️'; if(sm)sm.textContent='Avisos marcados y guardados';
  }
  var fav=q('#favoritos');
  if(fav){
    var h=q('h2',fav); if(h)h.textContent='❤️ Me gusta y ⭐ Favoritos';
    var notice=q('.notice',fav); if(notice)notice.textContent='Los botones ❤️ Me gusta y ⭐ Guardar aparecen en los avisos publicados de Adopción, Perdidos y Encontrados.';
  }

  // Notificaciones sin sesión: ofrecer una salida real y evitar un botón Actualizar muerto.
  var notif=qa('.screen,section,div').find(function(x){
    return /Notificaciones/i.test((q('h1,h2',x)||{}).textContent||'') && /Inicia sesi[oó]n para ver tus notificaciones/i.test(x.textContent||'');
  });
  if(notif){
    var notice=qa('.notice,.card,p,div',notif).find(function(x){return /Inicia sesi[oó]n para ver tus notificaciones/i.test(x.textContent||'');});
    if(notice && !q('.md-login-action',notif)){
      var login=document.createElement('button'); login.type='button'; login.className='btn md-login-action'; login.textContent='🔐 Iniciar sesión';
      login.onclick=function(){
        var ids=['miCuenta','cuenta','login','auth','perfil'];
        var target=ids.find(function(id){return q('#'+id);});
        if(target && typeof window.show==='function') window.show(target);
        else { var cuenta=qa('.card').find(function(x){return /Mi cuenta/i.test(x.textContent||'');}); if(cuenta) cuenta.click(); }
      };
      notice.insertAdjacentElement('afterend',login);
    }
    var refresh=qa('button,.btn',notif).find(function(x){return /Actualizar/i.test(x.textContent||'');});
    if(refresh){
      refresh.onclick=function(){
        var msg=qa('.notice,.card,p,div',notif).find(function(x){return /Inicia sesi[oó]n para ver tus notificaciones/i.test(x.textContent||'');});
        if(msg){msg.textContent='🔐 Primero inicia sesión para actualizar tus notificaciones.';var b=q('.md-login-action',notif);if(b)b.focus();}
      };
    }
  }
  // Acciones de cierre: dar respuesta visible a controles que antes parecían inactivos.
  var fav=q('#favoritos');
  if(fav){
    var upd=qa('button,.btn',fav).find(function(x){return /Actualizar/i.test(x.textContent||'');});
    if(upd){
      upd.onclick=function(){
        // Re-renderizar la vista y dar confirmación aun cuando no existan favoritos.
        if(typeof window.renderFavoritos==='function') window.renderFavoritos();
        else if(typeof window.loadFavorites==='function') window.loadFavorites();
        var n=qa('.notice',fav).find(function(x){return /Todavía no|guardad|favorit/i.test(x.textContent||'');});
        if(n && !/Actualizado/i.test(n.textContent||'')) n.textContent=(n.textContent||'')+' · Actualizado';
        upd.classList.add('tap-ok'); setTimeout(function(){upd.classList.remove('tap-ok');},350);
      };
    }
  }
  // Perdidos/Encontrados: asegurar que las tarjetas sean controles de navegación.
  var pe=qa('.screen,section,div').find(function(x){return /Perdidos y encontrados/i.test((q('h1,h2',x)||{}).textContent||'');});
  if(pe){
    qa('.card',pe).forEach(function(card){
      var t=(card.textContent||'').trim(), target=null;
      if(/Ver mascotas perdidas/i.test(t)) target='perdidas';
      if(/Ver encontradas/i.test(t)) target='encontradas';
      if(target){
        card.classList.add('action'); card.setAttribute('role','button'); card.setAttribute('tabindex','0');
        var go=function(){
          var candidates=qa('[id]').filter(function(el){return new RegExp(target,'i').test(el.id);});
          if(candidates[0] && typeof window.show==='function') window.show(candidates[0].id);
          else {
            var notice=qa('.notice',pe).find(function(x){return /avisos|cargar/i.test(x.textContent||'');});
            if(notice) notice.textContent='No hay avisos disponibles en esta categoría por el momento.';
          }
        };
        card.onclick=go; card.onkeydown=function(e){if(e.key==='Enter'||e.key===' '){e.preventDefault();go();}};
      }
    });
  }
  // Formularios de negocio: nunca dejar Ciudad bloqueada en "Cargando ciudades...".
  qa('select').forEach(function(sel){
    var txt=(sel.textContent||'')+' '+(sel.name||'')+' '+(sel.id||'');
    if(/Cargando ciudades|ciudad/i.test(txt)){
      var loading=Array.from(sel.options||[]).some(function(o){return /Cargando ciudades/i.test(o.text||'');});
      if(loading && (sel.options.length<=2 || !Array.from(sel.options).some(function(o){return /Copiapó|Caldera|Vallenar/i.test(o.text||'');}))){
        var actual=sel.value; sel.innerHTML='';
        [['','Selecciona una comuna'],['Copiapó','Copiapó'],['Caldera','Caldera'],['Tierra Amarilla','Tierra Amarilla'],['Chañaral','Chañaral'],['Diego de Almagro','Diego de Almagro'],['Vallenar','Vallenar'],['Freirina','Freirina'],['Huasco','Huasco'],['Alto del Carmen','Alto del Carmen']].forEach(function(p){
          var o=document.createElement('option');o.value=p[0];o.textContent=p[1];sel.appendChild(o);
        });
        if(actual && Array.from(sel.options).some(function(o){return o.value===actual;})) sel.value=actual;
        sel.disabled=false;
      }
    }
  });
  // Nombre definitivo para las opciones comerciales.
  qa('h1,h2,h3').forEach(function(h){if(/^Modelo futuro$/i.test((h.textContent||'').trim())) h.textContent='Opciones para negocios';});
}
if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',patch); else patch();
// Reaplicar únicamente cuando la interfaz cambie, para cubrir pantallas renderizadas dinámicamente.
var patchTimer=null;
var observer=new MutationObserver(function(){
  clearTimeout(patchTimer);
  patchTimer=setTimeout(patch,80);
});
if(document.documentElement) observer.observe(document.documentElement,{childList:true,subtree:true});
setTimeout(patch,500);

// Directorio único por ciudad, basado en datos locales y sin servicios externos obligatorios.
(function(){
  var records=null, currentCity='Copiapó', installed=false;
  function clean(t){return (t||'').toString().normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase();}
  function el(tag,cls,txt){var n=document.createElement(tag);if(cls)n.className=cls;if(txt!==undefined)n.textContent=txt;return n;}
  function draw(){
    var target=document.getElementById('md-vet-results');if(!target)return;
    target.replaceChildren();
    if(!records){target.appendChild(el('div','notice','Cargando directorio local…'));return;}
    var search=document.getElementById('md-vet-search'),term=clean(search&&search.value);
    var matches=records.filter(function(x){return x.ciudad===currentCity && clean([x.nombre,x.direccion,x.telefono].join(' ')).includes(term);});
    target.appendChild(el('div','small',matches.length+' resultados en '+currentCity));
    if(!matches.length)target.appendChild(el('div','notice','No hay fichas disponibles con esta búsqueda.'));
    matches.forEach(function(x){
      var card=el('div','item');
      card.appendChild(el('h3','',x.nombre||'Veterinaria'));
      card.appendChild(el('div','meta','📍 '+(x.direccion||'Dirección por verificar')));
      if(x.telefono)card.appendChild(el('div','meta','📞 '+x.telefono));
      if(x.horario)card.appendChild(el('div','meta','🕐 '+x.horario));
      if(x.estado!=='verificado_fuente_oficial')card.appendChild(el('div','small','Datos por confirmar antes de visitar'));
      target.appendChild(card);
    });
  }
  function init(){
    if(installed)return;
    var section=document.getElementById('veterinarias'),old=document.getElementById('vet');
    if(!section||!old)return;
    installed=true;
    var oldSearch=section.querySelector('.search');if(oldSearch)oldSearch.style.display='none';
    old.style.display='none';
    var controls=el('div','form');
    var label=el('label','','Ciudad');
    var select=el('select');select.id='md-vet-city';
    ['Copiapó','Caldera','Vallenar','Tierra Amarilla','Chañaral','Diego de Almagro','Huasco','Freirina','Alto del Carmen'].forEach(function(c){var o=el('option','',c);o.value=c;select.appendChild(o);});
    select.value=currentCity;
    select.addEventListener('change',function(){currentCity=select.value;draw();});
    label.appendChild(select);controls.appendChild(label);
    var input=el('input');input.id='md-vet-search';input.type='search';input.placeholder='Buscar veterinaria o sector';input.addEventListener('input',draw);
    controls.appendChild(input);
    var results=el('div','list');results.id='md-vet-results';
    old.before(controls,results);
    fetch('/assets/data/mascotadatos.json').then(function(r){if(!r.ok)throw Error('No disponible');return r.json();}).then(function(data){
      records=Array.isArray(data.veterinarias_unificadas)?data.veterinarias_unificadas:[];draw();
    }).catch(function(){results.replaceChildren(el('div','notice','No fue posible cargar el directorio. Intenta abrir la sección nuevamente.'));});
    var back=section.querySelector('button.back');if(back)back.setAttribute('onclick',"show('inicio')");
    // La tarjeta inicial de ciudades dirige al mismo directorio, sin duplicar categorías.
    document.querySelectorAll('[onclick="show(\'ciudades\')"]').forEach(function(n){n.setAttribute('onclick',"show('veterinarias')");});
    draw();
  }
  if(document.readyState==='loading')document.addEventListener('DOMContentLoaded',init);else init();
})();

// Reanudar la app no modifica el desplazamiento actual.
})();