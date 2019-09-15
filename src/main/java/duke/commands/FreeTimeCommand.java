package duke.commands;

import duke.DukeException;
import duke.Storage;
import duke.TaskList;
import duke.Ui;
import duke.tasks.Task;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class FreeTimeCommand extends Command {
    int lowerTimeBound = 800;
    int upperTimeBound = 1700;

    public FreeTimeCommand(String str) {
        type = CmdType.FREETIME;
        input = str;
    }

    @Override
    public void execute(TaskList tasks, Ui ui, Storage storage) throws DukeException {
        if (input.length() == 8) {
            throw new DukeException("     ☹ OOPS!!! The description of a freetime cannot be empty.");
        }
        input = input.substring(9);
        try {
            int diff = Integer.parseInt(input);
            if (diff > 9) {
                throw new DukeException("Hour can only be <= 9 [0800 to 1700]");
            }
            diff *= 100;
            Date currDate = new Date();
            Calendar calendar = GregorianCalendar.getInstance();
            calendar.setTime(currDate);
            int currDay = calendar.get(Calendar.DAY_OF_MONTH);
            int currTime = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);;
            ui.showMessage("Current Date: " + currDate.toString());

            ArrayList<Task> eventList = new ArrayList<>();
            for (Task task : tasks.getData()) {
                if (task.getTaskType() == Task.TaskType.EVENT) {
                    eventList.add(task);
                }
            }

            for (Task task : eventList) {
                Date compDate = task.getDateTime();
                if (compDate == null) {
                    continue;
                }
                calendar.setTime(compDate);
                int compInt = calendar.get(Calendar.HOUR_OF_DAY) * 100 + calendar.get(Calendar.MINUTE);
                if (compInt - diff >= lowerTimeBound && currTime <= compInt) {
                    //ui.showMessage("A");
                    calendar.set(Calendar.HOUR_OF_DAY, (compInt - diff) / 100);
                    calendar.set(Calendar.MINUTE, (compInt - diff) % 100);
                } else if (compInt + diff <= upperTimeBound && currTime + diff <= upperTimeBound) {
                    //ui.showMessage("B");
                    calendar.set(Calendar.HOUR_OF_DAY, (compInt + diff) / 100);
                    calendar.set(Calendar.MINUTE, (compInt + diff) % 100);
                } else {
                    //ui.showMessage("C");
                    currDay++;
                    currTime = 800;
                    calendar.set(Calendar.DAY_OF_MONTH, currDay);
                    calendar.set(Calendar.HOUR_OF_DAY, currTime / 100);
                    calendar.set(Calendar.MINUTE, currTime % 100);
                    currDate = calendar.getTime();
                }
            }
            ui.showMessage("Next Available: " + currDate.toString());
        } catch (NumberFormatException e) {
            throw new DukeException("Not a valid hour!");
        }
    }
}
