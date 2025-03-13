package phi.phisoccerii.Controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import phi.phisoccerii.App;
import phi.phisoccerii.Controller.Match.IMatchController;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.player.Player;

import java.net.URL;
import java.util.ResourceBundle;

public class MatchController implements Initializable {
    private Match match=null;

    private FXMLLoader loader;
    private Parent root;
    private IMatchController rootController;

    @FXML private BorderPane contentPane;
    @FXML private Label awayTeam;
    @FXML private Label homeTeam;
    @FXML private Label league;
    @FXML private Label round;
    @FXML private Label status;
    @FXML private Label score;
    @FXML private ImageView homeLogo;
    @FXML private ImageView awayLogo;
    @FXML private Button goalsbtn;
    @FXML private Button infobtn;
    @FXML private Button lineupbtn;
    private Button currButton = null;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    public void setMatch(Match match) {
        this.match = match;
        setLabels();
        setNavBtns();
        setNavBtn("Info",infobtn);
    }
    private void setLabels()
    {
        league.setText(match.getLeague());
        round.setText(match.getRound());
        //status.setText(match.getStatus());
        setStatus();
        homeTeam.setText(match.getHomeTeam());
        awayTeam.setText(match.getAwayTeam());
        homeLogo.setImage(match.getHomeLogo().getImage());
        awayLogo.setImage(match.getAwayLogo().getImage());
    }
    private void setStatus()
    {
        if(match.getScore().equals("-"))
        {
            score.setText(match.getTime());
            status.setText(match.getDate());
        }
        else
        {
            score.setText(match.getScore());
            status.setText(match.getStatus());
        }
        //score.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        if(match.getStatus().equals("Finished")) status.setStyle("-fx-text-fill: red;");
        else if(match.getStatus().equals("Postponed") || match.getStatus().equals("After Pen.") ) status.setStyle("-fx-font-size: 12px; -fx-text-fill: gray;");
        else if( match.getStatus().equals("Half Time") ) status.setStyle("-fx-text-fill: orange;");
        else if( status.getText().contains("-") ) status.setStyle("-fx-text-fill: gray;");
        else status.setStyle("-fx-text-fill:  #2ecc71;");

    }

    private void setNavBtns()
    {
       infobtn.setOnAction(e->setNavBtn("Info",infobtn));
       lineupbtn.setOnAction(e->setNavBtn("Lineups",lineupbtn));
       goalsbtn.setOnAction(e->setNavBtn("Goals",goalsbtn));
    }
    private void setNavBtn(String viewName, Button pressedBtn )
    {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        currButton = pressedBtn;
        currButton.getStyleClass().add("pressed");
        navigateButtons(viewName);
    }

    private void navigateButtons(String viewName)
    {
        loader =  new FXMLLoader(App.class.getResource("View/Match/"+viewName+"View.fxml"));
        try{
            root = loader.load();
            rootController = loader.getController();
            rootController.setMatch(match);
            contentPane.setCenter(root);
        }catch (Exception e){e.printStackTrace();System.out.println("ERROR Navigating!");}
    }

}
