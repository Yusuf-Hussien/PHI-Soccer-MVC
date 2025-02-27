package phi.phisoccerii.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.LinksModel;
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


    @FXML
    private ComboBox<String> searchBox;

    @FXML
    void search(ActionEvent event) {
        if(!validateBox(searchBox))
        {
            showAlert("invalid INPUT","Please Select Exist League");
        }
        //System.out.println("Selected League: "+searchBox.getSelectionModel().getSelectedItem());
       // if(leaguesMap.containsKey(searchBox.getSelectionModel().getSelectedItem()));
        String leagueName = searchBox.getSelectionModel().getSelectedItem();
        int leagueId = leaguesMap.get(leagueName);
        League league = new League(leagueName,leagueId);
        switchScene(event,"League");
        LeagueController controller2 = loader.getController();
        //controller2.setLeagueId(leagueId);
        controller2.setLeague(league);
        controller2.setCells();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setLists();
        setMap();
        setSearchBox();
        System.out.println(leaguesMap);
        //System.out.println(GeneralService.getURL("Leagues"));
        //LinksModel model = new LinksModel();
        //System.out.println(model.getBASE_URL());
    }

    private void initalizeStreams()
    {
        leaguesObsList = FXCollections.observableArrayList();
        teamsObsList = FXCollections.observableArrayList();
    }
    private void setLists()
    {
        leaguesObsList = FXCollections.observableArrayList();
        teamsObsList = FXCollections.observableArrayList();

        //leaguesList = LeagueService.getLeagues(GeneralService.getURL("Leagues"));
        GeneralService service = new GeneralService();
        System.out.println(service.getLeagueRoutes(141, "Standings"));
        leaguesList = LeagueService.getLeagues(service.getURL("Leagues"));
        leaguesNamesList = LeagueService.getLeaguesNames(leaguesList);
        leaguesObsList.addAll(leaguesNamesList);
        leaguesFilList = new FilteredList<>(leaguesObsList,p->true);

        /*teamsList = TeamService.getTeams(GeneralService.getURL("Teams"));
        teamsNamesList = TeamService.getTeamsNames(teamsList);
        teamsObsList.addAll(teamsNamesList);
        teamsFilList = new FilteredList<>(teamsObsList,p->true);*/

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
    /*public void switchScene(ActionEvent event,int LeagueId)
    {
        try {
            loader = new FXMLLoader(App.class.getResource("View/"+selected+"/"+selected+".fxml"));
            root = loader.load();
            stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(selected);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
    }*/

}
