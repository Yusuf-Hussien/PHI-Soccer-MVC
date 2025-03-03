package phi.phisoccerii;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/League/LeagueView.fxml"));
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/HomeView.fxml"));
        //FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/test.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PHI Soccer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setScene(scene);
        stage.show();
    }
    private static Properties prop;
    private static String PREMIER_LEAGUE_LINK;
    private static String CHAMPIONS_LEAGUE_LINK;
    private static String EGYPTIAN_LEAGUE_LINK;
    private static String LEAGUES_LINK;
    public static void main(String[] args) {

        launch();

    }
}