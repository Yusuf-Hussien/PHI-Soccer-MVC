package phi.phisoccerii.Controller;

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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.league.LeagueService;
import phi.phisoccerii.Model.team.Team;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.json.JSONArray;
import org.json.JSONObject;

import static phi.phisoccerii.Model.GeneralService.showAlert;
import static phi.phisoccerii.Model.match.MatchService.*;

public class HomeController implements Initializable {
    private FXMLLoader loader;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private List<String> leaguesNamesList;
    private List<String> teamsNamesList;

    private List<League>leaguesList;
    private List<Team>teamsList;

    private static ObservableList<String>leaguesObsList=FXCollections.observableArrayList();
    private ObservableList<Match>matchesObsList =FXCollections.observableArrayList();
    private ObservableList<String>teamsObsList;

    private static Map<String,League> leaguesMap=null;
    private Map<String,Integer> teamsMap;

    private FilteredList<String> leaguesFilList;
    private FilteredList<String> leaguesForMatchFilList;
    private FilteredList<String> teamsFilList;
    private FilteredList<Match> matchesFilList;

    private final GeneralService service = new GeneralService();

    private boolean asyncLogo = false, oneBYone=true;

    @FXML private CheckBox liveBtn;
    @FXML private ComboBox<String> searchBox;
    @FXML private TableColumn<Match, String> homeTeam;
    @FXML private TableColumn<Match, String> status;
    @FXML private TableColumn<Match, String> awayTeam;
    @FXML private TableColumn<Match, ImageView> awayLogo;
    @FXML private TableColumn<Match, ImageView> homeLogo;
    @FXML private TableView<Match> MatchesTable;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> leaguesBox;
    @FXML private TextField matchSearchBar;

