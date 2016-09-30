package us.koller.todolist.DriveSync;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveContents;
import com.google.android.gms.drive.DriveFile;

import android.os.AsyncTask;
import android.os.ParcelFileDescriptor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 * Created by Lukas on 29.03.2016.
 */
public class EditFileInAppFolder extends AsyncTask<DriveFile, Void, Integer> {

    private GoogleApiClient mGoogleApiClient;

    private String data;

    public EditFileInAppFolder(GoogleApiClient mGoogleApiClient, String data) {
        this.mGoogleApiClient = mGoogleApiClient;
        this.data = data;

        //Log.d("EditFileInAppFolder", "data to write: " + data);
    }

    @Override
    protected Integer doInBackground(DriveFile... params) {
        DriveFile file = params[0];
        try {
            DriveApi.DriveContentsResult driveContentsResult
                   // = file.open(mGoogleApiClient, DriveFile.MODE_WRITE_ONLY, null).await();
            = file.open(mGoogleApiClient, DriveFile.MODE_READ_WRITE, null).await();

            if (!driveContentsResult.getStatus().isSuccess()) {
                return CommonStatusCodes.ERROR;
            }

            DriveContents driveContents = driveContentsResult.getDriveContents();
            //OutputStream outputStream = driveContents.getOutputStream();
            //outputStream.write(data.getBytes());

            //com.google.android.gms.common.api.Status status = driveContents.commit(mGoogleApiClient, null).await();

            //return status.getStatus().getStatusCode();


            ParcelFileDescriptor parcelFileDescriptor = driveContents.getParcelFileDescriptor();

            // write to the file.
            FileOutputStream fileOutputStream = new FileOutputStream(parcelFileDescriptor
                    .getFileDescriptor());
            Writer writer = new OutputStreamWriter(fileOutputStream);
            writer.write(data);
            writer.flush();
            writer.close();

            // Read to the end of the file.
            /*FileInputStream fileInputStream = new FileInputStream(parcelFileDescriptor
                    .getFileDescriptor());

            StringBuilder fileContent = new StringBuilder();
            fileInputStream.read(new byte[fileInputStream.available()]);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream) ;
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader) ;

            String readString = bufferedReader.readLine();
            while (readString != null) {
                fileContent.append(readString);
                readString = bufferedReader.readLine();
            }
            inputStreamReader.close();

            Log.d("EditFileInAppFolder", "written data: " + fileContent.toString());*/

            com.google.android.gms.common.api.Status status
                    = driveContents.commit(mGoogleApiClient, null).await();
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