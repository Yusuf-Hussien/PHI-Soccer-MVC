package phi.phisoccerii.Controller.League;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.input.MouseEvent;
import phi.phisoccerii.Controller.IController;
import phi.phisoccerii.Model.league.League;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchesController implements Initializable, ILeagueController {
    private boolean oneBYone, async;
    private League league;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    @Override
    public void setMethod(boolean oneBYone, boolean async) {
        this.oneBYone = oneBYone;
        this.async = async;
    }

    @Override
    public void setLeague(League league) {

    }

    @Override
    public void declareTable() {

    }

    @Override
    public void setUpTable() {

    }

    @FXML
    public void onMatchClicked(MouseEvent mouseEvent) {
    }
}