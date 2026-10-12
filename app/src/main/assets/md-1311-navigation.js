(function(){
'use strict';
if(window.__mdNavigationInstalled)return;
if(typeof window.show!=='function')return;
window.__mdNavigationInstalled=true;
var originalShow=window.show, history=[];
var screenPositions=Object.create(null);
function active(){return document.querySelector('.view.active');}
function snapshot(){var v=active();if(!v)return null;return {id:v.id,scrollY:window.scrollY,innerY:v.scrollTop,fields:Array.from(v.querySelectorAll('input,select,textarea')).map(function(el){return {el:el,value:el.value,checked:el.checked};})};}
function restore(s){if(!s)return;requestAnimationFrame(function(){requestAnimationFrame(function(){s.fields.forEach(function(f){if(f.el&&f.el.isConnected){f.el.value=f.value;if(f.el.type==='checkbox'||f.el.type==='radio')f.el.checked=f.checked;}});var v=document.getElementById(s.id);if(v)v.scrollTop=s.innerY;window.scrollTo(0,s.scrollY);});});}
window.show=function(id){
  if(id==='veterinarias')id='ciudades';
  var current=active();if(!document.getElementById(id))return;
  if(current&&current.id===id)return;
  if(current){var previous=snapshot();screenPositions[current.id]=previous;if(id!=='inicio')history.push(previous);}
  if(id==='inicio')history.length=0;
  originalShow(id);
  // La pantalla solicitada debe conservar su posición anterior cuando se reabre.
  if(screenPositions[id])restore(screenPositions[id]);
};
function goBack(){
  var current=snapshot();if(current)screenPositions[current.id]=current;
  var prev=history.pop();
  if(prev){originalShow(prev.id);restore(prev);return true;}
  var v=active();if(v&&v.id!=='inicio'&&document.getElementById('inicio')){originalShow('inicio');if(screenPositions.inicio)restore(screenPositions.inicio);return true;}
  return false;
}
// Unificar la portada: el directorio por ciudad es el único acceso a Veterinarias.
var oldCard=document.querySelector('#inicio .card.action[onclick="show(\'veterinarias\')"]');
if(oldCard)oldCard.remove();
var cityCard=document.querySelector('#inicio .card.action[onclick="show(\'ciudades\')"]');
if(cityCard){var title=cityCard.querySelector('b');if(title)title.textContent='Veterinarias';}
// Buscador local sobre las fichas de la ciudad seleccionada, sin cambiar de pantalla.
var cityBox=document.getElementById('cityContent');
if(cityBox){
  var observer=new MutationObserver(function(){
    if(cityBox.querySelector('#mdCitySearch'))return;
    var items=cityBox.querySelectorAll('.item');
    if(!items.length)return;
    var field=document.createElement('input');
    field.id='mdCitySearch';field.type='search';field.placeholder='Buscar veterinaria, dirección o teléfono';
    field.setAttribute('aria-label','Buscar veterinarias en esta ciudad');
    field.style.cssText='width:100%;box-sizing:border-box;padding:12px;margin:10px 0;border-radius:10px;border:1px solid #9ca3af;font-size:16px';
    var heading=cityBox.querySelector('h2');
    if(heading)heading.insertAdjacentElement('afterend',field);else cityBox.prepend(field);
    field.addEventListener('input',function(){
      var q=field.value.trim().toLocaleLowerCase('es');
      cityBox.querySelectorAll('.item').forEach(function(item){item.style.display=item.textContent.toLocaleLowerCase('es').includes(q)?'':'none';});
    });
  });
  observer.observe(cityBox,{childList:true});
}
document.addEventListener('mascotadatos:back',function(e){if(goBack())e.preventDefault();});
document.addEventListener('click',function(e){var b=e.target.closest('button.back');if(!b)return;if(goBack()){e.preventDefault();e.stopImmediatePropagation();}},true);
})();
