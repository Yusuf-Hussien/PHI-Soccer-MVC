package phi.phisoccerii.Controller;

import javafx.application.Platform;
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
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.team.Team;
import phi.phisoccerii.Model.team.TeamService;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static phi.phisoccerii.Model.GeneralService.fetchData;
import static phi.phisoccerii.Model.GeneralService.showPHIinfo;
import static phi.phisoccerii.Model.team.TeamService.getTeam;

public class LeagueController implements Initializable {

    private  FXMLLoader loader;
    private Parent root;

    private final GeneralService service = new GeneralService();

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
    @FXML private ImageView leagueLogo;

    private League league;
    private ObservableList<Team>standingObsList;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
        standingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        //setCells();
    }

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

    private void declareStandingTable()
    {
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        logoCol.setCellValueFactory(new PropertyValueFactory<>("logo"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        playedCol.setCellValueFactory(new PropertyValueFactory<>("matches"));
        GDCol.setCellValueFactory(new PropertyValueFactory<>("goalDiff"));
        winCol.setCellValueFactory(new PropertyValueFactory<>("win"));
        drawCol.setCellValueFactory(new PropertyValueFactory<>("draw"));
        loseCol.setCellValueFactory(new PropertyValueFactory<>("lose"));
        pointsCol.setCellValueFactory(new PropertyValueFactory<>("points"));
    }

    public void setCells(boolean oneBYone,boolean async)
    {
        if(oneBYone) setCells1by1(async);
        else setCellsAll(async);
    }

    public void setCellsAll(boolean async)
    {
        declareStandingTable();
        Task<ObservableList<Team>>task = new Task<ObservableList<Team>>() {
            @Override
            protected ObservableList<Team> call() throws Exception {
                ObservableList<Team>obsList = getObsList(service.getLeagueRoutes(league.getId() ,"Standings" ));
                return obsList;
            }
        };
        task.setOnSucceeded(e->{standingTable.setItems(task.getValue());});

        new Thread(task).start();
    }

    public void setCells1by1(boolean async)
    {
        declareStandingTable();
        standingObsList = FXCollections.observableArrayList();
        standingTable.setItems(standingObsList);

        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject json = fetchData(service.getLeagueRoutes(league.getId() ,"Standings" ));
                JSONObject result=null;
                try {
                    result = json.getJSONObject("result");
                }catch (Exception e){
                    //System.out.println("No Live Matches Now!");
                    System.out.println("ERROR GETTING TEAMS!!!!!!!!!!!\n ----Check TeamService Class----");
                }
                if (result == null) return null;
                JSONArray total = result.getJSONArray("total");
                for (int i=0 ;i<total.length();i++)
                {
                    Team team = getTeam(total.getJSONObject(i),async);  //Flag for Async
                    Platform.runLater(()->{
                        standingObsList.add(team);
                        standingTable.refresh();
                    });
                }

                return null;
            }
        };
        new Thread(task).start();
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


    public void setLeague(League league)
    {
        this.league = league;
        leagueNameLab.setText(league.getName());
        league.setLogo();
        leagueLogo.setImage(league.getLogo());
        leagueLogo.setFitHeight(50);leagueLogo.setFitWidth(50);leagueLogo.setPreserveRatio(true);
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

    @FXML
    public void PHI_clicked(MouseEvent event) {
        showPHIinfo();
    }

}
