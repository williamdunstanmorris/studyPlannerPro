package japrc2016;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class DialogController {

  private View view_;

    public View getView_() {
        return view_;
    }

    public void setView_(View view_) {
        this.view_ = view_;
    }

    @FXML
    private Label messageLabel;

    @FXML
    private Label detailsLabel;

    @FXML
    private HBox actionParent;

    @FXML
    private Button cancelButton;

    @FXML
    private HBox okParent;

    @FXML
    private Button okButton;

    @FXML
    void onCancelButton(ActionEvent event) {
        getView_().closeDialog();
    }

    @FXML
    void onOkayButton(ActionEvent event) {
        getView_().closeDialog();
    }

    public void changeText(String message){
        messageLabel.setText(message);
    }

}