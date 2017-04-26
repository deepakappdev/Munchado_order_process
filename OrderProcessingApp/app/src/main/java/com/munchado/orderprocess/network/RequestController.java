package com.munchado.orderprocess.network;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;

import com.android.volley.Cache;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.model.BaseResponse;
import com.munchado.orderprocess.model.archiveorder.ActiveOrderResponse;
import com.munchado.orderprocess.model.archiveorder.ArchiveOrderResponse;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.model.profile.RestaurantProfileResponse;
import com.munchado.orderprocess.model.token.TokenResponse;
import com.munchado.orderprocess.network.request.BaseRequest;
import com.munchado.orderprocess.network.request.GetActiveOrderRequest;
import com.munchado.orderprocess.network.request.GetArchiveOrderRequest;
import com.munchado.orderprocess.network.request.GetOrderDetailRequest;
import com.munchado.orderprocess.network.request.GetRestaurantProfileRequest;
import com.munchado.orderprocess.network.request.LoginRequest;
import com.munchado.orderprocess.network.request.LogoutRequest;
import com.munchado.orderprocess.network.request.NewTokenRequest;
import com.munchado.orderprocess.network.request.OrderProcessRequest;
import com.munchado.orderprocess.network.request.UpdateAppRequest;
import com.munchado.orderprocess.network.volley.GsonRequest;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

import java.util.concurrent.TimeUnit;

