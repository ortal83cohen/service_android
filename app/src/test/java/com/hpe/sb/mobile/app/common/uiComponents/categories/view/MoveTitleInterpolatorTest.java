package com.hpe.sb.mobile.app.common.uiComponents.categories.view;

import org.junit.Before;
import org.junit.Test;

/**
 * Created by mufler on 25/04/2016.
 */

public class MoveTitleInterpolatorTest {

    MoveTitleInterpolator testedObject;

    @Before
    public void setUp() throws Exception {
        testedObject = new MoveTitleInterpolator(0, 100);
    }

    @Test
    public void test1() throws Exception {
        for (float i = 0; i <= 1; i = (float) (i + 0.1)) {
            System.out.println(i + " => " + testedObject.getInterpolation(i));
        }
    }
}
