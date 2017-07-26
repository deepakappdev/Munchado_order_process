package com.munchado.orderprocess.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.NestedScrollView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.reservation.ReservationDetailResponseData;
import com.munchado.orderprocess.model.reservation.ReservationDetailsResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.StringUtils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationDetailFragment extends BaseFragment implements View.OnClickListener,RequestCallback {

    String reservation_Id;

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
    private ReservationDetailsResponse response;
    private ImageView imageView;
    private View progressBar;
    private TextView textAction;
    private TextView textCancel;
    private TextView textPrint;
    private TextView textChange_Time;
    private LinearLayout layout_instrctions;
    LinearLayout layout_profile, empty_layout;
    ImageButton layout_close;
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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reservation_Id = getArguments().getString("RESERVATION_ID");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_detail, container, false);

    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        df.setMinimumFractionDigits(2);
        df.setMaximumFractionDigits(2);
        fetchReservationDetail();
    }
    private void fetchReservationDetail() {
        showProgressBar();
        Bundle bundle = getArguments();
        reservation_Id = bundle.getString("RESERVATION_ID");
        Constants.totalPage = 1;
        RequestController.getReservationDetail(reservation_Id, this);
    }

    private void initView(View view) {
        progressBar = view.findViewById(R.id.progress_bar);
        layout_close = (ImageButton) view.findViewById(R.id.iv_close);

        layout_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((BaseActivity) getActivity()).backPressed();
            }
        });
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.RESERVATION_DETAIL;
    }

    @Override
    public void error(NetworkError volleyError) {
        hideProgressBar();
    }

    @Override
    public void success(Object obj) {
        if (getActivity() == null) return;
        hideProgressBar();
        if (obj instanceof ReservationDetailsResponse) {
            response = (ReservationDetailsResponse) obj;
          //  printData = ReceiptFormatUtils.setPrintData(response.data);
//            LogUtils.e(printData);
            showDetail(response.data);
        }


    }

    private void showDetail(ReservationDetailResponseData data) {
        layout_profile = (LinearLayout) rootView.findViewById(R.id.layout_customer_detail_1);
        empty_layout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int w = (int) (width * .4);
        layout_profile.setLayoutParams(new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootView.findViewById(R.id.layout_customer_detail_1).setVisibility(View.VISIBLE);
        StringBuilder nameBuilder = new StringBuilder(data.getFirst_name());
        if (!StringUtils.isNullOrEmpty(data.getFirst_name()))
            nameBuilder.append(" ").append(data.getFirst_name());
        textName.setText(nameBuilder.toString());
        textTelephone.setText(data.getPhone());
        textEmail.setText(data.getEmail());
        System.out.println("PHONENUMBER  "+data.getPhone());


    }
        @Override
    public void onClick(View view) {

    }
}
