package phi.phisoccerii.Controller.Match;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.GridPane;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.player.Player;

import java.io.IOException;
import java.util.List;

public class LineupsController  implements IMatchController{
    private Match match;
    private FXMLLoader lineUpLoader;
    @FXML private GridPane lineUpsPane;

    @Override
    public void setMatch(Match match) {
        this.match=match;
        setContent();
    }

    @Override
    public void setContent() {
        Parent homeLineup = loadLineUp(match.getHomeCoach(), match.getHomeFormat(), match.getHomeLineup());
        Parent awayLineup = loadLineUp(match.getAwayCoach(), match.getAwayFormat(), match.getAwayLineup());
        lineUpsPane.add(homeLineup,0,0);
        lineUpsPane.add(awayLineup,1,0);
    }

    private Parent loadLineUp(String coach, List<Integer> formation, List<Player>lineup)
    {
        lineUpLoader=new FXMLLoader(App.class.getResource("View/Match/LineupView.fxml"));
        Parent lineUp=null;
        try {
             lineUp = lineUpLoader.load();
             LineupController lineupController = lineUpLoader.getController();
             lineupController.setData(coach,formation,lineup);
        } catch (IOException e) {
            System.out.println("ERROR LOADING LINEUP!");
        }
        return lineUp;
    }
}