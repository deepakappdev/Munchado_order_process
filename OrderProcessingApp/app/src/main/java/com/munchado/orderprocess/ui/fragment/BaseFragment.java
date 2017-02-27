package com.munchado.orderprocess.ui.fragment;

import android.os.Handler;
import android.support.v4.app.Fragment;

import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.ui.activity.BaseActivity;
import com.munchado.orderprocess.ui.activity.HomeActivity;

/**
 * Created by android on 22/2/17.
 */

public abstract class BaseFragment extends Fragment {
    Handler handler = new Handler();

    abstract FRAGMENTS getFragmentId();

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setCustomTitle(getCustomTitle(getFragmentId()));
    }

    public void showToast(String message) {
        if(getActivity()!=null)
            ((BaseActivity)getActivity()).showToast(message);
    }

    private String getCustomTitle(FRAGMENTS fragmentId) {
        switch (fragmentId) {
            case ACTIVE:
                return "Active Orders";
            case ARCHIVE:
                return "Archive Orders";
            case LOGIN:
                return "Login";
            case ORDER_DETAIL:
                return "Order Detail";
            case PRINT:
                return "Settings";
            default:
                return "Order Process";
        }
    }
}
