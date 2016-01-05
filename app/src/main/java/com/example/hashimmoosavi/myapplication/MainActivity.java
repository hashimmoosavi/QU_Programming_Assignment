package com.example.hashimmoosavi.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Getting the installed apps
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
        String systemAppsDir = "/system/app";
        String userAppsDir = "/data/app";
        String sdCardAppsDir = "/mnt/asec";

        File appsDir = new File(sdCardAppsDir);

        TextView maintext = (TextView) findViewById(R.id.maintext);
        maintext.setText("Following are the apks:\n");

        if(appsDir != null) {
            String files[] = appsDir.list();

            for (int i = 0; i < files.length; i++) {
                File currentAppDir = new File(sdCardAppsDir + "/" + files[i] + "/");

                if (currentAppDir != null) {
                    String[] apks = currentAppDir.list();

                    for (int j = 0; j < apks.length; j++) {

                        // Find the apks

                        if (apks[j].lastIndexOf(".apk") > -1) {
                            maintext.append(apks[j]);
                            maintext.append("\n");
                        }
                    }
                }
            }
        }

    }
}
