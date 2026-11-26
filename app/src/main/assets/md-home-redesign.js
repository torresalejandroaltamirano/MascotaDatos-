/* Portada MascotaDatos: imagen cinematográfica, accesos reparados y directorio local sin conexión. */
(() => {
  if (window.__mdHomeRefreshInstalled) return;
  window.__mdHomeRefreshInstalled = true;
  const home = document.getElementById('inicio');
  if (!home) return;

  const style = document.createElement('style');
  style.textContent = `
    #inicio .hero{background:linear-gradient(135deg,#fff8eb,#f3e4cc);border:1px solid #eadcc8;border-radius:22px;padding:16px;box-shadow:0 8px 22px #4a2d1813}
    #inicio .hero img{display:none}
    #inicio .hero h1{font-size:22px;line-height:1.16;color:#0b4d4a;margin:0 0 5px}
    #inicio .hero p{font-size:13px;line-height:1.45;color:#5e5045}
    .md-home-stage{position:relative;overflow:hidden;margin:12px 0 16px;border-radius:24px;background:#0b4d4a;color:white;box-shadow:0 14px 30px #0b4d4a30}
    .md-home-photo{height:clamp(310px,92vw,390px);position:relative;overflow:hidden;touch-action:pan-y;perspective:1000px}
    .md-home-photo img{width:100%;height:100%;display:block;object-fit:cover;object-position:center 58%;transform:scale(1.025);transition:transform .18s ease-out,filter .18s ease-out;will-change:transform}
    .md-home-photo:after{content:"";position:absolute;inset:0;background:linear-gradient(180deg,#0b302d66 0%,transparent 35%,#0b302d05 52%,#0b302dcc 100%);pointer-events:none}
    .md-photo-label{position:absolute;z-index:1;top:14px;left:14px;padding:7px 11px;border:1px solid #fff8;border-radius:20px;background:#0b4d4a99;color:#fff8eb;font-size:10px;font-weight:800;letter-spacing:.08em}
    .md-photo-caption{position:absolute;z-index:1;left:16px;right:16px;bottom:16px}
    .md-photo-caption b{display:block;font-size:20px;line-height:1.15;text-shadow:0 2px 12px #001c19a6}
    .md-photo-caption span{display:block;margin-top:5px;color:#fff8eb;font-size:12px;text-shadow:0 1px 8px #001c19}
    .md-quick{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin:12px 0}
    .md-quick button{font:inherit;text-align:left;border:1px solid #eadcc8;background:#fff;border-radius:18px;padding:13px;color:#4a2d18;box-shadow:0 5px 13px #4a2d180c;min-height:76px}
    .md-quick button:active{transform:scale(.98)}
    .md-quick button b{display:block;color:#0b4d4a;font-size:13px;margin-top:4px}
    .md-quick button span{font-size:21px}
    .md-local-entry{position:relative}
    .md-local-entry .badge{white-space:nowrap}
    .md-local-entry .md-local-source{font-size:10px;color:#71675d;margin-top:6px}    @media(prefers-reduced-motion:reduce){.md-home-photo img{transition:none}}
  `;
  document.head.append(style);

  const hero = home.querySelector('.hero');
  if (hero) {
    const h=hero.querySelector('h1'), p=hero.querySelector('p');
    if (h) h.textContent='Bienvenido a MascotaDatos';
    if (p) p.textContent='Información para cuidar a tu mascota y encontrar servicios en Atacama.';
  }
  const stage=document.createElement('section');
  stage.className='md-home-stage';
  stage.setAttribute('aria-label','Imagen cinematográfica de mascotas en el desierto de Atacama');
  stage.innerHTML='<div class="md-home-photo"><img src="https://appassets.androidplatform.net/assets/md-home-hero.png" alt="Gato y perro en un paisaje de Atacama" loading="eager"><div class="md-photo-label">MASCOTADATOS · ATACAMA</div><div class="md-photo-caption"><b>Cerca de quienes más quieres</b><span>Veterinarias, insumos y servicios locales</span></div></div>';
  if (hero) hero.insertAdjacentElement('afterend',stage);
  else home.prepend(stage);

  const quick=document.createElement('div');
  quick.className='md-quick';
  quick.innerHTML='<button type="button" data-go="veterinarias"><span>🏥</span><b>Veterinarias de Copiapó</b></button><button type="button" data-go="insumosCiudad"><span>🛍️</span><b>Insumos y tiendas locales</b></button>';
  stage.insertAdjacentElement('afterend',quick);

  const offlineData=[
    {n:'Centro Quirúrgico Veterinario CEQVet',a:'Luis Flores 483, Copiapó',p:'+56 9 7633 8018',c:'veterinaria',s:'por_verificar',u:'ceqvetcopiapo.cl'},
    {n:'Hospital Clínico Veterinario Huellitas',a:'Callejón José Joaquín Vallejo 630, Copiapó',p:'+56 52 223 3133 / +56 9 7444 4971',c:'veterinaria',s:'por_verificar',u:'veterinariahuellitas.cl'},
    {n:'Munay Vet — Clínica Veterinaria',a:'Av. Ignacio Carrera Pinto 743, Copiapó',p:'+56 9 7374 6914',c:'veterinaria',s:'por_verificar',u:'munayvet.cl'},
    {n:'Ciudad Mascota',a:'Los Carrera 1036, Copiapó',p:'+56 9 5215 0987',c:'insumos',s:'por_verificar'},
    {n:'Farmacia Veterinaria Los Mancos',a:'Bernardo O’Higgins 21, Copiapó',p:'+56 52 223 9520 / +56 9 8848 4840',c:'insumos',s:'por_verificar',u:'guabinatural.cl'},
    {n:'Farmavet Los Mancos',a:'Rodriguez 760, Copiapó',p:'+56 9 5830 0433',c:'insumos',s:'verificado',u:'farmavetlosmancos.com'},
    {n:'Huellitas Pet Shop',a:'Callejón José Joaquín Vallejo 630, Copiapó',p:'+56 52 223 3133 / +56 9 7444 4971',c:'insumos',s:'por_verificar',u:'veterinariahuellitas.cl'},
    {n:'Mundo Mascotas — Mall Plaza Copiapó',a:'Mall Plaza Copiapó',p:'+56 9 2035 8935',c:'insumos',s:'por_verificar',u:'guabinatural.cl'},
    {n:'Mundo Mascotas — Patio Center San Fernando',a:'Los Carrera 4723, Local 13, Patio Center San Fernando, Copiapó',p:'+56 9 2035 8935',c:'insumos',s:'por_verificar',u:'guabinatural.cl'},
    {n:'Panchitas Pet Shop',a:'La Cruz 678, Copiapó',p:'',c:'insumos',s:'por_verificar'},
    {n:'Pet Shop Matanda',a:'Av. Los Carrera 2354, Copiapó',p:'+56 9 5664 1895',c:'insumos',s:'por_verificar',u:'matandapetshop.cl'},
    {n:'Pet Store Olea',a:'José Joaquín Vallejos 474, Copiapó',p:'',c:'insumos',s:'por_verificar',u:'biogance.cl'}
  ];
  const norm=s=>String(s||'').normalize('NFD').replace(/[\u0300-\u036f]/g,'').toLowerCase().replace(/[^a-z0-9]/g,'');
  function addOfflineDirectory(){
    const status=document.getElementById('onlineStatus');
    const msg=norm(status?.textContent||'');
    if(!msg.includes('sinconexion')&&!msg.includes('desconect'))return;
    const containers={
      veterinaria:(document.getElementById('cityContent')||document.getElementById('vet')),
      insumos:document.getElementById('shopResults')||document.getElementById('shops')
    };
    let inserted=0;
    offlineData.forEach(row=>{
      const box=containers[row.c]; if(!box)return;
      const key=norm(row.n);
      if([...box.querySelectorAll('.item')].some(el=>norm(el.querySelector('h3')?.textContent||el.textContent).includes(key)))return;
      const card=document.createElement('div');card.className='item md-local-entry';card.dataset.search=norm([row.n,row.a,row.p].join(' '));
      const top=document.createElement('div');top.className='top';
      const title=document.createElement('h3');title.textContent=row.n;
      const badge=document.createElement('span');badge.className='badge '+(row.s==='verificado'?'':'warn');badge.textContent=row.s==='verificado'?'Verificado':'En revisión';
      top.append(title,badge);
      const meta=document.createElement('div');meta.className='meta';meta.textContent='📍 '+row.a+(row.p?' · 📞 '+row.p:' · Teléfono no publicado');
      const source=document.createElement('div');source.className='md-local-source';source.textContent=row.u?'Fuente informativa: '+row.u:'Ficha local; contacto pendiente de confirmar.';
      card.append(top,meta,source);box.append(card);inserted++;
    });
    if(inserted&&status)status.textContent='⚠️ Sin conexión a la base online · Mostrando fichas locales guardadas.';
  }
  const originalShow=window.show;
  if(typeof originalShow==='function'&&!window.__mdShowRouteFix){
    window.__mdShowRouteFix=true;
    window.show=function(id){
      const target=(id==='ciudades'&&!document.getElementById('ciudades'))?'veterinarias':id;
      const result=originalShow.call(window,target);
      if(target==='veterinarias'||target==='insumosCiudad')setTimeout(addOfflineDirectory,0);
      return result;
    };
  }
  quick.querySelectorAll('[data-go]').forEach(b=>b.addEventListener('click',()=>window.show?.(b.dataset.go)));

  const photo=stage.querySelector('.md-home-photo'),img=photo.querySelector('img');
  let down=false,x0=0,y0=0;
  photo.addEventListener('pointerdown',e=>{down=true;x0=e.clientX;y0=e.clientY;photo.setPointerCapture(e.pointerId)});
  photo.addEventListener('pointermove',e=>{
    if(!down)return;
    const rx=Math.max(-4,Math.min(4,(e.clientY-y0)*-.035));
    const ry=Math.max(-6,Math.min(6,(e.clientX-x0)*.035));
    img.style.transform='scale(1.06) rotateX('+rx+'deg) rotateY('+ry+'deg)';
    img.style.filter='saturate(1.05) brightness(1.02)';
  });
  const reset=()=>{down=false;img.style.transform='scale(1.025)';img.style.filter='none'};
  photo.addEventListener('pointerup',reset);photo.addEventListener('pointercancel',reset);

  setTimeout(addOfflineDirectory,1200);
  const status=document.getElementById('onlineStatus');
  if(status)new MutationObserver(()=>addOfflineDirectory()).observe(status,{childList:true,subtree:true,characterData:true});
})();