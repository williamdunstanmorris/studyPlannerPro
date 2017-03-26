package japrc2016;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;

public class View extends Application {

	private Stage primaryStage;
	private Stage dialogStage;
    private BorderPane rootLayout;
    private StudyPlannerGUI _studyPlannerGUI;
    private StudyPlanner planner;
    StudyPlannerGUIInterface studyPlannerGUIInterface;

    private ObservableList<TopicInterface> topicData = FXCollections.observableArrayList();
    private ObservableList<Events> planData = FXCollections.observableArrayList();

    /**
	 * Constructor
	 */
	public View() {
		System.out.println("View constructor initialised...");
	}

	public View getStudyPlannerGUI_FX(){
		return this;
	}

	public void setPlanner(StudyPlanner planner) {
		this.planner = planner;
	}

	public StudyPlannerGUI get_studyPlannerGUI() {
		if(_studyPlannerGUI == null){
			System.out.println("Study Planner StudyPlannerGUI is null");
		}
		return _studyPlannerGUI;
	}

	public ObservableList<TopicInterface> getTopicData() {
		return topicData;
	}

	public void setTopicData(ObservableList<TopicInterface> topicData) {
		this.topicData = topicData;
	}

	public ObservableList<Events> getPlanData() {
		return planData;
	}

	public void setPlanData(ObservableList<Events> planData) {
		this.planData = planData;
	}

	public StudyPlanner getPlanner() {
		return this.planner;
	}

	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("Study Planner");

		try{
			FXMLLoader loader = new FXMLLoader(View.class.getResource("RootLayout.fxml"));
            rootLayout = loader.load();
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
		} catch (IOException e){
			e.printStackTrace();
		}
		showOverview();
	}

	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public void showOverview(){
		setPlanner(new StudyPlanner());

		try{
			FXMLLoader loader = new FXMLLoader(View.class.getResource("TopicOverview.fxml"));

			AnchorPane overviewPage = loader.load();
			rootLayout.setCenter(overviewPage);
			_studyPlannerGUI = loader.getController();
			_studyPlannerGUI.setView_(this);

			if(_studyPlannerGUI == null){
				System.out.println("Study planner controller not initialised");
			} else {
				System.out.println("Study planner controller initialised...");

			}

		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public void updateTopicDisplay(){

		getTopicData().clear();
		for(TopicInterface t: get_studyPlannerGUI().getPlanner().getTopics()){
			System.out.println("Topic in View: " + t.getSubject());
			getTopicData().add(t);
		}
	}

	public void updatePlanDisplay(){

		getPlanData().clear();
		ArrayList<Events> events = new ArrayList<>();
		for(StudyBlockInterface sb: get_studyPlannerGUI().getPlanner().getStudyPlan()){
			/**
			 * Convert calendar into String
			 */
			String stringDate = DateFormat.getInstance().format(sb.getStartTime().getTime());
			events.add(new Events(sb.getTopic(), stringDate ));
		}
		/**
		 * Finally I add all of this sorted data to an observable array list.
		 */
		for(Events ev: events){
			planData.add(ev);
		}
	}


	public void showDialog(String errorMessage) {
		GridPane dialogLayout;
		dialogStage = new Stage();
		dialogStage.setTitle("Error Message");
		try {
			FXMLLoader loader = new FXMLLoader(View.class.getResource("DialogBox.fxml"));
			dialogLayout = loader.load();
			Scene scene = new Scene(dialogLayout);
			dialogStage.setScene(scene);
			dialogStage.show();

			//Create a dialog controller for this alert box:
			DialogController dialogController = loader.getController();
			dialogController.setView_(this);
			dialogController.changeText(errorMessage);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Stage getDialogStage() {
		return dialogStage;
	}

	public void closeDialog() {
		getDialogStage().close();
	}

	public static void main(String[] args) {
		launch(args);
	}

	
}
