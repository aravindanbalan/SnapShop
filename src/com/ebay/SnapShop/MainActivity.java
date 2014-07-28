package com.ebay.SnapShop;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;

import org.apache.http.impl.client.DefaultHttpClient;

import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Created by arbalan on 7/26/14.
 */
public class MainActivity extends Activity
{
    private static final int PICK_IMAGE = 1;
    private static final int PICK_Camera_IMAGE = 2;
    public final static String EXTRA_MESSAGE = "com.ebay.SnapShop.MESSAGE";
    public final static String EXTRA_URL = "com.ebay.SnapShop.URL";
    private ImageView imgView;
    private Button upload,cancel;
    private Bitmap bitmap;

    private ProgressDialog dialog;
    String selectedImagePath;
    Uri imageUri;
    String filePathLoc = null;
    MainActivity myActivity ;


    /** Called when the activity is first created. */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        myActivity = this;

        imgView = (ImageView) findViewById(R.id.ImageView);
        upload = (Button) findViewById(R.id.imguploadbtn);
        cancel = (Button) findViewById(R.id.imgcancelbtn);

        upload.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                if (bitmap == null) {
                    Toast.makeText(getApplicationContext(),
                            "Please select image", Toast.LENGTH_SHORT).show();
                } else {
                    dialog = ProgressDialog.show(MainActivity.this, "Searching",
                            "Please wait...", true);
                    new ImageUploadTask(myActivity,imageUri,filePathLoc).execute();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // TODO Auto-generated method stub
                MainActivity.this.finish();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_camera:
                //define the file-name to save photo taken by Camera activity
                String fileName = "new-photo-name.jpg";
                //create parameters for Intent with filename
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, fileName);
                values.put(MediaStore.Images.Media.DESCRIPTION,"Image capture by camera");
                //imageUri is the current activity attribute, define and save it for later usage (also in onSaveInstanceState)
                imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                //create new Intent
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
                startActivityForResult(intent, PICK_Camera_IMAGE);
                return true;

