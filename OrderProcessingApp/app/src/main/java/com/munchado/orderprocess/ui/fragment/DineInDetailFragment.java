package com.munchado.orderprocess.ui.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.github.aakira.expandablelayout.ExpandableRelativeLayout;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.ui.activity.BaseActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class DineInDetailFragment extends BaseFragment implements View.OnClickListener {

    com.munchado.orderprocess.ui.widgets.SquareImageView iv_Profile;
    private View rootView;
    private Button btn_confirm_time, btn_alternate_time, btn_reject_time;
    private ExpandableRelativeLayout layout_confirm_time, layout_alternate_time, layout_reject_time;

    public DineInDetailFragment() {
        // Required empty public constructor
    }


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

        layout_confirm_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_confirm_time);
        layout_alternate_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_alternate_time);
        layout_reject_time = (ExpandableRelativeLayout) view.findViewById(R.id.layout_reject_time);

        layout_alternate_time.setExpanded(true);
        view.findViewById(R.id.iv_close).setOnClickListener(this);
        btn_confirm_time.setOnClickListener(this);
        btn_alternate_time.setOnClickListener(this);
        btn_reject_time.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_close) {
            ((BaseActivity) getActivity()).backPressed();
        } else if (v.getId() == R.id.btn_confirm_time) {

            layout_confirm_time.toggle();
            if (layout_confirm_time.isExpanded()) {
                btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            } else
                btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            if (layout_alternate_time.isExpanded())
                layout_alternate_time.collapse();
            if (layout_reject_time.isExpanded())
                layout_reject_time.collapse();
        } else if (v.getId() == R.id.btn_alternate_time) {
            layout_alternate_time.toggle();
            if (layout_alternate_time.isExpanded()) {
                btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            } else
                btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            if (layout_confirm_time.isExpanded())
                layout_confirm_time.collapse();
            if (layout_reject_time.isExpanded())
                layout_reject_time.collapse();
        } else if (v.getId() == R.id.btn_reject_time) {
            layout_reject_time.toggle();
            if (layout_reject_time.isExpanded()) {
                btn_alternate_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_bg_confirm_table));
                btn_confirm_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
                btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            } else
                btn_reject_time.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.grey_unselected_btn));
            if (layout_confirm_time.isExpanded())
                layout_confirm_time.collapse();
            if (layout_alternate_time.isExpanded())
                layout_alternate_time.collapse();
        }
    }

    @Override
    FRAGMENTS getFragmentId() {
        return FRAGMENTS.DINE_IN_DETAIL;
    }
}
