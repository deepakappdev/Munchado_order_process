package com.munchado.orderprocess.ui.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.ui.activity.HomeActivity;
import com.munchado.orderprocess.ui.activity.settings.OrderProcessingSettingActivity;
import com.munchado.orderprocess.ui.activity.settings.PrinterSettingActivity;
import com.munchado.orderprocess.ui.activity.settings.ProfileSettingActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class PrintSettingFragment extends BaseFragment implements View.OnClickListener{

    TextView tv_print_setting, tv_profile_setting,tv_order_processing_setting;
    public PrintSettingFragment() {
        // Required empty public constructor
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.PRINT;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =inflater.inflate(R.layout.fragment_print_setting, container, false);
        tv_print_setting = (TextView)view.findViewById(R.id.tv_print_setting);
        tv_profile_setting = (TextView)view.findViewById(R.id.tv_profile_setting);
        tv_order_processing_setting = (TextView)view.findViewById(R.id.tv_order_processing_setting);

        tv_print_setting.setOnClickListener(this);
        tv_profile_setting.setOnClickListener(this);
        tv_order_processing_setting.setOnClickListener(this);
        return view;
    }


    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.tv_print_setting){
            startActivity(new Intent(getActivity(), PrinterSettingActivity.class));
        } else if(v.getId()==R.id.tv_profile_setting){
            startActivity(new Intent(getActivity(), ProfileSettingActivity.class));
        } else if(v.getId()==R.id.tv_order_processing_setting){
            startActivity(new Intent(getActivity(), OrderProcessingSettingActivity.class));
        }
    }
}
