package phi.phisoccerii.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import phi.phisoccerii.Model.LinksModel;
import phi.phisoccerii.Model.team.Team;
import phi.phisoccerii.Model.team.TeamService;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

public class standingController implements Initializable{
    @FXML private TableColumn<Team, ImageView> logo;
    @FXML private TableColumn<Team, String > matches;
    @FXML private TableColumn<Team, String > points;
    @FXML private TableColumn<Team, String > rank;
    @FXML private TableColumn<Team, String >team;
    @FXML private TableColumn<Team, Integer>GD;
    @FXML private TableColumn<Team, Integer >w;
    @FXML private TableColumn<Team, Integer >d;
    @FXML private TableColumn<Team, Integer >l;
    @FXML private TableView<Team> table;

    @Override
    public void initialize(URL location, ResourceBundle resources) {


        System.out.println("from Standing Controller constructor");

        LinksModel model = new LinksModel();
        ObservableList<Team>ObsList = getObsList(model.getPremierLeagueLink());

        rank.setCellValueFactory(new PropertyValueFactory<>("rank"));
        logo.setCellValueFactory(new PropertyValueFactory<>("logo"));
        team.setCellValueFactory(new PropertyValueFactory<>("name"));
        matches.setCellValueFactory(new PropertyValueFactory<>("matches"));
        points.setCellValueFactory(new PropertyValueFactory<>("points"));
        GD.setCellValueFactory(new PropertyValueFactory<>("goalDiff"));
        w.setCellValueFactory(new PropertyValueFactory<>("win"));
        d.setCellValueFactory(new PropertyValueFactory<>("draw"));
        l.setCellValueFactory(new PropertyValueFactory<>("lose"));

        table.setItems(ObsList);


            /*HashMap<String ,String >leaguesMap=new HashMap<>();
            leaguesMap.put("Premier League",Model.getPremierLeagueLink());
            leaguesMap.put("Champions League",Model.getChampionsLeagueLink());
            leaguesMap.put("Egyptian League",Model.getEgyptianLeagueLink());
            ObservableList<String>leagues=FXCollections.observableArrayList();

            leagues.add()*/
    }

    private ObservableList<Team> getObsList(String link)
    {
        List<Team>list= TeamService.getTeams(link);
        ObservableList<Team>obsList = FXCollections.observableArrayList(list);
        return obsList;
    }
}