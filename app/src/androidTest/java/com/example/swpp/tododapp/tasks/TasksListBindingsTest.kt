package com.example.swpp.tododapp.tasks

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import androidx.test.platform.app.InstrumentationRegistry
import com.example.swpp.tododapp.data.Task
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate

/**
 * Integration tests for [setDueDateLabel], written from specs/TasksListBindings.spec.md.
 */
@SmallTest
@RunWith(AndroidJUnit4::class)
class TasksListBindingsTest {

    private lateinit var targetContext: Context
    private lateinit var today: LocalDate

    @Before
    fun setUp() {
        targetContext = InstrumentationRegistry.getInstrumentation().targetContext
        today = LocalDate.now()
    }

    /** The view shows exactly [expected] and is visible. */
    private fun assertShows(expected: String, textView: TextView) {
        assertEquals(expected, textView.text.toString())
        assertEquals(View.VISIBLE, textView.visibility)
    }

    /** The user sees no D-day label: the view is not visible, or its text is empty. */
    private fun assertShowsNoLabel(textView: TextView) {
        assertTrue(
            "Expected no label but was \"${textView.text}\" with visibility ${textView.visibility}",
            textView.visibility != View.VISIBLE || textView.text.toString() == ""
        )
    }

    // ---------- Success cases ----------

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeDueIn3Days_showsDMinus3() {
        // GIVEN - A new TextView and an active task due in 3 days
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3))

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - D-3 is shown
        assertShows("D-3", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeOverdueBy3Days_showsDPlus3() {
        // GIVEN - A new TextView and an active task that was due 3 days ago
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(3))

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - D+3 is shown
        assertShows("D+3", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeDueToday_showsDDay() {
        // GIVEN - A new TextView and an active task due today
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today)

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - D-Day is shown
        assertShows("D-Day", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedWithDueDate_showsClear() {
        // GIVEN - A new TextView and a completed task due in 3 days
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3))

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - CLEAR is shown
        assertShows("CLEAR", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_completedWithoutDueDate_showsClear() {
        // GIVEN - A new TextView and a completed task with no due date
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = null)

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - CLEAR is shown
        assertShows("CLEAR", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeWithoutDueDate_showsNoLabel() {
        // GIVEN - A new TextView and an active task with no due date
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = null)

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - No label is shown
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_nullTask_showsNoLabel() {
        // GIVEN - A new TextView
        val textView = TextView(targetContext)

        // WHEN - Bind a null task
        setDueDateLabel(textView, null)

        // THEN - No label is shown
        assertShowsNoLabel(textView)
    }

    // ---------- Edge cases: due-today boundary ----------

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeOverdueBy1Day_showsDPlus1() {
        // GIVEN - A new TextView and an active task that was due yesterday
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(1))

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - D+1 is shown
        assertShows("D+1", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_activeDueIn1Day_showsDMinus1() {
        // GIVEN - A new TextView and an active task due tomorrow
        val textView = TextView(targetContext)
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(1))

        // WHEN - Bind the label
        setDueDateLabel(textView, task)

        // THEN - D-1 is shown
        assertShows("D-1", textView)
    }

    // ---------- Edge cases: check / uncheck ----------

    @Test
    @UiThreadTest
    fun setDueDateLabel_checkTaskWithDueDate_changesDMinus3ToClear() {
        // GIVEN - A row showing D-3 for an active task
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3))
        val textView = TextView(targetContext)
        setDueDateLabel(textView, task)

        // WHEN - The task is checked and the row is bound again
        setDueDateLabel(textView, task.copy(isCompleted = true))

        // THEN - CLEAR is shown
        assertShows("CLEAR", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_uncheckTaskWithDueDate_changesClearToDMinus3() {
        // GIVEN - A row showing CLEAR for a completed task due in 3 days
        val task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3))
        val textView = TextView(targetContext)
        setDueDateLabel(textView, task)

        // WHEN - The task is unchecked and the row is bound again
        setDueDateLabel(textView, task.copy(isCompleted = false))

        // THEN - D-3 is shown
        assertShows("D-3", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_checkTaskWithoutDueDate_changesNoLabelToClear() {
        // GIVEN - A row showing no label for an active task with no due date
        val task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = null)
        val textView = TextView(targetContext)
        setDueDateLabel(textView, task)

        // WHEN - The task is checked and the row is bound again
        setDueDateLabel(textView, task.copy(isCompleted = true))

        // THEN - CLEAR is shown
        assertShows("CLEAR", textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_uncheckTaskWithoutDueDate_changesClearToNoLabel() {
        // GIVEN - A row showing CLEAR for a completed task with no due date
        val task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = null)
        val textView = TextView(targetContext)
        setDueDateLabel(textView, task)

        // WHEN - The task is unchecked and the row is bound again
        setDueDateLabel(textView, task.copy(isCompleted = false))

        // THEN - No label is shown
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_uncheckTaskDueToday_changesClearToDDay() {
        // GIVEN - A row showing CLEAR for a completed task due today
        val task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today)
        val textView = TextView(targetContext)
        setDueDateLabel(textView, task)

        // WHEN - The task is unchecked and the row is bound again
        setDueDateLabel(textView, task.copy(isCompleted = false))

        // THEN - D-Day is shown
        assertShows("D-Day", textView)
    }

    // ---------- Edge cases: reused row ----------

    @Test
    @UiThreadTest
    fun setDueDateLabel_reusedRowLabeledToUnlabeled_showsNoLabel() {
        // GIVEN - A row showing D-3 for another task
        val textView = TextView(targetContext)
        setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))

        // WHEN - The row is reused for an active task with no due date
        setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = null))

        // THEN - No label is shown
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_reusedRowLabeledToNullTask_showsNoLabel() {
        // GIVEN - A row showing D-3 for another task
        val textView = TextView(targetContext)
        setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))

        // WHEN - The row is bound with a null task
        setDueDateLabel(textView, null)

        // THEN - No label is shown
        assertShowsNoLabel(textView)
    }

    @Test
    @UiThreadTest
    fun setDueDateLabel_reusedRowUnlabeledToLabeled_showsDDay() {
        // GIVEN - A row showing no label for an active task with no due date
        val textView = TextView(targetContext)
        setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = null))

        // WHEN - The row is reused for an active task due today
        setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = today))

        // THEN - D-Day is shown
        assertShows("D-Day", textView)
    }
}
