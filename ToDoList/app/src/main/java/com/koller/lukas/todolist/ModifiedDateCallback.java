package com.koller.lukas.todolist;

import com.google.android.gms.drive.DriveId;

/**
 * Created by Lukas on 18.06.2016.
 */
public interface ModifiedDateCallback {
    public void getModifiedDate(long timeStamp, DriveId driveId);
}
