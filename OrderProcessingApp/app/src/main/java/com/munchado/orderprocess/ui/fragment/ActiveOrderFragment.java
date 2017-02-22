package com.munchado.orderprocess.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.munchado.orderprocess.R;

/**
 * Created by android on 22/2/17.
 */

public class ActiveOrderFragment extends BaseFragment {

    public ActiveOrderFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_active_order, container, false);
    }
}
