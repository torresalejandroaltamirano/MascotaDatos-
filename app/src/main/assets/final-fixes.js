(function(){
'use strict';
function q(s,r){return (r||document).querySelector(s)}
function qa(s,r){return Array.from((r||document).querySelectorAll(s))}
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
}
if(document.readyState==='loading') document.addEventListener('DOMContentLoaded',patch); else patch();
setTimeout(patch,500);
})();