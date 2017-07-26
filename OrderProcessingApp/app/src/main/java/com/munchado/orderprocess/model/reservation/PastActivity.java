package com.munchado.orderprocess.model.reservation;

/**
 * Created by test on 25/7/17.
 */
public class PastActivity
{
    private String totalorder;

    private String totalcheckin;

    private String totalreservation;

    private String totalreview;

    public String getTotalorder ()
    {
        return totalorder;
    }

    public void setTotalorder (String totalorder)
    {
        this.totalorder = totalorder;
    }

    public String getTotalcheckin ()
    {
        return totalcheckin;
    }

    public void setTotalcheckin (String totalcheckin)
    {
        this.totalcheckin = totalcheckin;
    }

    public String getTotalreservation ()
    {
        return totalreservation;
    }

    public void setTotalreservation (String totalreservation)
    {
        this.totalreservation = totalreservation;
    }

    public String getTotalreview ()
    {
        return totalreview;
    }

    public void setTotalreview (String totalreview)
    {
        this.totalreview = totalreview;
    }

    @Override
    public String toString()
    {
        return "ClassPojo [totalorder = "+totalorder+", totalcheckin = "+totalcheckin+", totalreservation = "+totalreservation+", totalreview = "+totalreview+"]";
    }
}
