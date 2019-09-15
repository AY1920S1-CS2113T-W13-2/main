package duke.tasks;

import duke.DukeException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import com.joestelmach.natty.DateGroup;

public class RecurringTask extends ToDo{
    Date date;
    Frequency frequency;
    String freq;

    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

    public RecurringTask(String description, Date startDateTime, String frequency) {
        super(description);

        this.date = startDateTime;

        try {
            setFrequency(frequency);
        } catch (DukeException e) {
            e.getMessage();
        }
    }

    private void setFrequency (String freq) throws DukeException {
        switch (freq) {
            case "daily":
                this.frequency = Frequency.DAILY;
                this.freq = "DAILY";
                break;
            case "weekly":
                this.frequency = Frequency.WEEKLY;
                this.freq = "WEEKLY";
                break;
            case "monthly":
                this.frequency = Frequency.MONTHLY;
                this.freq = "MONTHLY";
                break;
            default:
                throw new DukeException("Please enter a frequency: daily, weekly or monthly");
        }
    }

    @Override
    public String toString() {
        return ("[]" + description);
    }

    @Override
    public String getDateTime() {
        return null;
    }

    @Override
    public String getExtra() {
        return null;
    }
}
