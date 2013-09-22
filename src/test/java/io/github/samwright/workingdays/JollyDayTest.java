package io.github.samwright.workingdays;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import org.joda.time.IllegalFieldValueException;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * User: Sam Wright Date: 20/09/2013 Time: 19:43
 */
public class JollyDayTest {
    private HolidayManager holidayManager;

    @Before
    public void setUp() throws Exception {
        holidayManager = HolidayManager.getInstance(HolidayCalendar.UNITED_KINGDOM);
    }

    @Test
    public void testPrintHolidays() throws Exception {
        List<String> holidays = new ArrayList<>();
        for (Holiday holiday : holidayManager.getHolidays(2016, "en"))
            holidays.add(holiday.toString());

        Collections.sort(holidays);

        for (String holidayString : holidays)
            System.out.println(holidayString);
    }

    @Test(expected = IllegalFieldValueException.class)
    public void testBadDate() throws Exception {
        new LocalDate("2012-02-31");
    }

    @Test(expected = NumberFormatException.class)
    public void testIntegerParse() throws Exception {
        Integer.parseInt("f3");
    }
}
