package japrc2016test;

import japrc2016.StudyBlockInterface;
import japrc2016.StudyPlanner;
import japrc2016.StudyPlannerException;
import japrc2016.StudyPlannerInterface;

import static org.junit.Assert.*;

import java.util.Calendar;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class LevelOneTests {

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
    public final void shorterStudyBlockCreated(){
    	planner.addTopic("Maths", 160);
    	planner.setBlockSize(45);
    	planner.setBreakLength(0);
    	Calendar start = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 10, 0, 0);
    	Calendar endTime = new GregorianCalendar(GregorianCalendar.getInstance().get(GregorianCalendar.YEAR), GregorianCalendar.getInstance().get(GregorianCalendar.MONTH), GregorianCalendar.getInstance().get(GregorianCalendar.DAY_OF_MONTH), 13, 0, 0);
    	planner.setDailyEndStudyTime(endTime);
    	System.out.println("New end time is: " + planner.getDailyEndStudyTime().getTime());
    	planner.generateStudyPlan(start);
    	/* 
    	 *160 divided by 45 gives us a remainder of 25, the fourth study block
    	 *should be of shorter length.
    	 */
    	assertTrue(planner.getStudyPlan().get(3).getDuration() == 25);	
    }
  
	@Test
    public final void manipulateTopicArray(){
    	try {
    		planner.addTopic("Maths", 240);
    		planner.deleteTopic("Maths");
    		assertTrue(planner.getTopics().size() == 0);
    		planner.addTopic("Maths", 240);
    		planner.addTopic("Maths", 240);
    	} catch (StudyPlannerException se){
    		System.err.println(se);
    	}
    	assertTrue(planner.getTopics().size() == 1);
    }
    
	@Test
	public final void planWithoutBreak(){
		planner.addTopic("Maths", 300);
        planner.setBreakLength(0);
		planner.generateStudyPlan();
		assertEquals(5, planner.getStudyPlan().size());
	}
	
    
    
    






}
