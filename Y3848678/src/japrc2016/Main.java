package japrc2016;

public class Main {

    public static void main(String[] args){
 	
    	/*
         * Call this to execute the JavaFX Application (Please comment out the below code(Although it should
         * not affect anything anyway, as the JavaFX application creates a new instance of the model)
         */
        StudyPlannerGUI studyPlannerGUI = new StudyPlannerGUI();
        studyPlannerGUI.startApplication();
        
        /*
         * Else call business logic
         */
        
//		StudyPlanner planner = new StudyPlanner();
//	
//		planner.addTopic("Maths", 60);
//    	planner.setBlockSize(30);
//    	Calendar endTime = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 11, 0, 0);
//    	planner.setDailyEndStudyTime(endTime);
//    	
//    	System.out.println("New end time is: " + planner.getDailyEndStudyTime().getTime());
//    	planner.generateStudyPlan(endTime);
////		
//		Calendar calendar = new GregorianCalendar(2015, 4, 5, 2, 1, 5);
//		
//		System.out.println("Time now:" + planner.getTimeNow().getTime());
//		planner.setTimeNow(calendar.getTime());
//		System.out.println("Time now: " + planner.getTimeNow().getTime());
//		
//		
//		planner.setDailyStartStudyTime(Calendar.getInstance());
//		System.out.println("Daily start time is" + planner.getDailyStartStudyTime().getTime());
//		planner.getDailyStartStudyTime().add(GregorianCalendar.DAY_OF_MONTH, 1);
//		System.out.println("Daily start time is" + planner.getDailyStartStudyTime().getTime());		
//		
//		planner.addTopic("Topic A ",  132);
//		planner.addTopic("Topic B ", 453);
//		planner.addTopic("Topic C", 654);
//		planner.addTopic("Topic D", 243);
//
//
//		Calendar topicA = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 9, 30, GregorianCalendar.getInstance().get(GregorianCalendar.SECOND));
//		Calendar topicB = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 22, 20, GregorianCalendar.getInstance().get(GregorianCalendar.SECOND));
//		Calendar topicC = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 8, 30, GregorianCalendar.getInstance().get(GregorianCalendar.SECOND));
//		topicC.add(GregorianCalendar.DAY_OF_MONTH, 2);
//////
////
//		planner.addCalendarEvent("EXAM", topicA, 50);
//		planner.addCalendarEvent("ESSAY", topicB, 20);
//		planner.addCalendarEvent("Lovely ", topicC, 90, StudyPlannerInterface.CalendarEventType.OTHER);
//////
//		planner.getTopics().get(0).setTargetEvent(planner.getCalendarEvents().get(2));
//		System.out.println("Topic has the associated event value " + planner.getTopics().get(0).getTargetEvent().getName());


//		planner.setBlockSize(50);
//		planner.setBreakLength(20);
//		planner.generateStudyPlan(newcal);
//
//		System.out.println("------------\n");
//		System.out.println("------------\n");

//        try{
//            OutputStream saveData = new FileOutputStream("Plan.txt", false);
//            planner.saveData(saveData);
//        }catch(IOException ioe) {
//            ioe.printStackTrace();
//        }
//
////        try{
////            InputStream loadData = new FileInputStream("Plan.txt");
////            planner.loadData(loadData);
////        } catch (FileNotFoundException fnfe){
////            fnfe.printStackTrace();
////        }
//
////		System.out.println(planner.getStudyPlan().toString());
////		System.out.println(planner.getTopics());
////		System.out.println(planner.getCalendarEvents());
////
////		planner.getCalendarEventsAsString();
////		planner.setBreakLength(10);
////		planner.setBlockSize(60);

      
    }
}
