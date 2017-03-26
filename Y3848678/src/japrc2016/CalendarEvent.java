package japrc2016;

import japrc2016.StudyPlannerInterface.CalendarEventType;

import java.io.Serializable;
import java.util.Calendar;


public class CalendarEvent implements CalendarEventInterface, Serializable {

    private String name;
    private Calendar startTime;
    private int duration;
    public CalendarEventType type;

    public CalendarEvent(String name, Calendar startTime, int duration, CalendarEventType type) {
        this.name = name;
        this.startTime = startTime;
        this.duration = duration;
        this.type = type;
    }

    public CalendarEventType getType() {
        return type;
    }

    public void setType(CalendarEventType type) {
        this.type = type;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setDuration(int duration)
    {
        this.duration = duration;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Calendar getStartTime() {
        return this.startTime;
    }

    @Override
    public int getDuration() {
        return this.duration;
    }
    /**
     *
     * @return
     */
    @Override
    public boolean isValidTopicTarget() {
        if (type.equals(CalendarEventType.ESSAY) || type.equals(CalendarEventType.EXAM)){
            return true;
        }
        return false;
    }
}
