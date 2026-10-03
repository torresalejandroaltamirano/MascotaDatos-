(function(){
'use strict';
function q(s,r){return (r||document).querySelector(s)}
function qa(s,r){return Array.from((r||document).querySelectorAll(s))}
var mdScroll={}, mdCurrent=null, mdGoingBack=false;
function visibleScreen(){
  return qa('.screen').find(function(s){return s.classList.contains('active') || getComputedStyle(s).display!=='none';});
}
function rememberScreenScroll(){
  var s=visibleScreen();
  if(s && s.id){mdCurrent=s.id;mdScroll[s.id]={page:window.scrollY||document.documentElement.scrollTop||0,inside:s.scrollTop||0};}
}
function topOfScreen(){
  var s=visibleScreen(); if(!s)return;
  s.scrollTop=0; requestAnimationFrame(function(){window.scrollTo(0,0);});
}
function restoreScreenScroll(){
  var s=visibleScreen(); if(!s)return;
  var p=s.id&&mdScroll[s.id]; if(!p)return topOfScreen();
  requestAnimationFrame(function(){s.scrollTop=p.inside||0;window.scrollTo(0,p.page||0);});
}
document.addEventListener('click',function(e){
  var a=e.target.closest('a,button,.btn,.card.action,[role="button"]'); if(!a)return;
  var isBack=/volver|atrás|atras/i.test((a.textContent||'')+' '+(a.getAttribute('aria-label')||''));
  rememberScreenScroll(); mdGoingBack=isBack;
  setTimeout(function(){ if(mdGoingBack) restoreScreenScroll(); else topOfScreen(); mdGoingBack=false; },100);
},true);
window.addEventListener('popstate',function(){mdGoingBack=true;setTimeout(function(){restoreScreenScroll();mdGoingBack=false;},120);});
window.addEventListener('hashchange',function(){setTimeout(function(){if(mdGoingBack)restoreScreenScroll();else topOfScreen();},120);});
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
// Al volver a una pantalla, conservar el punto de lectura en vez de saltar al final/inicio.
document.addEventListener('visibilitychange',function(){if(!document.hidden)setTimeout(restoreScreenScroll,80);});
})();