package com.munchado.orderprocess.model.reservation;

import java.util.ArrayList;

/**
 * Created by test on 28/7/17.
 */
public class ArchiveReservationResponseData {
    private String summary_title;

    private ArrayList<Archive_reservation> view_today_reservation;

    private String date_text;

    private ArrayList<Archive_reservation> incoming_reservations;

    private String time_zone_date;

    private ArrayList<Archive_reservation> archive_reservation;

    private Today_reservations today_reservations;

    private String reservation_date;

    public String getSummary_title ()
    {
        return summary_title;
    }

    public void setSummary_title (String summary_title)
    {
        this.summary_title = summary_title;
    }

    public ArrayList getView_today_reservation ()
    {
        return view_today_reservation;
    }

    public void setView_today_reservation (ArrayList view_today_reservation)
    {
        this.view_today_reservation = view_today_reservation;
    }

    public String getDate_text ()
    {
        return date_text;
    }

    public void setDate_text (String date_text)
    {
        this.date_text = date_text;
    }

    public ArrayList getIncoming_reservations ()
    {
        return incoming_reservations;
    }

    public void setIncoming_reservations (ArrayList incoming_reservations)
    {
        this.incoming_reservations = incoming_reservations;
    }

    public String getTime_zone_date ()
    {
        return time_zone_date;
    }

    public void setTime_zone_date (String time_zone_date)
    {
        this.time_zone_date = time_zone_date;
    }

    public ArrayList<Archive_reservation> getArchive_reservation ()
    {
        return archive_reservation;
    }

    public void setArchive_reservation (ArrayList<Archive_reservation> archive_reservation)
    {
        this.archive_reservation = archive_reservation;
    }

    public Today_reservations getToday_reservations ()
    {
        return today_reservations;
    }

    public void setToday_reservations (Today_reservations today_reservations)
    {
        this.today_reservations = today_reservations;
    }

    public String getReservation_date ()
    {
        return reservation_date;
    }

    public void setReservation_date (String reservation_date)
    {
        this.reservation_date = reservation_date;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [summary_title = "+summary_title+", view_today_reservation = "+view_today_reservation+", date_text = "+date_text+", incoming_reservations = "+incoming_reservations+", time_zone_date = "+time_zone_date+", archive_reservation = "+archive_reservation+", today_reservations = "+today_reservations+", reservation_date = "+reservation_date+"]";
    }
}
