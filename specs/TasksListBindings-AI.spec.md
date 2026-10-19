# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Show the D-day label for `task` in a task-list row's `textView`. The label is `dueDateLabel(task.dueDate, <device's today>, task.isCompleted)`. When there is a label, the view shows that text and is visible. When there is none (no task, or an active task with no due date), the view shows no text and is hidden (`View.GONE`), so the title can take the row's width. Each call must fully set the view's text and visibility, because RecyclerView reuses row views for other tasks.

**Pre-conditions**
- `textView` is any `TextView`. Its text and visibility may still be left over from an earlier binding (a recycled row).
- `task` is a `Task` or `null`. Data binding passes `null` before the row's `task` variable is set.
- "Today" is the device's local date at call time (`LocalDate.now()`). It cannot be injected, so the Tips build due dates relative to `today = LocalDate.now()`, read once in Given.
- The test creates `textView` with `TextView(InstrumentationRegistry.getInstrumentation().targetContext)` and calls `setDueDateLabel` directly.

**Post-conditions**
- Success cases
  - An active task due in N days shows `"D-N"` and is visible. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"D-3"` and `textView.visibility` is `View.VISIBLE`
  - An active task overdue by N days shows `"D+N"` and is visible. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"D+3"` and `textView.visibility` is `View.VISIBLE`
  - An active task due today shows `"D-Day"` and is visible. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"D-Day"` and `textView.visibility` is `View.VISIBLE`
  - A completed task with a due date shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - An active task with no due date shows no text and is hidden. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `""` and `textView.visibility` is `View.GONE`
  - A `null` task shows no text and is hidden. `[Integration]`
    - Tip: Given a new `textView` / When `setDueDateLabel(textView, null)` is called / Then `textView.text` is `""` and `textView.visibility` is `View.GONE`
- Error cases
  - None. Every `TextView` with any `Task?` is valid input.
- Edge cases
  - Due-today boundary, one day overdue: shows `"D+1"`, which shows the device's today is used. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"D+1"` and `textView.visibility` is `View.VISIBLE`
  - Due-today boundary, due in one day: shows `"D-1"`. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"D-1"` and `textView.visibility` is `View.VISIBLE`
  - Empty/non-empty label boundary: a completed task with no due date, which is one flag away from the hidden case, shows `"CLEAR"` and is visible. `[Integration]`
    - Tip: Given a new `textView` and `Task(title = "Title", description = "Desc", isCompleted = true, dueDate = null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - Recycled row, labeled to unlabeled: a view still showing an earlier label is cleared and hidden. `[Integration]`
    - Tip: Given a `textView` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))` (shows `"D-3"`, visible) / When `setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = null))` is called / Then `textView.text` is `""` and `textView.visibility` is `View.GONE`
  - Recycled row, labeled to `null` task: a view still showing an earlier label is cleared and hidden. `[Integration]`
    - Tip: Given a `textView` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))` (shows `"D-3"`, visible) / When `setDueDateLabel(textView, null)` is called / Then `textView.text` is `""` and `textView.visibility` is `View.GONE`
  - Recycled row, unlabeled to labeled: a hidden view becomes visible again with the new label. `[Integration]`
    - Tip: Given a `textView` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = null))` (empty, `View.GONE`) / When `setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = today))` is called / Then `textView.text` is `"D-Day"` and `textView.visibility` is `View.VISIBLE`
  - Same row, task checked as completed: the label changes from `"D-3"` to `"CLEAR"` and stays visible. `[Integration]`
    - Tip: Given a `textView` already bound with `setDueDateLabel(textView, Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3), id = "id1"))` (shows `"D-3"`) / When `setDueDateLabel(textView, Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3), id = "id1"))` is called / Then `textView.text` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`

**Undefined behavior** (optional)
- A call that runs across local midnight may use either date as "today". The function reads `LocalDate.now()` itself, so a test whose Given and When straddle midnight can be off by one. This is rare enough to accept rather than inject a clock.
