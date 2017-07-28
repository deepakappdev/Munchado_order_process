package com.munchado.orderprocess.model.reservation;

/**
 * Created by test on 28/7/17.
 */

public class Today_reservations {
    private String total_upcoming_reservations;

    private String guests;

    private String total_reservations;

    public String getTotal_upcoming_reservations ()
    {
        return total_upcoming_reservations;
    }

    public void setTotal_upcoming_reservations (String total_upcoming_reservations)
    {
        this.total_upcoming_reservations = total_upcoming_reservations;
    }

    public String getGuests ()
    {
        return guests;
    }

    public void setGuests (String guests)
    {
        this.guests = guests;
    }

    public String getTotal_reservations ()
    {
        return total_reservations;
    }

    public void setTotal_reservations (String total_reservations)
    {
        this.total_reservations = total_reservations;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [total_upcoming_reservations = "+total_upcoming_reservations+", guests = "+guests+", total_reservations = "+total_reservations+"]";
    }
}
