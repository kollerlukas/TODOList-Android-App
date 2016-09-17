package com.koller.lukas.todolist.Activities;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveFolder;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.DriveStatusCodes;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.graphics.drawable.AnimatedVectorDrawableCompat;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.koller.lukas.todolist.BroadcastReceiver;
import com.koller.lukas.todolist.DriveSync.CreateFile;
import com.koller.lukas.todolist.DriveSync.EditFileInAppFolder;
import com.koller.lukas.todolist.DriveSync.RetrieveDataFromAppFolder;
import com.koller.lukas.todolist.DriveSync.RetrieveDriveId;
import com.koller.lukas.todolist.DriveSync.SyncDataAsyncTask;
import com.koller.lukas.todolist.ImportListViewAdapter;
import com.koller.lukas.todolist.R;
import com.koller.lukas.todolist.RecyclerViewAdapters.RVAdapter;
import com.koller.lukas.todolist.RecyclerViewAdapters.RVAdapter.EventViewHolder;
import com.koller.lukas.todolist.Settings;
import com.koller.lukas.todolist.Todolist.Alarm;
import com.koller.lukas.todolist.Todolist.Event;
import com.koller.lukas.todolist.Todolist.Todolist;
import com.koller.lukas.todolist.Util.Callbacks.DriveIdCallback;
import com.koller.lukas.todolist.Util.Callbacks.ModifiedDateCallback;
import com.koller.lukas.todolist.Util.Callbacks.OnItemClickInterface;
import com.koller.lukas.todolist.Util.Callbacks.RetrievedDataFromAppFolderCallback;
import com.koller.lukas.todolist.Util.Callbacks.ShareEventCallback;
import com.koller.lukas.todolist.Util.Callbacks.SyncDataCallback;
import com.koller.lukas.todolist.Util.ClickHelper.CardButtonOnClickHelper;
import com.koller.lukas.todolist.Util.ClickHelper.OnItemClickHelper;
import com.koller.lukas.todolist.Util.DPCalc;
import com.koller.lukas.todolist.Util.ThemeHelper;
import com.koller.lukas.todolist.Widget.WidgetProvider_List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private Context context = this;
    private Todolist todolist;
    private static MainActivity mThis = null;
    private NotificationManager mNotificationManager;
    private RVAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private SwipeRefreshLayout.OnRefreshListener mRefreshListener;
    private ItemTouchHelper mItemTouchHelper;
    private ItemTouchHelper.SimpleCallback mItemTouchHelperCallback;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private LinearLayoutManager mLinearLayoutManager;
    private View drawerIcon;
    private Toolbar mToolbar;
    private boolean toolbarElevated = false;
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;
    private TextView mTextView;
    private MenuItem mSilenceAllAlarms;
    private MenuItem autoSyncMenuItem;
    private MenuItem generalNotif;
    private ArrayList<MenuItem> navigationHeaders;

    private Drawable overflowIcon;
    private MenuItem shareIcon;

    private AlertDialog colorSelectorDialog;
    private AlertDialog addEventDialog;
    private AlertDialog alarmInfoDialog;
    private boolean categoryWasSelected = false;
    private Snackbar snackbar;

    private AlertDialog importDialog;

    private Handler handler = new Handler();
    private boolean actionButtonAlreadyClicked = false;
    private DialogInterface.OnDismissListener dismisslistener;

    private Event eventToColorChange;
    private int selectedColor; //For Color selecting at the add Dialog
    private boolean newTheme = true;

    private ThemeHelper helper;

    private Settings settings;

    private boolean isEventDraged = false;

    private AnimatedVectorDrawableCompat ntd_anim;

    private ShareEventCallback shareEventCallback;
    private boolean shareEvents = false;

    private boolean tablet;

    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private TextView personName;
    private TextView personEmail;
    private RelativeLayout personData;
    private static final int RC_SIGN_IN = 9001;
    private MenuItem syncData;
    private long modifiedDateTemp = 0;
    private boolean completeSync = false; // should directly write after done syncing

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        settings = new Settings(MainActivity.this);

        settings.readSettings();

        todolist = new Todolist(settings);

        buildGoogleApiClient();
        mGoogleApiClient.connect();

        todolist.initData(context);

        initRecyclerView();

        initDrawerLayout();

        initSignInWithGoogle();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (getResources().getBoolean(R.bool.tablet)) {
            tablet = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            tablet = false;
        }

        mDrawerToggle.syncState();

        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            if (mToolbar.getChildAt(i) instanceof ImageView) {
                drawerIcon = mToolbar.getChildAt(i);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        initTheme();

        checkForIntentInputs();

        checkEventRemoved();

        mThis = this;

        if ((boolean) settings.get("showNotification")) {
            checkForNotificationUpdate();
        }

        if (todolist.getTodolist().size() == 0) {
            showNothingTodo(false);
        }

        if (mAdapter.getList().size()
                == 0 && todolist.getTodolist().size() > 0) {
            showSnackbar(getString(R.string.no_category_selected));
        }
        super.onResume();
    }

    public void buildGoogleApiClient() {
        // For Google Drive Api
        GoogleSignInOptions gso
                = new GoogleSignInOptions.Builder(
                GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .build();
    }

    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        addOnItemTouchListenerToRecyclerView();

        /*if (tablet) {
            //Put Tablet specific layout here
        } else {

        }*/

        mAdapter = new RVAdapter(todolist.initAdapterList(),
                new CardButtonOnClickHelper(this), this);

        mLinearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CheckToolbarElevation();
            }
        });

        mSwipeRefreshLayout
                = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                completeSync = true;
                lookForSync();
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    public void initDrawerLayout() {
        mDrawerLayout
                = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle
                = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }
        };
        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.setDrawerListener(mDrawerToggle);
        navigationView
                = (NavigationView) findViewById(R.id.navigation_view);
        NavigationMenuView navigationMenuView
                = (NavigationMenuView) navigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        //find header Views
        View headerLayout = navigationView.getHeaderView(0);
        signInButton
                = (SignInButton) headerLayout.findViewById(R.id.sign_in_button);
        personName
                = (TextView) headerLayout.findViewById(R.id.personName);
        personEmail
                = (TextView) headerLayout.findViewById(R.id.personEmail);
        personData
                = (RelativeLayout) headerLayout.findViewById(R.id.personData);
        navigationHeaders = new ArrayList<>();
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            navigationHeaders.add(mi);
            if (mi.getItemId() == R.id.navigation_header0) {
                syncData = mi;
            }
            SubMenu subMenu = mi.getSubMenu();
            if (subMenu != null && subMenu.size() > 0) {
                for (int j = 0; j < subMenu.size(); j++) {
                    MenuItem subMenuItem = subMenu.getItem(j);
                    switch (subMenuItem.getItemId()) {
                        case R.id.show_notification_toggle:
                            generalNotif = subMenuItem;
                            final SwitchCompat shoNotif
                                    = (SwitchCompat) subMenu.getItem(j).getActionView();
                            shoNotif.setChecked((boolean) settings.get("showNotification"));
                            shoNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ShowNotificationToggleClicked();
                                    if (!(shoNotif.isChecked() && isChecked)) {
                                        shoNotif.setChecked((boolean) settings.get("showNotification"));
                                    }
                                }
                            });
                            break;
                        case R.id.silence_all_alarms:
                            mSilenceAllAlarms = subMenuItem;
                            if (!todolist.isAlarmScheduled()) {
                                subMenuItem.getActionView().setEnabled(false);
                            }
                            final SwitchCompat silence_all_alarms
                                    = (SwitchCompat) subMenu.getItem(j).getActionView();
                            silence_all_alarms.setChecked(!(boolean) settings.get("vibrate"));
                            silence_all_alarms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    SilenceAlarmsClicked();
                                }
                            });
                            break;
                        case R.id.autoSync:
                            autoSyncMenuItem = subMenuItem;
                            final SwitchCompat autoSync
                                    = (SwitchCompat) subMenu.getItem(j).getActionView();
                            autoSync.setChecked((boolean) settings.get("autoSync"));
                            autoSync.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    AutoSyncClicked(isChecked);
                                }
                            });
                            break;
                    }
                }
            }
        }
        NavigationViewSetItemSelectedListener(navigationView);
    }

    public void NavigationViewSetItemSelectedListener(NavigationView navigation_view) {
        navigation_view.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case android.R.id.home:
                                mDrawerLayout.openDrawer(GravityCompat.START);
                                break;
                            /*case R.id.restore_last_removed_event:
                                RestoreLastDoneEventClicked();
                                break;*/
                            case R.id.select_theme:
                                SelectThemeClicked();
                                break;
                            case R.id.info:
                                InfoButtonClicked();
                                break;
                            case R.id.select_category:
                                SelectCategoryClicked();
                                break;
                        }
                        return true;
                    }
                });
    }

    public void initSignInWithGoogle() {
        mSwipeRefreshLayout.setEnabled(false);

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();

                if (!isNetworkAvailable()) {
                    showToast("No Internet Connection");
                    return;
                }

                if (mGoogleApiClient.isConnected()) {
                    showSyncExperimentalFeatureDialog();
                } else {
                    showToast("Client not connected!");
                }

            }
        });

        if (!(boolean) settings.get("syncEnabled")) {
            personData.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            personData.setVisibility(View.VISIBLE);
            personEmail.setText("...");
            signInButton.setVisibility(View.GONE);
            OptionalPendingResult<GoogleSignInResult> opr
                    = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    public void initTheme() {
        if (!newTheme) {
            return;
        }
        helper = new ThemeHelper(MainActivity.this);

        mToolbar.setBackgroundColor(helper.get("toolbar_color"));
        mToolbar.setTitleTextColor(helper.get("toolbar_textcolor"));
        ChangeColorOfToolbarDrawerIcon(helper.getToolbarIconColor());

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(helper.get("fab_color")));
        mFab.getDrawable().setTint(helper.get("fab_textcolor"));

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.setBackgroundColor(helper.get("cord_color"));
        ((ImageView) findViewById(R.id.nothing_todo))
                .setColorFilter(helper.get("cord_textcolor"), PorterDuff.Mode.SRC_IN);

        int color_grey = ContextCompat.getColor(context, R.color.grey);
        int color_dark = ContextCompat.getColor(context, R.color.black_light);
        final int[][] states = new int[3][];
        final int[] thumbColors = new int[3];
        final int[] trackColors = new int[3];
        int k = 0;

        // Disabled state
        states[k] = new int[]{-android.R.attr.state_enabled};
        thumbColors[k] = Color.argb(72, Color.red(color_grey),
                Color.green(color_grey), Color.blue(color_grey));
        trackColors[k] = Color.argb(72, Color.red(color_dark),
                Color.green(color_dark), Color.blue(color_dark));
        k++;

        states[k] = new int[]{android.R.attr.state_checked};
        thumbColors[k] = helper.get("fab_color");
        trackColors[k] = Color.argb(72, Color.red(helper.get("fab_color")),
                Color.green(helper.get("fab_color")), Color.blue(helper.get("fab_color")));
        k++;

        // Default enabled state
        states[k] = new int[0];
        thumbColors[k] = color_grey;
        trackColors[k] = color_dark;
        k++;

        ((SwitchCompat) mSilenceAllAlarms.getActionView()).setHighlightColor(helper.get("fab_color"));

        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                mSilenceAllAlarms.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                mSilenceAllAlarms.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                autoSyncMenuItem.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                autoSyncMenuItem.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                generalNotif.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                generalNotif.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

        setOverflowButtonColor(helper.getToolbarIconColor());

        for (int i = 0; i < navigationHeaders.size(); i++) {
            SpannableString s = new SpannableString(navigationHeaders.get(i).getTitle());
            s.setSpan(new ForegroundColorSpan(helper.get("fab_color")), 0, s.length(), 0);
            navigationHeaders.get(i).setTitle(s);
        }

        mAdapter.allItemsChanged();
        newTheme = false;

        CheckToolbarElevation();
    }

    public static boolean isRunning() {
        return mThis != null;
    }

    public static MainActivity getThis() {
        return mThis;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void signIn() {
        //show loading
        signInButton.setVisibility(View.GONE);
        personData.setVisibility(View.VISIBLE);
        this.personEmail.setText("...");

        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // signed out!
                        settings.set("signedIn", false);
                        settings.set("syncEnabled", false);
                        settings.set("lastSyncTimeStamp", (long) 0);

                        mSwipeRefreshLayout.setEnabled(false);
                        syncData.setVisible(false);
                        initSignInWithGoogle();
                    }
                });
    }

    //for removing Account from App
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {/*noting*/}
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    handleSignInResult(
                            Auth.GoogleSignInApi.getSignInResultFromIntent(intent));
                }
                break;
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();
            String personName = acct.getDisplayName();
            if (personName == null) {
                personName = "";
            }
            String personEmail = acct.getEmail();
            signInButton.setVisibility(View.GONE);
            personData.setVisibility(View.VISIBLE);
            this.personName.setText(personName);
            this.personEmail.setText(personEmail);

            personData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mDrawerLayout.closeDrawers();
                    String content = getString(R.string.signOutDialog_content);
                    AlertDialog.Builder builder
                            = new AlertDialog.Builder(context, getDialogTheme());
                    builder.setMessage(content)
                            .setTitle(getString(R.string.signOut))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mGoogleApiClient.isConnected()) {
                                        //revokeAccess();
                                        signOut();
                                    }
                                }
                            })
                            .setNegativeButton(getString(R.string.no), null);
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    changeDialogButtonColor(dialog);
                }
            });
            //syncData.setVisible(true);
            settings.set("syncEnabled", true);
            settings.set("signedIn", true);

            mSwipeRefreshLayout.setEnabled(true);
        } else {
            showToast("SignIn not successful!");
            // not Signed in
            personData.setOnClickListener(null);
            personData.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            syncData.setVisible(false);
            settings.set("syncEnabled", false);
            settings.set("signedIn", false);

            mSwipeRefreshLayout.setEnabled(false);
        }
    }

    public void lookForSync() {
        mSwipeRefreshLayout.setRefreshing(true);

        settings.preventDriveChangeListener();

        if (!(boolean) settings.get("signedIn") && !mGoogleApiClient.isConnected()) {
            showToast("Api Client not connected");
            mSwipeRefreshLayout.setRefreshing(false);
            return;
        }

        if (!isNetworkAvailable()) {
            showSnackbar("no Internet Connection");
        }

        Drive.DriveApi.requestSync(mGoogleApiClient)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status result) {
                        if (!result.isSuccess()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                            if (result.getStatusCode()
                                    == DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED) {
                                showToast("Sync currently not possible. Please try again later");
                                return;
                            }
                            showToast("MainActivity.syncData(), Error: "
                                    + DriveStatusCodes.getStatusCodeString(result.getStatusCode()));
                            return;
                        }

                        new RetrieveDriveId(mGoogleApiClient, new ModifiedDateCallback() {
                            @Override
                            public void noFilesFound() {
                                showToast("no Files found -> creating new file");

                                createNewFile();
                            }

                            @Override
                            public void getModifiedDate(long timeStamp, DriveId driveId) {
                                Calendar cal = Calendar.getInstance();
                                cal.setTimeInMillis(timeStamp);

                                //Log.d("MainActivity", "getModifiedDate: " + cal.getTime().toString());

                                if (timeStamp > (long) MainActivity.this.settings.get("lastReceivedDataTimeStamp")) {
                                    if (mGoogleApiClient.isConnected()) {

                                        //settings.set("lastReceivedDataTimeStamp", timeStamp);
                                        modifiedDateTemp = timeStamp;
                                        settings.set("driveId", driveId);

                                        tryToRetrieveData(driveId);
                                    } else {
                                        mSwipeRefreshLayout.setRefreshing(false);
                                        showToast("Api Client not connected");
                                    }
                                } else {
                                    mSwipeRefreshLayout.setRefreshing(false);
                                    showToast("MainActivity: modifiedDate old!");

                                    settings.set("lastReceivedDataTimeStamp", timeStamp);
                                    settings.set("driveId", driveId);

                                    //writeToGoogleDrive();
                                }
                            }

                            @Override
                            public void error(int statusCode) {
                                mSwipeRefreshLayout.setRefreshing(false);
                                showToast("MainActivity: modifiedDateCallback, error");
                            }
                        }).execute();
                    }
                });
    }

    public void tryToRetrieveDriveId() {
        new RetrieveDriveId(mGoogleApiClient, new DriveIdCallback() {
            @Override
            public void noFilesFound() {
                showToast("no Files found -> creating new file");

                createNewFile();
            }

            @Override
            public void gotDriveId(DriveId driveId) {
                settings.set("driveId", driveId);
                tryToRetrieveData(driveId);
            }

            @Override
            public void error(int statusCode) {
                DriveIdError(statusCode);
            }
        }).execute();
    }

    public void tryToRetrieveData(DriveId driveId) {
        mSwipeRefreshLayout.setRefreshing(true);

        new RetrieveDataFromAppFolder(mGoogleApiClient, new RetrievedDataFromAppFolderCallback() {
            @Override
            public void error(String error) {
                switch (error) {
                    case "no data":
                        showToast("no Files found -> creating new file");

                        createNewFile();
                        break;
                    case "file.open() not successful":
                        showToast(error);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case "no data written":
                        showToast(error);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case "driveId Error":
                        showToast(error);
                        mSwipeRefreshLayout.setRefreshing(false);
                        break;
                    case "empty file":
                        showToast(error);

                        writeToGoogleDrive();
                        break;
                }
            }

            @Override
            public void retrievedDataFromAppFolder(String data) {
                showToast("data successfully received");
                syncData(data);
            }
        }).execute(driveId);
    }

    public void syncData(String data) {
        //Log.d("MainActivity", "received data: " + data);

        new SyncDataAsyncTask(todolist, data,
                (long) settings.get("lastSyncTimeStamp"), new SyncDataCallback() {
            @Override
            public void DoneSyncingData(ArrayList<Long> eventsToUpdate) {
                MainActivity.this.DoneSyncingData(eventsToUpdate);

                if (completeSync) {
                    writeToGoogleDrive();
                    completeSync = false;
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet) {
                MainActivity.this.updateAlarms(alarmsToCancel, alarmsToSet);
            }

            @Override
            public void error(String error) {
                showToast("SyncDataAsyncTask: " + error);

                if (error.equals("JSONException") && completeSync) {
                    writeToGoogleDrive();
                    completeSync = false;
                    return;
                }

                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void updateColors(int[] newColors, int[] newTextColors) {
                helper.setColors(newColors);
                helper.setTextColors(newTextColors);
                helper.saveData();
            }

        }).execute();
    }

    public void writeToGoogleDrive() {
        mDrawerLayout.closeDrawers();
        if (mGoogleApiClient.isConnected()) {
            if (settings.driveIdStored()) {
                showToast("reusing driveId");
                writeToFile((DriveId) settings.get("driveId"));
            } else {
                new RetrieveDriveId(mGoogleApiClient, new DriveIdCallback() {
                    @Override
                    public void noFilesFound() {
                        //showToast("no Files found -> creating new file");

                        createNewFile();
                    }

                    @Override
                    public void gotDriveId(DriveId driveId) {
                        writeToFile(driveId);
                    }

                    @Override
                    public void error(int statusCode) {
                        DriveIdError(statusCode);
                    }
                }).execute();
            }
        } else {
            showToast("Client not connected!");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void writeToFile(DriveId driveId) {
        DriveFile file = driveId.asDriveFile();

        String data = todolist.getSyncData();

        //Log.d("MainActivity", "send data: " + data);

        int statusCode = 0;
        try {
            statusCode = new EditFileInAppFolder(mGoogleApiClient, data)
                    .execute(file).get();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        showToast("EditFileInAppFolder, StatusCode: "
                + CommonStatusCodes.getStatusCodeString(statusCode));

        if (statusCode == CommonStatusCodes.SUCCESS) {
            settings.set("lastReceivedDataTimeStamp", modifiedDateTemp);
            settings.set("lastSyncTimeStamp", System.currentTimeMillis());

            todolist.clearRemovedAndAddedEvents(MainActivity.this);
        }

        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void createNewFile() {
        showToast("creating File");

        mDrawerLayout.closeDrawers();
        if (mGoogleApiClient.isConnected()) {
            Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(
                    new ResultCallback<DriveApi.DriveContentsResult>() {
                        @Override
                        public void onResult(DriveApi.DriveContentsResult result) {
                            if (!result.getStatus().isSuccess()) {
                                showToast("Error while trying to create new file contents");
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            new CreateFile(mGoogleApiClient,
                                    new ResultCallback<DriveFolder.DriveFileResult>() {
                                        @Override
                                        public void onResult(DriveFolder.DriveFileResult result) {
                                            if (!result.getStatus().isSuccess()) {
                                                showToast("Error while trying to create a file; Status-Code: "
                                                        + result.getStatus().getStatusMessage());
                                                mSwipeRefreshLayout.setRefreshing(false);
                                                return;
                                            }
                                            settings.set("driveId", result.getDriveFile().getDriveId());
                                            writeToFile(result.getDriveFile().getDriveId());
                                        }
                                    }, result).execute();
                        }
                    });
        } else {
            showToast("Client not connected!");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void DriveIdError(int statusCode) {
        if (statusCode == DriveStatusCodes.SUCCESS) {
            //File not found
            createNewFile();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
            showToast("Error while retrieving DriveId; Status-Code: "
                    + DriveStatusCodes.getStatusCodeString(statusCode));
        }
    }

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void checkForIntentInputs() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "widget_button":
                    if (addEventDialog != null) {
                        addEventDialog.dismiss();
                    }
                    FloatingActionButton fab
                            = (FloatingActionButton) findViewById(R.id.fab);
                    if (fab != null) {
                        FabClicked(fab);
                    }
                    break;
                case "notification_add_todo":
                    if (addEventDialog != null) {
                        addEventDialog.dismiss();
                    }
                    FabClicked((FloatingActionButton) findViewById(R.id.fab));
                    break;
                case "Import":
                    //check if theme or TODOs
                    if (importDialog != null) {
                        importDialog.dismiss();
                    }
                    onImportIntent(intent.getStringExtra("events"));
                    //showImportEvents(intent.getStringExtra("events"), true);
                    break;
                case "addNewEvents":
                    if (importDialog != null) {
                        importDialog.dismiss();
                    }
                    showImportEvents(intent.getStringExtra("events"), false);
                    break;
                default:
                    //do nothing
                    break;
            }
        }
        intent.setAction("no_action");
    }

    public void checkForNotificationUpdate() {
        if (todolist.getTodolist().size() != 0
                && (boolean) settings.get("showNotification")) {
            showNotification();
        } else {
            cancelNotification();
        }
    }

    public void cancelNotification() {
        if (mNotificationManager != null) {
            mNotificationManager.cancel(0);
        }
    }

    public void showNotification() {
        String content;
        if (todolist.getTodolist().size() == 1) {
            content = getString(R.string.you_have) + " " + todolist.getTodolist().size() + " " + getString(R.string.event_in_your_todolist);
        } else {
            content = getString(R.string.you_have) + " " + todolist.getTodolist().size() + " " + getString(R.string.events_in_your_todolist);
        }
        Intent add_event_intent = new Intent(context, MainActivity.class);
        add_event_intent.setAction("notification_add_todo");
        add_event_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent add_event_pendingIntent = PendingIntent.getActivity(context, 6, add_event_intent, 0); // PendingIntent.FLAG_IMMUTABLE

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_notif)
                .setContentTitle(getString(R.string.app_name))
                .addAction(R.drawable.ic_add, context.getString(R.string.add_event), add_event_pendingIntent)
                .setColor(ContextCompat.getColor(context, R.color.button_color))
                .setContentText(content);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 666, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        int todoSize;
        if (todolist.getTodolist().size() > 5) {
            todoSize = 5;
            events[5] = "...";
        } else {
            todoSize = todolist.getTodolist().size();
        }
        for (int i = 0; i < todoSize; i++) {
            if (todolist.getTodolist().get(i) != null) {
                events[i] = todolist.getTodolist().get(i).getWhatToDo();
            }
        }
        inboxStyle.setSummaryText(content);
        inboxStyle.setBigContentTitle(getString(R.string.your_events_are));
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        mBuilder.setStyle(inboxStyle);
        mBuilder.setOngoing(true);
        mNotificationManager.notify(0, mBuilder.build());
    }

    public void addOnItemTouchListenerToRecyclerView() {
        mItemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                todolist.eventMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                mAdapter.itemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                checkForNotificationUpdate();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                removeEvent(viewHolder.getAdapterPosition());
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return !shareEvents;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return !isEventDraged && !shareEvents;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, final ViewHolder viewHolder,
                                    float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                if (isCurrentlyActive) {
                    if (((EventViewHolder) viewHolder).event.isExpanded) {
                        ((EventViewHolder) viewHolder).cardClicked();
                    }
                    elevateToolbar();
                    viewHolder.itemView.setPressed(true);

                    switch (actionState) {
                        case ItemTouchHelper.ACTION_STATE_DRAG:
                            isEventDraged = true;
                            if (!todolist.isAdapterListTodolist(mAdapter)) {
                                boolean[] all_categories_selected = new boolean[13];
                                for (int i = 1; i < all_categories_selected.length; i++) {
                                    all_categories_selected[i] = true;
                                }
                                todolist.setEventsSemiTransparent(mAdapter);
                                todolist.addAllEventToAdapterList(mAdapter);
                                mRecyclerView.scrollToPosition(viewHolder.getAdapterPosition());
                            }
                            break;

                        case ItemTouchHelper.ACTION_STATE_SWIPE:
                            Display mdisp = getWindowManager().getDefaultDisplay();
                            Point mdispSize = new Point();
                            mdisp.getSize(mdispSize);
                            float sX = mdispSize.x / 2 - 50;
                            if (dX < -0.0f) {
                                viewHolder.itemView.setAlpha(1 - ((dX / (2 * (-1))) / sX));
                            } else {
                                viewHolder.itemView.setAlpha(1 - ((dX / 2) / sX));
                            }
                            break;
                    }
                } else {
                    if (dX == 0) {
                        viewHolder.itemView.setAlpha(1);
                    }
                    isEventDraged = false;
                    viewHolder.itemView.setPressed(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            todolist.resetAllSemiTransparentEvents(mAdapter);
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CheckToolbarElevation();
                        }
                    }, 300);
                }
            }
        };
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, RecyclerView.ViewHolder holder) {
                if (shareEvents) {
                    shareEventCallback.eventClicked(position, ((RVAdapter.EventViewHolder) holder).event);
                    return;
                }
                if (!isCardExpandingOrCollapsing()) {
                    RVAdapter.EventViewHolder viewHolder = (RVAdapter.EventViewHolder) holder;
                    viewHolder.cardClicked();
                    closeAllOpenCards(position);
                }
            }
        });
    }

    public void closeAllOpenCards(int position) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (mAdapter.getList().get(i).isExpanded && i != position) {
                RVAdapter.EventViewHolder holder
                        = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    if (holder.event.isExpanded) {
                        holder.cardClicked();
                    }
                }
            }
        }
    }

    public void scrollToCard(int position) {
        if (position == mAdapter.getItemCount() - 1 && mAdapter.getItemCount() > 1) {
            mRecyclerView.smoothScrollToPosition(position);
        }
    }

    public boolean isCardExpandingOrCollapsing() {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            RVAdapter.EventViewHolder holder
                    = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                if (holder.isExpandingOrCollapsing()) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeEvent(int index) {
        todolist.removeEvent(mAdapter, index);
        checkForNotificationUpdate();
        if (todolist.getTodolist().size() == 0) {
            showNothingTodo(true);
        }
        CheckToolbarElevation();
        EventRemovedSnackbar();
    }

    public void actionButtonClicked(View v, Event e) {
        if (!actionButtonAlreadyClicked) {
            actionButtonAlreadyClicked = true;
            dismisslistener = new DialogInterface.OnDismissListener() {
                @Override
                public void onDismiss(DialogInterface dialog) {
                    actionButtonAlreadyClicked = false;
                }
            };
            switch (v.getId()) {
                case R.id.color_button:
                    ColorButtonClicked(e);
                    break;
                case R.id.edit_button:
                    EditButtonClicked(e);
                    break;
                case R.id.alarm_button:
                    AlarmButtonClicked(e);
                    break;
            }
        }
    }

    public void ColorButtonClicked(Event e) {
        eventToColorChange = e;
        categoryWasSelected = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, getDialogTheme());
        builder.setView(inflateColorSelector())
                .setTitle(getString(R.string.choose_a_color))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setOnDismissListener(dismisslistener);
        colorSelectorDialog = builder.create();
        colorSelectorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        colorSelectorDialog.show();
        changeDialogButtonColor(colorSelectorDialog);
    }

    public void EditButtonClicked(final Event e) {
        final LayoutInflater inputDialog_layout = getLayoutInflater();
        final View inputDialog = inputDialog_layout.inflate(R.layout.input_dialog, null);
        final EditText editText = (EditText) inputDialog.findViewById(R.id.edit_text);
        editText.setTextColor(getDialogTextColor());
        editText.setText(e.getWhatToDo());
        editText.setSelection(e.getWhatToDo().length());

        AlertDialog.Builder input_dialog_builder =
                new AlertDialog.Builder(context, getDialogTheme());
        input_dialog_builder.setTitle(getString(R.string.edit_event))
                .setView(inputDialog)
                .setNegativeButton(getString(R.string.cancel), null)
                .setOnDismissListener(dismisslistener)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        e.editWhatToDo(s);
                        checkForNotificationUpdate();
                        mAdapter.itemChanged(mAdapter.getList().indexOf(e));
                        try {
                            todolist.saveData(context);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });

        final AlertDialog input_dialog = input_dialog_builder.create();
        input_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        input_dialog.show();
        changeDialogButtonColor(input_dialog);
    }

    public void AlarmButtonClicked(Event e) {
        if (e.hasAlarm() && !todolist.hasAlarmFired(e)) {
            showAlarmInfoDialog(e);
        } else {
            ShowAlarmDatePicker(e);
        }
    }

    public void FabClicked(View v) {
        if (shareEvents) {
            shareEventCallback.shareEvents();
            return;
        }
        int timeToWait = 0;
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
                timeToWait = 500;
            }
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addEvent();
            }
        }, timeToWait);
    }

    public void addEvent() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_down);
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {/*nothing*/}

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {/*nothing*/}
        });
        fab.startAnimation(anim);

        final LayoutInflater inputDialog_layout = this.getLayoutInflater();
        final View inputDialog = inputDialog_layout.inflate(R.layout.add_event_dialog, null);
        final TextInputEditText editText = (TextInputEditText) inputDialog.findViewById(R.id.edit_text);
        final RadioButton color_rb = (RadioButton) inputDialog.findViewById(R.id.radio_button_color);
        final HorizontalScrollView horizontalScrollView
                = (HorizontalScrollView) inputDialog.findViewById(R.id.color_scroll_view);
        horizontalScrollView.setVisibility(View.GONE);

        final ImageButton[] buttons = getColorButtons(inputDialog);
        final int[] sortedColors = helper.getSortedColors();
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }

        if (helper.getDefaultColorIndex() != 0) {
            int index = 0;
            int defaultColor = helper.getDefaultColorIndex();
            for (int i = 1; i < sortedColors.length; i++) {
                if (defaultColor == sortedColors[i]) {
                    index = i;
                    break;
                }
            }
            buttons[index].setImageDrawable(getButtonForegroundRes(defaultColor));
            selectedColor = index;
        } else {
            selectedColor = 0;
        }
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                int colorIndex = sortedColors[index];
                if (selectedColor != 0 || index == selectedColor) {
                    buttons[selectedColor].setImageResource(android.R.color.transparent);
                }
                if (index != selectedColor) {
                    ImageButton imageButton = (ImageButton) v;
                    imageButton.setImageDrawable(getButtonForegroundRes(colorIndex));
                    selectedColor = index;
                } else {
                    selectedColor = 0;
                }
            }
        };

        Button.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                int colorIndex = sortedColors[index];
                int default_color = helper.getDefaultColorIndex();
                if (default_color != colorIndex) {
                    ImageButton imageButton = (ImageButton) v;
                    imageButton.setImageDrawable(getButtonForegroundRes(colorIndex));
                    if (selectedColor != 0 && selectedColor != index) {
                        buttons[selectedColor].setImageResource(android.R.color.transparent);
                    }
                    selectedColor = index;
                    helper.setDefaultColorIndex(colorIndex);
                    showToast("Default Color set");
                } else {
                    buttons[index].setImageResource(android.R.color.transparent);
                    selectedColor = 0;
                    helper.setDefaultColorIndex(0);
                    showToast("Default Color removed");
                }
                return true;
            }
        };

        for (int i = 1; i < buttons.length; i++) {
            buttons[i].setOnClickListener(onClickListener);
            buttons[i].setOnLongClickListener(onLongClickListener);
        }
        editText.setText("");
        editText.setTextColor(getDialogTextColor());
        if (helper.lightCordColor()) {
            editText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.light_grey));
        } else {
            editText.setHintTextColor(ContextCompat.getColor(getApplicationContext(), R.color.dark_grey));
        }

        color_rb.setTextColor(getDialogTextColor());
        color_rb.setChecked(false);
        color_rb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                horizontalScrollView.setVisibility(View.VISIBLE);
            }
        });

        final String hint = getAddEventHint();
        editText.setHint(hint);

        AlertDialog.Builder input_dialog_builder
                = new AlertDialog.Builder(context, getDialogTheme());
        input_dialog_builder.setTitle(getString(R.string.add_event))
                .setView(inputDialog)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (todolist.getTodolist().size() == 0) {
                            removeNothingTodo();
                        }
                        String s = editText.getText().toString();
                        if (s.length() == 0) {
                            s = hint;
                        }
                        //int color = selectedColor;
                        int color = sortedColors[selectedColor];
                        boolean[] possible_colors
                                = new boolean[((boolean[]) settings.get("selected_categories")).length];
                        for (int i = 1; i < possible_colors.length; i++) {
                            if (settings.getCategory(i) || !todolist.doesCategoryContainEvents(i)) {
                                possible_colors[i] = true;
                            }
                        }
                        Event e = new Event(s, 0, color, 0, 0, possible_colors);
                        todolist.addEvent(mAdapter, e);
                        settings.setCategory(e.getColor(), true);
                        closeAllOpenCards(mAdapter.getItemCount());
                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                        mRecyclerView.scrollToPosition(mAdapter.getList().indexOf(e));
                        checkForNotificationUpdate();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        fab.setVisibility(View.VISIBLE);
                        fab.startAnimation(AnimationUtils.loadAnimation(context, R.anim.fab_scale_up));
                    }
                });
        addEventDialog = input_dialog_builder.create();
        //addEventDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                addEventDialog.show();
                changeDialogButtonColor(addEventDialog);
            }
        }, 100);
    }

    public Todolist getTodolist() {
        return todolist;
    }

    public void removeNothingTodo() {
        final ImageView illustration = (ImageView) findViewById(R.id.nothing_todo);
        if (illustration.getVisibility() == View.GONE) {
            return;
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    illustration.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            illustration.startAnimation(fadeOut);
        }
    }

    public String getAddEventHint() {
        Random rand = new Random();
        String string;
        switch (rand.nextInt(3)) {
            case 0:
                string = getString(R.string.do_homework);
                break;
            case 1:
                string = getString(R.string.clean_kitchen);
                break;
            default:
                string = getString(R.string.do_laundry);
                break;
        }
        return string;
        //return "test";
    }

    public void showAlarmInfoDialog(final Event e) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis((long) e.getAlarm().get("time"));

        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
        boolean timeFormat = dateFormat.is24HourFormat(this);

        int Hour = calendar.get(Calendar.HOUR_OF_DAY);

        String am_pm = "";

        if (!timeFormat) {
            am_pm = " am";
        }

        if (Hour > 12 && !timeFormat) {
            Hour = Hour - 12;
            am_pm = " pm";
        }

        int Minutes = calendar.get(Calendar.MINUTE);
        Calendar currentTime = Calendar.getInstance(TimeZone.getDefault());
        String s;
        String month = calendar.getDisplayName(Calendar.MONTH, Calendar.LONG, getResources().getConfiguration().locale);
        if (calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH)) {
            if (calendar.get(Calendar.DATE) == currentTime.get(Calendar.DATE)) {
                s = getString(R.string.today);
            } else if (currentTime.getTimeInMillis() + 24 * 60 * 60 * 1000 > calendar.getTimeInMillis()) {
                s = getString(R.string.tomorrow);
            } else {
                s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + month + " " + calendar.get(Calendar.YEAR);
            }
        } else {
            s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + month + " " + calendar.get(Calendar.YEAR);
        }

        String content = "<b>" + s + " "
                + getString(R.string.at)
                + " " + Hour + ":"
                + String.format("%02d", Minutes) + am_pm + "</b>";

        final LayoutInflater layoutInflater = this.getLayoutInflater();
        final View alarm_info_dialog = layoutInflater.inflate(R.layout.alarm_info_dialog, null);

        final TextView alarmInfoText1 = (TextView) alarm_info_dialog.findViewById(R.id.alarmInfoText1);
        alarmInfoText1.setText(Html.fromHtml(getString(R.string.alarm_scheduled_for)));
        alarmInfoText1.setTextColor(getDialogTextColor());

        final TextView alarmInfoText2 = (TextView) alarm_info_dialog.findViewById(R.id.alarmInfoText2);
        alarmInfoText2.setText(Html.fromHtml(content));
        alarmInfoText2.setTextColor(getDialogTextColor());

        final AppCompatCheckBox checkbox = (AppCompatCheckBox) alarm_info_dialog.findViewById(R.id.checkbox);
        checkbox.setSupportButtonTintList(getColorStateListForCheckbox());
        checkbox.setTextColor(getDialogTextColor());
        checkbox.setChecked((boolean) e.getAlarm().get("repeating"));

        final ScrollView certain_days = (ScrollView) alarm_info_dialog.findViewById(R.id.certain_days);
        if ((int) e.getAlarm().get("repeatMode") != 3) {
            certain_days.setVisibility(View.GONE);
        }

        final LinearLayout numberPickers = (LinearLayout) alarm_info_dialog.findViewById(R.id.numberPickers);
        if ((int) e.getAlarm().get("repeatMode") != 4) {
            numberPickers.setVisibility(View.GONE);
        }

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                (int) DPCalc.dpIntoPx(getResources(), 100));

        final MaterialNumberPicker numberPicker1 = new MaterialNumberPicker.Builder(context)
                .minValue(1)
                .maxValue(25)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(getDialogTextColor())
                .textSize(16)
                .enableFocusability(false)
                .wrapSelectorWheel(false)
                .build();
        numberPicker1.setLayoutParams(params);

        final MaterialNumberPicker numberPicker2 = new MaterialNumberPicker.Builder(context)
                .minValue(1)
                .maxValue(6)
                .defaultValue(1)
                .backgroundColor(Color.TRANSPARENT)
                .separatorColor(Color.TRANSPARENT)
                .textColor(getDialogTextColor())
                .textSize(16)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .formatter(new NumberPicker.Formatter() {
                    @Override
                    public String format(int i) {
                        switch (i) {
                            case 1:
                                return getString(R.string.hours);
                            case 2:
                                return getString(R.string.days);
                            case 3:
                                return getString(R.string.weeks);
                            //needed for wheelWrap
                            case 4:
                                return getString(R.string.hours);
                            case 5:
                                return getString(R.string.days);
                            case 6:
                                return getString(R.string.weeks);
                        }
                        return "";
                    }
                })
                .build();
        numberPicker2.setLayoutParams(params);

        numberPickers.addView(numberPicker1);
        numberPickers.addView(numberPicker2);

        if ((int) e.getAlarm().get("repeatMode") == 4) {
            numberPicker1.setValue((int) e.getAlarm().get("numberPicker1_value"));
            numberPicker2.setValue((int) e.getAlarm().get("numberPicker2_value"));
        }

        final String[] state = {getString(R.string.daily),
                getString(R.string.weekly),
                getString(R.string.monthly),
                getString(R.string.certain_days),
                getString(R.string.custom)};

        final AppCompatSpinner spinner = (AppCompatSpinner) alarm_info_dialog.findViewById(R.id.spinner);
        spinner.setEnabled((boolean) e.getAlarm().get("repeating"));

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, state);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSupportBackgroundTintList(getColorStateListForSpinner());
        final AdapterView.OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                boolean showCustomIntervall = false;
                boolean showCertainDays = false;
                if (checkbox.isChecked()) {
                    showCertainDays = i == 3;
                    showCustomIntervall = i == 4;
                }
                hideOrShowView(certain_days, showCertainDays);
                hideOrShowView(numberPickers, showCustomIntervall);
                colorSpinnerTextView(((TextView) spinner.getSelectedView()), checkbox.isChecked());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        spinner.setOnItemSelectedListener(onItemSelectedListener);
        spinner.setSelection((int) e.getAlarm().get("repeatMode"));

        checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                onItemSelectedListener.onItemSelected(null, null, spinner.getSelectedItemPosition(), 0);
                spinner.setEnabled(b);
                colorSpinnerTextView(((TextView) spinner.getSelectedView()), b);
            }
        });

        final Button[] buttons = getWeekButtons(alarm_info_dialog);
        for (int i = 0; i < buttons.length; i++) {
            int color = helper.get("fab_color");
            int textcolor = helper.get("fab_textcolor");
            if (helper.get("fab_color") == ContextCompat.getColor(MainActivity.this, R.color.white)) {
                color = ContextCompat.getColor(MainActivity.this, R.color.grey);
                textcolor = ContextCompat.getColor(MainActivity.this, R.color.white);
            }

            if (e.getAlarm().getCertainDay(i)) {
                buttons[i].setTextColor(textcolor);
                buttons[i].getBackground().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            } else {
                buttons[i].setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_text_color));
                buttons[i].getBackground().setColorFilter(
                        ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
            }
        }

        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView clickedButton = (TextView) v;
                boolean b;
                if (clickedButton.getCurrentTextColor() == helper.get("fab_textcolor")) {
                    clickedButton.setTextColor(ContextCompat.getColor(MainActivity.this, R.color.light_text_color));
                    clickedButton.getBackground().setColorFilter(
                            ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
                    b = false;
                } else {
                    clickedButton.setTextColor(helper.get("fab_textcolor"));
                    clickedButton.getBackground().setColorFilter(helper.get("fab_color"), PorterDuff.Mode.SRC_IN);
                    b = true;
                }
                e.getAlarm().setCertainDay(getWeekButtonsIndexById(v), b);
            }
        };

        for (int i = 0; i < buttons.length; i++) {
            buttons[i].setOnClickListener(onClickListener);
        }

        alarmInfoDialog = new AlertDialog.Builder(context, getDialogTheme())
                .setTitle(getString(R.string.alarm))
                .setView(alarm_info_dialog)
                .setOnDismissListener(dismisslistener)
                .setNeutralButton(getString(R.string.edit_time), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ShowAlarmDatePicker(e);
                    }
                })
                .setNegativeButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAlarm(e);
                    }
                })
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (checkbox.isChecked()) {
                            int index = spinner.getSelectedItemPosition();
                            e.getAlarm().setRepeating(index);
                            if (index == 4) {
                                int multiplier = 0;
                                switch (numberPicker2.getValue()) {
                                    case 1:
                                        multiplier = 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 1);
                                        break;
                                    case 2:
                                        multiplier = 24 * 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 2);
                                        break;
                                    case 3:
                                        multiplier = 7 * 24 * 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 3);
                                        break;
                                    //needed for wheelwrapping
                                    case 4:
                                        multiplier = 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 1);
                                        break;
                                    case 5:
                                        multiplier = 24 * 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 2);
                                        break;
                                    case 6:
                                        multiplier = 7 * 24 * 60 * 60 * 1000;
                                        e.getAlarm().set("numberPicker2_value", 3);
                                        break;
                                }
                                e.getAlarm().set("custom_intervall", numberPicker1.getValue() * multiplier);
                                e.getAlarm().set("numberPicker1_value", numberPicker1.getValue());
                            }
                        } else {
                            e.getAlarm().unRepeat();
                        }
                        try {
                            todolist.saveData(MainActivity.this);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                })
                .create();
        alarmInfoDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        alarmInfoDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        alarmInfoDialog.show();
        changeDialogButtonColor(alarmInfoDialog);
    }

    public ColorStateList getColorStateListForSpinner() {
        int color = helper.get("fab_color");
        if (helper.get("fab_color") == ContextCompat.getColor(MainActivity.this, R.color.white)) {
            color = ContextCompat.getColor(MainActivity.this, R.color.grey);
        }

        int color_grey = ContextCompat.getColor(context, R.color.grey);
        final int[][] states_spinner = new int[3][];
        final int[] colors_spinner = new int[3];
        int k_spinner = 0;
        // Disabled state
        states_spinner[k_spinner] = new int[]{-android.R.attr.state_enabled};
        colors_spinner[k_spinner] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
        k_spinner++;
        states_spinner[k_spinner] = new int[]{android.R.attr.state_checked};
        colors_spinner[k_spinner] = color;
        k_spinner++;
        // Default enabled state
        states_spinner[k_spinner] = new int[0];
        colors_spinner[k_spinner] = color;

        return new ColorStateList(states_spinner, colors_spinner);
    }

    public ColorStateList getColorStateListForCheckbox() {
        int color = helper.get("fab_color");
        if (helper.get("fab_color") == ContextCompat.getColor(MainActivity.this, R.color.white)) {
            color = ContextCompat.getColor(MainActivity.this, R.color.grey);
        }

        int color_grey = ContextCompat.getColor(context, R.color.grey);
        final int[][] states = new int[3][];
        final int[] colors = new int[3];
        int k = 0;
        // Disabled state
        states[k] = new int[]{-android.R.attr.state_enabled};
        colors[k] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
        k++;
        states[k] = new int[]{android.R.attr.state_checked};
        colors[k] = color;
        k++;
        // Default enabled state
        states[k] = new int[0];
        colors[k] = color_grey;
        return new ColorStateList(states, colors);
    }

    public void hideOrShowView(View v, boolean show) {
        if (show) {
            v.setVisibility(View.VISIBLE);
        } else {
            v.setVisibility(View.GONE);
        }
    }

    public Button[] getWeekButtons(View layout) {
        Button[] buttons = new Button[7];
        buttons[0] = (Button) layout.findViewById(R.id.monday_button);
        buttons[1] = (Button) layout.findViewById(R.id.tuesday_button);
        buttons[2] = (Button) layout.findViewById(R.id.wednesday_button);
        buttons[3] = (Button) layout.findViewById(R.id.thursday_button);
        buttons[4] = (Button) layout.findViewById(R.id.friday_button);
        buttons[5] = (Button) layout.findViewById(R.id.saturday_button);
        buttons[6] = (Button) layout.findViewById(R.id.sunday_button);
        return buttons;
    }

    public int getWeekButtonsIndexById(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.monday_button:
                return 0;
            case R.id.tuesday_button:
                return 1;
            case R.id.wednesday_button:
                return 2;
            case R.id.thursday_button:
                return 3;
            case R.id.friday_button:
                return 4;
            case R.id.saturday_button:
                return 5;
            case R.id.sunday_button:
                return 6;
        }
        return 0;
    }

    public void colorSpinnerTextView(TextView textView, boolean enabled) {
        if (enabled) {
            textView.setTextColor(getDialogTextColor());
        } else {
            textView.setTextColor(Color.argb(72,
                    Color.red(ContextCompat.getColor(context, R.color.grey)),
                    Color.green(ContextCompat.getColor(context, R.color.grey)),
                    Color.blue(ContextCompat.getColor(context, R.color.grey))));
        }
    }

    public void ShowAlarmTimePicker(final Event e, final Calendar alarmDate) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        calendar.add(Calendar.MINUTE, 1);
        int theme;
        if (helper.lightCordColor()) {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }

        android.text.format.DateFormat dateFormat = new android.text.format.DateFormat();
        boolean timeFormat = dateFormat.is24HourFormat(this);

        TimePickerDialog timePickerDialog = new TimePickerDialog(context, theme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alarmDate.set(Calendar.MINUTE, minute);
                        alarmDate.set(Calendar.SECOND, 0);
                        alarmDate.set(Calendar.MILLISECOND, 0);
                        if (alarmDate.getTimeInMillis() < System.currentTimeMillis()) {
                            showToast("Your Date lies in the past");
                        } else {
                            setAlarm(alarmDate.getTimeInMillis(), e);
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), timeFormat);
        timePickerDialog.setOnDismissListener(dismisslistener);
        timePickerDialog.show();
    }

    public void ShowAlarmDatePicker(final Event e) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int theme;
        if (helper.lightCordColor()) {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        DatePickerDialog datePickerDialog =
                new DatePickerDialog(context, theme, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        calendar.set(Calendar.YEAR, year);
                        calendar.set(Calendar.MONTH, monthOfYear);
                        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        calendar.set(Calendar.HOUR_OF_DAY, 0);
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        if (calendar.getTimeInMillis()
                                + 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                            showToast("Your Date lies in the past");
                        } else {
                            ShowAlarmTimePicker(e, calendar);
                        }
                    }
                }, calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(dismisslistener);
        datePickerDialog.show();
    }

    public void setAlarm(long alarmTime, Event e) {
        long id = e.getId();
        setAlarm(alarmTime, id);
        e.setAlarm(id, alarmTime);
        showSnackbar(getString(R.string.alarm_was_set_for) + " "
                + e.getWhatToDo() + " " + getString(R.string.hinzugefuegt));
    }

    public void setAlarm(long alarmTime, long id) {
        AlarmManager mAlarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        intent.putExtra("EventId", id);
        intent.setAction("ALARM");
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);
        mSilenceAllAlarms.setEnabled(true);
        mSilenceAllAlarms.getActionView().setEnabled(true);
    }

    public void removeAlarm(Event e) {
        long id = e.getId();
        e.removeAlarm();
        removeAlarm(id);
        showSnackbar(getString(R.string.alarm_removed));
    }

    public void removeAlarm(long id) {
        AlarmManager mAlarmManager
                = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, BroadcastReceiver.class);
        intent.putExtra("EventId", id);
        intent.setAction("ALARM");
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(context, (int) id, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        if (!todolist.isAlarmScheduled()) {
            mSilenceAllAlarms.setEnabled(false);
            mSilenceAllAlarms.getActionView().setEnabled(false);
        }
    }

    public void ColorButtonClicked(View v) {
        int[] sortedColors = helper.getSortedColorsColorSelector();
        int color_index = sortedColors[getColorIndexByButtonId(v.getId())];
        if (categoryWasSelected) {
            if (!settings.getCategory(color_index)) {
                ImageButton imageButton = (ImageButton) v;
                imageButton.setImageDrawable(getButtonForegroundRes(color_index));
                settings.setCategory(color_index, true);
            } else {
                ImageButton imageButton = (ImageButton) v;
                imageButton.setImageResource(android.R.color.transparent);
                settings.setCategory(color_index, false);
            }
        } else {
            if (colorSelectorDialog != null) {
                colorSelectorDialog.dismiss();
            }
            if (color_index != eventToColorChange.getColor()) {
                eventToColorChange.setColor(color_index);
                //mAdapter.itemChanged(mAdapter.getList().indexOf(eventToColorChange));
                int index = todolist.getAdapterListPosition(mAdapter, eventToColorChange);
                ((RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(index))
                        .changeCardColorAnim(MainActivity.this, helper.getEventColor(color_index), helper.getEventTextColor(color_index));
                if (!settings.getCategory(color_index)) {
                    int number_of_event_with_color_index = 0;
                    for (int i = 0; i < todolist.getTodolist().size(); i++) {
                        if (todolist.getTodolist().get(i).getColor() == color_index) {
                            number_of_event_with_color_index++;
                        }
                    }
                    if (number_of_event_with_color_index == 1) {
                        settings.setCategory(color_index, true);
                    }
                }
                eventToColorChange = null;
            }
        }
    }

    public void RestoreLastDoneEventClicked() {
        mDrawerLayout.closeDrawers();
        Event e = todolist.getLastRemovedEvent();
        if (e != null) {
            if (mTextView != null) {
                mCoordinatorLayout.removeView(mTextView);
                mTextView = null;
            }
            removeNothingTodo();
            todolist.restoreLastRemovedEvent();
            settings.setCategory(e.getColor(), true);
            todolist.addOrRemoveEventFromAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mAdapter.getList().indexOf(e));
            checkForNotificationUpdate();

            handler.postDelayed(new Runnable() {
                public void run() {
                    CheckToolbarElevation();
                }
            }, 100);
        } else {
            showSnackbar(getString(R.string.you_have_no_event_to_restore));
        }
    }

    public void SilenceAlarmsClicked() {
        settings.toggle("vibrate");

        if (!(boolean) settings.get("vibrate")) {
            showSnackbar(getString(R.string.all_alarm_are_now_silent));
        } else {
            showSnackbar(getString(R.string.now_the_Phone_will_vibrate_when_alarms_are_fired));
        }
    }

    public void AutoSyncClicked(boolean isChecked) {
        settings.set("autoSync", isChecked);
    }

    public void SelectCategoryClicked() {
        mDrawerLayout.closeDrawers();
        categoryWasSelected = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context, getDialogTheme());
        builder1.setView(inflateCategorySelector())
                .setTitle(getString(R.string.choose_a_category))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                        if (mAdapter.getList().size() == 0 && todolist.getTodolist().size() != 0) {
                            showSnackbar(getString(R.string.no_category_selected));
                        }
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                CheckToolbarElevation();
                            }
                        }, 100);
                    }
                });
        colorSelectorDialog = builder1.create();
        colorSelectorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        colorSelectorDialog.show();
        changeDialogButtonColor(colorSelectorDialog);
    }

    public void SelectThemeClicked() {
        mDrawerLayout.closeDrawers();
        newTheme = true;
        Intent intent = new Intent(this, ThemeActivity.class);
        startActivity(intent);
    }

    public AlertDialog changeDialogButtonColor(AlertDialog dialog) {
        int color = helper.get("fab_color");
        if (helper.get("fab_color") == ContextCompat.getColor(MainActivity.this, R.color.white)) {
            color = ContextCompat.getColor(MainActivity.this, R.color.grey);
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

    public void ShowNotificationToggleClicked() {
        settings.toggle("showNotification");

        if (!(boolean) settings.get("showNotification")) {
            showSnackbar(getString(R.string.general_notification_is_hidden));
            cancelNotification();
        } else {
            showSnackbar(getString(R.string.general_notification_is_shown));
        }
        checkForNotificationUpdate();
    }

    public void InfoButtonClicked() {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(intent);
    }

    public void showNothingTodo(boolean withAnim) {
        /*if(ntd_anim == null){
            ntd_anim = AnimatedVectorDrawableCompat.create(context,
                    R.drawable.ic_ntd_illustration_animatable);
        }*/

        final ImageView illustration = (ImageView) findViewById(R.id.nothing_todo);
        illustration.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_ntd_illustration_vector));
        illustration.setColorFilter(helper.get("cord_textcolor"), PorterDuff.Mode.SRC_IN);
        //illustration.setImageDrawable(ntd_anim);
        //ntd_anim.setTint(helper.get("cord_textcolor"));
        illustration.setAlpha(0.5f);
        if (!withAnim) {
            illustration.setVisibility(View.VISIBLE);
            return;
        }
        if (illustration.getVisibility() != View.VISIBLE) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
            anim.setStartOffset(300);
            anim.setDuration(500);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    illustration.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {/*nothing*/}

                @Override
                public void onAnimationRepeat(Animation animation) {/*nothing*/}
            });
            illustration.startAnimation(anim);
        }
    }

    public void ntdClicked(View v){
        if(ntd_anim != null){
            ntd_anim.start();
            showToast("Anim start");
        }
    }

    public void DoneSyncingData(ArrayList<Long> eventsToUpdate) {

        checkForNotificationUpdate();
        if (todolist.getTodolist().size() != 0) {
            removeNothingTodo();
        } else {
            showNothingTodo(true);
        }

        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (!todolist.isEventInTodolist(mAdapter.getList().get(i).getId())) {
                mAdapter.removeItem(i);
            }
        }

        for (int i = 0; i < eventsToUpdate.size(); i++) {
            Event e = todolist.getEventById(eventsToUpdate.get(i));
            if (todolist.isEventInAdapterList(mAdapter, e)) {
                int index = todolist.getAdapterListPosition(mAdapter, e);
                mAdapter.itemChanged(index);
            }
        }

        for (int i = 0; i < mAdapter.getList().size(); i++) {
            int newIndex = todolist.getAdapterListPosition(mAdapter, mAdapter.getList().get(i));
            if (i != newIndex) {
                if (newIndex < mAdapter.getList().size()) {
                    mAdapter.itemMoved(i, newIndex);
                } else {
                    mAdapter.itemMoved(i, mAdapter.getList().size() - 1);
                }
                showToast("eventMoved()");
            }
        }

        boolean[] selected_categories = (boolean[]) settings.get("selected_categories");
        for (int i = 0; i < selected_categories.length; i++) {
            selected_categories[i] = true;
        }

        todolist.addOrRemoveEventFromAdapter(mAdapter);

        try {
            todolist.saveData(MainActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        showToast("DoneSyncingData");
    }

    public void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet) {
        for (int i = 0; i < alarmsToCancel.size(); i++) {
            long id = alarmsToCancel.get(i);
            removeAlarm((int) id);
            showToast("removeAlarm");
        }

        for (int i = 0; i < alarmsToSet.size(); i++) {
            long time = (long) alarmsToSet.get(i).get("time");
            long alarmId = (long) alarmsToSet.get(i).get("id");
            //Log.d("MainActivity", "alarmId: " + String.valueOf(alarmId));
            int id = (int) alarmId;
            setAlarm(time, id);
            showToast("setAlarm");
        }
    }

    public void CheckToolbarElevation() {
        int position = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();

        if (helper.get("toolbar_color") != helper.get("cord_color")) {
            elevateToolbar();
            return;
        } else {
            if (position == 0 || position == RecyclerView.NO_POSITION) {
                deelevateToolbar();
                return;
            }
        }

        if (position == 0 || mAdapter.getList().size() == 0) {
            deelevateToolbar();
        } else {
            elevateToolbar();
        }
    }

    public void elevateToolbar() {
        if (toolbarElevated) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.toolbar_raise);
        set.setTarget(mToolbar);
        set.start();
        toolbarElevated = true;
    }

    public void deelevateToolbar() {
        if (!toolbarElevated) {
            return;
        }
        AnimatorSet set = (AnimatorSet) AnimatorInflater.loadAnimator(MainActivity.this, R.animator.toolbar_lower);
        set.setTarget(mToolbar);
        set.start();
        toolbarElevated = false;
    }


    public void ChangeColorOfToolbarDrawerIcon(int color) {
        if (drawerIcon != null) {
            ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_IN);
        } else {
            for (int i = 0; i < mToolbar.getChildCount(); i++) {
                if (mToolbar.getChildAt(i) instanceof ImageView) {
                    drawerIcon = mToolbar.getChildAt(i);
                    ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_IN);
                }
            }
        }
    }

    public int getDialogTextColor() {
        if (helper.lightCordColor()) {
            return ContextCompat.getColor(context, R.color.light_text_color);
        }
        return ContextCompat.getColor(context, R.color.dark_text_color);
    }

    public int getDialogTheme() {
        if (helper.lightCordColor()) {
            return R.style.DialogTheme_light;
        }
        return R.style.DialogTheme_dark;
    }

    public int getColorIndexByButtonId(int button_id) {
        switch (button_id) {
            case R.id.color1_button:
                return 1;
            case R.id.color2_button:
                return 2;
            case R.id.color3_button:
                return 3;
            case R.id.color4_button:
                return 4;
            case R.id.color5_button:
                return 5;
            case R.id.color6_button:
                return 6;
            case R.id.color7_button:
                return 7;
            case R.id.color8_button:
                return 8;
            case R.id.color9_button:
                return 9;
            case R.id.color10_button:
                return 10;
            case R.id.color11_button:
                return 11;
            default:
                return 12;
        }
    }

    public View inflateCategorySelector() {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.color_selector, null);
        final ImageButton[] buttons = getColorButtons(layout);

        boolean[] selected_categories
                = (boolean[]) settings.get("selected_categories");
        int[] sortedColors = helper.getSortedColorsColorSelector();
        for (int i = 1; i < selected_categories.length; i++) {
            int colorIndex = sortedColors[getColorIndexByButtonId(buttons[i].getId())];
            if (!todolist.doesCategoryContainEvents(colorIndex)) {
                buttons[i].setEnabled(false);
                buttons[i].getBackground().setAlpha(60);
            } else if (selected_categories[colorIndex]) {
                buttons[i].setImageDrawable(getButtonForegroundRes(colorIndex));
            }
        }
        return layout;
    }

    public View inflateColorSelector() {
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.color_selector, null);
        final ImageButton[] buttons = getColorButtons(layout);
        /*int [] sortedColors = helper.getSortedColorsColorSelector();
        for (int i = 1; i < buttons.length; i++) {
            //buttons[i].getBackground().setColorFilter(helper.getEventColor(i), PorterDuff.Mode.SRC_IN);
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }*/
        return layout;
    }

    public ImageButton[] getColorButtons(View layout) {
        final ImageButton[] buttons = new ImageButton[13];
        buttons[1] = (ImageButton) layout.findViewById(R.id.color1_button);
        buttons[2] = (ImageButton) layout.findViewById(R.id.color2_button);
        buttons[3] = (ImageButton) layout.findViewById(R.id.color3_button);
        buttons[4] = (ImageButton) layout.findViewById(R.id.color4_button);
        buttons[5] = (ImageButton) layout.findViewById(R.id.color5_button);
        buttons[6] = (ImageButton) layout.findViewById(R.id.color6_button);
        buttons[7] = (ImageButton) layout.findViewById(R.id.color7_button);
        buttons[8] = (ImageButton) layout.findViewById(R.id.color8_button);
        buttons[9] = (ImageButton) layout.findViewById(R.id.color9_button);
        buttons[10] = (ImageButton) layout.findViewById(R.id.color10_button);
        buttons[11] = (ImageButton) layout.findViewById(R.id.color11_button);
        buttons[12] = (ImageButton) layout.findViewById(R.id.color12_button);

        int[] sortedColors = helper.getSortedColorsColorSelector();
        for (int i = 1; i < buttons.length; i++) {
            //buttons[i].getBackground().setColorFilter(helper.getEventColor(i), PorterDuff.Mode.SRC_IN);
            buttons[i].getBackground().setColorFilter(helper.getEventColor(sortedColors[i]), PorterDuff.Mode.SRC_IN);
        }
        return buttons;
    }

    public Drawable getButtonForegroundRes(int color_index) {
        Drawable d = ResourcesCompat.getDrawable(getResources(),
                R.drawable.ic_selected_light, null).getConstantState().newDrawable().mutate();
        d.setColorFilter(helper.getEventTextColor(color_index), PorterDuff.Mode.SRC_IN);
        return d;
    }

    public void showSnackbar(String content) {
        snackbar = Snackbar.make(mCoordinatorLayout, content, Snackbar.LENGTH_LONG);
        //disabe swipeToDissmiss
        snackbar.getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackbar.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setBehavior(null);
                return true;
            }
        });
        snackbar.show();
    }

    public void EventRemovedSnackbar() {
        snackbar = Snackbar.make(mCoordinatorLayout,
                getString(R.string.event_removed), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(helper.get("fab_color"))
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RestoreLastDoneEventClicked();
                    }
                });
        //disabe swipeToDissmiss
        snackbar.getView().getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                snackbar.getView().getViewTreeObserver().removeOnPreDrawListener(this);
                ((CoordinatorLayout.LayoutParams) snackbar.getView().getLayoutParams()).setBehavior(null);
                return true;
            }
        });
        snackbar.show();
    }

    public void shareTodosClicked() {
        if (shareEvents) {
            shareEventCallback.cancel();
            return;
        }

        showImportDialog();

        FabShareAnim(true);

        mSwipeRefreshLayout.setEnabled(false);

        todolist.addAllEventToAdapterList(mAdapter);
        todolist.setAllEventsSemitransparent(mAdapter);

        shareEvents = true;
        shareEventCallback = new ShareEventCallback() {
            @Override
            public void eventClicked(int index, Event e) {
                e.semiTransparent = !e.semiTransparent;
                mAdapter.itemChanged(index);
            }

            @Override
            public void shareEvents() {
                FabShareAnim(false);

                shareEvents = false;
                shareEventCallback = null;

                ArrayList<Event> eventsToShare = new ArrayList<>();

                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    if (!mAdapter.getList().get(i).semiTransparent) {
                        eventsToShare.add(mAdapter.getList().get(i));
                    }
                    mAdapter.getList().get(i).semiTransparent = false;
                    mAdapter.itemChanged(i);

                }
                todolist.addOrRemoveEventFromAdapter(mAdapter);

                MainActivity.this.shareEvents(eventsToShare);
            }

            @Override
            public void cancel() {
                FabShareAnim(false);

                if ((boolean) settings.get("signedIn")) {
                    mSwipeRefreshLayout.setEnabled(true);
                }

                shareEvents = false;
                shareEventCallback = null;

                for (int i = 0; i < mAdapter.getList().size(); i++) {
                    mAdapter.getList().get(i).semiTransparent = false;
                    mAdapter.itemChanged(i);

                }
                todolist.addOrRemoveEventFromAdapter(mAdapter);
            }
        };
    }

    public void showImportDialog() {
        if ((boolean) settings.get("importTutorialDialogShown")) {
            return;
        }
        settings.set("importTutorialDialogShown", true);

        String content = getString(R.string.import_dialog_content);

        AlertDialog dialog
                = new AlertDialog.Builder(MainActivity.this, getDialogTheme())
                .setMessage(content)
                .setPositiveButton(getString(R.string.ok), null)
                .create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void showSyncExperimentalFeatureDialog() {
        String content = getString(R.string.signin_dialog_experimental_feature);

        AlertDialog dialog
                = new AlertDialog.Builder(MainActivity.this, getDialogTheme())
                .setTitle(getString(R.string.experimental_feature))
                .setMessage(content)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        signIn();
                    }
                })
                .setNegativeButton(getString(R.string.cancel), null)
                .create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void FabShareAnim(boolean b) {
        int draw_res = R.drawable.ic_send_white_24dp;
        if (!b) {
            draw_res = R.drawable.ic_add;
        }
        final int draw_res_final = draw_res;

        final Animation anim1
                = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_scale_down);
        final Animation anim2
                = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_scale_up);

        anim1.setDuration(300);
        anim2.setDuration(300);

        anim1.setFillAfter(true);
        anim2.setFillAfter(true);

        anim1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setImageResource(draw_res_final);
                mFab.getDrawable().setTint(helper.get("fab_textcolor"));
                mFab.startAnimation(anim2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {/*nothing*/}
        });
        mFab.startAnimation(anim1);
    }

    public void shareEvents(ArrayList<Event> eventsToShare) {
        String data;
        try {
            data = todolist.getShareFileData(eventsToShare);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }

        String filename = "SharedTheme.txt";
        generateFileToShare(filename, data);

        File f = new File(getFilesDir().getAbsolutePath(), filename);
        Uri uri = FileProvider.getUriForFile(this,
                "com.koller.lukas.todolist.fileprovider", f);

        Intent shareIntent = ShareCompat.IntentBuilder.from(MainActivity.this)
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

    public void setOverflowButtonColor(int color) {
        if (overflowIcon == null) {
            overflowIcon = mToolbar.getOverflowIcon();
            if (overflowIcon != null) {
                overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
            }
        } else {
            overflowIcon.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        }
    }

    public void colorShareIcon(int color) {
        if (shareIcon != null) {
            //shareIcon.getIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            this.invalidateOptionsMenu();
        }
    }

    public void updateWidget() {
        Intent intent = new Intent(this, WidgetProvider_List.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(MainActivity.this).getAppWidgetIds(new ComponentName(context, WidgetProvider_List.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void checkEventRemoved() {
        ArrayList<Long> eventsToRemove;
        try {
            eventsToRemove = todolist.EventRemoved(this);
            for (int i = 0; i < eventsToRemove.size(); i++) {
                removeEventNotifDoneButton(eventsToRemove.get(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeEventNotifDoneButton(long id) {
        Event e = todolist.getEventById(id);
        if (e != null) {
            if (todolist.isEventInAdapterList(mAdapter, e)) {
                removeEvent(todolist.getIndexOfEventInAdapterListById(mAdapter, id));
            } else {
                todolist.removeEvent(e);
            }
        }
    }

    public void onImportIntent(String data) {
        try {
            new JSONObject(data);
        } catch (JSONException e) {
            showImportEvents(data, true);
            return;
        }

        importTheme(data);
    }

    public void importTheme(String data) {
        ArrayList<Event> colors = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Event e = new Event("Color" + i, 0, i, 0, 0, null);
            colors.add(e);
        }

        final ThemeHelper importHelper;
        try {
            JSONObject json = new JSONObject(data);
            importHelper = new ThemeHelper(json, MainActivity.this);
        } catch (JSONException e) {
            //e.printStackTrace();
            Toast.makeText(MainActivity.this, "Error trying to import theme!", Toast.LENGTH_SHORT).show();
            return;
        }

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.import_theme_dialog, null);

        ListView list = (ListView) layout.findViewById(R.id.import_theme_list);
        ImportListViewAdapter adapter = new ImportListViewAdapter(MainActivity.this, colors, importHelper);
        adapter.eventsImport = false;
        list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.import_theme_fab);
        CardView card = (CardView) layout.findViewById(R.id.import_theme_card);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.import_theme_toolbar);
        View statusBar = layout.findViewById(R.id.import_theme_statusbar);

        fab.setBackgroundTintList(ColorStateList.valueOf(importHelper.get("fab_color")));
        fab.getDrawable().setTint(importHelper.get("fab_textcolor"));
        card.setCardBackgroundColor(importHelper.get("cord_color"));

        toolbar.setBackgroundColor(importHelper.get("toolbar_color"));
        toolbar.setTitleTextColor(importHelper.get("toolbar_textcolor"));
        toolbar.setNavigationIcon(null);

        if (importHelper.get("cord_color") != importHelper.get("toolbar_color")) {
            toolbar.setElevation(DPCalc.dpIntoPx(getResources(), 4));

            statusBar.setBackgroundColor(importHelper.get("toolbar_color"));
        }

        importDialog = new AlertDialog.Builder(context, getDialogTheme())
                .setView(layout)
                .setTitle("Import Theme")
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        importHelper.saveData();
                        newTheme = true;
                        initTheme();
                    }
                })
                .setOnDismissListener(dismisslistener)
                .create();
        importDialog.show();
        changeDialogButtonColor(importDialog);
    }

    public void showImportEvents(String data, boolean jsonArray) {
        if (data == null || data.equals("")) {
            return;
        }
        final ArrayList<Event> events = new ArrayList<>();
        if (jsonArray) {
            JSONArray array;
            try {
                array = new JSONArray(data);
                for (int i = 0; i < array.length(); i++) {
                    Event e = new Event(array.getJSONObject(i));
                    events.add(e);
                }
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
        } else {
            String lines[] = data.split("\\r?\\n");
            for (int i = 0; i < lines.length; i++) {
                Event e = new Event(lines[i], 0, 0, 0, 0, null);
                events.add(e);
            }
        }

        if (events.size() == 0) {
            return;
        }

        LayoutInflater layoutInflater = this.getLayoutInflater();
        View layout = layoutInflater.inflate(R.layout.import_events_list_view, null);

        ListView list = (ListView) layout.findViewById(R.id.widget_list);
        ImportListViewAdapter adapter = new ImportListViewAdapter(MainActivity.this, events,
                new ThemeHelper(MainActivity.this));
        list.setAdapter(adapter);

        final boolean[] whichEventsToImport = new boolean[events.size()];
        for (int i = 0; i < whichEventsToImport.length; i++) {
            whichEventsToImport[i] = false;
        }

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView text = (TextView) view;
                int color_index = events.get(position).getColor();
                if (text.getCurrentTextColor() == helper.getEventTextColor_semitransparent(color_index)) {
                    text.getBackground().setColorFilter(helper.getEventColor(color_index), PorterDuff.Mode.SRC_IN);
                    text.setTextColor(helper.getEventTextColor(color_index));
                    whichEventsToImport[position] = true;
                } else {
                    text.getBackground().setColorFilter(helper.getEventColor_semitransparent(color_index), PorterDuff.Mode.SRC_IN);
                    text.setTextColor(helper.getEventTextColor_semitransparent(color_index));
                    whichEventsToImport[position] = false;
                }
            }
        });

        importDialog = new AlertDialog.Builder(context, getDialogTheme())
                .setView(layout)
                .setTitle("Import TODOs")
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (events.size() > 0 && todolist.getTodolist().size() == 0) {
                            removeNothingTodo();
                        }
                        ArrayList<Event> importEvents = new ArrayList<Event>();
                        for (int k = 0; k < whichEventsToImport.length; k++) {
                            if (whichEventsToImport[k]) {
                                importEvents.add(events.get(k));
                            }
                        }
                        todolist.importEvents(importEvents);

                        boolean[] selected_categories
                                = (boolean[]) settings.get("selected_categories");
                        for (int k = 1; k < selected_categories.length; k++) {
                            selected_categories[k] = true;
                        }

                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                    }
                })
                .setOnDismissListener(dismisslistener)
                .create();
        importDialog.show();
        changeDialogButtonColor(importDialog);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        for (int i = 0; i < mToolbar.getMenu().size(); i++) {
            if (mToolbar.getMenu().getItem(i).getItemId() == R.id.share_todos) {
                shareIcon = mToolbar.getMenu().getItem(i);
                //shareIcon.getIcon().setColorFilter(helper.get("toolbar_textcolor"), PorterDuff.Mode.SRC_IN);
                //shareIcon.getIcon().setColorFilter(helper.getToolbarIconColor(), PorterDuff.Mode.SRC_IN);
            }
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.share_todos:
                if (todolist.getTodolist().size() > 0) {
                    shareTodosClicked();
                } else {
                    showSnackbar("No TODOs to share");
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (shareEvents) {
            shareEventCallback.cancel();
            return;
        }
        super.onBackPressed();
        this.finish();
    }

    @Override
    public void onStop() {
        try {
            todolist.saveData(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        try {
            todolist.saveData(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        settings.saveSettings();
        mThis = null;

        updateWidget();

        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // GoogleApiClient connected

        //Log.d("MainActivity", "onConnected()");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // GoogleApiClient connection suspended

        //Log.d("MainActivity", "onConnectionSuspended()");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // GoogleApiClient connection failed
        //Log.d("MainActivity", "onConnectionFailed(): " + CommonStatusCodes.getStatusCodeString(result.getErrorCode()));

        /*mGoogleApiClient.connect();

        if (mGoogleApiClient.isConnecting()) {
            Log.d("MainActivity", "connecting");
        }

        if (mGoogleApiClient.isConnected()) {
            Log.d("MainActivity", "connected");
        } else {
            Log.d("MainActivity", "not connected");
        }*/
    }
}
