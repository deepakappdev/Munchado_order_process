package com.munchado.orderprocess.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.munchado.orderprocess.Constants;

/**
 * Created by android on 22/2/17.
 */

public class BaseFragment extends Fragment {



    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     *
     * @return A new instance of fragment CurrentReviewFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static BaseFragment newInstance(String fragmentType , String...params) {

        String paramArray[] = params;

        BaseFragment baseFragment =null;

        if(fragmentType==null)
            return null ;
        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_PRINT_SETTING))
            baseFragment=  new PrintSettingFragment();
        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_ACTIVE_ORDER))
            baseFragment =  new ActiveOrderFragment();
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_NEW_TIP))
//            baseFragment =  new NewTipFragment();
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_NEW_REVIEW_DINEIN)) {
//            baseFragment = new NewReviewDineInFragment();
//        }
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_NEW_REVIEW_DELIVERY)) {
//            baseFragment = new NewReviewDeliveryFragment();
//        }
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_NEW_REVIEW_TAKEOUT)) {
//            baseFragment = new NewReviewTakeoutFragment();
//        }
//        /*Friends Fragment*/
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_FRIENDS))
//            baseFragment=  new NewReviewFragment();
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_FIND_FRIENDS))
//            baseFragment =  new CurrentReviewFragment();
//        else if(fragmentType.equalsIgnoreCase(Constants.FRAGMENT_INVITE_FRIENDS))
//            baseFragment =  new NewTipFragment();
        if(paramArray!=null && paramArray.length>0) {
            Bundle args = new Bundle();
            args.putStringArray(Constants.FRAGMENT_ARGS, paramArray);
            baseFragment.setArguments(args);
        }

        return baseFragment;
    }

    public static void addFragment(Context context , BaseFragment baseFragment , int containerId){
        //  ReviewMainFragment reviewMainFragment = new ReviewMainFragment();

        FragmentTransaction fragmentTransaction = ((FragmentActivity)context).getSupportFragmentManager().beginTransaction();

        fragmentTransaction.add(containerId,baseFragment).addToBackStack(null).commitAllowingStateLoss();


    }
}
