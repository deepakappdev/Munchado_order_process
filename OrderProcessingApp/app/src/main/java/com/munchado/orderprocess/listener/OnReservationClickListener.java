package com.munchado.orderprocess.listener;

import com.munchado.orderprocess.model.reservation.UpcomingReservationModelResponse;

/**
 * Created by munchado on 21/7/17.
 */

public interface OnReservationClickListener {

    void onReservationClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations);

    void onReservationActionClick(UpcomingReservationModelResponse.Incoming_reservations incomingReservations);
}
