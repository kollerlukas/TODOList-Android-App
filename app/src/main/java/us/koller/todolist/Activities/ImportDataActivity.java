package us.koller.todolist.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import us.koller.todolist.R;

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

        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

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
            Toast.makeText(this, "Sorry! This file can't be imported." + '\n'
                    + " Please only import files shared through the app.", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        }

        if (cR.getType(data).equals("application/octet-stream")) {
            //intent.putExtra("events", eventsToImport);
            Toast.makeText(this, "Sorry! This file can't be imported." + '\n'
                    + " Please only import files shared through the app.", Toast.LENGTH_SHORT).show();
            //Toast.makeText(this, "contentTypeError", Toast.LENGTH_SHORT).show();
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
