package com.example.janicduplessis.myapplication;

import android.graphics.Rect;

/**
 * Created by janicduplessis on 2015-11-06.
 */

/**
 * Represents a square zone on the fibonacci clock.
 */
public class Zone {
    public Zone(Rect rect) {
        this.rect = rect;
    }

    /**
     * Position of the zone.
     */
    public Rect rect;

    /**
     * The number of pixels inside the zone that matches each of the color configs.
     */
    public int[] matches;

    /**
     * The value of the matching color if a color is matching or -1 of no match.
     */
    public ColorType color;
}
