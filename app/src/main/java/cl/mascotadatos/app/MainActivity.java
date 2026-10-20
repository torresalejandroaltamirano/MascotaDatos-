package cl.mascotadatos.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Button;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private androidx.compose.ui.platform.ComposeView composeHome;
    private boolean pageReady;
    private String pendingRoute;

    private void openSection(String route) {
        if (!pageReady) { pendingRoute = route; return; }
        // El único argumento procede del enum nativo; JSON evita interpolación de código.
        String id = org.json.JSONObject.quote(route);
        webView.evaluateJavascript("(function(){if(typeof show!=='function'||!show(" + id + "))return false;" +
            "window.scrollTo(0,0);" +
            "if(" + id + "==='favoritos'&&typeof loadFavorites==='function')loadFavorites();" +
            "if(" + id + "==='notificaciones'&&typeof loadNotifications==='function')loadNotifications();" +
            "return true;})()", result -> {
                if ("true".equals(result)) setHomeVisible(false);
            });
    }

    private void setHomeVisible(boolean visible) {
        composeHome.setVisibility(visible ? View.VISIBLE : View.GONE);
        webView.setVisibility(visible ? View.INVISIBLE : View.VISIBLE);
        if (visible) {
            webView.clearFocus();
            android.view.inputmethod.InputMethodManager keyboard =
                (android.view.inputmethod.InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            if (keyboard != null) keyboard.hideSoftInputFromWindow(webView.getWindowToken(), 0);
        }
        webView.setImportantForAccessibility(visible ? View.IMPORTANT_FOR_ACCESSIBILITY_NO_HIDE_DESCENDANTS : View.IMPORTANT_FOR_ACCESSIBILITY_AUTO);
    }

    private void connectHome() {
        pageReady = true;
        // Solo observar navegación de la página local; no se expone un JavascriptInterface.
        webView.evaluateJavascript("(function(){if(window.mdNativeObserver)return;" +
            "const notify=()=>{const v=document.querySelector('.view.active');" +
            "document.title='MascotaDatos:native:'+(v?v.id:'inicio');};" +
            "window.mdNativeObserver=new MutationObserver(notify);" +
            "document.querySelectorAll('.view').forEach(v=>window.mdNativeObserver.observe(v,{attributes:true,attributeFilter:['class']}));notify();})()", null);
        if (pendingRoute != null) { String route = pendingRoute; pendingRoute = null; openSection(route); }
    }
    private AdView banner;
    private ConsentInformation consent;
    private boolean adsStarted;
    private boolean destroyed;
    private boolean refreshingAssets;

    private void updatePrivacyOptions() {
        if (destroyed) return;
        findViewById(R.id.privacy_options).setVisibility(
            consent.getPrivacyOptionsRequirementStatus() ==
                ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED ? View.VISIBLE : View.GONE);
    }

    private void startAdsIfAllowed() {
        if (destroyed || adsStarted || !consent.canRequestAds()) return;
        adsStarted = true;
        MobileAds.initialize(this, status -> runOnUiThread(() -> {
            if (destroyed || !consent.canRequestAds()) return;
            FrameLayout container = findViewById(R.id.banner_container);
            container.post(() -> {
                if (destroyed || !consent.canRequestAds()) return;
                int width = Math.round(container.getWidth() / getResources().getDisplayMetrics().density);
                if (width <= 0) width = getResources().getConfiguration().screenWidthDp;
                banner = new AdView(this);
                banner.setAdUnitId(BuildConfig.BANNER_AD_UNIT_ID);
                banner.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, width));
                banner.setAdListener(new AdListener() {
                    @Override public void onAdLoaded() {
                        if (!destroyed) container.setVisibility(View.VISIBLE);
                    }
                    @Override public void onAdFailedToLoad(LoadAdError error) {
                        if (!destroyed) container.setVisibility(View.GONE);
                    }
                });
                container.addView(banner);
                banner.loadAd(new AdRequest.Builder().build());
            });
        }));
    }

    private void configureAds() {
        consent = UserMessagingPlatform.getConsentInformation(this);
        Button privacy = findViewById(R.id.privacy_options);
        privacy.setOnClickListener(view -> {
            if (banner != null) { banner.destroy(); banner = null; }
            FrameLayout container = findViewById(R.id.banner_container);
            container.removeAllViews();
            container.setVisibility(View.GONE);
            adsStarted = false;
            UserMessagingPlatform.showPrivacyOptionsForm(this, error -> {
                updatePrivacyOptions();
                startAdsIfAllowed();
            });
        });
        consent.requestConsentInfoUpdate(this, new ConsentRequestParameters.Builder().build(), () -> {
            if (destroyed) return;
            updatePrivacyOptions();
            UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, error -> {
                updatePrivacyOptions();
                startAdsIfAllowed();
            });
        }, error -> { updatePrivacyOptions(); startAdsIfAllowed(); });
        startAdsIfAllowed();
    }

    private ValueCallback<Uri[]> fileCallback;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        composeHome = findViewById(R.id.compose_home);
        cl.mascotadatos.app.ui.MascotaHomeHost.install(composeHome, this::openSection);
        configureAds();

        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setDatabaseEnabled(true);
        s.setAllowFileAccess(false);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);

        final WebViewAssetLoader loader = new WebViewAssetLoader.Builder()
                .addPathHandler("/assets/", new WebViewAssetLoader.AssetsPathHandler(this))
                .build();

        webView.setWebViewClient(new WebViewClient() {
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (destroyed || !url.startsWith("https://appassets.androidplatform.net/assets/")) return;
                android.content.SharedPreferences prefs = getSharedPreferences("asset_updates", MODE_PRIVATE);
                if (refreshingAssets) { prefs.edit().putInt("version", BuildConfig.VERSION_CODE).apply(); connectHome(); return; }
                if (prefs.getInt("version", 0) >= BuildConfig.VERSION_CODE) { connectHome(); return; }
                refreshingAssets = true;
                // Retirar solo caché de la interfaz antigua: conservar localStorage, sesión y favoritos.
                view.evaluateJavascript("(async function(){try{" +
                    "if(navigator.serviceWorker){const r=await navigator.serviceWorker.getRegistrations();await Promise.all(r.map(x=>x.unregister()));}" +
                    "if(window.caches){const k=await caches.keys();await Promise.all(k.map(x=>caches.delete(x)));}" +
                    "}finally{location.replace('https://appassets.androidplatform.net/assets/index.html?build=" + BuildConfig.VERSION_CODE + "');}})()", null);
            }
            @Override public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return openExternal(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return openExternal(Uri.parse(url));
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                if (pageReady && view.getUrl() != null &&
                    view.getUrl().startsWith("https://appassets.androidplatform.net/assets/") &&
                    title != null && title.startsWith("MascotaDatos:native:")) {
                    setHomeVisible("MascotaDatos:native:inicio".equals(title));
                }
            }
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                Intent intent = params.createIntent();
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                try { startActivityForResult(intent, 42); }
                catch (ActivityNotFoundException e) { fileCallback = null; callback.onReceiveValue(null); return false; }
                return true;
            }
        });

        if (savedInstanceState == null) {
            webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
        } else {
            webView.restoreState(savedInstanceState);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (webView == null || composeHome.getVisibility() == View.VISIBLE) { finish(); return; }
                webView.evaluateJavascript(
                    "(function(){try{" +
                    "if(typeof window.mascotaDatosBack==='function') return window.mascotaDatosBack()?'handled':'unhandled';" +
                    "return 'unhandled';" +
                    "}catch(e){return 'unhandled';}})()",
                    result -> {
                        if ("\"handled\"".equals(result)) return;
                        if (webView != null && webView.canGoBack()) webView.goBack();
                        else finish();
                    });
            }
        });
    }

    private boolean openExternal(Uri uri) {
        if (uri == null) return false;
        String scheme = uri.getScheme();
        if ("mailto".equalsIgnoreCase(scheme)) {
            try { startActivity(new Intent(Intent.ACTION_SENDTO, uri)); return true; }
            catch (ActivityNotFoundException ignored) { return true; }
        }
        if ("https".equalsIgnoreCase(scheme) || "http".equalsIgnoreCase(scheme)) {
            String host = uri.getHost();
            if ("appassets.androidplatform.net".equalsIgnoreCase(host)) return false;
            try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); return true; }
            catch (ActivityNotFoundException ignored) { return false; }
        }
        return false;
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode != 42 || fileCallback == null) return;
        Uri[] result = WebChromeClient.FileChooserParams.parseResult(resultCode, data);
        fileCallback.onReceiveValue(result);
        fileCallback = null;
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onPause() {
        if (banner != null) banner.pause();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (banner != null) banner.resume();
    }

    @Override protected void onDestroy() {
        destroyed = true;
        if (banner != null) { banner.destroy(); banner = null; }
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
