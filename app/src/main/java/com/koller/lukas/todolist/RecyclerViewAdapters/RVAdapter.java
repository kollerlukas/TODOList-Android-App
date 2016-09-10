package com.koller.lukas.todolist.RecyclerViewAdapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.koller.lukas.todolist.R;
import com.koller.lukas.todolist.Todolist.Event;
import com.koller.lukas.todolist.Util.Callbacks.CardButtonOnClickInterface;
import com.koller.lukas.todolist.Util.ClickHelper.CardButtonOnClickHelper;
import com.koller.lukas.todolist.Util.ThemeHelper;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lukas on 23.08.2015.
 */
public class RVAdapter extends RecyclerView.Adapter<RVAdapter.EventViewHolder> {

    public static class EventViewHolder extends RecyclerView.ViewHolder implements OnClickListener {
        public CardView card;
        private CardView card_action_view;
        private RelativeLayout relative_layout;
        private TextView textview;
        private Button color_button;
        private Button edit_button;
        private ImageView edit_imageView;
        private Button alarm_button;
        private ImageView alarm_imageView;
        public boolean is_expanding_or_collapsing = false;
        public boolean is_animation_running = false;
        private Context context;
        public Event event;
        private CardButtonOnClickInterface mCardButtonOnClickInterface;
        public boolean intro_card = false;

        EventViewHolder(CardButtonOnClickInterface mCardButtonOnClickInterface, Context context, View v) {
            super(v);
            card = (CardView) v.findViewById(R.id.card);
            card_action_view = (CardView) v.findViewById(R.id.card_action_buttons);
            relative_layout = (RelativeLayout) v.findViewById(R.id.relative_layout);
            textview = (TextView) v.findViewById(R.id.event_name);
            color_button = (Button) v.findViewById(R.id.color_button);
            Drawable d_color = ResourcesCompat.getDrawable(context.getResources(), R.drawable.color_button_background, null).getConstantState().newDrawable().mutate();
            color_button.setBackground(d_color);
            edit_button = (Button) v.findViewById(R.id.edit_button);
            Drawable d_edit = ResourcesCompat.getDrawable(context.getResources(), R.drawable.edit_button_background, null).getConstantState().newDrawable().mutate();
            edit_button.setBackground(d_edit);
            edit_imageView = (ImageView) v.findViewById(R.id.edit_imageView);
            alarm_button = (Button) v.findViewById(R.id.alarm_button);
            Drawable d_alarm = ResourcesCompat.getDrawable(context.getResources(), R.drawable.alarm_button_background, null).getConstantState().newDrawable().mutate();
            alarm_button.setBackground(d_alarm);
            alarm_imageView = (ImageView) v.findViewById(R.id.alarm_imageView);
            this.context = context;
            this.mCardButtonOnClickInterface = mCardButtonOnClickInterface;

            color_button.setOnClickListener(this);
            edit_button.setOnClickListener(this);
            alarm_button.setOnClickListener(this);

            card_action_view.setVisibility(View.GONE);
        }

