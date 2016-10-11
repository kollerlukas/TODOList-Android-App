package us.koller.todolist.RecyclerViewAdapters;

import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import us.koller.todolist.R;

/**
 * Created by Lukas on 27.09.2016.
 */

public class Info_RVAdapter extends RecyclerView.Adapter {

    public static class InfoViewHolder extends RecyclerView.ViewHolder {

        private View v;

        InfoViewHolder(View v) {
            super(v);
            this.v = v;
        }

        void initItem(String text, String text_small, Drawable drawable){
            TextView textView = (TextView) v.findViewById(R.id.textView);
            textView.setText(text);
            if(text_small.equals("")){
                v.findViewById(R.id.textView_small).setVisibility(View.GONE);

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
                layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
                textView.setLayoutParams(layoutParams);
            } else {
                ((TextView) v.findViewById(R.id.textView_small)).setText(text_small);
            }
            ((ImageView) v.findViewById(R.id.imageView)).setImageDrawable(drawable);
        }
    }

    private String[] itemsText;
    private String[] itemsText_small;
    private Drawable[] drawables;

    public Info_RVAdapter(String[] itemsText, String[] itemsText_small, Drawable[] drawables) {
        this.itemsText = itemsText;
        this.itemsText_small = itemsText_small;
        this.drawables = drawables;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.one_item_info, parent, false);
        return new InfoViewHolder(v);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((InfoViewHolder) holder).initItem(itemsText[position], itemsText_small[position], drawables[position]);
    }

    @Override
    public int getItemCount() {
        return 7;
    }
}
