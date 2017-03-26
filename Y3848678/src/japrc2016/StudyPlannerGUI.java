package japrc2016;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.*;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


public class StudyPlannerGUI implements StudyPlannerGUIInterface {

    @FXML
    private MenuItem load;

    @FXML
    private MenuItem save;

    @FXML
    private MenuItem quit;

    @FXML
    private MenuItem generate;

    @FXML
    private TableView<TopicInterface> topicTable;

    @FXML
    private TableColumn<TopicInterface, String> topicNameColumn;

    @FXML
    private TableColumn<TopicInterface, Integer> durationIntColumn;

    @FXML
    private Text topicNameLabel;

    @FXML
    private Text topicDurationLabel;

    @FXML
    private Text topicTargetLabel;

    @FXML
    private TextField topicNameField;

    @FXML
    private TextField topicDurationField;

    @FXML
    private ComboBox<String> topicTargetComboBox;

    @FXML
    private Button addTopicButton;

    @FXML
    private Button deleteTopicButton;

    @FXML
    private TextField eventNameField;

    @FXML
    private TextField eventDurationField;

    @FXML
    private ComboBox<?> eventTypeComboBox;

    @FXML
    private DatePicker eventDatePicker;

    @FXML
    private ComboBox<String> eventTimeHour;

    @FXML
    private ComboBox<String> eventTimeMinute;

    @FXML
    private Button deleteEventButton;

    @FXML
    private Button addEventButton;

    @FXML
    private TableView<Events> planTable;

    @FXML
    private TableColumn<Events, String> planTimeColumn;

    @FXML
    private TableColumn<Events, String> planTopicNameColumn;

    @FXML
    private ComboBox<String> dailyStartTimeHour;

    @FXML
    private ComboBox<String> dailyStartTimeMinute;

    @FXML
    private ComboBox<String> dailyEndTimeHour;

    @FXML
    private ComboBox<String> dailyEndTimeMinute;

    @FXML
    private TextField blockLengthField;

    @FXML
    private TextField breakLengthField;

    /**
     * Reference to the main model
     */
    private StudyPlanner planner = new StudyPlanner(this);
    /**
     * Reference to the main application
     */
    private View view_;

    public void startApplication(){
        view_ = new View();
        view_.main(null);

    }

    public View getView_() {
        return view_;
    }

    public void setPlanner(StudyPlanner planner) {
        this.planner = planner;
    }

    public StudyPlanner getPlanner() {
        return planner;
    }

    public StudyPlannerGUI getStudyPlanner_FX_Controller() {
        return this;
    }

    @FXML
    private void initialize() {
        
        topicNameColumn.setCellValueFactory(new PropertyValueFactory<TopicInterface, String>("subject"));
        durationIntColumn.setCellValueFactory(new PropertyValueFactory<TopicInterface, Integer>("duration"));
        topicTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        showTopicDetails(null);

        topicTable.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<TopicInterface>() {
            @Override
            public void changed(ObservableValue<? extends TopicInterface> observable, TopicInterface oldValue, TopicInterface newValue) {
                showTopicDetails(newValue);
            }
        });

        planTopicNameColumn.setCellValueFactory(new PropertyValueFactory<>("eventName"));
        planTimeColumn.setCellValueFactory(new PropertyValueFactory<>("eventTime"));
        planTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);


        topicDurationField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = topicDurationField.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        topicDurationField.setText(topicDurationField.getText().substring(0, topicDurationField.getText().length() - 1));
                    }
                }
            }
        });

        eventDurationField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = eventDurationField.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        eventDurationField.setText(eventDurationField.getText().substring(0, eventDurationField.getText().length() - 1));
                    }
                }
            }
        });

        breakLengthField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = breakLengthField.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        breakLengthField.setText(breakLengthField.getText().substring(0, breakLengthField.getText().length() - 1));
                    }
                }
            }
        });

        blockLengthField.lengthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                if (newValue.intValue() > oldValue.intValue()) {
                    char ch = blockLengthField.getText().charAt(oldValue.intValue());
                    // Check if the new character is the number or other's
                    if (!(ch >= '0' && ch <= '9')) {
                        // if it's not number then just setText to previous one
                        blockLengthField.setText(blockLengthField.getText().substring(0, blockLengthField.getText().length() - 1));
                    }
                }
            }
        });

        /**
         * Add hours to combo boxes
         */
        for (int i = 0; i < 24; i++) {
            String formatInt = Integer.toString(i);

            if (formatInt.length() == 1) {
                eventTimeHour.getItems().add("0" + formatInt);
                dailyStartTimeHour.getItems().add("0" + formatInt);
                dailyEndTimeHour.getItems().add("0" + formatInt);
            } else {
                eventTimeHour.getItems().add(formatInt);
                dailyStartTimeHour.getItems().add(formatInt);
                dailyEndTimeHour.getItems().add(formatInt);
            }
        }
        eventTimeHour.getSelectionModel().select(9);
