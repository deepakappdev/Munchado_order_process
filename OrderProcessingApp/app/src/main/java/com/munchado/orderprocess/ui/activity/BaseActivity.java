package com.munchado.orderprocess.ui.activity;

import android.content.Context;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.munchado.orderprocess.ui.fragment.BaseFragment;

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


    public void addFragment(BaseFragment baseFragment, int containerId) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerId, baseFragment);
        ft.commit();


    }

}
