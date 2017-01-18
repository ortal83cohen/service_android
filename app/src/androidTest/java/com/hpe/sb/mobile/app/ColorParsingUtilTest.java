package com.hpe.sb.mobile.app;

import android.support.test.runner.AndroidJUnit4;
import com.hpe.sb.mobile.app.common.uiComponents.categories.adapter.ColorParsingUtil;
import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by mufler on 28/04/2016.
 */
@RunWith(AndroidJUnit4.class)
public class ColorParsingUtilTest {

    private ColorParsingUtil testedObject = new ColorParsingUtil();

    @Test
    public void testParseAlpha() throws Exception {
        Assert.assertEquals(0, testedObject.parseAlpha("0"));
        Assert.assertEquals(255, testedObject.parseAlpha("1"));
        Assert.assertEquals(51, testedObject.parseAlpha("0.2"));
        Assert.assertEquals(1, testedObject.parseAlpha("2"));
        Assert.assertEquals(1, testedObject.parseAlpha("-8"));
    }

    @Test
    public void testConvertColorRgbaBlack() throws Exception {
        String color = "RgBa ( 0, 0, 0, 0)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#00000000", output);
    }

    @Test
    public void testConvertColorRgbaWhite() throws Exception {
        String color = "RgBa ( 255, 255, 255, 1)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#ffffffff", output);
    }

    @Test
    public void testConvertColorRgbCapital() throws Exception {
        String color = "RgB ( 10,11,12)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#0a0b0c", output);
    }

    @Test
    public void testConvertColorRgbBlack() throws Exception {
        String color = "RgB ( 0, 0, 0)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#000000", output);
    }

    @Test
    public void testConvertColorRgbWhite() throws Exception {
        String color = "RgB ( 255, 255, 255)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#ffffff", output);
    }

    @Test
    public void testConvertColorRgb() throws Exception {
        String color = "RgBa ( 10,11,12,0.01)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#030a0b0c", output);
    }

    @Test
    public void testConvertColorRgbIllegal1() throws Exception {
        String color = "rgb(2,2,2, 3)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorRgbIllegal2() throws Exception {
        String color = "rgb(2,2,800,0.3)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorHex() throws Exception {
        String color = "#030a0b0c";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals("#030a0b0c", output);
    }

    @Test
    public void testConvertColor() throws Exception {
        String color = "blue";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertEquals(color, output);
    }

    @Test
    public void testConvertColorRgbaIllegal() throws Exception {
        String color = "RgBa ( 300, 500, 800, 3)";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorNull() throws Exception {
        final String output = testedObject.convertColorToHexIfNeeded(null);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorHexIllegal1() throws Exception {
        String color = "030a0b0c";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorHexIllegal2() throws Exception {
        String color = "#030a0";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }

    @Test
    public void testConvertColorHexIllegal3() throws Exception {
        String color = "colorGreen";
        final String output = testedObject.convertColorToHexIfNeeded(color);
        Assert.assertNull(output);
    }
}