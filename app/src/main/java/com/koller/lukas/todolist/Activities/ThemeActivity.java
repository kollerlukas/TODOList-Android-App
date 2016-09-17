package com.koller.lukas.todolist.Activities;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.koller.lukas.todolist.R;
import com.koller.lukas.todolist.RecyclerViewAdapters.RVAdapter;
import com.koller.lukas.todolist.RecyclerViewAdapters.Theme_RVAdapter;
import com.koller.lukas.todolist.Util.Callbacks.ColorPickerDialogCallback;
import com.koller.lukas.todolist.Util.Callbacks.OnItemClickInterface;
import com.koller.lukas.todolist.Util.ClickHelper.OnItemClickHelper;
import com.koller.lukas.todolist.Util.ThemeHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeActivity extends AppCompatActivity {

    private ThemeHelper helper;
    private int[] colors;
    private int[] textcolors;
    private int fab_color;
    private int fab_textcolor;

    private int toolbar_color;
    private int toolbar_textcolor;

    private int cord_color;
    private int cord_textcolor;

    private Toolbar toolbar;
    private Toolbar toolbar_card;
    private FloatingActionButton fab;
    private CoordinatorLayout cord;
    private CardView card;
    private View drawerIcon;
    private MenuItem infoIcon;
    private Drawable overflowIcon;

    private View statusBar;

    private RecyclerView mRecyclerView;
    private Theme_RVAdapter mAdapter;
    private LinearLayoutManager mLinearLayoutManager;

    private AlertDialog dialog;
    private AlertDialog themePickerDialog;

    private boolean toolbar_elevated = true;
    private boolean toolbarAndBackgroundSameColor = true;

    private Button presetThemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new ThemeHelper(this);

        if (!getResources().getBoolean(R.bool.tablet)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        setContentView(R.layout.activity_theme);

        toolbar = (Toolbar) findViewById(R.id.toolbar_theme);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fab = (FloatingActionButton) findViewById(R.id.fab_theme);
        cord = (CoordinatorLayout) findViewById(R.id.coordinatorLayout_theme);
        card = (CardView) findViewById(R.id.cardView);
        toolbar_card = (Toolbar) findViewById(R.id.toolbar_theme_card);
        statusBar = findViewById(R.id.statusbar);

        presetThemes = (Button) findViewById(R.id.preset_themes);

        toolbar_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                helper.toggleToolbarIconsTranslucent();
                if (helper.isToolbarIconsTranslucent()) {
                    Toast.makeText(ThemeActivity.this, "Toolbar icons translucent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThemeActivity.this, "Toolbar icons not translucent", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        deelevateToolbar();

        setupRecyclerView();
        setupTheme();

        //showInfoDialog();
    }

    public ThemeActivity() {
    }

    public void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_theme);
        mRecyclerView.setHasFixedSize(true);
        addOnItemTouchListenerToRecyclerView();

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mAdapter = new Theme_RVAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CheckToolbarElevation();
            }
        });
    }

    public void addOnItemTouchListenerToRecyclerView() {
        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, final int position, final RecyclerView.ViewHolder holder) {
                ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
                    @Override
                    public void colorPicked(int red, int green, int blue, int textColor) {
                        colors[position + 1] = Color.rgb(red, green, blue);
                        textcolors[position + 1] = textColor;
                        ((Theme_RVAdapter.ColorViewHolder) holder).colorCard(colors[position + 1], textcolors[position + 1]);
                    }
                };
                showColorPickerDialog(colorPickerDialogCallback, true, colors[position + 1], textcolors[position + 1], false);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN | ItemTouchHelper.LEFT |
                ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView,
                                  RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                int fromPosition = viewHolder.getAdapterPosition();
                int toPosition = target.getAdapterPosition();
                ThemeActivity.this.onItemMove(fromPosition, toPosition);
                return mAdapter.onItemMove(fromPosition, toPosition);
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {/*nothing*/}

            @Override
            public boolean isLongPressDragEnabled() {
                return false;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return false;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (isCurrentlyActive && actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
                    elevateToolbar();
                    viewHolder.itemView.setPressed(true);
                } else {
                    viewHolder.itemView.setPressed(false);
                    CheckToolbarElevation();

                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.notifyDataSetChanged();
                        }
                    }, 500);
                }
            }

        }).attachToRecyclerView(mRecyclerView);
    }

    public void setupTheme() {
        colors = new int[13];
        for (int i = 1; i < colors.length; i++) {
            colors[i] = helper.getEventColor(i);
        }
        textcolors = new int[13];
        for (int i = 1; i < textcolors.length; i++) {
            textcolors[i] = helper.getEventTextColor(i);
        }
        fab_color = helper.get("fab_color");
        fab_textcolor = helper.get("fab_textcolor");
        toolbar_color = helper.get("toolbar_color");
        toolbar_textcolor = helper.get("toolbar_textcolor");
        cord_color = helper.get("cord_color");
        cord_textcolor = helper.get("cord_textcolor");

        if (cord_color != toolbar_color) {
            toolbarAndBackgroundSameColor = false;
            elevateToolbar();
        }

        statusBar.setBackgroundColor(toolbar_color);

        toolbar_card.setBackgroundColor(toolbar_color);
        toolbar_card.setTitleTextColor(toolbar_textcolor);

        int color;
        if (helper.isToolbarIconsTranslucent()) {
            int color_base = ContextCompat.getColor(ThemeActivity.this, R.color.light_text_color);
            color = Color.argb(95, Color.red(color_base), Color.green(color_base), Color.blue(color_base));
        } else {
            color = ContextCompat.getColor(ThemeActivity.this, R.color.light_text_color);
        }
        ChangeColorOfToolbarDrawerIcon(color);
        colorInfoIcon(color);
        setOverflowButtonColor(color);

        fab.setBackgroundTintList(ColorStateList.valueOf(fab_color));
        fab.getDrawable().setTint(fab_textcolor);

        card.setCardBackgroundColor(cord_color);

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.itemChanged(i);
        }

        presetThemes.setTextColor(ContextCompat.getColor(ThemeActivity.this, R.color.black));
    }

    public void FabClicked(final View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(red, green, blue)));
                fab.getDrawable().setTint(textColor);
                fab_color = Color.rgb(red, green, blue);
                fab_textcolor = textColor;
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, fab_color, fab_textcolor, false);
    }

    public void ToolbarClicked(View v) {
        if (v.getId() == R.id.toolbar_theme) {
            return;
        }
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                colorToolbarAndCoord(red, green, blue, textColor, toolbarAndBackgroundSameColor, true);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, toolbar_color, toolbar_textcolor, true);
    }

    public void CoordinatorLayoutClicked(View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                colorToolbarAndCoord(red, green, blue, textColor, true, toolbarAndBackgroundSameColor);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, cord_color, cord_textcolor, true);
    }

    public void colorToolbarAndCoord(int red, int green, int blue, int textColor, boolean colorCord, boolean colorToolbar) {
        if (colorCord) {
            card.setCardBackgroundColor(Color.rgb(red, green, blue));
            cord_color = Color.rgb(red, green, blue);
            cord_textcolor = textColor;
        }
        if (colorToolbar) {
            toolbar_color = Color.rgb(red, green, blue);
            toolbar_textcolor = textColor;

            toolbar_card.setBackgroundColor(Color.rgb(red, green, blue));
            toolbar_card.setTitleTextColor(Color.rgb(textColor, textColor, textColor));
            toolbar_card.setTitleTextColor(textColor);

            statusBar.setBackgroundColor(toolbar_color);
        }
        if (cord_color == toolbar_color) {
            toolbarAndBackgroundSameColor = true;
        }
        if (!toolbarAndBackgroundSameColor) {
            elevateToolbar();
        } else {
            CheckToolbarElevation();
        }
    }

    public void showColorPickerDialog(final ColorPickerDialogCallback colorPickerDialogCallback, boolean eventColor, int oldColor, int oldTextColor, boolean showCheckbox) {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.theme_picker, null);
        final String format = "%1$03d";

        final CardView colorCard = (CardView) layout.findViewById(R.id.colorCard);
        final TextView colorTextView = (TextView) layout.findViewById(R.id.colorTextView);
        final EditText hexEditText = (EditText) layout.findViewById(R.id.edit_text);
        hexEditText.setTextColor(ContextCompat.getColor(this, R.color.light_text_color));
        CharSequence hexColor_argb = Integer.toHexString(oldColor);
        String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3) + hexColor_argb.charAt(4) + hexColor_argb.charAt(5) + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
        hexEditText.setText(hexColor);
        SeekBar[] colorSeekbars = new SeekBar[3];

        final SeekBar seekBarRed = (SeekBar) layout.findViewById(R.id.seekbar_red);
        final TextView red_text = (TextView) layout.findViewById(R.id.red_text);
        seekBarRed.setProgress(Color.red(oldColor));
        red_text.setText(String.format(format, Color.red(oldColor)));
        colorSeekbars[0] = seekBarRed;

        final SeekBar seekBarGreen = (SeekBar) layout.findViewById(R.id.seekbar_green);
        final TextView green_text = (TextView) layout.findViewById(R.id.green_text);
        seekBarGreen.setProgress(Color.green(oldColor));
        green_text.setText(String.format(format, Color.green(oldColor)));
        colorSeekbars[1] = seekBarGreen;

        final SeekBar seekBarBlue = (SeekBar) layout.findViewById(R.id.seekbar_blue);
        final TextView blue_text = (TextView) layout.findViewById(R.id.blue_text);
        seekBarBlue.setProgress(Color.blue(oldColor));
        blue_text.setText(String.format(format, Color.blue(oldColor)));
        colorSeekbars[2] = seekBarBlue;

        final SeekBar seekBarGrey = (SeekBar) layout.findViewById(R.id.seekbar_grey);
        final TextView grey_text = (TextView) layout.findViewById(R.id.grey_text);
        if (Color.red(oldTextColor) == 255) {
            seekBarGrey.setProgress(Color.alpha(oldTextColor) + 256);
            grey_text.setText(String.format(format, Color.alpha(oldTextColor)));
        } else {
            seekBarGrey.setProgress(255 - Color.alpha(oldTextColor));
            grey_text.setText(String.format(format, Color.alpha(oldTextColor)));
        }

        colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
        int color;
        if (seekBarGrey.getProgress() > 255) {
            color = Color.argb(seekBarGrey.getProgress() - 256, 255, 255, 255);
            grey_text.setText(String.format(format, seekBarGrey.getProgress() - 256));
        } else {
            color = Color.argb(255 - seekBarGrey.getProgress(), 0, 0, 0);
            grey_text.setText(String.format(format, 255 - seekBarGrey.getProgress()));
        }
        colorTextView.setTextColor(color);
        colorTextView.setText("TextColor");

        AppCompatCheckBox checkbox = (AppCompatCheckBox) layout.findViewById(R.id.checkbox);
        if (!showCheckbox) {
            checkbox.setVisibility(View.GONE);
        } else {
            int color_checkbox = fab_color;
            if (fab_color == ContextCompat.getColor(ThemeActivity.this, R.color.white)) {
                color_checkbox = ContextCompat.getColor(ThemeActivity.this, R.color.grey);
            }

            int color_grey = ContextCompat.getColor(ThemeActivity.this, R.color.grey);
            final int[][] states = new int[3][];
            final int[] colors = new int[3];
            int k = 0;
            // Disabled state
            states[k] = new int[]{-android.R.attr.state_enabled};
            colors[k] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
            k++;
            states[k] = new int[]{android.R.attr.state_checked};
            colors[k] = color_checkbox;
            k++;
            // Default enabled state
            states[k] = new int[0];
            colors[k] = color_grey;

            checkbox.setSupportButtonTintList(new ColorStateList(states, colors));
            checkbox.setTextColor(ContextCompat.getColor(ThemeActivity.this, R.color.light_text_color));

            checkbox.setChecked(toolbarAndBackgroundSameColor);
            checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                    toolbarAndBackgroundSameColor = b;
                }
            });
        }

        if (!eventColor) {
            colorTextView.setVisibility(View.GONE);
            seekBarGrey.setVisibility(View.GONE);
        }

        for (int i = 0; i < colorSeekbars.length; i++) {
            colorSeekbars[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    red_text.setText(String.format(format, seekBarRed.getProgress()));
                    green_text.setText(String.format(format, seekBarGreen.getProgress()));
                    blue_text.setText(String.format(format, seekBarBlue.getProgress()));
                    CharSequence hexColor_argb = Integer.toHexString(Color.rgb(seekBarRed.getProgress(), seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3) + hexColor_argb.charAt(4) + hexColor_argb.charAt(5) + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
                    hexEditText.setText(hexColor);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
        }


        seekBarGrey.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int color;
                if (progress > 255) {
                    color = Color.argb(seekBarGrey.getProgress() - 256, 255, 255, 255);
                    grey_text.setText(String.format(format, seekBarGrey.getProgress() - 256));
                } else {
                    color = Color.argb(255 - seekBarGrey.getProgress(), 0, 0, 0);
                    grey_text.setText(String.format(format, 255 - seekBarGrey.getProgress()));
                }
                colorTextView.setTextColor(color);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        hexEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 7 && s.charAt(0) == '#') {
                    int color = Color.parseColor(String.valueOf(s));
                    int red = Color.red(color);
                    int green = Color.green(color);
                    int blue = Color.blue(color);
                    colorCard.setCardBackgroundColor(Color.rgb(red, green, blue));
                    seekBarRed.setProgress(red);
                    red_text.setText(String.format(format, red));
                    seekBarGreen.setProgress(green);
                    green_text.setText(String.format(format, green));
                    seekBarBlue.setProgress(blue);
                    blue_text.setText(String.format(format, blue));
                }
            }
        });


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_light_theme);
        builder.setView(layout)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int textcolor;
                        if (seekBarGrey.getProgress() > 255) {
                            textcolor = Color.argb(seekBarGrey.getProgress(), 255, 255, 255);
                        } else {
                            textcolor = Color.argb(255 - seekBarGrey.getProgress(), 0, 0, 0);
                        }
                        colorPickerDialogCallback.colorPicked(seekBarRed.getProgress(),
                                seekBarGreen.getProgress(), seekBarBlue.getProgress(), textcolor);
                    }
                });
        themePickerDialog = builder.create();
        themePickerDialog.show();
        changeDialogButtonColor(themePickerDialog);
    }

    public void ChangeColorOfToolbarDrawerIcon(int color) {
        if (drawerIcon != null) {
            ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } else {
            for (int i = 0; i < toolbar.getChildCount(); i++) {
                if (toolbar.getChildAt(i) instanceof ImageView) {
                    drawerIcon = toolbar.getChildAt(i);
                    ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }
    }

    public void colorInfoIcon(int color) {
        if (infoIcon != null) {
            infoIcon.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            this.invalidateOptionsMenu();
        }
    }

    public void setOverflowButtonColor(int color) {
        if (overflowIcon == null) {
            overflowIcon = toolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        } else {
            overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public AlertDialog changeDialogButtonColor(AlertDialog dialog) {
        int color = fab_color;
        if (fab_color == ContextCompat.getColor(ThemeActivity.this, R.color.white)) {
            color = ContextCompat.getColor(ThemeActivity.this, R.color.grey);
        }

        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setTextColor(color);
        }
        Button neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if (neutral != null) {
            neutral.setTextColor(color);
        }
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setTextColor(color);
        }
        return dialog;
    }

    public void saveColorData() {
        helper.set("fab_color", fab_color);
        helper.set("fab_textcolor", fab_textcolor);
        helper.set("toolbar_color", toolbar_color);
        helper.set("toolbar_textcolor", toolbar_textcolor);
        helper.set("cord_color", cord_color);
        helper.set("cord_textcolor", cord_textcolor);

        helper.setColors(colors);
        helper.setTextColors(textcolors);

        helper.saveData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        saveColorData();
        overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long);
        this.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (themePickerDialog != null) {
            themePickerDialog.dismiss();
        }
        saveColorData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            if (toolbar.getMenu().getItem(i).getItemId() == R.id.info_theme) {
                infoIcon = toolbar.getMenu().getItem(i);
                //infoIcon.getIcon().setColorFilter(toolbar_textcolor, PorterDuff.Mode.SRC_IN);
                int color;
                if (helper.isToolbarIconsTranslucent()) {
                    int color_base = ContextCompat.getColor(ThemeActivity.this, R.color.light_text_color);
                    color = Color.argb(95, Color.red(color_base), Color.green(color_base), Color.blue(color_base));
                } else {
                    color = ContextCompat.getColor(ThemeActivity.this, R.color.light_text_color);
                }
                infoIcon.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.info_theme:
                //infoIcon = item.getIcon();
                showInfoDialog();
                return true;
            case R.id.restore_default_colors:
                restoreDefaultColors();
                return true;
            case R.id.share_theme:
                shareTheme();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long);
    }

    public void presetThemesClicked(View v) {
        restoreDefaultColors();
    }

    public void restoreDefaultColors() {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.theme_restore, null);
        CardView light = (CardView) layout.findViewById(R.id.light_cardview);
        CardView dark = (CardView) layout.findViewById(R.id.dark_cardview);
        CardView black = (CardView) layout.findViewById(R.id.black_cardview);

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restoreTheme(v);
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        };
        light.setOnClickListener(onClickListener);
        dark.setOnClickListener(onClickListener);
        black.setOnClickListener(onClickListener);

        dialog = new AlertDialog.Builder(this)
                .setView(layout)
                .setCancelable(true)
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        dialog.show();
    }

    public void restoreTheme(View v) {
        String theme = "dark";
        switch (v.getId()) {
            case R.id.light_cardview:
                theme = "light";
                break;
            case R.id.dark_cardview:
                theme = "dark";
                break;
            case R.id.black_cardview:
                theme = "black";
                break;
        }
        helper.restoreDefaultTheme(theme);
        helper.saveData();
        setupTheme();

        //deelevateToolbar();
        CheckToolbarElevation();
    }

    public void shareTheme() {
        String data;
        try {
            data = getShareData();
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String filename = "SharedTheme.txt";
        generateFileToShare(filename, data);

        File f = new File(getFilesDir().getAbsolutePath(), filename);
        Uri uri = FileProvider.getUriForFile(ThemeActivity.this,
                "com.koller.lukas.todolist.fileprovider", f);

        Intent shareIntent = ShareCompat.IntentBuilder.from(ThemeActivity.this)
                .addStream(uri)
                .setType(getContentResolver().getType(uri))
                .getIntent();

        //shareIntent.setData(uri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        if (shareIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(shareIntent);
        }
    }

    public void generateFileToShare(String filename, String data) {
        try {
            FileOutputStream fos
                    = openFileOutput(filename, Context.MODE_PRIVATE);
            fos.write(data.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getShareData() throws JSONException {
        JSONObject json = new JSONObject();

        json.put("fab_color", fab_color);
        json.put("fab_textcolor", fab_textcolor);
        json.put("toolbar_color", toolbar_color);
        json.put("toolbar_textcolor", toolbar_textcolor);
        json.put("cord_color", cord_color);
        json.put("cord_textcolor", cord_textcolor);

        for (int i = 1; i < colors.length; i++) {
            json.put("color" + i, colors[i]);
        }

        for (int i = 1; i < textcolors.length; i++) {
            json.put("textcolor" + i, textcolors[i]);
        }

        return json.toString();
    }

    public void showInfoDialog() {
        AlertDialog infoDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_light)
                .setTitle(getString(R.string.themeActivityInfoDialogTitle))
                .setMessage(getString(R.string.themeActivityInfoDialogContent))
                .setPositiveButton(getString(R.string.ok), null);
        infoDialog = builder.create();
        infoDialog.show();
        changeDialogButtonColor(infoDialog);
    }

    public void CheckToolbarElevation() {
        if (!toolbarAndBackgroundSameColor && toolbar_elevated) {
            return;
        } else {
            elevateToolbar();
        }
        int position = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position == 0) {
            deelevateToolbar();
        } else {
            elevateToolbar();
        }
    }

    public void elevateToolbar() {
        if (toolbar_elevated) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(ThemeActivity.this, R.animator.toolbar_raise);
        set.setTarget(toolbar_card);
        set.start();
        toolbar_elevated = true;
    }

    public void deelevateToolbar() {
        if (!toolbar_elevated) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(ThemeActivity.this, R.animator.toolbar_lower);
        set.setTarget(toolbar_card);
        set.start();
        toolbar_elevated = false;
    }

    public int getEventTextColor(int index) {
        return textcolors[index];
    }

    public int getEventColor(int index) {
        return colors[index];
    }

    public void onItemMove(int fromPosition, int toPosition) {
        ArrayList<Integer> colors_temp = new ArrayList<>();
        for (int i = 1; i < colors.length; i++){
            colors_temp.add(colors[i]);
        }

        Collections.swap(colors_temp, fromPosition, toPosition);

        for (int i = 1; i < colors.length; i++){
            colors[i] = colors_temp.get(i -1);
        }
    }
}


