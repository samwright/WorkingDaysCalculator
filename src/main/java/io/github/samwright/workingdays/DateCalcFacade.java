package io.github.samwright.workingdays;

import de.jollyday.Holiday;
import de.jollyday.HolidayCalendar;
import de.jollyday.HolidayManager;
import net.objectlab.kit.datecalc.common.DateCalculator;
import net.objectlab.kit.datecalc.common.DefaultHolidayCalendar;
import net.objectlab.kit.datecalc.common.HolidayHandlerType;
import net.objectlab.kit.datecalc.joda.LocalDateKitCalculatorsFactory;
import org.joda.time.DateTimeConstants;
import org.joda.time.LocalDate;

import java.util.*;

/**
 * User: Sam Wright Date: 20/09/2013 Time: 20:14
 */
public class DateCalcFacade {
    private final static HolidayManager holidayManager;
    private final static Set<String> excludedHolidays;

    static {
        holidayManager = HolidayManager.getInstance(HolidayCalendar.UNITED_KINGDOM);
        excludedHolidays = new HashSet<>();
        excludedHolidays.add("-12-15");
        excludedHolidays.add("-12-16");
        excludedHolidays.add("-12-17");
    }

    private static boolean isNotExcluded(LocalDate date) {
        return !excludedHolidays.contains(date.toString("-MM-dd"));
    }

    /**
     * Gets the calendar years of all days in the specified range.
     *
     * NB. This overestimates the number of holidays in the year (it assumes at least 150 days a
     * year are spent working).
     *
     * @param startDate the start data of the range.
     * @param durationInDays the duration of the range, in days.
     * @return the calendar years of all days in the specified range.
     */
    public static List<Integer> getApproximateYears(LocalDate startDate, int durationInDays) {
        List<Integer> years = new ArrayList<>();
        int year = startDate.year().get();
        years.add(year);

        while (durationInDays > 0) {
            years.add(++year);
            durationInDays -= 150;
        }

        return years;
    }

    /**
     * Returns all holidays in the given list of years.
     *
     * @param years the years to find all holidays in.
     * @return the holidays in the given years.
     */
    public static Set<LocalDate> getHolidaysInYears(List<Integer> years) {
        Set<LocalDate> holidays = new HashSet<>();

        for (int year : years) {
            List<LocalDate> holidaysThisYear = new ArrayList<>();

            for (Holiday holiday : holidayManager.getHolidays(year, "en"))
                if (isNotExcluded(holiday.getDate()))
                    holidaysThisYear.add(holiday.getDate());

            fixChristmasHolidays(holidaysThisYear);

            holidays.addAll(holidaysThisYear);
        }

        return holidays;
    }

    private static void fixChristmasHolidays(List<LocalDate> holidaysThisYear) {
        Collections.sort(holidaysThisYear);

        // Get Boxing day (last by date)
        LocalDate boxingDay = holidaysThisYear.remove(holidaysThisYear.size() - 1);

        // Get Christmas day (last by date, except for Boxing day which is already removed)
        LocalDate xmasDay = holidaysThisYear.remove(holidaysThisYear.size() - 1);

        // Check Christmas is around the 25th:
        if (xmasDay.getMonthOfYear() != 12 || xmasDay.getDayOfMonth() < 25)
            throw new RuntimeException("Wrongly detected xmas as: " + xmasDay);

        // Bug in JollyDay - it will always move boxing day to next Tuesday,
        // regardless of whether xmas was on Monday or the previous Friday.  Not a problem,
        // unless xmas is on Friday and Boxing day is on Monday, because JollyDay thinks it
        // should be Tuesday.
        if (xmasDay.getDayOfWeek() == DateTimeConstants.FRIDAY
                && boxingDay.getDayOfWeek() == DateTimeConstants.TUESDAY) {
            boxingDay = boxingDay.withDayOfWeek(DateTimeConstants.MONDAY);
        }

        holidaysThisYear.add(xmasDay);
        holidaysThisYear.add(boxingDay);
    }

    /**
     * Gets the next working date after the given duration (in working days) after the given
     * starting date.
     *
     * @param startDate the starting date of the range.
     * @param durationInDays the number of working days to count on.
     * @return the next working date after the given duration after the given starting date.
     */
    public static LocalDate getEndDate(LocalDate startDate, int durationInDays) {
        List<Integer> approximateYears = getApproximateYears(startDate, durationInDays);
        Set<LocalDate> holidayDates = getHolidaysInYears(approximateYears);

        String yearStart = approximateYears.get(0) + "-01-01";
        String yearEnd = approximateYears.get(approximateYears.size()-1) + "-12-31";

        DefaultHolidayCalendar<LocalDate> calendar = new DefaultHolidayCalendar<>(
                holidayDates,
                new LocalDate(yearStart),
                new LocalDate(yearEnd)
        );

        // Register holiday calendar with the string "UK"
        LocalDateKitCalculatorsFactory.getDefaultInstance().registerHolidays("UK", calendar);

        // Create the date calculator based on the created calendar
        DateCalculator<LocalDate> calc
                = LocalDateKitCalculatorsFactory.getDefaultInstance()
                    .getDateCalculator("UK", HolidayHandlerType.FORWARD);

        // Set the starting date
        calc.setStartDate(startDate);

        // Get next working date after given durationInDays
        return calc.moveByBusinessDays(durationInDays).getCurrentBusinessDate();
    }

}
