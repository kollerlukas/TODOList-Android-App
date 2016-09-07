package com.koller.lukas.todolist.RecyclerViewAdapters;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.koller.lukas.todolist.Activities.ThemeActivity;
import com.koller.lukas.todolist.R;

/**
 * Created by Lukas on 14.06.2016.
 */
public class Theme_RVAdapter extends RecyclerView.Adapter {

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView textview;
        private String s;

        EventViewHolder(View v) {
            super(v);
            card = (CardView) v.findViewById(R.id.color_event);
            textview = (TextView) v.findViewById(R.id.color_event_name);
        }

        public void colorCard(int color, int textcolor) {
            card.setCardBackgroundColor(color);
            textview.setTextColor(textcolor);
            //textview.setTextColor(Color.argb(0, 255, 255, 255));

            Spannable spanText = Spannable.Factory.getInstance().newSpannable(s);
            //spanText.setSpan(new BackgroundColorSpan(textcolor), 0, s.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            textview.setText(spanText);
        }

        public void setColorIndex(int color_index){
            s = "Color" + color_index;
            textview.setText(s);
        }
    }

    private int [] colors;
    private ThemeActivity themeActivity;

    public Theme_RVAdapter(int [] colors, ThemeActivity themeActivity){
        this.colors = colors;
        this.themeActivity = themeActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_event_theme, parent, false);
        return new EventViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((EventViewHolder) holder).setColorIndex(colors[position]);
        ((EventViewHolder) holder).colorCard(themeActivity.getEventColor(colors[position]), themeActivity.getEventTextColor(colors[position]));
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public void itemChanged(int index) {
        notifyItemChanged(index);
    }
}
