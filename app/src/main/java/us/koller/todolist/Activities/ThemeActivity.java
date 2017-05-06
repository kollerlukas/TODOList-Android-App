package us.koller.todolist.Activities;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import us.koller.todolist.R;
import us.koller.todolist.RecyclerViewAdapters.ThemeRVAdapter;
import us.koller.todolist.Util.Callbacks.ColorPickerDialogCallback;
import us.koller.todolist.Util.Callbacks.OnItemClickInterface;
import us.koller.todolist.Util.ClickHelper.OnItemClickHelper;
import us.koller.todolist.Util.ThemeHelper;

/**
 * Created by Lukas on 13.06.2016.
 */
public class ThemeActivity extends AppCompatActivity {

    private ThemeHelper helper;

    private Toolbar toolbar;
    private Toolbar toolbar_card;
    private FloatingActionButton fab;
    private CardView card;
    private MenuItem infoIcon;
    private Drawable overflowIcon;

    private View statusBar;

    private RecyclerView mRecyclerView;
    private ThemeRVAdapter mAdapter;

    private AlertDialog dialog;
    private AlertDialog themePickerDialog;

    private boolean toolbarAndBackgroundSameColor = true;

    private Button presetThemes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        helper = new ThemeHelper(this);

        setContentView(R.layout.activity_theme);

        toolbar = (Toolbar) findViewById(R.id.toolbar_theme);
        toolbar.setTitleTextColor(helper.getDarkTextColor());
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        fab = (FloatingActionButton) findViewById(R.id.fab_theme);
        card = (CardView) findViewById(R.id.cardView);
        toolbar_card = (Toolbar) findViewById(R.id.toolbar_theme_card);
        statusBar = findViewById(R.id.statusbar);

        //set status bar icon color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            toolbar.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        presetThemes = (Button) findViewById(R.id.preset_themes);

