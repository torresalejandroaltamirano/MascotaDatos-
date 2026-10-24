package cl.mascotadatos.app;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
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

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.ump.ConsentInformation;
import com.google.android.ump.ConsentRequestParameters;
import com.google.android.ump.UserMessagingPlatform;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private FrameLayout adContainer;
    private Button privacyOptionsButton;
    private AdView adView;
    private ConsentInformation consentInformation;
    private boolean pageReady = false;
    private boolean adsInitialized = false;

    private static final String TEST_BANNER_ID = "ca-app-pub-3940256099942544/9214589741";
    private static final String LIVE_BANNER_ID = "ca-app-pub-2369519530952827/7526305566";

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);
        adContainer = findViewById(R.id.ad_view_container);
        privacyOptionsButton = findViewById(R.id.privacy_options_button);
        privacyOptionsButton.setOnClickListener(v ->
                UserMessagingPlatform.showPrivacyOptionsForm(MainActivity.this, formError -> updatePrivacyOptionsButton()));

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
            @Override public android.webkit.WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
                return loader.shouldInterceptRequest(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                return openExternal(request.getUrl());
            }
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return openExternal(Uri.parse(url));
            }
            @Override public void onPageStarted(WebView view, String url, android.graphics.Bitmap favicon) {
                pageReady = false;
                destroyBanner();
                super.onPageStarted(view, url, favicon);
            }
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.startsWith("https://appassets.androidplatform.net/assets/index.html")) {
                    view.evaluateJavascript("(function(){var c=document.createElement('link');c.rel='stylesheet';c.href='/assets/final-fixes.css';document.head.appendChild(c);var s=document.createElement('script');s.src='/assets/final-fixes.js';document.body.appendChild(s);})();", null);
                    pageReady = true;
                    maybeInitializeAds();
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
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

        requestConsent();

        if (savedInstanceState == null) {
            webView.loadUrl("https://appassets.androidplatform.net/assets/index.html");
        } else {
            webView.restoreState(savedInstanceState);
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override public void handleOnBackPressed() {
                if (webView == null) { finish(); return; }
                webView.evaluateJavascript(
                    "(function(){try{" +
                    "if(typeof window.mascotaDatosBack==='function') return window.mascotaDatosBack()?'handled':'unhandled';" +
                    "return 'unhandled';" +
                    "}catch(e){return 'unhandled';}})()",
                    result -> {
                        if ("\\"handled\\"".equals(result)) return;
                        if (webView != null && webView.canGoBack()) webView.goBack();
                        else finish();
                    });
            }
        });
    }

    private void requestConsent() {
        consentInformation = UserMessagingPlatform.getConsentInformation(this);
        ConsentRequestParameters params = new ConsentRequestParameters.Builder().build();
        consentInformation.requestConsentInfoUpdate(
                this,
                params,
                () -> {
                    updatePrivacyOptionsButton();
                    UserMessagingPlatform.loadAndShowConsentFormIfRequired(this, formError -> {
                        updatePrivacyOptionsButton();
                        maybeInitializeAds();
                    });
                },
                requestError -> {
                    updatePrivacyOptionsButton();
                    maybeInitializeAds();
                });
    }

    private void updatePrivacyOptionsButton() {
        if (privacyOptionsButton == null || consentInformation == null) return;
        boolean required = consentInformation.getPrivacyOptionsRequirementStatus()
                == ConsentInformation.PrivacyOptionsRequirementStatus.REQUIRED;
        privacyOptionsButton.setVisibility(required ? View.VISIBLE : View.GONE);
    }

    private void maybeInitializeAds() {
        if (!pageReady || consentInformation == null || !consentInformation.canRequestAds()) return;
        if (adsInitialized) {
            if (adView == null) loadBanner();
            return;
        }
        adsInitialized = true;
        MobileAds.initialize(this, status -> runOnUiThread(() -> {
            if (pageReady) loadBanner();
        }));
    }

    private void loadBanner() {
        if (isFinishing() || !pageReady || adContainer == null) return;
        destroyBanner();
        adView = new AdView(this);
        int widthDp = (int) (getResources().getDisplayMetrics().widthPixels / getResources().getDisplayMetrics().density);
        adView.setAdSize(AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(this, widthDp));
        adView.setAdUnitId(BuildConfig.ADMOB_LIVE_ADS_ENABLED && !BuildConfig.DEBUG ? LIVE_BANNER_ID : TEST_BANNER_ID);
        adContainer.addView(adView, new FrameLayout.LayoutParams(-1, -2));
        adContainer.setVisibility(View.VISIBLE);
        adView.loadAd(new AdRequest.Builder().build());
    }

    private void destroyBanner() {
        if (adView != null) {
            adContainer.removeView(adView);
            adView.destroy();
            adView = null;
        }
        if (adContainer != null) adContainer.setVisibility(View.GONE);
    }

    private boolean openExternal(Uri uri) {
        if (uri == null) return false;
        String scheme = uri.getScheme();
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

    @Override protected void onPause() {
        if (adView != null) adView.pause();
        super.onPause();
    }

    @Override protected void onResume() {
        super.onResume();
        if (adView != null) adView.resume();
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        webView.saveState(outState);
        super.onSaveInstanceState(outState);
    }

    @Override protected void onDestroy() {
        destroyBanner();
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
