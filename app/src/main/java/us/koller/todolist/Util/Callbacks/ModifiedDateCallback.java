package us.koller.todolist.Util.Callbacks;

import com.google.android.gms.drive.DriveId;

/**
 * Created by Lukas on 18.06.2016.
 */
public interface ModifiedDateCallback {
    void getModifiedDate(long timeStamp, DriveId driveId);
    void error(int statusCode);
    void noFilesFound();
}
