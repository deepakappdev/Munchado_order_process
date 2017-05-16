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
    Handler handlerRing = new Handler();

    public abstract FRAGMENTS getFragmentId();

    @Override
    public void onResume() {
        super.onResume();
        if (getActivity() != null && getActivity() instanceof HomeActivity)
            ((HomeActivity) getActivity()).setCustomTitle(getCustomTitle(getFragmentId()));
    }

    public void showToast(String message) {
        if (getActivity() != null)
            ((BaseActivity) getActivity()).showToast(message);
    }

    public String getCustomTitle(FRAGMENTS fragmentId) {
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
            case DINE_IN:
                return "Snag A Spot";
            case DINE_IN_ARCHIVE:
                return "Snag A Spot";//"Dine-in Archive";
            case DINE_IN_DETAIL:
                return "CAN YOU ACCOMMODATE";//"Dine-in Details";
            default:
                return "Order Process";
        }
    }
}
