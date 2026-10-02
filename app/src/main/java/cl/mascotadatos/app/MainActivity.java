package cl.mascotadatos.app;

import android.content.ActivityNotFoundException;
import android.Manifest;
import android.content.pm.PackageManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.GeolocationPermissions;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebResourceResponse;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.webkit.WebViewAssetLoader;

public class MainActivity extends AppCompatActivity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private GeolocationPermissions.Callback locationCallback;
    private String locationOrigin;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), true);
        setContentView(R.layout.activity_main);
        webView = findViewById(R.id.webview);

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
            @Override public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (url != null && url.startsWith("https://appassets.androidplatform.net/assets/index.html")) {
                    view.evaluateJavascript("(function(){var c=document.createElement('link');c.rel='stylesheet';c.href='/assets/final-fixes.css';document.head.appendChild(c);var s=document.createElement('script');s.src='/assets/final-fixes.js';document.body.appendChild(s);})();", null);
                }
            }
        });

        webView.setWebChromeClient(new WebChromeClient() {
            @Override public void onGeolocationPermissionsShowPrompt(String origin, GeolocationPermissions.Callback callback) {
                if (!"https://appassets.androidplatform.net".equals(origin)) {
                    callback.invoke(origin, false, false);
                    return;
                }
                if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                        || ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    callback.invoke(origin, true, false);
                    return;
                }
                if (locationCallback != null) locationCallback.invoke(locationOrigin, false, false);
                locationCallback = callback;
                locationOrigin = origin;
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 43);
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
                if (webView != null && webView.canGoBack()) webView.goBack(); else finish();
            }
        });
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 43 && locationCallback != null) {
            boolean granted = false;
            for (int result : grantResults) if (result == PackageManager.PERMISSION_GRANTED) granted = true;
            locationCallback.invoke(locationOrigin, granted, false);
            locationCallback = null;
            locationOrigin = null;
        }
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
        if ("mailto".equalsIgnoreCase(scheme) || "tel".equalsIgnoreCase(scheme)
                || "geo".equalsIgnoreCase(scheme)) {
            try { startActivity(new Intent(Intent.ACTION_VIEW, uri)); return true; }
            catch (ActivityNotFoundException ignored) { return true; }
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

    @Override protected void onDestroy() {
        if (locationCallback != null) {
            locationCallback.invoke(locationOrigin, false, false);
            locationCallback = null;
        }
        if (webView != null) {
            webView.stopLoading();
            webView.setWebChromeClient(null);
            webView.destroy();
            webView = null;
        }
        super.onDestroy();
    }
}
