package com.munchado.orderprocess.ui.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hp.mss.hpprint.model.PrintMetricsData;
import com.hp.mss.hpprint.util.PrintUtil;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.orderdetail.AddonsList;
import com.munchado.orderprocess.model.orderdetail.MyItemList;
import com.munchado.orderprocess.model.orderdetail.OrderAmountCalculation;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponse;
import com.munchado.orderprocess.model.orderdetail.OrderDetailResponseData;
import com.munchado.orderprocess.model.orderprocess.OrderProcessResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.print.PrinterSetting;
import com.munchado.orderprocess.print.StarPrinterUtils;
import com.munchado.orderprocess.print.WifiPrinterUtils;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.print.SearchPrinterActivity;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.MyFont;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.ReceiptFormatUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;
import com.munchado.orderprocess.utils.WifiPrintReceiptFormatUtils;
import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by android on 23/2/17.
 */

public class OrderDetailFragment extends BaseFragment implements RequestCallback, View.OnClickListener, PrintUtil.PrintMetricsListener {

    private TextView textName;
    private TextView textEmail;
    private TextView textTelephone;
    private TextView textPastActivity;
    private TextView textSubtotal;
    private TextView textDealDiscount;
    private TextView textPromocodeDiscount, textPromocetitle;
    private TextView textDelivery, textDeliverytitle;
    private TextView textTax;
    private TextView textTip, textTiptitle;
    private TextView textTotal;
    private LinearLayout orderLayout;
    private TextView textOrderId;
    private TextView text_receipt_no;
    private TextView textOrderType;
    private TextView textOrderTime;
    private TextView labelOrderTime;
    private TextView tv_change_delivery_time;

    private TextView textDeliveryTime;
    private TextView labelDeliveryAddress;
    private TextView textDeliveryAddress;
    private OrderDetailResponse response;
    private ImageView imageView;
    private View progressBar;
    private TextView textAction;
    private TextView textCancel;
    private TextView textPrint;
    private TextView textChange_Time;
    private LinearLayout layout_instrctions;
    LinearLayout layout_profile, empty_layout, layout_close;
    ImageButton btn_close;
    RelativeLayout layout_base;
    private TextView textinstrctions;
    private NestedScrollView scrollView;

    private String printData = "";
    private String deliveryTakeyoutDateString = "";

    private View rootView;
    private TextView textChangeDeliveryTime;
    private TextView textPlus;
    private TextView textMinus;
    private String clickFrom = "";
    private String order_type = "", sent_status = "";
    private String PRINT = "print_click", CONFIRM = "confirm_click";
    DecimalFormat df = new DecimalFormat();
    Calendar calendar = Calendar.getInstance();
    Date date;
    int layoutheight = 0;
    Handler handler = new Handler();
    Runnable runnable;

