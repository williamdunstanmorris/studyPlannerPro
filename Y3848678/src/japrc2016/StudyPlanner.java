package japrc2016;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class StudyPlanner implements StudyPlannerInterface {

    private ArrayList<TopicInterface> topics = new ArrayList<TopicInterface>();
    private ArrayList<StudyBlockInterface> plan = new ArrayList<StudyBlockInterface>();
    /**
     * Topics
     */
    private ArrayList<Topic> studyList = new ArrayList<>();
    private ArrayList<Topic> topicsToRemove = new ArrayList<>();
    /**
     * Events
     */
    private ArrayList<CalendarEventInterface> calendarEvents = new ArrayList<>();
    private ArrayList<CalendarEvent> calendarEventsToRemove = new ArrayList<>();
    private ArrayList<CalendarEvent> singleEvent = new ArrayList<>();

    private StudyPlannerGUI studyPlannerGUI;

    private GregorianCalendar timeNow = new GregorianCalendar();

    private GregorianCalendar defaultDailyStartTime = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 9, 0, 0);
    private GregorianCalendar defaultDailyEndTime = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 17, 0, 0);

    private int defaultStudyBlockDuration = 60;
    private int breakBlockDuration = 10;

    private boolean shorterStudyBlockBeforeEvent = false;
    private boolean eventClash;

    private boolean eventEntersDayWindow = false;

    private boolean thereWasAnEvent = false;

    private int minimumStudyBlockSize = 10;

    public StudyPlanner() {
    }

    public StudyPlanner(StudyPlannerGUI studyPlannerGUI){
        this.studyPlannerGUI = studyPlannerGUI;
    }

    public StudyPlannerGUI getStudyPlannerGUI() {
        return studyPlannerGUI;
    }

    @Override
    public void addTopic(String name, int duration) throws StudyPlannerException {
        String errorMessage = "Topic " + name + " already exists.";

        if (topics.isEmpty()) {
            topics.add(new Topic(name, duration));
        } else if (containsLocation(name)) {
            topics.add(new Topic(name, duration));
        } else {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog(errorMessage);
            } else {
                throw new StudyPlannerException(errorMessage);
            }
        }
    }

    public boolean containsLocation(String name) {
        for (TopicInterface t : getTopics()) {
            if (t.getSubject().equals(name)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public List<TopicInterface> getTopics() {
        return topics;
    }

    @Override
    public List<StudyBlockInterface> getStudyPlan() {
        return plan;
    }

    public Calendar getTimeNow() {
        return timeNow;
    }

    public void setTimeNow(Date time) {
    	timeNow.clear();
    	timeNow = (GregorianCalendar) Calendar.getInstance();
    	timeNow.setTime(time);
    }


    @Override
    public void generateStudyPlan() throws StudyPlannerException {
        String errorMessage = "Add some topics first before generating a plan.";
        if (topics.isEmpty()) {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog(errorMessage);
            } else {
                throw new StudyPlannerException(errorMessage);
            }
        } else {
            /**
             * To avoid Concurrentmodificationexception, I created separate the array lists. This way
             * we can still access the topic information in getTopics() after the study plan has been generated.
             *
             */
            for (TopicInterface t : topics) {
                studyList.add(new Topic(t.getSubject(), t.getDuration()));
                topicsToRemove.add(new Topic(t.getSubject(), t.getDuration()));
            }
            for (CalendarEventInterface c : calendarEvents) {
                calendarEventsToRemove.add((CalendarEvent) c);
            }
            /**
             * To begin with, we set the timeNow to a reference of the start time (i.e. a date)
             */
            setTimeNow(Calendar.getInstance().getTime());
            generateStudyPlanAlgorithm();
        }
    }


    /**
     * @param startStudy
     */
    @Override
    public void generateStudyPlan(Calendar startStudy) {

        String errorMessage = "Add some topics first before generating a plan.";
        if (topics.isEmpty()) {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog(errorMessage);
            } else {
                throw new StudyPlannerException(errorMessage);
            }
        } else {
            
            for (TopicInterface t : topics) {
                studyList.add(new Topic(t.getSubject(), t.getDuration()));
                topicsToRemove.add(new Topic(t.getSubject(), t.getDuration()));
            }
            for (CalendarEventInterface c : calendarEvents) {
                calendarEventsToRemove.add((CalendarEvent) c);
            }
            /**
             * To begin with, we set the timeNow to a reference of the start time (i.e. a date)
             */
//            getDailyStartStudyTime().clear();
//            getDailyEndStudyTime().clear();
//
//            this.defaultDailyStartTime = (GregorianCalendar)startStudy.clone();
//            defaultDailyStartTime.set(GregorianCalendar.HOUR_OF_DAY, 9);
//
//            this.defaultDailyEndTime = (GregorianCalendar)startStudy.clone();
//            defaultDailyEndTime.set(GregorianCalendar.HOUR_OF_DAY, 17);
            
//            startStudy = new GregorianCalendar();

            setTimeNow(startStudy.getTime());
            System.out.println("Plan generated from time: " + getTimeNow().getTime());
            generateStudyPlanAlgorithm();
        }

    }

    private void generateStudyPlanAlgorithm(){
            /*
             * We iterate over every duration of this topic, each time reducing it in size, until all topics have been studied.
             */
        boolean flag = true;

        while (flag) {

            for (Topic t : studyList) {

                if (topicsToRemove.isEmpty()) {
                    flag = false;
                    break;
                } else {
                    if (t.getDuration() > getBlockSize()) {

                        if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) > getBlockSize()) {
                            System.out.println("1.1: STUDY-BLOCK - STILL TIME IN THE DAY");
                            /**
                             * if there are any events available
                             */
                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("1.1.1");
                                checkForEvents_InDayWindow(t, getBlockSize());
                                if (!wasThereAnEvent()) {
                                    System.out.println("1.1.1.1");
                                    studyBlockEvent(t, getBlockSize());
                                }
                            } else {
                                System.out.println("1.1.2");
                                studyBlockEvent(t, getBlockSize());
                            }
                        } else if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) < minimumStudyBlockSize) {
                            System.out.println("1.2: STUDY BLOCK - TOPIC STARTS NEXT DAY");
                                    /*
                                    Check for events outside study day hours. e.g. between 17.00 (now) - 9.00am the next day.
                                     */
                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("1.2.1");

                                checkForEvents_OutsideDayWindow();

                                if (didEventEnterNewDayWindow()) {
                                    System.out.println("1.2.1.1");
                                    if (!calendarEventsToRemove.isEmpty()) {
                                        System.out.println("1.2.1.1.1");
                                        getDailyStartStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);
                                        getDailyEndStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);

                                        checkForEvents_InDayWindow(t, getBlockSize());

                                        if (!wasThereAnEvent()) {
                                            System.out.println("1.2.1.1.1.1");
                                            studyBlockEvent(t, getBlockSize());
                                        }
                                    } else {
                                        System.out.println("1.2.1.1.2");
                                        studyBlock_nextDay(t, getBlockSize());
                                    }
                                    setEventEntersDayWindow(false);
                                } else {
                                    System.out.println("1.2.1.2");
                                        /*
                                        Check for events like normal again, treating it as a new day.
                                         */
                                    if (!calendarEventsToRemove.isEmpty()) {
                                        System.out.println("1.2.1.2.1");

                                        checkForEvents_InDayWindow(t, getBlockSize());
                                        if (!wasThereAnEvent()) {
                                            System.out.println("1.2.1.2.1.1");
                                            studyBlock_nextDay(t, getBlockSize());
                                        }
                                    } else {
                                        System.out.println("1.2.1.2.2");
                                        studyBlock_nextDay(t, getBlockSize());
                                    }
                                }
                            } else {
                                System.out.println("1.2.2");
                                studyBlock_nextDay(t, getBlockSize());
                            }
                        } else {
                            System.out.println();
                            System.out.println("1.3: STUDY BLOCK - CUSTOM SIZE TO FILL THE REMAINDER OF THE DAY");

                            int minutesBetweenTimeNowAndDailyEndTime = (int) minutesBetween(getTimeNow(), getDailyEndStudyTime());

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("1.3.1");

                                checkForEvents_InDayWindow(t, minutesBetweenTimeNowAndDailyEndTime);

                                if (!wasThereAnEvent()) {
                                    System.out.println("1.3.1.1");
                                    studyBlockEvent_customSize(t, minutesBetweenTimeNowAndDailyEndTime);
                                }
                            } else {
                                System.out.println("1.3.2");
                                studyBlockEvent_customSize(t, minutesBetweenTimeNowAndDailyEndTime);
                            }

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("1.3.3");
                                checkForEvents_OutsideDayWindow();
                            }
                        }
                        /**
                         * This is a shorter study block that marks the end of a topic. This will be the last StudyBlock for the selected topic.
                         * If it does not meet the minimum size (10), it will fall under the next condition.
                         */
                    } else if (t.getDuration() >= minimumStudyBlockSize) {

                        if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) > t.getDuration()) {
                            System.out.println();
                            System.out.println("2.1: SHORTER STUDY BLOCK - STILL TIME IN THE DAY");

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("2.1.1");
                                checkForEvents_InDayWindow(t, t.getDuration());
                                if (!wasThereAnEvent()) {
                                    System.out.println("2.1.1.1");
                                    studyBlockEvent(t, t.getDuration());
                                }
                            } else {
                                System.out.println("2.1.1.2");
                                studyBlockEvent(t, t.getDuration());
                            }

                        } else if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) <= t.getDuration()) {
                            System.out.println();
                            System.out.println("2.2: SHORTER STUDY-BLOCK - NOT ENOUGH TIME LEFT IN DAY.");

                            int minutesBetweenTimeNowAndDailyEndTime = (int) minutesBetween(getTimeNow(), getDailyEndStudyTime());

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("2.2.1");

                                checkForEvents_InDayWindow(t, minutesBetweenTimeNowAndDailyEndTime);

                                //Check here whether having an event will even trigger a study block to be called.
                                if (!wasThereAnEvent()) {
                                    System.out.println("2.2.1.1");
                                    if(minutesFromCalendar(getTimeNow()) < minutesFromCalendar(getDailyEndStudyTime())) {
                                        studyBlockEvent_customSize(t, minutesBetweenTimeNowAndDailyEndTime);
                                    }
                                }
                            } else {
                                System.out.println("2.2.2");
                                studyBlockEvent_customSize(t, minutesBetweenTimeNowAndDailyEndTime);
                            }
                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("2.2.3");
                                checkForEvents_OutsideDayWindow();
                            }
                        } else {
                            System.out.println();
                            System.out.println("2.3: SHORTER STUDY BLOCK - BLOCK STARTS NEXT DAY");

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("2.3.1");
                                checkForEvents_OutsideDayWindow();

                                if (didEventEnterNewDayWindow()) {
                                    System.out.println("2.3.1.1");

                                    if (!calendarEventsToRemove.isEmpty()) {
                                        System.out.println("2.3.1.1.1");


                                        getDailyStartStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);
                                        getDailyEndStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);

                                        checkForEvents_InDayWindow(t, t.getDuration());

                                        if (!wasThereAnEvent()) {
                                            System.out.println("2.3.1.1.1.1");

                                            studyBlockEvent(t, t.getDuration());
                                        }
                                    } else {
                                        System.out.println("2.3.1.1.2");
                                        studyBlock_nextDay(t, t.getDuration());
                                    }
                                    setEventEntersDayWindow(false);
                                } else {
                                    System.out.println("2.3.1.2");

                                        /*
                                        Check for events like normal again, treating it as a new day.
                                         */
                                    if (!calendarEventsToRemove.isEmpty()) {
                                        System.out.println("2.3.1.2.1");

                                        checkForEvents_InDayWindow(t, t.getDuration());
                                        if (!wasThereAnEvent()) {
                                            System.out.println("2.3.1.2.1.1");

                                            studyBlock_nextDay(t, t.getDuration());
                                        }
                                    } else {
                                        System.out.println("2.3.1.2.2");

                                        studyBlock_nextDay(t, t.getDuration());
                                    }
                                }
                            } else {
                                System.out.println("2.3.2");
                                studyBlock_nextDay(t, getBlockSize());
                            }
                        }
                            /*
                             * On the rare occasion that we have a duration left that is between 1-9 minutes, we will
                             * enforce the last minimum study block for this topic here...
                             */
                    } else if ((t.getDuration() < minimumStudyBlockSize) && (t.getDuration() != 0)) {
                            /*
                             * ... by making the study block a minimum length of 10 minutes.
                             */
                        t.setDuration(minimumStudyBlockSize);
                        /**
                         * We don't need to compare the t.getDuration now - because we have changed it. Now we need to know if we have enough time
                         * in the day for a minimum study block.
                         */
                        if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) > minimumStudyBlockSize) {
                            System.out.println();
                            System.out.println("3.1: MINIMUM STUDY-BLOCK - STILL TIME IN DAY");

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("3.1.1");
                                checkForEvents_InDayWindow(t, t.getDuration());
                                if (!wasThereAnEvent()) {
                                    System.out.println("3.1.1.1");
                                    studyBlockEvent(t, t.getDuration());
                                }
                            } else {
                                System.out.println("3.1.1.2");
                                studyBlockEvent(t, t.getDuration());
                            }
                        } else {
                            System.out.println();
                            System.out.println("3.2: MINIMUM STUDY BLOCK - STARTS NEXT DAY ---");

                            if (!calendarEventsToRemove.isEmpty()) {
                                System.out.println("3.2.1");
                                checkForEvents_InDayWindow(t, t.getDuration());
                                if (!wasThereAnEvent()) {
                                    System.out.println("3.2.1.1");

                                    studyBlockEvent(t, t.getDuration());
                                }
                            } else {
                                System.out.println("3.2.2");

                                studyBlockEvent(t, t.getDuration());
                            }
                        }
                    } else if (t.getDuration() == 0) {
                        Iterator<Topic> it = topicsToRemove.iterator();
                        while (it.hasNext()) {
                            if (it.next().getSubject() == t.getSubject()) {
                                it.remove();
                                break;
                            }
                        }
                        if (topicsToRemove.isEmpty()) {
                            studyList.clear();
                            flag = false;
                            break;
                        }
                    }
                }
            }
        }
    }

    private void checkForEvents_InDayWindow(Topic t, int blockSize) {
        Calendar start = checkForEventCalendar();
        Calendar endCalendar = checkForEventCalendar(blockSize);
        setThereWasAnEvent(false);
        scanForEvents(start, endCalendar, t, blockSize);
    }

    private void checkForEvents_OutsideDayWindow() {
        Calendar start = checkForEventCalendar();
        Calendar endCalendar = checkForEventCalendar();
        endCalendar.setTime(getDailyStartStudyTime().getTime());
        endCalendar.add(GregorianCalendar.DAY_OF_MONTH, 1);
        setThereWasAnEvent(false);
        for (Calendar pointer = start; pointer.before(endCalendar); pointer.add(GregorianCalendar.MINUTE, 1)) {
            for (CalendarEventInterface calEv : calendarEvents) {

                if ((int) minutesFromCalendar(pointer) == (int) minutesFromCalendar(calEv.getStartTime())) {
//                    System.out.println("CFE: OUT.1.1");
                    printEvent(calEv);
                    arbitraryEvent((CalendarEvent) calEv);

                    Calendar eventEndDayWindowCheck = (Calendar) calEv.getStartTime().clone();
                    eventEndDayWindowCheck.add(GregorianCalendar.MINUTE, calEv.getDuration());

                    if ((int) minutesFromCalendar(eventEndDayWindowCheck) >= minutesFromCalendar(endCalendar)) {
//                        System.out.println("CFE: OUT.1.1.1");
                        setEventEntersDayWindow(true);
                    }
                }
            }
        }
    }
    
    private boolean checkForEvents_InBreak(Calendar start, int amountOfTime) {
      start = checkForEventCalendar();
      Calendar endCalendar = checkForEventCalendar(amountOfTime + getBreakLength());
      for (Calendar pointer = start; pointer.before(endCalendar); pointer.add(GregorianCalendar.MINUTE, 1)) {
          for (CalendarEventInterface calEv : calendarEvents) {
              if ((int) minutesFromCalendar(pointer) == (int) minutesFromCalendar(calEv.getStartTime())) {
            	  return true;
              }
          }
      }
      return false;
  }

    private Calendar checkForEventCalendar() {
        return (Calendar) getTimeNow().clone();
    }

    private Calendar checkForEventCalendar(int amountAdded) {
        Calendar calendar = (Calendar) getTimeNow().clone();
        calendar.add(GregorianCalendar.MINUTE, amountAdded);
        return calendar;
    }

    private void scanForEvents(Calendar start, Calendar endCalendar, Topic t, int blockSize) {
        for (Calendar pointer = start; pointer.before(endCalendar); pointer.add(GregorianCalendar.MINUTE, 1)) {
            for (CalendarEventInterface calEv : calendarEvents) {
                if ((int) minutesFromCalendar(pointer) == (int) minutesFromCalendar(calEv.getStartTime())) {
                    System.out.println("5.1");
                	/*
                    Check if you are in the day window itself. Out of day window scans also use this method, so we must not
                    add any study blocks outside the daily study times.
                     */
                    setThereWasAnEvent(true);
                    System.out.println(getTimeNow().getTime());

                    if ((int) minutesBetween(getTimeNow(), calEv.getStartTime()) >= minimumStudyBlockSize) {
                        System.out.println("5.1.1");
                    	/*
                         * Schedule a study block to occur before the event and find out the duration of the studyblock
                         */
                        int shorterStudyBlockBeforeEventDuration = (int) minutesBetween(getTimeNow(), calEv.getStartTime());
                        setShorterStudyBlockBeforeEvent(true);
                        studyBlockEvent(t, shorterStudyBlockBeforeEventDuration);
                        setShorterStudyBlockBeforeEvent(false);
                    }
                    printEvent(calEv);
                    arbitraryEvent((CalendarEvent) calEv);
                    /*
                    Add a single event to an array list for reference to studyBlockAfterEvent().
                     */
                    if((minutesFromCalendar(calEv.getStartTime()) + calEv.getDuration()) < minutesFromCalendar(getDailyEndStudyTime())) {
                        singleEvent.add((CalendarEvent) calEv);
                    }
                    calendarEventsToRemove.remove(calEv);
                }
            }
        }
        start.add(GregorianCalendar.MINUTE, -blockSize);
        if (wasThereAnEvent()) {
            studyBlockAfterEvent(start, t, blockSize);
        }
    }

    private void studyBlockAfterEvent(Calendar start, Topic t, int blockSize) {
        for (CalendarEvent calEv : singleEvent) {
            if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) > getBlockSize()) {

                if (shorterStudyBlockBeforeEvent) {
                    int minutesBetweenStartAndArbitraryEvent = (int) minutesBetween(start, calEv.getStartTime());
                    studyBlockEvent(t, getBlockSize() - minutesBetweenStartAndArbitraryEvent);
                } else {

                    studyBlockEvent(t, getBlockSize());
                }
            } else if ((int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) < minimumStudyBlockSize) {

                if (shorterStudyBlockBeforeEvent) {

                    int minutesBetweenStartAndArbitraryEvent = (int) minutesBetween(start, calEv.getStartTime());
                    studyBlock_nextDay(t, getBlockSize() - minutesBetweenStartAndArbitraryEvent);
                } else {

                    studyBlock_nextDay(t, getBlockSize());
                }
            } else {
                if (shorterStudyBlockBeforeEvent) {

                    int minutesBetweenStartAndArbitraryEvent = (int) minutesBetween(start, calEv.getStartTime());
                    studyBlockEvent_customSize(t, (int) minutesBetween(getTimeNow(), getDailyEndStudyTime()) - minutesBetweenStartAndArbitraryEvent);
                } else {
                    studyBlockEvent_customSize(t, (int) minutesBetween(getTimeNow(), getDailyEndStudyTime()));
                }
            }
            singleEvent.remove(calEv);
            break;
        }
    }

    /**
     * Add event to the plan and move the time forward.
     * @param calEv
     */
    private void arbitraryEvent(CalendarEvent calEv) {
        Calendar newPlanner = (Calendar) calEv.getStartTime().clone();
        addToPlan(calEv.getName(), (GregorianCalendar) newPlanner, calEv.getDuration());
        Calendar endCalendar = (Calendar)calEv.getStartTime().clone();
        endCalendar.add(GregorianCalendar.MINUTE, calEv.getDuration());
        setTimeNow(endCalendar.getTime());
    }
    
    public boolean didEventEnterNewDayWindow() {
        return eventEntersDayWindow;
    }

    public void setEventEntersDayWindow(boolean eventEntersDayWindow) {
        this.eventEntersDayWindow = eventEntersDayWindow;
    }

    public boolean isShorterStudyBlockBeforeEvent() {
        return shorterStudyBlockBeforeEvent;
    }

    public void setShorterStudyBlockBeforeEvent(boolean shorterStudyBlockBeforeEvent) {
        this.shorterStudyBlockBeforeEvent = shorterStudyBlockBeforeEvent;
    }

    public boolean wasThereAnEvent() {
        return thereWasAnEvent;
    }

    public void setThereWasAnEvent(boolean thereWasAnEvent) {
        this.thereWasAnEvent = thereWasAnEvent;
    }
    
   
    /**
     * Add topic to the plan and move the time forward.
     * @param t
     * @param blockSize
     */
    private void addToPlan(Topic t, int blockSize) {
        GregorianCalendar planCalendar = (GregorianCalendar) getTimeNow().clone();
        plan.add(new StudyBlock(t.getSubject(), planCalendar, blockSize));
        Calendar addedCalendar = (Calendar)planCalendar.clone();
        addedCalendar.add(GregorianCalendar.MINUTE, blockSize);
        setTimeNow(addedCalendar.getTime());
    }

    private void addToPlan(String subject, GregorianCalendar calendar, int blockSize) {
        plan.add(new StudyBlock(subject, calendar, blockSize));
    }

    /**
     * Method to calculate the new amount. If the topic is 0, then it is removed from the
     * array topicToRemove, which is guiding the algorithm.
     * @param t
     * @param setAmount
     */
    private void removeOrSetNewTopicDuration(Topic t, int setAmount) {
        t.setDuration(setAmount);
        /**
         * If the topic duration equals 0, remove it
         */
        if (t.getDuration() == 0) {
            Iterator<Topic> it = topicsToRemove.iterator();
            while (it.hasNext()) {
                if (it.next().getSubject() == t.getSubject()) {
//                    System.out.println(t.getSubject() + " removed.");
                    it.remove();
                    break;
                }
            }
        }
        printEvent_TWO(setAmount);
    }

    /**
     * Move the start time and end time a day forward.
     */
    private void nextDay() {
        getDailyStartStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);
        System.out.println("Next Day Start Time: " + getDailyStartStudyTime().getTime());
        getDailyEndStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);
        System.out.println("Next Day Start Time: " + getDailyEndStudyTime().getTime());
        setTimeNow(getDailyStartStudyTime().getTime());
    }

    /*
     *
     *
     * Study Blocks with time left over after.
     *
     *
     */

    /**
     * @param topic
     * @param blockSize
     */
    private void studyBlockEvent(Topic topic, int blockSize) {
        printEvent_ONE(getTimeNow().getTime(), topic.getSubject());
        addToPlan(topic, blockSize);
        printEventDurations(timeNow.getTime(), topic.getDuration());
        removeOrSetNewTopicDuration(topic, topic.getDuration() - blockSize);
        if (getBreakLength() > 0) {
            breakEvent((GregorianCalendar) getTimeNow());
        }
    }
    /*
     * Study Blocks with no time in the day:
     *
     */

    /**
     * @param topic
     * @param timeAdded
     */
    private void studyBlock_nextDay(Topic topic, int timeAdded) {
        System.out.println("1.2");
        nextDay();
        printEvent_ONE(getTimeNow().getTime(), topic.getSubject());
        addToPlan(topic, timeAdded);
        printEventDurations(timeNow.getTime(), topic.getDuration());
        removeOrSetNewTopicDuration(topic, topic.getDuration() - timeAdded);
        if (getBreakLength() > 0) {
            breakEvent((GregorianCalendar) getTimeNow());
        }
    }


    /*
     * Study Blocks that enforce minimum length i.e. fill the remainder of the day. No break needed.
     */

    /**
     * @param topic
     * @param timeAdded
     */
    private void studyBlockEvent_customSize(Topic topic, int timeAdded) {
        System.out.println("1.3");
        printEvent_ONE(getTimeNow().getTime(), topic.getSubject());
        addToPlan(topic, timeAdded);
        printEventDurations(timeNow.getTime(), topic.getDuration());
        removeOrSetNewTopicDuration(topic, topic.getDuration() - timeAdded);
        nextDay();
    }

    /**
     * Break event that is only scheduled in spaces eligible for study time.
     * If there is a study block before an event, this method will not do anything.
     * @param gregorianCalendar
     */
    private void breakEvent(GregorianCalendar gregorianCalendar) {
        if(!checkForEvents_InBreak(getTimeNow(), getBreakLength())){
        	if(!isShorterStudyBlockBeforeEvent()) {
        		if (minutesBetween(gregorianCalendar, getDailyEndStudyTime()) >= getBreakLength()) {
                gregorianCalendar = (GregorianCalendar) getTimeNow().clone();
                addToPlan("Break", gregorianCalendar, getBreakLength());
                printBreak(getBreakLength());
                getTimeNow().add(GregorianCalendar.MINUTE, getBreakLength());
        		}
        	}
        }
    }

    /*
    System Outs
     */
    public void setDefaultStudyBlockDuration(int defaultStudyBlockDuration) {
        this.defaultStudyBlockDuration = defaultStudyBlockDuration;
    }

    private void printEvent_ONE(Date value, String subject) {
        System.out.println("\n-----STUDY-BLOCK-----");
        System.out.println(value);
        System.out.println(subject);
    }

    private void printEventDurations(Date value, int duration) {
        System.out.println("Study block ends at " + value);
        System.out.println("Full study duration is " + duration);
    }

    private void printEvent_TWO(int remainingMinutes) {
        System.out.println("There are " + remainingMinutes + " minutes left to study.");
        System.out.println("-----STUDY BLOCK END-----");
        System.out.println();
    }

    private void printBreak(int breakLength) {
        System.out.println("-----BREAK-----");
        System.out.println(" Lasting " + breakLength + " minutes.");
        System.out.println("-----BREAK-END-----\n");
    }

    private void printEvent(CalendarEventInterface calendarEvent) {
        System.out.println("\n-----EVENT-----");
        System.out.println("Event: " + calendarEvent.getName());
        System.out.println("Event starts at " + calendarEvent.getStartTime().getTime());
        Calendar newCal = (Calendar)calendarEvent.getStartTime().clone();
        newCal.add(GregorianCalendar.MINUTE, calendarEvent.getDuration());
        System.out.println("Event ends at: " + newCal.getTime());
        System.out.println("Event duration is: " + calendarEvent.getDuration());
        System.out.println("-----EVENT END-----\n");
    }

    /**
     *
     * @param topic
     */
    @Override
    public void deleteTopic(String topic) {
        for (int i = 0; i < getTopics().size(); i++) {
            if (getTopics().get(i).getSubject().equals(topic)) {
                topics.remove(i);
            }
        }
//        if (getStudyPlannerGUI() != null) {
//            studyPlannerGUI.notifyModelHasChanged();
//        }
    }

    public ArrayList<Topic> getStudyList() {
        return studyList;
    }


    /**
     *
     * Block and Break Lengths
     */

    /**
     * @param size the standard duration in minutes
     *             This will take parameters that influence the length of the study block.
     *             Call this method later to change the length of the blocks when there
     *             is no time left in the day.
     *             If the block size is more than 60 minutes, then it should be out of bounds.
     */
    @Override
    public void setBlockSize(int size) {
        if (size < minimumStudyBlockSize) {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog("Minimum block time is 10 minutes.");
            }else{
                throw new StudyPlannerException("Minimum block time is 10 minutes.");
            }
        } else if ((int) minutesBetween(getDailyStartStudyTime(), getDailyEndStudyTime()) < size) {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog("The block size specified cannot fit between the start time and end time of the day.");
            }else{
                throw new StudyPlannerException("The block size specified cannot fit between the start time and end time of the day.");
            }
        } else {
            this.defaultStudyBlockDuration = size;
        }
    }

    /**
     *
     * @return
     */
    public int getBlockSize() {
        return this.defaultStudyBlockDuration;
    }

    /**
     *
     * @param i
     */
    @Override
    public void setBreakLength(int i) {
        this.breakBlockDuration = i;
    }

    /**
     *
     * @return
     */
    public int getBreakLength() {
        return this.breakBlockDuration;
    }


    /*
     *
     *
     * Daily Start and End Times
     *
     */

    /**
     * @return
     */
    @Override
    public Calendar getDailyStartStudyTime() {
        return defaultDailyStartTime;
    }

    /**
     *
     * @return
     */
    @Override
    public Calendar getDailyEndStudyTime() {
        return defaultDailyEndTime;
    }

    /**
     * @param startTime Calendar object specifying the time of day to start studying. The date parts of the Calendar are ignored
     */
    @Override
    public void setDailyStartStudyTime(Calendar startTime) {
        if (defaultDailyStartTime.equals(null)) {
            this.defaultDailyStartTime = (GregorianCalendar) startTime;
        } else if (startTime.before(getDailyEndStudyTime())) {
            if (getDailyEndStudyTime().get(GregorianCalendar.HOUR_OF_DAY) - startTime.get(GregorianCalendar.HOUR_OF_DAY) <= (int) calculateMinutesToHours(getBlockSize())) {
                if ((int) minutesBetween(startTime, getDailyEndStudyTime()) < getBlockSize()) {
                    throw new StudyPlannerException("Not enough minutes left in the hour for this");
                } else {
                    this.defaultDailyStartTime = (GregorianCalendar) startTime;
                }
            } else {
                this.defaultDailyStartTime = (GregorianCalendar) startTime;
            }
        } else {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog("Specified time is after your end time");
            }else{
                throw new StudyPlannerException("Specified time is after your end time");
            }
        }
    }

    /**
     * @param endTime
     */
    @Override
    public void setDailyEndStudyTime(Calendar endTime) {
        if (defaultDailyEndTime.equals(null)) {
        	System.out.println("1.1");
            this.defaultDailyEndTime = (GregorianCalendar) endTime;
        } else if (endTime.after(getDailyStartStudyTime())) {
        	System.out.println("1.2");

            if (endTime.get(GregorianCalendar.HOUR_OF_DAY) - getDailyStartStudyTime().get(GregorianCalendar.HOUR_OF_DAY) <= (int) calculateMinutesToHours(getBlockSize())) {
            	System.out.println("1.2.1.1");
                if ((int) minutesBetween(getDailyStartStudyTime(), endTime) < getBlockSize()) {
                    throw new StudyPlannerException("Not enough minutes left in the hour for a study block to occur");
                } else {
                	System.out.println("1.2.1.2");
                    this.defaultDailyEndTime = (GregorianCalendar) endTime;
                }
            } else {
            	System.out.println("1.2.2");
                this.defaultDailyEndTime = (GregorianCalendar) endTime;
            }
        } else {
            if (getStudyPlannerGUI() != null) {
                getStudyPlannerGUI().getView_().showDialog("Specified time is before your daily start time");
            }else {
                throw new StudyPlannerException("Specified time is before your daily start time");
            }
        }
    }

    /*
     *
     *
     * Conversion Methods
     *
     */

    /**
     * Using the TimeUnit utility, we can find out the difference in minutes there
     * are between two calendar dates. Using Math.abs allows us to return an absolute value to work with
     *
     * @param startTime
     * @param endDate
     * @return
     */
    private long minutesBetween(Calendar startTime, Calendar endDate) {
        long end = endDate.getTimeInMillis();
        long start = startTime.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toMinutes((end - start));
    }

    /**
     * Find out the total minutes that have passed since Jan 1970 up to now.
     * This only rounds up to minutes.
     * @param calendar
     * @return
     */
    private long minutesFromCalendar(Calendar calendar) {
        long calendarTimeInMillis = calendar.getTimeInMillis();
        return TimeUnit.MILLISECONDS.toMinutes((calendarTimeInMillis));
    }

    /**
     * @param minutes
     * @return
     */
    private long calculateMinutesToHours(int minutes) {
        return TimeUnit.MINUTES.toHours(minutes);
    }


    /*
     *
     * Calendar Events
     *
     *
     */

    /**
     *
     * @return
     */
    public boolean wasThereAnEventClash() {
        return eventClash;
    }

    /**
     *
     * @param eventClash
     */
    public void setEventClash(boolean eventClash) {
        this.eventClash = eventClash;
    }

    /**
     * @param eventName the name of the event by which it can be referred to later
     * @param startTime the date and time at which the event starts
     * @param duration  the duration of the event in minutes
     */
    @Override
    public void addCalendarEvent(String eventName, Calendar startTime, int duration) {

        if (calendarEvents.isEmpty()) {
            calendarEvents.add(new CalendarEvent(eventName, startTime, duration, null));
        } else {
            setEventClash(false);
            int proposedEventStartTime = (int) minutesFromCalendar(startTime);
            int proposedEventEndTime = (int) minutesFromCalendar(startTime) + duration;

            for (int i = proposedEventStartTime; i < proposedEventEndTime; i++) {

                for (CalendarEventInterface c : calendarEvents) {

                    int calendarEventStartTime = (int) minutesFromCalendar(c.getStartTime());
                    int calendarEventEndTime = (int) minutesFromCalendar(c.getStartTime()) + duration;

                    for (int j = calendarEventStartTime; j < calendarEventEndTime; j++) {

                        if (j == i) {
                            setEventClash(true);
                        }
                    }
                }
            }
            if (wasThereAnEventClash()) {
                if (getStudyPlannerGUI() != null) {
                    getStudyPlannerGUI().getView_().showDialog("There is an event that clashes with your event");
                } else {
                    throw new StudyPlannerException("There is an event that clashes with your event");
                }
            } else {
                calendarEvents.add(new CalendarEvent(eventName, startTime, duration, null));

                }



        }
    }

    /**
     * Adding calendar events with an associated target type.
     * @param eventName the name of the event by which it can be referred to later
     * @param startTime the date and time at which the event starts
     * @param duration  the duration of the event in minutes
     * @param type      the type of the event
     */
    @Override
    public void addCalendarEvent(String eventName, Calendar startTime, int duration, CalendarEventType type) {

        if (calendarEvents.isEmpty()) {
            calendarEvents.add(new CalendarEvent(eventName, startTime, duration, type));
        } else {
            setEventClash(false);
            int proposedEventStartTime = (int) minutesFromCalendar(startTime);
            int proposedEventEndTime = (int) minutesFromCalendar(startTime) + duration;

            for (int i = proposedEventStartTime; i < proposedEventEndTime; i++) {

                for (CalendarEventInterface c : calendarEvents) {

                    int calendarEventStartTime = (int) minutesFromCalendar(c.getStartTime());
                    int calendarEventEndTime = (int) minutesFromCalendar(c.getStartTime()) + duration;

                    for (int j = calendarEventStartTime; j < calendarEventEndTime; j++) {

                        if (j == i) {
                            setEventClash(true);
                        }
                    }
                }
            }
            if (wasThereAnEventClash()) {
                if (getStudyPlannerGUI() != null) {
                    getStudyPlannerGUI().getView_().showDialog("There is an event that clashes with your event");
                } else {
                    throw new StudyPlannerException("There is an event that clashes with your event");
                }
            } else {
                calendarEvents.add(new CalendarEvent(eventName, startTime, duration, type));
                }
            }
        }


    /**
     * @return
     */
    @Override
    public List<CalendarEventInterface> getCalendarEvents() {
        return calendarEvents;
    }

    public void getCalendarEventsAsString() {
        for (CalendarEventInterface calev : calendarEvents) {
            System.out.println(calev.getName());
        }
    }

    /*
     *
     * Loading and Saving
     *
     */

    /**
     * @param saveStream The stream to write the save data to
     */
    @Override
    public void saveData(OutputStream saveStream) {
        try {
            if (!plan.isEmpty()) {
                ObjectOutputStream planOutputStream = new ObjectOutputStream(saveStream);
                savePlan(planOutputStream);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            closeOutputStream(saveStream);
        }
    }

    /**
     * The stream to write the objects from the plan.
     * @param objectOutputStream
     */
    private void savePlan(ObjectOutputStream objectOutputStream) {
        try {
            for (StudyBlockInterface sb : plan) {
                objectOutputStream.writeObject(sb);
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * Close the output stream associated with saving the data.
     * @param saveStream
     */
    private void closeOutputStream(OutputStream saveStream) {
        //Switch statement between output and input streams?
        try {
            saveStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /**
     *
     * @param loadStream The stream to load the data from
     */
	@Override
    public void loadData(InputStream loadStream) {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(loadStream);

            if (loadStream.available() == 0) {
                if (getStudyPlannerGUI() != null) {
                    getStudyPlannerGUI().getView_().showDialog("File is empty.");
                } else {
                    throw new StudyPlannerException("File is empty.");
                }
            } else if (loadStream == null) {
                if (getStudyPlannerGUI() != null) {
                    getStudyPlannerGUI().getView_().showDialog("File not found.");
                } else {
                    throw new StudyPlannerException("File not found.");
                }
            } else {
                while (loadStream.available() != 0) {
                    loadPlan(objectInputStream);
                }
            }
            objectInputStream.close();
        } catch (IOException ioce) {
            ioce.printStackTrace();
        } finally {
            closeInputStream(loadStream);
        }
    }

    /**
     * Add the objects to the plan.
     * @param objectInputStream
     */
    private void loadPlan(ObjectInputStream objectInputStream) {
        try {

            StudyBlock newBlock = (StudyBlock) objectInputStream.readObject();
            plan.add(new StudyBlock(newBlock.getTopic(), newBlock.getStartTime(), newBlock.getDuration()));
        } catch (ClassNotFoundException | IOException ioe) {
            ioe.printStackTrace();
        }
    }
    /**
     * Close the input stream associated with loading the data.
     * @param inputStream
     */
    private void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
    }

    /**
     * This is an implemented method that is not used because of the way I implemented Javafx.
     *
     * @param gui
     */
    @Override
    public void setGUI(StudyPlannerGUIInterface gui) {

    }
}
