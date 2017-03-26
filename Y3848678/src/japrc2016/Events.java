package japrc2016;

public class Events {

	/*
	 *All the study blocks in the plan are converted into string formats 
	 *for the Tableview format in the FX GUI.
	 */
    private String eventName;
    private String eventTime;

    public Events(String eventName, String eventTime) {
        this.eventName = eventName;
        this.eventTime = eventTime;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }
}
