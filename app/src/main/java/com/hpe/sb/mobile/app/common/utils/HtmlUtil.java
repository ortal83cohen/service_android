package com.hpe.sb.mobile.app.common.utils;

import android.text.Html;
import android.text.Spanned;

import com.hpe.sb.mobile.app.common.uiComponents.HtmlTagHandler;

/**
 * Created by oded on 02/08/2016.
 * util for using Html class correctly in the app
 * pls use this class instead of default Html class.
 * example:
 * use HtmlUtil.fromHtml instead of Html.fromHtml
 */
public class HtmlUtil {

    /**
     * @param string - the html string to present
     * @param imageGetter - a class that indicates how to deal with img tags, use null if you don't want this
     * @return - the Spanned that represents the string to display
     */
    public static Spanned fromHtml(String string, Html.ImageGetter imageGetter) {
        return Html.fromHtml(convertToHtmlString(string), imageGetter, new HtmlTagHandler());
    }

    /**
     * replace html character that are not represented correctly at mobile when using Html.fromHtml
     **/
    private static String convertToHtmlString(String htmlString) {
        //fix defect 18653 --> letters get link presentation when they are not links
        return htmlString.replace("&nbsp;", " ");
    }
}
