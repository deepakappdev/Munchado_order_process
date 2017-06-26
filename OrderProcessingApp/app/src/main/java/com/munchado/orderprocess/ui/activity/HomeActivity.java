package com.munchado.orderprocess.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.archiveorder.AllOrderItem;
import com.munchado.orderprocess.model.archiveorder.AllOrderResponse;
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
import com.munchado.orderprocess.utils.DateUtils;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;
import com.munchado.orderprocess.utils.WifiPrintReceiptFormatUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

import static com.munchado.orderprocess.utils.DateTimeUtils.FORMAT_YYYY_MM_DD;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    PowerManager.WakeLock mWakeLock;
    private final ArrayList<View> mMenuItems = new ArrayList<>();
    public List<UpcomingReservation> upcommingReservationList = new ArrayList<>();
    public List<ArchiveReservation> archiveReservationList = new ArrayList<>();
    public DineinArchiveAdapter mDineinArchiveAdapter;
    boolean isFirst;
    private int mYear, mMonth, mDay;
    String from_date_str, to_date_str, from_old_date_str, to_old_date_str;
    private Button btn_from, btn_to, btn_submit;
    Calendar calendar = Calendar.getInstance();
    SimpleDateFormat sdf = new SimpleDateFormat(FORMAT_YYYY_MM_DD);
    private List<AllOrderItem> allOrderItemList;
    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;

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
                    if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found"))
                        Utils.showLogin(HomeActivity.this);
                }
            }

            @Override
            public void success(Object response) {
                DialogUtil.hideProgressDialog();
                StatusResponse mStatusResponse = (StatusResponse) response;
                if (mStatusResponse.data.message) {
                    String username = PrefUtil.getUsername();
                    String password = PrefUtil.getPassword();
                    PrefUtil.clearAllData();
                    PrefUtil.putUsername(username);
                    PrefUtil.putPassword(password);
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

    public void showDownloadDialog() {

        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.setContentView(R.layout.dialog_order_filter);
        dialog.setCancelable(false);
        dialog.show();

        btn_from = (Button) dialog.findViewById(R.id.btn_from);
        btn_to = (Button) dialog.findViewById(R.id.btn_to);
        btn_submit = (Button) dialog.findViewById(R.id.btn_submit);
        Button btn_download = (Button) dialog.findViewById(R.id.btn_download);
        TextView text_cancel = (TextView) dialog.findViewById(R.id.text_cancel);
        from_old_date_str = "";
        to_old_date_str = "";
        setDate(btn_from);
        setDate(btn_to);
        // if decline button is clicked, close the custom dialog
        btn_to.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog1 = new DatePickerDialog(HomeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                btn_to.setText(StringUtils.formatSingleDigit(dayOfMonth) + "-" + StringUtils.formatSingleDigit(monthOfYear + 1) + "-" + year);
                                to_date_str = year + "-" + StringUtils.formatSingleDigit(monthOfYear + 1) + "-" + StringUtils.formatSingleDigit(dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dialog1.getDatePicker().setMaxDate(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
                dialog1.show();
            }
        });
        btn_from.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYear = calendar.get(Calendar.YEAR);
                mMonth = calendar.get(Calendar.MONTH);
                mDay = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog1 = new DatePickerDialog(HomeActivity.this,
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {


                                btn_from.setText(StringUtils.formatSingleDigit(dayOfMonth) + "-" + StringUtils.formatSingleDigit(monthOfYear + 1) + "-" + year);
                                from_date_str = year + "-" + StringUtils.formatSingleDigit(monthOfYear + 1) + "-" + StringUtils.formatSingleDigit(dayOfMonth);
                            }
                        }, mYear, mMonth, mDay);
                dialog1.getDatePicker().setMaxDate(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(1));
                dialog1.show();
            }
        });
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LogUtils.d("===== " + from_date_str + " is before " + to_date_str + " === " + DateUtils.isBeforeDay(from_date_str, to_date_str));
                if (!DateUtils.isBeforeDay(from_date_str, to_date_str)) {
                    Toast.makeText(HomeActivity.this, "Please select valid date.", Toast.LENGTH_SHORT).show();
                    return;
                }
                DialogUtil.showProgressDialog(HomeActivity.this);
                RequestController.getAllOrder(HomeActivity.this, from_date_str, to_date_str, new RequestCallback() {
                    @Override
                    public void error(NetworkError volleyError) {
                        DialogUtil.hideProgressDialog();
                        from_old_date_str = from_date_str;
                        to_old_date_str = to_date_str;
                    }

                    @Override
                    public void success(Object obj) {
                        DialogUtil.hideProgressDialog();
                        AllOrderResponse response = (AllOrderResponse) obj;
                        allOrderItemList = response.data.orders;
                        from_old_date_str = from_date_str;
                        to_old_date_str = to_date_str;
//                        LogUtils.d("============ list size : " + allOrderItemList.size());
                    }
                });
            }
        });
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StringUtils.isNullOrEmpty(allOrderItemList)) {
                    if (!StringUtils.isNullOrEmpty(from_old_date_str) && !StringUtils.isNullOrEmpty(to_old_date_str)) {
                        if (!from_old_date_str.equalsIgnoreCase(from_date_str) || !to_old_date_str.equalsIgnoreCase(to_date_str)) {
                            Toast.makeText(HomeActivity.this, "Please tap \"Submit\" button.", Toast.LENGTH_SHORT).show();
                        } else
                            Toast.makeText(HomeActivity.this, "No records found between " + btn_from.getText() + " and " + btn_to.getText(), Toast.LENGTH_SHORT).show();
                    } else if (StringUtils.isNullOrEmpty(from_old_date_str) || StringUtils.isNullOrEmpty(to_old_date_str))
                        Toast.makeText(HomeActivity.this, "Please tap \"Submit\" button.", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(HomeActivity.this, "No records found between " + btn_from.getText() + " and " + btn_to.getText(), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (checkExternalStoragePermission(HomeActivity.this)) {
                    // Continue with your action after permission request succeed
                    writeFile();

                }
                dialog.dismiss();
            }
        });
        text_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close dialog
                dialog.dismiss();
            }
        });
    }

    private void setDate(Button btn) {
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH) - 1;
        btn.setText(StringUtils.formatSingleDigit(mDay) + "-" + StringUtils.formatSingleDigit(mMonth + 1) + "-" + mYear);
        from_date_str = mYear + "-" + StringUtils.formatSingleDigit(mMonth + 1) + "-" + StringUtils.formatSingleDigit(mDay);
        to_date_str = mYear + "-" + StringUtils.formatSingleDigit(mMonth + 1) + "-" + StringUtils.formatSingleDigit(mDay);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public static final String[] PERMISSIONS_EXTERNAL_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public boolean checkExternalStoragePermission(Activity activity) {
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            return true;
        }

        int readStoragePermissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int writeStoragePermissionState = ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        boolean externalStoragePermissionGranted = readStoragePermissionState == PackageManager.PERMISSION_GRANTED &&
                writeStoragePermissionState == PackageManager.PERMISSION_GRANTED;
        if (!externalStoragePermissionGranted) {
            requestPermissions(PERMISSIONS_EXTERNAL_STORAGE, REQUEST_EXTERNAL_PERMISSION_CODE);
        }

        return externalStoragePermissionGranted;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (requestCode == REQUEST_EXTERNAL_PERMISSION_CODE) {
                if (checkExternalStoragePermission(HomeActivity.this)) {
                    writeFile();
                }
            }
        }
    }

    private void writeFile() {
        final File file = new WifiPrintReceiptFormatUtils().createPDF(allOrderItemList, "Order_" + from_date_str + "_to_" + to_date_str + ".pdf", HomeActivity.this);
        Toast.makeText(HomeActivity.this, "Pdf is saved at location : " + file.getAbsolutePath(), Toast.LENGTH_SHORT).show();
    }
}
