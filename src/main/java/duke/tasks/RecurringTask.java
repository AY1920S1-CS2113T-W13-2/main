package duke.tasks;

import com.joestelmach.natty.DateGroup;
import duke.DukeException;

import java.util.Date;

public class RecurringTask extends ToDo{
    private Date date;
    protected Frequency frequency;
    protected String freq;

    public enum Frequency {
        DAILY, WEEKLY, MONTHLY
    }

    public RecurringTask(String description, Date startDateTime, String frequency) {
        super(description);

        this.date = startDateTime;

        try {
            setFrequency(frequency);
        } catch (DukeException e) {
            //e.getMessage();
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
        char freqLetter;
        if (this.frequency == Frequency.DAILY) {
            freqLetter = 'd';
        } else if (this.frequency == Frequency.WEEKLY) {
            freqLetter = 'w';
        } else {
            freqLetter = 'm';
        }
        return ("[R][" + freqLetter + "]" + "[" + super.getStatusIcon() + "]" + description);
    }

    @Override
    public Date getDateTime() {
        return this.date;
    }

    @Override
    public String getExtra() {
        return freq;
    }

    public Frequency getFrequency() {
        return this.frequency;
    }
}
