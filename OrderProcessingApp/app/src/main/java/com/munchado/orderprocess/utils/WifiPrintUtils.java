package com.munchado.orderprocess.utils;

/**
 * Created by munchado on 23/5/17.
 */
public class WifiPrintUtils {

    public String getReciept() {
        StringBuilder sb = new StringBuilder();
        sb.append("<!DOCTYPE html>");
        sb.append("<html>");
        sb.append("<body>");
        sb.append("<h1>My First Heading</h1>");
        sb.append("<p>My first paragraph.</p>");
        sb.append("</body>");
        sb.append("</html>");
        return sb.toString();
    }
}
