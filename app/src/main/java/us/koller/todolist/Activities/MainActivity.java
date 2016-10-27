package us.koller.todolist.Activities;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import android.app.ActivityManager;
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
import android.content.res.ColorStateList;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

import us.koller.todolist.BroadcastReceiver;
import us.koller.todolist.FirebaseSync.SyncDataAsyncTask;
import us.koller.todolist.ImportListViewAdapter;
import us.koller.todolist.R;
import us.koller.todolist.RecyclerViewAdapters.RVAdapter;
import us.koller.todolist.Settings;
import us.koller.todolist.Todolist.Alarm;
import us.koller.todolist.Todolist.Event;
import us.koller.todolist.Todolist.Todolist;
import us.koller.todolist.Util.Callbacks.AlarmInfoDialogOnPositiveCallback;
import us.koller.todolist.Util.Callbacks.ColorSelectedCallback;
import us.koller.todolist.Util.Callbacks.OnItemClickInterface;
import us.koller.todolist.Util.Callbacks.ShareEventCallback;
import us.koller.todolist.Util.Callbacks.SyncDataCallback;
import us.koller.todolist.Util.ClickHelper.OnItemClickHelper;
import us.koller.todolist.Util.DPCalc;
import us.koller.todolist.Util.DialogBuilder;
import us.koller.todolist.Util.ThemeHelper;
import us.koller.todolist.Widget.WidgetProvider_List;

