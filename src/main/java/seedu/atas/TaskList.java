package seedu.atas;

import tasks.Task;
import tasks.Assignment;
import tasks.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.util.ArrayList;
import java.util.Comparator;

public class TaskList {

    private ArrayList<Task> tasks;

    /**
     * Default constructor for TaskList class.
     * Instantiate a new ArrayList object.
     */
    public TaskList() {
        this.tasks = new ArrayList<>();
    }

    /**
     * Getter for size of ArrayList.
     * @return ArrayList size
     */
    public int getListSize() {
        return tasks.size();
    }

    /**
     * Getter for ArrayList of tasks.
     * @return ArrayList of tasks
     */
    public ArrayList<Task> getTaskArray() {
        return this.tasks;
    }

    /**
     * Getter for the current Local Date.
     * Formats Local Date into "dd/MM/yyyy" format.
     * @return LocalDate object of the formatted current Date
     */
    public LocalDate getCurrentDate() {
        LocalDate currentDateObj = LocalDate.now();
        DateTimeFormatter formattedDateObj = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String currentDate = currentDateObj.format(formattedDateObj);
        LocalDate formattedCurrDate = LocalDate.parse(currentDate, formattedDateObj);
        return formattedCurrDate;
    }

    /**
     * Getter method for tasks depending of days from today.
     * @param days Integer representing number of days from today
     * @return ArrayList object containing all tasks from indicated days from today
     */
    public ArrayList<Task> getTasksByDays(int days) {
        assert days >= 0;
        ArrayList<Task> taskList = new ArrayList<>();
        LocalDate currDate = getCurrentDate();
        LocalDate daysIndicated = currDate.plusDays(days);
        for (Task task : tasks) {
            LocalDate taskDate = task.getDate();
            assert taskList.size() <= tasks.size();
            if (currDate.compareTo(taskDate) <= 0 && taskDate.compareTo(daysIndicated) <= 0) {
                taskList.add(task);
            }
        }
        return taskList;
    }

    /**
     * Getter method for tasks that are events and in the future.
     * @return ArrayList object containing all future events.
     */
    public ArrayList<Task> getUpcomingEventArray() {
        ArrayList<Task> eventList = new ArrayList<>();
        LocalDateTime currDateTime = LocalDateTime.now();
        for (Task task : tasks) {
            LocalDateTime taskDateTime = task.getDateAndTime();
            if (task instanceof Event && taskDateTime.compareTo(currDateTime) > 0) {
                eventList.add(task);
            }
        }
        return eventList;
    }

    /**
     * Getter method for tasks that are assignments and not marked done.
     * @return ArrayList object containing all incomplete assignments
     */
    public ArrayList<Task> getIncompleteAssignArray() {
        ArrayList<Task> assignList = new ArrayList<>();
        for (Task task : tasks) {
            if (task instanceof Assignment && !task.getIsDone()) {
                assignList.add(task);
            }
        }
        return assignList;
    }

    /**
     * Getter for all events tasks.
     * @return ArrayList object containing all events
     */
    public ArrayList<Task> getEventsArray() {
        ArrayList<Task> eventList = new ArrayList<>();
        for (Task task: tasks) {
            if (task instanceof Event) {
                eventList.add(task);
            }
        }
        return eventList;
    }

    /**
     * Getter method for Task with the provided index in TaskList.
     * @param index index of Task to return
     * @return Task object with corresponding index
     * @throws IndexOutOfBoundsException if index is invalid
     */
    public Task getTask(int index) throws IndexOutOfBoundsException {
        return this.tasks.get(index);
    }

    /**
     * Getter for all assignment tasks.
     * @return ArrayList object containing all assignments
     */
    public ArrayList<Task> getAssignmentsArray() {
        ArrayList<Task> assignmentList = new ArrayList<>();
        for (Task task: tasks) {
            if (task instanceof Assignment) {
                assignmentList.add(task);
            }
        }
        return assignmentList;
    }
    
    /**
     * Adds a task to TaskList.
     * @param task task object to be added
     */
    public void addTask(Task task) {
        tasks.add(task);
    }

    /**
     * Set the Task corresponding to index specified as done.
     * @param doneIndex index of Task to be marked done
     * @throws IndexOutOfBoundsException throws when index is out of range of size of current TaskList
     */
    public void markTaskAsDone(int doneIndex) throws IndexOutOfBoundsException {
        tasks.get(doneIndex).setDone();
        assert tasks.get(doneIndex).getIsDone() == true;
    }

    /**
     * Delete tasks according to the index specified by user.
     * @param deleteIndex index of task to be deleted
     * @throws IndexOutOfBoundsException throws when index is out of range of the size of current Tasklist
     */
    public void deleteTask(int deleteIndex) throws IndexOutOfBoundsException {
        int size = tasks.size();
        tasks.remove(deleteIndex);
        assert tasks.size() == size - 1;
    }

    /**
     * Edits task according to the index specified by user.
     * Edited task replaces the index of the old task.
     * @param editIndex Integer of index of task to be edited
     * @param editedTask Edited task object to be replaced in ArrayList
     * @throws IndexOutOfBoundsException Thrown when index is out of range of the current TaskList
     */
    public void editTask(int editIndex, Task editedTask) throws IndexOutOfBoundsException {
        tasks.set(editIndex, editedTask);
    }

    /**
     * Deletes all the tasks in the list.
     */
    public void clearList() {
        tasks.clear();
        assert tasks.size() == 0;
    }

    /**
     * Deletes the all tasks specified by doneIndex.
     * @param doneIndex ArrayList of indexes to be removed
     */
    public void deleteAllDoneTask(ArrayList<Integer> doneIndex) {
        doneIndex.sort(Comparator.reverseOrder());
        for (int index : doneIndex) {
            deleteTask(index);
        }
    }

    /**
     * Getter for tasks that falls within the provided time period.
     * @param startOfRange LocalDate representing start of time period
     * @param endOfRange LocalDate representing end of time period
     * @return ArrayList of tasks that falls withing time period
     */
    public ArrayList<Task> getTasksByRange(LocalDate startOfRange, LocalDate endOfRange) {
        ArrayList<Task> taskArrayList = new ArrayList<>();

        for (Task task : tasks) {
            LocalDate taskDate = task.getDate();
            assert taskArrayList.size() <= tasks.size();
            if (startOfRange.compareTo(taskDate) <= 0 && endOfRange.compareTo(taskDate) >= 0) {
                taskArrayList.add(task);
            }
        }
        return taskArrayList;
    }
}