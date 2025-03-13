package phi.phisoccerii.Controller.League;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.App;
import phi.phisoccerii.Controller.MatchController;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static java.lang.Thread.sleep;
import static phi.phisoccerii.Model.match.MatchService.getMatch;

public class MatchesController implements Initializable, ILeagueController {

    private FXMLLoader matchLoader;
    private Parent matchPane;
    private MatchController matchController;
    private Stage matchStage=new Stage();

    private boolean oneBYone, async;
    private League league=null;
    private String url;
    private int startDay=0;
    private static Task<Void>currentTask=null;

    private ObservableList<Match> matchesObsList =FXCollections.observableArrayList();
    private FilteredList<Match> matchesFilList;
    private final GeneralService service = new GeneralService();

    @FXML private TextField matchSearchBar;
    @FXML private CheckBox liveBtn;
    @FXML private TableColumn<Match, String> homeCol;
    @FXML private TableColumn<Match, ImageView> homeLogoCol;
    @FXML private TableColumn<Match, String> statusCol;
    @FXML private TableColumn<Match, ImageView> awayLogoCol;
    @FXML private TableColumn<Match, String> awayCol;
    @FXML private TableView<Match> matchesTable;

    public static final Label loadingMatchesLab = new Label("Matches Loading!");
    public static final Label noMatchesLab = new Label("No Matches on that Weak!");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        matchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        loadingMatchesLab.setStyle("-fx-text-fill:green;");
        noMatchesLab.setStyle("-fx-text-fill:red;");
        liveBtn.setSelected(false);
        Platform.runLater(()->{  //to cancel fetching data when exit app
            Stage stage = (Stage)liveBtn.getScene().getWindow();
            if(stage!=null)stage.setOnCloseRequest(e->{cancelPreviousOperation();});
        });
    }
    @Override
    public void setLeague(League league) {
        this.league = league;
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


                    VBox vbox = new VBox(roundLabel ,scoreLabel,statusLabel);
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
        matchesTable.setPlaceholder(loadingMatchesLab);
        if(url==null) setURL(service.getLeagueRoutes(league.getId(),service.FIXTURES+MatchService.getWeakMatchesURL(startDay)));
        if(oneBYone)setMatchesTable1by1();
        else setMatchesTableAll();
        checkFailedLoading();
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
            //showSelectedMatchInfo(selectedMatch);
            matchLoader =  new FXMLLoader(App.class.getResource("View/MatchView.fxml"));
            try{
                matchPane = matchLoader.load();
                matchController = matchLoader.getController();
                matchController.setMatch(selectedMatch);
                matchStage.setScene(new Scene(matchPane));
                matchStage.setTitle(selectedMatch.getHomeTeam()+" - "+selectedMatch.getAwayTeam());
                matchStage.show();
            }catch (Exception e){System.out.println("ERROR LOADING MATCH!");}
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
                "Goals: "+match.getGoals()+"\n"+
                "\n"+match.getHomeCoach()+"\n"+
                "\n"+match.getAwayCoach()+"\n"+
                match.getHomeLineup()+"\n"+
                match.getAwayLineup());
        alert.showAndWait();
    }

    private void cancelPreviousOperation() {
        if (currentTask!=null && currentTask.isRunning())currentTask.cancel();
    }

    @FXML
    void nextWeak(ActionEvent event) {
        url=null;
        startDay+=7;
        setUpTable();
    }

    @FXML
    void prevWeak(ActionEvent event) {
        url=null;
        startDay-=7;
        setUpTable();
    }

    @FXML
    void fullSeason(ActionEvent event) {
        startDay=0;
        setURL(service.getLeagueRoutes(league.getId(), service.FIXTURES+MatchService.getSeasonMatchesURL(0)));
        setUpTable();
    }

    public void handelLiveButton(ActionEvent event) {
        if(liveBtn.isSelected())
        {
            setURL(service.getLeagueRoutes(league.getId(), service.LIVE));
            setUpTable();
        }
        else {
            setURL(service.getLeagueRoutes(league.getId(),service.FIXTURES+MatchService.getWeakMatchesURL(0)));
            setUpTable();
        }
    }
    private void checkFailedLoading()
    {
        new Thread(()->{
            try {
                sleep(3500);
            } catch (InterruptedException e) {}
            Platform.runLater(()->{if(matchesObsList.isEmpty())matchesTable.setPlaceholder(noMatchesLab);});
        }).start();
    }
}