    @FXML private Button todaybtn;
    @FXML private Button tomorrowbtn;
    @FXML private Button yesterdaybtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDatePicker();
        MatchesTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        setListsAsync();
        declareMatchesTable();
        setDateButtons();
        //if(leaguesList.isEmpty())
           setMatchesTable(service.getURL(service.LIVE),asyncLogo);
        //else restoreLists();
    }

    public static void restoreLists()
    {

    }

    @FXML
    void searchForLeague(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(!validateBox(searchBox))
            showAlert("invalid INPUT","Please Select Exist League");
        else
        {
        String leagueName = searchBox.getSelectionModel().getSelectedItem();
        League league = leaguesMap.get(leagueName);
        int leagueId = league.getId();
        //League league = new League(leagueName,leagueId);
        switchScene(event,"League");
        LeagueController controller2 = loader.getController();
                    controller2.setLeague(league);
                    controller2.setCells(true,true);
        }
    }



    @FXML
    void checkIsLive(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(liveBtn.isSelected())
        {
            setMatchesTable(service.getURL(service.LIVE),asyncLogo);
            datePicker.setValue(LocalDate.now());
            leaguesBox.setValue("");
        }
        else
        {
            String date = null;
            try{
             date =datePicker.getValue().toString();
            }catch (Exception e){}
            date = date==null? MatchService.getDayMatchesURL(0):date;
            setMatchesTable(MatchService.getDayMatchesURL(date),asyncLogo);
        }
    }

    private void setListsAsync()
    {
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching league data...");
                leaguesObsList = FXCollections.observableArrayList();
                leaguesList = LeagueService.getLeagues(service.getURL("Leagues"));
               leaguesNamesList = LeagueService.getLeaguesNames(leaguesList);
                //leaguesNamesList =  leaguesList.parallelStream().map(League::getName).toList(); //faster
                leaguesObsList.addAll(leaguesNamesList);
                leaguesFilList = new FilteredList<>(leaguesObsList,p->true);
                leaguesForMatchFilList = new FilteredList<>(leaguesObsList,p->true);
                if(leaguesList!=null)
                {
                    setMap();
                    Platform.runLater(()->{
                        setSearchBox(searchBox,leaguesFilList);
                        setSearchBox(leaguesBox,leaguesForMatchFilList);
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    private void setSearchBox(ComboBox<String>box,FilteredList<String>filteredList)
    {
        box.setItems(filteredList);
        box.setEditable(true);
        box.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate(item->{
                if(newValue==null || newValue.isEmpty())return true;
                return  item.toLowerCase().contains(newValue.toLowerCase());
            });
            box.hide();
            box.show();
        }));
        box.getEditor().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue) validateBox(box);
        }));
    }

    private boolean validateBox(ComboBox<String> box)
    {
        String input = box.getSelectionModel().getSelectedItem();
        if(input==null || input.isEmpty() || !box.getItems().contains(input))
        {
            box.getEditor().setText("");
            box.setValue("");
            return false;
        }
        return true;
    }


    private void setMap()
    {
        leaguesMap = LeagueService.getLeaguesMap(leaguesList);
    }


    private void switchScene(ActionEvent event,String selected)
    {
        try {
            loader = new FXMLLoader(App.class.getResource("View/"+selected+"/"+selected+"View.fxml"));
            root = loader.load();
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(selected);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
    }


    private void declareMatchesTable()
    {
        homeTeam.setCellValueFactory(new PropertyValueFactory<>("homeTeam"));
        awayTeam.setCellValueFactory(new PropertyValueFactory<>("awayTeam"));
        //status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getStatus()));
        setMatchStatus();
        homeLogo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getHomeLogo()));
        awayLogo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAwayLogo()));



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

    private void setMatchesTable(String url,boolean oneBYone ,boolean async)
    {
        if(oneBYone)setMatchesTable(url,async);
        else setMatchesTableAll(url);
    }

    private void setMatchesTable2(String url)
    {
        MatchService.getMatches1by1(url,matchesObsList, matchesFilList,MatchesTable);
    }

    private Task<Void>currentTask=null;
    private void setMatchesTable(String url,boolean async) {
        if(currentTask!=null && currentTask.isRunning()) currentTask.cancel();

        matchesObsList.clear();
        MatchesTable.refresh();
        matchesFilList = new FilteredList<>(matchesObsList, p -> true);
        MatchesTable.setItems(matchesFilList);

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
                        if(!isCancelled())
                        {
                            matchesObsList.add(match);
                            MatchesTable.refresh();
                        }
                    });
                }
                return null;
                }
        };
        new Thread(currentTask).start();
    }

    private void setMatchesTableAll(String url)
    {
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                List<Match>matches = MatchService.getMatches(url);
                matchesObsList = FXCollections.observableArrayList(matches);
                matchesFilList = new FilteredList<>(matchesObsList);
                Platform.runLater(()->{MatchesTable.setItems(matchesFilList);});

                //setLogos
                MatchService.setLogos(url,matchesObsList);
                Platform.runLater(() -> MatchesTable.refresh());
                return null;
            }
        };
        new Thread(task).start();
    }

    private void setMatchStatus()
    {
        status.setCellFactory(tc->new TableCell<>(){
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Match match = getTableView().getItems().get(getIndex());

                    // Status ->Bold
                    Label scoreLabel;
                    if(match.getScore().equals("-"))
                        scoreLabel = new Label(match.getTime());
                    else scoreLabel = new Label(match.getScore());
                    scoreLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");


                    Label roundLabel = new Label(match.getRound());
                    roundLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    Label leagueLabel = new Label(match.getLeague());
                    leagueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    Label statusLabel = new Label(match.getStatus());
                    if(match.getStatus().equals("Finished")) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: red;");
                    else if(match.getStatus().equals("Postponed") || match.getStatus().equals("After Pen.") ) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
                    else if( match.getStatus().equals("Half Time") ) statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: orange;");
                    else statusLabel.setStyle("-fx-font-size: 12px; -fx-text-fill:  #2ecc71;");

                    VBox vbox = new VBox( leagueLabel ,roundLabel ,scoreLabel,statusLabel);
                    vbox.setAlignment(Pos.CENTER);

                    setGraphic(vbox);
                }
            }
        });

    }

    @FXML
    private void onRowClicked()
    {
        Match selectedMatch = MatchesTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            showAlertt("Match Details",
                    "Home Team: " + selectedMatch.getHomeTeam() + "\n" +
                            "Away Team: " + selectedMatch.getAwayTeam() + "\n" +
                            "Status: " + selectedMatch.getStatus()+"\n"+
                            selectedMatch.getLeague()+"\n"+
                            selectedMatch.getRound()+"\n"+
                            "Goals: "+selectedMatch.getGoals());
        }
    }
    private void showAlertt(String title, String msg) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }
    private void showMatch(Match match)
    {

    }

    private void setDatePicker()
    {
        Locale.setDefault(Locale.ENGLISH);
        // Set the DatePicker format to English
        datePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            @Override
            public String toString(java.time.LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
    }

    @FXML
    void searchWithDate(ActionEvent event) {
        liveBtn.setSelected(false);
        String date = null;
        try {
             date = datePicker.getValue().toString();
        }catch (Exception e){
            showAlert("No Date Selected","Please Select date First!");
        }
        if(date!=null){
            String url = service.getURL(service.FIXTURES)+"&from="+date+"&to="+date;
            String league = leaguesBox.getSelectionModel().getSelectedItem();
            if(league!=null && !league.isEmpty()){
                int leagueId = leaguesMap.get(league).getId();
                url+= "&"+service.LEAGUE_ID+"="+leagueId;
            }
            setMatchesTable(url,asyncLogo);
        }
        //setTable("https://apiv2.allsportsapi.com/football/?APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede&met=Fixtures&from=2025-03-08&to=2025-03-08&leagueId=152");
    }
    private void setDateButtons()
    {
        yesterdaybtn.setOnAction(e->setDateButton(yesterdaybtn,-1));
        todaybtn.setOnAction(e->setDateButton(todaybtn,0));
        tomorrowbtn.setOnAction(e->setDateButton(tomorrowbtn,1));
    }
    private Button currButton = null;
    private void setDateButton(Button btn , int days)
    {
        liveBtn.setSelected(false);
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        currButton = btn;
        currButton.getStyleClass().add("pressed");
        setMatchesTable(MatchService.getDayMatchesURL(days),asyncLogo);
    }
}
