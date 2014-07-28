package com.ebay.SnapShop;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.*;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.util.*;


public class FragmentTab2 extends Fragment {

    WebView myWebView;
    String message;
    String url;
    InputStream in;

    FragmentTab2(String message,String url,InputStream in)
    {
        this.message = message;
        this.url = url;
        this.in = in;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment2, container, false);

        myWebView = (WebView) rootView.findViewById(R.id.webviewfrag2);
        myWebView.getSettings().setJavaScriptEnabled(true);
       // myWebView.setWebChromeClient(new WebChromeClient());
        myWebView.setWebViewClient(new WebViewClient() {
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {

            }

            public void onPageFinished(WebView view, String url) {
                Log.i("SnapShop","#################");
                CookieManager.getInstance().setAcceptCookie(true);
                CookieSyncManager.getInstance().sync();

            }
        });

        Log.i("SnapShop","################### URL : "+url);
       // myWebView.loadUrl("http://csr.ebay.com/cse/list.jsf?usecase=create&mode=AddItem&categoryId=9355&title=mobile&productReferenceId=153951941");
        myWebView.loadData(getListingURL(in,getListing(message, url)), "text/html", "UTF-8");
       // myWebView.loadUrl("file:///android_asset/ebay-listings.html");
        //System.out.println("$$$$$$$$$$$$$$"+Environment.getRootDirectory());

        CookieManager.getInstance().setAcceptCookie(true);

