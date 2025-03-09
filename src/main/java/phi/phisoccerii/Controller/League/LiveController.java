package phi.phisoccerii.Controller.League;

import javafx.fxml.Initializable;
import phi.phisoccerii.Model.league.League;

import java.net.URL;
import java.util.ResourceBundle;

public class LiveController implements Initializable,IController{
    boolean oneBYone, async;
    League league;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

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

    @Override
    public void setUpTable() {

    }

}
