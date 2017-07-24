package com.munchado.orderprocess.model.reservation;

import com.munchado.orderprocess.model.BaseResponse;

import java.util.ArrayList;

/**
 * Created by munchado on 21/7/17.
 */

public class UpcomingReservationModelResponse extends BaseResponse{
    public Data data;

    public class Today_reservations {
        public String total_reservations;
        public String guests;
        public String total_upcoming_reservations;
    }

    public class Incoming_reservations {
        public String id;
        public String user_id;
        public String restaurant_id;
        public String city_id;
        public String seat_type_id;
        public String party_size;
        public String reserved_on;
        public String user_instruction;
        public String restaurant_comment;
        public String time_slot;
        public String meal_slot;
        public String status;
        public String restaurant_name;
        public String first_name;
        public String last_name;
        public String phone;
        public String email;
        public String reserved_seats;
        public String is_read;
        public String receipt_no;
        public String is_reviewed;
        public String review_id;
        public String cron_status;
        public String remarks;
        public String agent;
        public String host_name;
        public String user_ip;
        public String order_id;
        public String assignMuncher;
        public String cronUpdate;
        public String cronUpdateForCancelation;
        public String is_modify;
        public String today_time;
        public int res_flag;
        public String time_slot_readable;
        public String statusClass;
        public String newcustomer;
        public boolean inProgress;
    }

    public class Data {
        public Today_reservations today_reservations;
        public ArrayList<Incoming_reservations> incoming_reservations;
        public ArrayList<Incoming_reservations> view_today_reservation;
        public String summary_title;
        public String time_zone_date;
        public String date_text;
        public String reservation_date;
    }
}
