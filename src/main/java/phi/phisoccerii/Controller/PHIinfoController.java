package phi.phisoccerii.Controller;

import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

public class PHIinfoController {

    @FXML
    void openMyLinkedIn(MouseEvent event) {
        openLink("www.linkedin.com/in/yusuf-7ussien");
    }

    @FXML
    void openProjectRepo(MouseEvent event) {
        openLink("https://github.com/Yusuf-Hussien/PHI-Soccer-MVC");
    }

    private void openLink(String url) {
        try {
            Desktop.getDesktop().browse(URI.create(url));
        } catch (IOException e) {}
    }
}
