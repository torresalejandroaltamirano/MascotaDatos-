/* Correcciones funcionales 1.3.12. No modifica almacenamiento de usuarios. */
(() => {
  const normalize = value => String(value || '').normalize('NFD').replace(/[\u0300-\u036f]/g, '').toLowerCase().trim();
  const names = {copiapo:'Copiapó',caldera:'Caldera',vallenar:'Vallenar',chanaral:'Chañaral',huasco:'Huasco',freirina:'Freirina',diego:'Diego de Almagro',tierra:'Tierra Amarilla',alto:'Alto del Carmen'};
  const style = document.createElement('style');
  style.textContent = `#globalSearch,.search input{display:block!important;width:100%!important;min-width:0!important;min-height:54px!important;padding:15px!important;font-size:17px!important;box-sizing:border-box!important;border:2px solid #507d77!important;border-radius:14px!important}.search{width:100%;box-sizing:border-box}#servicios2 .grid button,#publicidad .grid button{font:inherit;color:inherit;text-align:left;cursor:pointer;min-height:104px}#md-service-results{scroll-margin-top:120px}.item[hidden]{display:none!important}.md-directory-link{display:inline-block;margin:8px 6px 0 0}.md-release-label{font-size:12px;text-align:center;color:#45625e}`;
  document.head.append(style);
  document.querySelectorAll('footer').forEach(el => el.textContent='MascotaDatos 1.3.12 · Información y servicios para mascotas');
  const official = [
    {name:'Nicoovet',city:'Chañaral',category:'veterinaria',address:'Atención veterinaria a domicilio en Chañaral',website:'https://nicoovet.cl/'},
    {name:'Centro Veterinario Renacer Pets',city:'Vallenar',category:'veterinaria',address:'Vallejos 962, Vallenar',website:'https://renacerpets.cl/contacto/'}
  ];
  const local = [];
  Object.entries(names).forEach(([id,city]) => (cityData[id] || []).forEach(x => local.push({name:x[0],address:x[1],phone:x[2],city,category:'veterinaria'})));
  shops.forEach(x => local.push({name:x.nombre,address:x.direccion,phone:x.telefono,city:x.ciudad,category:'insumos'}));
  globalData.forEach(x => {
    const city = Object.values(names).find(n => new RegExp('(?:,|^)\\s*'+n.replace(/[.*+?^${}()|[\]\\]/g,'\\$&')+'\\s*$','i').test(x.direccion || ''));
    if(city) local.push({name:x.nombre,address:x.direccion,phone:x.telefono,city,category:normalize(x.tipo).includes('veterinari')?'veterinaria':'insumos'});
  });
  // La dirección actual publicada por la propia clínica sustituye la ficha histórica.
  official.forEach(x => { for(let i=local.length-1;i>=0;i--) if(local[i].city===x.city && normalize(local[i].name).includes('renacer pets')) local.splice(i,1); local.push(x); });
  function unique(rows) {
    const seen = new Set();
    return rows.filter(x => {const key=normalize(x.name).replace(/[^a-z0-9]/g,'');if(seen.has(key))return false;seen.add(key);return true;});
  }
  function item(row) {
    const el=document.createElement('div');el.className='item';
    const title=document.createElement('h3');title.textContent=row.name;el.append(title);
    const meta=document.createElement('div');meta.className='meta';meta.textContent=[row.city,row.address,row.phone].filter(Boolean).join(' · ');el.append(meta);
    const status=document.createElement('p');status.className='small';status.textContent='Información de referencia. Confirma disponibilidad y horarios con el local.';el.append(status);
    if(row.phone && /\d{7}/.test(row.phone.replace(/\D/g,''))) {const a=document.createElement('a');a.className='btn md-directory-link';a.href='tel:'+row.phone.replace(/[^+\d]/g,'');a.textContent='Llamar';el.append(a);}
    if(row.website){const a=document.createElement('a');a.className='btn md-directory-link';a.href=row.website;a.target='_blank';a.rel='noopener';a.textContent='Consultar servicio';el.append(a);}
    const map=document.createElement('button');map.type='button';map.className='btn md-directory-link';map.textContent='Ver ubicación';map.onclick=()=>openMap((row.address || row.name)+', '+row.city+', Chile');el.append(map);
    return el;
  }
  window.doGlobalSearch=()=>{
    const q=normalize(document.getElementById('globalSearch').value),box=document.getElementById('globalResults');
    box.replaceChildren();
    if(!q){const p=document.createElement('p');p.className='notice';p.textContent='Escribe un nombre, servicio o ciudad para buscar.';box.append(p);return;}
    const rows=unique(local).filter(x=>normalize([x.name,x.address,x.city,x.category].join(' ')).includes(q));
    if(rows.length)rows.slice(0,50).forEach(x=>box.append(item(x)));
    else {const p=document.createElement('p');p.className='notice';p.textContent='No encontramos fichas con esa búsqueda.';box.append(p);}
  };
  const generations = new Map();
  async function directory(city,kind,box) {
    if(!box || !Object.values(names).includes(city))return;
    const generation=(generations.get(box)||0)+1;generations.set(box,generation);
    const render=(remote,loading=false)=>{
      if(generations.get(box)!==generation)return;
      const rows=unique([...remote,...local.filter(x=>x.city===city && (kind==='veterinaria'?x.category==='veterinaria':x.category!=='veterinaria'))]);
      box.replaceChildren();
      const h=document.createElement('h2');h.textContent='📍 '+city;box.append(h);
      const input=document.createElement('input');input.type='search';input.placeholder='Buscar en '+city;input.setAttribute('aria-label','Buscar locales en '+city);const wrapper=document.createElement('div');wrapper.className='search';wrapper.append(input);box.append(wrapper);
      const notice=document.createElement('p');notice.className='notice';notice.textContent=rows.length?rows.length+' fichas de '+city+(loading?' · Actualizando…':''):'Aún no hay fichas locales disponibles para '+city+'. No se muestran locales de otras ciudades.';box.append(notice);
      const list=document.createElement('div');list.className='list';rows.forEach(x=>list.append(item(x)));box.append(list);
      input.oninput=()=>list.querySelectorAll('.item').forEach(el=>el.hidden=!normalize(el.textContent).includes(normalize(input.value)));
    };
    render([],true);
    try {
      const {data,error}=await sb.from('businesses').select('id,name,address,phone,category,verification_status,cities!inner(name)').eq('cities.name',city).order('name');
      if(error)throw error;
      const rows=(data||[]).filter(x=>normalize(x.cities?.name)===normalize(city) && (kind==='veterinaria'?normalize(x.category)==='veterinaria':normalize(x.category)!=='veterinaria')).map(x=>({...x,city}));
      render(rows);
    } catch (_) { render([]); }
  }
  let directoryKind='veterinaria', directoryCity='copiapo';
  const cityScreen=document.getElementById('ciudades');
  const categoryLabel=document.createElement('label');categoryLabel.textContent='Tipo de local';categoryLabel.className='search';
  const category=document.createElement('select');category.setAttribute('aria-label','Tipo de local');category.style.cssText='width:100%;min-height:54px;padding:14px;font:inherit;border-radius:12px';category.innerHTML='<option value="veterinaria">Veterinarias</option><option value="insumos">Tiendas e insumos</option>';categoryLabel.append(category);
  if(cityScreen){const heading=cityScreen.querySelector('h2');if(heading){heading.textContent='Directorio por ciudad';heading.after(categoryLabel);}}
  window.city=id=>{directoryCity=id;return directory(names[id],directoryKind,document.getElementById('cityContent'));};
  category.onchange=()=>{directoryKind=category.value;city(directoryCity);};
  const originalShow=window.show;
  window.show=id=>{if(id==='insumosCiudad'){directoryKind='insumos';category.value=directoryKind;id='ciudades';}const result=originalShow(id);if(id==='ciudades')city(directoryCity);return result;};
  window.loadShops=name=>{directoryKind='insumos';category.value=directoryKind;directoryCity=Object.keys(names).find(id=>names[id]===name)||'copiapo';originalShow('ciudades');return city(directoryCity);};
  const oldShopScreen=document.getElementById('insumosCiudad');if(oldShopScreen)oldShopScreen.remove();
  document.querySelectorAll('#inicio .card.action').forEach(el=>{
    const title=el.querySelector('b');if(!title)return;
    if(title.textContent.trim()==='Veterinarias'){title.textContent='Directorio';const small=el.querySelector('.small');if(small)small.textContent='Ciudad · veterinarias · tiendas e insumos';}
    else if(/^(Insumos|Tiendas e insumos)$/.test(title.textContent.trim()))el.remove();
  });
  const navVet=document.querySelector('nav button[data-view="veterinarias"]');if(navVet)navVet.innerHTML='<span>🏥</span>Directorio';
  const navShop=document.querySelector('nav button[data-view="insumos"]');if(navShop){navShop.innerHTML='<span>✂️</span>Servicios';navShop.setAttribute('onclick',"show('servicios2')");}

  const serviceData={
    'peluquerías':[
      {name:'Estética Canina Copiapó',city:'Copiapó',address:'San Antonio 3167',phone:'+56 9 5428 8889'},
      {name:'Isacura Spa Canino',city:'Copiapó',address:'Pl. Wheelwright 1252',phone:'+56 9 6765 3013'},
      {name:'Sammy Pets',city:'Copiapó',address:'Av. El Palomar 1936',phone:'+56 9 4553 2892'}],
    'hoteles':[{name:'Hotel canino Incavet',city:'Copiapó',address:'El Inca 440',website:'https://incavet.cl/reserva-hotel/'}],
    'atención a domicilio':[
      {name:'Clínica Veterinaria Huellitas',city:'Copiapó',website:'https://veterinariahuellitas.cl/veterinario-a-domicilio/'},
      {name:'Munay Vet',city:'Copiapó',website:'https://munayvet.cl/'},
      official[0]],
    'paseadores':[], 'cuidadores':[], 'transporte':[]
  };
  const serviceBox=document.getElementById('md-service-results');
  window.mdServiceType=tipo=>{
    if(!serviceBox)return;
    serviceBox.replaceChildren();const h=document.createElement('h3');h.textContent=tipo.charAt(0).toUpperCase()+tipo.slice(1);serviceBox.append(h);
    const rows=serviceData[tipo]||[];
    if(rows.length) rows.forEach(x=>serviceBox.append(item(x)));
    else {const p=document.createElement('p');p.className='notice';p.textContent='Todavía no tenemos prestadores publicados en esta categoría. Puedes solicitar la incorporación de tu servicio.';serviceBox.append(p);const b=document.createElement('button');b.className='btn';b.textContent='Registrar mi servicio';b.onclick=()=>{show('publicidad');document.querySelector('#publicidad [name="category"]').value='servicio';};serviceBox.append(b);}
    serviceBox.scrollIntoView({block:'start'});
  };
  document.querySelectorAll('#servicios2 .grid .card').forEach((el,index)=>{
    const types=['peluquerías','paseadores','cuidadores','transporte','hoteles','atención a domicilio'];
    const b=document.createElement('button');b.type='button';b.className='card action';b.innerHTML=el.innerHTML;b.onclick=()=>mdServiceType(types[index]);el.replaceWith(b);
  });
  const form=document.querySelector('#publicidad form');
  if(form){
    form.setAttribute('onsubmit','submitBusiness(event)');
    const label=document.createElement('label');label.textContent='Tipo de ficha solicitada';
    const select=document.createElement('select');select.name='listing_plan';select.innerHTML='<option value="basica">Ficha básica</option><option value="destacada">Ficha destacada — solicitar información</option>';label.append(select);form.prepend(label);
    const planStatus=document.createElement('p');planStatus.className='notice';planStatus.textContent='Ficha básica: solicita la incorporación de tu negocio. Todas las solicitudes se revisan antes de publicarse.';form.prepend(planStatus);
    const choose=plan=>{select.value=plan;planStatus.textContent=plan==='destacada'?'Ficha destacada seleccionada. Solicita información de disponibilidad y condiciones; este formulario no realiza cobros ni activa publicidad automáticamente.':'Ficha básica seleccionada. Envía los datos de tu negocio para revisión.';form.scrollIntoView({block:'start'});};
    select.onchange=()=>choose(select.value);
    document.querySelectorAll('#publicidad .grid .card').forEach((el,i)=>{const b=document.createElement('button');b.type='button';b.className='card action';b.innerHTML=el.innerHTML;b.onclick=()=>choose(i?'destacada':'basica');el.replaceWith(b);});
    const heading=[...document.querySelectorAll('#publicidad h2')].find(x=>x.textContent==='Modelo futuro');if(heading)heading.textContent='Elige tu ficha';
    // Conserva el esquema del servidor: el tipo solicitado se envía en las notas ya admitidas.
    form.addEventListener('submit',()=>{const notes=form.elements.notes;notes.value=notes.value.replace(/^\[Ficha solicitada: [^\]]+\]\s*/,'');notes.value='[Ficha solicitada: '+select.value+'] '+notes.value;},true);
  }
  if(serviceBox){const p=document.createElement('p');p.className='notice';p.textContent='Selecciona una categoría para consultar sus prestadores y datos de contacto.';serviceBox.append(p);}
})();
