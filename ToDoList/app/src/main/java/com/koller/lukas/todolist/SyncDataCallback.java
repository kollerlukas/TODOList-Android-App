package com.koller.lukas.todolist;

/**
 * Created by Lukas on 10.06.2016.
 */
public interface SyncDataCallback {
    public void DoneSyncingData();
    public void addEvent(EVENT e, int position);
    public void removeEvent(int adapter_index);
    public void updateEvent(EVENT e, EVENT newEvent);
    public void updateColors(int [] newColors, int [] newTextColors);
    public void moveEvent(EVENT e, int toPosition);
}
