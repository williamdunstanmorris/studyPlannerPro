package japrc2016test;

import japrc2016.StudyPlanner;
import japrc2016.StudyPlannerException;
import japrc2016.StudyPlannerInterface;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class LevelTwoTests {
	
	  private StudyPlannerInterface planner;

	    @Before
	    public void setUp() throws StudyPlannerException{
	        try{
	            planner = new StudyPlanner();
	        } catch (StudyPlannerException se){
	            se.printStackTrace();
	        }
	    }
	    
	    @Test
	    public final void minimumStudyBlockBeforeEndOfDay(){
	    	planner.addTopic("Maths", 140);
	    	planner.setBlockSize(45);
	    	planner.setBreakLength(0);
	    	Calendar start = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 10, 0, 0);
	    	Calendar endTime = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 11, 0, 0);
	    	planner.setDailyEndStudyTime(endTime);
	    	System.out.println("New end time is: " + planner.getDailyEndStudyTime().getTime());
	    	planner.generateStudyPlan(start);
	    	assertTrue(planner.getStudyPlan().get(1).getDuration() < 45);
	    }
	    
		@Test
	    public final void doEventsOverlap() {
			Calendar event1 = new GregorianCalendar(2017,0, 2, 10, 00,00);
			Calendar event2 = new GregorianCalendar(2017,0, 2, 10, 30,00);
			try {
				planner.addCalendarEvent("Java Test", event1, 60);
				planner.addCalendarEvent("Java Test", event2, 60);
			} catch (StudyPlannerException e) {
				System.err.println(e);
			}
	    }
		
		@Test
		public final void testEventInsideDayWindow(){
			Calendar start = new GregorianCalendar(2017,1, 11, 9, 0,00);
			Calendar endTime = new GregorianCalendar(2017,1, 11, 17, 0,00);
			Calendar event1 = new GregorianCalendar(2017,1, 11, 10, 15,00);
			planner.setDailyEndStudyTime(endTime);
			planner.addTopic("Maths", 120);
			/**
			 * Two full study blocks of 60 minutes should occur. However, when 
			 * an event occurs mid way through a block, a minimum studyblock occurs
			 * and an event after too. Altogether there should be 7 events including breaks after
			 * study blocks (not before an event).
			 */
			planner.addCalendarEvent("EXAM", event1, 30, null);
			planner.generateStudyPlan(start);
			assertEquals(7, planner.getStudyPlan().size());
		}
	
}
