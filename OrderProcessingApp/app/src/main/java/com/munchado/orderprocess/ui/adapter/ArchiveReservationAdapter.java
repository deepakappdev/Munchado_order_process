package com.munchado.orderprocess.ui.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.munchado.orderprocess.listener.OnReservationClickListener;
import com.munchado.orderprocess.model.reservation.UpcomingReservationModelResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by test on 25/7/17.
 */

public class ArchiveReservationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final OnReservationClickListener clickListener;
    private List<UpcomingReservationModelResponse.Incoming_reservations> orderItems = new ArrayList<>();
    private Context context;
    public ArchiveReservationAdapter(Context ctx, OnReservationClickListener clickListener)
    {
        this.clickListener = clickListener;
        context = ctx;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }
}
