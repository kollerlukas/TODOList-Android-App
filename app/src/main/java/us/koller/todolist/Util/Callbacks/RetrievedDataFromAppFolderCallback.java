package us.koller.todolist.Util.Callbacks;

/**
 * Created by Lukas on 13.06.2016.
 */
public interface RetrievedDataFromAppFolderCallback {
    void retrievedDataFromAppFolder(String data);
    void error(String error);
}
