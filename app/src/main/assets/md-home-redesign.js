/* Portada interactiva MascotaDatos · conserva logo, navegación y contenido existente. */
(() => {
  if (window.__mdHome3dInstalled) return;
  window.__mdHome3dInstalled = true;
  const home = document.getElementById('inicio');
  if (!home) return;
  const hero = home.querySelector('.hero');
  if (!hero) return;

  const style = document.createElement('style');
  style.textContent = `
    #inicio .hero{background:linear-gradient(135deg,#fff8eb 0%,#f4e6ce 100%);border:1px solid #eadcc8;border-radius:24px;padding:20px;box-shadow:0 12px 28px #4a2d1814}
    #inicio .hero img{display:none}
    #inicio .hero h1{font-size:24px;line-height:1.12;color:#0b4d4a}
    #inicio .hero p{font-size:14px;line-height:1.5;color:#5e5045;margin-top:7px}
    .md-home-stage{position:relative;overflow:hidden;isolation:isolate;margin:14px 0 17px;padding:18px 16px 16px;border-radius:26px;background:radial-gradient(circle at 75% 22%,#f9dca2 0,transparent 27%),linear-gradient(145deg,#0b4d4a,#126d65 66%,#184c48);color:#fff;box-shadow:0 16px 30px #0b4d4a28}
    .md-home-stage:before,.md-home-stage:after{content:"";position:absolute;border-radius:50%;pointer-events:none;z-index:-1}
    .md-home-stage:before{width:180px;height:180px;right:-65px;top:-75px;border:1px solid #ffffff35;box-shadow:0 0 0 18px #ffffff0b,0 0 0 38px #ffffff08}
    .md-home-stage:after{width:110px;height:110px;left:-62px;bottom:-60px;background:#d98a2528}
    .md-stage-kicker{font-size:11px;text-transform:uppercase;letter-spacing:.12em;color:#f7dca9;font-weight:800}
    .md-stage-title{font-size:19px;font-weight:800;margin:5px 0 2px}
    .md-stage-hint{font-size:12px;color:#e2f0e8;margin:0}
    .md-carousel{height:224px;position:relative;perspective:850px;touch-action:pan-y;cursor:grab;user-select:none}
    .md-carousel:active{cursor:grabbing}
    .md-cylinder{position:absolute;width:170px;height:170px;left:50%;top:25px;transform:translateX(-50%);transform-style:preserve-3d;will-change:transform}
    .md-pet-face{position:absolute;inset:0;border:1px solid #ffffff4a;border-radius:28px;background:linear-gradient(145deg,#fff7e5,#f1d6a5);box-shadow:0 16px 28px #001c194d;backface-visibility:hidden;display:grid;place-items:center}
    .md-pet-face:nth-child(1){transform:rotateY(0deg) translateZ(112px)}
    .md-pet-face:nth-child(2){transform:rotateY(120deg) translateZ(112px)}
    .md-pet-face:nth-child(3){transform:rotateY(240deg) translateZ(112px)}
    .md-pet-face svg{width:148px;height:148px;filter:drop-shadow(0 9px 7px #4a2d1833)}
    .md-orbit{position:absolute;left:50%;top:43px;width:225px;height:142px;transform:translateX(-50%) rotateX(69deg);border:1px solid #ffffff55;border-radius:50%;pointer-events:none}
    .md-rotate-hint{position:absolute;bottom:5px;left:0;right:0;text-align:center;font-size:11px;color:#e4f2eb}
    .md-stage-controls{display:flex;justify-content:center;gap:8px;margin-top:2px}
    .md-stage-controls button{width:38px;height:38px;border-radius:50%;border:1px solid #ffffff5c;color:#fff;background:#ffffff16;font-size:17px}
    .md-quick{display:grid;grid-template-columns:1fr 1fr;gap:10px;margin:12px 0}
    .md-quick button{font:inherit;text-align:left;border:1px solid #eadcc8;background:#fff;border-radius:18px;padding:13px;color:#4a2d18;box-shadow:0 5px 13px #4a2d180c;min-height:75px}
    .md-quick button b{display:block;color:#0b4d4a;font-size:13px;margin-top:4px}
    .md-quick button span{font-size:21px}
    @media (max-width:360px){.md-carousel{height:205px}.md-cylinder{width:148px;height:148px}.md-pet-face:nth-child(1){transform:rotateY(0deg) translateZ(98px)}.md-pet-face:nth-child(2){transform:rotateY(120deg) translateZ(98px)}.md-pet-face:nth-child(3){transform:rotateY(240deg) translateZ(98px)}}
    @media(prefers-reduced-motion:reduce){.md-cylinder{transition:none!important}}
  `;
  document.head.append(style);

  const face = (kind, label) => {
    const cat = kind === 'cat';
    return '<div class="md-pet-face" role="img" aria-label="'+label+'"><svg viewBox="0 0 160 160" xmlns="http://www.w3.org/2000/svg"><defs><linearGradient id="fur'+kind+'" x2="0.8" y2="1"><stop stop-color="'+(cat?'#d98a25':'#f0c47b')+'"/><stop offset="1" stop-color="'+(cat?'#bd542b':'#d98a25')+'"/></linearGradient><radialGradient id="shine'+kind+'"><stop stop-color="#fff9e9"/><stop offset="1" stop-color="#f1d6a5"/></radialGradient></defs><ellipse cx="80" cy="137" rx="48" ry="9" fill="#4a2d1824"/>'+(cat?'<path d="M38 64 33 20 67 43Q80 38 93 43L127 20 122 66Q137 91 119 113Q103 132 80 132Q57 132 41 113Q23 91 38 64Z"':'<path d="M42 51Q24 23 16 46Q8 78 39 88Q39 112 57 126Q80 142 103 126Q121 112 121 88Q152 78 144 46Q136 23 118 51Q103 39 80 39Q57 39 42 51Z')+' " fill="url(#fur'+kind+')" stroke="#8b4b25" stroke-width="3"/><ellipse cx="80" cy="89" rx="42" ry="34" fill="url(#shine'+kind+')" opacity=".88"/><ellipse cx="61" cy="77" rx="5" ry="7" fill="#263a32"/><ellipse cx="99" cy="77" rx="5" ry="7" fill="#263a32"/><path d="M74 92Q80 99 86 92M80 98Q77 106 70 106M80 98Q83 106 90 106" fill="none" stroke="#4a2d18" stroke-width="3" stroke-linecap="round"/><path d="M53 101 25 96M54 108 29 112M107 101 135 96M106 108 131 112" stroke="#8b4b25" stroke-width="2" stroke-linecap="round"/><circle cx="48" cy="96" r="3" fill="#bd542b"/><circle cx="112" cy="96" r="3" fill="#bd542b"/><path d="M55 121Q80 141 105 121" fill="none" stroke="#fff8eb" stroke-width="5" stroke-linecap="round"/></svg></div>';
  };
  const stage = document.createElement('section');
  stage.className = 'md-home-stage';
  stage.setAttribute('aria-label','Mascotas ilustradas en escena 3D giratoria');
  stage.innerHTML = '<div class="md-stage-kicker">MascotaDatos · Atacama</div><div class="md-stage-title">Un mundo para cuidarles mejor</div><p class="md-stage-hint">Veterinarias, insumos y servicios locales en un solo lugar.</p><div class="md-carousel" aria-label="Arrastra para girar la escena 360 grados"><div class="md-orbit"></div><div class="md-cylinder">'+face('cat','Gato ilustrado en 3D')+face('dog','Perro ilustrado en 3D')+face('cat','Gato ilustrado en 3D')+'</div><div class="md-rotate-hint">↔ Arrastra para explorar en 360°</div></div><div class="md-stage-controls"><button type="button" aria-label="Girar a la izquierda">‹</button><button type="button" aria-label="Girar a la derecha">›</button></div>';
  hero.insertAdjacentElement('afterend',stage);

  const quick=document.createElement('div');
  quick.className='md-quick';
  quick.innerHTML='<button type="button" data-go="ciudades"><span>🏥</span><b>Veterinarias cercanas</b></button><button type="button" data-go="insumosCiudad"><span>🛍️</span><b>Insumos locales</b></button>';
  stage.insertAdjacentElement('afterend',quick);
  quick.querySelectorAll('[data-go]').forEach(b=>b.addEventListener('click',()=>{if(typeof window.show==='function')window.show(b.dataset.go)}));

  const carousel=stage.querySelector('.md-carousel'), cylinder=stage.querySelector('.md-cylinder');
  let angle=0,startX=0,startY=0,dragging=false;
  const draw=()=>{cylinder.style.transform='translateX(-50%) rotateX(-7deg) rotateY('+angle+'deg)'};
  const step=dir=>{angle+=dir*120;draw()};
  const buttons=stage.querySelectorAll('.md-stage-controls button');
  buttons[0].addEventListener('click',()=>step(-1));buttons[1].addEventListener('click',()=>step(1));
  carousel.addEventListener('pointerdown',e=>{dragging=true;startX=e.clientX;startY=e.clientY;carousel.setPointerCapture(e.pointerId);});
  carousel.addEventListener('pointermove',e=>{if(!dragging)return;const dx=e.clientX-startX;angle+=dx*.85;startX=e.clientX;draw();});
  const stop=()=>{dragging=false};carousel.addEventListener('pointerup',stop);carousel.addEventListener('pointercancel',stop);draw();
})();