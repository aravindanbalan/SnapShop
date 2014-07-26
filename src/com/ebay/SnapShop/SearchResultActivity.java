package com.ebay.SnapShop;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by arbalan on 7/26/14.
 */
public class SearchResultActivity extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchresults);
        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);

        myWebView.setWebViewClient(new MyWebViewClient());
        String customHtml = "<html><body><h1>Hello, WebView</h1></body></html>";
       // myWebView.loadData(customHtml, "text/html", "UTF-8");
        //setContentView(myWebView);
        myWebView.loadUrl("http://www.ebay.com");
    }

}
class MyWebViewClient extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

        return true;
    }
}