/* Cuenta: acceso primero; privacidad en una pantalla independiente. */
(() => {
  const account = document.getElementById('cuenta');
  const privacy = document.getElementById('md-privacy-contact');
  const login = account?.querySelector('form[onsubmit="loginAccount(event)"]')?.closest('.card');
  const register = account?.querySelector('form[onsubmit="registerAccount(event)"]')?.closest('.card');
  const grid = account?.querySelector(':scope > .grid');
  if (!account || !privacy || !login || !register || !grid) return;

  grid.id = 'md-account-access';
  grid.prepend(login);
  const registration = document.createElement('details');
  registration.id = 'md-account-registration';
  const summary = document.createElement('summary');
  summary.textContent = '¿Primera vez? Crear una cuenta';
  registration.append(summary, register);
  grid.append(registration);

  const page = document.createElement('section');
  page.id = 'cuentaPrivacidad';
  page.className = 'view';
  const back = document.createElement('button');
  back.type = 'button'; back.className = 'back'; back.textContent = '← Volver a Mi cuenta';
  back.onclick = () => show('cuenta');
  const title = document.createElement('h2'); title.textContent = 'Privacidad y datos';
  page.append(back, title, privacy);
  account.after(page);

  const settings = document.createElement('div');
  settings.className = 'card'; settings.id = 'md-account-settings';
  const settingsTitle = document.createElement('h3'); settingsTitle.textContent = 'Configuración de la cuenta';
  const link = document.createElement('button');
  link.type = 'button'; link.className = 'btn'; link.textContent = 'Privacidad y datos';
  link.onclick = () => { show('cuentaPrivacidad'); page.scrollIntoView({block:'start'}); };
  const hint = document.createElement('p'); hint.className = 'small';
  hint.textContent = 'Consulta la política de privacidad o solicita la eliminación de tu cuenta.';
  settings.append(settingsTitle, link, hint); account.append(settings);

  const originalShow = window.show;
  window.show = id => {
    const result = originalShow(id);
    if (id === 'cuenta' && result) account.scrollIntoView({block:'start'});
    return result;
  };

  const style = document.createElement('style'); style.id = 'md-account-layout';
  style.textContent = `
    #cuenta>#md-account-access{grid-template-columns:minmax(0,1fr)!important;gap:18px!important}
    #cuenta{scroll-margin-top:84px}
    #md-account-registration{border:1px solid #ded8cc;border-radius:18px;background:#fffdf9;overflow:hidden}
    #md-account-registration>summary{padding:18px 20px;min-height:24px;cursor:pointer;font-weight:600;color:#1b4d4f;line-height:1.5}
    #md-account-registration>summary:focus-visible{outline:3px solid #bd884f;outline-offset:-4px}
    #md-account-registration>.card{margin:0;border:0;box-shadow:none;padding:20px}
    #md-account-settings{margin-top:24px;border-top:1px solid #ded8cc}
    #md-account-settings>.btn{min-height:52px;white-space:normal}
    #cuentaPrivacidad{scroll-margin-top:16px}
    #cuentaPrivacidad #md-privacy-contact{margin-top:20px!important}
    #cuentaPrivacidad h2{line-height:1.3}
  `;
  document.head.append(style);
})();
