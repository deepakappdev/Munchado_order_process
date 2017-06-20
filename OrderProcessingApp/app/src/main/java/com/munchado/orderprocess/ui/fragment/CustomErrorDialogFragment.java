package com.munchado.orderprocess.ui.fragment;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.munchado.orderprocess.MyApplication;
import com.munchado.orderprocess.R;
import com.munchado.orderprocess.ui.activity.LoginActivity;
import com.munchado.orderprocess.utils.PrefUtil;
import com.munchado.orderprocess.utils.StringUtils;

/**
 * Created by manish.soni on 9/1/2016.
 */
public class CustomErrorDialogFragment extends DialogFragment {

    private TextView mErrorText, mCloseBtn;
    private String mErrorMsg = "Please try again later!";
    private String mButtonText;
    private boolean isLogin;
    private static Activity activity;

    public CustomErrorDialogFragment() {
    }

    public static CustomErrorDialogFragment newInstance(String message) {
        CustomErrorDialogFragment f = new CustomErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        f.setArguments(args);
        return f;
    }

    public static CustomErrorDialogFragment newInstance(Activity activi, String message, boolean loginAgain) {
        activity = activi;
        CustomErrorDialogFragment f = new CustomErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putBoolean("loginAgain", loginAgain);
        f.setArguments(args);
        return f;
    }

    public static CustomErrorDialogFragment newInstance(String message, String buttonText) {
        CustomErrorDialogFragment f = new CustomErrorDialogFragment();
        Bundle args = new Bundle();
        args.putString("message", message);
        args.putString("buttonText", buttonText);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onStart() {
        super.onStart();

        final View decorView = getDialog().getWindow().getDecorView();
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                PropertyValuesHolder.ofFloat("scaleX", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("scaleY", 0.0f, 1.0f),
                PropertyValuesHolder.ofFloat("alpha", 0.0f, 1.0f));
        scaleDown.setDuration(200);
        scaleDown.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().setCanceledOnTouchOutside(false);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Dialog dialog = new Dialog(getActivity());
        mErrorMsg = getArguments().getString("message");
        mButtonText = getArguments().getString("buttonText");
        if (getArguments().containsKey("loginAgain")) {
            isLogin = getArguments().getBoolean("loginAgain");
        }

        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        dialog.setContentView(R.layout.dialog_error_layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        mErrorText = (TextView) dialog.findViewById(R.id.mErrortext);
        mCloseBtn = (TextView) dialog.findViewById(R.id.mCloseBtn);
        if (!StringUtils.isNullOrEmpty(mButtonText))
            mCloseBtn.setText(mButtonText);
        mErrorText.setText(mErrorMsg);
        mCloseBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final View decorView = getDialog().getWindow().getDecorView();
                ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(decorView,
                        PropertyValuesHolder.ofFloat("scaleX", 1.0f, 0.0f),
                        PropertyValuesHolder.ofFloat("scaleY", 1.0f, 0.0f),
                        PropertyValuesHolder.ofFloat("alpha", 1.0f, 0.0f));
                scaleDown.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        dismiss();
                        if (isLogin) {
                            Intent i = new Intent(activity, LoginActivity.class);
                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            String username = PrefUtil.getUsername();
                            String password = PrefUtil.getPassword();
                            PrefUtil.clearAllData();
                            PrefUtil.putUsername(username);
                            PrefUtil.putPassword(password);
                            activity.startActivity(i);
                            activity.finish();
                            MyApplication.isDialogShown = false;
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                scaleDown.setDuration(200);
                scaleDown.start();
            }
        });
        return dialog;
    }
}
