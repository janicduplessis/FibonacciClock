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
            zone.color = ColorType.NONE;
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
                double ratio = zone.matches[i] / (double)(zone.rect.width() * zone.rect.height());
                if (ratio >= 0.5 && (bestMatch == -1 || ratio > bestMatchRatio)) {
                    bestMatch = i;
                    bestMatchRatio = ratio;
                }
            }
            if (bestMatch != -1) {
                zone.color = mColorConfigs.get(bestMatch).colorType;
            }
        }

        ImageParserResult res = new ImageParserResult();
        res.success = true;
        res.values = new ColorType[mZones.size()];
        for (int i = 0; i < mZones.size(); i++) {
            if (mZones.get(i).color == ColorType.NONE) {
                res.success = false;
            }
            res.values[i] = mZones.get(i).color;
        }

        return res;
    }

    private int indexOfMatch(int pixel) {
        // All right so we transform this into HSV
        float[] hsvValue = new float[3];
        Color.colorToHSV(pixel, hsvValue);
        float hue = hsvValue[0];
        float saturation = hsvValue[1];
        float value = hsvValue[2];

        for (int i = 0; i < mColorConfigs.size(); i++) {
            ColorConfig config = mColorConfigs.get(i);
            float[] configHsv = new float[3];
            Color.colorToHSV(config.colorValue, configHsv);
            // Get the difference between the pixel color and the config color.
            float hueDiff = Math.min(Math.abs(configHsv[0] - hue), 360 - Math.abs(configHsv[0] - hue));
            float satDiff = Math.abs(configHsv[1] - saturation);
            float valueDiff = Math.abs(configHsv[2] - value);

            if (hueDiff <= config.hueTolerance &&
                    satDiff <= config.saturationTolerance &&
                    valueDiff <= config.valueTolerance) {
                return i;
            }
        }

        return -1;
    }
}
