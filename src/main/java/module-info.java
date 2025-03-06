module phi.phisoccerii {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires org.kordamp.bootstrapfx.core;
    requires org.json;
    requires java.net.http;
    requires javafx.graphics;

    opens phi.phisoccerii to javafx.fxml;
    exports phi.phisoccerii;
    exports phi.phisoccerii.Controller;
    opens phi.phisoccerii.Controller to javafx.fxml;


    opens phi.phisoccerii.Model.team to javafx.base;
    opens phi.phisoccerii.Model.match to javafx.base;

}
