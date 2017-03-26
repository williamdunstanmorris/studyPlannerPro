package japrc2016test;

import static org.junit.Assert.*;


import org.junit.Before;
import org.junit.Test;


import japrc2016.StudyPlannerGUI;
import japrc2016.StudyPlannerException;

public class LevelThreeTests {
	
	  private StudyPlannerGUI studyPlannerGUI;

	    @Before
	    public void setUp() throws StudyPlannerException{
	        try{
	            studyPlannerGUI = new StudyPlannerGUI();
	        } catch (StudyPlannerException se){
	            se.printStackTrace();
	        }
	    }
	    
	    @Before
	    public final void testInit(){
	    	studyPlannerGUI.startApplication();
	    }
	    
	    @Test
	    public final void testNullableClasses(){
	    	assertNotNull(studyPlannerGUI.getView_());
	    	assertNotNull(studyPlannerGUI.getPlanner());
	    }
	    
	   @Test
	   public final void testPlannersPlanUpdatestheObservableArrayList(){
		   //Could not get JUnit to do this. Wanted to call onAction event on add button, and check that the observable list had been updated.
		   //Would love to know how to test GUI properly!!
		   
//		   studyPlannerGUI.getPlanner().addTopic("Maths", 230);
//		   studyPlannerGUI.notifyModelHasChanged();
//		   assertTrue(studyPlannerGUI.getView_().getTopicData().size() == 1);
//		  
	   }

}
