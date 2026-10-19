# Spec: `TasksListBindings.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `@BindingAdapter("app:dueDateLabel") fun setDueDateLabel(textView: TextView, task: Task?)`

**Intent**: Show the D-day label of `task` in its task-list row. An active task due in N days shows `D-N`, one due today shows `D-Day`, and one overdue by N days shows `D+N`. An active task with no due date shows no label. A completed task shows `CLEAR`, with or without a due date. "Today" is the device's current date. When the user checks or unchecks a task, the row is bound again with the updated task, and the label must change to match. "No label" means the user sees no D-day text in the row. Whether the view is hidden with `GONE` or `INVISIBLE`, or just left empty, is up to the implementation.

**Pre-conditions**
- `textView` is any `TextView`. It may still show the text and visibility from an earlier call, for example when RecyclerView reuses the row for another task or the same task is bound again after a check or uncheck.
- `task` is a `Task` or `null`. Data binding passes `null` before the row's `task` variable is set.
- "Today" is `LocalDate.now()` on the device at call time and cannot be injected. The Tips therefore set due dates relative to `today = LocalDate.now()`, read once in Given. For example, on 2026-10-19 `today.plusDays(3)` is 2026-10-22.
- In the Tips, `targetContext` is `InstrumentationRegistry.getInstrumentation().targetContext`.

**Post-conditions**
- Success cases
  - An active task due in N days shows `"D-N"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"D-3"` and `textView.visibility` is `View.VISIBLE`
  - An active task overdue by N days shows `"D+N"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"D+3"` and `textView.visibility` is `View.VISIBLE`
  - An active task due today shows `"D-Day"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"D-Day"` and `textView.visibility` is `View.VISIBLE`
  - A completed task with a due date shows `"CLEAR"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - A completed task with no due date shows `"CLEAR"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - An active task with no due date shows no label. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = null)` / When `setDueDateLabel(textView, task)` is called / Then `textView.visibility != View.VISIBLE || textView.text.toString() == ""` is `true`
  - A `null` task shows no label. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` / When `setDueDateLabel(textView, null)` is called / Then `textView.visibility != View.VISIBLE || textView.text.toString() == ""` is `true`
- Error cases
  - None. Any `TextView` with any `Task?` is valid input.
- Edge cases
  - Due-today boundary, one day overdue: shows `"D+1"`. This checks that the device's today is used. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.minusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"D+1"` and `textView.visibility` is `View.VISIBLE`
  - Due-today boundary, due in one day: shows `"D-1"`. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` and `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(1))` / When `setDueDateLabel(textView, task)` is called / Then `textView.text.toString()` is `"D-1"` and `textView.visibility` is `View.VISIBLE`
  - Check a task with a due date: the label changes from `"D-3"` to `"CLEAR"`. `[Integration]`
    - Tip: Given `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = today.plusDays(3))` and `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, task)` (shows `"D-3"`) / When `setDueDateLabel(textView, task.copy(isCompleted = true))` is called / Then `textView.text.toString()` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - Uncheck a task with a due date: the label changes from `"CLEAR"` back to `"D-3"`. `[Integration]`
    - Tip: Given `task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today.plusDays(3))` and `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, task)` (shows `"CLEAR"`) / When `setDueDateLabel(textView, task.copy(isCompleted = false))` is called / Then `textView.text.toString()` is `"D-3"` and `textView.visibility` is `View.VISIBLE`
  - Check a task with no due date: the row goes from no label to `"CLEAR"`. `[Integration]`
    - Tip: Given `task = Task(title = "Title", description = "Desc", isCompleted = false, dueDate = null)` and `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, task)` (shows no label) / When `setDueDateLabel(textView, task.copy(isCompleted = true))` is called / Then `textView.text.toString()` is `"CLEAR"` and `textView.visibility` is `View.VISIBLE`
  - Uncheck a task with no due date: `"CLEAR"` goes away and the row shows no label. `[Integration]`
    - Tip: Given `task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = null)` and `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, task)` (shows `"CLEAR"`) / When `setDueDateLabel(textView, task.copy(isCompleted = false))` is called / Then `textView.visibility != View.VISIBLE || textView.text.toString() == ""` is `true`
  - Uncheck a task due today: the label changes from `"CLEAR"` to `"D-Day"`. `[Integration]`
    - Tip: Given `task = Task(title = "Title", description = "Desc", isCompleted = true, dueDate = today)` and `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, task)` (shows `"CLEAR"`) / When `setDueDateLabel(textView, task.copy(isCompleted = false))` is called / Then `textView.text.toString()` is `"D-Day"` and `textView.visibility` is `View.VISIBLE`
  - Reused row, labeled to unlabeled: a row that showed another task's label shows no label. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))` (shows `"D-3"`) / When `setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = null))` is called / Then `textView.visibility != View.VISIBLE || textView.text.toString() == ""` is `true`
  - Reused row, labeled to `null` task: a row that showed a label shows no label. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = today.plusDays(3)))` (shows `"D-3"`) / When `setDueDateLabel(textView, null)` is called / Then `textView.visibility != View.VISIBLE || textView.text.toString() == ""` is `true`
  - Reused row, unlabeled to labeled: a row that showed no label shows the new task's label. `[Integration]`
    - Tip: Given `textView = TextView(targetContext)` already bound with `setDueDateLabel(textView, Task(title = "Old", description = "Desc", isCompleted = false, dueDate = null))` (shows no label) / When `setDueDateLabel(textView, Task(title = "New", description = "Desc", isCompleted = false, dueDate = today))` is called / Then `textView.text.toString()` is `"D-Day"` and `textView.visibility` is `View.VISIBLE`

**Undefined behavior** (optional)
- A call that runs across local midnight may use either date as "today". The function reads `LocalDate.now()` itself, so a test whose Given and When fall on different sides of midnight can be off by one day. This is rare enough to accept rather than change the signature to inject a clock.