        return rootView;
    }


    private static String[] getNames( JSONObject jo ) {
        int length = jo.length();
        if (length == 0) return null;
        Iterator iterator = jo.keys();
        String[] names = new String[length];
        int i = 0;
        while (iterator.hasNext()) {
            names[i] = (String)iterator.next();
            i += 1;
        }
        return names;
    }
    public String getListingURL(InputStream in, Map<String, String> info)
    {

        Document doc = null;
        Log.i("SnapShop","$$$$$$$$$$$ INfo get image : "+info.get("imageURL"));
        try
        {
            //doc= Jsoup.connect("http://csr.ebay.com/cse/list.jsf?usecase=create&mode=AddItem&categoryId=9355&title=mobile&productReferenceId=153951941").get();
            String html = "";
            doc= Jsoup.parse(in, "UTF-8", "www.ebay.com");
            //doc = Jsoup.parse(html);
            //System.out.println(doc.getElementById("title"));
            //System.out.println("Description:" + doc.getElementsByAttributeValue("class", "rich-editor").first().toString());
            doc.getElementById("title").attr("value", info.get("Title"));
            doc.getElementsByAttributeValue("class", "rich-editor").first().html(info.get("Description"));
            doc.getElementById("startPrice").attr("value", info.get("Price"));
            doc.getElementsByAttributeValue("class", "thumb").first().attr("src", info.get("imageURL"));
        }
        catch(Exception e)
        {
            //System.out.println("There was an exception parsing the seller page."+e.getMessage());
        }
        return doc.toString();
    }
    public String getListingHTML(Map<String, String> info) {
        Context context = this.getActivity().getApplicationContext();
        Log.i("SnapShop", "#################File Path : " + context.getFilesDir());
        File input = new File(context.getFilesDir() + "/" + "ebay-listings.html");
       // File input= new File("libs/ebay-listings.html");
        Document doc = null;
        try
        {
            doc= Jsoup.parse(input, "UTF-8", "www.ebay.com");
            //System.out.println(doc.getElementById("title"));
            //System.out.println("Description:" + doc.getElementsByAttributeValue("class", "rich-editor").first().toString());
            doc.getElementById("title").attr("value", info.get("Title"));
            doc.getElementsByAttributeValue("class", "rich-editor").first().html(info.get("Description"));
            doc.getElementById("startPrice").attr("value", info.get("Price"));
            doc.getElementsByAttributeValue("class", "thumb").first().attr("src", info.get("imageURL"));
        }
        catch(Exception e)
        {
            //System.out.println("There was an exception parsing the seller page."+e.getMessage());
        }
        return doc.toString();
    }


    public Map<String,String> getListing(String keyword, String imageURL)
    {
        String keyword1 = keyword;
        keyword = keyword.replaceAll(" ","%20");
        String url = "http://svcs.ebay.com/services/search/FindingService/v1?OPERATION-NAME=findItemsByKeywords&SERVICE-NAME=FindingService&SERVICE-VERSION=1.0.0&GLOBAL-ID=EBAY-US&SECURITY-APPNAME=AdityaPa-f521-449a-8b91-b3b3b0b876ad&RESPONSE-DATA-FORMAT=JSON&REST-PAYLOAD&keywords="+keyword;
        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);
        request.setHeader("X-Mashape-Key",
                "dUsmUexyLjmshbZvf4t9kZNOUXv6p1J0sXYjsnHaGQhjtWb7dK");
        JSONObject root = null;
        Map<String,String> itemProperties = new HashMap<String,String>();
        itemProperties.put("Description", keyword1);
        itemProperties.put("Category","");
        itemProperties.put("Price", "");
        itemProperties.put("Title", keyword1);
        itemProperties.put("CategoryId","");
        itemProperties.put("imageURL", imageURL);
        try
        {
            org.apache.http.HttpResponse response = client
                    .execute(request);
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream stream = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                root = new JSONObject(sb.toString());
                //System.out.println(root);
            }
            List<String> names = new ArrayList<String>(Arrays.asList(getNames(root)));
            for(String name:names)
            {
                //System.out.println(name);
            }



            JSONArray array = root.getJSONArray(names.get(0));
            //System.out.println(array.get(0));
            JSONObject obj = array.getJSONObject(0);
            array = obj.getJSONArray("searchResult");
            //System.out.println(array.get(0));
            obj = array.getJSONObject(0);
            array = obj.getJSONArray("item");
            int count = 0;
            String itemId = "";
            for(int i=0;i<array.length();i++)
            {
                JSONObject o = array.getJSONObject(i);
                JSONArray tempArray = o.getJSONArray("topRatedListing");
                if(tempArray.getString(0).equals("true"))
                {
                    //System.out.println(o.get("itemId")+":"+o.get("topRatedListing"));

                    itemId = o.getJSONArray("itemId").getString(0);
                    count+=1;
                    break;
                }

            }
            //System.out.println(itemId+"done");

            //add itemId to the get first item call

            JSONObject getDetails = getdetails(itemId);
            getDetails = getDetails.getJSONObject("Item");
            List<String> itemspecs = new ArrayList<String>(Arrays.asList(getNames(getDetails)));
            for(String spec:itemspecs)
            {
                itemProperties.put("Description", getDetails.getString("Description"));
                itemProperties.put("Category", getDetails.getString("PrimaryCategoryName").split(":")[0]);
                obj = getDetails.getJSONObject("ConvertedCurrentPrice");
                itemProperties.put("Price", obj.get("Value").toString());
                itemProperties.put("Title", getDetails.getString("Title"));
                itemProperties.put("CategoryId",getDetails.get("PrimaryCategoryID").toString());
                //System.out.println(spec);
            }
            itemProperties.put("imageURL", imageURL);
            //System.out.println(itemProperties);

        }
        catch(Exception e)
        {
            //System.out.println(e.getMessage());
        }
        return itemProperties;

    }

    private JSONObject getdetails(String itemid)
    {
        String urlStr = "http://open.api.ebay.com/shopping?"
                + "callname=GetSingleItem&" +
                "responseencoding=JSON&" +
                "appid=" + "AdityaPa-f521-449a-8b91-b3b3b0b876ad" +"&" +
                "siteid=0&" +
                "version=525&" +
                "IncludeSelector=TextDescription,ItemSpecifics,ShippingCosts&" +
                "ItemID=" + itemid + "&";

        HttpClient httpclient = new DefaultHttpClient();


        HttpGet httpget = new HttpGet(urlStr);
        //System.out.println("executing request " + httpget.getRequestLine());
        try
        {
            HttpResponse response = httpclient.execute(httpget);
            HttpEntity resEntity = response.getEntity();
            JSONObject root = null;
            if (resEntity != null) {
                InputStream stream = resEntity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream));
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                root = new JSONObject(sb.toString());
                //System.out.println(root);
            }
            return root;


        }
        catch(Exception e)
        {
            //System.out.println("Exception in the GetFirstItem call "+e.getMessage());
        }
        return null;
    }



}
class MyWebViewClientFrag2 extends WebViewClient {
    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        // Otherwise, the link is not for a page on my site, so launch another Activity that handles URLs

        return true;
    }
}
class MyJavaScriptInterface
{
    @JavascriptInterface
    @SuppressWarnings("unused")
    public void processHTML(String html)
    {
        // process the html as needed by the app
        //System.out.println("$$$$$$$$$$$$$$$$HTML"+html);
    }
}
