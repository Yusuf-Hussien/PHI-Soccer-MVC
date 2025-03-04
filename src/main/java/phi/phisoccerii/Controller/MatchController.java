package phi.phisoccerii.Controller;

import javafx.fxml.Initializable;
import phi.phisoccerii.Model.match.Match;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchController implements Initializable {
    private Match match=null;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMatch(Match match) {
        this.match = match;
    }

}
