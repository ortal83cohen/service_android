package com.hpe.sb.mobile.app.common.uiComponents.categories.adapter;

import android.graphics.Color;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mufler on 28/04/2016.
 */
public class ColorParsingUtil {
    private static final String TAG = ColorParsingUtil.class.getSimpleName();
    private static Pattern RGBA_PATTERN = Pattern.compile(
            "^rgba\\s*\\(\\s*(\\d{0,3})\\s*,\\s*(\\d{0,3})\\s*,\\s*(\\d{0,3})\\s*,\\s*([01]*(\\.\\d+)?)\\s*\\)$",
            Pattern.CASE_INSENSITIVE);
    private static Pattern RGB_PATTERN = Pattern.compile(
            "^rgb\\s*\\(\\s*(\\d{0,3})\\s*,\\s*(\\d{0,3})\\s*,\\s*(\\d{0,3})\\s*\\)$",
            Pattern.CASE_INSENSITIVE);

    public String convertColorToHexIfNeeded(String colorAsStr){
        if (colorAsStr == null) {
            return null;
        }
        try {
            Matcher rgbaMatcher = RGBA_PATTERN.matcher(colorAsStr);
            if (rgbaMatcher.matches()) {
                int red = parseColorValue(rgbaMatcher.group(1));
                int green = parseColorValue(rgbaMatcher.group(2));
                int blue = parseColorValue(rgbaMatcher.group(3));
                int alpha = parseAlpha(rgbaMatcher.group(4));
                String hex = String.format("#%02x%02x%02x%02x", alpha, red, green, blue);
                return hex;
            } else {
                Matcher rgbMatcher = RGB_PATTERN.matcher(colorAsStr);
                if (rgbMatcher.matches()) {
                    int red = parseColorValue(rgbMatcher.group(1));
                    int green = parseColorValue(rgbMatcher.group(2));
                    int blue = parseColorValue(rgbMatcher.group(3));
                    String hex = String.format("#%02x%02x%02x", red, green, blue);
                    return hex;
                } else { //try to parse it with android to see if its legal
                    try {
                        final int colorAsInt = Color.parseColor(colorAsStr);
                        Log.d(TAG, "Parsed successfully: " + colorAsStr + "to " + colorAsInt);
                        return colorAsStr;
                    } catch (Exception e) {
                        Log.e(TAG, "Invalid color value: " + colorAsStr + "; will be chosen random");
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Unexpected exception in parsing color " + colorAsStr);
            return null;
        }
    }



    // input color is red/green/blue part of color
    private int parseColorValue(String colorString)  {
        int color;
        try {
            color = Integer.valueOf(colorString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid color value: " + colorString + "; fallback to white");
            return 255;
        }
        if (color < 0 || color > 255) {
            Log.e(TAG, "Color value not in range [0...255]: " + colorString + "; fallback to white");
            return 255;
        }
        return color;
    }

    public static int parseAlpha(String colorString)  {
        double alpha;
        try {
            alpha = Double.valueOf(colorString);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Illegal alpha format " + colorString + ", setting 1 (opaque)");
            return 1;
        }
        if (alpha < 0 || alpha > 1) {
            Log.e(TAG, "Illegal alpha range " + colorString + ", setting 1 (opaque)");
            return 1;
        }
        return doubleToInt(alpha);
    }

    static int doubleToInt(double alpha) {
        return (int) Math.round(alpha * 255d);
    }

}
