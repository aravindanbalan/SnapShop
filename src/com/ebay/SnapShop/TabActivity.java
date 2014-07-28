package com.ebay.SnapShop;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by arbalan on 7/26/14.
 */
public class TabActivity extends Activity {
    public final static String EXTRA_MESSAGE = "com.ebay.SnapShop.MESSAGE";
    public final static String EXTRA_URL = "com.ebay.SnapShop.URL";
        // Declare Tab Variable
        ActionBar.Tab Tab1, Tab2;


        @Override
        public void onCreate(Bundle savedInstanceState) {
            try {
                super.onCreate(savedInstanceState);
                setContentView(R.layout.tab_layout);

                ActionBar actionBar = getActionBar();
                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                StrictMode.setThreadPolicy(policy);
                // Hide Actionbar Icon
                actionBar.setDisplayShowHomeEnabled(false);

                // Hide Actionbar Title
                actionBar.setDisplayShowTitleEnabled(false);

                // Create Actionbar Tabs
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

                // Set Tab Icon and Titles
                Tab1 = actionBar.newTab().setText("SnapBuy");
                Tab2 = actionBar.newTab().setText("SnapSell");

                Intent intent = this.getIntent();
                String message = intent.getStringExtra(EXTRA_MESSAGE);
                String url = intent.getStringExtra(EXTRA_URL);
                Log.i("SnapShop", "##########" + message);

                AssetManager assetManager = getAssets();
                InputStream in = assetManager.open("ebay-listings.html");

                Fragment fragmentTab1 = new FragmentTab1(message);
                Fragment fragmentTab2 = new FragmentTab2(message, url,in);

                // Set Tab Listeners
                Tab1.setTabListener(new TabListener(fragmentTab1));
                Tab2.setTabListener(new TabListener(fragmentTab2));

                // Add tabs to actionbar
                actionBar.addTab(Tab1);
                actionBar.addTab(Tab2);
            }catch (IOException e){}

        }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.menu_home:
                Intent homeIntent = new Intent(this, MainActivity.class);
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
        }
        return (super.onOptionsItemSelected(menuItem));
    }

}
