package javacake;

import javacake.commands.Command;
import javacake.commands.ExitCommand;
import javacake.commands.ListCommand;
import javacake.commands.BackCommand;
import javacake.commands.GoToCommand;
import javacake.tasks.Task;
import javacake.tasks.ToDo;
import javacake.tasks.Deadline;
import javacake.tasks.RecurringTask;
import com.joestelmach.natty.DateGroup;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Parser {

    public enum TaskState {
        NOT_DONE, DONE
    }

    /**
     * Allows the user input to be parsed before running 'execute'.
     * @param input String inputted by user, which needs to be parsed
     *              to identify the intent
     * @return a subclass of the Command Class along
     *         with their respective intent
     * @throws DukeException Shows error when unknown command is inputted
     */
    public static Command parse(String input) throws DukeException {
        if (input.equals("exit")) {
            return new ExitCommand();
        } else if (input.equals("list")) {
            return new ListCommand();
        } else if (input.equals("back")) {
            return new BackCommand();
        } else if (input.length() > 4 && input.substring(0,4).equals("goto")) {
            return new GoToCommand(input);
        } else {
            throw new DukeException("☹ OOPS!!! I'm sorry, but I don't know what that means :-( [Unknown COMMAND TYPE]");
        }
    }

    private static String getString(ArrayList<Task> data, TaskState state, Task tempTask) {
        StringBuilder stringBuilder = new StringBuilder();
        if (state == TaskState.DONE) {
            tempTask.markAsDone();
        }
        data.add(tempTask);
        stringBuilder.append("Got it. I've added this task: ").append("\n   ");
        stringBuilder.append(tempTask.getFullString()).append("\n");
        stringBuilder.append("Now you have ").append(data.size()).append(" tasks in the list.");
        return stringBuilder.toString();
    }

    /**
     * Creates a new 'toBeDone' task, before adding it to current list,
     * then returning the output by Duke.
     * @param data ArrayList of Tasks that's currently being stored
     * @param input Command input by user
     * @param state The type of output needed:
     *              0 : Needs to return a string
     *              1 : Returns null string with unchecked task
     *              2 : Returns null string with checked task
     * @return String which highlights what Duke processed
     */
    public static String runTodo(ArrayList<Task> data, String input, TaskState state) {
        input = input.substring(5);
        Task tempTask = new ToDo(input);
        return getString(data, state, tempTask);
    }

    /**
     * Creates a new 'Deadline' task, before adding it to current list,
     * then returning the output by Duke.
     * @param data ArrayList of Tasks that's currently being stored
     * @param input Command input by user
     * @param state The type of output needed:
     *              0 : Needs to return a string
     *              1 : Returns null string with unchecked task
     *              2 : Returns null string with checked task
     * @return String which highlights what Duke processed
     */


    public static String runDeadline(ArrayList<Task> data, String input, TaskState state) throws DukeException {
        input = input.substring(9);
        int startOfBy = input.indexOf("/");
        if (startOfBy <= 0) {
            throw new DukeException("No task description\nPlease input 'deadline TASK /by TASK_DATE'");
        }
        if (input.charAt(startOfBy - 1) != ' ') {
            throw new DukeException("Please leave space!\nPlease input 'deadline TASK /by TASK_DATE'");
        }
        String tt1 = input.substring(0, startOfBy - 1);
        if (startOfBy + 4 >= input.length()) {
            throw new DukeException("No date parameter!\nPlease input 'deadline TASK /by TASK_DATE'");
        }
        String tt2 = input.substring(startOfBy + 4);
        Task tempTask = new Deadline(tt1, tt2);
        return getString(data, state, tempTask);
    }

    /**
     * Method to run recurring tasks.
     * @param data ArrayList of Tasks that's currently being stored
     * @param input Command input by user
     * @param state The type of output needed:
     *              0 : Needs to return a string
     *              1 : Returns null string with unchecked task
     *              2 : Returns null string with checked task
     * @param freq daily, weekly or monthly
     * @return String which highlights what Duke processed
     * @throws DukeException Shows error when cannot parse date
     */
    public static String runRecurring(ArrayList<Task> data, String input,
                                      TaskState state, String freq) throws DukeException {
        input = input.substring(5).trim();
        String tt1;
        String tt2;
        int token;
        token = input.indexOf("/");
        tt1 = input.substring(0, token - 1);
        if (freq.equals("daily")) {
            tt2 = input.substring(token + 7);
        } else if (freq.equals("weekly")) {
            tt2 = input.substring(token + 8);
        } else {
            tt2 = input.substring(token + 9);
        }

        // parse date here
        Date startDate = parseDate(tt2);
        Task tempTask = new RecurringTask(tt1, startDate, freq);
        return getString(data, state, tempTask);
    }

    /**
     * Method to return parsed Date.
     * @param tt2 String to be parsed into Date
     * @return Date parsed from the string
     * @throws DukeException If date cannot be parsed
     */
    public static Date parseDate(String tt2) throws DukeException {
        try {
            com.joestelmach.natty.Parser parser = new com.joestelmach.natty.Parser();
            List<DateGroup> groups = parser.parse(tt2);
            return groups.get(0).getDates().get(0);
        } catch (Exception e) {
            throw new DukeException("   Date cannot be parsed: " + tt2);
        }
    }
}
