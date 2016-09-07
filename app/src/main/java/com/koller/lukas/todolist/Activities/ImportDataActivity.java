package com.koller.lukas.todolist.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Lukas on 15.08.2016.
 */
public class ImportDataActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Uri data = getIntent().getData();

        ContentResolver cR = ImportDataActivity.this.getContentResolver();
        //Toast.makeText(ImportDataActivity.this, String.valueOf(cR.getType(data)), Toast.LENGTH_SHORT).show();

        String eventsToImport = "";
        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction("Import");
        if (data != null) {
            getIntent().setData(null);
            try {
                eventsToImport = importData(data);
            } catch (Exception e) {
                this.finish();
                return;
            }
        } else {
            this.finish();
            return;
        }

        if(cR.getType(data) == null){
            Toast.makeText(this, "null Data", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cR.getType(data).equals("application/octet-stream")) {
            //intent.putExtra("events", eventsToImport);
            this.finish();
            return;
        } else if (cR.getType(data).equals("text/plain")) {
            intent.putExtra("events", eventsToImport);
        }
        startActivity(intent);
        this.finish();
    }

    private String importData(Uri data) {
        final String scheme = data.getScheme();
        if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            try {
                ContentResolver cr = this.getContentResolver();
                InputStream is = cr.openInputStream(data);
                if (is == null) {
                    return "";
                }
                StringBuffer buf = new StringBuffer();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str;
                if (is != null) {
                    while ((str = reader.readLine()) != null) {
                        buf.append(str + "\n");
                    }
                }
                is.close();
                return buf.toString();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