        toolbar_card.setNavigationIcon(ContextCompat.getDrawable(this, R.drawable.ic_menu_black_24dp));
        toolbar_card.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                helper.toggleToolbarIconsTranslucent();
                changeColorOfToolbarDrawerIcon(toolbar_card, helper.getToolbarIconColor());
                if (helper.isToolbarIconsTranslucent()) {
                    Toast.makeText(ThemeActivity.this, "Toolbar icons translucent", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ThemeActivity.this, "Toolbar icons not translucent", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        deelevateToolbar(toolbar_card);

        initRecyclerView();
        initTheme();

        //showInfoDialog();
    }

    public ThemeActivity() {
    }

    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv_theme);
        mRecyclerView.setHasFixedSize(true);
        addOnItemTouchListenerToRecyclerView();

        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.layout_manager_span_count),
                StaggeredGridLayoutManager.VERTICAL);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mAdapter = new ThemeRVAdapter(helper);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkToolbarElevation();
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
                        helper.setEventColor(position + 1, Color.rgb(red, green, blue));
                        helper.setEventTextColor(position + 1, textColor);
                        ((ThemeRVAdapter.ColorViewHolder) holder).colorCard(position + 1, Color.rgb(red, green, blue), textColor);
                    }
                };
                showColorPickerDialog(colorPickerDialogCallback, true, helper.getEventColor(position + 1),
                        helper.getEventTextColor(position + 1), false);
            }
        });
    }

    public void initTheme() {
        toolbar_card.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));
        toolbar_card.setTitleTextColor(helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR));
        changeColorOfToolbarDrawerIcon(toolbar_card, helper.getToolbarIconColor());

        statusBar.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));

        int color;
        if (helper.isToolbarIconsTranslucent()) {
            int color_base = helper.getDarkTextColor();
            color = Color.argb(95, Color.red(color_base), Color.green(color_base), Color.blue(color_base));
        } else {
            color = helper.getDarkTextColor();
        }
        changeColorOfToolbarDrawerIcon(toolbar, color);
        colorInfoIcon(color);
        setOverflowButtonColor(color);

        card.setCardBackgroundColor(helper.get(ThemeHelper.CORD_COLOR));

        fab.setBackgroundTintList(ColorStateList.valueOf(helper.get(ThemeHelper.FAB_COLOR)));
        fab.getDrawable().setTint(helper.get(ThemeHelper.FAB_TEXT_COLOR));
        fab.setRippleColor(ContextCompat.getColor(ThemeActivity.this, R.color.white));

        presetThemes.setTextColor(helper.getDarkTextColor());

        if (helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            toolbarAndBackgroundSameColor = false;
            elevateToolbar(toolbar_card);
        }

        for (int i = 0; i < mAdapter.getItemCount(); i++) {
            mAdapter.itemChanged(i);
        }
    }

    public void fabClicked(final View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                fab.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(red, green, blue)));
                fab.getDrawable().setTint(textColor);

                helper.set(ThemeHelper.FAB_COLOR, Color.rgb(red, green, blue));
                helper.set(ThemeHelper.FAB_TEXT_COLOR, textColor);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, helper.get(ThemeHelper.FAB_COLOR), helper.get(ThemeHelper.FAB_TEXT_COLOR), false);
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
        showColorPickerDialog(colorPickerDialogCallback, true, helper.get(ThemeHelper.TOOLBAR_COLOR), helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR), true);
    }

    public void CoordinatorLayoutClicked(View v) {
        ColorPickerDialogCallback colorPickerDialogCallback = new ColorPickerDialogCallback() {
            @Override
            public void colorPicked(int red, int green, int blue, int textColor) {
                colorToolbarAndCoord(red, green, blue, textColor, true, toolbarAndBackgroundSameColor);
            }
        };
        showColorPickerDialog(colorPickerDialogCallback, true, helper.get(ThemeHelper.CORD_COLOR), helper.get(ThemeHelper.CORD_TEXT_COLOR), true);
    }

    public void colorToolbarAndCoord(int red, int green, int blue, int textColor, boolean colorCord, boolean colorToolbar) {
        if (colorCord) {
            helper.set(ThemeHelper.CORD_COLOR, Color.rgb(red, green, blue));
            helper.set(ThemeHelper.CORD_TEXT_COLOR, textColor);

            card.setCardBackgroundColor(Color.rgb(red, green, blue));
        }
        if (colorToolbar) {
            helper.set(ThemeHelper.TOOLBAR_COLOR, Color.rgb(red, green, blue));
            helper.set(ThemeHelper.TOOLBAR_TEXT_COLOR, textColor);

            toolbar_card.setBackgroundColor(Color.rgb(red, green, blue));
            toolbar_card.setTitleTextColor(textColor);
            changeColorOfToolbarDrawerIcon(toolbar_card, helper.getToolbarIconColor());

            statusBar.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));
        }
        if (helper.get(ThemeHelper.CORD_COLOR) == helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            toolbarAndBackgroundSameColor = true;
        }
        if (!toolbarAndBackgroundSameColor) {
            elevateToolbar(toolbar_card);
        } else {
            checkToolbarElevation();
        }
    }

    public void showColorPickerDialog(final ColorPickerDialogCallback colorPickerDialogCallback, boolean eventColor,
                                      int oldColor, int oldTextColor, boolean showCheckbox) {
        View layout = View.inflate(this, R.layout.color_picker, null);
        final String format = "%1$03d";

        final CardView colorCard = (CardView) layout.findViewById(R.id.colorCard);
        final TextView colorTextView = (TextView) layout.findViewById(R.id.colorTextView);
        final EditText hexEditText = (EditText) layout.findViewById(R.id.edit_text);
        hexEditText.setTextColor(helper.getDarkTextColor());
        CharSequence hexColor_argb = Integer.toHexString(oldColor);
        String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3) + hexColor_argb.charAt(4) + hexColor_argb.charAt(5) + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
        hexEditText.setText(hexColor);
        SeekBar[] colorSeekbars = new SeekBar[3];

        final SeekBar seekBarRed = (SeekBar) layout.findViewById(R.id.seekbar_red);
        final TextView red_text = (TextView) layout.findViewById(R.id.red_text);
        seekBarRed.setProgress(Color.red(oldColor));
        red_text.setText(String.format(getResources().getConfiguration().locale, format, Color.red(oldColor)));
        colorSeekbars[0] = seekBarRed;

        final SeekBar seekBarGreen = (SeekBar) layout.findViewById(R.id.seekbar_green);
        final TextView green_text = (TextView) layout.findViewById(R.id.green_text);
        seekBarGreen.setProgress(Color.green(oldColor));
        green_text.setText(String.format(getResources().getConfiguration().locale, format, Color.green(oldColor)));
        colorSeekbars[1] = seekBarGreen;

        final SeekBar seekBarBlue = (SeekBar) layout.findViewById(R.id.seekbar_blue);
        final TextView blue_text = (TextView) layout.findViewById(R.id.blue_text);
        seekBarBlue.setProgress(Color.blue(oldColor));
        blue_text.setText(String.format(getResources().getConfiguration().locale, format, Color.blue(oldColor)));
        colorSeekbars[2] = seekBarBlue;

        colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(),
                seekBarGreen.getProgress(), seekBarBlue.getProgress()));

        final SeekBar seekBarGrey = (SeekBar) layout.findViewById(R.id.seekbar_grey);
        final TextView grey_text = (TextView) layout.findViewById(R.id.grey_text);
        if (Color.red(oldTextColor) == 255) {
            seekBarGrey.setProgress(Color.alpha(oldTextColor) + 256);
            grey_text.setText(String.format(format, Color.alpha(oldTextColor)));
        } else {
            seekBarGrey.setProgress(255 - Color.alpha(oldTextColor));
            grey_text.setText(String.format(format, Color.alpha(oldTextColor)));
        }

        int color;
        if (seekBarGrey.getProgress() > 255) {
            color = Color.argb(seekBarGrey.getProgress() - 256, 255, 255, 255);
            grey_text.setText(String.format(format, seekBarGrey.getProgress() - 256));
        } else {
            color = Color.argb(255 - seekBarGrey.getProgress(), 0, 0, 0);
            grey_text.setText(String.format(format, 255 - seekBarGrey.getProgress()));
        }
        colorTextView.setTextColor(color);
        colorTextView.setText(getString(R.string.textcolor));

        AppCompatCheckBox checkbox = (AppCompatCheckBox) layout.findViewById(R.id.checkbox);
        if (!showCheckbox) {
            checkbox.setVisibility(View.GONE);
        } else {
            int color_checkbox = helper.get(ThemeHelper.FAB_COLOR);
            if (color_checkbox == ContextCompat.getColor(ThemeActivity.this, R.color.white)) {
                color_checkbox = ContextCompat.getColor(ThemeActivity.this, R.color.grey);
            }

            int color_grey = ContextCompat.getColor(ThemeActivity.this, R.color.grey);
            final int[][] states = new int[3][];
            final int[] colors = new int[3];
            int k = 0;

            // Disabled state
            states[k] = new int[]{-android.R.attr.state_enabled};
            colors[k] = Color.argb(72, Color.red(color_grey),
                    Color.green(color_grey), Color.blue(color_grey));
            k++;
            states[k] = new int[]{android.R.attr.state_checked};
            colors[k] = color_checkbox;
            k++;

            // Default enabled state
            states[k] = new int[0];
            colors[k] = color_grey;

            checkbox.setSupportButtonTintList(new ColorStateList(states, colors));
            checkbox.setTextColor(helper.getDarkTextColor());

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
                    colorCard.setCardBackgroundColor(Color.rgb(seekBarRed.getProgress(),
                            seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    red_text.setText(String.format(format, seekBarRed.getProgress()));
                    green_text.setText(String.format(format, seekBarGreen.getProgress()));
                    blue_text.setText(String.format(format, seekBarBlue.getProgress()));
                    CharSequence hexColor_argb = Integer.toHexString(Color.rgb(seekBarRed.getProgress(),
                            seekBarGreen.getProgress(), seekBarBlue.getProgress()));
                    String hexColor = "#" + hexColor_argb.charAt(2) + hexColor_argb.charAt(3)
                            + hexColor_argb.charAt(4) + hexColor_argb.charAt(5)
                            + hexColor_argb.charAt(6) + hexColor_argb.charAt(7);
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
                    int color;
                    try {
                        color = Color.parseColor(String.valueOf(s));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return;
                    }
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


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light_noKeyboard);
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

    public void changeColorOfToolbarDrawerIcon(Toolbar toolbar, int color) {
        for (int i = 0; i < toolbar.getChildCount(); i++) {
            if (toolbar.getChildAt(i) instanceof ImageView) {
                ImageView drawerIcon = (ImageView) toolbar.getChildAt(i);
                drawerIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
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
        int color = helper.get(ThemeHelper.FAB_COLOR);
        if (color == ContextCompat.getColor(ThemeActivity.this, R.color.white)) {
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        helper.saveData(ThemeActivity.this);
        overridePendingTransition(R.anim.fade_in_long, R.anim.fade_out_long);

        Intent intent = new Intent(this, MainActivity.class);
        intent.setAction(MainActivity.NEW_THEME);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onStop() {
        super.onStop();
        if (themePickerDialog != null) {
            themePickerDialog.dismiss();
        }
        helper.saveData(ThemeActivity.this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_theme, menu);
        for (int i = 0; i < toolbar.getMenu().size(); i++) {
            if (toolbar.getMenu().getItem(i).getItemId() == R.id.info_theme) {
                infoIcon = toolbar.getMenu().getItem(i);
                int color;
                if (helper.isToolbarIconsTranslucent()) {
                    int color_base = helper.getDarkTextColor();
                    color = Color.argb(95, Color.red(color_base), Color.green(color_base), Color.blue(color_base));
                } else {
                    color = helper.getDarkTextColor();
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
        View layout = View.inflate(this, R.layout.theme_restore, null);
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
        if (dialog.getWindow() != null) {
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        }
        dialog.show();
    }

    public void restoreTheme(View v) {
        String theme = ThemeHelper.LIGHT;
        switch (v.getId()) {
            case R.id.light_cardview:
                theme = ThemeHelper.LIGHT;
                break;
            case R.id.dark_cardview:
                theme = ThemeHelper.DARK;
                break;
            case R.id.black_cardview:
                theme = ThemeHelper.BLACK;
                break;
        }
        helper.restoreDefaultTheme(ThemeActivity.this, theme);
        helper.saveData(ThemeActivity.this);
        initTheme();

        if (helper.get(ThemeHelper.TOOLBAR_COLOR) == helper.get(ThemeHelper.CORD_COLOR)) {
            toolbarAndBackgroundSameColor = true;
        }
        checkToolbarElevation();
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
                "us.koller.todolist.fileprovider", f);

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
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
        JSONObject json = new JSONObject()

                .put(ThemeHelper.FAB_COLOR, helper.get(ThemeHelper.FAB_COLOR))
                .put(ThemeHelper.FAB_TEXT_COLOR, helper.get(ThemeHelper.FAB_TEXT_COLOR))
                .put(ThemeHelper.TOOLBAR_COLOR, helper.get(ThemeHelper.TOOLBAR_COLOR))
                .put(ThemeHelper.TOOLBAR_TEXT_COLOR, helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR))
                .put(ThemeHelper.CORD_COLOR, helper.get(ThemeHelper.CORD_COLOR))
                .put(ThemeHelper.CORD_TEXT_COLOR, helper.get(ThemeHelper.CORD_TEXT_COLOR));

        for (int i = 1; i < 13; i++) {
            json.put(ThemeHelper.COLOR + i, helper.getEventColor(i));
        }

        for (int i = 1; i < 13; i++) {
            json.put(ThemeHelper.TEXT_COLOR + i, helper.getEventTextColor(i));
        }

        return json.toString();
    }

    public void showInfoDialog() {
        AlertDialog infoDialog;
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme_Light)
                .setTitle(getString(R.string.themeActivityInfoDialogTitle))
                .setMessage(getString(R.string.themeActivityInfoDialogContent))
                .setPositiveButton(getString(R.string.ok), null);
        infoDialog = builder.create();
        infoDialog.show();
        changeDialogButtonColor(infoDialog);
    }

    public void checkToolbarElevation() {
        if (helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            elevateToolbar(toolbar_card);
            return;
        }

        if (mRecyclerView.canScrollVertically(-1)) {
            elevateToolbar(toolbar_card);
        } else {
            deelevateToolbar(toolbar_card);
        }
    }

    /*public void elevateToolbar() {
        toolbar_card.setSelected(true);
    }

    public void deelevateToolbar() {
        toolbar_card.setSelected(false);
    }*/

    public void elevateToolbar(Toolbar toolbar) {
        if(!((View) toolbar.getParent()).isSelected()){
            ObjectAnimator.ofFloat((View) toolbar.getParent(), "elevation", 0f,
                    getResources().getDimension(R.dimen.toolbar_elevation)).start();
            ((View) toolbar.getParent()).setSelected(true);
        }
    }

    public void deelevateToolbar(Toolbar toolbar) {
        if(!((View) toolbar.getParent()).isSelected()
                || helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)){
            return;
        }
        ObjectAnimator.ofFloat((View) toolbar.getParent(), "elevation",
                getResources().getDimension(R.dimen.toolbar_elevation), 0f).start();
        ((View) toolbar.getParent()).setSelected(false);
    }
}


