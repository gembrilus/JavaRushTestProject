package com.space.model;

import java.util.Calendar;

public class ShipUtils {
    public static Double rate(Ship ship) {
        double speed = ship.getSpeed();
        double k = ship.getUsed() ? 0.5d : 1.0d;

        Calendar cal = Calendar.getInstance();
        cal.setTime(ship.getProdDate());
        double y1 = cal.get(Calendar.YEAR);

        double result = (80 * speed * k) / (3019 - y1 + 1);
        return (double) Math.round(result * 100) / 100;
    }
}
