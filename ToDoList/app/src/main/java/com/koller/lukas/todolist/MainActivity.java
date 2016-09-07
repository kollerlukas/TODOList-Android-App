package com.koller.lukas.todolist;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.content.res.Configuration;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.internal.NavigationMenuView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Random;
import java.util.TimeZone;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private Context context;
    private TODOLIST todolist;
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
    private CoordinatorLayout mCoordinatorLayout;
    private FloatingActionButton mFab;
    private TextView mTextView;
    private MenuItem mSilenceAllAlarms;
    private MenuItem autoSyncMenuItem;
    private MenuItem generalNotif;
    private ArrayList<MenuItem> navigation_headers;

    private AlertDialog ColorSelectorDialog;
    private AlertDialog addEventDialog;
    private boolean CategoryWasSelected = false;
    private Snackbar snackbar;

    private Handler handler;
    private boolean actionButtonAlreadyClicked = false;
    private DialogInterface.OnDismissListener dismisslistener;

    private EVENT eventToColorChange;
    private boolean[] selected_categories;
    public int selected_color; //For Color selecting at the add Dialog
    public int default_color = 0;
    private boolean newTheme = false;

    private ThemeHelper helper;

    private boolean is_event_draged = false;

    private boolean tablet;

    private boolean signedIn;
    private GoogleApiClient mGoogleApiClient;
    private SignInButton signInButton;
    private TextView personName;
    private TextView personEmail;
    private RelativeLayout personData;
    private static final int RC_SIGN_IN = 9001;
    private MenuItem syncData;
    private boolean autoSync = false;
    private boolean pendingSync = false;
    private boolean pendingSyncRequest = false;
    private DriveId driveId;

    /*final private ResultCallback<DriveApi.DriveContentsResult> contentsCallback = new ResultCallback<DriveApi.DriveContentsResult>() {
        @Override
        public void onResult(DriveApi.DriveContentsResult result) {
            if (!result.getStatus().isSuccess()) {
                showToast("Error while trying to create new file contents");
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            new CreateFile(mGoogleApiClient, fileCallback, result).execute();
        }
    };

    final private ResultCallback<DriveFolder.DriveFileResult> fileCallback = new ResultCallback<DriveFolder.DriveFileResult>() {
        @Override
        public void onResult(DriveFolder.DriveFileResult result) {
            if (!result.getStatus().isSuccess()) {
                showToast("Error while trying to create a file; Status-Code: " + result.getStatus().getStatusMessage());
                mSwipeRefreshLayout.setRefreshing(false);
                return;
            }
            writeToFile(result.getDriveFile().getDriveId());
        }
    };

    final DriveIdCallback driveIdCallback_read = new DriveIdCallback() {
        @Override
        public void gotDriveId(DriveId driveId) {
            //showToast("Got DriveId read");
            readFromFile(driveId);
        }

        @Override
        public void error(int statusCode) {
            DriveIdError(statusCode);
        }
    };

    final DriveIdCallback driveIdCallback_write = new DriveIdCallback() {
        @Override
        public void gotDriveId(DriveId driveId) {
            //showToast("Got DriveId write");
            writeToFile(driveId);
        }

        @Override
        public void error(int statusCode) {
            if (statusCode == CommonStatusCodes.SUCCESS) {
                createNewFile();
            }
            DriveIdError(statusCode);
        }
    };

    final SyncDataCallback syncDataCallback = new SyncDataCallback() {
        @Override
        public void DoneSyncingData() {
            MainActivity.this.DoneSyncingData();
        }

        @Override
        public void addEvent(EVENT e, int position) {
            MainActivity.this.addEvent(e, position);
            todolist.saveEventAddedInSyncTimeStamp(MainActivity.this);
        }

        @Override
        public void removeEvent(int adapter_index) {
            MainActivity.this.removeEvent(adapter_index);
        }

        @Override
        public void updateEvent(EVENT e, EVENT newEvent) {
            MainActivity.this.updateEvent(e, newEvent);
        }

        @Override
        public void updateColors(int[] newColors, int[] newTextColors) {
            helper.setColors(newColors);
            helper.setTextColors(newTextColors);
            helper.saveData();
        }

        @Override
        public void moveEvent(EVENT e, int toPosition) {
            MainActivity.this.moveEvent(e, toPosition);
        }
    };

    final RetrievedDataFromAppFolderCallback retrievedDataFromAppFolderCallback = new RetrievedDataFromAppFolderCallback() {
        @Override
        public void retrievedDataFromAppFolder(String data) {
            if (data == null) {
                showToast("Error while trying to retrieve Data");
                createNewFile();
            } else {
                if (!data.equals("")) {
                    new SyncDataAsyncTask(todolist, mAdapter, data, syncDataCallback, MainActivity.this).execute();
                } else {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            }
        }
    };*/

    //For Intro
    public boolean introMode = false;
    private TextView mIntroTextView;
    private TextView mIndicatorTextView;
    private boolean intro_event = true;
    private boolean intro_event_tap = true;
    private boolean intro_action_area = true;
    private boolean intro_swipe = true;
    private Animation fade_in;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getResources().getBoolean(R.bool.tablet)) {
            tablet = true;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            tablet = false;
        }

        todolist = new TODOLIST();
        handler = new Handler();
        helper = new ThemeHelper(this);
        context = this;
        try {
            todolist.readSettings(context);
            todolist.readCategorySettings(context, this);
            todolist.readData(context);
            default_color = todolist.readDefaultColor(context);
        } catch (JSONException e) {
            e.printStackTrace();
            ///showToast("JSONException");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            //showToast("FileNotFoundException");
        }

        mAdapter = new RVAdapter(todolist.initAdapterList(selected_categories), new CardButtonOnClickHelper(this), this);

        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
        setSupportActionBar(mToolbar);

        setupRecyclerView();
        setupDrawerLayout();

        if (todolist.getShowNotification()) {
            checkForNotificationUpdate();
        }

        // Needed to find drawerIcon
        handler.postDelayed(new Runnable() {
            public void run() {
                for (int i = 0; i < mToolbar.getChildCount(); i++) {
                    if (mToolbar.getChildAt(i) instanceof ImageView) {
                        drawerIcon = mToolbar.getChildAt(i);
                        setupTheme();
                    }
                }
            }
        }, 1);

        //For IntroMode
        if (context.getSharedPreferences("todolist", Context.MODE_PRIVATE).getBoolean("intro_mode", true)) {
            introMode = true;
            SharedPreferences sharedpreferences = context.getSharedPreferences("todolist", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedpreferences.edit();
            editor.putBoolean("intro_mode", false);
            editor.apply();
            IntroMode();
        } else if (todolist.getTodolist().size() == 0) {
            mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
            showNothingTodo();
        }

        // For Google Drive Api
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(Drive.SCOPE_APPFOLDER)
                .build();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_APPFOLDER)
                .addConnectionCallbacks(this)
                .build();*/

        setupSignInWithGoogle();
    }

    public void setupSignInWithGoogle() {
        SharedPreferences sharedpreferences = getSharedPreferences("todolist", MODE_PRIVATE);
        //signedIn = sharedpreferences.getBoolean("SignedIn", false);
        signedIn = false;
        mSwipeRefreshLayout.setEnabled(signedIn);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawers();
                /*if (mGoogleApiClient.isConnected()) {
                    signIn();
                } else {
                    showToast("Client not connected!");
                }*/
                AlertDialog.Builder builder = new AlertDialog.Builder(context, getDialogTheme());
                builder.setMessage("Feature coming soon!")
                        .setPositiveButton(getString(R.string.ok), null);
                AlertDialog dialog = builder.create();
                dialog.show();
                changeDialogButtonColor(dialog);

            }
        });
        /*if (!signedIn) {
            personData.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
        } else {
            personData.setVisibility(View.VISIBLE);
            signInButton.setVisibility(View.GONE);
            OptionalPendingResult<GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(mGoogleApiClient);
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
        }*/
    }

    /*public void saveSignedIn() {
        SharedPreferences sharedpreferences = getSharedPreferences("todolist", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedpreferences.edit();
        editor.putBoolean("SignedIn", signedIn);
        editor.apply();
    }

    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        // signed out!
                        signedIn = false;
                        mSwipeRefreshLayout.setEnabled(false);
                        syncData.setVisible(false);
                        todolist.todolistTimeStamp = 0;
                        saveSignedIn();
                        setupSignInWithGoogle();
                    }
                });
    }

    //for removing Account from App
    private void revokeAccess() {
        Auth.GoogleSignInApi.revokeAccess(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {

                    }
                });
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RC_SIGN_IN:
                if (resultCode == RESULT_OK) {
                    GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                    handleSignInResult(result);
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
                    AlertDialog.Builder builder = new AlertDialog.Builder(context, getDialogTheme());
                    builder.setMessage(content)
                            .setTitle(getString(R.string.signOut))
                            .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (mGoogleApiClient.isConnected()) {
                                        revokeAccess();
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
            syncData.setVisible(true);
            signedIn = true;
            mSwipeRefreshLayout.setEnabled(true);
            if (autoSync) {
                lookIfSyncNeeded();
            }
        } else {
            Toast.makeText(this, "SignIn not successful!", Toast.LENGTH_SHORT).show();
            // Signed out
            personData.setOnClickListener(null);
            personData.setVisibility(View.GONE);
            signInButton.setVisibility(View.VISIBLE);
            syncData.setVisible(false);
            signedIn = false;
            mSwipeRefreshLayout.setEnabled(false);
        }
        saveSignedIn();
    }

    public void syncData() {
        if (signedIn) {
            mSwipeRefreshLayout.setRefreshing(true);
            //showToast("syncData()");
            readFromGoogleDrive();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void writeToGoogleDrive() {
        mDrawerLayout.closeDrawers();
        if (mGoogleApiClient.isConnected()) {
            //showToast("RetrieveDriveId write");
            if (driveId != null) {
                writeToFile(driveId);
            } else {
                new RetrieveDriveId(mGoogleApiClient, driveIdCallback_write, null).execute();
            }
        } else {
            showToast("Client not connected!");
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public void writeToFile(DriveId driveId) {
        DriveFile file = driveId.asDriveFile();
        String data = "";
        try {
            data = todolist.getDataWithColors(helper);
            JSONObject json = new JSONObject(data.toString());
            todolist.todolistTimeStamp = json.getLong("timeStamp");
            int statusCode = new EditFileInAppFolder(mGoogleApiClient, data).execute(file).get();
            //if (statusCode != CommonStatusCodes.SUCCESS) {
            showToast("EditFileInAppFolder StatusCode: " + CommonStatusCodes.getStatusCodeString(statusCode));
            //
            todolist.lastSyncTimeStamp = System.currentTimeMillis();
            mSwipeRefreshLayout.setRefreshing(false);
            //this.recreate();

        } catch (JSONException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createNewFile() {
        mDrawerLayout.closeDrawers();
        if (mGoogleApiClient.isConnected()) {
            if (todolist.getTodolist().size() > 0) {
                Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(contentsCallback);
            }
        } else {
            showToast("Client not connected!");
        }
    }

    public void readFromGoogleDrive() {
        mDrawerLayout.closeDrawers();
        if (mGoogleApiClient.isConnected()) {
            //showToast("requestSync");
            Drive.DriveApi.requestSync(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status result) {
                            if (!result.isSuccess()) {
                                if (result.getStatusCode() == DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED) {
                                    showToast("Sync currently not possible");
                                } else {
                                    showToast("Error: " + DriveStatusCodes.getStatusCodeString(result.getStatusCode()));
                                }
                                mSwipeRefreshLayout.setRefreshing(false);
                                return;
                            }
                            //showToast("RetrieveDriveId read");
                            if (driveId != null) {
                                readFromFile(driveId);
                            } else {
                                new RetrieveDriveId(mGoogleApiClient, driveIdCallback_read, null).execute();
                            }
                        }
                    });
        } else {
            showToast("ApiClient not connected!");
        }
    }

    public void readFromFile(DriveId driveId) {
        //showToast("readFromFile()");
        new RetrieveDataFromAppFolder(mGoogleApiClient, retrievedDataFromAppFolderCallback).execute(driveId);
    }

    public void DriveIdError(int statusCode) {
        if (statusCode == DriveStatusCodes.SUCCESS) {
            //File not found
            createNewFile();
        } else {
            showToast("Error while retrieving DriveId; Status-Code: " + DriveStatusCodes.getStatusCodeString(statusCode));
        }
    }

    public void lookIfSyncNeeded() {
        if(!signedIn){
            return;
        }
        if (todolist.hasSomethingChanged(MainActivity.this)) {
            showToast("syncData todolist Changed");
            if (mGoogleApiClient.isConnected()) {
                syncData();
            } else {
                pendingSync = true;
            }
            return;
        }
        if (todolist.lastSyncTimeStamp + 2 * 60 * 1000 > System.currentTimeMillis()) {
            showToast("Sync 2 minutes ago");
            //return;
        }
        final ModifiedDateCallback modifiedDateCallback = new ModifiedDateCallback() {
            @Override
            public void getModifiedDate(long timeStamp, DriveId driveId) {
                if (timeStamp > MainActivity.this.todolist.lastSyncTimeStamp) {
                    showToast("syncData DriveList Changed");
                    if (mGoogleApiClient.isConnected()) {
                        mSwipeRefreshLayout.setRefreshing(true);
                        MainActivity.this.driveId = driveId;
                        readFromFile(driveId);
                    } else {
                        pendingSync = true;
                    }
                }
            }
        };
        if (mGoogleApiClient.isConnected()) {
            Drive.DriveApi.requestSync(mGoogleApiClient)
                    .setResultCallback(new ResultCallback<Status>() {
                        @Override
                        public void onResult(Status result) {
                            if (!result.isSuccess()) {
                                if (result.getStatusCode() == DriveStatusCodes.DRIVE_RATE_LIMIT_EXCEEDED) {

                                } else {
                                    showToast("Error: " + DriveStatusCodes.getStatusCodeString(result.getStatusCode()));
                                }
                                return;
                            }
                            new RetrieveDriveId(mGoogleApiClient, null, modifiedDateCallback).execute();
                        }
                    });
        } else {
            pendingSyncRequest = true;
        }
    }*/

    public void showToast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }

    public void checkForIntentInputs() {
        Intent intent = getIntent();
        String action = intent.getAction();
        if (action != null) {
            switch (action) {
                case "notification_button":
                    long id = intent.getLongExtra("EventId", 0);
                    if (mAdapter != null) {
                        if (id != 0) {
                            EVENT e = todolist.getEventById(id);
                            if (e != null) {
                                if (todolist.isEventInAdapterList(mAdapter, e)) {
                                    removeEvent(todolist.getIndexOfEventInAdapterListById(mAdapter, id), true);
                                } else {
                                    todolist.removeEvent(e);
                                }
                            }
                        }
                    }
                    NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancel(intent.getIntExtra("NotificationId", 0));
                    break;
                case "widget_button":
                    if (addEventDialog != null) {
                        addEventDialog.dismiss();
                    }
                    FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
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
                default:
                    //do nothing
                    break;
            }
        }
        intent.setAction("no_action");
    }

    public void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.rv);
        mRecyclerView.setHasFixedSize(true);
        addOnItemTouchListenerToRecyclerView();

        mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        /*if (tablet) {
            //Put Tablet specific layout here
        } else {

        }*/

        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                CheckToolbarElevation();
            }
        });

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        //mSwipeRefreshLayout.setColorSchemeResources(helper.fab_color, helper.fab_color, helper.fab_color);
        mRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //MainActivity.this.syncData();
            }
        };
        mSwipeRefreshLayout.setOnRefreshListener(mRefreshListener);
    }

    public void setupDrawerLayout() {
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open, R.string.drawer_close) {
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
        navigationView = (NavigationView) findViewById(R.id.navigation_view);
        NavigationMenuView navigationMenuView = (NavigationMenuView) navigationView.getChildAt(0);
        if (navigationMenuView != null) {
            navigationMenuView.setVerticalScrollBarEnabled(false);
        }
        //find header Views
        View headerLayout = navigationView.getHeaderView(0);
        signInButton = (SignInButton) headerLayout.findViewById(R.id.sign_in_button);
        personName = (TextView) headerLayout.findViewById(R.id.personName);
        personEmail = (TextView) headerLayout.findViewById(R.id.personEmail);
        personData = (RelativeLayout) headerLayout.findViewById(R.id.personData);
        navigation_headers = new ArrayList<>();
        Menu m = navigationView.getMenu();
        for (int i = 0; i < m.size(); i++) {
            MenuItem mi = m.getItem(i);
            navigation_headers.add(mi);
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
                            final SwitchCompat shoNotif = (SwitchCompat) subMenu.getItem(j).getActionView();
                            shoNotif.setChecked(todolist.getShowNotification());
                            shoNotif.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    ShowNotificationToggleClicked();
                                    if (!(shoNotif.isChecked() && isChecked)) {
                                        shoNotif.setChecked(todolist.getShowNotification());
                                    }
                                }
                            });
                            break;
                        case R.id.silence_all_alarms:
                            mSilenceAllAlarms = subMenuItem;
                            if (!todolist.isAlarmScheduled()) {
                                subMenuItem.getActionView().setEnabled(false);
                            }
                            final SwitchCompat silence_all_alarms = (SwitchCompat) subMenu.getItem(j).getActionView();
                            silence_all_alarms.setChecked(!todolist.getVibrate());
                            silence_all_alarms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                @Override
                                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                    SilenceAlarmsClicked();
                                }
                            });
                            break;
                        case R.id.autoSync:
                            autoSyncMenuItem = subMenuItem;
                            final SwitchCompat autoSync = (SwitchCompat) subMenu.getItem(j).getActionView();
                            MainActivity.this.autoSync = getSharedPreferences("todolist", MODE_PRIVATE).getBoolean("autoSync", true);
                            autoSync.setChecked(MainActivity.this.autoSync);
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
                            case R.id.restore_last_removed_event:
                                RestoreLastDoneEventClicked();
                                break;
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

    public void setupTheme() {
        mToolbar.setBackgroundColor(helper.toolbar_color);
        mToolbar.setTitleTextColor(helper.toolbar_textcolor);
        ChangeColorOfToolbarDrawerIcon(helper.toolbar_textcolor);
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setBackgroundTintList(ColorStateList.valueOf(helper.fab_color));
        mFab.getDrawable().setTint(helper.fab_textcolor);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.setBackgroundColor(helper.cord_color);
        int color_grey = ContextCompat.getColor(context, R.color.grey);
        int color_dark = ContextCompat.getColor(context, R.color.black_light);
        final int[][] states = new int[3][];
        final int[] thumbColors = new int[3];
        final int[] trackColors = new int[3];
        int k = 0;

        // Disabled state
        states[k] = new int[]{-android.R.attr.state_enabled};
        thumbColors[k] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
        trackColors[k] = Color.argb(72, Color.red(color_dark), Color.green(color_dark), Color.blue(color_dark));
        k++;

        states[k] = new int[]{android.R.attr.state_checked};
        thumbColors[k] = helper.fab_color;
        trackColors[k] = Color.argb(72, Color.red(helper.fab_color), Color.green(helper.fab_color), Color.blue(helper.fab_color));
        k++;

        // Default enabled state
        states[k] = new int[0];
        thumbColors[k] = color_grey;
        trackColors[k] = color_dark;
        k++;

        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) mSilenceAllAlarms.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        ((SwitchCompat) mSilenceAllAlarms.getActionView()).setHighlightColor(helper.fab_color);
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) mSilenceAllAlarms.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) autoSyncMenuItem.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) autoSyncMenuItem.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) generalNotif.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
        DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) generalNotif.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

        for (int i = 0; i < navigation_headers.size(); i++) {
            SpannableString s = new SpannableString(navigation_headers.get(i).getTitle());
            s.setSpan(new ForegroundColorSpan(helper.fab_color), 0, s.length(), 0);
            navigation_headers.get(i).setTitle(s);
        }
    }

    public void checkForNotificationUpdate() {
        if (todolist.getTodolist().size() != 0 && todolist.getShowNotification()) {
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
        mItemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                todolist.EventMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                mAdapter.ItemMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
                todolist.saveMoveTimeStamp(context);
                checkForNotificationUpdate();
                return true;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                removeEvent(viewHolder.getAdapterPosition(), false);
            }

            @Override
            public boolean isLongPressDragEnabled() {
                return !introMode;
            }

            @Override
            public boolean isItemViewSwipeEnabled() {
                return !is_event_draged;
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, final RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                if (actionState == ItemTouchHelper.ACTION_STATE_DRAG && isCurrentlyActive) {
                    is_event_draged = true;
                    viewHolder.itemView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    if (!todolist.isAdapterListTodolist(mAdapter)) {
                        boolean[] all_categories_selected = new boolean[13];
                        for (int i = 1; i < all_categories_selected.length; i++) {
                            all_categories_selected[i] = true;
                        }
                        todolist.setEventsSemiTransparent(mAdapter);
                        todolist.addAllEventToAdapterList(mAdapter);
                        mRecyclerView.scrollToPosition(viewHolder.getAdapterPosition());
                    }
                    if (((RVAdapter.EventViewHolder) viewHolder).event.is_expanded) {
                        ((RVAdapter.EventViewHolder) viewHolder).collapse();
                    }
                    elevateToolbar();
                } else if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE && isCurrentlyActive) {
                    if (((RVAdapter.EventViewHolder) viewHolder).event.is_expanded) {
                        ((RVAdapter.EventViewHolder) viewHolder).collapse();
                    }
                    Display mdisp = getWindowManager().getDefaultDisplay();
                    Point mdispSize = new Point();
                    mdisp.getSize(mdispSize);
                    float sX = mdispSize.x / 2 - 50;
                    if (dX < -0.0f) {
                        viewHolder.itemView.setAlpha(1 - ((dX / (2 * (-1))) / sX));
                    } else {
                        viewHolder.itemView.setAlpha(1 - ((dX / 2) / sX));
                    }
                } else if (!isCurrentlyActive) {
                    if (dX == 0) {
                        viewHolder.itemView.setAlpha(1);
                    }
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            viewHolder.itemView.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                        }
                    }, 100);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            todolist.resetAllSemiTransparentEvents(mAdapter);
                            is_event_draged = false;
                        }
                    }, 500);
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CheckToolbarElevation();
                        }
                    }, 300);
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        mItemTouchHelper = new ItemTouchHelper(mItemTouchHelperCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        OnItemClickHelper.addTo(mRecyclerView).setOnItemClickListener(new OnItemClickInterface() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, RecyclerView.ViewHolder holder) {
                if (!isCardExpandingOrCollapsing()) {
                    RVAdapter.EventViewHolder viewHolder = (RVAdapter.EventViewHolder) holder;
                    viewHolder.cardClicked();
                    closeAllOpenCards(position);
                    if (introMode && intro_action_area) {
                        Intro_ActionArea();
                    }
                }
            }
        });
    }

    public void closeAllOpenCards(int position) {
        for (int i = 0; i < mAdapter.getList().size(); i++) {
            if (mAdapter.getList().get(i).is_expanded && i != position) {
                RVAdapter.EventViewHolder holder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
                if (holder != null) {
                    holder.collapse();
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
            RVAdapter.EventViewHolder holder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(i);
            if (holder != null) {
                if (holder.is_expanding_or_collapsing) {
                    return true;
                }
            }
        }
        return false;
    }

    public void removeEvent(int index, final boolean with_delay) {
        if (!introMode) {
            todolist.removeEvent(mAdapter, index);
            checkForNotificationUpdate();
            if (todolist.getTodolist().size() == 0 && !with_delay) {
                showNothingTodo();
            }
            handler.postDelayed(new Runnable() {
                public void run() {
                    CheckToolbarElevation();
                    if (todolist.getTodolist().size() == 0 && with_delay) {
                        showNothingTodo();
                    }
                }
            }, 400);
            EventRemovedSnackbar();
        } else {
            final RVAdapter.EventViewHolder eventViewHolder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
            eventViewHolder.itemView.setVisibility(View.GONE);
            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) eventViewHolder.itemView.getLayoutParams();
            layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
            eventViewHolder.itemView.setLayoutParams(layoutParams);
            mAdapter.removeItem(0);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intro_Done();
                    eventViewHolder.itemView.setVisibility(View.VISIBLE);
                    eventViewHolder.collapse_noAnimation();
                }
            }, 300);
            Intro_Done();
        }
    }

    public void actionButtonClicked(View v, EVENT e) {
        if (!actionButtonAlreadyClicked && !introMode) {
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

    public void ColorButtonClicked(EVENT e) {
        eventToColorChange = e;
        CategoryWasSelected = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(context, getDialogTheme());
        builder.setView(inflateColorSelector())
                .setTitle(getString(R.string.choose_a_color))
                .setCancelable(true)
                .setNegativeButton(getString(R.string.cancel), null)
                .setOnDismissListener(dismisslistener);
        ColorSelectorDialog = builder.create();
        ColorSelectorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        ColorSelectorDialog.show();
        changeDialogButtonColor(ColorSelectorDialog);
    }

    public void EditButtonClicked(final EVENT e) {
        final LayoutInflater inputDialog_layout = getLayoutInflater();
        final View inputDialog = inputDialog_layout.inflate(R.layout.input_dialog, null);
        final EditText editText = (EditText) inputDialog.findViewById(R.id.edit_text);
        editText.setTextColor(getDialogTextColor());
        editText.setText(e.getWhatToDo());
        editText.setSelection(e.getWhatToDo().length());

        AlertDialog.Builder input_dialog_builder = new AlertDialog.Builder(context, getDialogTheme());
        input_dialog_builder.setTitle(getString(R.string.edit_event))
                .setView(inputDialog)
                .setNegativeButton(getString(R.string.cancel), null)
                .setOnDismissListener(dismisslistener)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String s = editText.getText().toString();
                        e.EditWhatToDo(s);
                        checkForNotificationUpdate();
                        mAdapter.itemChanged(mAdapter.getList().indexOf(e));
                        try {
                            todolist.saveData(context);
                        } catch (JSONException e) {
                            Toast.makeText(context, "Error while saving Data (JSONException)", Toast.LENGTH_LONG).show();
                            e.printStackTrace();
                        }

                    }
                });

        final AlertDialog input_dialog = input_dialog_builder.create();
        input_dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        input_dialog.show();
        changeDialogButtonColor(input_dialog);
    }

    public void AlarmButtonClicked(EVENT e) {
        if (e.hasAlarm() && !todolist.hasAlarmFired(e)) {
            showAlarmInfoDialog(e);
        } else {
            //ShowAlarmTimePicker(e);
            ShowAlarmDatePicker(e);
        }
    }

    public void FabClicked(View v) {
        if (!introMode) {
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
    }

    public void addEvent() {
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Animation anim = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_scale_down);
        anim.setDuration(100);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fab.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fab.startAnimation(anim);

        final LayoutInflater inputDialog_layout = this.getLayoutInflater();
        final View inputDialog = inputDialog_layout.inflate(R.layout.add_event_dialog, null);
        final TextInputEditText editText = (TextInputEditText) inputDialog.findViewById(R.id.edit_text);
        final RadioButton color_rb = (RadioButton) inputDialog.findViewById(R.id.radio_button_color);
        final HorizontalScrollView horizontalScrollView = (HorizontalScrollView) inputDialog.findViewById(R.id.color_scroll_view);
        horizontalScrollView.setVisibility(View.GONE);
        final ImageButton[] buttons = getColorButtons(inputDialog);

        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(getColorByIndex(i), PorterDuff.Mode.SRC_ATOP);
        }

        if (default_color != 0) {
            buttons[default_color].setImageDrawable(getButtonForegroundRes(default_color));
            selected_color = default_color;
        } else {
            selected_color = 0;
        }
        Button.OnClickListener onClickListener = new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                if (selected_color != 0 || index == selected_color) {
                    buttons[selected_color].setImageResource(android.R.color.transparent);
                }
                if (index != selected_color) {
                    ImageButton imageButton = (ImageButton) v;
                    imageButton.setImageDrawable(getButtonForegroundRes(index));
                    selected_color = index;
                } else {
                    selected_color = 0;
                }
            }
        };

        Button.OnLongClickListener onLongClickListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int index = getColorIndexByButtonId(v.getId());
                if (default_color != index) {
                    default_color = index;
                    ImageButton imageButton = (ImageButton) v;
                    imageButton.setImageDrawable(getButtonForegroundRes(index));
                    if (selected_color != 0 && selected_color != default_color) {
                        buttons[selected_color].setImageResource(android.R.color.transparent);
                    }
                    selected_color = index;
                    Toast.makeText(MainActivity.this, "Default Color set", Toast.LENGTH_SHORT).show();
                } else {
                    buttons[index].setImageResource(android.R.color.transparent);
                    default_color = 0;
                    selected_color = 0;
                    Toast.makeText(MainActivity.this, "Default Color removed", Toast.LENGTH_SHORT).show();
                }
                saveDefaultColor();
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

        AlertDialog.Builder input_dialog_builder = new AlertDialog.Builder(context, getDialogTheme());
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
                        int color = selected_color;
                        boolean[] possible_colors = new boolean[selected_categories.length];
                        for (int i = 1; i < selected_categories.length; i++) {
                            if (selected_categories[i] || !todolist.doesCategoryContainEvents(i)) {
                                possible_colors[i] = true;
                            }
                        }
                        EVENT e = new EVENT(s, color, 0, possible_colors, 0);
                        todolist.addEvent(mAdapter, e);
                        selected_categories[e.getColor()] = true;
                        closeAllOpenCards(mAdapter.getItemCount());
                        todolist.addOrRemoveEventFromAdapter(mAdapter, selected_categories);
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

    public void saveDefaultColor() {
        todolist.saveDefaultColor(default_color, this);
    }

    public TODOLIST getTodolist() {
        return todolist;
    }

    public void removeNothingTodo() {
        final RelativeLayout nothingTodo = (RelativeLayout) findViewById(R.id.nothing_todo);
        if (nothingTodo.getVisibility() == View.GONE) {
            return;
        } else {
            Animation fadeOut = AnimationUtils.loadAnimation(this, R.anim.abc_fade_out);
            fadeOut.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    nothingTodo.setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            nothingTodo.startAnimation(fadeOut);
        }
    }

    public String getAddEventHint() {
        Random r = new Random();
        String s;
        switch (r.nextInt(3)) {
            case 0:
                s = getString(R.string.do_homework);
                break;
            case 1:
                s = getString(R.string.clean_kitchen);
                break;
            default:
                s = getString(R.string.do_laundry);
                break;
        }
        return s;
    }

    public void showAlarmInfoDialog(final EVENT e) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(e.getAlarmTimeInMills());
        int Hour = calendar.get(Calendar.HOUR_OF_DAY);
        int Minutes = calendar.get(Calendar.MINUTE);
        Calendar currentTime = Calendar.getInstance(TimeZone.getDefault());
        String s;
        if (calendar.get(Calendar.YEAR) == currentTime.get(Calendar.YEAR) && calendar.get(Calendar.MONTH) == currentTime.get(Calendar.MONTH)) {
            if (calendar.get(Calendar.DATE) == currentTime.get(Calendar.DATE)) {
                s = getString(R.string.today);
            } else if (currentTime.getTimeInMillis() + 24 * 60 * 60 * 1000 > calendar.getTimeInMillis()) {
                s = getString(R.string.tomorrow);
            } else {
                s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + getMonth(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR);
            }
        } else {
            s = String.valueOf(calendar.get(Calendar.DATE)) + ". " + getMonth(calendar.get(Calendar.MONTH)) + " " + calendar.get(Calendar.YEAR);
        }
        String content = getString(R.string.alarm_scheduled_for) + " " + "<b>" + s + " " + getString(R.string.at) + " " + Hour + ":" + String.format("%02d", Minutes) + "</b>";
        AlertDialog dialog = new AlertDialog.Builder(context, getDialogTheme())
                .setMessage(Html.fromHtml(content))
                .setTitle(getString(R.string.alarm))
                .setOnDismissListener(dismisslistener)
                .setNeutralButton(getString(R.string.edit_time), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //ShowAlarmTimePicker(e);
                        ShowAlarmDatePicker(e);
                    }
                })
                .setNegativeButton(getString(R.string.remove), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        removeAlarm(e, true);
                    }
                })
                .create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        dialog.show();
        changeDialogButtonColor(dialog);
    }

    public void ShowAlarmTimePicker(final EVENT e) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int currentHour = calendar.get(Calendar.HOUR_OF_DAY);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        int theme;
        if (helper.lightCordColor()) {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, theme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        final Calendar calendar = Calendar.getInstance();
                        calendar.setTimeInMillis(System.currentTimeMillis());
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE, minute);
                        calendar.set(Calendar.SECOND, 0);
                        calendar.set(Calendar.MILLISECOND, 0);
                        if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
                            AlertDialog dialog = new AlertDialog.Builder(context, getDialogTheme())
                                    .setMessage(getString(R.string.schedule_for_this_alarm_for_tomorrow))
                                    .setTitle(getString(R.string.tomorrow) + "?")
                                    .setNegativeButton(getString(R.string.cancel), null)
                                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            calendar.add(Calendar.HOUR, 24);
                                            setAlarm(calendar.getTimeInMillis(), e, true);
                                        }
                                    })
                                    .create();
                            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
                            dialog.show();
                            changeDialogButtonColor(dialog);
                        } else {
                            setAlarm(calendar.getTimeInMillis(), e, true);
                        }
                    }
                }, currentHour, currentMinutes, true);
        timePickerDialog.setOnDismissListener(dismisslistener);
        timePickerDialog.show();
    }

    public void ShowAlarmTimePicker(final EVENT e, final Calendar alarmDate) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int theme;
        if (helper.lightCordColor()) {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            theme = TimePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, theme,
                new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        alarmDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        alarmDate.set(Calendar.MINUTE, minute);
                        alarmDate.set(Calendar.SECOND, 0);
                        alarmDate.set(Calendar.MILLISECOND, 0);
                        if (alarmDate.getTimeInMillis() < System.currentTimeMillis()) {
                            Toast.makeText(MainActivity.this, "Your Date lies in the past", Toast.LENGTH_SHORT).show();
                        } else {
                            setAlarm(alarmDate.getTimeInMillis(), e, true);
                        }
                    }
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);
        timePickerDialog.setOnDismissListener(dismisslistener);
        timePickerDialog.show();
    }

    public void ShowAlarmDatePicker(final EVENT e) {
        final Calendar calendar = Calendar.getInstance(TimeZone.getDefault());
        int theme;
        if (helper.lightCordColor()) {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_LIGHT;
        } else {
            theme = DatePickerDialog.THEME_DEVICE_DEFAULT_DARK;
        }
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, theme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                if (calendar.getTimeInMillis() + 24 * 60 * 60 * 1000 < System.currentTimeMillis()) {
                    Toast.makeText(MainActivity.this, "Your Date lies in the past", Toast.LENGTH_SHORT).show();
                } else {
                    ShowAlarmTimePicker(e, calendar);
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.setOnDismissListener(dismisslistener);
        datePickerDialog.show();
    }

    public String getMonth(int month) {
        switch (month) {
            case 0:
                return "January";
            case 1:
                return "February";
            case 2:
                return "March";
            case 3:
                return "April";
            case 4:
                return "May";
            case 5:
                return "June";
            case 6:
                return "July";
            case 7:
                return "August";
            case 8:
                return "September";
            case 9:
                return "October";
            case 10:
                return "November";
            case 11:
                return "December";
        }
        return "Error";
    }

    public void setAlarm(long AlarmTime, EVENT e, boolean updateTimeStamp) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int id = (int) e.getId();
        Intent intent = new Intent(context, BroadcastReceiver.class);
        intent.putExtra("EventId", e.getId());
        intent.setAction("ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        mAlarmManager.setExact(AlarmManager.RTC_WAKEUP, AlarmTime, pendingIntent);
        if (updateTimeStamp) {
            e.setAlarmTimeInMills(AlarmTime);
            e.setAlarmId(id);
        } else {
            e.updateAlarm(id, AlarmTime);
        }
        showSnackbar(getString(R.string.alarm_was_set_for) + " " + e.getWhatToDo() + " " + getString(R.string.hinzugefuegt));
        mSilenceAllAlarms.setEnabled(true);
        mSilenceAllAlarms.getActionView().setEnabled(true);
        try {
            todolist.saveData(this);
        } catch (JSONException je) {
            Toast.makeText(context, "Error while saving Data (JSONException)", Toast.LENGTH_LONG).show();
            je.printStackTrace();
        }
    }

    public void removeAlarm(EVENT e, boolean updateTimeStamp) {
        AlarmManager mAlarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        int id = (int) e.getId();
        Intent intent = new Intent(context, BroadcastReceiver.class);
        intent.putExtra("EventId", e.getId());
        intent.setAction("ALARM");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, intent, 0);
        mAlarmManager.cancel(pendingIntent);
        if (updateTimeStamp) {
            e.setAlarmTimeInMills(0);
        } else {
            e.updateAlarm(0, 0);
        }
        if (!todolist.isAlarmScheduled()) {
            mSilenceAllAlarms.setEnabled(false);
            mSilenceAllAlarms.getActionView().setEnabled(false);
        }
        try {
            todolist.saveData(this);
        } catch (JSONException je) {
            Toast.makeText(context, "Error while saving Data (JSONException)", Toast.LENGTH_LONG).show();
            je.printStackTrace();
        }
        showSnackbar(getString(R.string.alarm_removed));
    }

    public void ColorButtonClicked(View v) {
        int color_index = getColorIndexByButtonId(v.getId());
        if (CategoryWasSelected) {
            if (!selected_categories[color_index]) {
                ImageButton imageButton = (ImageButton) v;
                imageButton.setImageDrawable(getButtonForegroundRes(color_index));
                selected_categories[color_index] = true;
            } else {
                ImageButton imageButton = (ImageButton) v;
                imageButton.setImageResource(android.R.color.transparent);
                selected_categories[color_index] = false;
            }
        } else {
            if (ColorSelectorDialog != null) {
                ColorSelectorDialog.dismiss();
            }
            if (color_index != eventToColorChange.getColor()) {
                eventToColorChange.setColor(color_index);
                mAdapter.itemChanged(mAdapter.getList().indexOf(eventToColorChange));
                if (!selected_categories[color_index]) {
                    int number_of_event_with_color_index = 0;
                    for (int i = 0; i < todolist.getTodolist().size(); i++) {
                        if (todolist.getTodolist().get(i).getColor() == color_index) {
                            number_of_event_with_color_index++;
                        }
                    }
                    if (number_of_event_with_color_index == 1) {
                        selected_categories[color_index] = true;
                    }
                }
                eventToColorChange = null;
            }
        }
    }

    public void RestoreLastDoneEventClicked() {
        mDrawerLayout.closeDrawers();
        EVENT e = todolist.getLastRemovedEvent();
        if (e != null) {
            if (mTextView != null) {
                mCoordinatorLayout.removeView(mTextView);
                mTextView = null;
            }
            removeNothingTodo();
            todolist.restoreLastRemovedEvent();
            selected_categories[e.getColor()] = true;
            todolist.addOrRemoveEventFromAdapter(mAdapter, selected_categories);
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
        if (todolist.getVibrate()) {
            todolist.setVibrate(false);
            showSnackbar(getString(R.string.all_alarm_are_now_silent));
        } else {
            todolist.setVibrate(true);
            showSnackbar(getString(R.string.now_the_Phone_will_vibrate_when_alarms_are_fired));
        }
    }

    public void AutoSyncClicked(boolean isChecked) {
        autoSync = isChecked;
        SharedPreferences.Editor editor = getSharedPreferences("todolist", MODE_PRIVATE).edit();
        editor.putBoolean("autoSync", autoSync);
        editor.apply();
    }

    public void SelectCategoryClicked() {
        mDrawerLayout.closeDrawers();
        CategoryWasSelected = true;
        AlertDialog.Builder builder1 = new AlertDialog.Builder(context, getDialogTheme());
        builder1.setView(inflateCategorySelector())
                .setTitle(getString(R.string.choose_a_category))
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        todolist.addOrRemoveEventFromAdapter(mAdapter, selected_categories);
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
        ColorSelectorDialog = builder1.create();
        ColorSelectorDialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimations;
        ColorSelectorDialog.show();
        changeDialogButtonColor(ColorSelectorDialog);
    }

    public void SelectThemeClicked() {
        mDrawerLayout.closeDrawers();
        newTheme = true;
        Intent intent = new Intent(this, ThemeActivity.class);
        startActivity(intent);
    }

    public void updateTheme() {
        if (newTheme) {
            helper = new ThemeHelper(this);
            mToolbar.setBackgroundColor(helper.toolbar_color);
            mToolbar.setTitleTextColor(helper.toolbar_textcolor);
            TextView nothing_todo_text = (TextView) findViewById(R.id.nothing_todo_text);
            nothing_todo_text.setTextColor(helper.toolbar_textcolor);
            ChangeColorOfToolbarDrawerIcon(helper.toolbar_textcolor);
            mFab.setBackgroundTintList(ColorStateList.valueOf(helper.fab_color));
            mFab.getDrawable().setTint(helper.fab_textcolor);
            mCoordinatorLayout.setBackgroundColor(helper.cord_color);
            int color_grey = ContextCompat.getColor(context, R.color.grey);
            int color_dark = ContextCompat.getColor(context, R.color.black_light);
            final int[][] states = new int[3][];
            final int[] thumbColors = new int[3];
            final int[] trackColors = new int[3];
            int k = 0;

            // Disabled state
            states[k] = new int[]{-android.R.attr.state_enabled};
            thumbColors[k] = Color.argb(72, Color.red(color_grey), Color.green(color_grey), Color.blue(color_grey));
            trackColors[k] = Color.argb(72, Color.red(color_dark), Color.green(color_dark), Color.blue(color_dark));
            k++;

            states[k] = new int[]{android.R.attr.state_checked};
            thumbColors[k] = helper.fab_color;
            trackColors[k] = Color.argb(72, Color.red(helper.fab_color), Color.green(helper.fab_color), Color.blue(helper.fab_color));
            k++;

            // Default enabled state
            states[k] = new int[0];
            thumbColors[k] = color_grey;
            trackColors[k] = color_dark;
            k++;

            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) mSilenceAllAlarms.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
            ((SwitchCompat) mSilenceAllAlarms.getActionView()).setHighlightColor(helper.fab_color);
            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) mSilenceAllAlarms.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) autoSyncMenuItem.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) autoSyncMenuItem.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));
            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) generalNotif.getActionView()).getThumbDrawable()), new ColorStateList(states, thumbColors));
            DrawableCompat.setTintList(DrawableCompat.wrap(((SwitchCompat) generalNotif.getActionView()).getTrackDrawable()), new ColorStateList(states, trackColors));

            for (int i = 0; i < navigation_headers.size(); i++) {
                SpannableString s = new SpannableString(navigation_headers.get(i).getTitle());
                s.setSpan(new ForegroundColorSpan(helper.fab_color), 0, s.length(), 0);
                navigation_headers.get(i).setTitle(s);
            }

            for (int i = 0; i < mAdapter.getList().size(); i++) {
                mAdapter.itemChanged(i);
            }
            newTheme = false;
        }
    }

    public AlertDialog changeDialogButtonColor(AlertDialog dialog) {
        Button positive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        if (positive != null) {
            positive.setTextColor(helper.fab_color);
        }
        Button neutral = dialog.getButton(DialogInterface.BUTTON_NEUTRAL);
        if (neutral != null) {
            neutral.setTextColor(helper.fab_color);
        }
        Button negative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        if (negative != null) {
            negative.setTextColor(helper.fab_color);
        }
        return dialog;
    }

    public void ShowNotificationToggleClicked() {
        if (todolist.getShowNotification()) {
            todolist.setShowNotification(false);
            showSnackbar(getString(R.string.general_notification_is_hidden));
            cancelNotification();
        } else {
            todolist.setShowNotification(true);
            showSnackbar(getString(R.string.general_notification_is_shown));
            checkForNotificationUpdate();
        }
        checkForNotificationUpdate();
    }

    public void InfoButtonClicked() {
        mDrawerLayout.closeDrawers();
        Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
        startActivity(intent);
    }

    public void setSelectedCategories(boolean[] selected_categories) {
        this.selected_categories = selected_categories;
    }

    public void showNothingTodo() {
        final RelativeLayout nothingTodo = (RelativeLayout) findViewById(R.id.nothing_todo);
        TextView nothing_todo_text = (TextView) findViewById(R.id.nothing_todo_text);
        nothing_todo_text.setTextColor(helper.toolbar_textcolor);
        if (nothingTodo.getVisibility() == View.VISIBLE) {
            return;
        } else {
            Animation anim = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
            anim.setStartOffset(300);
            anim.setDuration(500);
            anim.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    nothingTodo.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            nothingTodo.startAnimation(anim);
        }
    }

    /*public void DoneSyncingData() {
        writeToGoogleDrive();
        checkForNotificationUpdate();
        if (todolist.getTodolist().size() != 0) {
            removeNothingTodo();
        } else {
            showNothingTodo();
        }
        todolist.addOrRemoveEventFromAdapter(mAdapter, selected_categories);
    }*/

    public void addEvent(EVENT e, int position) {
        if (todolist.getTodolist().size() == 0) {
            removeNothingTodo();
        }
        todolist.addEvent(e, position, mAdapter);
        selected_categories[e.getColor()] = true;
        closeAllOpenCards(mAdapter.getItemCount());
        todolist.saveCategorySettings(MainActivity.this, selected_categories);
    }

    public void removeEvent(final int adapter_index) {
        if (adapter_index >= mAdapter.getList().size()) {
            return;
        }
        EVENT e = mAdapter.getList().get(adapter_index);
        todolist.getTodolist().remove(e);
        mAdapter.removeItem(adapter_index);
        if (todolist.getTodolist().size() == 0) {
            showNothingTodo();
        }
    }

    public void updateEvent(EVENT e, EVENT newEvent) {
        e.update(newEvent.getWhatToDo(), newEvent.getColor(), newEvent.getTimeStamp());
        if (e.getAlarmTimeInMills() != newEvent.getAlarmTimeInMills()) {
            //cancel old Alarm
            removeAlarm(e, false);
            setAlarm(newEvent.getAlarmTimeInMills(), e, false);
        }
        selected_categories[newEvent.getColor()] = true;
        todolist.saveCategorySettings(MainActivity.this, selected_categories);
        mAdapter.itemChanged(todolist.getAdapterListPosition(mAdapter, e));
    }

    public void moveEvent(EVENT e, int toPosition) {
        todolist.EventMoved(todolist.getTodolist().indexOf(e), toPosition);
        mAdapter.ItemMoved(todolist.getAdapterListPosition(mAdapter, e), toPosition);
    }

    public void CheckToolbarElevation() {
        int position = mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
        if (position == 0 || mAdapter.getList().size() == 0) {
            deelevateToolbar();
        } else {
            elevateToolbar();
        }
    }

    public void elevateToolbar() {
        mToolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
    }

    public void deelevateToolbar() {
        mToolbar.setElevation(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0, getResources().getDisplayMetrics()));
    }


    public void ChangeColorOfToolbarDrawerIcon(int color) {
        if (drawerIcon != null) {
            ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        } else {
            for (int i = 0; i < mToolbar.getChildCount(); i++) {
                if (mToolbar.getChildAt(i) instanceof ImageView) {
                    drawerIcon = mToolbar.getChildAt(i);
                    ((ImageView) drawerIcon).setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
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

        for (int i = 1; i < selected_categories.length; i++) {
            int colorIndex = getColorIndexByButtonId(buttons[i].getId());
            if (!todolist.doesCategoryContainEvents(colorIndex)) {
                buttons[i].setEnabled(false);
                buttons[i].setElevation(0);
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
        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(getColorByIndex(i), PorterDuff.Mode.SRC_ATOP);
        }
        return layout;
    }

    public int getColorByIndex(int index) {
        return helper.getEventColor(index);
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

        for (int i = 1; i < buttons.length; i++) {
            buttons[i].getBackground().setColorFilter(getColorByIndex(i), PorterDuff.Mode.SRC_ATOP);
        }
        return buttons;
    }

    public Drawable getButtonForegroundRes(int color_index) {
        Drawable d = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_selected_light, null).getConstantState().newDrawable().mutate();
        d.setColorFilter(helper.getEventTextColor(color_index), PorterDuff.Mode.SRC_ATOP);
        return d;
    }

    public void showSnackbar(String content) {
        if (mFab == null){
            mFab = (FloatingActionButton) findViewById(R.id.fab);
        }
        snackbar = Snackbar.make(mFab, content, Snackbar.LENGTH_LONG);
        snackbar.show();
    }

    public void EventRemovedSnackbar() {
        if (mFab == null){
            mFab = (FloatingActionButton) findViewById(R.id.fab);
        }
        snackbar = Snackbar.make(mFab, getString(R.string.event_removed), Snackbar.LENGTH_LONG);
        snackbar.setActionTextColor(helper.fab_color)
                .setAction(getString(R.string.undo), new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        RestoreLastDoneEventClicked();
                    }
                });
        snackbar.show();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
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
        todolist.saveSettings(this);
        todolist.saveCategorySettings(this, selected_categories);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    protected void onPause() {
        //lookIfSyncNeeded();
        try {
            todolist.saveData(this);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        todolist.saveSettings(this);
        todolist.saveCategorySettings(this, selected_categories);
        super.onPause();
    }

    @Override
    protected void onResume() {
        //lookIfSyncNeeded();
        updateTheme();
        checkForIntentInputs();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        // GoogleApiClient connected
        /*if(signedIn){
            if (pendingSync) {
                pendingSync = false;
                syncData();
            }
            if (pendingSyncRequest) {
                pendingSyncRequest = false;
                lookIfSyncNeeded();
            }
        }*/
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // GoogleApiClient connection suspended
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // GoogleApiClient connection failed
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    //Methods for the Intro Mode
    public void IntroMode() {
        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setImageResource(R.drawable.ic_arrow_fltr_white_24dp);
        mToolbar.setVisibility(View.INVISIBLE);
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
        mCoordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinatorLayout);
        mCoordinatorLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.dark_background));

        mIndicatorTextView = new TextView(this);
        CoordinatorLayout.LayoutParams indicatorTextViewLayoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        indicatorTextViewLayoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 28, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mIndicatorTextView.setLayoutParams(indicatorTextViewLayoutParams);
        mIndicatorTextView.setGravity(Gravity.BOTTOM | Gravity.START);
        mIndicatorTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mIndicatorTextView.setTextColor(ContextCompat.getColor(context, R.color.grey));
        mCoordinatorLayout.addView(mIndicatorTextView);

        mIntroTextView = new TextView(this);
        CoordinatorLayout.LayoutParams introTextViewLayoutParams = new CoordinatorLayout.LayoutParams(CoordinatorLayout.LayoutParams.WRAP_CONTENT, CoordinatorLayout.LayoutParams.WRAP_CONTENT);
        introTextViewLayoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 74, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics()),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
        mIntroTextView.setLayoutParams(introTextViewLayoutParams);
        mIntroTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 30);
        mIntroTextView.setTextColor(ContextCompat.getColor(context, R.color.white));
        mCoordinatorLayout.addView(mIntroTextView);

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intro_event) {
                    Intro_showEvent();
                } else if (intro_event_tap) {
                    Intro_tapEvent();
                } else if (intro_action_area) {
                    RVAdapter.EventViewHolder eventViewHolder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                    eventViewHolder.expand();
                    Intro_ActionArea();
                } else if (intro_swipe) {
                    Intro_SwipeEvent();
                } else {
                    mFab.setOnClickListener(null);
                    Animation anim = AnimationUtils.loadAnimation(MainActivity.this, R.anim.event_swipe);
                    anim.setAnimationListener(new Animation.AnimationListener() {
                        @Override
                        public void onAnimationStart(Animation animation) {
                        }

                        @Override
                        public void onAnimationEnd(Animation animation) {
                            final RVAdapter.EventViewHolder eventViewHolder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                            eventViewHolder.itemView.setVisibility(View.GONE);
                            RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) eventViewHolder.itemView.getLayoutParams();
                            layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()),
                                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                                    (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
                            eventViewHolder.itemView.setLayoutParams(layoutParams);
                            mAdapter.removeItem(0);
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    Intro_Done();
                                    eventViewHolder.itemView.setVisibility(View.VISIBLE);
                                    eventViewHolder.collapse_noAnimation();
                                }
                            }, 300);
                        }

                        @Override
                        public void onAnimationRepeat(Animation animation) {
                        }
                    });
                    RVAdapter.EventViewHolder eventViewHolder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                    eventViewHolder.itemView.startAnimation(anim);
                }
            }
        });
        fade_in = AnimationUtils.loadAnimation(this, R.anim.abc_fade_in);
        fade_in.setStartOffset(300);
        fade_in.setDuration(500);
        showWelcomeText();
    }

    public void showWelcomeText() {
        mIntroTextView.setText(getString(R.string.welcome));
        mIntroTextView.startAnimation(fade_in);
        mIndicatorTextView.setText("1/5");
    }

    public void Intro_showEvent() {
        intro_event = false;
        EVENT e = new EVENT(getAddEventHint(), 0, 0, null, 0);
        mAdapter.addItem(e);
        mIntroTextView.setText(getString(R.string.this_is_a_todo));
        mIntroTextView.startAnimation(fade_in);
        mIndicatorTextView.setText("2/5");
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                final RVAdapter.EventViewHolder eventViewHolder = (RVAdapter.EventViewHolder) mRecyclerView.findViewHolderForAdapterPosition(0);
                RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) eventViewHolder.itemView.getLayoutParams();
                eventViewHolder.intro_card = true;
                layoutParams.setMargins((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()),
                        (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics()));
                eventViewHolder.itemView.setLayoutParams(layoutParams);
            }
        }, 10);
    }

    public void Intro_tapEvent() {
        intro_event_tap = false;
        mIntroTextView.setText(getString(R.string.tap_event));
        mIntroTextView.startAnimation(fade_in);
        mIndicatorTextView.setText("3/5");
    }

    public void Intro_ActionArea() {
        intro_action_area = false;
        mIntroTextView.setText(getString(R.string.action_area));
        mIntroTextView.startAnimation(fade_in);
        mIndicatorTextView.setText("4/5");
    }

    public void Intro_SwipeEvent() {
        intro_swipe = false;
        mIntroTextView.setText(getString(R.string.swipe_event));
        mIntroTextView.startAnimation(fade_in);
        mIndicatorTextView.setText("5/5");
    }

    public void Intro_Done() {
        mCoordinatorLayout.removeView(mIntroTextView);
        mCoordinatorLayout.removeView(mIndicatorTextView);
        mIntroTextView = null;
        mIndicatorTextView = null;
        mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

        Animation anim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setStartOffset(300);
        anim.setDuration(500);
        mFab.setOnClickListener(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mFab.setImageResource(R.drawable.ic_add);
            }
        }, 600);
        anim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mFab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        FabClicked(v);
                    }
                });
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mFab.startAnimation(anim);

        fade_in.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mToolbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                fade_in = null;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        mToolbar.startAnimation(fade_in);
        introMode = false;
        showNothingTodo();
    }
}
