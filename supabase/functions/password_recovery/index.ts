// El formulario vive en GitHub Pages: los dominios compartidos de Edge Functions sirven HTML como texto.
// Location sin fragmento conserva el fragmento de recuperación en el navegador.
// No registrar ni reenviar tokens a otros destinos.
const RECOVERY_PAGE = "https://torresalejandroaltamirano.github.io/MascotaDatos-/recuperar-clave.html";
Deno.serve((request: Request) => {
  if (request.method !== "GET" && request.method !== "HEAD") {
    return new Response("Método no permitido", {status: 405, headers: {"Allow": "GET, HEAD", "Cache-Control": "no-store"}});
  }
  return new Response(null, {
    status: 303,
    headers: {"Location": RECOVERY_PAGE, "Cache-Control": "no-store", "Referrer-Policy": "no-referrer"}
  });
});
