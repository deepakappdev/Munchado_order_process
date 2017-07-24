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

import java.util.ArrayList;
import java.util.List;

public class ReservationListFragment extends BaseFragment implements View.OnClickListener, RequestCallback, OnReservationClickListener {

    private View rootView;
    RecyclerView recyclerView;
    private TextView textUpcomingBookingCount;
    private LinearLayoutManager mLinearLayoutManager;
    private List<UpcomingReservationModelResponse.Incoming_reservations> reservationsList;
    private ActiveReservationAdapter mActiveReservationAdapter;
    private HomeActivity mHomeActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHomeActivity = (HomeActivity) context;
    }

    public ReservationListFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_list, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
        fetchBookingList();
    }

    private void initView(View view) {
        textUpcomingBookingCount = (TextView) view.findViewById(R.id.text_reservation_count);
        view.findViewById(R.id.text_archive_reservation).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_reservation);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        reservationsList = new ArrayList<>();
        textUpcomingBookingCount.setText(reservationsList.size() + " NEW RESERVATIONS");
        mActiveReservationAdapter = new ActiveReservationAdapter(mHomeActivity, this);
        recyclerView.setAdapter(mActiveReservationAdapter);
    }


    private void fetchBookingList() {
        RequestController.getUpcomingReservation(this);
    }


    @Override
    public void error(NetworkError volleyError) {

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
                    fetchBookingList();
                }
            }, 30000);
            mHomeActivity.startPlaySoundForNewBookings();
        } else if (obj instanceof DineinConfirmResponse) {
            fetchBookingList();
        }
    }

    @Override
    public void onClick(View v) {

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
}
