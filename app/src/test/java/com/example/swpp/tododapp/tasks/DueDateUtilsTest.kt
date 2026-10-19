package com.example.swpp.tododapp.tasks

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

/**
 * Unit tests for [dueDateLabel], written from specs/DueDateUtils.spec.md.
 */
class DueDateUtilsTest {

    // ---------- Success cases ----------

    @Test
    fun dueDateLabel_completedWithDueDate_returnsClear() {
        // GIVEN - A completed task due in 3 days
        val dueDate = LocalDate.of(2026, 10, 22)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedWithoutDueDate_returnsClear() {
        // GIVEN - A completed task with no due date
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(null, today, isCompleted = true)

        // THEN - The label is CLEAR
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_activeWithoutDueDate_returnsEmpty() {
        // GIVEN - An active task with no due date
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(null, today, isCompleted = false)

        // THEN - There is no label
        assertEquals("", result)
    }

    @Test
    fun dueDateLabel_activeDueToday_returnsDDay() {
        // GIVEN - An active task due today
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-Day
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueIn3Days_returnsDMinus3() {
        // GIVEN - An active task due in 3 days
        val dueDate = LocalDate.of(2026, 10, 22)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-3
        assertEquals("D-3", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy3Days_returnsDPlus3() {
        // GIVEN - An active task that was due 3 days ago
        val dueDate = LocalDate.of(2026, 10, 16)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+3
        assertEquals("D+3", result)
    }

    // ---------- Edge cases: due-today boundary ----------

    @Test
    fun dueDateLabel_activeOverdueBy1Day_returnsDPlus1() {
        // GIVEN - An active task that was due yesterday
        val dueDate = LocalDate.of(2026, 10, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_activeOnDueTodayBoundary_returnsDDayNotDZero() {
        // GIVEN - An active task exactly on the due-today boundary
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-Day, not D-0 or D+0
        assertEquals("D-Day", result)
    }

    @Test
    fun dueDateLabel_activeDueIn1Day_returnsDMinus1() {
        // GIVEN - An active task due tomorrow
        val dueDate = LocalDate.of(2026, 10, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    // ---------- Edge cases: completed boundary ----------

    @Test
    fun dueDateLabel_completedDueToday_returnsClear() {
        // GIVEN - A completed task due today
        val dueDate = LocalDate.of(2026, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR, not D-Day
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedOverdueBy1Day_returnsClear() {
        // GIVEN - A completed task that was due yesterday
        val dueDate = LocalDate.of(2026, 10, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR, not D+1
        assertEquals("CLEAR", result)
    }

    @Test
    fun dueDateLabel_completedDueIn1Day_returnsClear() {
        // GIVEN - A completed task due tomorrow
        val dueDate = LocalDate.of(2026, 10, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = true)

        // THEN - The label is CLEAR, not D-1
        assertEquals("CLEAR", result)
    }

    // ---------- Edge cases: digit boundary ----------

    @Test
    fun dueDateLabel_activeDueIn9Days_returnsDMinus9() {
        // GIVEN - An active task due in 9 days (one-digit N)
        val dueDate = LocalDate.of(2026, 10, 28)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-9
        assertEquals("D-9", result)
    }

    @Test
    fun dueDateLabel_activeDueIn10Days_returnsDMinus10() {
        // GIVEN - An active task due in 10 days (two-digit N)
        val dueDate = LocalDate.of(2026, 10, 29)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-10, without padding or spaces
        assertEquals("D-10", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy10Days_returnsDPlus10() {
        // GIVEN - An active task that was due 10 days ago (two-digit N)
        val dueDate = LocalDate.of(2026, 10, 9)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+10
        assertEquals("D+10", result)
    }

    // ---------- Edge cases: month end ----------

    @Test
    fun dueDateLabel_activeDueFirstDayOfNextMonth_returnsDMinus1() {
        // GIVEN - Today is the last day of October, due the first day of November
        val dueDate = LocalDate.of(2026, 11, 1)
        val today = LocalDate.of(2026, 10, 31)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeOverdueFromLastDayOfPreviousMonth_returnsDPlus1() {
        // GIVEN - Today is the first day of November, due the last day of October
        val dueDate = LocalDate.of(2026, 10, 31)
        val today = LocalDate.of(2026, 11, 1)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    // ---------- Edge cases: year end ----------

    @Test
    fun dueDateLabel_activeDueFirstDayOfNextYear_returnsDMinus1() {
        // GIVEN - Today is the last day of 2026, due the first day of 2027
        val dueDate = LocalDate.of(2027, 1, 1)
        val today = LocalDate.of(2026, 12, 31)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeOverdueFromLastDayOfPreviousYear_returnsDPlus1() {
        // GIVEN - Today is the first day of 2027, due the last day of 2026
        val dueDate = LocalDate.of(2026, 12, 31)
        val today = LocalDate.of(2027, 1, 1)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    // ---------- Edge cases: leap day and February ----------

    @Test
    fun dueDateLabel_activeDueOnLeapDay_returnsDMinus1() {
        // GIVEN - Today is 2028-02-28, due on the leap day
        val dueDate = LocalDate.of(2028, 2, 29)
        val today = LocalDate.of(2028, 2, 28)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeDueDayAfterLeapDay_returnsDMinus2() {
        // GIVEN - Today is 2028-02-28, due the day after the leap day
        val dueDate = LocalDate.of(2028, 3, 1)
        val today = LocalDate.of(2028, 2, 28)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The leap day is counted, so the label is D-2
        assertEquals("D-2", result)
    }

    @Test
    fun dueDateLabel_activeTodayIsLeapDay_returnsDMinus1() {
        // GIVEN - Today is the leap day, due the next day
        val dueDate = LocalDate.of(2028, 3, 1)
        val today = LocalDate.of(2028, 2, 29)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeOverdueFromLeapDay_returnsDPlus1() {
        // GIVEN - Today is 2028-03-01, due on the leap day
        val dueDate = LocalDate.of(2028, 2, 29)
        val today = LocalDate.of(2028, 3, 1)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    @Test
    fun dueDateLabel_activeOverdueFromDayBeforeLeapDay_returnsDPlus2() {
        // GIVEN - Today is 2028-03-01, due the day before the leap day
        val dueDate = LocalDate.of(2028, 2, 28)
        val today = LocalDate.of(2028, 3, 1)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The leap day is counted, so the label is D+2
        assertEquals("D+2", result)
    }

    @Test
    fun dueDateLabel_activeNonLeapFeb28ToMar1_returnsDMinus1() {
        // GIVEN - Today is 2027-02-28 (non-leap year), due 2027-03-01
        val dueDate = LocalDate.of(2027, 3, 1)
        val today = LocalDate.of(2027, 2, 28)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-1
        assertEquals("D-1", result)
    }

    @Test
    fun dueDateLabel_activeNonLeapOverdueFromFeb28_returnsDPlus1() {
        // GIVEN - Today is 2027-03-01 (non-leap year), due 2027-02-28
        val dueDate = LocalDate.of(2027, 2, 28)
        val today = LocalDate.of(2027, 3, 1)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+1
        assertEquals("D+1", result)
    }

    // ---------- Edge cases: one-month span ----------

    @Test
    fun dueDateLabel_activeDueIn30Days_returnsDMinus30() {
        // GIVEN - An active task due one day short of a calendar month
        val dueDate = LocalDate.of(2026, 11, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-30
        assertEquals("D-30", result)
    }

    @Test
    fun dueDateLabel_activeDueInOneCalendarMonth_returnsDMinus31() {
        // GIVEN - An active task due exactly one calendar month later (31 days)
        val dueDate = LocalDate.of(2026, 11, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - Total days are counted, so the label is D-31, not D-Day
        assertEquals("D-31", result)
    }

    @Test
    fun dueDateLabel_activeDueIn32Days_returnsDMinus32() {
        // GIVEN - An active task due one day past a calendar month
        val dueDate = LocalDate.of(2026, 11, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-32, not D-1
        assertEquals("D-32", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy29Days_returnsDPlus29() {
        // GIVEN - An active task overdue by one day short of a calendar month
        val dueDate = LocalDate.of(2026, 9, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+29
        assertEquals("D+29", result)
    }

    @Test
    fun dueDateLabel_activeOverdueByOneCalendarMonth_returnsDPlus30() {
        // GIVEN - An active task overdue by exactly one calendar month (30 days)
        val dueDate = LocalDate.of(2026, 9, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - Total days are counted, so the label is D+30, not D-Day
        assertEquals("D+30", result)
    }

    @Test
    fun dueDateLabel_activeOverdueBy31Days_returnsDPlus31() {
        // GIVEN - An active task overdue by one day past a calendar month
        val dueDate = LocalDate.of(2026, 9, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D+31, not D+1
        assertEquals("D+31", result)
    }

    // ---------- Edge cases: one-year span ----------

    @Test
    fun dueDateLabel_activeDueIn364Days_returnsDMinus364() {
        // GIVEN - An active task due one day short of a calendar year
        val dueDate = LocalDate.of(2027, 10, 18)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-364
        assertEquals("D-364", result)
    }

    @Test
    fun dueDateLabel_activeDueInOneCalendarYear_returnsDMinus365() {
        // GIVEN - An active task due exactly one calendar year later (365 days)
        val dueDate = LocalDate.of(2027, 10, 19)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - Total days are counted, so the label is D-365, not D-Day
        assertEquals("D-365", result)
    }

    @Test
    fun dueDateLabel_activeDueIn366Days_returnsDMinus366() {
        // GIVEN - An active task due one day past a calendar year
        val dueDate = LocalDate.of(2027, 10, 20)
        val today = LocalDate.of(2026, 10, 19)

        // WHEN - Build the label
        val result = dueDateLabel(dueDate, today, isCompleted = false)

        // THEN - The label is D-366
        assertEquals("D-366", result)
    }
}
