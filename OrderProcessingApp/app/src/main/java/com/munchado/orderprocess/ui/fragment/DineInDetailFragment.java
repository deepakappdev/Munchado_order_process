package com.munchado.orderprocess.ui.fragment;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.model.dinein.DineinConfirmResponse;
import com.munchado.orderprocess.model.dinein.DineinDetailResponse;
import com.munchado.orderprocess.model.dinein.DineinDetailResponseData;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.widgets.CustomTextView;
import com.munchado.orderprocess.utils.Constants;
import com.munchado.orderprocess.utils.DateTimeUtils;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;
import com.squareup.picasso.Picasso;

/**
 * A simple {@link Fragment} subclass.
 */
public class DineInDetailFragment extends BaseFragment implements View.OnClickListener, RequestCallback {

    com.munchado.orderprocess.ui.widgets.SquareImageView iv_Profile;
    private View rootView;
    private Button btn_confirm_time, btn_alternate_time, btn_reject_time;
    private ExpandableRelativeLayout layout_confirm_time, layout_alternate_time, layout_reject_time;

    private Spinner spinner_confirm, spinner_alternate, spinner_rejct;
    private EditText edittext_confirm, edittext_alternate, edittext_reject;
    private Button btn_confirm, btn_alternate, btn_reject;
    private CustomTextView text_minus, text_change_delivery_time, text_plus;
    private CustomTextView text_name, text_email, text_telephone, text_past_activity;
    private CustomTextView text_booking_id, text_people, text_time, text_hold_time, text_instructions_id;

    private LinearLayout ll_confirm_reject, ll_booking, layout_customer_detail;
    private View progressBar;
    String reservationid, user_id;
    String status, holdTime;
    int holdtimemin = 10;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_dine_in_detail, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        iv_Profile = (com.munchado.orderprocess.ui.widgets.SquareImageView) view.findViewById(R.id.image_view);
        btn_confirm_time = (Button) view.findViewById(R.id.btn_confirm_time);
        btn_alternate_time = (Button) view.findViewById(R.id.btn_alternate_time);
        btn_reject_time = (Button) view.findViewById(R.id.btn_reject_time);
        btn_confirm = (Button) view.findViewById(R.id.btn_confirm);
        btn_alternate = (Button) view.findViewById(R.id.btn_alternate);
        btn_reject = (Button) view.findViewById(R.id.btn_reject);