public class RequestController {
    public static final String TAG = "RequestController";
    private static final int DEFAULT_MAX_RETRIES = 2;
    private static RetryPolicy rp = new DefaultRetryPolicy((int) TimeUnit.SECONDS.toMillis(60), DEFAULT_MAX_RETRIES,
            DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
    private static RequestQueue mRequestQueue;
    private static Context context;
    private static String apk_link, upgrade_type;

    public static RetryPolicy getRetryPolicy() {
        return rp;
    }

    private static Response.ErrorListener getErrorListener(final RequestCallback callback) {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                LogUtils.e("====" + new String(volleyError.networkResponse.data));
                LogUtils.e("==== Error: ", volleyError.getLocalizedMessage());
                if (callback != null)
                    callback.error(new NetworkError(volleyError));

            }
        };
    }

    private static Response.Listener getListener(final Context ctx, final RequestCallback callback, final BaseRequest request) {
        return new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (response == null)
                    callback.error(new NetworkError(new VolleyError("Something went wrong")));
                else if (response instanceof BaseResponse) {
                    if (((BaseResponse) response).is_token_valid) {

                        if (ctx != null) {
                            context = ctx;
                            if (response instanceof ActiveOrderResponse && ((ActiveOrderResponse) response).data != null && ((ActiveOrderResponse) response).data.fource_update != null && !StringUtils.isNullOrEmpty(((ActiveOrderResponse) response).data.fource_update.upgrade_type)) {
                                if (((ActiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("hard") || ((ActiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("soft")) {
                                    if (PrefUtil.getUpgradeType().isEmpty()) {
                                        PrefUtil.setUpgradeDisplayCount(((ActiveOrderResponse) response).data.fource_update.counter);
                                    }
                                    updateForceUpgradeData(ctx, ((ActiveOrderResponse) response).data.fource_update.clear_data, ((ActiveOrderResponse) response).data.fource_update.upgrade_type, ((ActiveOrderResponse) response).data.fource_update.message, ((ActiveOrderResponse) response).data.fource_update.apk_link);

                                    callback.success(response);
                                } else {
                                    PrefUtil.setUpgradeDisplayCount(0);
                                    PrefUtil.setUpgradeType("");
                                    callback.success(response);
                                }
                            } else if (response instanceof ArchiveOrderResponse && ((ArchiveOrderResponse) response).data != null && ((ArchiveOrderResponse) response).data.fource_update != null && !StringUtils.isNullOrEmpty(((ArchiveOrderResponse) response).data.fource_update.upgrade_type)) {
                                if (((ArchiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("hard") || ((ArchiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("soft")) {
                                    if (PrefUtil.getUpgradeType().isEmpty()) {
                                        PrefUtil.setUpgradeDisplayCount(((ArchiveOrderResponse) response).data.fource_update.counter);
                                    }
//                                    PrefUtil.setUpgradeClearData(((ArchiveOrderResponse) response).data.fource_update.clear_data);
//                                    PrefUtil.setUpgradeType(((ArchiveOrderResponse) response).data.fource_update.upgrade_type);
//                                    PrefUtil.setUpgradeMessage(((ArchiveOrderResponse) response).data.fource_update.message);
//                                    apk_link = ((ArchiveOrderResponse) response).data.fource_update.apk_link;
//                                    upgrade_type = ((ArchiveOrderResponse) response).data.fource_update.upgrade_type;
//                                    if (((ArchiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("hard")) {
//                                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                                        builder.setMessage("MA App Update Available.").setPositiveButton("Update", dialogClickListener).setCancelable(false).show();
//                                    } else if ((((ArchiveOrderResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("soft") && PrefUtil.getUpgradeDisplayCount() == 3)) {
//                                        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
//                                        builder.setMessage("MA App Update Available.").setPositiveButton("Update", dialogClickListener)
//                                                .setNegativeButton("Cancel", dialogClickListener).setCancelable(false).show();
//                                    }
                                    updateForceUpgradeData(ctx, ((ArchiveOrderResponse) response).data.fource_update.clear_data, ((ArchiveOrderResponse) response).data.fource_update.upgrade_type, ((ArchiveOrderResponse) response).data.fource_update.message, ((ArchiveOrderResponse) response).data.fource_update.apk_link);

                                    callback.success(response);
                                } else {
                                    PrefUtil.setUpgradeDisplayCount(0);
                                    PrefUtil.setUpgradeType("");
                                    callback.success(response);
                                }
                            } else if (response instanceof OrderDetailResponse && ((OrderDetailResponse) response).data != null && ((OrderDetailResponse) response).data.fource_update != null && !StringUtils.isNullOrEmpty(((OrderDetailResponse) response).data.fource_update.upgrade_type)) {
                                if (((OrderDetailResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("hard") || ((OrderDetailResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("soft")) {
                                    if (PrefUtil.getUpgradeType().isEmpty()) {
                                        PrefUtil.setUpgradeDisplayCount(((OrderDetailResponse) response).data.fource_update.counter);
                                    }
                                    updateForceUpgradeData(ctx, ((OrderDetailResponse) response).data.fource_update.clear_data, ((OrderDetailResponse) response).data.fource_update.upgrade_type, ((OrderDetailResponse) response).data.fource_update.message, ((OrderDetailResponse) response).data.fource_update.apk_link);

                                    callback.success(response);
                                } else {
                                    PrefUtil.setUpgradeDisplayCount(0);
                                    PrefUtil.setUpgradeType("");
                                    callback.success(response);
                                }
                            } else if (response instanceof RestaurantProfileResponse && ((RestaurantProfileResponse) response).data != null && ((RestaurantProfileResponse) response).data.fource_update != null && !StringUtils.isNullOrEmpty(((RestaurantProfileResponse) response).data.fource_update.upgrade_type)) {
                                if (((RestaurantProfileResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("hard") || ((RestaurantProfileResponse) response).data.fource_update.upgrade_type.equalsIgnoreCase("soft")) {
                                    if (PrefUtil.getUpgradeType().isEmpty()) {
                                        PrefUtil.setUpgradeDisplayCount(((RestaurantProfileResponse) response).data.fource_update.counter);
                                    }
                                    updateForceUpgradeData(ctx, ((RestaurantProfileResponse) response).data.fource_update.clear_data, ((RestaurantProfileResponse) response).data.fource_update.upgrade_type, ((RestaurantProfileResponse) response).data.fource_update.message, ((RestaurantProfileResponse) response).data.fource_update.apk_link);
                                    callback.success(response);
                                } else {
                                    PrefUtil.setUpgradeDisplayCount(0);
                                    PrefUtil.setUpgradeType("");
                                    callback.success(response);
                                }
                            } else
                                callback.success(response);
                        } else {
                            callback.success(response);
                        }

                    } else {
                        RequestController.getNewAccessToken(callback, request);
                    }

                }
            }
        };
    }

    public static void updateForceUpgradeData(Context ctx, boolean cleardata, String upgradetype, String message, String apklink) {
        if (!MyApplication.isDialogShown) {
            PrefUtil.setUpgradeClearData(cleardata);
            PrefUtil.setUpgradeType(upgrade_type);
            PrefUtil.setUpgradeMessage(message);
            apk_link = apklink;
            upgrade_type = upgradetype;
            if (upgrade_type.equalsIgnoreCase("hard")) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(ctx.getResources().getString(R.string.app_name)+" Update Available.").setPositiveButton("Update", dialogClickListener).setCancelable(false).show();
            } else if (upgrade_type.equalsIgnoreCase("soft") && PrefUtil.getUpgradeDisplayCount() == 3) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
                builder.setMessage(ctx.getResources().getString(R.string.app_name)+" Update Available.").setPositiveButton("Update", dialogClickListener)
                        .setNegativeButton("Cancel", dialogClickListener).setCancelable(false).show();
            }
            MyApplication.isDialogShown = true;
        }

    }

    public static DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE: {
                    dialog.dismiss();
                    Intent webIntent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("https://play.google.com/store/apps/details?id=" + context.getPackageName()));
                    context.startActivity(webIntent);
//                    DownloadManager dm = (DownloadManager) context.getSystemService(context.DOWNLOAD_SERVICE);
//                    DownloadManager.Request request = new DownloadManager.Request(
//                            Uri.parse(apk_link));
//                    long enqueue = dm.enqueue(request);
                    if (upgrade_type.equalsIgnoreCase("hard")) {
                        PrefUtil.setUpgradeDisplayCount(0);
                        PrefUtil.setUpgradeType("");
                    }
                    MyApplication.isDialogShown = false;
                }
                break;

                case DialogInterface.BUTTON_NEGATIVE:
                    MyApplication.isDialogShown = false;
                    dialog.dismiss();
                    //No button clicked
                    break;
            }

        }
    };

    private static Response.Listener getListener(final RequestCallback callback, final BaseRequest request) {
        return new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (response == null)
                    callback.error(new NetworkError(new VolleyError("Something went wrong")));
                else if (response instanceof BaseResponse) {
                    if (((BaseResponse) response).is_token_valid) {
                        callback.success(response);
                    } else {
                        RequestController.getNewAccessToken(callback, request);
                    }
                }

            }
        };
    }

    public static void getNewAccessToken(final RequestCallback lastCallback, final BaseRequest request) {
        NewTokenRequest newTokenRequest = new NewTokenRequest();
        GsonRequest authRequest = newTokenRequest.createServerRequest(getErrorListener(lastCallback),
                new Response.Listener() {
                    @Override
                    public void onResponse(Object response) {
                        if (response instanceof TokenResponse) {
                            TokenResponse authResponse = (TokenResponse) response;
                            if (authResponse.is_token_valid) {
                                PrefUtil.putToken(authResponse.data.token);
                                getmRequestQueue().add(request.createServerRequest(getErrorListener(lastCallback), getListener(lastCallback, request)));
                            }
                        }
                    }
                });
        getmRequestQueue().add(authRequest);
    }

    public static synchronized RequestQueue getmRequestQueue() {
        if (mRequestQueue != null) return mRequestQueue;
        Cache cache = new DiskBasedCache(MyApplication.mContext.getCacheDir(), 1024 * 1024); // 1/8MB cap
        Network network = new BasicNetwork(new HurlStack());
        mRequestQueue = new RequestQueue(cache, network);
        mRequestQueue.start();
        return mRequestQueue;
    }

    public static void createNewAccessToken(RequestCallback callBack) {
        NewTokenRequest request = new NewTokenRequest();
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void login(String username, String password, RequestCallback callBack) {
        LoginRequest request = new LoginRequest(username, password);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void logout(String username, String password, RequestCallback callBack) {
        LogoutRequest request = new LogoutRequest(username, password);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void getArchiveOrder(Context ctx, RequestCallback callBack, int page) {
        GetArchiveOrderRequest request = new GetArchiveOrderRequest(getVersion(ctx), page);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(ctx, callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void getActiveOrder(Context ctx, RequestCallback callBack) {

        GetActiveOrderRequest request = new GetActiveOrderRequest(getVersion(ctx));
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(ctx, callBack, request));
        getmRequestQueue().add(gsonRequest);
    }


    public static void getOrderDetail(Context ctx, String orderId, RequestCallback callBack) {
        GetOrderDetailRequest request = new GetOrderDetailRequest(getVersion(ctx), orderId);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(ctx, callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static void orderProcess(String orderId, String status, String reason, String deliverytime, RequestCallback callBack) {
        OrderProcessRequest request = new OrderProcessRequest(orderId, status, reason, deliverytime);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }


    public static void getRestaurantProfileDetail(Context ctx, RequestCallback callBack) {
        GetRestaurantProfileRequest request = new GetRestaurantProfileRequest(getVersion(ctx));
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(ctx, callBack, request));
        getmRequestQueue().add(gsonRequest);
    }


    public static void getUpdateApp(String version, RequestCallback callBack) {
        UpdateAppRequest request = new UpdateAppRequest(version);
        GsonRequest gsonRequest = request.createServerRequest(getErrorListener(callBack), getListener(callBack, request));
        getmRequestQueue().add(gsonRequest);
    }

    public static String getVersion(Context ctx) {
        int versionCode = 1;
        try {
            PackageInfo pInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            versionCode = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return versionCode + "";
    }

}
