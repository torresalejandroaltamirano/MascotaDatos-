/* MascotaDatos 1.3.11 - puente de navegacion experimental */
(function(){
"use strict";
if(window.__mdStableNavigationInstalled||typeof window.show!=="function")return;
window.__mdStableNavigationInstalled=true;
const original=window.show,history=[],positions=new Map();
let navigating=false;
const current=()=>document.querySelector(".view.active");
function snapshot(){
 const v=current();if(!v||!v.id)return null;
 return {id:v.id,y:window.scrollY||0,inside:v.scrollTop||0,
 fields:[...v.querySelectorAll("input,select,textarea")].map((e,i)=>({i,id:e.id,value:e.value,checked:e.checked}))};
}
function restore(s){
 if(!s)return;
 const v=document.getElementById(s.id);if(!v)return;
 (s.fields||[]).forEach(f=>{
 const e=f.id?document.getElementById(f.id):v.querySelectorAll("input,select,textarea")[f.i];
 if(e&&v.contains(e)){e.value=f.value;if(e.type==="checkbox"||e.type==="radio")e.checked=f.checked;}
 });
 requestAnimationFrame(()=>requestAnimationFrame(()=>{
 v.scrollTop=s.inside||0;
 window.scrollTo({left:0,top:s.y||0,behavior:"instant"});
 }));
}
window.show=function(id){
 const next=document.getElementById(id),prev=current();
 if(!next||!next.classList.contains("view"))return original.apply(this,arguments);
 if(prev&&prev.id===id)return;
 const old=snapshot();
 if(old){positions.set(old.id,old);if(!navigating)history.push(old);}
 navigating=true;
 try{original.apply(this,arguments);}finally{navigating=false;}
 const saved=positions.get(id);
 if(saved)restore(saved);
 else if(old)requestAnimationFrame(()=>window.scrollTo({left:0,top:old.y,behavior:"instant"}));
};
window.mascotaDatosBack=function(){
 const last=history.pop();
 if(last&&document.getElementById(last.id)){
 navigating=true;try{window.show(last.id);}finally{navigating=false;}
 restore(last);return true;
 }
 const v=current();
 if(v&&v.id!=="inicio"&&document.getElementById("inicio")){
 navigating=true;try{window.show("inicio");}finally{navigating=false;}
 return true;
 }
 return false;
};
})();