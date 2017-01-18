package com.hpe.sb.mobile.app.features.request.factory;

import com.hpe.sb.mobile.app.common.dataClients.entityDisplay.EntityBadgeService;
import com.hpe.sb.mobile.app.serverModel.catalog.CatalogGroup;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.form.FormEnumDescriptor;
import com.hpe.sb.mobile.app.serverModel.form.FormField;
import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import com.hpe.sb.mobile.app.features.request.recycleview.type.*;

import java.util.List;

/**
 * Created by malikdav on 26/04/2016.
 *
 */
public class ViewTypeFactory {

    private final FormFieldViewTypeFactory formFieldViewTypeFactory;

    public ViewTypeFactory() {
        formFieldViewTypeFactory =  new FormFieldViewTypeFactory();

    }

    public DescribeNewRequestViewType describeNewRequestViewType() {
        return new DescribeNewRequestViewType();
    }

    public ChooseRelatedEntityViewType chooseRelatedEntitiesViewType(List<EntityItem> relatedEntities) {
        return new ChooseRelatedEntityViewType(relatedEntities);
    }

    public CategoriesViewType chooseCategoriesViewType(List<CatalogGroup> categories, boolean isShowStillNotFoundMessage) {
        return new CategoriesViewType(categories, isShowStillNotFoundMessage);
    }

    public ChooseRelatedEntityInCategoryViewType chooseRelatedEntityInCategoryViewType(String catalogGroupId) {
        return new ChooseRelatedEntityInCategoryViewType(catalogGroupId);
    }

    public ReviewChosenRelatedEntityViewType reviewChosenRelatedEntityViewType(Offering offering, EntityBadgeService entityBadgeService) {
        return new ReviewChosenRelatedEntityViewType(offering, entityBadgeService);
    }

    public FormSpecificFieldViewType createFormFieldViewType(FormField formField, List<FormEnumDescriptor> enumDescriptors) {
        return formFieldViewTypeFactory.createViewType(formField, enumDescriptors);
    }

    public AlmostThereViewType almostThereViewType() {
        return new AlmostThereViewType();
    }
}
