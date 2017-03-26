//package japrc2016;
//
//import javafx.beans.value.ChangeListener;
//import javafx.beans.value.ObservableValue;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.Initializable;
//import javafx.scene.control.Button;
//import javafx.scene.control.TextField;
//
//import java.net.URL;
//import java.util.Observer;
//import java.util.ResourceBundle;
//
//public class AlertBoxController extends MyGUI implements Initializable, Observer {
//
//    @FXML
//    private Button gui_addTopic_addTopicButton;
//
//    @FXML
//    private Button gui_addTopic_cancelButton;
//
//    @FXML
//    private TextField gui_addTopic_TopicNameInput;
//
//    @FXML
//    private TextField gui_onAddTopic_TopicDurationInput;
//
//    @Override
//    public void initialize(URL location, ResourceBundle resources) {
//        gui_onAddTopic_TopicDurationInput.setOnAction(this::onAddTopic_TopicDurationInput);
//
//        gui_onAddTopic_TopicDurationInput.lengthProperty().addListener(new ChangeListener<Number>(){
//            @Override
//            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
//                if (newValue.intValue() > oldValue.intValue()) {
//                    char ch = gui_onAddTopic_TopicDurationInput.getText().charAt(oldValue.intValue());
//                    // Check if the new character is the number or other's
//                    if (!(ch >= '0' && ch <= '9' )) {
//                        // if it's not number then just setText to previous one
//                        gui_onAddTopic_TopicDurationInput.setText(gui_onAddTopic_TopicDurationInput.getText().substring(0,gui_onAddTopic_TopicDurationInput.getText().length()-1));
//                    }
//                }
//            }
//        });
//
//    }
//
//    @FXML
//    void onAddTopic_AddTopicButton(ActionEvent event) {
//
//        if ((!gui_addTopic_TopicNameInput.getText().equals("")) && (!gui_onAddTopic_TopicDurationInput.getText().equals(""))) {
//            /**
//             *
//             * 1. Update the model
//             * 2. Allow the model to then update the view in the MyGUI app.
//             * 3.
//             */
//            //Unsure about how to access this...
//            //1: try it from the static approach
//            //2: try from a getPlanner approach
//            //Initialise it and access it from the start(Stage primaryStage) in MyGUI class... unsure about how to do that..
//
////            getStudyPlanner().addTopic(gui_addTopic_TopicNameInput.getText(), Integer.parseInt(gui_onAddTopic_TopicDurationInput.getText()));
////        } else if (getStudyPlanner().addTopic();) {
////            /**
////             * Run a new window to open that shows a thrown exception if the topic already exists (inside the studyplanner.addtopic already)
////             */
////        } else {
////            /**
////             * Add the topic and update the model, then update the view in the MainGUI window.
////             * Close the window too!
////             */
////        }
//
//// studyPlanner.addTopic(addName.getText(), addDurationInteger);
//        }
//    }
//
//
//    @FXML
//    void onAddTopic_TopicDurationInput(ActionEvent event) {
//
//    }
//
//    @FXML
//    void onAddTopic_TopicNameInput(ActionEvent event) {
//
//    }
//
//    @FXML
//    void onAddTopic_cancelButton(ActionEvent event) {
//    }
//
//
//
//}
