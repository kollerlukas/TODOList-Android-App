package us.koller.todolist.Util.Callbacks;

import com.google.android.gms.drive.DriveId;

/**
 * Created by Lukas on 31.03.2016.
 */
public interface DriveIdCallback {
    void gotDriveId(DriveId driveId);
    void error(int statusCode);
    void noFilesFound();
}