            case R.id.menu_gallery:
                try {
                    Intent gintent = new Intent();
                    gintent.setType("image/*");
                    gintent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(
                            Intent.createChooser(gintent, "Select Picture"),
                            PICK_IMAGE);
                } catch (Exception e) {
                    Toast.makeText(getApplicationContext(),
                            e.getMessage(),
                            Toast.LENGTH_LONG).show();
                    Log.e(e.getClass().getName(), e.getMessage(), e);
                }
                return true;
        }
        return false;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Uri selectedImageUri = null;

        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    selectedImageUri = data.getData();
                }
                break;
            case PICK_Camera_IMAGE:
                if (resultCode == RESULT_OK) {
                    //use imageUri here to access the image
                    selectedImageUri = imageUri;
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Picture was not taken", Toast.LENGTH_SHORT).show();
                }
                break;
        }

        if(selectedImageUri != null){
            try {
                // OI FILE Manager
                String filemanagerstring = selectedImageUri.getPath();

                // MEDIA GALLERY
                selectedImagePath = getPath(selectedImageUri);
                Log.i("SnapShop","*******Image Url : "+ selectedImagePath);
                if (selectedImagePath != null) {
                    filePathLoc = selectedImagePath;
                } else if (filemanagerstring != null) {
                    filePathLoc = filemanagerstring;
                } else {
                    Toast.makeText(getApplicationContext(), "Unknown path",
                            Toast.LENGTH_LONG).show();
                    Log.e("Bitmap", "Unknown path");
                }

                if (filePathLoc != null) {
                    decodeFile(filePathLoc);
                } else {
                    bitmap = null;
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), "Internal error",
                        Toast.LENGTH_LONG).show();
                Log.e(e.getClass().getName(), e.getMessage(), e);
            }
        }

    }

    class ImageUploadTask extends AsyncTask<Void, Void,String> {
    @SuppressWarnings("unused")

    private static final String UPLOAD_URL = "https://api.imgur.com/3/image";
    private Uri mImageUri;
    private Activity myActivity;
    private String keyWord = "";
    private String filePath;
    ImageUploadTask(Activity myActivity,Uri imageUri, String filePath)
    {
        this.mImageUri = imageUri;
        this.filePath = filePath;
        this.myActivity = myActivity;
    }
        public void setKeyWord(String keyWord) {
            this.keyWord = keyWord;
        }
    @Override
    protected String doInBackground(Void... unsued) {


        InputStream imageIn;

        try {

            try {
                imageIn = getContentResolver().openInputStream(mImageUri);

                HttpURLConnection conn = null;
                InputStream responseIn = null;
                conn = (HttpURLConnection) new URL(UPLOAD_URL).openConnection();
                conn.setDoOutput(true);

                addToHttpURLConnection(conn);
                OutputStream out = conn.getOutputStream();
                copy(imageIn, out);
                out.flush();
                out.close();

                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    responseIn = conn.getInputStream();
                    return onInput(responseIn);
                }
                else {
                    Log.i("SnapShop", "responseCode=" + conn.getResponseCode());
                    responseIn = conn.getErrorStream();
                    StringBuilder sb = new StringBuilder();
                    Scanner scanner = new Scanner(responseIn);
                    while (scanner.hasNext()) {
                        sb.append(scanner.next());
                    }
                    Log.i("SnapShop", "error response: " + sb.toString());
                    return null;
                }

            } catch (FileNotFoundException e) {
                Log.e("SnapShop", "could not open InputStream", e);
                return null;
            }


        }catch(Exception e){
            Log.v("log_tag", "Error in http connection "+e.toString());
        }

        return "Success";

    }
        protected String onInput(InputStream in) throws Exception {
            StringBuilder sb = new StringBuilder();
            Scanner scanner = new Scanner(in);
            while (scanner.hasNext()) {
                sb.append(scanner.next());
            }

            JSONObject root = new JSONObject(sb.toString());
            Log.i("SnapShop",root.toString());
            String link = root.getJSONObject("data").getString("link");

            Log.i("SnapShop", "new imgur url: " + link);

            //start an activity and pass this as an intent

            CamFind(link);

            return link;
        }
        private void CamFind(String link) {
            getSearchKeyWords(link);
        }
       /* ******************************** */
        /*
        Cam find api - calls
         */

        public void getSearchKeyWords(String imageUrl) {
            @SuppressWarnings("deprecation")
            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost("https://camfind.p.mashape.com/image_requests");
            httppost.setHeader("X-Mashape-Key",GetProperties.getInstance().getCamFindAPIKey());                                
            String token = "";
            try {
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("focus[y]", "640"));
                nameValuePairs.add(new BasicNameValuePair(
                        "image_request[altitude]", "27.912109375"));
                nameValuePairs.add(new BasicNameValuePair(
                        "image_request[language]", "en"));
                nameValuePairs.add(new BasicNameValuePair(
                        "image_request[latitude]", "35.8714220766008"));
                nameValuePairs.add(new BasicNameValuePair("image_request[locale]",
                        "en_US"));
                nameValuePairs.add(new BasicNameValuePair(
                        "image_request[longitude]", "14.3583203002251"));
                nameValuePairs.add(new BasicNameValuePair(
                        "image_request[remote_image_url]", imageUrl));

                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                org.apache.http.HttpResponse response = httpclient
                        .execute(httppost);
                HttpEntity entity2 = response.getEntity();
                // do something useful with the response body
                // and ensure it is fully consumed
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        entity2.getContent()), 65728);
                String line = null;
                StringBuilder sb = new StringBuilder();
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
                JSONObject root = new JSONObject(sb.toString());
                token = root.getString("token");
                //System.out.println(token);

            } catch (Exception e) {
                //System.out.println(e.getMessage() + ":");
                e.printStackTrace();
            }

            String url = "https://camfind.p.mashape.com/image_responses/" + token;
            //System.out.println(url);
            String status = "not completed";
            long startTime = System.currentTimeMillis();
            long midTime = System.currentTimeMillis();
            String name = "not set";
            int count = 0;
            do {
                try {
                    if (System.currentTimeMillis() - midTime > 5000) {
                        count+=1;

                        //System.out.println(url);
                        HttpClient client = new DefaultHttpClient();
                        HttpGet request = new HttpGet(url);
                        request.setHeader("X-Mashape-Key",GetProperties.getInstance().getCamFindAPIKey()); 
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
                            JSONObject root = new JSONObject(sb.toString());
                            status = root.getString("status");
                            //System.out.println(status);
                            if (root.has("name"))
                                name = root.getString("name");

                            midTime = System.currentTimeMillis();
                        }

                    }
                } catch (Exception e) {
                    //System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            } while (status.equals("not completed")&&count<=10);

            long endTime = System.currentTimeMillis();
            //System.out.println("Time taken: " + (endTime - startTime)
               //     + ". Number of times called " + count);
            //System.out.println(name);
            setKeyWord(name);
            Intent intent = new Intent(myActivity, TabActivity.class);
            intent.putExtra(EXTRA_MESSAGE, keyWord);
            intent.putExtra(EXTRA_URL, imageUrl);
            startActivity(intent);

        }
        /* ******************************** */

        private int copy(InputStream input, OutputStream output) throws IOException {
            byte[] buffer = new byte[8192];
            int count = 0;
            int n = 0;
            while (-1 != (n = input.read(buffer))) {
                output.write(buffer, 0, n);
                count += n;
            }
            return count;
        }

        public void addToHttpURLConnection(HttpURLConnection conn) {

            String clientID = "48e960fea53ed6b";
            conn.setRequestProperty("Authorization", "Client-ID " + clientID);
        }
    private String getStringContent(InputStream ips,HttpResponse response) throws Exception
    {
        BufferedReader buf = new BufferedReader(new InputStreamReader(ips,"UTF-8"));
        if(response.getStatusLine().getStatusCode()!= HttpStatus.SC_OK)
        {
            throw new Exception(response.getStatusLine().getReasonPhrase());
        }
        StringBuilder sb = new StringBuilder();
        String s;
        while(true )
        {
            s = buf.readLine();
            if(s==null || s.length()==0)
                break;
            sb.append(s);

        }
        buf.close();
        ips.close();
        Log.i("SnapShop","************ Response 1 :"+sb.toString());
        return sb.toString();
    }

    @Override
    protected void onProgressUpdate(Void... unsued) {

    }

    @Override
    protected void onPostExecute(String sResponse) {
        try {
            if (dialog.isShowing())
                dialog.dismiss();
        } catch (Exception e) {
            Toast.makeText(getApplicationContext(),
                    e.getMessage(),
                    Toast.LENGTH_LONG).show();
            Log.e(e.getClass().getName(), e.getMessage(), e);
        }
    }

}

    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        bitmap = BitmapFactory.decodeFile(filePath, o2);

        imgView.setImageBitmap(bitmap);

    }

}