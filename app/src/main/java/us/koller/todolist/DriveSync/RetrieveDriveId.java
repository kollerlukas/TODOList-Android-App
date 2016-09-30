package us.koller.todolist.DriveSync;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveResource;
import com.google.android.gms.drive.Metadata;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;
import com.google.android.gms.drive.query.SortOrder;
import com.google.android.gms.drive.query.SortableField;

import android.os.AsyncTask;

import java.util.concurrent.Semaphore;

import us.koller.todolist.Util.Callbacks.DriveIdCallback;
import us.koller.todolist.Util.Callbacks.ModifiedDateCallback;

/**
 * Created by Lukas on 30.03.2016.
 */
public class RetrieveDriveId extends AsyncTask<Void, Void, DriveId> {
    private GoogleApiClient mGoogleApiClient;

    private DriveIdCallback driveIdCallback;
    private ModifiedDateCallback modifiedDateCallback;

    private int statusCode;

    private DriveId driveId;

    private long modifiedTime = 0;

    private Semaphore s;

    private boolean noFilesFound = false;

    public RetrieveDriveId(GoogleApiClient mGoogleApiClient, DriveIdCallback driveIdCallback) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.driveIdCallback = driveIdCallback;
        this.modifiedDateCallback = null;
        s = new Semaphore(0);
    }

    public RetrieveDriveId(GoogleApiClient mGoogleApiClient, ModifiedDateCallback modifiedDateCallback) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.driveIdCallback = null;
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

        Drive.DriveApi.getAppFolder(mGoogleApiClient).queryChildren(mGoogleApiClient, query)
                .setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
            @Override
            public void onResult(DriveApi.MetadataBufferResult result) {
                statusCode = result.getStatus().getStatusCode();

                if(!result.getStatus().isSuccess()){
                    //Log.d("RetrieveDriveId", "result not successful");
                    s.release();
                    return;
                }

                if (result.getStatus().isSuccess() && result.getMetadataBuffer().getCount() > 0) {
                    if (result.getMetadataBuffer().get(0).getDriveId() != null) {
                        driveId = result.getMetadataBuffer().get(0).getDriveId();
                    }
                } else if(result.getMetadataBuffer().getCount() == 0){
                    //Log.d("RetrieveDriveId", "no items found");
                    noFilesFound = true;
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

        if (driveId == null || noFilesFound) {
            return null;
        }

        DriveFile file = driveId.asDriveFile();

        if (modifiedDateCallback != null) {

            DriveApi.DriveContentsResult driveContentsResult
                    = file.open(mGoogleApiClient, DriveFile.MODE_READ_ONLY, null).await();
            DriveResource.MetadataResult metadataResult
                    = file.getMetadata(mGoogleApiClient).await();

            Metadata metadata = metadataResult.getMetadata();

            modifiedTime = metadata.getModifiedDate().getTime();

            driveContentsResult.getDriveContents().discard(mGoogleApiClient);
        }

        return driveId;
    }

    @Override
    protected void onPostExecute(DriveId driveId) {
        if(noFilesFound){
            if (driveIdCallback != null) {
                driveIdCallback.noFilesFound();
            } else if (modifiedDateCallback != null) {
                modifiedDateCallback.noFilesFound();
            }
        } else {
            if (driveIdCallback != null) {
                if (driveId != null && statusCode == CommonStatusCodes.SUCCESS) {
                    driveIdCallback.gotDriveId(driveId);
                } else {
                    driveIdCallback.error(statusCode);
                }
            } else if (modifiedDateCallback != null) {
                if (driveId != null && statusCode == CommonStatusCodes.SUCCESS) {
                    modifiedDateCallback.getModifiedDate(modifiedTime, driveId);
                } else {
                    modifiedDateCallback.error(statusCode);
                }
            }
        }
    }
}
