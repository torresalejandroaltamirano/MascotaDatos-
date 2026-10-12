(function(){
'use strict';
if(window.__mdNavigationInstalled)return;
if(typeof window.show!=='function')return;
window.__mdNavigationInstalled=true;
var originalShow=window.show, history=[];
function active(){return document.querySelector('.view.active');}
function snapshot(){var v=active();if(!v)return null;return {id:v.id,scrollY:window.scrollY,innerY:v.scrollTop,fields:Array.from(v.querySelectorAll('input,select,textarea')).map(function(el){return {el:el,value:el.value,checked:el.checked};})};}
function restore(s){if(!s)return;requestAnimationFrame(function(){requestAnimationFrame(function(){s.fields.forEach(function(f){if(f.el&&f.el.isConnected){f.el.value=f.value;if(f.el.type==='checkbox'||f.el.type==='radio')f.el.checked=f.checked;}});var v=document.getElementById(s.id);if(v)v.scrollTop=s.innerY;window.scrollTo(0,s.scrollY);});});}
window.show=function(id){if(id==='veterinarias')id='ciudades';var current=active();if(!document.getElementById(id))return;if(current&&current.id===id)return;if(current&&id!=='inicio')history.push(snapshot());if(id==='inicio')history.length=0;originalShow(id);};
function goBack(){var prev=history.pop();if(prev){originalShow(prev.id);restore(prev);return true;}var v=active();if(v&&v.id!=='inicio'&&document.getElementById('inicio')){originalShow('inicio');return true;}return false;}
// Unificar la portada: el directorio por ciudad es el único acceso a Veterinarias.
var oldCard=document.querySelector('#inicio .card.action[onclick="show(\'veterinarias\')"]');
if(oldCard)oldCard.remove();
var cityCard=document.querySelector('#inicio .card.action[onclick="show(\'ciudades\')"]');
if(cityCard){var title=cityCard.querySelector('b');if(title)title.textContent='Veterinarias';}
document.addEventListener('mascotadatos:back',function(e){if(goBack())e.preventDefault();});
document.addEventListener('click',function(e){var b=e.target.closest('button.back');if(!b)return;if(goBack()){e.preventDefault();e.stopImmediatePropagation();}},true);
})();
