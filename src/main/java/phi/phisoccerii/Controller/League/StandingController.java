package phi.phisoccerii.Controller.League;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.team.Team;
import phi.phisoccerii.Model.team.TeamService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import static phi.phisoccerii.Model.GeneralService.applyBoldTextStyle;
import static phi.phisoccerii.Model.GeneralService.fetchData;
import static phi.phisoccerii.Model.team.TeamService.getTeam;

public class StandingController implements Initializable , ILeagueController {

    private League league;
    private boolean oneBYone,async;
    private ObservableList<Team>standingObsList;

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


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        standingTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }


    @Override
    public void declareTable()
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

        applyBoldTextStyle(teamCol);
        applyBoldTextStyle(rankCol);
        applyBoldTextStyle(pointsCol);

    }

    public void setUpTable()
    {
        if(oneBYone) setCells1by1(async);
        else setCellsAll(async);
    }

    public void setCellsAll(boolean async)
    {
        declareTable();
        Task<ObservableList<Team>> task = new Task<ObservableList<Team>>() {
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
        declareTable();
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

    @Override
    public void setLeague(League league)
    {
        this.league = league;
    }

    @Override
    public void setMethod(boolean oneBYone, boolean async) {
        this.oneBYone = oneBYone;
        this.async = async;
    }
}
