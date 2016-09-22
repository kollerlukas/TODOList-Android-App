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

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lukas on 14.06.2016.
 */
public class Theme_RVAdapter extends RecyclerView.Adapter {

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        private CardView card;
        private TextView textview;
        private String s;

        ColorViewHolder(View v) {
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

    private ThemeActivity themeActivity;

    public Theme_RVAdapter(ThemeActivity themeActivity){
        this.themeActivity = themeActivity;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_event_theme, parent, false);
        return new ColorViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ColorViewHolder) holder).setColorIndex(position +1);
        ((ColorViewHolder) holder).colorCard(themeActivity.getEventColor(position +1),
                themeActivity.getEventTextColor(position +1));
    }

    @Override
    public int getItemCount() {
        return 12;/*colors.length;*/
    }

    public void itemChanged(int index) {
        notifyItemChanged(index);
    }

    public boolean onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }
}
