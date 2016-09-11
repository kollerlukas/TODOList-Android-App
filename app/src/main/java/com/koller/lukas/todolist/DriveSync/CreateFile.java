package com.koller.lukas.todolist.DriveSync;

import android.os.AsyncTask;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.MetadataChangeSet;

/**
 * Created by Lukas on 13.06.2016.
 */
public class CreateFile extends AsyncTask<Void, Void, Boolean> {
    private String filename = "todolist.txt";
    private GoogleApiClient mGoogleApiClient;
    private ResultCallback<DriveFolder.DriveFileResult> fileCallback;
    private DriveApi.DriveContentsResult result;

    public CreateFile(GoogleApiClient mGoogleApiClient, ResultCallback<DriveFolder.DriveFileResult> fileCallback, DriveApi.DriveContentsResult result){
        this.mGoogleApiClient = mGoogleApiClient;
        this.fileCallback = fileCallback;
        this.result = result;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        MetadataChangeSet changeSet = new MetadataChangeSet.Builder()
                .setTitle(filename)
                .setMimeType("text/plain")
                .build();

        Drive.DriveApi.getAppFolder(mGoogleApiClient)
                .createFile(mGoogleApiClient, changeSet, result.getDriveContents())
                .setResultCallback(fileCallback);

        return false;
    }

    @Override
    protected void onPostExecute(Boolean b) {}
}
