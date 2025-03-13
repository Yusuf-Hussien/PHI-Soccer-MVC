package phi.phisoccerii;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import java.io.IOException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
       FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/HomeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PHI Soccer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e->{Platform.exit();});
    }

    public static void main(String[] args) {
        launch();
    }
}