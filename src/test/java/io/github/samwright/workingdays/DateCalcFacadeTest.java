package io.github.samwright.workingdays;

import io.github.samwright.workingdays.DateCalcFacade;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;

import java.util.*;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * User: Sam Wright Date: 20/09/2013 Time: 20:40
 */
public class DateCalcFacadeTest {

    private Set<LocalDate> holidays2014, holidays2015;

    @Before
    public void setUp() {
        holidays2014 = new HashSet<>();
        holidays2015 = new HashSet<>();

        holidays2014.add(new LocalDate("2014-01-01"));
        holidays2014.add(new LocalDate("2014-04-18"));
        holidays2014.add(new LocalDate("2014-04-21"));
        holidays2014.add(new LocalDate("2014-05-05"));
        holidays2014.add(new LocalDate("2014-05-26"));
        holidays2014.add(new LocalDate("2014-08-25"));
        holidays2014.add(new LocalDate("2014-12-25"));
        holidays2014.add(new LocalDate("2014-12-26"));

        holidays2015.add(new LocalDate("2015-01-01"));
        holidays2015.add(new LocalDate("2015-04-03"));
        holidays2015.add(new LocalDate("2015-04-06"));
        holidays2015.add(new LocalDate("2015-05-04"));
        holidays2015.add(new LocalDate("2015-05-25"));
        holidays2015.add(new LocalDate("2015-08-31"));
        holidays2015.add(new LocalDate("2015-12-25"));
        holidays2015.add(new LocalDate("2015-12-28"));
    }

    private void removeWeekends(Set<LocalDate> dates) {
        Iterator<LocalDate> iterator = dates.iterator();

        while (iterator.hasNext()) {
            int nextDayOfWeek = iterator.next().getDayOfWeek();
            if (nextDayOfWeek == DateTimeConstants.SATURDAY
                    || nextDayOfWeek == DateTimeConstants.SUNDAY)
                iterator.remove();
        }
    }

    @Test
    public void testGetApproximateYears() throws Exception {
        List<Integer> allYears = DateCalcFacade.getApproximateYears(new LocalDate("2012-01-01"), 100);
        assertTrue(allYears.contains(2012));
        assertTrue(allYears.contains(2013));
    }

    private void checkHolidaysInYears(Set<LocalDate> holidays, List<Integer> years) {
        Set<LocalDate> holidaysInYears = DateCalcFacade.getHolidaysInYears(years);
        removeWeekends(holidaysInYears);
        assertEquals(holidays, holidaysInYears);
    }

    @Test
    public void testGetHolidaysIn2014() throws Exception {
        checkHolidaysInYears(holidays2014, Arrays.asList(2014));
    }

    @Test
    public void testGetHolidaysIn2015() throws Exception {
        checkHolidaysInYears(holidays2015, Arrays.asList(2015));
    }

    @Test
    public void testGetHolidaysIn2014And2015() throws Exception {
        Set<LocalDate> holidays2014And2015 = new HashSet<>(holidays2014);
        holidays2014And2015.addAll(holidays2015);

        checkHolidaysInYears(holidays2014And2015, Arrays.asList(2014, 2015));
    }

    @Test
    public void testGetEndDate() throws Exception {
        checkEndDate("2015-12-23", 2, "2015-12-29");
        checkEndDate("2015-12-24", 1, "2015-12-29");
        checkEndDate("2015-12-24", 2, "2015-12-30");
        checkEndDate("2015-12-24", 3, "2015-12-31");
        checkEndDate("2015-12-24", 4, "2016-01-04");
    }

    private void checkEndDate(String startDateString, int workingDays, String endDateString) {
        LocalDate endDate = DateCalcFacade.getEndDate(new LocalDate(startDateString), workingDays);
        assertEquals(endDateString, endDate.toString("yyyy-MM-dd"));
    }
}
