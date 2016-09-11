package com.koller.lukas.todolist.DriveSync;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.ExecutionOptions;
import com.google.android.gms.drive.Metadata;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by Lukas on 29.03.2016.
 */
public class EditFileInAppFolder extends AsyncTask<DriveFile, Void, Integer> {

    private GoogleApiClient mGoogleApiClient;

    private String data;

    public EditFileInAppFolder(GoogleApiClient mGoogleApiClient, String data) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.data = data;
    }

    @Override
    protected Integer doInBackground(DriveFile... params) {
        DriveFile file = params[0];
        try {
            DriveApi.DriveContentsResult driveContentsResult
                    = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
            if (!driveContentsResult.getStatus().isSuccess()) {
                return CommonStatusCodes.ERROR;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();
            OutputStream outputStream = driveContents.getOutputStream();
            outputStream.write(data.getBytes());

            com.google.android.gms.common.api.Status status
                    = driveContents.commit(mGoogleApiClient, null).await();

            /*file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            DriveResource.MetadataResult metadataResult
                    = file.getMetadata(mGoogleApiClient).await();

            Metadata metadata = metadataResult.getMetadata()
            Log.d("EditFileInAppFolder", "modifiedDate: "
                    + String.valueOf(metadata.getModifiedDate()));*/

            return status.getStatus().getStatusCode();

        } catch (IOException e) {
            //IOException while appending to the output stream
            e.printStackTrace();
        }
        return CommonStatusCodes.ERROR;
    }

    @Override
    protected void onPostExecute(Integer result) {
    }
}