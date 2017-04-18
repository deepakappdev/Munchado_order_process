package com.munchado.orderprocess.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.ui.fragment.ActiveOrderFragment;
import com.munchado.orderprocess.ui.fragment.ArchiveOrderFragment;
import com.munchado.orderprocess.ui.fragment.BaseFragment;
import com.munchado.orderprocess.ui.fragment.OrderDetailFragment;
import com.munchado.orderprocess.ui.fragment.PrintSettingFragment;

/**
 * Created by android on 22/2/17.
 */

public class BaseActivity extends AppCompatActivity {

    private Toast toast;
    BaseFragment fragmentActiveOrder;

    public String order_ID = "", order_Status = "";

    public void addFragment(FRAGMENTS fragmentId, Bundle bundle) {
        BaseFragment fragment = getFragment(fragmentId);
        if (fragment != null) {
            fragment.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.frame_container, fragment);
            ft.addToBackStack(fragmentId.name());
            ft.commit();
        }
    }

    private BaseFragment getFragment(FRAGMENTS fragmentId) {
        BaseFragment fragment = null;
        switch (fragmentId) {
            case ACTIVE:
                fragment = new ActiveOrderFragment();
                fragmentActiveOrder = fragment;
                break;
            case ARCHIVE:
                fragment = new ArchiveOrderFragment();
                break;
            case PRINT:
                fragment = new PrintSettingFragment();
                break;
            case LOGIN:
                break;
            case ORDER_DETAIL:
                fragment = new OrderDetailFragment();
                break;
        }
        return fragment;
    }


    public void popToHomePage() {
        getSupportFragmentManager().popBackStack(FRAGMENTS.ACTIVE.name(), 0);

    }

    public void backPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 1) {
            getSupportFragmentManager().popBackStack();
        } else
            finish();
    }

    @Override
    protected void onPause() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }

        super.onPause();
    }

    public void showToast(String message) {
        if (toast == null)
            toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.setText(message);
        toast.show();
    }


}
