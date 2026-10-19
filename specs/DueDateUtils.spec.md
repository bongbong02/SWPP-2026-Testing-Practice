# Spec: `DueDateUtils.kt`

> Post-conditions are the test oracle: write the expected behavior from intent, not from the code.
> Tag each post-condition [Unit] if its test runs on the JVM; otherwise tag it [Integration].
> End each valid case with `Tested by <test>` if an existing test checks it, or `Tip:` describing the test to write.

## `internal fun dueDateLabel(dueDate: LocalDate?, today: LocalDate, isCompleted: Boolean): String`

**Intent**: Build the D-day label that the task list shows for one task. Each task has an optional due date. An active task due in N days gets `D-N`, an active task due today gets `D-Day`, and an active task overdue by N days gets `D+N`. An active task with no due date gets no label (`""`). A completed task gets `CLEAR`, with or without a due date. N counts calendar days from `today` to `dueDate`, so a due date one month away is about 30 days away, not 0 days.

**Pre-conditions**
- `today` is the caller's current local date. The function never reads the clock itself.
- `dueDate` is any `LocalDate` or `null` (no due date). It may be before, on, or after `today`.
- `isCompleted` is the task's completion state.

**Post-conditions**
- Success cases
  - A completed task with a due date returns `"CLEAR"`, whatever the due date is. `[Unit]`
    - Tip: Given `dueDate = 2026-10-22`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`
  - A completed task with no due date returns `"CLEAR"`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`
  - An active task with no due date returns `""`. `[Unit]`
    - Tip: Given `dueDate = null`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `""`
  - An active task due today returns `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"`
  - An active task due N days after today returns `"D-"` followed by N in decimal, with no spaces and no zero padding. `[Unit]`
    - Tip: Given `dueDate = 2026-10-22`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-3"`
  - An active task whose due date was N days before today returns `"D+"` followed by N in decimal, with no spaces and no zero padding. `[Unit]`
    - Tip: Given `dueDate = 2026-10-16`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+3"`
- Error cases
  - None. Every combination of `dueDate`, `today` and `isCompleted` is valid input.
- Edge cases
  - Due-today boundary, one day overdue: returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`
  - Due-today boundary, on the boundary: returns `"D-Day"`, not `"D-0"` or `"D+0"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-Day"`
  - Due-today boundary, due in one day: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Completed boundary: a completed task due today returns `"CLEAR"`, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-19`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`
  - Completed boundary: a completed task that is overdue returns `"CLEAR"`, not `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-18`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`
  - Completed boundary: a completed task due tomorrow returns `"CLEAR"`, not `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-20`, `today = 2026-10-19`, `isCompleted = true` / When `dueDateLabel` is called / Then it returns `"CLEAR"`
  - Digit boundary, one-digit N: returns `"D-9"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-28`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-9"`
  - Digit boundary, two-digit N: returns `"D-10"`, not `"D-010"` or `"D - 10"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-29`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-10"`
  - Digit boundary, two-digit N overdue: returns `"D+10"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-09`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+10"`
  - Month end, due the first day of the next month: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-01`, `today = 2026-10-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Month end, overdue from the last day of the previous month: returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-10-31`, `today = 2026-11-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`
  - Year end, due the first day of the next year: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2027-01-01`, `today = 2026-12-31`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Year end, overdue from the last day of the previous year: returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-12-31`, `today = 2027-01-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`
  - Leap year, due on the leap day: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2028-02-29`, `today = 2028-02-28`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Leap year, due the day after the leap day: the leap day is counted, so it returns `"D-2"`. `[Unit]`
    - Tip: Given `dueDate = 2028-03-01`, `today = 2028-02-28`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-2"`
  - Leap year, today is the leap day: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2028-03-01`, `today = 2028-02-29`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Leap year, overdue from the leap day: returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2028-02-29`, `today = 2028-03-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`
  - Leap year, overdue from the day before the leap day: the leap day is counted, so it returns `"D+2"`. `[Unit]`
    - Tip: Given `dueDate = 2028-02-28`, `today = 2028-03-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+2"`
  - Non-leap year, February 28 to March 1: returns `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2027-03-01`, `today = 2027-02-28`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-1"`
  - Non-leap year, overdue from February 28: returns `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2027-02-28`, `today = 2027-03-01`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+1"`
  - One-month span, one day short of a calendar month: returns `"D-30"`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-30"`
  - One-month span, exactly one calendar month: counts total days, so it returns `"D-31"`, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-31"`
  - One-month span, one day past a calendar month: returns `"D-32"`, not `"D-1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-11-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-32"`
  - One month overdue, one day short of a calendar month: returns `"D+29"`. `[Unit]`
    - Tip: Given `dueDate = 2026-09-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+29"`
  - One month overdue, exactly one calendar month: returns `"D+30"`, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2026-09-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+30"`
  - One month overdue, one day past a calendar month: returns `"D+31"`, not `"D+1"`. `[Unit]`
    - Tip: Given `dueDate = 2026-09-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D+31"`
  - One-year span, one day short of a calendar year: returns `"D-364"`. `[Unit]`
    - Tip: Given `dueDate = 2027-10-18`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-364"`
  - One-year span, exactly one calendar year: returns `"D-365"`, not `"D-Day"`. `[Unit]`
    - Tip: Given `dueDate = 2027-10-19`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-365"`
  - One-year span, one day past a calendar year: returns `"D-366"`. `[Unit]`
    - Tip: Given `dueDate = 2027-10-20`, `today = 2026-10-19`, `isCompleted = false` / When `dueDateLabel` is called / Then it returns `"D-366"`
