package com.koller.lukas.todolist;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Lukas on 29.03.2016.
 */
public class RetrieveDataFromAppFolder extends AsyncTask<DriveId, Boolean, String> {

    private GoogleApiClient mGoogleApiClient;
    private RetrievedDataFromAppFolderCallback retrievedDataFromAppFolderCallback;

    public RetrieveDataFromAppFolder(GoogleApiClient mGoogleApiClient, RetrievedDataFromAppFolderCallback retrievedDataFromAppFolderCallback) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.retrievedDataFromAppFolderCallback = retrievedDataFromAppFolderCallback;
    }

    @Override
    protected String doInBackground(DriveId... params) {
        String contents = null;
        if(params[0] == null){
            return "Error";
        }
        DriveFile file = params[0].asDriveFile();
        DriveApi.DriveContentsResult driveContentsResult = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        BufferedReader reader = new BufferedReader(new InputStreamReader(driveContents.getInputStream()));
        StringBuilder builder = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            contents = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }

        driveContents.discard(mGoogleApiClient);
        return contents;
    }

    @Override
    protected void onPostExecute(String result) {
        retrievedDataFromAppFolderCallback.retrievedDataFromAppFolder(result);
    }
}
