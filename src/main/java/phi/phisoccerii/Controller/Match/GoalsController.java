package phi.phisoccerii.Controller.Match;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.goal.Goal;
import phi.phisoccerii.Model.match.Match;

public class GoalsController implements IMatchController{
    private Match match;
    private ObservableList<Goal>goalObsList;
    private static final Image goalLogo = new Image(App.class.getResourceAsStream("logo.png"));
    @FXML private ListView<Goal> goalsListView;

    @Override
    public void setMatch(Match match) {
        this.match=match;
        goalObsList = FXCollections.observableArrayList(match.getGoals());
        goalsListView.setItems(goalObsList);
        setContent();
    }

    @Override
    public void setContent() {
        goalsListView.setCellFactory(param -> new ListCell<Goal>() {
            @Override
            protected void updateItem(Goal goal, boolean empty) {
                super.updateItem(goal, empty);
                if (empty || goal == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    Label timeLabel = new Label(goal.getTime());
                    Label scorerLabel = new Label(goal.isHomeScored()? goal.getSoccer() + " (" + goal.getScore() + ")":" (" + goal.getScore() + ") "+goal.getSoccer());
                    Label assistLabel = new Label("Assist: " + goal.getAssist());
                    ImageView goalImage = new ImageView(goalLogo);
                    goalImage.setFitWidth(27); goalImage.setFitHeight(27);

                    timeLabel.getStyleClass().add("time-label");
                    //scorerLabel.setStyle(goal.isHomeScored() ? "-fx-text-fill: green;-fx-font-weight:bold;" : "-fx-text-fill: red;-fx-font-weight:bold;");
                    scorerLabel.getStyleClass().add("scorer-label");
                    scorerLabel.getStyleClass().add(goal.isHomeScored()?"home-goal":"away-goal");
                    assistLabel.getStyleClass().add("assist-label");

                    VBox vBox = new VBox(scorerLabel);
                    if(!goal.getAssist().isEmpty()) vBox.getChildren().add(assistLabel);
                    vBox.setSpacing(2);

                    HBox hBox = new HBox(10);
                    hBox.setPrefWidth(400);
                    //HBox.setHgrow(vBox, Priority.ALWAYS);

                    if (goal.isHomeScored()) {
                        hBox.getChildren().addAll(vBox, timeLabel,goalImage);
                        hBox.setAlignment(Pos.CENTER_LEFT);
                    } else {
                        hBox.getChildren().addAll(goalImage,timeLabel, vBox);
                        hBox.setAlignment(Pos.CENTER_RIGHT);
                    }

                    setGraphic(hBox);
                }
            }
        });
    }

}
