package phi.phisoccerii.Controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static phi.phisoccerii.Model.GeneralService.applyBoldTextStyle;
import static phi.phisoccerii.Model.match.MatchService.getMatch;

public class MatchesController implements Initializable, IController {
    private boolean oneBYone, async;
    private String url;
    private static Task<Void>currentTask=null;

    private ObservableList<Match> matchesObsList =FXCollections.observableArrayList();
    private FilteredList<Match> matchesFilList;

    @FXML private TextField matchSearchBar;
    @FXML private TableColumn<Match, String> homeCol;
    @FXML private TableColumn<Match, ImageView> homeLogoCol;
    @FXML private TableColumn<Match, String> statusCol;
    @FXML private TableColumn<Match, ImageView> awayLogoCol;
    @FXML private TableColumn<Match, String> awayCol;
    @FXML private TableView<Match> matchesTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    public void setURL(String url) {
        this.url = url;
    }

    @Override
    public void setMethod(boolean oneBYone, boolean async) {
        this.oneBYone = oneBYone;
        this.async = async;
    }

    @Override
    public void declareTable() {
        homeCol.setCellValueFactory(new PropertyValueFactory<>("homeTeam"));
        awayCol.setCellValueFactory(new PropertyValueFactory<>("awayTeam"));
        //status.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusCol.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getStatus()));
        setMatchStatus();
        homeLogoCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getHomeLogo()));
        awayLogoCol.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAwayLogo()));

        //applyBoldTextStyle(homeCol);
        //applyBoldTextStyle(awayCol);
        setUpMatchSearchBar();
    }

    private void setMatchStatus()
    {
        statusCol.setCellFactory(tc->new TableCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Match match = getTableView().getItems().get(getIndex());

                    // Status ->Bold
                    Label scoreLabel;
                    Label statusLabel;
                    if(match.getScore().equals("-"))
                    {
                        scoreLabel = new Label(match.getTime());
                        statusLabel = new Label(match.getDate());
                    }
                    else
                    {
                        scoreLabel = new Label(match.getScore());
                        statusLabel = new Label(match.getStatus());
                    }

                    scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

                    if(match.getStatus().equals("Finished")) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                    else if(match.getStatus().equals("Postponed") || match.getStatus().equals("After Pen.") ) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
                    else if( match.getStatus().equals("Half Time") ) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: orange;");
                    else if( statusLabel.getText().contains("-") ) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
                    else statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:  #2ecc71;");

                    Label roundLabel = new Label(match.getRound());
                    roundLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    Label leagueLabel = new Label(match.getLeague());
                    leagueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");


                    VBox vbox = new VBox( leagueLabel ,roundLabel ,scoreLabel,statusLabel);
                    vbox.setAlignment(Pos.CENTER);

                    setGraphic(vbox);
                }
            }
        });
    }

    private void setUpMatchSearchBar()
    {
        matchSearchBar.textProperty().addListener(((observable, oldValue, newValue) ->
        {
            matchesFilList.setPredicate(match->{
                if(newValue.isEmpty() || newValue.isBlank() || newValue==null) return true;
                String keyWord = newValue.toLowerCase();
                if(match.getHomeTeam().toLowerCase().contains(keyWord)) return true;
                else if(match.getAwayTeam().toLowerCase().contains(keyWord))return true;
                else if(match.getTime().toLowerCase().contains(keyWord))return true;
                else if(match.getStatus().toLowerCase().contains(keyWord))return true;
                else if(match.getScore().toLowerCase().contains(keyWord))return true;
                else if(match.getLeague().toLowerCase().contains(keyWord))return true;
                else if(match.getRound().toLowerCase().contains(keyWord))return true;
                else return false;
            });

        }));
    }

    @Override
    public void setUpTable() {
        if(oneBYone)setMatchesTable1by1();
        else setMatchesTableAll();
    }

    public void setUpTable(String url) {
        setURL(url);
        if(oneBYone)setMatchesTable1by1();
        else setMatchesTableAll();
    }

    private void setMatchesTable1by1() {
        cancelPreviousOperation(); //if there is new task ordered cancel previous

        matchesObsList.clear();
        matchesTable.refresh();
        matchesFilList = new FilteredList<>(matchesObsList, p -> true);
        matchesTable.setItems(matchesFilList);

        currentTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = GeneralService.fetchData(url);
                JSONArray arr = response.getJSONArray("result");
                for(int i =0 ;i<arr.length();i++)
                {
                    if(isCancelled()) break;
                    Match match = getMatch(arr.getJSONObject(i),true,async);
                    Platform.runLater(() -> {
                        if(!isCancelled())  //if there is new task ordered
                        {
                            matchesObsList.add(match);
                            matchesTable.refresh();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(currentTask).start();
    }

    private void setMatchesTableAll() {
        cancelPreviousOperation(); //if there is new task ordered cancel previous
        matchesObsList.clear();
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Match> matches = MatchService.getMatches(url);
                matchesObsList = FXCollections.observableArrayList(matches);
                matchesFilList = new FilteredList<>(matchesObsList);
                Platform.runLater(()->{matchesTable.setItems(matchesFilList);});

                //setLogos
                MatchService.setLogos(url,matchesObsList);
                Platform.runLater(() -> matchesTable.refresh());
                return null;
            }
        };
        new Thread(task).start();
    }


    @FXML
    public void onMatchClicked(MouseEvent mouseEvent) {
        Match selectedMatch = matchesTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            showSelectedMatchInfo(selectedMatch);
        }
    }
    private void showSelectedMatchInfo(Match match) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Match Details");
        alert.setContentText("Home Team: " + match.getHomeTeam() + "\n" +
                "Away Team: " + match.getAwayTeam() + "\n" +
                "Status: " + match.getStatus()+"\n"+
                match.getLeague()+"\n"+
                match.getRound()+"\n"+
                "Goals: "+match.getGoals());
        alert.showAndWait();
    }

    void cancelPreviousOperation() {
        if (currentTask!=null && currentTask.isRunning())currentTask.cancel();
    }

}