public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    public static final String ADD_EVENT = "ADD_EVENT";
    public static final String IMPORT = "IMPORT";
    public static final String NOTIFICATION_DONE_BUTTON = "NOTIFICATION_DONE_BUTTON";
    public static final String UPDATE_EVENT_ALARM = "UPDATE_EVENT_ALARM";
    public static final String NEW_THEME = "NEW_THEME";

    public static boolean isRunning;

    private Todolist todolist;

    private Settings settings;

    private ThemeHelper helper;

    private NotificationManager mNotificationManager;

    private RecyclerView mRecyclerView;
    private RVAdapter mAdapter;

    private DrawerLayout mDrawerLayout;
    private NavigationView mNavigationView;
    private ActionBarDrawerToggle mDrawerToggle;
    private View drawerIcon;
    private Drawable overflowIcon;

    private MenuItem silenceAllAlarmsToggle;
    private MenuItem syncToggle;
    private MenuItem notificationToggle;

    private Toolbar mToolbar;

    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;

    private AlertDialog dialog;
    private ColorSelectedCallback colorSelectedCallback;

    private Snackbar snackbar;

    private static Handler handler = new Handler();

    private boolean isEventDraged = false;

    private ArrayList<Event> eventsToShare;
    private ShareEventCallback shareEventCallback;
    private boolean shareEvents = false;

    private boolean tablet;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private ValueEventListener mValueEventListener;

    private boolean updateWithDataFromFirebase = true; // the device that send data should not update
    private boolean wasItemMoved = false;

    private SyncDataAsyncTask syncDataAsyncTask;

    private TextView personName;
    private TextView personEmail;
    private LinearLayout personData;
    private static final int RC_SIGN_IN = 66;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        settings = new Settings(MainActivity.this);
        settings.readSettings();

        todolist = new Todolist(settings);
        todolist.initData(MainActivity.this);

        initRecyclerView();
        initDrawerLayout();

        initFirebase();
        buildGoogleApiClient();
        initSignInWithGoogle();

        //App might been started through AddEvent-Widget
        checkIntent(this.getIntent());
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        tablet = getResources().getBoolean(R.bool.tablet);
        /*if (!tablet) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }*/

        mDrawerToggle.syncState();

        for (int i = 0; i < mToolbar.getChildCount(); i++) {
            if (mToolbar.getChildAt(i) instanceof ImageView) {
                drawerIcon = mToolbar.getChildAt(i);
            }
        }

        initTheme();
    }

    @Override
    public void onStart() {
        super.onStart();

        checkEventRemoved();

        if (mAuth != null && mAuthListener != null) {
            mAuth.addAuthStateListener(mAuthListener);
        }
        if (mDatabase != null && mValueEventListener != null) {
            mDatabase.addValueEventListener(mValueEventListener);
        }
    }

    @Override
    protected void onResume() {
        isRunning = true;

        checkForNotificationUpdate();

        if (todolist.getTodolistArray().size() == 0) {
            showNothingTodo(false);
        }

        if (mAdapter.getList().size()
                == 0 && todolist.getTodolistArray().size() > 0) {
            showSnackbar(getString(R.string.no_category_selected));
        }

        mGoogleApiClient.connect();
        super.onResume();
    }

    public void initRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);

        /*if (tablet) {
            //Put Tablet specific layout here
        } else {

        }*/

        mAdapter = new RVAdapter(todolist.initAdapterList());

        /*LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);*/

        StaggeredGridLayoutManager mStaggeredGridLayoutManager = new StaggeredGridLayoutManager(
                getResources().getInteger(R.integer.layout_manager_span_count),
                StaggeredGridLayoutManager.VERTICAL);
        mStaggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        mRecyclerView.setLayoutManager(mStaggeredGridLayoutManager);

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkToolbarElevation();
            }
        });

        addOnItemTouchListenerToRecyclerView();
    }

    public void addOnItemTouchListenerToRecyclerView() {
        ItemTouchHelper.SimpleCallback mItemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP |
                ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, ViewHolder viewHolder, ViewHolder target) {
                todolist.eventMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                mAdapter.itemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                checkForNotificationUpdate();
                wasItemMoved = true;
                return true;
            }

            @Override
            public void onSwiped(ViewHolder viewHolder, int swipeDir) {
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
                                    float dX, float dY, final int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

                if (isCurrentlyActive) {
                    closeOpenCard();

                    switch (actionState) {
                        case ItemTouchHelper.ACTION_STATE_DRAG:
                            elevateToolbar();
                            viewHolder.itemView.setPressed(false);
                            viewHolder.itemView.setHovered(true);

                            isEventDraged = true;
                            if (!todolist.isAdapterListTodolist()) {
                                boolean[] all_categories_selected = new boolean[13];
                                for (int i = 1; i < all_categories_selected.length; i++) {
                                    all_categories_selected[i] = true;
                                }

                                //adding all Events to adapter List
                                ArrayList<Event> itemToBeSetSemiTransparent = new ArrayList<>();
                                for (int i = 0; i < todolist.getTodolistArray().size(); i++) {
                                    if (!todolist.isEventInAdapterList(todolist.getTodolistArray().get(i))) {
                                        mAdapter.addItem(i, todolist.getTodolistArray().get(i));

                                        itemToBeSetSemiTransparent.add(todolist.getTodolistArray().get(i));
                                    }
                                }
                                mAdapter.setItemToBeSetSemiTransparent(itemToBeSetSemiTransparent);

                                mRecyclerView.scrollToPosition(viewHolder.getAdapterPosition());
                            }
                            break;

                        case ItemTouchHelper.ACTION_STATE_SWIPE:
                            viewHolder.itemView.setPressed(true);
                            viewHolder.itemView.setHovered(false);

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
                    viewHolder.itemView.setHovered(false);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            resetAllSemiTransparentEvents();
                            if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && wasItemMoved) {
                                wasItemMoved = false;
                                if ((boolean) settings.get(Settings.SIGNED_IN)
                                        && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                                    updateFirebaseData();
                                }
                            }
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            checkToolbarElevation();
                        }
                    }, 300);
                }
            }
        };
        ItemTouchHelper mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, ViewHolder holder) {
                RVAdapter.EventViewHolder viewHolder = (RVAdapter.EventViewHolder) holder;
                if (shareEvents) {
                    viewHolder.setSemiTransparent(!viewHolder.semiTransparent);
                    viewHolder.initCard(helper);

                    if (!eventsToShare.contains(mAdapter.getList().get(position))) {
                        eventsToShare.add(mAdapter.getList().get(position));
                    } else {
                        eventsToShare.remove(mAdapter.getList().get(position));
                    }

                } else if (!viewHolder.semiTransparent) {
                    if (mAdapter.mExpandedPosition != position || mAdapter.mExpandedPosition == -1) {
                        closeOpenCard();

                        mAdapter.mExpandedPosition = position;
                        viewHolder.expand();
                    } else {
                        mAdapter.mExpandedPosition = -1;
                        viewHolder.collapse();
                    }
                }
            }
        });
    }

    public void closeOpenCard() {
        if (mAdapter.mExpandedPosition == -1) {
            return;
        }

        RVAdapter.EventViewHolder holder
                = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(mAdapter.mExpandedPosition);
        if (holder != null) {
            holder.collapse();
        }
        mAdapter.mExpandedPosition = -1;
    }

    public void resetAllSemiTransparentEvents() {
        mAdapter.clearSemiTransparentEventIds();
        todolist.addOrRemoveEventFromAdapter(mAdapter);
    }

    public void initDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (shareEvents) {
                    shareEventCallback.cancel();
                }
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
        mDrawerLayout.addDrawerListener(mDrawerToggle);
        mNavigationView
                = (NavigationView) findViewById(R.id.navigation_view);
        NavigationMenuView navigationMenuView
                = (NavigationMenuView) mNavigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        //find header Views
        View headerLayout = mNavigationView.getHeaderView(0);
        personName = (TextView) headerLayout.findViewById(R.id.personName);
        personEmail = (TextView) headerLayout.findViewById(R.id.personEmail);
        personData = (LinearLayout) headerLayout.findViewById(R.id.personData);
        ArrayList<MenuItem> navigationHeaders = new ArrayList<>();
        Menu m = mNavigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            navigationHeaders.add(mi);

            switch (mi.getItemId()) {
                case R.id.show_notification_toggle:
                    notificationToggle = mi;
                    final SwitchCompat notificationToggle
                            = (SwitchCompat) mi.getActionView();
                    notificationToggle.setChecked((boolean) settings.get(Settings.NOTIFICATION_TOGGLE));
                    notificationToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            notificationToggleClicked();
                            notificationToggle.setChecked((boolean) settings.get(Settings.NOTIFICATION_TOGGLE));
                        }
                    });
                    break;
                case R.id.silence_all_alarms:
                    silenceAllAlarmsToggle = mi;
                    if (!todolist.isAlarmScheduled()) {
                        mi.getActionView().setEnabled(false);
                    }
                    final SwitchCompat silenceAllAlarmsToggle
                            = (SwitchCompat) mi.getActionView();
                    silenceAllAlarmsToggle.setChecked(!(boolean) settings.get(Settings.VIBRATE));
                    silenceAllAlarmsToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            silenceAlarmsToggleClicked();
                        }
                    });
                    break;
                case R.id.sync_switch:
                    syncToggle = mi;
                    final SwitchCompat syncToggle
                            = (SwitchCompat) mi.getActionView();
                    syncToggle.setChecked((boolean) settings.get(Settings.SYNC_TOGGLE));
                    syncToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            syncToggleClicked(isChecked);
                        }
                    });
                    break;
            }
        }
        NavigationViewSetItemSelectedListener(mNavigationView);
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
                            case R.id.select_theme:
                                appThemeClicked();
                                break;
                            case R.id.info:
                                InfoButtonClicked();
                                break;
                            case R.id.select_category:
                                selectCategoryClicked();
                                break;
                        }
                        return true;
                    }
                });
    }

    public void initTheme() {
        helper = new ThemeHelper(MainActivity.this);
        mAdapter.setThemeHelper(helper);

        mToolbar.setBackgroundColor(helper.get(ThemeHelper.TOOLBAR_COLOR));
        mToolbar.setTitleTextColor(helper.get(ThemeHelper.TOOLBAR_TEXT_COLOR));
        changeColorOfToolbarDrawerIcon(helper.getToolbarIconColor());

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(helper.get(ThemeHelper.FAB_COLOR)));
        mFab.getDrawable().setTint(helper.get(ThemeHelper.FAB_TEXT_COLOR));
        mFab.setRippleColor(ContextCompat.getColor(MainActivity.this, R.color.white));

        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.setBackgroundColor(helper.get(ThemeHelper.CORD_COLOR));
        ((ImageView) findViewById(R.id.nothing_todo))
                .setColorFilter(helper.get(ThemeHelper.CORD_TEXT_COLOR), PorterDuff.Mode.SRC_IN);

        int color;
        if (helper.lightCoordColor()) {
            mNavigationView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.white));
            color = helper.getDarkTextColor();
        } else {
            mNavigationView.setBackgroundColor(ContextCompat.getColor(MainActivity.this, R.color.dark_dialog));
            color = helper.getLightTextColor();
        }

        int[][] state = new int[][]{new int[]{-android.R.attr.state_enabled},
                new int[]{android.R.attr.state_enabled},
                new int[]{-android.R.attr.state_checked},
                new int[]{android.R.attr.state_pressed}};

        int[] colors = new int[]{color, color, color, color};

        mNavigationView.setItemTextColor(new ColorStateList(state, colors));
        mNavigationView.setItemIconTintList(new ColorStateList(state, colors));

        personName.setTextColor(getDialogTextColor());
        personEmail.setTextColor(getDialogTextColor());

        //ColorStateList for NavigationDrawer Switches
        int color_grey = ContextCompat.getColor(MainActivity.this, R.color.grey);
        int color_dark;
        if (helper.lightCoordColor()) {
            color_dark = ContextCompat.getColor(MainActivity.this, R.color.light_grey);
        } else {
            color_dark = ContextCompat.getColor(MainActivity.this, R.color.black_light);
        }

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

        int fab_color = helper.get(ThemeHelper.FAB_COLOR);
        states[k] = new int[]{android.R.attr.state_checked};
        thumbColors[k] = fab_color;
        trackColors[k] = Color.argb(72, Color.red(fab_color),
                Color.green(fab_color), Color.blue(fab_color));
        k++;

        // Default enabled state
        states[k] = new int[0];
        thumbColors[k] = color_grey;
        trackColors[k] = color_dark;

        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                silenceAllAlarmsToggle.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                silenceAllAlarmsToggle.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                syncToggle.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                syncToggle.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                notificationToggle.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat)
                notificationToggle.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

        setOverflowButtonColor(helper.getToolbarIconColor());

        mAdapter.allItemsChanged();

        checkToolbarElevation();

        this.setTaskDescription(new ActivityManager.TaskDescription(
                getString(R.string.app_name),
                ((BitmapDrawable) ContextCompat.getDrawable(MainActivity.this, R.mipmap.ic_launcher)).getBitmap(),
                helper.get(ThemeHelper.TOOLBAR_COLOR)));
    }

    //<FirebaseSync Methods>

    public void buildGoogleApiClient() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addConnectionCallbacks(this)
                .build();
    }

    public void initSignInWithGoogle() {
        //onClickListener to SignInButton
        mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button)
                .setOnClickListener(new View.OnClickListener() {
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

        //check if user previouly signed in
        if (!(boolean) settings.get(Settings.SYNC_ENABLED)) {
            personData.setVisibility(View.GONE);
            mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
        } else {
            //try silent sign in
            mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            personData.setVisibility(View.VISIBLE);
            this.personName.setText("");
            this.personEmail.setText("...");

            OptionalPendingResult<GoogleSignInResult> opr
                    = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
            if (opr.isDone()) {
                GoogleSignInResult result = opr.get();
                handleSignInResult(result);
            } else {
                opr.setResultCallback(new ResultCallback<GoogleSignInResult>() {
                    @Override
                    public void onResult(@NonNull GoogleSignInResult googleSignInResult) {
                        handleSignInResult(googleSignInResult);
                    }
                });
            }
        }
    }

    public void initFirebase() {
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d("MainActivity", "signed in");
                    mDatabase = FirebaseDatabase.getInstance()
                            .getReference().child("accounts")
                            .child(user.getUid());
                    personEmail.setText(user.getEmail());
                    personName.setText(user.getDisplayName());
                } else {
                    // User is signed out
                    Log.d("MainActivity", "signed out");
                }
            }
        };
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

    private void handleSignInResult(GoogleSignInResult result) {
        if (result.isSuccess()) {
            // Signed in successfully
            GoogleSignInAccount acct = result.getSignInAccount();
            firebaseAuthWithGoogle(acct);
            String personName = "", personEmail = "";
            if (acct != null) {
                personName = acct.getDisplayName();
                personEmail = acct.getEmail();
            }
            mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.GONE);
            personData.setVisibility(View.VISIBLE);
            this.personName.setText(personName);
            this.personEmail.setText(personEmail);

            personData.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOutDialog();
                }
            });
            settings.set(Settings.SYNC_ENABLED, true);
            settings.set(Settings.SIGNED_IN, true);

            syncToggle.setVisible(true);
        } else {
            showToast("SignIn not successful! "
                    + GoogleSignInStatusCodes.getStatusCodeString(result.getStatus().getStatusCode()));
            // not Signed in
            personData.setOnClickListener(null);
            personData.setVisibility(View.GONE);
            mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.VISIBLE);
            syncToggle.setVisible(false);
            settings.set(Settings.SYNC_ENABLED, false);
            settings.set(Settings.SIGNED_IN, false);
            syncToggle.setVisible(false);
        }
        settings.saveSettings();
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            //failed
                            return;
                        }
                        if (mValueEventListener == null) {
                            addFirebaseDataListener();
                        }
                    }
                });
    }

    public void signIn() {
        //show loading
        mNavigationView.getHeaderView(0).findViewById(R.id.sign_in_button).setVisibility(View.GONE);
        personData.setVisibility(View.VISIBLE);
        this.personName.setText("");
        this.personEmail.setText("...");

        startActivityForResult(Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient), RC_SIGN_IN);
    }

    public void signOutDialog() {
        mDrawerLayout.closeDrawers();
        String content = getString(R.string.signOutDialog_content);
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, getDialogTheme());
        builder.setMessage(content)
                .setTitle(getString(R.string.signOut))
                .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mGoogleApiClient.isConnected()) {
                            signOut();
                        }
                    }
                })
                .setNegativeButton(getString(R.string.no), null);
        AlertDialog dialog = builder.create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        // signed out!
                        settings.set(Settings.SIGNED_IN, false);
                        settings.set(Settings.SYNC_ENABLED, false);
                        settings.set(Settings.WAS_EVER_SYNCED, false);

                        syncToggle.setVisible(false);

                        if (mValueEventListener != null) {
                            mDatabase.removeEventListener(mValueEventListener);
                            mValueEventListener = null;
                        }

                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                        mAuth.signOut();
                        initSignInWithGoogle();
                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case RC_SIGN_IN:
                handleSignInResult(
                        Auth.GoogleSignInApi.getSignInResultFromIntent(intent));
                break;
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateFirebaseData() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }
        if (isNetworkAvailable()) {
            updateWithDataFromFirebase = false;

            String data = todolist.getSyncData();
            mDatabase.setValue(data);
        }
    }

    public void addFirebaseDataListener() {
        if (mAuth.getCurrentUser() == null) {
            return;
        }

        mValueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (updateWithDataFromFirebase
                        && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                    showToast("onDataChange()");
                    String data = dataSnapshot.getValue(String.class);
                    interpretDataFromFirebase(data);
                } else {
                    updateWithDataFromFirebase = true;
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {/*nothing*/}
        };
        mDatabase.addValueEventListener(mValueEventListener);
    }

    public void interpretDataFromFirebase(String data) {
        if (data == null && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
            return;
        }
        //Handler needed to update RVAdapter from UI-Thread
        final Handler mainHandler = new Handler(getMainLooper());
        syncDataAsyncTask = new SyncDataAsyncTask(todolist, data, (boolean) settings.get(Settings.WAS_EVER_SYNCED),
                new SyncDataCallback() {
                    @Override
                    public void doneSyncingData(JSONObject selected_categories) {
                        syncDataAsyncTask = null;
                        settings.readSelectedCategories(selected_categories.toString());
                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                        MainActivity.this.doneSyncingData();
                    }

                    @Override
                    public void updateAlarms(ArrayList<Long> alarmsToCancel,
                                             ArrayList<Alarm> alarmsToSet) {
                        MainActivity.this.updateAlarms(alarmsToCancel, alarmsToSet);
                    }

                    @Override
                    public void error(String error) {
                        syncDataAsyncTask = null;
                        showToast("SyncDataAsyncTask: " + error);
                    }

                    @Override
                    public void notifyItemInserted(final int pos) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemInserted(pos);
                                Log.d("MainActivity", "notifyItemInserted");
                            }
                        });
                    }

                    @Override
                    public void notifyItemChanged(final int pos) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemChanged(pos);
                                Log.d("MainActivity", "notifyItemChanged");
                            }
                        });
                    }

                    @Override
                    public void notifyItemRemoved(final int pos) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemRemoved(pos);
                                Log.d("MainActivity", "notifyItemRemoved");
                            }
                        });
                    }

                    @Override
                    public void notifyItemMoved(final int from, final int to) {
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                mAdapter.notifyItemMoved(from, to);
                                Log.d("MainActivity", "notifyItemMoved");
                            }
                        });
                    }

                    @Override
                    public void doneFirstEverSync() {
                        settings.set(Settings.WAS_EVER_SYNCED, true);
                        updateFirebaseData();
                        Log.d("MainActivity", "doneFirstEverSync");
                    }

                });
        syncDataAsyncTask.execute();
    }

    public void doneSyncingData() {
        todolist.clearRemovedAndAddedEvents();
        checkForNotificationUpdate();
        try {
            todolist.saveData(MainActivity.this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        closeOpenCard();
        checkForNotificationUpdate();

        //show or hide illustration
        if (todolist.getTodolistArray().size() != 0) {
            removeNothingTodo();
        } else {
            showNothingTodo(true);
        }
    }

    public void updateAlarms(ArrayList<Long> alarmsToCancel, ArrayList<Alarm> alarmsToSet) {
        for (int i = 0; i < alarmsToCancel.size(); i++) {
            long id = alarmsToCancel.get(i);
            removeAlarm((int) id);
        }

        for (int i = 0; i < alarmsToSet.size(); i++) {
            long time = (long) alarmsToSet.get(i).get(Alarm.TIME);
            long alarmId = (long) alarmsToSet.get(i).get(Alarm.ID);
            setAlarm(time, alarmId);
        }
    }

    //</FirebaseSync Methods>

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void checkIntent(Intent intent) {
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case ADD_EVENT:
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    fabClicked(findViewById(R.id.fab));
                    break;

                case IMPORT:
                    //check if theme or TODOs
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                    onImportIntent(intent.getStringExtra("events"));
                    break;

                case NOTIFICATION_DONE_BUTTON:
                    removeEventNotificationDoneButton(intent.getLongExtra("eventId", 0));
                    break;

                case UPDATE_EVENT_ALARM:
                    //called when alarm is repeating, to schedule next alarm
                    long eventId = intent.getLongExtra("eventId", 0);
                    long alarmTime = intent.getLongExtra("alarmTime", 0);
                    Alarm alarm = todolist.getEventById(eventId).getAlarm();
                    if (alarm != null) {
                        alarm.setTime(alarmTime);
                    } else {
                        todolist.getEventById(eventId).setAlarm(eventId, alarmTime);
                    }
                    setAlarm(alarmTime, eventId);
                    break;

                case NEW_THEME:
                    initTheme();
                    break;
            }
        }
    }

    public void checkForNotificationUpdate() {
        if (todolist.getTodolistArray().size() != 0
                && (boolean) settings.get(Settings.NOTIFICATION_TOGGLE)) {
            showNotification();
        } else if (!(boolean) settings.get(Settings.NOTIFICATION_TOGGLE)) {
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
        if (todolist.getTodolistArray().size() == 1) {
            content = getString(R.string.you_have) + " " + todolist.getTodolistArray().size() + " " + getString(R.string.event_in_your_todolist);
        } else {
            content = getString(R.string.you_have) + " " + todolist.getTodolistArray().size() + " " + getString(R.string.events_in_your_todolist);
        }
        Intent add_event_intent = new Intent(MainActivity.this, MainActivity.class);
        add_event_intent.setAction(ADD_EVENT);
        add_event_intent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        PendingIntent add_event_pendingIntent = PendingIntent.getActivity(MainActivity.this, 6, add_event_intent, 0); // PendingIntent.FLAG_IMMUTABLE

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(getString(R.string.app_name))
                .addAction(R.drawable.ic_add, getString(R.string.add_event), add_event_pendingIntent)
                .setColor(ContextCompat.getColor(MainActivity.this, R.color.button_color))
                .setContentText(content);
        mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        Intent resultIntent = new Intent(this, MainActivity.class);
        PendingIntent resultPendingIntent = PendingIntent.getActivity(this, 666, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        int todoSize;
        if (todolist.getTodolistArray().size() > 5) {
            todoSize = 5;
            events[5] = "...";
        } else {
            todoSize = todolist.getTodolistArray().size();
        }
        for (int i = 0; i < todoSize; i++) {
            if (todolist.getTodolistArray().get(i) != null) {
                events[i] = todolist.getTodolistArray().get(i).getWhatToDo();
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

    public void removeEvent(int index) {
        todolist.removeEvent(mAdapter, index);
        checkForNotificationUpdate();
        if (todolist.getTodolistArray().size() == 0) {
            showNothingTodo(true);
        }
        checkToolbarElevation();
        eventRemovedSnackbar();
        if ((boolean) settings.get(Settings.SIGNED_IN)
                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
            updateFirebaseData();
        }

        if (mAdapter.mExpandedPosition == index) {
            mAdapter.mExpandedPosition = -1;
        }
    }

    public void notifAdapterItemChanged(Event e) {
        mAdapter.notifyItemChanged(mAdapter.getList().indexOf(e));
    }

    public void actionButtonClicked(View v, Event e) {
        switch (v.getId()) {
            case R.id.color_button:
                colorButtonClicked(e);
                break;
            case R.id.edit_button:
                EditButtonClicked(e);
                break;
            case R.id.alarm_button:
                AlarmButtonClicked(e);
                break;
        }
    }

    public void colorButtonClicked(final Event e) {
        colorSelectedCallback = new ColorSelectedCallback() {
            @Override
            public void colorSelected(View v, int colorIndex) {
                if (dialog != null) {
                    dialog.dismiss();
                    colorSelectedCallback = null;
                }
                if (colorIndex != e.getColor()) {
                    e.setColor(colorIndex);
                    int index = mAdapter.getList().indexOf(e);
                    ((RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(index))
                            .changeCardColorAnim(MainActivity.this, helper.getEventColor(colorIndex),
                                    helper.getEventTextColor(colorIndex));
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if ((boolean) settings.get(Settings.SIGNED_IN)
                                    && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                                updateFirebaseData();
                            }
                        }
                    }, 1000);
                    if (!settings.getCategory(colorIndex)) {
                        int number_of_event_with_color_index = 0;
                        for (int i = 0; i < todolist.getTodolistArray().size(); i++) {
                            if (todolist.getTodolistArray().get(i).getColor() == colorIndex) {
                                number_of_event_with_color_index++;
                            }
                        }
                        if (number_of_event_with_color_index == 1) {
                            settings.setCategory(colorIndex, true);
                        }
                    }
                    if (e.getAlarm() != null) {
                        try {
                            todolist.saveData(MainActivity.this);
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }
            }
        };

        View layout = getLayoutInflater().inflate(R.layout.color_selector, mCoordinatorLayout, false);
        dialog = DialogBuilder.getColorEventDialog(layout, getDialogTheme(), helper)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.dialog = null;
                    }
                })
                .create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void EditButtonClicked(final Event e) {
        final View dialogView = getLayoutInflater().inflate(R.layout.input_dialog, mCoordinatorLayout, false);
        AlertDialog dialog = DialogBuilder.getEditEventDialog(dialogView, getDialogTheme(), getDialogTextColor(), e)
                .setPositiveButton(dialogView.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = ((EditText) dialogView.findViewById(R.id.edit_text)).getText().toString();
                        e.editWhatToDo(s);
                        checkForNotificationUpdate();
                        notifAdapterItemChanged(e);

                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                    }
                }).create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void AlarmButtonClicked(Event e) {
        if (e.hasAlarm() && !todolist.hasAlarmFired(e)) {
            showAlarmInfoDialog(e);
        } else {
            showAlarmDatePicker(e);
        }
    }

    public void fabClicked(View v) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_scale_down);
            anim.setDuration(200);
            anim.setInterpolator(new AccelerateDecelerateInterpolator());
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
        }

        final View layout = getLayoutInflater().inflate(R.layout.add_event_dialog, mCoordinatorLayout, false);
        dialog = DialogBuilder.getAddEventDialog(layout, helper, getDialogTheme(), getDialogTextColor())
                .setPositiveButton(layout.getContext().getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = ((EditText) layout.findViewById(R.id.edit_text)).getText().toString();
                        if (s.length() == 0) {
                            s = DialogBuilder.addEventHint;
                        }
                        int[] sortedColors = helper.getSortedColors();
                        int color = sortedColors[DialogBuilder.selectedColor];

                        if (getTodolist().getTodolistArray().size() == 0) {
                            removeNothingTodo();
                        }

                        boolean[] possible_colors = new boolean[((boolean[]) settings.get(Settings.SELECTED_CATEGORIES)).length];
                        for (int i = 1; i < possible_colors.length; i++) {
                            if (settings.getCategory(i) || !todolist.doesCategoryContainEvents(i)) {
                                possible_colors[i] = true;
                            }
                        }
                        Event e = new Event(s, color, 0, possible_colors);
                        todolist.addEvent(mAdapter, e);
                        settings.setCategory(e.getColor(), true);
                        closeOpenCard();
                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                        mRecyclerView.scrollToPosition(mAdapter.getList().indexOf(e));
                        checkForNotificationUpdate();

                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                    }
                })
                .setOnDismissListener(
                        new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialog) {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.fab_scale_up);
                                    anim.setDuration(200);
                                    anim.setInterpolator(new AccelerateDecelerateInterpolator());
                                    fab.startAnimation(anim);
                                    fab.setVisibility(View.VISIBLE);
                                }
                                MainActivity.this.dialog = null;
                            }
                        }
                ).create();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dialog.show();
                    changeDialogButtonColor(dialog);
                }
            }, 200);
        } else {
            dialog.show();
            changeDialogButtonColor(dialog);
        }
    }

    public Todolist getTodolist() {
        return todolist;
    }

    public void showAlarmInfoDialog(final Event e) {
        final View layout = getLayoutInflater().inflate(R.layout.alarm_info_dialog, mCoordinatorLayout, false);
        dialog = DialogBuilder.getAlarmInfoDialog(layout, helper, getDialogTheme(), getDialogTextColor(), e,
                new AlarmInfoDialogOnPositiveCallback() {
                    @Override
                    public void onPositive() {
                        try {
                            todolist.saveData(MainActivity.this);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                    }
                })
                .setNeutralButton(layout.getContext().getString(R.string.edit_time), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        showAlarmDatePicker(e);
                    }
                })
                .setNegativeButton(layout.getContext().getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAlarm(e);
                    }
                })
                .create();

        Window w = dialog.getWindow();
        if (w != null) {
            w.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    @SuppressWarnings("deprecation")
    public void showAlarmDatePicker(final Event e) {
        DialogBuilder.getDatePickerDialog(this, helper,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
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
                }).show();
    }

    @SuppressWarnings("deprecation")
    public void ShowAlarmTimePicker(final Event e, final Calendar alarmDate) {
        DialogBuilder.getTimePickerDialog(this, helper,
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
                            if ((boolean) settings.get(Settings.SIGNED_IN)
                                    && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                                updateFirebaseData();
                            }

                            try {
                                todolist.saveData(MainActivity.this);
                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        }
                    }
                }).show();
    }

    public void setAlarm(long alarmTime, Event e) {
        long id = e.getId();
        setAlarm(alarmTime, id);
        e.setAlarm(id, alarmTime);
        showSnackbar(getString(R.string.alarm_was_set_for) + " " + e.getWhatToDo());
    }

    public void setAlarm(long alarmTime, long id) {
        AlarmManager mAlarmManager
                = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
        intent.putExtra("EventId", id);
        intent.setAction(BroadcastReceiver.ALARM);
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(MainActivity.this, (int) id, intent, 0);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, alarmTime, pendingIntent);

        silenceAllAlarmsToggle.setEnabled(true);
        silenceAllAlarmsToggle.getActionView().setEnabled(true);
    }

    public void removeAlarm(Event e) {
        long id = e.getId();
        e.removeAlarm();
        removeAlarm(id);
        showSnackbar(getString(R.string.alarm_removed));
    }

    public void removeAlarm(long id) {
        AlarmManager mAlarmManager
                = (AlarmManager) MainActivity.this.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(MainActivity.this, BroadcastReceiver.class);
        intent.putExtra("EventId", id);
        intent.setAction(BroadcastReceiver.ALARM);
        PendingIntent pendingIntent
                = PendingIntent.getBroadcast(MainActivity.this, (int) id, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        if (!todolist.isAlarmScheduled()) {
            silenceAllAlarmsToggle.setEnabled(false);
            silenceAllAlarmsToggle.getActionView().setEnabled(false);
        }
    }

    public void colorButtonClicked(View v) {
        int[] sortedColors = helper.getSortedColorsColorSelect();
        int colorIndex = sortedColors[getColorIndexByButtonId(v.getId())];
        if (colorSelectedCallback != null) {
            colorSelectedCallback.colorSelected(v, colorIndex);
        }
    }

    public void restoreLastDoneEventClicked() {
        mDrawerLayout.closeDrawers();
        Event e = todolist.getLastRemovedEvent();
        if (e != null) {
            removeNothingTodo();
            todolist.restoreLastRemovedEvent();
            settings.setCategory(e.getColor(), true);
            todolist.addOrRemoveEventFromAdapter(mAdapter);
            mRecyclerView.scrollToPosition(mAdapter.getList().indexOf(e));
            checkForNotificationUpdate();

            handler.postDelayed(new Runnable() {
                public void run() {
                    checkToolbarElevation();
                }
            }, 100);
        } else {
            showSnackbar(getString(R.string.you_have_no_event_to_restore));
        }
    }

    public void silenceAlarmsToggleClicked() {
        settings.toggle(Settings.VIBRATE);

        if (!(boolean) settings.get(Settings.VIBRATE)) {
            showSnackbar(getString(R.string.all_alarm_are_now_silent));
        } else {
            showSnackbar(getString(R.string.now_the_Phone_will_vibrate_when_alarms_are_fired));
        }
    }

    public void syncToggleClicked(boolean isChecked) {
        settings.set(Settings.SYNC_TOGGLE, isChecked);
    }

    public void selectCategoryClicked() {
        mDrawerLayout.closeDrawers();
        colorSelectedCallback = new ColorSelectedCallback() {
            @Override
            public void colorSelected(View v, int colorIndex) {
                if (!settings.getCategory(colorIndex)) {
                    v.setSelected(true);
                    settings.setCategory(colorIndex, true);
                } else {
                    v.setSelected(false);
                    settings.setCategory(colorIndex, false);
                }
            }
        };

        View layout = getLayoutInflater().inflate(R.layout.color_selector, mCoordinatorLayout, false);

        boolean[] categoriesToDisable = new boolean[13];
        for (int i = 0; i < categoriesToDisable.length; i++) {
            categoriesToDisable[i] = !todolist.doesCategoryContainEvents(i);
        }

        dialog = DialogBuilder.getCategorySelectorDialog(layout, helper, getDialogTheme(), settings, categoriesToDisable)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        closeOpenCard();
                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                        if (mAdapter.getList().size() == 0 && todolist.getTodolistArray().size() != 0) {
                            showSnackbar(getString(R.string.no_category_selected));
                        }
                        handler.postDelayed(new Runnable() {
                            public void run() {
                                checkToolbarElevation();
                            }
                        }, 400);

                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        colorSelectedCallback = null;
                        MainActivity.this.dialog = null;
                    }
                }).create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void appThemeClicked() {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(this, ThemeActivity.class);
        startActivity(intent);
    }

    public void notificationToggleClicked() {
        settings.toggle(Settings.NOTIFICATION_TOGGLE);

        if (!(boolean) settings.get(Settings.NOTIFICATION_TOGGLE)) {
            showSnackbar(getString(R.string.general_notification_is_hidden));
            cancelNotification();
        } else {
            showSnackbar(getString(R.string.general_notification_is_shown));
        }
        checkForNotificationUpdate();
    }

    public void InfoButtonClicked() {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(MainActivity.this, InfoActivity.class);
        startActivity(intent);
    }

    public void showNothingTodo(boolean withAnim) {
        final ImageView illustration = (ImageView) findViewById(R.id.nothing_todo);
        illustration.setImageDrawable(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_ntd_illustration_vector));
        illustration.setColorFilter(helper.get(ThemeHelper.CORD_TEXT_COLOR), PorterDuff.Mode.SRC_IN);
        illustration.setAlpha(0.5f);

        if (!withAnim) {
            illustration.setVisibility(View.VISIBLE);
            return;
        }
        if (illustration.getVisibility() != View.VISIBLE) {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.fade_in);
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

    public void removeNothingTodo() {
        final ImageView illustration = (ImageView) findViewById(R.id.nothing_todo);
        if (!(illustration.getVisibility() == View.GONE)) {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.fade_out);
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

    public void checkToolbarElevation() {
        if (helper.get(ThemeHelper.CORD_COLOR) != helper.get(ThemeHelper.TOOLBAR_COLOR)) {
            elevateToolbar();
            return;
        }

        if (mRecyclerView.canScrollVertically(-1)) {
            elevateToolbar();
        } else {
            deelevateToolbar();
        }
    }

    public void elevateToolbar() {
        mToolbar.setSelected(true);
    }

    public void deelevateToolbar() {
        mToolbar.setSelected(false);
    }


    public void changeColorOfToolbarDrawerIcon(int color) {
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

    public AlertDialog changeDialogButtonColor(AlertDialog dialog) {
        int color = helper.get(ThemeHelper.FAB_COLOR);
        if (color == ContextCompat.getColor(MainActivity.this, R.color.white)) {
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

    public int getDialogTextColor() {
        if (helper.lightCoordColor()) {
            return helper.getDarkTextColor();
        }
        return helper.getLightTextColor();
    }

    public int getDialogTheme() {
        if (helper.lightCoordColor()) {
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

    public void showSnackbar(String content) {
        snackbar = Snackbar.make(mCoordinatorLayout, content, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void eventRemovedSnackbar() {
        snackbar = Snackbar.make(mCoordinatorLayout,
                getString(R.string.event_removed), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(helper.get(ThemeHelper.FAB_COLOR))
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreLastDoneEventClicked();
                        if ((boolean) settings.get(Settings.SIGNED_IN)
                                && (boolean) settings.get(Settings.SYNC_TOGGLE)) {
                            updateFirebaseData();
                        }
                    }
                });
        snackbar.show();
    }

    public void shareTodosClicked() {
        if (shareEvents) {
            shareEventCallback.cancel();
            return;
        }

        fabShareAnim(true);
        closeOpenCard();

        eventsToShare = new ArrayList<>();

        //adding all Events to adapter List
        ArrayList<Event> itemToBeSetSemiTransparent = new ArrayList<>();
        for (int i = 0; i < todolist.getTodolistArray().size(); i++) {
            if (!todolist.isEventInAdapterList(todolist.getTodolistArray().get(i))) {
                mAdapter.addItem(i, todolist.getTodolistArray().get(i));

                itemToBeSetSemiTransparent.add(todolist.getTodolistArray().get(i));
            } else {
                eventsToShare.add(todolist.getTodolistArray().get(i));
            }
        }
        mAdapter.setItemToBeSetSemiTransparent(itemToBeSetSemiTransparent);

        shareEvents = true;
        shareEventCallback = new ShareEventCallback() {
            @Override
            public void shareEvents() {
                MainActivity.this.shareEvents(eventsToShare);

                cancel();
            }

            @Override
            public void cancel() {
                fabShareAnim(false);

                shareEvents = false;
                shareEventCallback = null;

                mAdapter.clearSemiTransparentEventIds();
                mAdapter.notifyDataSetChanged();
                todolist.addOrRemoveEventFromAdapter(mAdapter);
            }
        };
    }

    public void fabShareAnim(boolean b) {
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
                mFab.getDrawable().setTint(helper.get(ThemeHelper.FAB_TEXT_COLOR));
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

        String filename = "SharedEvents.txt";
        generateFileToShare(filename, data);

        File f = new File(getFilesDir().getAbsolutePath(), filename);
        Uri uri = FileProvider.getUriForFile(this,
                "us.koller.todolist.fileprovider", f);

        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .addStream(uri)
                .setType(getContentResolver().getType(uri))
                .getIntent();

        shareIntent.setData(uri);
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

    public void updateWidget() {
        Intent intent = new Intent(this, WidgetProvider_List.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(MainActivity.this)
                .getAppWidgetIds(new ComponentName(MainActivity.this, WidgetProvider_List.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        sendBroadcast(intent);
    }

    public void checkEventRemoved() {
        ArrayList<Long> eventsToRemove;
        try {
            eventsToRemove = todolist.eventRemovedThroughNotificationButton(this);
            for (int i = 0; i < eventsToRemove.size(); i++) {
                removeEventNotificationDoneButton(eventsToRemove.get(i));
            }
        } catch (JSONException | FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void removeEventNotificationDoneButton(long id) {
        Event e = todolist.getEventById(id);
        if (e != null) {
            if (todolist.isEventInAdapterList(e)) {
                removeEvent(todolist.getIndexOfEventInAdapterListById(id));
            } else {
                todolist.removeEvent(e);
                checkForNotificationUpdate();
            }
        }
    }

    public void onImportIntent(String data) {
        try {
            new JSONObject(data);
        } catch (JSONException e) {
            try {
                new JSONArray(data);
            } catch (JSONException e1) {
                showToast("Sorry! This file can't be imported." + '\n'
                        + "Please only import files shared through the app.");
                return;
            }
            showImportEvents(data, true);
            return;
        }

        importTheme(data);
    }

    public void importTheme(String data) {
        ArrayList<Event> colors = new ArrayList<>();
        for (int i = 1; i < 13; i++) {
            Event e = new Event("Color" + i, i, 0, null);
            colors.add(e);
        }

        final ThemeHelper importHelper;
        try {
            JSONObject json = new JSONObject(data);
            importHelper = new ThemeHelper(json);
        } catch (JSONException e) {
            //e.printStackTrace();
            showToast("Error trying to import theme!");
            return;
        }

        View layout = getLayoutInflater().inflate(R.layout.import_theme_dialog, mCoordinatorLayout, false);

        ListView list = (ListView) layout.findViewById(R.id.import_theme_list);
        ImportListViewAdapter adapter = new ImportListViewAdapter(MainActivity.this, colors, importHelper);
        adapter.eventsImport = false;
        list.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) layout.findViewById(R.id.import_theme_fab);
        CardView card = (CardView) layout.findViewById(R.id.import_theme_card);
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.import_theme_toolbar);
        View statusBar = layout.findViewById(R.id.import_theme_statusbar);

        fab.setBackgroundTintList(ColorStateList.valueOf(importHelper.get(ThemeHelper.FAB_COLOR)));
        fab.getDrawable().setTint(importHelper.get(ThemeHelper.FAB_TEXT_COLOR));
        card.setCardBackgroundColor(importHelper.get(ThemeHelper.CORD_COLOR));

        toolbar.setBackgroundColor(importHelper.get(ThemeHelper.TOOLBAR_COLOR));
        toolbar.setTitleTextColor(importHelper.get(ThemeHelper.TOOLBAR_TEXT_COLOR));
        toolbar.setNavigationIcon(null);

        if (importHelper.get(ThemeHelper.CORD_COLOR) != importHelper.get(ThemeHelper.TOOLBAR_COLOR)) {
            toolbar.setElevation(DPCalc.dpIntoPx(getResources(), 4));

            statusBar.setBackgroundColor(importHelper.get(ThemeHelper.TOOLBAR_COLOR));
        }

        dialog = new AlertDialog.Builder(MainActivity.this, getDialogTheme())
                .setView(layout)
                .setTitle("Import Theme")
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        importHelper.saveData(MainActivity.this);
                        initTheme();
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.dialog = null;
                    }
                })
                .create();
        dialog.show();
        changeDialogButtonColor(dialog);
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
                Event e = new Event(lines[i], 0, 0, null);
                events.add(e);
            }
        }

        if (events.size() == 0) {
            return;
        }

        View layout = getLayoutInflater().inflate(R.layout.import_events_list_view, mCoordinatorLayout, false);

        ListView list = (ListView) layout.findViewById(R.id.widget_list);
        ImportListViewAdapter adapter = new ImportListViewAdapter(MainActivity.this, events,
                new ThemeHelper(MainActivity.this));
        list.setAdapter(adapter);

        final boolean[] whichEventsToImport = new boolean[events.size()];
        for (int i = 0; i < whichEventsToImport.length; i++) {
            whichEventsToImport[i] = true;
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

        dialog = new AlertDialog.Builder(MainActivity.this, getDialogTheme())
                .setView(layout)
                .setTitle("Import TODOs")
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setPositiveButton("Import", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (events.size() > 0 && todolist.getTodolistArray().size() == 0) {
                            removeNothingTodo();
                        }
                        ArrayList<Event> importEvents = new ArrayList<>();
                        for (int k = 0; k < whichEventsToImport.length; k++) {
                            if (whichEventsToImport[k]) {
                                importEvents.add(events.get(k));
                            }
                        }
                        todolist.importEvents(importEvents);

                        boolean[] selected_categories
                                = (boolean[]) settings.get(Settings.SELECTED_CATEGORIES);
                        for (int k = 1; k < selected_categories.length; k++) {
                            selected_categories[k] = true;
                        }

                        todolist.addOrRemoveEventFromAdapter(mAdapter);
                    }
                })
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        MainActivity.this.dialog = null;
                    }
                })
                .create();
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.share_todos:
                if (todolist.getTodolistArray().size() > 0) {
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
        settings.saveSettings();

        updateWidget();

        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }

        if (mValueEventListener != null) {
            mDatabase.removeEventListener(mValueEventListener);
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (isChangingConfigurations()
                && syncDataAsyncTask != null) {
            syncDataAsyncTask.dettach();
        }
    }


    @Override
    protected void onPause() {
        super.onPause();
        isRunning = false;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        checkIntent(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // GoogleApiClient connected
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // GoogleApiClient connection suspended
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        // GoogleApiClient connection failed
    }
}
