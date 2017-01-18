package com.hpe.sb.mobile.app.common.uiComponents;

import java.util.Stack;

import org.xml.sax.XMLReader;

import android.text.Editable;
import android.text.Html;
import android.util.Log;


/**
 * Created by oded on 02/08/2016.
 *
 */
public class HtmlTagHandler implements Html.TagHandler {
    /**
     * Keeps track of lists (ol, ul). On bottom of Stack is the outermost list
     * and on top of Stack is the most nested list
     */
    Stack<String> lists = new Stack<String>();
    /**
     * Tracks indexes of ordered lists so that after a nested list ends
     * we can continue with correct index of outer list
     */
    Stack<Integer> olNextIndex = new Stack<Integer>();

    @Override
    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
        if (tag.equalsIgnoreCase("ul")) {
            if (opening) {
                lists.push(tag);
            } else {
                lists.pop();
            }
        }
        else if (tag.equalsIgnoreCase("ol")) {
            if (opening) {
                lists.push(tag);
                olNextIndex.push(Integer.valueOf(1)).toString();
            } else {
                lists.pop();
                olNextIndex.pop().toString();


            }
        }
        else if (tag.equalsIgnoreCase("li")) {
            if (opening) {
                if (output.length() > 0 && output.charAt(output.length()-1) != '\n') {
                    output.append("\n");
                }
                String parentList = lists.peek();
                if (parentList.equalsIgnoreCase("ol")) {
                    output.append(new String(new char[lists.size()-1]).replace("\0", "  "));
                    output.append(olNextIndex.peek().toString()+". ");
                    olNextIndex.push(Integer.valueOf(olNextIndex.pop().intValue() + 1));
                }
                else if (parentList.equalsIgnoreCase("ul")) {
                    output.append(new String(new char[lists.size()-1]).replace("\0", "  "));
                    output.append("• ");
                }
            }
        } else {
            if (opening) Log.d("TagHandler", "Found an unsupported tag " + tag);
        }
    }
}