        layout_confirm_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_confirm_time);
        layout_alternate_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_alternate_time);
        layout_reject_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_reject_time);

        text_minus = (CustomTextView) view.findViewById(R.id.text_minus);
        text_change_delivery_time = (CustomTextView) view.findViewById(R.id.text_change_delivery_time);
        text_plus = (CustomTextView) view.findViewById(R.id.text_plus);
        progressBar = view.findViewById(R.id.progress_bar);
        text_name = (CustomTextView) view.findViewById(R.id.text_name);
        text_email = (CustomTextView) view.findViewById(R.id.text_email);
        text_telephone = (CustomTextView) view.findViewById(R.id.text_telephone);
        text_past_activity = (CustomTextView) view.findViewById(R.id.text_past_activity);

        text_booking_id = (CustomTextView) view.findViewById(R.id.text_booking_id);
        text_people = (CustomTextView) view.findViewById(R.id.text_people);
        text_time = (CustomTextView) view.findViewById(R.id.text_time);
        text_hold_time = (CustomTextView) view.findViewById(R.id.text_hold_time);
        text_instructions_id = (CustomTextView) view.findViewById(R.id.text_instructions_id);

        edittext_confirm = (EditText) view.findViewById(R.id.edittext_confirm);
        edittext_alternate = (EditText) view.findViewById(R.id.edittext_alternate);
        edittext_reject = (EditText) view.findViewById(R.id.edittext_reject);
        ll_confirm_reject = (LinearLayout) view.findViewById(R.id.ll_confirm_reject);
        layout_customer_detail = (LinearLayout) view.findViewById(R.id.layout_customer_detail);
        ll_booking = (LinearLayout) view.findViewById(R.id.ll_booking);

        reservationid = getArguments().getString("BOOKING_ID");

        view.findViewById(R.id.iv_close).setOnClickListener(this);
        btn_confirm_time.setOnClickListener(this);
        btn_alternate_time.setOnClickListener(this);
        btn_reject_time.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        btn_alternate.setOnClickListener(this);
        btn_reject.setOnClickListener(this);
        text_minus.setOnClickListener(this);
        text_plus.setOnClickListener(this);
        text_email.setOnClickListener(this);
        text_telephone.setOnClickListener(this);
        text_change_delivery_time.setText(holdtimemin + " Min");
        fetchDetail();
    }

    private void fetchDetail() {
        showProgressBar();
        RequestController.getBookingDetail(reservationid, this);
    }

    private void updateBookintStatus(String instructions) {
        showProgressBar();
        RequestController.updateBookingDetail(user_id, reservationid, status, "", instructions, holdTime, this);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.iv_close:
                ((BaseActivity) getActivity()).backPressed();
                break;
            case R.id.btn_confirm_time:
                layout_confirm_time.toggle();

                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  if (layout_confirm_time.isExpanded()) {
                                                      btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                                                      btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                      btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                  } else
                                                      btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                              }
                                          },
                        350);

                if (layout_alternate_time.isExpanded())
                    layout_alternate_time.collapse();
                if (layout_reject_time.isExpanded())
                    layout_reject_time.collapse();
                break;
            case R.id.btn_alternate_time:
                layout_alternate_time.toggle();

                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  if (layout_alternate_time.isExpanded()) {
                                                      btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                                                      btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                      btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                  } else
                                                      btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                              }
                                          },
                        350);

                if (layout_confirm_time.isExpanded())
                    layout_confirm_time.collapse();
                if (layout_reject_time.isExpanded())
                    layout_reject_time.collapse();
                break;
            case R.id.btn_reject_time:
                layout_reject_time.toggle();

                new Handler().postDelayed(new Runnable() {
                                              @Override
                                              public void run() {
                                                  if (layout_reject_time.isExpanded()) {
                                                      btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                                                      btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                      btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                                  } else
                                                      btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                                              }
                                          },
                        350);
                if (layout_confirm_time.isExpanded())
                    layout_confirm_time.collapse();
                if (layout_alternate_time.isExpanded())
                    layout_alternate_time.collapse();
                break;
            case R.id.btn_confirm:

                if (!StringUtils.isNullOrEmpty(edittext_confirm.getText().toString())) {
                    String instructions = edittext_confirm.getText().toString();
                    status = "1";
                    holdTime = "0";
                    updateBookintStatus(instructions);
                } else
                    Toast.makeText(getActivity(), "Please enter instructions.", Toast.LENGTH_SHORT).show();

                break;
            case R.id.btn_alternate:
                status = "3";
                holdTime = "" + holdtimemin;
                if (!StringUtils.isNullOrEmpty(edittext_alternate.getText().toString())) {
                    String instructions = edittext_alternate.getText().toString();
                    updateBookintStatus(instructions);
                } else
                    Toast.makeText(getActivity(), "Please enter reason to reject.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_reject:
                if (!StringUtils.isNullOrEmpty(edittext_reject.getText().toString())) {
                    String instructions = edittext_reject.getText().toString();
                    status = "2";
                    holdTime = "0";
                    updateBookintStatus(instructions);
                } else
                    Toast.makeText(getActivity(), "Please enter reason to reject.", Toast.LENGTH_SHORT).show();
                break;
            case R.id.text_minus:
                if (holdtimemin > 10)
                    holdtimemin -= 10;
                text_change_delivery_time.setText(holdtimemin + " Min");
                break;
            case R.id.text_plus:
                if (holdtimemin < 90)
                    holdtimemin += 10;
                text_change_delivery_time.setText(holdtimemin + " Min");
                break;
            case R.id.text_email:
                if (!StringUtils.isNullOrEmpty(text_email.getText().toString())) {
                    Intent i = new Intent(Intent.ACTION_SEND);
                    i.setType("message/rfc822");
                    i.putExtra(Intent.EXTRA_EMAIL, new String[]{text_email.getText().toString()});
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
                if (!StringUtils.isNullOrEmpty(text_telephone.getText().toString())) {
                    Intent callIntent = new Intent(Intent.ACTION_DIAL);
                    callIntent.setData(Uri.parse("tel:" + text_telephone.getText().toString()));//change the number
                    try {
                        startActivity(Intent.createChooser(callIntent, "Dial with..."));
                    } catch (android.content.ActivityNotFoundException ex) {
                        Toast.makeText(getActivity(), "There are no email clients installed.", Toast.LENGTH_SHORT).show();
                    }
                }
        }

    }

    @Override
    public void error(NetworkError volleyError) {
        hideProgressBar();
    }

    @Override
    public void success(Object obj) {
        hideProgressBar();
        if (obj instanceof DineinConfirmResponse) {
            ((BaseActivity) getActivity()).reservation_Id = reservationid;
            ((BaseActivity) getActivity()).reservation_status = ((DineinConfirmResponse) obj).data.status;

            if (((BaseActivity) getActivity()).mDineInListFragment != null)
                ((DineInListFragment) ((BaseActivity) getActivity()).mDineInListFragment).performActionFromDetail();
            ((BaseActivity) getActivity()).backPressed();
        } else if (obj instanceof DineinDetailResponse) {
            DineinDetailResponse mDineinDetailResponse = (DineinDetailResponse) obj;
//            if (mDineinDetailResponse instanceof DineinDetailResponse) {

            setData(mDineinDetailResponse.data);
//            }
        }

    }


    private void setData(DineinDetailResponseData data) {
        user_id = data.user_id;
        Picasso.with(getContext()).load(data.user_pic).resize(200 * (int) getActivity().getResources().getDisplayMetrics().density, 200 * (int) getActivity().getResources().getDisplayMetrics().density).centerCrop().placeholder(R.drawable.profile_img).into(iv_Profile);
        setText(text_name, data.first_name + (!StringUtils.isNullOrEmpty(data.last_name) ? " " + Utils.decodeHtml(data.last_name) : ""));
        setText(text_email, data.email);
        setText(text_telephone, data.phone);
        setText(text_booking_id, data.booking_id);
        setText(text_people, data.seats + " People");
        setText(text_time, DateTimeUtils.getFormattedDate(data.hold_table_time, DateTimeUtils.FORMAT_HH_MM_A));
        setText(text_hold_time, "(" + data.hold_time + " min)");
        setText(text_instructions_id, data.user_instruction);
        ll_booking.setVisibility(View.VISIBLE);
        layout_customer_detail.setVisibility(View.VISIBLE);
        if (data.status.equalsIgnoreCase(Constants.CONFIRM) || data.status.equalsIgnoreCase(Constants.REJECT) || data.status.equalsIgnoreCase(Constants.USER_CONFIRM) || data.status.equalsIgnoreCase(Constants.CANCEL) || data.status.equalsIgnoreCase(Constants.ARCHIVE))
            ll_confirm_reject.setVisibility(View.INVISIBLE);
        else
            ll_confirm_reject.setVisibility(View.VISIBLE);
        StringBuilder stringBuilder = new StringBuilder();
        if (!StringUtils.isNullOrEmpty(data.user_activity.totalorder) && !data.user_activity.totalorder.equalsIgnoreCase("0"))
            stringBuilder.append(data.user_activity.totalorder).append(" Orders").append("\n");
        else if (data.user_activity.totalorder.equalsIgnoreCase("1"))
            stringBuilder.append(data.user_activity.totalorder).append(" Order").append("\n");
        if (!StringUtils.isNullOrEmpty(data.user_activity.totalreservation) && !data.user_activity.totalreservation.equalsIgnoreCase("0"))
            stringBuilder.append(data.user_activity.totalreservation).append(" Reservations").append("\n");
        else if (data.user_activity.totalreservation.equalsIgnoreCase("1"))
            stringBuilder.append(data.user_activity.totalreservation).append(" Reservation").append("\n");
        if (!StringUtils.isNullOrEmpty(data.user_activity.totalcheckin) && !data.user_activity.totalcheckin.equalsIgnoreCase("0"))
            stringBuilder.append(data.user_activity.totalcheckin).append(" Checkins").append("\n");
        else if (data.user_activity.totalcheckin.equalsIgnoreCase("1"))
            stringBuilder.append(data.user_activity.totalcheckin).append(" Checkin").append("\n");
        if (!StringUtils.isNullOrEmpty(data.user_activity.totalreview) && !data.user_activity.totalreview.equalsIgnoreCase("0"))
            stringBuilder.append(data.user_activity.totalreview).append(" Reviews").append("");
        else if (data.user_activity.totalreview.equalsIgnoreCase("1"))
            stringBuilder.append(data.user_activity.totalreview).append(" Review").append("\n");

        text_past_activity.setText(stringBuilder.toString());
    }


    void setText(CustomTextView textView, String text) {

        textView.setText(!StringUtils.isNullOrEmpty(text) ? Utils.decodeHtml(text) : "");
    }

    void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
    }

    void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.DINE_IN_DETAIL;
    }

}
