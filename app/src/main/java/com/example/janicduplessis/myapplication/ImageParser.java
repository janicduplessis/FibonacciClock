package com.example.janicduplessis.myapplication;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by janicduplessis on 2015-11-06.
 */
public class ImageParser {
    private List<ColorConfig> mColorConfigs;
    private List<Zone> mZones;

    public ImageParser() {
        mZones = new ArrayList<>(5);
        mZones.add(Constants.ZONE_MEDIUM_TOP_LEFT, new Zone(new Rect(0, 0, 100, 100)));
        mZones.add(Constants.ZONE_SMALL_TOP_LEFT_1, new Zone(new Rect(100, 0, 150, 50)));
        mZones.add(Constants.ZONE_SMALL_TOP_LEFT_2, new Zone(new Rect(100, 50, 150, 100)));
        mZones.add(Constants.ZONE_BOTTOM_LEFT, new Zone(new Rect(0, 100, 150, 250)));
        mZones.add(Constants.ZONE_RIGHT, new Zone(new Rect(150, 0, 400, 250)));
    }

    public void setColorConfigs(List<ColorConfig> configs) {
        mColorConfigs = configs;
    }

    public ImageParserResult parseBitmap(Bitmap bitmap) {
        if (mColorConfigs == null) {
            throw new IllegalStateException("Color configs must be set before parsing.");
        }


        for (Zone zone: mZones) {
            // For each pixel inside the zone check if it matches any color from the
            // color configs.
            zone.matches = new int[mColorConfigs.size()];
            zone.value = -1;
            for(int x = zone.rect.left; x < zone.rect.right; x++) {
                for(int y = zone.rect.top; y < zone.rect.bottom; y++) {
                    int pixel = bitmap.getPixel(x, y);
                    int match = indexOfMatch(pixel);
                    if (match != -1) {
                        zone.matches[match]++;
                    }
                }
            }

            // Finds the best matching color for the zone
            // At least 80% of the pixels of the zone must match the same color.
            int bestMatch = -1;
            double bestMatchRatio = 0;
            for (int i = 0; i < zone.matches.length; i++) {
                double ratio = zone.matches[i] / (zone.rect.width() * zone.rect.height());
                if (ratio >= 0.8 && (bestMatch == -1 || ratio > bestMatchRatio)) {
                    bestMatch = i;
                    bestMatchRatio = ratio;
                }
            }
            if (bestMatch != -1) {
                zone.value = mColorConfigs.get(bestMatch).value;
            }
        }

        ImageParserResult res = new ImageParserResult();
        res.success = true;
        res.values = new int[mZones.size()];
        for (int i = 0; i < mZones.size(); i++) {
            if (mZones.get(i).value == -1) {
                res.success = false;
            }
            res.values[i] = mZones.get(i).value;
        }

        return res;
    }

    private int indexOfMatch(int pixel) {
        // TODO: Check if the color is close to the color specified in the config.
        for (int i = 0; i < mColorConfigs.size(); i++) {
            ColorConfig config = mColorConfigs.get(i);
            if(checkifColorValid(config.color))
                return i;
        }

        return -1;
    }

    private boolean checkifColorValid(int color)
    {
       //all rigth so we transform this into HSV
        float[] hsvValue = new float[3];
        Color.colorToHSV(color, hsvValue);

        float Hue = hsvValue[0];
        float Saturation = hsvValue[1];
        float Value = hsvValue[2];

        //acceptable values
        int SaturationMin = 50;
        int SaturationMax = 100;
        int ValueMin = 60;
        int ValueMax = 100;

        //more on the purple side
        int RedHueMinHighSpectrum = 318;
        int RedHueMaxHighSpectrum = 358;

        //more on the orange side
        int RedHueMinLowSpectrum = 0;
        int RedHueMaxLowSpectrum = 20;

        //between yellow and blue
        int GreenHueMin = 82;
        int GreenHueMax = 164;

        //between green and purple
        int BlueHueMin = 172;
        int BlueHueMax = 274;


        boolean SaturationOk = Saturation > SaturationMin && Saturation < SaturationMax;
        boolean ValueOk = Value > ValueMin && Value < ValueMax;
        boolean HueOk =  ((Hue > RedHueMinHighSpectrum && Hue < RedHueMaxHighSpectrum) || (Hue > RedHueMinLowSpectrum && Hue < RedHueMaxLowSpectrum) ||
                         (Hue > GreenHueMin && Hue < GreenHueMax) ||
                         (Hue > BlueHueMin && Hue < BlueHueMax));

        return SaturationOk && ValueOk && HueOk;
    }
}