    public static final int REQUEST_EXTERNAL_PERMISSION_CODE = 666;
    SimpleDateFormat format = new SimpleDateFormat(DateTimeUtils.FORMAT_YYYY_MM_DD_HHMMSS);

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        setHasOptionsMenu(false);
//    }
//
//    @Override
//    public void onPrepareOptionsMenu(Menu menu) {
////        super.onPrepareOptionsMenu(menu);
//        menu.findItem(R.id.action_menu_more).setVisible(false);
//    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        return rootView = inflater.inflate(R.layout.frag_order_detail, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        fetchOrderDetail();
    }

    private void fetchOrderDetail() {
        showProgressBar();
        Bundle bundle = getArguments();
        String orderId = bundle.getString("ORDER_ID");
        ((BaseActivity) getActivity()).order_ID = orderId;
        Constants.totalPage = 1;
        RequestController.getOrderDetail((BaseActivity) getActivity(), orderId, this);
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        textName = (TextView) view.findViewById(R.id.text_name);
        textEmail = (TextView) view.findViewById(R.id.text_email);
        imageView = (ImageView) view.findViewById(R.id.image_view);
        textTelephone = (TextView) view.findViewById(R.id.text_telephone);
        textPastActivity = (TextView) view.findViewById(R.id.text_past_activity);
        btn_close = (ImageButton) view.findViewById(R.id.iv_close);

        textOrderId = (TextView) view.findViewById(R.id.text_order_id);
        text_receipt_no = (TextView) view.findViewById(R.id.text_receipt_no);
        textOrderType = (TextView) view.findViewById(R.id.text_order_type);
        textOrderTime = (TextView) view.findViewById(R.id.text_order_time);
        labelOrderTime = (TextView) view.findViewById(R.id.label_order_time);
        textDeliveryTime = (TextView) view.findViewById(R.id.text_delivery_time);
        textDeliverytitle = (TextView) view.findViewById(R.id.text_delivery_title);
        labelDeliveryAddress = (TextView) view.findViewById(R.id.label_delivery_address);
        textDeliveryAddress = (TextView) view.findViewById(R.id.text_delivery_address);
        tv_change_delivery_time = (TextView) view.findViewById(R.id.tv_change_delivery_time);

        orderLayout = (LinearLayout) view.findViewById(R.id.order_layout);
        layout_instrctions = (LinearLayout) view.findViewById(R.id.special_instruction_layout);

        textSubtotal = (TextView) view.findViewById(R.id.text_subtotal);
        textPromocodeDiscount = (TextView) view.findViewById(R.id.text_promocode_discount);
        textPromocetitle = (TextView) view.findViewById(R.id.text_promocode_title);
        textinstrctions = (TextView) view.findViewById(R.id.speical_instruction);
        textDealDiscount = (TextView) view.findViewById(R.id.text_deal_discount);
        textDelivery = (TextView) view.findViewById(R.id.text_delivery);
        textTax = (TextView) view.findViewById(R.id.text_tax);
        textTip = (TextView) view.findViewById(R.id.text_tip);
        textTiptitle = (TextView) view.findViewById(R.id.text_tip_title);
        textTotal = (TextView) view.findViewById(R.id.text_total);

        textPlus = (TextView) view.findViewById(R.id.text_plus);
        textChangeDeliveryTime = (TextView) view.findViewById(R.id.text_change_delivery_time);
        textMinus = (TextView) view.findViewById(R.id.text_minus);

        layout_close = (LinearLayout) view.findViewById(R.id.rl_close);
        layout_base = (RelativeLayout) view.findViewById(R.id.home_layout);
        scrollView = (NestedScrollView) view.findViewById(R.id.scrollView);
        textPlus.setOnClickListener(this);
        textMinus.setOnClickListener(this);
        btn_close.setOnClickListener(this);

        layout_close.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    ((BaseActivity) getActivity()).backPressed();
                    return true;
                }
                return true;
            }
        });

        (textPrint = (TextView) view.findViewById(R.id.text_print)).setOnClickListener(this);
        (textChange_Time = (TextView) view.findViewById(R.id.text_change)).setOnClickListener(this);

        textEmail.setPaintFlags(textEmail.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textTelephone.setPaintFlags(textTelephone.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
        textChange_Time.setPaintFlags(textChange_Time.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        (textCancel = (TextView) view.findViewById(R.id.text_cancel)).setOnClickListener(this);
        textCancel.setPaintFlags(textCancel.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);

        (textAction = (TextView) view.findViewById(R.id.btn_action)).setOnClickListener(this);

        view.findViewById(R.id.btn_update).setOnClickListener(this);
        textEmail.setOnClickListener(this);
        textTelephone.setOnClickListener(this);
    }

    private void showOrderDetail(OrderDetailResponseData orderDetailData) {
        rootView.findViewById(R.id.layout_order_detail).setVisibility(View.VISIBLE);
        textOrderId.setText(orderDetailData.id);
        text_receipt_no.setText(orderDetailData.payment_receipt);
        textOrderType.setText(orderDetailData.order_type);

        textOrderTime.setText(DateTimeUtils.getFormattedDate(orderDetailData.order_date, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(orderDetailData.order_date, DateTimeUtils.FORMAT_HH_MM_A));
        if (orderDetailData.order_type.equalsIgnoreCase("takeout")) {
            order_type = "takeout";
            labelOrderTime.setText("Time of Takeout: ");
            tv_change_delivery_time.setText("Change Takeout Time");
            labelDeliveryAddress.setVisibility(View.GONE);
            textDeliveryAddress.setVisibility(View.GONE);
            textTiptitle.setVisibility(View.GONE);
            textTip.setVisibility(View.GONE);
            textDelivery.setVisibility(View.GONE);
            textDeliverytitle.setVisibility(View.GONE);
        } else {
            order_type = "delivery";
            labelOrderTime.setText("Time of Delivery: ");
            labelDeliveryAddress.setVisibility(View.VISIBLE);
            textDeliveryAddress.setVisibility(View.VISIBLE);
            textDeliveryAddress.setText(Utils.decodeHtml(orderDetailData.my_delivery_detail.address));
        }
        textDeliveryTime.setText(DateTimeUtils.getFormattedDate(orderDetailData.delivery_date, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(orderDetailData.delivery_date, DateTimeUtils.FORMAT_HH_MM_A));
        textChangeDeliveryTime.setText(DateTimeUtils.getFormattedDate(orderDetailData.delivery_date, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(orderDetailData.delivery_date, DateTimeUtils.FORMAT_HH_MM_A));
        deliveryTakeyoutDateString = orderDetailData.delivery_date;
    }

    @Override
    public void error(NetworkError volleyError) {
        hideProgressBar();
        if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage()))
            if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found")) {
                Utils.showLogin(getActivity());
//                showToast(volleyError.getLocalizedMessage());
            }

    }

    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.ORDER_DETAIL;
    }

    @Override
    public void success(Object obj) {
        if (getActivity() == null) return;
        hideProgressBar();
        if (obj instanceof OrderDetailResponse) {
            response = (OrderDetailResponse) obj;
            printData = ReceiptFormatUtils.setPrintData(response.data);
            LogUtils.e(printData);
            showDetail(response.data);
        } else if (obj instanceof OrderProcessResponse) {
            if (((OrderProcessResponse) obj).data.message) {
                response.data.status = ((OrderProcessResponse) obj).data.status;
                ((BaseActivity) getActivity()).order_Status = response.data.status;
                updateActionButton();

                if (clickFrom.equalsIgnoreCase(PRINT)) {

                    if (response.data.status.equalsIgnoreCase("confirmed") || response.data.status.equalsIgnoreCase("arrived") || response.data.status.equalsIgnoreCase("delivered")) {

                        sendToPrinter();
                    }
                }


            }
        }
        if (!StringUtils.isNullOrEmpty(deliveryTakeyoutDateString))
            textDeliveryTime.setText(DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_HH_MM_A));
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    private void showDetail(OrderDetailResponseData data) {
        layout_profile = (LinearLayout) rootView.findViewById(R.id.layout_customer_detail_1);
        empty_layout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int w = (int) (width * .4);
        layout_profile.setLayoutParams(new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootView.findViewById(R.id.layout_customer_detail_1).setVisibility(View.VISIBLE);
        StringBuilder nameBuilder = new StringBuilder(data.customer_first_name);
        if (!StringUtils.isNullOrEmpty(data.customer_last_name))
            nameBuilder.append(" ").append(data.customer_last_name);
        textName.setText(nameBuilder.toString());
        textTelephone.setText(data.my_delivery_detail.phone);
        textEmail.setText(data.email);

        StringBuilder pastActivity = new StringBuilder();
        if (data.user_activity != null) {
            if (data.user_activity.total_user_order > 0)
                pastActivity.append(data.user_activity.total_user_order).append(" Orders");
            if (data.user_activity.total_user_reservation > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_reservation).append(" Reservation");
            if (data.user_activity.total_user_checkin > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_checkin).append(" Checkins");
            if (data.user_activity.total_user_review > 0)
                pastActivity.append(", ").append(data.user_activity.total_user_review).append(" Reviews");
        }
        if (pastActivity.toString().isEmpty())
            textPastActivity.setText("No Past Activity");
        else
            textPastActivity.setText(Utils.decodeHtml(pastActivity.toString()));

        showOrderDetail(data);
        showOrderPaymentDetail(data, data.order_amount_calculation);


        updateActionButton();


        if (!StringUtils.isNullOrEmpty(data.special_instruction)) {
            layout_instrctions.setVisibility(View.VISIBLE);
            if (data.special_instruction.contains("||"))
                data.special_instruction = data.special_instruction.replaceAll("\\|\\|", "\n");

            textinstrctions.setText(Utils.decodeHtml(data.special_instruction));
        } else
            layout_instrctions.setVisibility(View.GONE);
    }

    private void showOrderItem(ArrayList<MyItemList> item_list) {
        orderLayout.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(orderLayout.getContext());
        int view_count = 0;
        for (MyItemList itemList : item_list) {
            View view = inflater.inflate(R.layout.row_order_item, null);
            TextView itemName = (TextView) view.findViewById(R.id.text_item_name);
            TextView textInstruction = (TextView) view.findViewById(R.id.text_instruction);
            TextView itemCount = (TextView) view.findViewById(R.id.text_item_count);
            TextView itemPrice = (TextView) view.findViewById(R.id.text_item_price);

            if (!StringUtils.isNullOrEmpty(itemList.item_price_desc))
                itemName.setText(Utils.decodeHtml(itemList.item_name) + " (" + Utils.decodeHtml(itemList.item_price_desc) + ")");
            else
                itemName.setText(Utils.decodeHtml(itemList.item_name));
            if (StringUtils.isNullOrEmpty(itemList.item_special_instruction)) {
                textInstruction.setVisibility(View.GONE);
            } else {
                textInstruction.setText(Utils.decodeHtml(itemList.item_special_instruction));
            }
            itemCount.setText(itemList.item_qty);
            itemPrice.setText("$" + df.format(Utils.parseDouble(itemList.unit_price) * Utils.parseDouble(itemList.item_qty)));
            LinearLayout layoutAddons = (LinearLayout) view.findViewById(R.id.layout_add_ons);
            if (StringUtils.isNullOrEmpty(itemList.addons_list))
                layoutAddons.setVisibility(View.GONE);
            else {
                layoutAddons.setVisibility(View.VISIBLE);

                for (AddonsList addonsItem : itemList.addons_list) {
                    View addonView = inflater.inflate(R.layout.row_addon_item, null);
                    itemName = (TextView) addonView.findViewById(R.id.text_item_name);
                    itemCount = (TextView) addonView.findViewById(R.id.text_item_count);
                    itemPrice = (TextView) addonView.findViewById(R.id.text_item_price);

                    itemName.setText("+ " + Utils.decodeHtml(addonsItem.addon_name));
                    itemCount.setText(addonsItem.addon_quantity);
                    itemPrice.setText("$" + df.format(Double.valueOf(addonsItem.addons_total_price)));
                    view_count++;
                    layoutAddons.addView(addonView);
                }
            }
            view_count++;
            orderLayout.addView(view);
        }


        if (layout_instrctions.getVisibility() == View.VISIBLE) {

            ViewTreeObserver viewTreeObserver = layout_instrctions.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout_instrctions.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        layoutheight += layout_instrctions.getHeight();
                    }
                });
            }

            ViewTreeObserver viewTreeObserver_order = orderLayout.getViewTreeObserver();
            if (viewTreeObserver_order.isAlive()) {
                viewTreeObserver_order.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        layout_instrctions.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        layoutheight += orderLayout.getHeight();
                        empty_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, layoutheight));
                        layout_close.setVisibility(View.VISIBLE);
                        layout_base.setVisibility(View.VISIBLE);
                    }
                });
            }
        } else {
            ViewTreeObserver viewTreeObserver = orderLayout.getViewTreeObserver();
            if (viewTreeObserver.isAlive()) {
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        orderLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        empty_layout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, orderLayout.getHeight()));
                        layout_close.setVisibility(View.VISIBLE);
                        layout_base.setVisibility(View.VISIBLE);
                    }
                });
            }
        }


    }

    void updateActionButton() {
        textPrint.setVisibility(View.VISIBLE);
        textAction.setVisibility(View.VISIBLE);
        textCancel.setVisibility(View.VISIBLE);

        textChange_Time.setVisibility(View.GONE);
        String currentStatus = response.data.status;
        if (currentStatus.equalsIgnoreCase("placed")) {
            textAction.setText("CONFIRM");
            textAction.setBackgroundResource(R.drawable.green_button);
            textChange_Time.setVisibility(View.VISIBLE);
        } else if (currentStatus.equalsIgnoreCase("confirmed")) {

            if (response.data.order_type.equalsIgnoreCase("takeout")) {
                textAction.setText("READY");
                textAction.setBackgroundResource(R.drawable.green_button);
            } else {
                textAction.setText("ARCHIVE");
                textAction.setBackgroundResource(R.drawable.grey_button);
            }
        } else if (currentStatus.equalsIgnoreCase("Arrived") || currentStatus.equalsIgnoreCase("delivered")) {
            textAction.setBackgroundResource(R.drawable.grey_button);
            textAction.setText("ARCHIVE");
        } else {
            if (response.data.order_type.equalsIgnoreCase("takeout") && sent_status.equalsIgnoreCase("arrived") && currentStatus.equalsIgnoreCase("archived")) {
                textAction.setBackgroundResource(R.drawable.grey_button);
                textAction.setText("ARCHIVE");
            } else {
                textAction.setVisibility(View.GONE);
                textCancel.setVisibility(View.GONE);
            }
        }
    }

    private void showOrderPaymentDetail(final OrderDetailResponseData data, OrderAmountCalculation payment_detail) {
        rootView.findViewById(R.id.layout_order_payment).setVisibility(View.VISIBLE);
        textSubtotal.setText("$" + df.format(Double.valueOf(payment_detail.subtotal)));
        if (StringUtils.isNullOrEmpty(payment_detail.promocode_discount) || payment_detail.promocode_discount.equalsIgnoreCase("0") || payment_detail.promocode_discount.equalsIgnoreCase("0.00")) {
            textPromocetitle.setVisibility(View.GONE);
            textPromocodeDiscount.setVisibility(View.GONE);
        } else
            textPromocodeDiscount.setText("$" + df.format(Double.valueOf(payment_detail.promocode_discount)));
        textDealDiscount.setText("$" + df.format(Double.valueOf(payment_detail.discount)));

        if (Utils.parseDouble(payment_detail.delivery_charge) == 0)
            textDelivery.setText("Free");
        else
            textDelivery.setText("$" + df.format(Double.valueOf(payment_detail.delivery_charge)));

        textTax.setText("$" + df.format(Double.valueOf(payment_detail.tax_amount)));
        textTip.setText("$" + df.format(Double.valueOf(payment_detail.tip_amount)));
        textTotal.setText("$" + df.format(Double.valueOf(payment_detail.total_order_price)));
        final LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.layout_order_payment);

        ViewTreeObserver viewTreeObserver = layout.getViewTreeObserver();
        if (viewTreeObserver.isAlive()) {
            viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    layoutheight += layout.getHeight();
                    showOrderItem(data.item_list);
                }
            });
        }

        final LinearLayout layout2 = (LinearLayout) rootView.findViewById(R.id.layout_customer_detail_1);

        ViewTreeObserver viewTreeObserver2 = layout2.getViewTreeObserver();
        if (viewTreeObserver2.isAlive()) {
            viewTreeObserver2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    layout2.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    int width = layout2.getWidth();
                    imageView.setLayoutParams(new LinearLayout.LayoutParams(width, width));
                    Picasso.with(getContext()).load(data.user_image).resize(200 * (int) getActivity().getResources().getDisplayMetrics().density, 200 * (int) getActivity().getResources().getDisplayMetrics().density).centerCrop().placeholder(R.drawable.profile_img).into(imageView);

                }
            });
        }
    }

    private void sendToPrinter() {

        LogUtils.d("========== set printer type : " + PrefUtil.getPrinterType());
        if (PrefUtil.getPrinterType().equalsIgnoreCase(Constants.BLUETOOTH)) {
            PrinterSetting setting = new PrinterSetting(getActivity());
            if (TextUtils.isEmpty(setting.getPortName()) || TextUtils.isEmpty(setting.getPortSettings())) {
                Bundle bundle = new Bundle();
                Intent intent = new Intent(getActivity(), SearchPrinterActivity.class);
//                intent.putExtra("printData", printData);
                bundle.putParcelable("data", response.data);
                bundle.putString("printData", printData);
                intent.putExtras(bundle);
                startActivity(intent);
            } else
                new StarPrinterUtils(getActivity(), "", printData);
        } else if (checkExternalStoragePermission(getActivity())) {

//            printData = new WifiPrintReceiptFormatUtils().getReciept();
//            printData = new WifiPrintReceiptFormatUtils().getReciept(response.data);
//            File file = createPDF(printData, "test.pdf", getActivity());
            LogUtils.d("========== write permission granted : " + PrefUtil.getPrinterType());
            File file = new WifiPrintReceiptFormatUtils().createPDF(response.data, "Order_" + response.data.payment_receipt + ".pdf", getActivity());
            if (file != null)
                new WifiPrinterUtils().startPrint(getActivity(), file, "Order of " + response.data.customer_first_name);
//                new WifiPrinterUtils().sendToPrint(getActivity(), file, "Order of " + response.data.customer_first_name);
            else
                new WifiPrinterUtils().startPrint(getActivity(), printData);

        }
    }

    public File createPDF(String rawHTML, String fileName, Activity context) {
        File file;
        final String APPLICATION_PACKAGE_NAME = context.getPackageName();
        File path = new File(Environment.getExternalStorageDirectory(), APPLICATION_PACKAGE_NAME);
        if (!path.exists()) {
            path.mkdir();
        }
        file = new File(path, fileName);

        try {

            Document document = new Document();
            PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Подготавливаем HTML
            String htmlText = Jsoup.clean(rawHTML, Whitelist.relaxed());
            InputStream inputStream = new ByteArrayInputStream(htmlText.getBytes());

            // Печатаем документ PDF
            XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                    inputStream, null, Charset.defaultCharset(), new MyFont());

            document.close();
            return file;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.text_print:
                String currentStatus = response.data.status;
                if (textAction.getVisibility() == View.VISIBLE && textAction.getText().toString().equalsIgnoreCase("CONFIRM") || currentStatus.equalsIgnoreCase("placed")) {
                    clickFrom = PRINT;
                    showProgressBar();
                    String status2 = "";// "archived";
                    if (currentStatus.equalsIgnoreCase("placed"))
                        status2 = "confirmed";
                    sent_status = status2;
                    RequestController.orderProcess(response.data.id, status2, "", "", OrderDetailFragment.this);
                } else {
                    sendToPrinter();
                }

                break;
            case R.id.btn_action:
                clickFrom = CONFIRM;
                String currentStatus2 = response.data.status;
                showProgressBar();
                String status = "archived";
                if (currentStatus2.equalsIgnoreCase("placed"))
                    status = "confirmed";
                else if (currentStatus2.equalsIgnoreCase("confirmed")) {
                    if (order_type.equalsIgnoreCase("takeout"))
                        status = "ready";
                    else
                        status = "delivered";
                } else if (currentStatus2.equalsIgnoreCase("arrived") || currentStatus2.equalsIgnoreCase("delivered"))
                    status = "archived";
                sent_status = status;
                RequestController.orderProcess(response.data.id, status, "", "", OrderDetailFragment.this);
                break;
            case R.id.text_cancel:
                if (progressBar.getVisibility() != View.VISIBLE)
                    askUserForReason();

                break;
            case R.id.text_plus:
                changeTime(30);
                break;
            case R.id.text_minus:
                changeTime(-30);
                break;
            case R.id.iv_close:
                ((BaseActivity) getActivity()).backPressed();
                break;
            case R.id.btn_update:
                showProgressBar();
                rootView.findViewById(R.id.layout_change_delivery_time).setVisibility(View.GONE);
                RequestController.orderProcess(response.data.id, "placed", "", deliveryTakeyoutDateString, OrderDetailFragment.this);
                break;
            case R.id.text_change:
                if (rootView.findViewById(R.id.layout_change_delivery_time).getVisibility() == View.VISIBLE)
                    rootView.findViewById(R.id.layout_change_delivery_time).setVisibility(View.GONE);
                else {
                    rootView.findViewById(R.id.layout_change_delivery_time).setVisibility(View.VISIBLE);
                    scrollView.fullScroll(View.FOCUS_DOWN);
                }
                break;

            case R.id.text_email:
                if (!StringUtils.isNullOrEmpty(textEmail.getText().toString())) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{textEmail.getText().toString()});
                    i.putExtra(Intent.EXTRA_SUBJECT, "");
                    i.putExtra(Intent.EXTRA_TEXT, "");
                    try {
                        startActivity(Intent.createChooser(i, "Send mail..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case R.id.text_telephone:
                if (!StringUtils.isNullOrEmpty(textTelephone.getText().toString())) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + textTelephone.getText().toString()));//change the number
                    try {
                        startActivity(Intent.createChooser(callIntent, "Dial with..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }

                break;

        }
    }


    private void changeTime(int minutes) {
        try {
            date = format.parse(deliveryTakeyoutDateString);
            calendar.setTime(date);
            calendar.add(Calendar.MINUTE, minutes);
            if (DateTimeUtils.isFutureDate(deliveryTakeyoutDateString)) {
                deliveryTakeyoutDateString = format.format(calendar.getTime());
                textChangeDeliveryTime.setText(DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_HH_MM_A));
            } else {
                if (minutes < 0) {
                    date = format.parse(deliveryTakeyoutDateString);
                    calendar.setTime(date);
                    calendar.add(Calendar.MINUTE, -1 * minutes);
                } else {
                    deliveryTakeyoutDateString = format.format(calendar.getTime());
                    textChangeDeliveryTime.setText(DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_MMM_DD_YYYY) + " @ " + DateTimeUtils.getFormattedDate(deliveryTakeyoutDateString, DateTimeUtils.FORMAT_HH_MM_A));
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    private void askUserForReason() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setMessage("Enter Reason");

        final EditText input = new EditText(getActivity());
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        input.setLayoutParams(lp);
        alertDialog.setView(input);

        alertDialog.setPositiveButton("Cancel Order",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });

        alertDialog.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        final AlertDialog dialog = alertDialog.create();
        dialog.show();
//Overriding the handler immediately after show is probably a better approach than OnShowListener as described below
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean wantToCloseDialog = false;

                String message = input.getText().toString().trim();
                if (!StringUtils.isNullOrEmpty(message)) {
                    showProgressBar();
                    wantToCloseDialog = true;
                    RequestController.orderProcess(response.data.id, "cancelled", input.getText().toString(), "", OrderDetailFragment.this);
                } else {
                    showToast("Please enter reason to cancel order.");
                }
                //Do stuff, possibly set wantToCloseDialog to true then...
                if (wantToCloseDialog)
                    dialog.dismiss();
                //else dialog stays open. Make sure you have an obvious way to close the dialog especially if you set cancellable to false.
            }
        });
    }

    @Override
    public void onPrintMetricsDataPosted(PrintMetricsData printMetricsData) {

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
                if (checkExternalStoragePermission(getActivity())) {
                    // Continue with your action after permission request succeed
//                    new WifiPrinterUtils().startPrint(getActivity(), printData);
                    File file = new WifiPrintReceiptFormatUtils().createPDF(response.data, "Order_" + response.data.payment_receipt + ".pdf", getActivity());
                    if (file != null)
                        new WifiPrinterUtils().startPrint(getActivity(), file, "Order of " + response.data.customer_first_name);
//                        new WifiPrinterUtils().sendToPrint(getActivity(), file, "Order of " + response.data.customer_first_name);
                    else
                        new WifiPrinterUtils().startPrint(getActivity(), printData);
                }
            }
        }
    }
}