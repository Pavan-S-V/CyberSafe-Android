
package com.cybersafe.security;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private static final String HOME =
            "https://cyber--safe.vercel.app/";

    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);

        TextView header = new TextView(this);
        header.setText("  CyberSafe");
        header.setTextSize(20);
        header.setTextColor(Color.WHITE);
        header.setBackgroundColor(Color.rgb(37, 99, 255));
        header.setPadding(16, 16, 16, 16);
        root.addView(header);

        webView = new WebView(this);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.setWebChromeClient(new WebChromeClient());

        root.addView(webView, new LinearLayout.LayoutParams(
                -1, 0, 1f));

        setContentView(root);
        handleLink(getIntent());
    }

    private void handleLink(Intent intent) {
        Uri uri = intent == null ? null : intent.getData();

        if (uri != null && isWebUrl(uri)) {
            askUser(uri.toString());
        } else {
            webView.loadUrl(HOME);
        }
    }

    private boolean isWebUrl(Uri uri) {
        String scheme = uri.getScheme();
        return uri.getHost() != null &&
                ("http".equalsIgnoreCase(scheme) ||
                 "https".equalsIgnoreCase(scheme));
    }

    private void askUser(String url) {
        new AlertDialog.Builder(this)
                .setTitle("Check this link with CyberSafe?")
                .setMessage(url)
                .setPositiveButton("Check URL risk", (dialog, which) -> {
                    String target = HOME +
                            "analyzer.html?sharedUrl=" + Uri.encode(url);
                    webView.loadUrl(target);
                })
                .setNegativeButton("Open normally", (dialog, which) -> {
                    try {
                        startActivity(new Intent(
                                Intent.ACTION_VIEW, Uri.parse(url)));
                        webView.loadUrl(HOME);
                    } catch (Exception e) {
                        Toast.makeText(this,
                                "Unable to open this link.",
                                Toast.LENGTH_LONG).show();
                        webView.loadUrl(HOME);
                    }
                })
                .setOnCancelListener(dialog -> webView.loadUrl(HOME))
                .show();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleLink(intent);
    }

    @Override
    public void onBackPressed() {
        if (webView != null && webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
