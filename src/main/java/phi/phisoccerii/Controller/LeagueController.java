package phi.phisoccerii.Controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.LinksModel;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.team.Team;
import phi.phisoccerii.Model.team.TeamService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class LeagueController implements Initializable {

    private  FXMLLoader loader;
    private Parent root;

    @FXML private TableColumn<Team, Integer> rankCol;
    @FXML private TableColumn<Team, ImageView> logoCol;
    @FXML private TableColumn<Team, String> teamCol;
    @FXML private TableColumn<Team, Integer> playedCol;
    @FXML private TableColumn<Team, Integer> winCol;
    @FXML private TableColumn<Team, Integer> drawCol;
    @FXML private TableColumn<Team, Integer> loseCol;
    @FXML private TableColumn<Team,Integer> GDCol;
    @FXML private TableColumn<Team, Integer> pointsCol;
    @FXML private TableView<Team> standingTable;
    @FXML private BorderPane contentPane;

    @FXML private Label leagueNameLab;

    private int leagueId;
    private League league;

    @FXML
    void live(ActionEvent event)
    {
        switchTab("Live");
    }

    @FXML
    void matches(ActionEvent event)
    {
        switchTab("Matches");
    }

    @FXML
    void standing(ActionEvent event)
    {
        switchTab("Standing");
    }

    @FXML
    void topScorers(ActionEvent event)
    {
        switchTab("TopScorers");
    }


    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        standingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

        System.out.println("from league Controller constructor");
        //LinksModel model = new LinksModel();
        //setCells();
    }
    public void setCells()
    {
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        logoCol.setCellValueFactory(new PropertyValueFactory<>("logo"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        playedCol.setCellValueFactory(new PropertyValueFactory<>("matches"));
        playedCol.setCellValueFactory(new PropertyValueFactory<>("points"));
        GDCol.setCellValueFactory(new PropertyValueFactory<>("goalDiff"));
        winCol.setCellValueFactory(new PropertyValueFactory<>("win"));
        drawCol.setCellValueFactory(new PropertyValueFactory<>("draw"));
        loseCol.setCellValueFactory(new PropertyValueFactory<>("lose"));
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));

        GeneralService service = new GeneralService();
        ObservableList<Team>obsList = getObsList(service.getLeagueRoutes(leagueId ,"Standings" ));
        standingTable.setItems(obsList);
    }
    private ObservableList<Team> getObsList(String link)
    {
        List<Team> list= TeamService.getTeams(link);
                ObservableList<Team>obsList = FXCollections.observableArrayList(list);
                return obsList;
    }

    public void switchTab(String viewName)
    {
         loader = new FXMLLoader(App.class.getResource("View/League/"+viewName+"View.fxml"));
        try {
             root = loader.load();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
        contentPane.setCenter(root);

       // contentPane.getChildren().removeAll();
        //contentPane.getChildren().add(root);
    }
    private void setLeagueId(int leagueId)
    {
        this.leagueId = leagueId;
    }
    public void setLeague(League league)
    {
        this.league = league;
        setLeagueId(league.getId());
        leagueNameLab.setText(league.getName());
    }

    @FXML void Back(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(App.class.getResource("View/HomeView.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) ((Node)event.getSource()).getScene().getWindow();
        stage.setScene(scene);
        stage.setTitle("PHI Soccer");
        stage.show();

    }
}
