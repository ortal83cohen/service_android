package com.hpe.sb.mobile.app.features.request.recycleview;

import com.hpe.sb.mobile.app.features.request.recycleview.type.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by malikdav on 26/04/2016.
 */
public class ViewTypeMapping {

    public static final int DESCRIBE_REQUEST_VIEW_TYPE = 1;
    public static final int CHOOSE_RELATED_ENTITY_VIEW_TYPE = 2;
    public static final int CATEGORIES_VIEW_TYPE = 3;
    public static final int CHOOSE_RELATED_ENTITY_IN_CATEGORY_VIEW_TYPE = 4;
    public static final int REVIEW_CHOSEN_RELATED_ENTITY_VIEW_TYPE = 5;
    public static final int RICH_TEXT_VIEW_TYPE = 6;
    public static final int ALMOST_THERE_VIEW_TYPE = 7;
    public static final int ENUM_VIEW_TYPE = 8;

    private Map<Class<? extends NewRequestViewType>, Integer> viewTypeToDefintionNumber;
    public ViewTypeMapping() {
        viewTypeToDefintionNumber=new HashMap<>();
        viewTypeToDefintionNumber.put(DescribeNewRequestViewType.class, DESCRIBE_REQUEST_VIEW_TYPE);
        viewTypeToDefintionNumber.put(ChooseRelatedEntityViewType.class, CHOOSE_RELATED_ENTITY_VIEW_TYPE);
        viewTypeToDefintionNumber.put(CategoriesViewType.class, CATEGORIES_VIEW_TYPE);
        viewTypeToDefintionNumber.put(ChooseRelatedEntityInCategoryViewType.class, CHOOSE_RELATED_ENTITY_IN_CATEGORY_VIEW_TYPE);
        viewTypeToDefintionNumber.put(ReviewChosenRelatedEntityViewType.class, REVIEW_CHOSEN_RELATED_ENTITY_VIEW_TYPE);
        viewTypeToDefintionNumber.put(RichTextViewType.class, RICH_TEXT_VIEW_TYPE);
        viewTypeToDefintionNumber.put(AlmostThereViewType.class, ALMOST_THERE_VIEW_TYPE);
        viewTypeToDefintionNumber.put(EnumViewType.class, ENUM_VIEW_TYPE);
    }

    public int getViewType(Class<? extends NewRequestViewType> viewTypeClass) {
        Integer integer = viewTypeToDefintionNumber.get(viewTypeClass);
        if(integer == null) {
            throw new RuntimeException(String.format("Missing view type mapping for %s", viewTypeClass.getName()));
        } else {
            return integer;
        }
    }
}
