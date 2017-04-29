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
import com.munchado.orderprocess.listener.OnDineinArchiveClickListener;
import com.munchado.orderprocess.model.dinein.ArchiveReservation;
import com.munchado.orderprocess.network.volley.NetworkError;
import com.munchado.orderprocess.network.volley.RequestCallback;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.adapter.DineinArchiveAdapter;

/**
 * A simple {@link Fragment} subclass.
 */
public class DineinArchiveFragment extends BaseFragment implements View.OnClickListener, RequestCallback {

    private View rootView;
    RecyclerView recyclerView;
    private TextView textArchiveOrderCount;
    private LinearLayoutManager mLinearLayoutManager;
    private HomeActivity mHomeActivity;
    private DineinArchiveAdapter mDineinArchiveAdapter;

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
            rootView = inflater.inflate(R.layout.fragment_dinein_archive, container, false);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }


    private void initView(View view) {
        textArchiveOrderCount = (TextView) view.findViewById(R.id.text_booking_count);
        view.findViewById(R.id.text_archive_booking).setOnClickListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        mLinearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(mLinearLayoutManager);
        mDineinArchiveAdapter = new DineinArchiveAdapter(mHomeActivity, mOnDineinArchiveClickListener);
        recyclerView.setAdapter(mDineinArchiveAdapter);
        if (mHomeActivity.archiveReservationList != null) {
            textArchiveOrderCount.setText(mHomeActivity.archiveReservationList.size() + " ARCHIVE BOOKINGS");
            if (mHomeActivity.archiveReservationList.size() > 0) {
                mDineinArchiveAdapter.setData(mHomeActivity.archiveReservationList);
            }
        }
    }

    OnDineinArchiveClickListener mOnDineinArchiveClickListener = new OnDineinArchiveClickListener() {
        @Override
        public void onDineItemClick(ArchiveReservation reservation) {
            Bundle bundle = new Bundle();
            bundle.putString("BOOKING_ID", reservation.reservation_id);
            ((BaseActivity) getActivity()).addOverLayFragment(FRAGMENTS.DINE_IN_DETAIL, bundle);
        }
    };

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.DINE_IN_ARCHIVE;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.text_archive_booking:
                ((BaseActivity) getActivity()).addFragment(FRAGMENTS.DINE_IN, null);
                break;
        }
    }

    @Override
    public void error(NetworkError volleyError) {

    }

    @Override
    public void success(Object obj) {

    }

}
