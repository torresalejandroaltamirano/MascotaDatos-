const {JSDOM}=require('jsdom'),fs=require('fs'),assert=require('node:assert/strict');
const html=fs.readFileSync('app/src/main/assets/index.html','utf8');
const scripts=[...html.matchAll(/<script(?:\s[^>]*)?>([\s\S]*?)<\/script>/g)].map(x=>x[1]);
const base=scripts.find(x=>x.includes('const mdNavHistory'));
const submit=scripts.find(x=>x.includes('async function submitBusiness(e)'));
const submitFunction=submit.slice(submit.indexOf('async function submitBusiness(e)'),submit.indexOf('async function registerAccount(e)'));
const dom=new JSDOM(html.replace(/<script\b[^>]*>[\s\S]*?<\/script>/gi,''),{runScripts:'dangerously',url:'https://appassets.androidplatform.net/assets/index.html'});
const w=dom.window; w.scrollTo=()=>{};w.HTMLElement.prototype.scrollIntoView=function(){this.scrolled=true;};
let inserted,queries=[];
w.stub={auth:{getUser:async()=>({data:{user:{id:'test-only',email_confirmed_at:'yes'}}})},from:table=>({select(){return this},eq(k,city){this.city=city;queries.push(city);return this},order(){const city=this.city;return new Promise(r=>setTimeout(()=>r({data:[{name:'FUGA COPIAPO',category:'insumos',cities:{name:'Copiapó'}},{name:'Local de '+city,category:'insumos',cities:{name:city}},{name:'Veterinaria de '+city,category:'veterinaria',cities:{name:city}}]}),city==='Copiapó'?15:0))},insert:async x=>{inserted=x;return {error:null}}})};
w.eval(base+'\nconst sb=window.stub;\n'+submitFunction+'\n'+fs.readFileSync('app/src/main/assets/md-release-1312.js','utf8'));
(async()=>{
 const names=['Caldera','Vallenar','Chañaral','Huasco','Freirina','Diego de Almagro','Tierra Amarilla','Alto del Carmen'];
 for(const city of names){await w.loadShops(city);const text=w.document.getElementById('cityContent').textContent;assert(!text.includes('FUGA COPIAPO'),city);assert(!text.includes('FarmVet Los Mancos'),city);assert(text.includes('Local de '+city));}
 w.document.querySelector('[aria-label="Tipo de directorio"]').value='veterinaria';w.document.querySelector('[aria-label="Tipo de directorio"]').onchange();
 for(const id of ['caldera','vallenar','chanaral','huasco','freirina','diego','tierra','alto']){await w.city(id);const text=w.document.getElementById('cityContent').textContent;assert(!text.includes('FUGA COPIAPO'));assert(!text.includes('Huellitas'));}
 await Promise.all([w.loadShops('Copiapó'),w.loadShops('Alto del Carmen')]);assert(w.document.getElementById('cityContent').textContent.includes('Tiendas e insumos en Alto del Carmen'));
 // Regresión del teléfono: entrar a insumos y luego pulsar Veterinarias debe restablecer el filtro.
 await w.loadShops('Copiapó');
 assert.equal(w.document.querySelector('[aria-label="Tipo de directorio"]').value,'insumos');
 w.show('veterinarias');await new Promise(r=>setTimeout(r,30));
 assert.equal(w.document.querySelector('[aria-label="Tipo de directorio"]').value,'veterinaria');
 let clinical=w.document.getElementById('cityContent').textContent;
 assert(clinical.includes('Veterinarias en Copiapó'));assert(clinical.includes('Huellitas'));
 assert(clinical.includes('Munay Vet'));assert(clinical.includes('CEQVet'));
 assert(!clinical.includes('Local de Copiapó'));assert(!clinical.includes('FarmVet Los Mancos'));
 await w.city('vallenar');clinical=w.document.getElementById('cityContent').textContent;
 assert(clinical.includes('Renacer Pets'));assert(clinical.includes('Smartpet'));assert(!clinical.includes('Huellitas'));
 await Promise.all([w.loadShops('Copiapó'),(async()=>{w.show('veterinarias');await w.city('vallenar');})()]);
 clinical=w.document.getElementById('cityContent').textContent;
 assert(clinical.includes('Veterinarias en Vallenar'));assert(!clinical.includes('FUGA COPIAPO'));assert(!clinical.includes('Local de Copiapó'));
 await w.loadShops('Copiapó');assert(w.document.querySelectorAll('#cityContent .item').length>=15);
 for(const b of w.document.querySelectorAll('#servicios2 .grid button')){b.click();assert(w.document.querySelector('#md-service-results h3'));assert(w.document.getElementById('md-service-results').scrolled);}
 const cards=w.document.querySelectorAll('#publicidad .grid button');cards[1].click();assert.equal(w.document.querySelector('[name=listing_plan]').value,'destacada');cards[0].click();assert.equal(w.document.querySelector('[name=listing_plan]').value,'basica');
 const input=w.document.getElementById('globalSearch');assert.equal(w.getComputedStyle(input).minHeight,'54px');assert.equal(w.getComputedStyle(input).width,'100%');
 const form=w.document.querySelector('#publicidad form');form.elements.city_id.innerHTML='<option value="1">Copiapó</option>';form.elements.name.value='Negocio ficticio';form.elements.category.value='servicio';cards[1].click();form.dispatchEvent(new w.Event('submit',{bubbles:true,cancelable:true}));await new Promise(r=>setTimeout(r,10));assert(inserted);assert(inserted.notes.includes('[Ficha solicitada: destacada]'));assert.equal(inserted.city_id,1);
 assert.equal(w.document.querySelectorAll('#insumosCiudad').length,0);assert.equal(w.document.querySelectorAll('[aria-label="Tipo de directorio"]').length,1);
 console.log('PASS: directorio unificado;  aislamiento de 8 comunas en ambos directorios; respuesta tardía; 15+ locales Copiapó; 6 categorías clicables; 2 planes; buscador ancho; envío de plan en formulario (servidor simulado).');
 dom.window.close();
})().catch(e=>{console.error(e);dom.window.close();process.exit(1)});

