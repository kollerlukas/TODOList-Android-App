package us.koller.todolist.RecyclerViewAdapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import us.koller.todolist.R;

import us.koller.todolist.Util.ThemeHelper;

/**
 * Created by Lukas on 14.06.2016.
 */
public class ThemeRVAdapter extends RecyclerView.Adapter {

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        View v;

        ColorViewHolder(View v) {
            super(v);
            this.v = v;
        }

        public void colorCard(int colorIndex, int color, int textcolor) {
            ((CardView) v.findViewById(R.id.color_event)).setCardBackgroundColor(color);
            ((TextView) v.findViewById(R.id.color_event_name)).setText("Color" + colorIndex);
            ((TextView) v.findViewById(R.id.color_event_name)).setTextColor(textcolor);
        }
    }

    private ThemeHelper helper;

    public ThemeRVAdapter(ThemeHelper helper){
        this.helper = helper;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_event_theme, parent, false);
        return new ColorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ColorViewHolder) holder).colorCard(position +1, helper.getEventColor(position +1),
                helper.getEventTextColor(position +1));
    }

    @Override
    public int getItemCount() {
        return 12;
    }

    public void itemChanged(int index) {
        notifyItemChanged(index);
    }
}
