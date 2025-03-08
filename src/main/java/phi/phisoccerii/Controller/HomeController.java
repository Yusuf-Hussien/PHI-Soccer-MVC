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
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.league.LeagueService;

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
import static phi.phisoccerii.Model.GeneralService.showPHIinfo;
import static phi.phisoccerii.Model.match.MatchService.*;

public class HomeController implements Initializable {

    //For Switching Scene
    private FXMLLoader loader;
    private Stage stage;
    private Scene scene;
    private Parent root;

    //Lists
    private List<String> leaguesNamesList;
    private List<League>leaguesList;
    //Observable Lists
    private ObservableList<String>leaguesObsList=FXCollections.observableArrayList();
    private ObservableList<Match>matchesObsList =FXCollections.observableArrayList();
    //Filtered Lists
    private FilteredList<String> leaguesFilList;
    private FilteredList<String> leaguesForMatchFilList;
    private FilteredList<Match> matchesFilList;
    //map for getting id and data of selected league
    private  Map<String,League> leaguesMap=null;
    //For Services like Fetching data and more...
    private final GeneralService service = new GeneralService();
    private Task<Void>currentTask=null;  //Background task
    //flags for Setting Up matches on the table
    private boolean asyncLogo = false, oneBYone=true;

    //Needed Components
    @FXML private CheckBox liveBtn;
    @FXML private ComboBox<String> leaguesSearchBox;
    @FXML private TableColumn<Match, String> homeTeam;
    @FXML private TableColumn<Match, String> status;
    @FXML private TableColumn<Match, String> awayTeam;
    @FXML private TableColumn<Match, ImageView> awayLogo;
    @FXML private TableColumn<Match, ImageView> homeLogo;
    @FXML private TableView<Match> MatchesTable;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> leaguesForMatchBox;
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
        setDaysButtons();
        setMatchesTable(service.getURL(service.LIVE),oneBYone,asyncLogo);

        Platform.runLater(()->{  //to cancel fetching data when exit app
            stage = (Stage)todaybtn.getScene().getWindow();
            if(stage!=null)stage.setOnCloseRequest(e->{cancelFetchingMatches();});
        });
    }

    @FXML
    void searchForLeague(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(!validateBox(leaguesSearchBox))
            showAlert("invalid INPUT","Please Select Exist League");
        else
        {
            cancelFetchingMatches();
            String leagueName = leaguesSearchBox.getSelectionModel().getSelectedItem();
            League league = leaguesMap.get(leagueName);
            switchScene("League");
            LeagueController controller2 = loader.getController();
            controller2.setLeague(league);
            controller2.setCells(true,true);
        }
    }

    @FXML
    void handelLiveButton(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(liveBtn.isSelected()) //if Selected get LIVE matches
        {
            setMatchesTable(service.getURL(service.LIVE),oneBYone,asyncLogo);
            datePicker.setValue(LocalDate.now());
            leaguesForMatchBox.setValue("");
        }
        else //if Deselected get Selected date from DatePicker
        {
            String date = null;
            try{
             date =datePicker.getValue().toString();
            }catch (Exception e){}
            date = date==null? MatchService.getDayMatchesURL(0):date; //if there is NO date Selected in datepicker get Todays Matches
            setMatchesTable(MatchService.getDayMatchesURL(date),oneBYone,asyncLogo);
        }
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
        if(date!=null){ //Format  -> Fixtures&from=2025-03-08&to=2025-03-08&leagueId=152
            String url = service.getURL(service.FIXTURES)+"&from="+date+"&to="+date;
            String league = leaguesForMatchBox.getSelectionModel().getSelectedItem();
            if(league!=null && !league.isEmpty()){ //if there is league selected
                int leagueId = leaguesMap.get(league).getId();
                url+= "&"+service.LEAGUE_ID+"="+leagueId;
            }
            setMatchesTable(url,oneBYone,asyncLogo);
        }
    }

    @FXML
    private void onMatchClicked()
    {
        Match selectedMatch = MatchesTable.getSelectionModel().getSelectedItem();
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

    private void setListsAsync()
    {
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching league data...");
                leaguesObsList = FXCollections.observableArrayList();
                leaguesList = LeagueService.getLeagues(service.getURL("Leagues"));
                //leaguesNamesList = LeagueService.getLeaguesNames(leaguesList);  // sync
                leaguesNamesList =  leaguesList.parallelStream().map(League::getName).toList(); // async
                leaguesObsList.addAll(leaguesNamesList);
                leaguesFilList = new FilteredList<>(leaguesObsList,p->true);
                leaguesForMatchFilList = new FilteredList<>(leaguesObsList,p->true);
                if(leaguesList!=null)
                {
                    setMap();
                    Platform.runLater(()->{
                        setSearchBox(leaguesSearchBox,leaguesFilList);
                        setSearchBox(leaguesForMatchBox,leaguesForMatchFilList);
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

    private void setMatchesTable(String url,boolean oneBYone ,boolean async)
    {
        if(oneBYone)setMatchesTable1by1(url,async);
        else setMatchesTableAll(url);
    }

    private void setMatchesTable1by1(String url,boolean async) {
        if(currentTask!=null && currentTask.isRunning()) currentTask.cancel(); //if there is new task ordered cancel previous

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
                        if(!isCancelled())  //if there is new task ordered
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

    private void setDaysButtons()
    {
        yesterdaybtn.setOnAction(e->setDayButton(yesterdaybtn,-1));
        todaybtn.setOnAction(e->setDayButton(todaybtn,0));
        tomorrowbtn.setOnAction(e->setDayButton(tomorrowbtn,1));
    }
    private Button currButton = null;
    private void setDayButton(Button btn , int days)
    {
        liveBtn.setSelected(false);
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        currButton = btn;
        currButton.getStyleClass().add("pressed");
        String url = MatchService.getDayMatchesURL(days);
        String selectedLeague = leaguesForMatchBox.getSelectionModel().getSelectedItem();
        if(selectedLeague != null&& !selectedLeague.isEmpty())
        {
           int selectedLeagueId = leaguesMap.get(selectedLeague).getId();
            url+= "&"+service.LEAGUE_ID+"="+selectedLeagueId;
        }
        setMatchesTable(url,oneBYone,asyncLogo);
    }

    private void cancelFetchingMatches() {
        if (currentTask!=null && currentTask.isRunning())currentTask.cancel();
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

    private void setMap()
    {
        leaguesMap = LeagueService.getLeaguesMap(leaguesList);
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

    @FXML
    void PHI_clicked(MouseEvent event) {
        showPHIinfo();
    }

    private void switchScene(String selected)
    {
        try {
            loader = new FXMLLoader(App.class.getResource("View/"+selected+"/"+selected+"View.fxml"));
            root = loader.load();
            //stage = (Stage) ((Node)event.getSource()).getScene().getWindow();  //loaded already
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(selected);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
    }

}