        public void colorButtonClicked() {
            is_animation_running = true;
            RotateAnimation r = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            r.setDuration(425);
            color_button.startAnimation(r);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    is_animation_running = false;
                    mCardButtonOnClickInterface.actionButtonClicked(color_button, event);
                }
            }, 425);
        }

        public void cardClicked() {
            if (event.semiTransparent || is_expanding_or_collapsing) {
                return;
            }
            if (event.isExpanded) {
                collapse();
            } else {
                expand();
            }
        }

        public void editButtonClicked() {
            is_animation_running = true;
            final ThemeHelper helper = new ThemeHelper(context);
            edit_button.setBackgroundResource(R.drawable.transparent_ripple);
            edit_imageView.setVisibility(View.VISIBLE);
            edit_imageView.setBackgroundResource(R.drawable.edit_anim);
            AnimationDrawable edit_anim = (AnimationDrawable) edit_imageView.getBackground();
            edit_anim.setColorFilter(helper.getEventTextColor(event.getColor()), PorterDuff.Mode.SRC_IN);
            edit_anim.start();

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.edit_button_background, null).getConstantState().newDrawable().mutate();
                    edit_button.setBackground(d);
                    edit_button.getBackground().setColorFilter(helper.getEventTextColor(event.getColor()), PorterDuff.Mode.SRC_IN);
                    edit_imageView.setVisibility(View.INVISIBLE);
                    is_animation_running = false;
                    mCardButtonOnClickInterface.actionButtonClicked(edit_button, event);
                }
            }, 425);
        }

        public void alarmButtonClicked() {
            is_animation_running = true;
            final ThemeHelper helper = new ThemeHelper(context);
            alarm_button.setBackgroundResource(R.drawable.transparent_ripple);
            alarm_imageView.setVisibility(View.VISIBLE);
            alarm_imageView.setBackgroundResource(R.drawable.alarm_anim);
            AnimationDrawable alarm_anim = (AnimationDrawable) alarm_imageView.getBackground();
            alarm_anim.setColorFilter(helper.getEventTextColor(event.getColor()), PorterDuff.Mode.SRC_IN);
            alarm_anim.start();

            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Drawable d = ResourcesCompat.getDrawable(context.getResources(), R.drawable.alarm_button_background, null).getConstantState().newDrawable().mutate();
                    alarm_button.setBackground(d);
                    alarm_button.getBackground().setColorFilter(helper.getEventTextColor(event.getColor()), PorterDuff.Mode.SRC_IN);
                    alarm_imageView.setVisibility(View.INVISIBLE);
                    is_animation_running = false;
                    mCardButtonOnClickInterface.actionButtonClicked(alarm_button, event);
                }
            }, 425);
        }

        public void collapse_noAnimation() {
            relative_layout.setVisibility(View.INVISIBLE);
            card_action_view.setVisibility(View.GONE);
            event.setExpanded(false);
        }

        private void collapse() {
            is_expanding_or_collapsing = true;
            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator());
            fadeOut.setDuration(150);

            final Animation scale = new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
            scale.setInterpolator(new DecelerateInterpolator());
            scale.setFillAfter(true);
            scale.setDuration(200);

            int finalHeight = card_action_view.getHeight();
            ValueAnimator mAnimator = getValueAnimator(finalHeight, 0);
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {/*relative_layout.setVisibility(View.INVISIBLE);*/}

                @Override
                public void onAnimationEnd(Animator animation) {
                    relative_layout.setVisibility(View.INVISIBLE);
                    card_action_view.setVisibility(View.GONE);
                    is_expanding_or_collapsing = false;
                    event.setExpanded(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {/*nothing*/}

                @Override
                public void onAnimationRepeat(Animator animation) {/*nothing*/}
            });
            //mAnimator.setStartDelay(100);
            mAnimator.setDuration(200);
            relative_layout.startAnimation(fadeOut);
            color_button.startAnimation(scale);
            edit_button.startAnimation(scale);
            alarm_button.startAnimation(scale);
            mAnimator.start();
        }

        private void expand() {
            is_expanding_or_collapsing = true;
            final Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(150);

            final Animation scale = new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0f);
            scale.setInterpolator(new DecelerateInterpolator());
            scale.setFillAfter(true);
            scale.setDuration(200);

            card_action_view.setVisibility(View.VISIBLE);
            relative_layout.setVisibility(View.INVISIBLE);

            final int widthSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            final int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
            card_action_view.measure(widthSpec, heightSpec);

            ValueAnimator mAnimator = getValueAnimator(0, card_action_view.getMeasuredHeight());
            mAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setColorFilterToActionButtons();
                    //relative_layout.setVisibility(View.VISIBLE);
                    //relative_layout.startAnimation(fadeIn);
                    is_expanding_or_collapsing = false;
                    event.setExpanded(true);
                    mCardButtonOnClickInterface.scrollToCard(getAdapterPosition());
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            mAnimator.setDuration(200);
            mAnimator.start();
            relative_layout.setVisibility(View.VISIBLE);
            relative_layout.startAnimation(fadeIn);
            color_button.startAnimation(scale);
            edit_button.startAnimation(scale);
            alarm_button.startAnimation(scale);
        }

        public ValueAnimator getValueAnimator(int start, int end) {
            ValueAnimator animator = ValueAnimator.ofInt(start, end);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    int value = (Integer) valueAnimator.getAnimatedValue();
                    ViewGroup.LayoutParams layoutParams = card_action_view.getLayoutParams();
                    layoutParams.height = value;
                    card_action_view.setLayoutParams(layoutParams);
                }
            });
            return animator;
        }

        public void colorCard(int color_index) {
            ThemeHelper helper = new ThemeHelper(context);
            setColor(helper.getEventColor(color_index), helper.getEventTextColor(color_index));
        }

        public void setCardSemiTransparent(int color_index) {
            ThemeHelper helper = new ThemeHelper(context);
            setColor(helper.getEventColor_semitransparent(color_index), helper.getEventTextColor_semitransparent(color_index));

        }

        private void setColor(int color, int textColor) {
            card.setCardBackgroundColor(color);
            textview.setTextColor(textColor);
            color_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            edit_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            alarm_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        }

        public void setColorFilterToActionButtons() {
            int textColor = new ThemeHelper(context).getEventTextColor(event.getColor());
            color_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            edit_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
            alarm_button.getBackground().setColorFilter(textColor, PorterDuff.Mode.SRC_IN);
        }

        public void setCardText(String text) {
            textview.setText(text);
        }

        public void setEvent(Event event) {
            this.event = event;
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.color_button:
                    colorButtonClicked();
                    break;
                case R.id.edit_button:
                    editButtonClicked();
                    break;
                case R.id.alarm_button:
                    alarmButtonClicked();
                    break;
            }
        }

        public void updateCard() {
            setCardText(event.getWhatToDo());
            colorCard(event.getColor());
            if (!event.semiTransparent) {
                card.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, context.getResources().getDisplayMetrics()));
            } else {
                setCardSemiTransparent(event.getColor());
            }
            if ((event.isExpanded && card_action_view.getVisibility() == View.GONE) || (!event.isExpanded && card_action_view.getVisibility() == View.VISIBLE)) {
                collapse_noAnimation();
            }
        }
    }

    private ArrayList<Event> events;
    private CardButtonOnClickHelper onClickHelper;
    private Context context;

    public RVAdapter(ArrayList<Event> events, CardButtonOnClickHelper onClickHelper, Context context) {
        this.events = events;
        this.onClickHelper = onClickHelper;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.setEvent(events.get(i));
        eventViewHolder.updateCard();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.one_event, viewGroup, false);
        return new EventViewHolder(onClickHelper, context, v);
    }

    public void addItem(int index, Event e) {
        if (index >= events.size()) {
            addItem(e);
            notifyItemInserted(events.indexOf(e));
        } else {
            events.add(index, e);
            notifyItemInserted(index);
        }
    }

    public void addItem(Event e) {
        events.add(e);
        notifyItemInserted(events.indexOf(e));
    }

    public void removeItem(int index) {
        events.remove(index);
        notifyItemRemoved(index);
    }

    public void itemChanged(int index) {
        notifyItemChanged(index);
    }

    public ArrayList<Event> getList() {
        return events;
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    public void itemMoved(int fromPosition, int toPosition) {
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(events, i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(events, i, i - 1);
            }
        }
        notifyItemMoved(fromPosition, toPosition);
    }

    public void allItemsChanged(){
        notifyItemRangeChanged(0, events.size());
    }
}
