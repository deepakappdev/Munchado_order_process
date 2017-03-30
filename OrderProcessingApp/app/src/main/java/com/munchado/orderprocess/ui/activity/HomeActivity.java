package com.munchado.orderprocess.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.design.widget.NavigationView;
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
import com.munchado.orderprocess.notification.PubnubService;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.PrefUtil;

import java.util.ArrayList;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    Toolbar toolbar;
    PowerManager.WakeLock mWakeLock;
    private final ArrayList<View> mMenuItems = new ArrayList<>(3);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        toolbar.setTitle("Active Orders");
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
                    id = "nav_manage";
                    MenuItem item1 = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                    navigationView.findViewsWithText(mMenuItems, item1.getTitle(), View.FIND_VIEWS_WITH_TEXT);
                    id = "nav_logout";
                    MenuItem item2 = navMenu.findItem(getResources().getIdentifier(id, "id", getPackageName()));
                    navigationView.findViewsWithText(mMenuItems, item2.getTitle(), View.FIND_VIEWS_WITH_TEXT);

                Typeface face = Typeface.createFromAsset(getAssets(),
                        "Avenir-Next-Demi-Bold.ttf");
                for (final View menuItem : mMenuItems) {
                    ((TextView) menuItem).setTextSize(20.0f);
                    ((TextView) menuItem).setTypeface(face, Typeface.NORMAL);
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
            if (getSupportFragmentManager().getBackStackEntryCount() > 1)
                getSupportFragmentManager().popBackStack();
            else
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
        } else if (id == R.id.nav_manage) {
            popToHomePage();
            addFragment(FRAGMENTS.PRINT, null);
        } else if (id == R.id.nav_logout) {
            PrefUtil.clearAllData();
            Intent i = new Intent(HomeActivity.this, PubnubService.class);
            i.putExtra(Constants.PARAM_PUBNUB_ACTION, Constants.PARAM_PUBNUB_UNSUBSCRIBE);
            startService(i);
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void setCustomTitle(String title) {
        if (toolbar != null)
            toolbar.setTitle(title);
    }


    @Override
    public void onResume() {
        super.onResume();
        initPub();
        if (PrefUtil.isScreenOn()) {
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
