package com.ebay.SnapShop;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import java.util.Arrays;
import java.util.List;


/**
 * Created by arbalan on 7/26/14.
 */
public class SearchResultActivity extends Activity{
    public final static String EXTRA_MESSAGE = "com.ebay.SnapShop.MESSAGE";
    public String UrlString= "http://www.ebay.com/sch/i.html?_nkw=";
    WebView myWebView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.searchresults);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        myWebView = (WebView) findViewById(R.id.webview);
        myWebView.getSettings().setJavaScriptEnabled(true);

        Intent intent = getIntent();
        String message = intent.getStringExtra(EXTRA_MESSAGE);
        Log.i("SnapShop","##########"+message);

        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(getUrlWithKeywords(message));

    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myWebView.goBack();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public String getUrlWithKeywords(String keywords)
    {
        List<String> keywordsList =  Arrays.asList(keywords.split(" "));
        String keyWordParam= new String();
        for(String keyword: keywordsList) {
            keyWordParam= keyWordParam + keyword + "+";
        }

        return UrlString + keyWordParam;
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