//        dailyStartTimeHour.getSelectionModel().select(9);
//        dailyEndTimeHour.getSelectionModel().select(17);


        /**
         * Add minutes to combo box
         */

        for (int i = 0; i < 60; i++) {
            String formatInt = Integer.toString(i);

            if (formatInt.length() == 1) {
                eventTimeMinute.getItems().add("0" + formatInt);
                dailyStartTimeMinute.getItems().add("0" + formatInt);
                dailyEndTimeMinute.getItems().add("0" + formatInt);
            } else {
                eventTimeMinute.getItems().add(formatInt);
                dailyStartTimeMinute.getItems().add(formatInt);
                dailyEndTimeMinute.getItems().add(formatInt);
            }
        }
        eventTimeMinute.getSelectionModel().select(0);
//        dailyStartTimeMinute.getSelectionModel().select(0);
//        dailyEndTimeMinute.getSelectionModel().select(0);

        topicTargetComboBox.getItems().addAll("ESSAY", "EXAM", "OTHER");

    }

    //The setMainApp(...) method must be called by the MainApp class. This gives us a way to access
    // the MainApp object and get the list of data and other things. In fact, let's do that call right now.
    public void setView_(View view_) {
        this.view_ = view_;
        //Add observable data to table:
        //At some point, this should update the planner, and call its method to add a topic.
        planTable.setItems(view_.getPlanData());
        topicTable.setItems(view_.getTopicData());
    }

    private void showTopicDetails(TopicInterface topic) {
        if (topic == null) {
            topicNameLabel.setText("");
            topicDurationLabel.setText("");
        } else {
            topicNameLabel.setText(topic.getSubject());
            topicDurationLabel.setText((Integer.toString(topic.getDuration())));
            if (topic.getTargetEvent() == null) {
                topicTargetLabel.setText("");
            } else {
                topicTargetLabel.setText(topic.getTargetEvent().getStartTime().getTime().toString());
            }
        }
    }

    /*
     * Add new Topic
     *
     * @param event
     */
    @FXML
    void addTopicButton(ActionEvent event) {
        String errorMessage = "";

        if (topicNameField.getText() == null || topicNameField.getText().length() == 0) {
            errorMessage += "No valid topic entered!\n";
        }
        if (topicDurationField.getText() == null || topicDurationField.getText().length() == 0) {
            errorMessage += "No duration specified!\n";
        }
        if (errorMessage.length() == 0) {

            if (topicTargetComboBox.getValue() == null) {
                getPlanner().addTopic(topicNameField.getText(), Integer.parseInt(topicDurationField.getText()));
            } else {
                /**
                 * Need to look at adding calendar events that have a target topic too!
                 */
                String s = topicTargetComboBox.getSelectionModel().getSelectedItem();
                
                
                
//                if (!getPlanner().getCalendarEvents().isEmpty()) {
//                    for(CalendarEventInterface c: getPlanner().getCalendarEvents()) {
//                        if (s.equals("EXAM")) {
//                            Topic topic = new Topic(topicNameField.getText(), Integer.parseInt(topicDurationField.getText()));
//
//                        } else if (s.equals("ESSAY")) {
//                        } else {
//                        }
//                    }
//                } else {
                    getPlanner().addTopic(topicNameField.getText(), Integer.parseInt(topicDurationField.getText()));
                
            }

            notifyModelHasChanged();
        } else {
            /**
             * Error message
             */
            view_.showDialog(errorMessage);
        }
    }

    /*
     * Delete a selected topic.
     */
    @FXML
    void deleteTopicButton(ActionEvent event) {
        int selectedIndex = topicTable.getSelectionModel().getSelectedIndex();
        if (selectedIndex >= 0) {
            TopicInterface topic = topicTable.getSelectionModel().getSelectedItem();
            getPlanner().deleteTopic(topic.getSubject());
        } else {
            /**
             * Nothing has been selected
             */
            String errorMessage = "Nothing has been selected!";
            view_.showDialog(errorMessage);
        }
    }

    @FXML
    void eventTypeComboBox(ActionEvent event) {

    }

    @FXML
    void onDailyEndTimeHour(ActionEvent event) {

    }

    @FXML
    void onDailyEndTimeMinute(ActionEvent event) {

    }

    @FXML
    void onDailyStartTimeHour(ActionEvent event) {

    }

    @FXML
    void onDailyStartTimeMinute(ActionEvent event) {

    }

    @FXML
    void onEventDate(ActionEvent event) {

    }

    @FXML
    void addEventButton(ActionEvent event) {
        String errorMessage = "";

        if (eventNameField.getText() == null || eventNameField.getText().length() == 0) {
            errorMessage += "No valid event entered!\n";
        }
        if (eventDurationField.getText() == null || eventDurationField.getText().length() == 0) {
            errorMessage += "No duration specified for event!\n";
        }
        if (eventTypeComboBox.getSelectionModel().selectedItemProperty() == null) {
            errorMessage += "No type specified for event!\n";
        }
        if (eventDatePicker.getValue() == null) {
            errorMessage += "No date has been selected!\n";
        }
        if (errorMessage.length() == 0) {
            /**
             * Convert the datepicker and time specified to a date, then a calendar
             */
            Date newDate = Date.from(eventDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            /**
             * Now convert this into a calendar field for adding a start time...
             */
            GregorianCalendar newStartTime = (GregorianCalendar) Calendar.getInstance();
            newStartTime.setTime(newDate);

            newStartTime.set(GregorianCalendar.HOUR_OF_DAY, Integer.parseInt(eventTimeHour.getValue()));
            newStartTime.set(GregorianCalendar.MINUTE, Integer.parseInt(eventTimeMinute.getValue()));
            
            if (eventTypeComboBox.getValue() == null) {
                getPlanner().addCalendarEvent(eventNameField.getText(), newStartTime, Integer.parseInt(eventDurationField.getText()));
            } else {
                /**
                 * Need to look at adding calendar events that have a target topic too!
                 */
                String s = topicTargetComboBox.getSelectionModel().getSelectedItem();

                //Convert this to a type?
                if(s.equals("EXAM")){
                    getPlanner().addCalendarEvent(eventNameField.getText(), newStartTime, Integer.parseInt(eventDurationField.getText()), StudyPlannerInterface.CalendarEventType.EXAM);
                } else if (s.equals("ESSAY")){
                    getPlanner().addCalendarEvent(eventNameField.getText(), newStartTime, Integer.parseInt(eventDurationField.getText()), StudyPlannerInterface.CalendarEventType.ESSAY);
                } else {
                    getPlanner().addCalendarEvent(eventNameField.getText(), newStartTime, Integer.parseInt(eventDurationField.getText()), StudyPlannerInterface.CalendarEventType.OTHER);
                }
            }
        } else {
            /**
             * Error message
             */
            view_.showDialog(errorMessage);
        }
    }

    @FXML
    void deleteEventButton(ActionEvent event) {

    }

    @FXML
    void topicDurationField(ActionEvent event) {
    }

    @FXML
    void topicNameField(ActionEvent event) {

    }

    @FXML
    void topicTargetComboBox(ActionEvent event) {
    }

    public TableView<TopicInterface> getTopicTable() {
        return topicTable;
    }

    @FXML
    void onGenerate(ActionEvent event) {

        boolean a = dailyStartTimeMinute.getSelectionModel().isEmpty();
        boolean b = dailyStartTimeHour.getSelectionModel().isEmpty();
        boolean c = dailyEndTimeMinute.getSelectionModel().isEmpty();
        boolean d = dailyEndTimeHour.getSelectionModel().isEmpty();
        boolean e = blockLengthField.getText().isEmpty();
        boolean f = breakLengthField.getText().isEmpty();

        if (a && b && c && d && e && f) {
            getPlanner().generateStudyPlan();
        } else if ((a && b && c && d && e) && (!f)) {
            System.out.println("No. 1: Break length");
            int breakLength = Integer.parseInt(breakLengthField.getText());
            getPlanner().setBreakLength(breakLength);
            getPlanner().generateStudyPlan();
        } else if ((a && b && c && d && f) && (!e)) {
            System.out.println("No. 2: Block Length");
            int blockLength = Integer.parseInt(blockLengthField.getText());
            getPlanner().setBlockSize(blockLength);
            getPlanner().generateStudyPlan();
        } else if ((a && b && c && d) && !e && !f) {
            System.out.println("No. 3: Break and Block Length");
            int breakLength = Integer.parseInt(breakLengthField.getText());
            int blockLength = Integer.parseInt(blockLengthField.getText());
            getPlanner().setBreakLength(breakLength);
            getPlanner().setBlockSize(blockLength);
            getPlanner().generateStudyPlan();
        }

        //Not finished...

//        view_.getPlanner().generateStudyPlan();
        view_.updatePlanDisplay();
    }

    /**
     * Work out whether or not the planner load method is wrong first by adding some data to the planner and then calling it in its original form.
     * Save, then load.
     *
     * @param event
     */

    @FXML
    void onLoad(ActionEvent event) {

        final Stage primaryStage = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);

        if (file != null) {
            try {
                InputStream loadData = new FileInputStream(file);
                getPlanner().loadData(loadData);

            } catch (IOException fnfe) {
                fnfe.printStackTrace();
            }
        }
    }

    @FXML
    void onQuit(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    void onSave(ActionEvent event) {

        final Stage stage = new Stage();
        FileChooser fileChooser1 = new FileChooser();

        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser1.getExtensionFilters().add(extFilter);
        fileChooser1.setTitle("Save");

        File file = fileChooser1.showSaveDialog(stage);

        try {
            OutputStream saveData = new ObjectOutputStream(new FileOutputStream(file));
            getPlanner().saveData(saveData);
            view_.showDialog("Information saved!");
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }

    }
    @Override
    public void notifyModelHasChanged() {
        getView_().updateTopicDisplay();
        getView_().updatePlanDisplay();
    }
}
