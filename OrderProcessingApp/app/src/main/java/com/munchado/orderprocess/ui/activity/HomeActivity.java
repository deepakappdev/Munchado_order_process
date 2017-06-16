package com.munchado.orderprocess.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.dinein.ArchiveReservation;
import com.munchado.orderprocess.model.dinein.UpcomingReservation;
import com.munchado.orderprocess.model.login.StatusResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.notification.PubnubService;
import com.munchado.orderprocess.ui.adapter.DineinArchiveAdapter;
import com.munchado.orderprocess.ui.fragment.CustomErrorDialogFragment;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PowerManager.WakeLock mWakeLock;
    private final ArrayList<View> mMenuItems = new ArrayList<>();
    public List<UpcomingReservation> upcommingReservationList = new ArrayList<>();
    ;
    public List<ArchiveReservation> archiveReservationList = new ArrayList<>();

    public DineinArchiveAdapter mDineinArchiveAdapter;
    boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toolbar.setTitle("Active Orders");
        setTypeFace();
        addFragment(FRAGMENTS.ACTIVE, null);
        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);

        final Menu navMenu = navigationView.getMenu();
        // Install an OnGlobalLayoutListener and wait for the NavigationMenu to fully initialize
        navigationView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                navigationView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                String id = "nav_order";
                MenuItem item = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                navigationView.findViewsWithText(mMenuItems, item.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                id = "nav_dinein";
                MenuItem item33 = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                navigationView.findViewsWithText(mMenuItems, item33.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                id = "nav_manage";
                MenuItem item1 = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                navigationView.findViewsWithText(mMenuItems, item1.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                id = "nav_logout";
                MenuItem item2 = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                navigationView.findViewsWithText(mMenuItems, item2.getTitle(), View.FIND_VIEWS_WITH_TEXT);

                Typeface face = Typeface.createFromAsset(getAssets(),
                        "HelveticaNeue-Medium.ttf");
                for (final View menuItem : mMenuItems) {
                    ((TextView) menuItem).setTextSize(18.0f);
                    ((TextView) menuItem).setTypeface(face, Typeface.NORMAL);
                    ((TextView) menuItem).setTextColor(ContextCompat.getColor(HomeActivity.this, R.color.dark_grey));
                }
            }
        });


        navigationView.setNavigationItemSelectedListener(this);
    }

    private void keepScreenOn() {
        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        this.mWakeLock = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag");
        this.mWakeLock.acquire();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
                getSupportFragmentManager().popBackStack();
                setCurrentFragmentTitle();

            } else
                finish();
        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_order) {
            popToHomePage();
        } else if (id == R.id.nav_dinein) {
            popToHomePage();
            addFragment(FRAGMENTS.DINE_IN, null);
        } else if (id == R.id.nav_manage) {
            popToHomePage();
            addFragment(FRAGMENTS.PRINT, null);
        } else if (id == R.id.nav_logout) {

            hitLogout();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void hitLogout() {
        DialogUtil.showProgressDialog(HomeActivity.this);
        RequestController.logout("", "", new RequestCallback() {
            @Override
            public void error(NetworkError volleyError) {
                if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage())) {
                    if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token"))
                        Utils.showLogin(HomeActivity.this);
                }
            }

            @Override
            public void success(Object response) {
                DialogUtil.hideProgressDialog();
                StatusResponse mStatusResponse = (StatusResponse) response;
                if (mStatusResponse.data.message) {
                    PrefUtil.clearAllData();
                    Intent i = new Intent(HomeActivity.this, PubnubService.class);
                    i.putExtra(Constants.PARAM_PUBNUB_ACTION, Constants.PARAM_PUBNUB_UNSUBSCRIBE);
                    startService(i);
                    startActivity(new Intent(HomeActivity.this, LoginActivity.class));
                    finish();
                } else {
                    CustomErrorDialogFragment errorDialogFragment = CustomErrorDialogFragment.newInstance("Unable to login.");
                    errorDialogFragment.show(getSupportFragmentManager(), "Error");
                }

            }
        });
    }

    public void setCustomTitle(String title) {
        if (toolbar != null) {
            toolbar.setTitle(title);
            setTypeFace();
        }
    }

    public void startPlaySoundForNewBookings() {
        if (!isFirst) {
            isFirst = true;
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    playRing();
                }
            }, 0, 5000);
        }
    }

    private void playRing() {
        try {
            if (upcommingReservationList != null && upcommingReservationList.size() > 0)
                for (UpcomingReservation item : upcommingReservationList) {
                    LogUtils.d("========= " + item.first_name + "=====" + item.status);
                    if (item.status.equalsIgnoreCase("0")) {

                        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                        Ringtone r = RingtoneManager.getRingtone(this, notification);
                        r.play();
                        break;


                    }
                }
        } catch (Exception e) {
        }
    }

    private void setTypeFace() {
        TextView titleTextView = null;

        try {
            Field f = toolbar.getClass().getDeclaredField("mTitleTextView");
            f.setAccessible(true);
            titleTextView = (TextView) f.get(toolbar);
            Typeface face = Typeface.createFromAsset(getAssets(),
                    "HelveticaNeue-Medium.ttf");
            titleTextView.setTypeface(face, Typeface.BOLD);
        } catch (NoSuchFieldException e) {
        } catch (IllegalAccessException e) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initPub();
        if (PrefUtil.isScreenOn()) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
            keepScreenOn();

        }
    }

    private void initPub() {
        Intent i = new Intent(this, PubnubService.class);
        i.putExtra(Constants.PARAM_PUBNUB_ACTION, Constants.PARAM_PUBNUB_SUBSCRIBE);
        startService(i);

    }

    @Override
    protected void onDestroy() {
        if (this.mWakeLock != null)
            this.mWakeLock.release();
        super.onDestroy();
    }
}
