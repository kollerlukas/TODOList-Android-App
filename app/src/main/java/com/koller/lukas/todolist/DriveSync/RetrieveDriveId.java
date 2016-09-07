package com.koller.lukas.todolist.DriveSync;

import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;
import com.koller.lukas.todolist.Util.Callbacks.DriveIdCallback;
import com.koller.lukas.todolist.Util.Callbacks.ModifiedDateCallback;

import java.util.concurrent.Semaphore;

/**
 * Created by Lukas on 30.03.2016.
 */
public class RetrieveDriveId extends AsyncTask<Void, Void, DriveId> {
    private GoogleApiClient mGoogleApiClient;
    private DriveIdCallback driveIdCallback;
    int statusCode;
    private DriveId driveId;
    private Semaphore s;
    private ModifiedDateCallback modifiedDateCallback;

    public RetrieveDriveId(GoogleApiClient mGoogleApiClient, DriveIdCallback driveIdCallback, ModifiedDateCallback modifiedDateCallback) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.driveIdCallback = driveIdCallback;
        this.modifiedDateCallback = modifiedDateCallback;
        s = new Semaphore(0);
    }

    @Override
    protected DriveId doInBackground(Void... params) {
        SortOrder sortOrder = new SortOrder.Builder()
                .addSortDescending(SortableField.MODIFIED_DATE)
                .build();

        Query query = new Query.Builder()
                .addFilter(Filters.eq(SearchableField.TITLE, "todolist.txt"))
                .addFilter(Filters.eq(SearchableField.MIME_TYPE, "text/plain"))
                .setSortOrder(sortOrder)
                .build();

        Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                statusCode = result.getStatus().getStatusCode();

                Log.d("RetrieveDriveId", String.valueOf(result.getMetadataBuffer().getCount()));

                if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0) {
                    if (result.getMetadataBuffer().get(0).getDriveId() != null) {
                        driveId = result.getMetadataBuffer().get(0).getDriveId();
                        if(modifiedDateCallback != null){
                            modifiedDateCallback.getModifiedDate(result.getMetadataBuffer().get(0).getModifiedDate().getTime(),
                                    result.getMetadataBuffer().get(0).getDriveId());
                        }
                    }

                    //Delete old files
                    if (result.getMetadataBuffer().getCount() < 1) {
                        for (int i = 1; i < result.getMetadataBuffer().getCount(); i++) {
                            DriveFile file = result.getMetadataBuffer().get(i).getDriveId().asDriveFile();
                            com.google.android.gms.common.api.Status deleteStatus = file.delete(mGoogleApiClient).await();
                        }
                    }
                }
                result.getMetadataBuffer().release();
                s.release();
            }
        });

        try {
            s.acquire();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return driveId;
    }

    @Override
    protected void onPostExecute(DriveId driveId) {
        if(driveIdCallback != null){
            if (driveId != null) {
                driveIdCallback.gotDriveId(driveId);
                return;
            }
            driveIdCallback.error(statusCode);
        }
    }
}
