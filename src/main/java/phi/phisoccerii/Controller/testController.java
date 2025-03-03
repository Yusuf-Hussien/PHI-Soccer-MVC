package phi.phisoccerii.Controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;

import javax.swing.text.DateFormatter;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.ResourceBundle;

public class testController implements Initializable {
    @FXML
    private TextField input;

    @FXML
    private Label result;

    @FXML
    private ProgressBar bar;
    @FXML
    private Label time;


    @FXML
    void calc(ActionEvent event)
    {
        if(timeTask.isRunning() && timeTask!=null)
        timeTask.cancel();

        Task<Long>task= new Task<Long>() {
            @Override
            protected Long call() throws Exception {
                long sum=0;
                long n = Long.parseLong(input.getText());
                for(long i=0;i<=n;i++)
                {
                    sum+=i;
                    long finalSum = sum;
                    Platform.runLater(() ->{result.setText(String.valueOf(finalSum));});
                    updateProgress(i,n);
                    Thread.sleep(1);
                }
                return sum;
            }
        };
        bar.progressProperty().bind(task.progressProperty());
        task.setOnSucceeded(e->{
            result.setText(String.valueOf(task.getValue()));
             bar.progressProperty().unbind();
             bar.setStyle("-fx-border-color:green;");
            //bar.setProgress(1.0);
        });
        task.setOnFailed(e->{
            System.out.println("ERROR!!");
        });
        new Thread(task).start();
    }


    Task<Void>timeTask;
    @FXML
    void showTime(ActionEvent event) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm:ss");
        timeTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (true)
                {
                LocalDateTime date = LocalDateTime.now();
                Platform.runLater(()->{time.setText(date.format(formatter));});
                Thread.sleep(1000);
                }
            }
        };
        timeTask.setOnCancelled(c->{time.setText("Stopped");});
        new Thread(timeTask).start();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        bar.setProgress(0);
        Date date = new Date();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh-mm-ss"); //yyyy-MM-dd
        System.out.println(now.format(formatter));

    }
}
