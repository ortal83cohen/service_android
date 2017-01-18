package com.hpe.sb.mobile.app;

import com.hpe.sb.mobile.app.common.uiComponents.todocards.DotsIndexIndicatorView;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import android.app.Instrumentation;
import android.support.test.runner.AndroidJUnit4;
import android.test.InstrumentationTestCase;
import android.view.View;


/**
 * Created by chovel on 24/04/2016.
 *
 */
@RunWith(AndroidJUnit4.class)
public class DotsIndexIndicatorViewTest  extends InstrumentationTestCase {

    private DotsIndexIndicatorView dotsIndexIndicatorView;
    public static final String INCORRECT_VISIBILITY_FOR_DOT_INDICATOR_ERROR_STR = "Incorrect visibility for dot indicator in index %s";
    private static final int CARD_AMOUNT = 10;
    private static final int MAX_DOTS = 3;
    Instrumentation instrumentation;
    @Before
    public void setUp() {
        instrumentation = getInstrumentation();
        dotsIndexIndicatorView = new DotsIndexIndicatorView(instrumentation.getContext(), CARD_AMOUNT, MAX_DOTS);

    }

    @Test
    public void testDotVisibility() {
        //first part
        dotsIndexIndicatorView.onCardSelected(0);
        assertDotVisibility(0, MAX_DOTS, View.VISIBLE);

        //middle
        dotsIndexIndicatorView.onCardSelected(5);
        assertDotVisibility(0, MAX_DOTS, View.VISIBLE);

        //end part
        dotsIndexIndicatorView.onCardSelected(9);
        assertDotVisibility(1, MAX_DOTS, View.INVISIBLE);
        assertDotVisibility(0, 1, View.VISIBLE);

        //back to first
        dotsIndexIndicatorView.onCardSelected(0);
        assertDotVisibility(0, MAX_DOTS, View.VISIBLE);

        //card amount is smaller than max dots
        dotsIndexIndicatorView.setCardAmount(2);
        dotsIndexIndicatorView.onCardSelected(0);
        assertDotVisibility(0, 2, View.VISIBLE);

        //end case where card amount == max dots
        dotsIndexIndicatorView.setCardAmount(MAX_DOTS);
        dotsIndexIndicatorView.onCardSelected(0);
        assertDotVisibility(0, MAX_DOTS, View.VISIBLE);
    }

    private void assertDotVisibility(int startIndex, int endIndex, int visibility) {
        for (int i = startIndex; i < endIndex; i++) {
            assertEquals(String.format(INCORRECT_VISIBILITY_FOR_DOT_INDICATOR_ERROR_STR, i), visibility, dotsIndexIndicatorView.getDots()[i].getVisibility());
        }
    }

    @Test
    public void testArrowsVisibility() {

        //first part
        dotsIndexIndicatorView.onCardSelected(0);
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.VISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());

        //middle
        dotsIndexIndicatorView.onCardSelected(5);
        assertEquals(View.VISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.VISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());

        //end part
        dotsIndexIndicatorView.onCardSelected(9);
        assertEquals(View.VISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());

        //back to first
        dotsIndexIndicatorView.onCardSelected(0);
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.VISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());

        //card amount is smaller than max dots
        dotsIndexIndicatorView.setCardAmount(2);
        dotsIndexIndicatorView.onCardSelected(0);
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());

        //end case where card amount == max dots
        dotsIndexIndicatorView.setCardAmount(MAX_DOTS);
        dotsIndexIndicatorView.onCardSelected(0);
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getStartArrow().getVisibility());
        assertEquals(View.INVISIBLE, dotsIndexIndicatorView.getEndArrow().getVisibility());
    }
}
