package com.ebay.SnapShop;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.*;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import org.apache.commons.codec.StringEncoderComparator;

import java.util.Arrays;
import java.util.List;

public class FragmentTab1 extends Fragment {

    public String UrlString= "http://www.ebay.com/sch/i.html?_nkw=";
    WebView myWebView;
    String message;

    FragmentTab1(String message)
    {
        this.message = message;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment1, container, false);

        myWebView = (WebView) rootView.findViewById(R.id.webviewfrag1);
        myWebView.getSettings().setJavaScriptEnabled(true);


        myWebView.setWebViewClient(new MyWebViewClient());
        myWebView.loadUrl(getUrlWithKeywords(message));

        return rootView;
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