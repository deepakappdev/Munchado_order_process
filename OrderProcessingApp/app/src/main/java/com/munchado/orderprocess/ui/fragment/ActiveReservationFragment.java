package com.munchado.orderprocess.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.listener.OnReservationClickListener;
import com.munchado.orderprocess.model.dinein.DineinConfirmResponse;
import com.munchado.orderprocess.model.reservation.UpcomingReservationModelResponse;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.ActiveReservationAdapter;
import com.munchado.orderprocess.utils.DialogUtil;
import com.munchado.orderprocess.utils.StringUtils;
import com.munchado.orderprocess.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class ActiveReservationFragment extends BaseFragment implements View.OnClickListener, RequestCallback, OnReservationClickListener {

    private View rootView;
    RecyclerView recyclerView;
    private TextView textUpcomingBookingCount;
    private LinearLayoutManager mLinearLayoutManager;
    private List<UpcomingReservationModelResponse.Incoming_reservations> reservationsList;
    private ActiveReservationAdapter mActiveReservationAdapter;
    private HomeActivity mHomeActivity;
    long lastApiHitTimeInMillies = 0L;
    long activefragmentappearTimeInMillies = 0L;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHomeActivity = (HomeActivity) context;
    }

    public ActiveReservationFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        activefragmentappearTimeInMillies = System.currentTimeMillis();
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_reservation_list, container, false);
        return rootView;
        // Inflate the layout for this fragment

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        fetchBookingList(true);
    }

    private void initView() {
        textUpcomingBookingCount = (TextView) rootView.findViewById(R.id.text_reservation_count);
        rootView.findViewById(R.id.text_archive_reservation).setOnClickListener(this);
        recyclerView = (RecyclerView) rootView.findViewById(R.id.rv_reservation);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reservationsList = new ArrayList<>();
        textUpcomingBookingCount.setText(reservationsList.size() + " NEW RESERVATIONS");
        mActiveReservationAdapter = new ActiveReservationAdapter(mHomeActivity, this);
        recyclerView.setAdapter(mActiveReservationAdapter);
    }


    private void fetchBookingList(boolean is_dialog_shown) {
        if ((System.currentTimeMillis() - lastApiHitTimeInMillies) / 1000 >= 30) {
            if (is_dialog_shown)
                RequestController.getUpcomingReservation(this);
        }
    }


    @Override
    public void error(NetworkError volleyError) {
        DialogUtil.hideProgressDialog();
        if (volleyError != null && !StringUtils.isNullOrEmpty(volleyError.getLocalizedMessage()))
            if (volleyError.getLocalizedMessage().equalsIgnoreCase("Invalid token") || volleyError.getLocalizedMessage().equalsIgnoreCase("Credential not found")) {
                Utils.showLogin(getActivity());
            }
    }

    @Override
    public void success(Object obj) {
        if (mHomeActivity == null) return;
        if (obj instanceof UpcomingReservationModelResponse) {
            try {
                reservationsList.clear();
                UpcomingReservationModelResponse mUpcomingReservationModelResponse = (UpcomingReservationModelResponse) obj;
                if (null != mUpcomingReservationModelResponse.data.incoming_reservations)
                    reservationsList.addAll(mUpcomingReservationModelResponse.data.incoming_reservations);
                if (null != mUpcomingReservationModelResponse.data.view_today_reservation)
                    reservationsList.addAll(mUpcomingReservationModelResponse.data.view_today_reservation);
                textUpcomingBookingCount.setText(reservationsList.size() + " NEW RESERVATIONS");
//
                if (reservationsList != null && reservationsList.size() > 0) {
                    mActiveReservationAdapter.updateResult(reservationsList);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchBookingList(false);
                }
            }, 30000);
            mHomeActivity.startPlaySoundForNewBookings();
        } else if (obj instanceof DineinConfirmResponse) {
            fetchBookingList(false);
        }
    }


    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.RESERVATION;
    }

    @Override
    public void onReservationClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations) {
        Bundle bundle = new Bundle();
        bundle.putString("RESERVATION_ID", incomingReservations.id);
        ((BaseActivity) getActivity()).addOverLayFragment(FRAGMENTS.RESERVATION_DETAIL, bundle);
    }

    @Override
    public void onReservationActionClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_reservation:
//                Toast.makeText(mHomeActivity, "hii", Toast.LENGTH_SHORT).show();
                // TRICK TO AVOID NO FRAGMENT APPEAR WHEN USER CONTINUOUS CLICK ON BUTTON VERY FAST SPEED
                if ((System.currentTimeMillis() - activefragmentappearTimeInMillies) / 1000 >= 1) {
                    activefragmentappearTimeInMillies = System.currentTimeMillis();
                    ((BaseActivity) getActivity()).addFragment(FRAGMENTS.RESERVATION_ARCHIVE, null);
                }
                break;
        }
    }
}
