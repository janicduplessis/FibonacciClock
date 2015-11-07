package com.example.janicduplessis.myapplication;

import android.graphics.Color;

/**
 * Created by janicduplessis on 2015-11-06.
 */
public class ColorConfig {
    public ColorType colorType;
    public int colorValue;
    public float hueTolerance;
    public float saturationTolerance;
    public float valueTolerance;

    public ColorConfig(ColorType colorType, int colorValue, float hueTolerence, float saturationTolerence, float valueTolerence) {
        this.colorType = colorType;
        this.colorValue = colorValue;
        this.hueTolerance = hueTolerence;
        this.saturationTolerance = saturationTolerence;
        this.valueTolerance = valueTolerence;
    }
}
