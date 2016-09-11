package com.koller.lukas.todolist.DriveSync;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.koller.lukas.todolist.Util.Callbacks.RetrievedDataFromAppFolderCallback;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Lukas on 29.03.2016.
 */
public class RetrieveDataFromAppFolder extends AsyncTask<DriveId, Boolean, String> {

    private GoogleApiClient mGoogleApiClient;
    private RetrievedDataFromAppFolderCallback retrievedDataFromAppFolderCallback;

    private String error = "none";

    public RetrieveDataFromAppFolder(GoogleApiClient mGoogleApiClient,
                                     RetrievedDataFromAppFolderCallback retrievedDataFromAppFolderCallback) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.retrievedDataFromAppFolderCallback = retrievedDataFromAppFolderCallback;
    }

    @Override
    protected String doInBackground(DriveId... params) {
        String contents = null;

        if(params[0] == null){
            error = "driveId Error";
        }

        DriveFile file = params[0].asDriveFile();
        DriveApi.DriveContentsResult driveContentsResult
                = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
        if (!driveContentsResult.getStatus().isSuccess()) {
            error = "file.open() not successful";
            return null;
        }
        DriveContents driveContents = driveContentsResult.getDriveContents();
        BufferedReader reader
                = new BufferedReader(new InputStreamReader(driveContents.getInputStream()));
        StringBuilder builder
                = new StringBuilder();
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            contents = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();

            error = "no data written";
        }

        driveContents.discard(mGoogleApiClient);

        //Delete file
        com.google.android.gms.common.api.Status deleteStatus = file.delete(mGoogleApiClient).await();
        Log.d("RetrieveDataFromApp...", "deleteFile status: " + CommonStatusCodes.getStatusCodeString(deleteStatus.getStatusCode()));

        return contents;
    }

    @Override
    protected void onPostExecute(String result) {
        if (result == null) {
            retrievedDataFromAppFolderCallback.error(error);
            return;
        } else if(error.equals("")){
            Log.d("RetrieveDataFromApp...", "content is empty");
        }
        retrievedDataFromAppFolderCallback.retrievedDataFromAppFolder(result);
    }
}
