package com.koller.lukas.todolist.RecyclerViewAdapters;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
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
        private CardView card;
        private RelativeLayout reveal_bg;
        private CardView card_action_view;
        private RelativeLayout relative_layout;
        private TextView textview;

        private ImageView color_button;
        private AnimatedVectorDrawableCompat color_anim;

        private ImageView edit_button;
        private AnimatedVectorDrawableCompat edit_anim;


        private ImageView alarm_button;
        private AnimatedVectorDrawableCompat alarm_anim;

        private boolean isExpandingOrCollapsing = false;
        private boolean isAnimationRunning = false;

        public Event event;

        private CardButtonOnClickInterface cardButtonOnClickInterface;

        EventViewHolder(CardButtonOnClickInterface cardButtonOnClickInterface, Context context, View v) {
            super(v);

            this.cardButtonOnClickInterface = cardButtonOnClickInterface;

            card = (CardView) v.findViewById(R.id.card);
            reveal_bg = (RelativeLayout) v.findViewById(R.id.rl_card);
            card_action_view = (CardView) v.findViewById(R.id.card_action_buttons);
            relative_layout = (RelativeLayout) v.findViewById(R.id.relative_layout);
            textview = (TextView) v.findViewById(R.id.event_name);

            color_button = (ImageView) v.findViewById(R.id.color_button);
            color_anim = AnimatedVectorDrawableCompat.create(context,
                    R.drawable.ic_color_animatable);
            color_button.setBackground(null);
            color_button.setImageDrawable(color_anim);

            edit_button = (ImageView) v.findViewById(R.id.edit_button);
            edit_anim = AnimatedVectorDrawableCompat.create(context,
                    R.drawable.ic_edit_animatable);
            edit_button.setBackground(null);
            edit_button.setImageDrawable(edit_anim);

            alarm_button = (ImageView) v.findViewById(R.id.alarm_button);
            alarm_anim = AnimatedVectorDrawableCompat.create(context,
                    R.drawable.ic_alarm_animatable);
            alarm_button.setBackground(null);
            alarm_button.setImageDrawable(alarm_anim);

            color_button.setOnClickListener(this);
            edit_button.setOnClickListener(this);
            alarm_button.setOnClickListener(this);

            card_action_view.setVisibility(View.GONE);
        }

        public void initCard(Context context) {
            ThemeHelper helper = new ThemeHelper(context, event.getColor());
            textview.setText(event.getWhatToDo());
            setColor(helper.getEventColor(event.getColor()),
                    helper.getEventTextColor(event.getColor()));
            if (!event.semiTransparent) {
                card.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                        2, context.getResources().getDisplayMetrics()));
            } else {
                setColor(helper.getEventColor_semitransparent(event.getColor()),
                        helper.getEventTextColor_semitransparent(event.getColor()));
            }
            if ((event.isExpanded && card_action_view.getVisibility() == View.GONE)
                    || (!event.isExpanded && card_action_view.getVisibility() == View.VISIBLE)) {
                relative_layout.setVisibility(View.INVISIBLE);
                card_action_view.setVisibility(View.GONE);
                event.setExpanded(false);
            }
        }

        private void setColor(int color, int textColor) {
            card.setCardBackgroundColor(color);
            textview.setTextColor(textColor);
            color_anim.setTint(textColor);
            edit_anim.setTint(textColor);
            alarm_anim.setTint(textColor);
        }

        public void cardClicked() {
            if (event.semiTransparent || isExpandingOrCollapsing) {
                return;
            }
            if (event.isExpanded) {
                collapse();
            } else {
                expand();
            }
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

        private void colorButtonClicked() {
            isAnimationRunning = true;

            color_anim.start();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isAnimationRunning = false;
                    cardButtonOnClickInterface.actionButtonClicked(color_button, event);
                }
            }, 350);
        }

        private void editButtonClicked() {
            isAnimationRunning = true;

            edit_anim.start();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isAnimationRunning = false;
                    cardButtonOnClickInterface.actionButtonClicked(edit_button, event);
                }
            }, 550);
        }

        private void alarmButtonClicked() {
            isAnimationRunning = true;
            alarm_anim.start();
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    isAnimationRunning = false;
                    cardButtonOnClickInterface.actionButtonClicked(alarm_button, event);
                }
            }, 350);
        }

        public void changeCardColorAnim(final Context context, final int color, final int textColor){
            final Drawable reveal_bg_d = ContextCompat.getDrawable(context, R.drawable.card_reveal_bg);
            reveal_bg_d.setColorFilter(color, PorterDuff.Mode.SRC_IN);

            Animator animator = ViewAnimationUtils.createCircularReveal(reveal_bg,
                    color_button.getWidth()/2 + color_button.getLeft() + relative_layout.getLeft() + card_action_view.getLeft(),
                    color_button.getHeight()/2 + color_button.getTop() + relative_layout.getTop() + card_action_view.getTop()
                    , 0, reveal_bg.getWidth());
            animator.setDuration(1000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    reveal_bg.setBackground(reveal_bg_d);
                    textview.setTextColor(textColor);
                    color_anim.setTint(textColor);
                    edit_anim.setTint(textColor);
                    alarm_anim.setTint(textColor);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    card.setCardBackgroundColor(color);
                    reveal_bg.setBackground(null);
                }

                @Override
                public void onAnimationCancel(Animator animation) {/*nothing*/}

                @Override
                public void onAnimationRepeat(Animator animation) {/*nothing*/}
            });
            animator.start();
        }

        private void collapse() {
            isExpandingOrCollapsing = true;
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
                    isExpandingOrCollapsing = false;
                    event.setExpanded(false);
                }

                @Override
                public void onAnimationCancel(Animator animation) {/*nothing*/}

                @Override
                public void onAnimationRepeat(Animator animation) {/*nothing*/}
            });
            mAnimator.setDuration(200);

            relative_layout.startAnimation(fadeOut);
            color_button.startAnimation(scale);
            edit_button.startAnimation(scale);
            alarm_button.startAnimation(scale);
            mAnimator.start();
        }

        private void expand() {
            isExpandingOrCollapsing = true;
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
                    //setColorFilterToActionButtons();
                    //relative_layout.setVisibility(View.VISIBLE);
                    //relative_layout.startAnimation(fadeIn);
                    isExpandingOrCollapsing = false;
                    event.setExpanded(true);
                    cardButtonOnClickInterface.scrollToCard(getAdapterPosition());
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

        public boolean isExpandingOrCollapsing(){
            return isExpandingOrCollapsing;
        }

        public void setEvent(Event event) {
            this.event = event;
        }
    }

    private ArrayList<Event> events;
    private CardButtonOnClickHelper onClickHelper;
    private Context context;

    public RVAdapter(ArrayList<Event> events,
                     CardButtonOnClickHelper onClickHelper, Context context) {
        this.events = events;
        this.onClickHelper = onClickHelper;
        this.context = context;
    }

    @Override
    public void onBindViewHolder(EventViewHolder eventViewHolder, int i) {
        eventViewHolder.setEvent(events.get(i));
        eventViewHolder.initCard(context);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public EventViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.one_event, viewGroup, false);
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
