package phi.phisoccerii.Controller.League;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.player.Player;
import phi.phisoccerii.Model.player.PlayerService;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class TopScorersController implements Initializable, ILeagueController {

    boolean oneBYone, async;
    League league;
    private ObservableList<Player>playersObsList;

    GeneralService service = new GeneralService();

    @FXML private TableColumn<Player, Integer> rankCol;
    @FXML private TableColumn<Player, String > nameCol;
    @FXML private TableColumn<Player, String > teamCol;
    @FXML private TableColumn<Player, Integer> assistsCol;
    @FXML private TableColumn<Player, Integer> penaltiesCol;
    @FXML private TableColumn<Player, Integer> goalsCol;
    @FXML private TableView<Player> topScorersTable;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        topScorersTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
    }

    @Override
    public void declareTable() {
        rankCol.setCellValueFactory(new PropertyValueFactory<>("rank"));
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        teamCol.setCellValueFactory(new PropertyValueFactory<>("team"));
        assistsCol.setCellValueFactory(new PropertyValueFactory<>("assists"));
        penaltiesCol.setCellValueFactory(new PropertyValueFactory<>("penaltys"));
        goalsCol.setCellValueFactory(new PropertyValueFactory<>("goals"));

        //applyBoldTextStyle(nameCol);
    }

    @Override
    public void setUpTable() {
        Task<ObservableList<Player>> task = new Task<ObservableList<Player>>() {
            @Override
            protected ObservableList<Player> call() throws Exception {
                return getObsList(service.getLeagueRoutes(league.getId(), service.TOP_SCORERS));
            }
        };
        task.setOnSucceeded(e->topScorersTable.setItems(task.getValue()));
        new Thread(task).start();
    }

    private ObservableList<Player> getObsList(String link)
    {
        List<Player> list= PlayerService.getPlayers(link);
        ObservableList<Player>obsList = FXCollections.observableArrayList(list);
        return obsList;
    }


    @Override
    public void setLeague(League league) {
        this.league = league;
    }

    @Override
    public void setMethod(boolean oneBYone, boolean async) {
        this.oneBYone = oneBYone;
        this.async = async;
    }


}
