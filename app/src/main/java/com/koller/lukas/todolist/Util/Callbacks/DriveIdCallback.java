package com.koller.lukas.todolist.Util.Callbacks;

import com.google.android.gms.drive.DriveId;

/**
 * Created by Lukas on 31.03.2016.
 */
public interface DriveIdCallback {
    public void gotDriveId(DriveId driveId);
    public void error(int statusCode);
    public void noFilesFound();
}
