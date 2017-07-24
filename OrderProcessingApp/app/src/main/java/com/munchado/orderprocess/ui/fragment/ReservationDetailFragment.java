package com.munchado.orderprocess.ui.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReservationDetailFragment extends BaseFragment {


    public ReservationDetailFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_reservation_detail, container, false);
    }

    @Override
    public FRAGMENTS getFragmentId() {
        return FRAGMENTS.RESERVATION_DETAIL;
    }
}
