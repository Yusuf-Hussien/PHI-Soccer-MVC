package phi.phisoccerii.Controller;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Controller.League.ILeagueController;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static phi.phisoccerii.Model.GeneralService.showPHIinfo;

public class LeagueController implements Initializable , ILeagueController {

    private  FXMLLoader loader;
    private Parent root;

    private final GeneralService service = new GeneralService();

    @FXML private BorderPane contentPane;
    @FXML private Label leagueNameLab;
    @FXML private ImageView leagueLogo;

    private League league;
    boolean oneBYone,  async;

    @Override
    public void initialize(URL location, ResourceBundle resources)
    {
    }
    public void setStanding()
    {
        standing(new ActionEvent());
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


    public void switchTab(String viewName)
    {
         loader = new FXMLLoader(App.class.getResource("View/League/"+viewName+"View.fxml"));
        try {
             root = loader.load();
             ILeagueController controller = loader.getController();
             controller.setLeague(league);
             controller.setMethod(oneBYone,async);
             controller.declareTable();
             controller.setUpTable();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
        //Platform.runLater(()->contentPane.setCenter(root));
        contentPane.setCenter(root);
    }


    @Override
    public void setLeague(League league)
    {
        this.league = league;
        leagueNameLab.setText(league.getName());
        league.setLogo();
        leagueLogo.setImage(league.getLogo());
        leagueLogo.setFitHeight(50);leagueLogo.setFitWidth(50);leagueLogo.setPreserveRatio(true);
    }

    @Override
    public void setMethod(boolean oneBYone, boolean async) {
        this.oneBYone = oneBYone;
        this.async = async;
    }

    @Override
    public void declareTable() {

    }

    @Override
    public void setUpTable() {}

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
