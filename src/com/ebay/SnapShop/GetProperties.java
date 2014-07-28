package com.ebay.SnapShop;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import android.util.Log;

public class GetProperties {
	
	private static GetProperties g_properties = new GetProperties();
	
	public static GetProperties getInstance()
	{
		return g_properties;
	}
	
	public String getCamFindAPIKey() {
		 
        Properties prop = new Properties();
        String propFileName = "config.properties";
        
        try
        {
	        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(propFileName);
	        prop.load(inputStream);
	        if (inputStream == null) {
	            Log.i("GetProperties","There is an error while fetching the input stream");
	        }
        }
        catch(Exception e)
        {
        	Log.i("GetProperties","There is an error while trying to load the input stream "+e.getMessage());
        }
        String key= prop.getProperty("CamFindAPIKey");
        Log.i("GetProperties","The key is "+key);
        return key;
    }

}
