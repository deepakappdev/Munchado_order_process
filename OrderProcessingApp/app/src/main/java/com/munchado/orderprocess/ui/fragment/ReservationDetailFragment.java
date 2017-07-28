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
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationDetailFragment extends BaseFragment implements View.OnClickListener,RequestCallback {

    String reservation_Id;

    private CustomTextView textName;
    private CustomTextView textEmail;
    private CustomTextView textTelephone;
    private CustomTextView textPastActivity;
    com.munchado.orderprocess.ui.widgets.SquareImageView iv_Profile;

    private LinearLayout orderLayout;
    private CustomTextView text_booking_id;
    private CustomTextView text_booking_time;
    private CustomTextView text_no_people;
    private CustomTextView text_time;
    private CustomTextView text_status;
    private CustomTextView text_instructions;

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
//    RelativeLayout layout_base;
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
        return rootView= inflater.inflate(R.layout.fragment_reservation_detail, container, false);

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
            showDetail(response.data);
        }


    }

    private void showDetail(ReservationDetailResponseData data) {
        layout_profile = (LinearLayout) rootView.findViewById(R.id.layout_customer_detail);
//        empty_layout = (LinearLayout) rootView.findViewById(R.id.empty_layout);
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        int w = (int) (width * .4);
        layout_profile.setLayoutParams(new RelativeLayout.LayoutParams(w, RelativeLayout.LayoutParams.MATCH_PARENT));
        rootView.findViewById(R.id.ll_booking).setVisibility(View.VISIBLE);
        textName = (CustomTextView) rootView.findViewById(R.id.text_name);
        textEmail = (CustomTextView) rootView.findViewById(R.id.text_email);
        textTelephone = (CustomTextView) rootView.findViewById(R.id.text_telephone);
        textPastActivity = (CustomTextView) rootView.findViewById(R.id.text_past_activity);
        iv_Profile = (com.munchado.orderprocess.ui.widgets.SquareImageView) rootView.findViewById(R.id.image_view);

        StringBuilder nameBuilder = new StringBuilder(data.getFirst_name());
        if (!StringUtils.isNullOrEmpty(data.getFirst_name()))
            nameBuilder.append(" ").append(data.getFirst_name());
        textName.setText(nameBuilder.toString());
        textTelephone.setText(data.getPhone());
        textEmail.setText(data.getEmail());
        layout_profile.setVisibility(View.VISIBLE);
//        System.out.println("PHONENUMBER  "+data.getPhone());
        StringBuilder pastActivity = new StringBuilder();
        if (data.getPastActivity() != null) {
            if (Integer.parseInt(data.getPastActivity().getTotalorder()) > 0)
                pastActivity.append(data.getPastActivity().getTotalorder()).append(" Orders");
            if (Integer.parseInt(data.getPastActivity().getTotalreservation() )> 0)
                pastActivity.append(", ").append(data.getPastActivity().getTotalreservation()).append(" Reservation");
            if (Integer.parseInt(data.getPastActivity().getTotalcheckin()) > 0)
                pastActivity.append(", ").append(data.getPastActivity().getTotalcheckin()).append(" Checkins");
            if (Integer.parseInt(data.getPastActivity().getTotalreview()) > 0)
                pastActivity.append(", ").append(data.getPastActivity().getTotalreview()).append(" Reviews");
        }
        if (pastActivity.toString().isEmpty())
            textPastActivity.setText("No Past Activity");
        else
            textPastActivity.setText(Utils.decodeHtml(pastActivity.toString()));
        showOrderDetail(data);
    }

    private void showOrderDetail(ReservationDetailResponseData orderDetailData) {
        rootView.findViewById(R.id.ll_booking).setVisibility(View.VISIBLE);
//        textOrderId.setText(orderDetailData.getReceipt_no());
//        text_receipt_no.setText(orderDetailData.payment_receipt);
//        textOrderType.setText(orderDetailData.order_type);

        text_booking_id = (CustomTextView) rootView.findViewById(R.id.text_booking_id);
        text_booking_time = (CustomTextView) rootView.findViewById(R.id.text_booking_time);
        text_no_people = (CustomTextView) rootView.findViewById(R.id.text_no_people);
        text_time = (CustomTextView) rootView.findViewById(R.id.text_time);
        text_status = (CustomTextView) rootView.findViewById(R.id.text_status);
        text_instructions = (CustomTextView) rootView.findViewById(R.id.text_instructions);

        text_booking_id.setText(orderDetailData.getId());
        text_booking_time.setText(orderDetailData.getReserved_on());
        text_no_people.setText(orderDetailData.getParty_size()+" People");
        text_time.setText(orderDetailData.getReservation_date_time());
        text_status.setText(orderDetailData.getStatus());
        text_instructions.setText(orderDetailData.getUser_instruction());
    }

    @Override
    public void onClick(View view) {

    }
}
