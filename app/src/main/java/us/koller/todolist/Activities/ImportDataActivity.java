package us.koller.todolist.Activities;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import us.koller.todolist.R;

import java.io.BufferedReader;
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
        if(cR.getType(data) == null){
            this.finish();
            return;
        }

        String eventsToImport;
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT
                |Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        intent.setAction(MainActivity.IMPORT);
        getIntent().setData(null);
        try {
            eventsToImport = importData(data);
        } catch (Exception e) {
            this.finish();
            return;
        }

        String type = cR.getType(data);

        if(type == null || type.equals("application/octet-stream")){
            Toast.makeText(this, "Sorry! This file can't be imported." + '\n'
                    + " Please only import files shared through the app.", Toast.LENGTH_SHORT).show();
            this.finish();
            return;
        } else if (type.equals("text/plain")) {
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
                StringBuilder buf = new StringBuilder();
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String str;
                while ((str = reader.readLine()) != null) {
                    buf.append(str).append("\n");
                }
                is.close();
                return buf.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return "";
    }
}
