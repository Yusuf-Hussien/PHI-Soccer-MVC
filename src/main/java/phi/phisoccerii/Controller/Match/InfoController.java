package phi.phisoccerii.Controller.Match;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import phi.phisoccerii.Model.match.Match;

public class InfoController  implements IMatchController{
    Match match;

    @FXML private Label judg;
    @FXML private Label satdium;

    @Override
    public void setMatch(Match match) {
        this.match = match;
        setContent();
    }
    public void setContent()
    {
        judg.setText("Judge: "+match.getJudg());
        satdium.setText("Stadium: "+match.getStadium());
    }
}