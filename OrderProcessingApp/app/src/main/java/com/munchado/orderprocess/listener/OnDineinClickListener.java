package com.munchado.orderprocess.listener;

import com.munchado.orderprocess.model.dinein.UpcomingReservation;

/**
 * Created by munchado on 28/4/17.
 */
public interface OnDineinClickListener {

    public void onDineItemClick(UpcomingReservation reservation);
}
