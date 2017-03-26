package japrc2016;

import java.io.Serializable;

public class Topic implements TopicInterface, Serializable {

	private String subject;
	
	/**
	 * The duration of a topic is the cumulative amount of time that should be
	 * spent studying a topic. For example, a topic of duration 240 minutes
	 * would be completed by four study sessions of 60 minutes, eight of 30
	 * minutes, etc. These sessions could take place in a single day, or be
	 * completed over multiple days.
	 */
	private int duration;
	private CalendarEventInterface calendarTarget;

	public Topic(String name, int duration) {
		this.subject = name;
		this.duration = duration;
	}

	@Override
	public String getSubject() {
		return subject;
	}

	@Override
	public int getDuration() {
		return duration;
	}

	@Override
	public void setTargetEvent(CalendarEventInterface target) {
		if(target.isValidTopicTarget()) {
			this.calendarTarget = target;
		} else {
			throw new StudyPlannerException("You can only set target events for an exam or essay events");
		}
	}

	public void setDuration(int duration){
		this.duration = duration;
	}

	@Override
	public CalendarEventInterface getTargetEvent() {
		return calendarTarget;
	}

}
