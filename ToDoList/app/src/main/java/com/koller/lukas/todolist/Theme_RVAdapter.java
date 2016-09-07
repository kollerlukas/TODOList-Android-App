package com.koller.lukas.todolist;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by Lukas on 14.06.2016.
 */
public class Theme_RVAdapter extends RecyclerView.Adapter {

    public static class EventViewHolder extends RecyclerView.ViewHolder {
        public CardView card;
        public TextView textview;

        EventViewHolder(View v) {
            super(v);
            card = (CardView) v.findViewById(R.id.color_event);
            textview = (TextView) v.findViewById(R.id.color_event_name);
        }

        public void colorCard(int color, int textcolor) {
            card.setCardBackgroundColor(color);
            textview.setTextColor(textcolor);
        }

        public void setColorIndex(int color_index){
            textview.setText("Color "+ color_index);
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
