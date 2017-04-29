package com.munchado.orderprocess.ui.fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.listener.OnDineinClickListener;
import com.munchado.orderprocess.model.dinein.DineinResponse;
import com.munchado.orderprocess.model.dinein.UpcomingReservation;
import com.munchado.orderprocess.network.RequestController;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.DineinAdapter;
import com.munchado.orderprocess.utils.LogUtils;
import com.munchado.orderprocess.utils.StringUtils;

/**
 * A simple {@link Fragment} subclass.
 */
public class DineInListFragment extends BaseFragment implements View.OnClickListener, RequestCallback {
    private View rootView;
    RecyclerView recyclerView;
    private TextView textActiveOrderCount;
    private LinearLayoutManager mLinearLayoutManager;
    private HomeActivity mHomeActivity;
    private DineinAdapter mDineinAdapter;

    public DineInListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mHomeActivity = (HomeActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        if (rootView == null)
            rootView = inflater.inflate(R.layout.fragment_dine_in_list, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
//        mHomeActivity.upcommingReservationList = new ArrayList<>();
        fetchBookingList();
    }

    private void initView(View view) {
        textActiveOrderCount = (TextView) view.findViewById(R.id.text_booking_count);
        view.findViewById(R.id.text_archive_booking).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mDineinAdapter = new DineinAdapter(mHomeActivity, mOnDineinClickListener);
        recyclerView.setAdapter(mDineinAdapter);
        if (mHomeActivity.upcommingReservationList != null) {
            textActiveOrderCount.setText(mHomeActivity.upcommingReservationList.size() + " NEW BOOKINGS");
            if (mHomeActivity.upcommingReservationList.size() > 0) {
                mDineinAdapter.setData(mHomeActivity.upcommingReservationList);
            }
        }
    }

    private void fetchBookingList() {
        RequestController.getBooking(this);
    }


    @Override
    public void onResume() {
        super.onResume();

        if (mDineinAdapter != null && mDineinAdapter.getAllItems() != null && mDineinAdapter.getAllItems().size() > 0) {
            if (!StringUtils.isNullOrEmpty(((BaseActivity) getActivity()).reservation_Id) && !StringUtils.isNullOrEmpty(((BaseActivity) getActivity()).reservation_status)) {
                for (int i = 0; i < mDineinAdapter.getAllItems().size(); i++) {
                    if (mDineinAdapter.getAllItems().get(i).reservation_id.equalsIgnoreCase(((BaseActivity) getActivity()).reservation_Id)) {

                        LogUtils.d("==== onResume . status : " + mDineinAdapter.getAllItems().get(i).status + "==== detail status : " + ((BaseActivity) getActivity()).reservation_status);
                        if (!mDineinAdapter.getAllItems().get(i).status.equalsIgnoreCase(((BaseActivity) getActivity()).reservation_status)) {
                            fetchBookingList();
                            break;
                        }
                    }
                }
            }

            ((BaseActivity) getActivity()).reservation_Id = "";
            ((BaseActivity) getActivity()).reservation_status = "";
        }
    }

    @Override
    public void error(NetworkError volleyError) {

    }

    @Override
    public void success(Object obj) {
        if (mHomeActivity == null) return;
        if (obj instanceof DineinResponse) {
            DineinResponse mDineinResponse = (DineinResponse) obj;
            mHomeActivity.upcommingReservationList = mDineinResponse.data.upcomming_reservation;
            mHomeActivity.archiveReservationList = mDineinResponse.data.archive_reservation;
            if (mHomeActivity.upcommingReservationList != null) {
                textActiveOrderCount.setText(mHomeActivity.upcommingReservationList.size() + " NEW BOOKINGS");
                if (mHomeActivity.upcommingReservationList.size() > 0) {
                    mDineinAdapter.setData(mHomeActivity.upcommingReservationList);
                }
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    fetchBookingList();
                }
            }, 30000);
        }
    }

    OnDineinClickListener mOnDineinClickListener = new OnDineinClickListener() {
        @Override
        public void onDineItemClick(UpcomingReservation reservation) {
            Bundle bundle = new Bundle();
            bundle.putString("BOOKING_ID", reservation.reservation_id);
            ((BaseActivity) getActivity()).addOverLayFragment(FRAGMENTS.DINE_IN_DETAIL, bundle);
        }
    };

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.DINE_IN;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_booking:
                ((BaseActivity) getActivity()).addFragment(FRAGMENTS.DINE_IN_ARCHIVE, null);
                break;
        }
    }

}