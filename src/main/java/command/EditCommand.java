package command;

import common.Messages;
import exceptions.AtasException;
import seedu.atas.Parser;
import seedu.atas.TaskList;
import seedu.atas.Ui;
import tasks.Task;
import tasks.Assignment;
import tasks.Event;
import tasks.RepeatEvent;

import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//@@author jichngan
public class EditCommand extends Command {

    public static final String COMMAND_WORD = "edit";
    public static final String COMMAND_USAGE = "Edit Task: edit [TASK NUMBER]";

    //Regex for Assignment Command
    public static final Pattern ASSIGNMENT_PARAMETERS_FORMAT = Pattern.compile(
            "(?<taskType>(?i)"
                    + AssignmentCommand.COMMAND_WORD
                    + "\\b)"
                    + "\\s+n/\\s*(?<assignmentName>[^|/\\s]+[^|/]*)"
                    + "\\s+m/\\s*(?<moduleName>[^|/\\s]+[^|/]*)"
                    + "\\s+d/\\s*(?<dateTime>\\d{2}/\\d{2}/\\d{2}\\s+\\d{4})"
                    + "\\s+c/\\s*(?<comments>[^|/\\s]+[^|/]*)$"
    );

    //Regex for Event Command
    public static final Pattern EVENT_PARAMETERS_FORMAT = Pattern.compile(
            "(?<taskType>(?i)"
                    + EventCommand.COMMAND_WORD
                    + "\\b)"
                    + "\\s+n/\\s*(?<eventName>[^|/\\s]+[^|/]*)"
                    + "\\s+l/\\s*(?<location>[^|/\\s]+[^|/]*)"
                    + "\\s+d/\\s*(?<dateTime>\\d{2}/\\d{2}/\\d{2}\\s+\\d{4}\\s*-\\s*\\d{4})"
                    + "\\s+c/\\s*(?<comments>[^|/\\s]+[^|/]*)$"
    );

    protected int editIndex;

    /**
     * Default constructor for EditCommand class.
     * @param editIndex Integer of task index to be edited
     */
    public EditCommand(int editIndex) {
        this.editIndex = editIndex;
    }

    /**
     * Executes the edit command function.
     * Takes in a new input from the user and formats input.
     * Replaces task from the tasklist at index specified by user.
     * @param taskList TaskList object that handles adding Task
     * @param ui Ui object that interacts with user
     * @return CommandResult object based on result
     */
    @Override
    public CommandResult execute(TaskList taskList, Ui ui) {
        if (taskList.getListSize() == 0) {
            return new CommandResult(Messages.NO_TASKS_MSG);
        }

        if (editIndex + 1 > taskList.getListSize() || editIndex < 0) {
            return new CommandResult(String.format(Messages.INVALID_ID_ERROR,
                    taskList.getRangeOfValidIndex(taskList)));
        }

        ui.showToUser(Messages.EDIT_PROMPT);
        ui.showToUser(Messages.DIVIDER);
        String userInput = ui.getUserInput();
        String commandType = userInput.split("\\s+", 2)[0].trim().toLowerCase();
        try {
            switch (commandType) {
            case AssignmentCommand.COMMAND_WORD:
                Task editedAssignment = editAssignment(userInput, ui);
                if (taskList.isRepeatTask(taskList, editedAssignment)) {
                    return new CommandResult(Messages.SAME_TASK_ERROR);
                }

                taskList.editTask(editIndex, editedAssignment);
                return new CommandResult(String.format(Messages.EDIT_SUCCESS_MESSAGE, editedAssignment));
            case EventCommand.COMMAND_WORD:
                Event editedEvent = editEvent(userInput, ui);
                if (taskList.isRepeatTask(taskList, editedEvent)) {
                    return new CommandResult((Messages.SAME_TASK_ERROR));
                }
                //Check if Event to be edited is repeating event.
                if (taskList.getTask(editIndex) instanceof RepeatEvent) {
                    Task editedRepeatEvent = editRepeatEvent(editedEvent, (RepeatEvent) taskList.getTask(editIndex));
                    taskList.editTask(editIndex, editedRepeatEvent);
                    return new CommandResult(String.format(Messages.EDIT_SUCCESS_MESSAGE, editedRepeatEvent));
                } else if (taskList.getTask(editIndex) instanceof Event) {
                    taskList.editTask(editIndex, editedEvent);
                    return new CommandResult(String.format(Messages.EDIT_SUCCESS_MESSAGE, editedEvent));
                } else {
                    return new CommandResult(String.format(Messages.EDIT_SUCCESS_MESSAGE, editedEvent));
                }
            default:
                return new CommandResult(Messages.UNKNOWN_COMMAND_ERROR);
            }
        } catch (AtasException e) {
            return new CommandResult(e.toString());
        }
    }

