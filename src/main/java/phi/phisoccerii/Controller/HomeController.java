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
import phi.phisoccerii.Model.LinksModel;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;
import phi.phisoccerii.Model.player.Player;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.league.LeagueService;
import phi.phisoccerii.Model.team.Team;
import phi.phisoccerii.Model.team.TeamService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

public class HomeController implements Initializable {
    private FXMLLoader loader;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private List<String> leaguesNamesList;
    private List<String> teamsNamesList;

    private List<League>leaguesList;
    private List<Team>teamsList;

    private ObservableList<String>leaguesObsList;
    private ObservableList<String>teamsObsList;

    private Map<String,Integer> leaguesMap;
    private Map<String,Integer> teamsMap;

    private FilteredList<String> leaguesFilList;
    private FilteredList<String> teamsFilList;


    @FXML private ComboBox<String> searchBox;
    @FXML private TableColumn<Match, String> homeTeam;
    @FXML private TableColumn<Match, String> status;
    @FXML private TableColumn<Match, String> awayTeam;
    @FXML private TableColumn<Match, ImageView> awayLogo;
    @FXML private TableColumn<Match, ImageView> homeLogo;
    @FXML private TableView<Match> liveTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        liveTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        setListsAsync();
        setLiveTable();

    }

    @FXML
    void search(ActionEvent event) {
        if(!validateBox(searchBox))
            showAlert("invalid INPUT","Please Select Exist League");
        else
        {
        String leagueName = searchBox.getSelectionModel().getSelectedItem();
        int leagueId = leaguesMap.get(leagueName);
        League league = new League(leagueName,leagueId);
        switchScene(event,"League");
        LeagueController controller2 = loader.getController();
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Platform.runLater(()->{
                    controller2.setLeague(league);
                    controller2.setCells();
                });
                return null;
            }
        };
        new Thread(task).start();
        }
    }

    private void setListsAsync()
    {
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching league data...");
                leaguesObsList = FXCollections.observableArrayList();
                GeneralService service = new GeneralService();
                leaguesList = LeagueService.getLeagues(service.getURL("Leagues"));
                //leaguesNamesList = LeagueService.getLeaguesNames(leaguesList);
                leaguesNamesList =  leaguesList.parallelStream().map(League::getName).toList(); //faster
                leaguesObsList.addAll(leaguesNamesList);
                leaguesFilList = new FilteredList<>(leaguesObsList,p->true);
                if(leaguesList!=null)
                {
                    setMap();
                    Platform.runLater(()->{setSearchBox();});
                }
                return null;
            }
        };
        new Thread(task).start();
    }
    private void setSearchBox()
    {
        searchBox.setItems(leaguesFilList);
        searchBox.setEditable(true);
        searchBox.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            leaguesFilList.setPredicate(item->{
                if(newValue==null || newValue.isEmpty())return true;
                return  item.toLowerCase().contains(newValue.toLowerCase());
            });
            searchBox.hide();
            searchBox.show();
        }));
        searchBox.getEditor().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue) validateBox(searchBox);
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
    private void showAlert(String title,String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
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


    private void setLiveTable()
    {
        homeTeam.setCellValueFactory(new PropertyValueFactory<>("homeTeam"));
        awayTeam.setCellValueFactory(new PropertyValueFactory<>("awayTeam"));
        //status.setCellValueFactory(new PropertyValueFactory<>("status"));
        status.setCellValueFactory(cellData-> new SimpleStringProperty(cellData.getValue().getStatus()));
        setStatus();
        homeLogo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getHomeLogo()));
        awayLogo.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getAwayLogo()));

        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                GeneralService service = new GeneralService();
                String url =service.getURL("Livescore");
                List<Match>matches = MatchService.getMatches(url);
                ObservableList<Match>obsList = FXCollections.observableArrayList(matches);
                Platform.runLater(()->{liveTable.setItems(obsList);});

                //setLogos
                MatchService.setLogos(url,obsList);
                Platform.runLater(() -> liveTable.refresh());
                return null;
            }
        };
        new Thread(task).start();
    }
    private void setStatus()
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
                    Label statusLabel = new Label(match.getStatus());
                    statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");


                    Label roundLabel = new Label(match.getRound());
                    roundLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    Label leagueLabel = new Label(match.getLeague());
                    leagueLabel.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");

                    VBox vbox = new VBox( leagueLabel ,statusLabel,roundLabel);
                    vbox.setAlignment(Pos.CENTER);

                    setGraphic(vbox);
                }
            }
        });

    }

    @FXML
    private void onRowClicked()
    {
        Match selectedMatch = liveTable.getSelectionModel().getSelectedItem();
        if (selectedMatch != null) {
            showAlertt("Match Details",
                    "Home Team: " + selectedMatch.getHomeTeam() + "\n" +
                            "Away Team: " + selectedMatch.getAwayTeam() + "\n" +
                            "Status: " + selectedMatch.getStatus());
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

}
