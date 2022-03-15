package seedu.sherpass.task;

public class Task {
    protected String description;
    protected boolean isDone;
    protected String byDate;
    protected String doOnDate;


    /**
     * Creates a constructor for the parent class of tasks, 'Task'.
     * Accepts only task description
     *
     * @param description Description of task.
     */
    public Task(String description, String byDate, String doOnDate) {
        this.description = description;
        this.isDone = false;
        this.byDate = byDate;
        this.doOnDate = doOnDate;
    }

    /**
     * Returns task description.
     *
     * @return task description.
     */
    public String getDescription() {
        return description;
    }


    /**
     * Returns mark status of task.
     *
     * @return Returns true if task has been marked. False otherwise.
     */
    public boolean isDone() {
        return isDone;
    }

    /**
     * Returns String version of mark status.
     *
     * @return X if task has been marked. White space otherwise.
     */
    public String getStatusIcon() {
        return (isDone ? "X" : " "); // mark done task with X
    }


    /**
     * Marks task as done.
     */
    public void markAsDone() {
        isDone = true;
    }

    /**
     * Marks task as undone.
     */
    public void markAsUndone() {
        isDone = false;
    }

    /**
     * Returns task date. Parent class is created
     * as a template for child classes to perform its own
     * respective functions, i.e. each task date returned is
     * different for each task type.
     *
     * @return White space.
     */
    public String getByDate() {
        return byDate;
    }

    public String getDoOnDate() {
        return doOnDate;
    }

    /**
     * Returns a string version of the task content.
     * Content includes mark status, task type and description.
     * For ease of printing the task.
     *
     * @return Task content.
     */
    @Override
    public String toString() {
        if (this.byDate.equals("") && this.doOnDate.equals("")) {
            return "[" + this.getStatusIcon() + "] " + this.getDescription();
        }

        if (this.doOnDate.equals("")) {
            return "[" + this.getStatusIcon() + "] " + this.getDescription()
                    + " (by: " + this.byDate + ")";
        }

        return "[" + this.getStatusIcon() + "] " + this.getDescription()
            + " (by: " + this.byDate + ") (reminder on: " + this.doOnDate + ")";
    }


    public void setTaskDescription(String taskDescription) {
        this.description = taskDescription;
    }

    public void setByDate(String byDate) {
        this.byDate = byDate;
    }

    public void setDoOnDate(String doOnDate) {
        this.doOnDate = doOnDate;
    }
}
