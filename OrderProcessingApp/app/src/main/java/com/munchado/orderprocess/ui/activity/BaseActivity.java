package com.munchado.orderprocess.ui.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.munchado.orderprocess.R;
import com.munchado.orderprocess.common.FRAGMENTS;
import com.munchado.orderprocess.ui.fragment.ActiveOrderFragment;
import com.munchado.orderprocess.ui.fragment.ArchiveOrderFragment;
import com.munchado.orderprocess.ui.fragment.BaseFragment;
import com.munchado.orderprocess.ui.fragment.PrintSettingFragment;

/**
 * Created by android on 22/2/17.
 */

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onPause() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
        super.onPause();
    }


  /*  public void addFragment(FRAGMENTS fragmentId, Bundle bundle) {
        BaseFragment fragment = getFragment(fragmentId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack(fragmentId.name());
        ft.commit();

    }*/

    public void addFragment(FRAGMENTS fragmentId, Bundle bundle) {
        BaseFragment fragment = getFragment(fragmentId);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    private BaseFragment getFragment(FRAGMENTS fragmentId) {
        BaseFragment fragment = null;
        switch (fragmentId) {
            case ACTIVE:
                fragment = new ActiveOrderFragment();
                setTitle("Active Orders");
                break;
            case ARCHIVE:
                fragment = new ArchiveOrderFragment();
                setTitle("Archive Orders");
                break;
            case PRINT:
                fragment = new PrintSettingFragment();
                setTitle("Settings");
                break;
            case LOGIN:
                break;
        }
        return fragment;
    }


    public void popToHomePage(){
        getSupportFragmentManager().popBackStackImmediate(FRAGMENTS.ACTIVE.name(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

    }

















}
