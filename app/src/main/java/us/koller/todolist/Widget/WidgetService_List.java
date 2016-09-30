package us.koller.todolist.Widget;

import android.content.Intent;
import android.widget.RemoteViewsService;

/**
 * Created by Lukas on 20.08.2016.
 */
public class WidgetService_List extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ViewFactory_List(this.getApplicationContext(), intent));
    }
}
