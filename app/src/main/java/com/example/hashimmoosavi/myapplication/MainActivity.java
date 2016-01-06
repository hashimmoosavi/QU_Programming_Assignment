package com.example.hashimmoosavi.myapplication;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    int serverResponseCode = 0;
    TextView maintext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        maintext = (TextView) findViewById(R.id.maintext);
        maintext.setText("");

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
    private void getFiles() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);


        // Limiting to user-installed apps

        ArrayList<ApplicationInfo> installedApps = new ArrayList<ApplicationInfo>();

        for (ApplicationInfo app : apps) {
            //checks for flags; if flagged, check if updated system app
            if ((app.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 1) {
                installedApps.add(app);
                //it's a system app, not interested
            } else if ((app.flags & ApplicationInfo.FLAG_SYSTEM) == 1) {
                //Discard this one
                //in this case, it should be a user-installed app
            } else {
                installedApps.add(app);
            }
        }

        String currappname = "";

        for(int i = 0; i < installedApps.size(); i++)
        {
            maintext.append(installedApps.get(i).loadLabel(pm) + " , ");
            currappname += "" + installedApps.get(i).packageName + "\n";
        }

        final String appnames = currappname;

        // Sending app names as one Post request

        Thread thread = new Thread() {
            @Override
            public void run() {
                uploadText(appnames);
            }
        };

        thread.start();
    }

    public void uploadText(String sourceFileUri) {

        try {

            String upLoadServerUri = "https://cse-os.qu.edu.qa/challenge";

            HttpClient httpclient = new DefaultHttpClient();
            HttpPost httppost = new HttpPost(upLoadServerUri);

            httppost.setEntity(new StringEntity(sourceFileUri));
            HttpResponse resp = httpclient.execute(httppost);
            HttpEntity ent = resp.getEntity();

        } catch (Exception e) {

            System.out.println("Error");
        }
    }
}