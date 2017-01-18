package com.hpe.sb.mobile.app.serverModel;

import com.hpe.sb.mobile.app.common.utils.ModelsConverter;
import com.hpe.sb.mobile.app.serverModel.catalog.Offering;
import com.hpe.sb.mobile.app.serverModel.search.EntityType;

import com.hpe.sb.mobile.app.serverModel.search.EntityItem;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Created by salemo on 01/05/2016.
 *
 */
public class ModelsConverterTest {
    ModelsConverter modelsConverter = new ModelsConverter();

    @Test
    public void testResolveLocaleWithDefaultLocale() {
        String id = "id";
        EntityType serviceOffering = EntityType.SERVICE_OFFERING;
        String title = "title";
        String description = "description";
        String imageId = "imageId";
        double cost = 11.2;
        String costCurrency = "costCurrency";
        Offering offering = new Offering(id, serviceOffering, title, description, imageId,
                "categoryId", cost,
                costCurrency, true, true);
        List<EntityItem> entityItems = modelsConverter.convertOfferingsToEntityItems(Arrays.asList(offering));

        assertEquals(1,entityItems.size());
        assertEquals(id,entityItems.get(0).getId());
        assertEquals(serviceOffering,entityItems.get(0).getType());
        assertEquals(title,entityItems.get(0).getTitle());
        assertEquals(description,entityItems.get(0).getPreview());
        assertEquals(imageId,entityItems.get(0).getImageId());
        assertEquals(cost,entityItems.get(0).getCost(), 0);
        assertEquals(costCurrency,entityItems.get(0).getCostCurrency());
        assertEquals(true,entityItems.get(0).isPopularity());
        assertEquals(true, entityItems.get(0).isSupportableOnMobile());
    }

}