    /**
     * Creates an assignment object by formatting the string supplied by user.
     * @param userInput String supplied by user
     * @param ui Formats output to display error messages to user
     * @return Assignment Object
     * @throws AtasException thrown when format of string supplied not recognised
     */
    public Assignment editAssignment(String userInput, Ui ui) throws AtasException {
        final Matcher matcher = ASSIGNMENT_PARAMETERS_FORMAT.matcher(userInput);
        if (!matcher.matches()) {
            throw new AtasException(String.format(Messages.INCORRECT_FORMAT_ERROR,
                    Parser.capitalize(AssignmentCommand.COMMAND_WORD), AssignmentCommand.COMMAND_USAGE));
        }

        LocalDateTime dateTime = null;

        try {
            dateTime = Parser.parseDate(matcher.group("dateTime"));
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            throw new AtasException(Messages.DATE_INCORRECT_OR_INVALID_ERROR);
        }



        String assignmentName = Parser.capitalize(matcher.group("assignmentName"));
        String moduleName = matcher.group("moduleName");
        String comments = Parser.capitalize(matcher.group("comments"));
        return new Assignment(assignmentName, moduleName, dateTime, comments);
    }

    /**
     * Creates an event object by formatting the string supplied by user.
     * @param userInput String supplied by user
     * @param ui Formats output to display error messages to user
     * @return Event object
     * @throws AtasException thrown when format of string supplied not recognised
     */
    public Event editEvent(String userInput, Ui ui) throws AtasException {
        final Matcher matcher = EVENT_PARAMETERS_FORMAT.matcher(userInput);
        if (!matcher.matches()) {
            throw new AtasException(String.format(Messages.INCORRECT_FORMAT_ERROR,
                    Parser.capitalize(EventCommand.COMMAND_WORD), EventCommand.COMMAND_USAGE));
        }

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;
        try {
            String startEndDateTime = matcher.group("dateTime");
            String[] dateTimeTokens = startEndDateTime.split("\\s+", 2);
            String[] timeTokens = dateTimeTokens[1].split("-", 2);
            startDateTime = Parser.parseDate(dateTimeTokens[0] + " " + timeTokens[0].trim());
            endDateTime = Parser.parseDate(dateTimeTokens[0] + " " + timeTokens[1].trim());
        } catch (DateTimeParseException | IndexOutOfBoundsException e) {
            throw new AtasException(Messages.START_END_DATE_INCORRECT_OR_INVALID_ERROR);
        }

        if (!endDateTime.isAfter(startDateTime)) {
            throw new AtasException(Messages.INCORRECT_START_END_TIME_ERROR);
        }

        String eventName = Parser.capitalize(matcher.group("eventName"));
        String location = matcher.group("location");
        String comments = Parser.capitalize(matcher.group("comments"));

        return new Event(eventName, location, startDateTime, endDateTime, comments);
    }

    /**
     * Creates a RepeatEvent object by taking Event object supplied and Repeated Event.
     * @param editedEvent Event Object that is created
     * @param repeatedEvent RepeatedEvent Object that was on the list
     * @return RepeatedEvent object
     */
    public RepeatEvent editRepeatEvent(Event editedEvent, RepeatEvent repeatedEvent) {
        int numOfPeriod = repeatedEvent.getNumOfPeriod();
        String typeOfPeriod = repeatedEvent.getTypeOfPeriod();
        LocalDateTime originalDateAndTime = repeatedEvent.getOriginalDateAndTime();
        int periodCounter = repeatedEvent.getPeriodCounter();

        RepeatEvent editedRepeatEvent = new RepeatEvent(editedEvent.getName(), editedEvent.getLocation(),
                editedEvent.getDateAndTime(), editedEvent.getEndDateAndTime(), editedEvent.getComments(),
                numOfPeriod, typeOfPeriod, originalDateAndTime, periodCounter);

        return editedRepeatEvent;
    }
}
