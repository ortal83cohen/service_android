package com.hpe.sb.mobile.app.common.services.dateTime;

import android.content.Context;


import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;

/**
 * Creator: Sergey Steblin
 * Date:    18/07/2016
 */
@RunWith(MockitoJUnitRunner.class)
public class DateTimeServiceTest {

    @Mock
    private Context context;

    @InjectMocks
    DateTimeService dateTimeService;

    @Test
    public void getPartOfDay() throws Exception {
        for(int partOfDay = 0; partOfDay < 6; partOfDay++) {
            assertEquals(PartOfDay.NIGHT, dateTimeService.getPartOfDay(partOfDay));
        }
        for(int partOfDay = 6; partOfDay < 12; partOfDay++) {
            assertEquals(PartOfDay.MORNING, dateTimeService.getPartOfDay(partOfDay));
        }
        for(int partOfDay = 12; partOfDay < 18; partOfDay++) {
            assertEquals(PartOfDay.AFTERNOON, dateTimeService.getPartOfDay(partOfDay));
        }
        for(int partOfDay = 18; partOfDay < 22; partOfDay++) {
            assertEquals(PartOfDay.EVENING, dateTimeService.getPartOfDay(partOfDay));
        }
        for(int partOfDay = 22; partOfDay < 24; partOfDay++) {
            assertEquals(PartOfDay.NIGHT, dateTimeService.getPartOfDay(partOfDay));
        }
    }
}
