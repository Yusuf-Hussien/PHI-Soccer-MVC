package phi.phisoccerii.Controller;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.league.LeagueService;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import org.json.JSONArray;
import org.json.JSONObject;

import static phi.phisoccerii.Model.GeneralService.*;
import static phi.phisoccerii.Model.match.MatchService.*;

public class HomeController implements Initializable {

    //For Switching Scene
    private FXMLLoader loader;
    private Stage stage;
    private Scene scene;
    private Parent root;

    private FXMLLoader MatchesLoader;
    private Parent matchesTable;
    private MatchesController matchesController;

    //Lists
    private List<String> leaguesNamesList;
    private List<League>leaguesList;
    //Observable Lists
    private ObservableList<String>leaguesObsList=FXCollections.observableArrayList();
    //Filtered Lists
    private FilteredList<String> leaguesFilList;
    private FilteredList<String> leaguesForMatchFilList;
    //map for getting id and data of selected league
    private  Map<String,League> leaguesMap=null;
    //For Services like Fetching data and more...
    private final GeneralService service = new GeneralService();
    private Task<Void>currentTask=null;  //Background task
    //flags for Setting Up matches on the table
    private boolean asyncLogo = true, oneBYone=true;

    //Needed Components
    @FXML private BorderPane contentPane;
    @FXML private CheckBox liveBtn;
    @FXML private ComboBox<String> leaguesSearchBox;
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> leaguesForMatchBox;
    @FXML private Button todaybtn;
    @FXML private Button tomorrowbtn;
    @FXML private Button yesterdaybtn;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setDatePicker();
        setListsAsync();
        setUpMatchesTable();
        matchesController.setUpTable(service.getURL(service.LIVE));
        setDaysButtons();

        Platform.runLater(()->{  //to cancel fetching data when exit app
            Stage currStage = (Stage)leaguesSearchBox.getScene().getWindow();
            if(currStage!=null)currStage.setOnCloseRequest(e->{matchesController.cancelPreviousOperation();});
        });
    }

    @FXML
    void searchForLeague(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(!validateBox(leaguesSearchBox))
            showAlert("invalid INPUT","Please Select Exist League");
        else
        {
            matchesController.cancelPreviousOperation();
            String leagueName = leaguesSearchBox.getSelectionModel().getSelectedItem();
            League league = leaguesMap.get(leagueName);
            switchScene("League");
            LeagueController controller2 = loader.getController();
            controller2.setLeague(league);
            controller2.setMethod(true,true);
            controller2.setStanding();
        }
    }

    @FXML
    void handelLiveButton(ActionEvent event) {
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        if(liveBtn.isSelected()) //if Selected get LIVE matches
        {
            matchesController.setUpTable(service.getURL(service.LIVE));
            datePicker.setValue(LocalDate.now());
            leaguesForMatchBox.setValue("");
        }
        else //if Deselected get Selected date from DatePicker
        {
            String date = null;
            try{
             date =datePicker.getValue().toString();
            }catch (Exception e){}
            //date = date==null? MatchService.getDayMatchesURL(0):date; //if there is NO date Selected in datepicker get Todays Matches
            if(date==null) todaybtn.fire();
            else matchesController.setUpTable(MatchService.getDayMatchesURL(date));
        }
    }

    @FXML
    void searchWithDate(ActionEvent event) {
        String date = null;
        try {
            date = datePicker.getValue().toString();
        }catch (Exception e){
            showAlert("No Date Selected","Please Select date First!");
        }
        if(date!=null){ //Format  -> Fixtures&from=2025-03-08&to=2025-03-08&leagueId=152
        liveBtn.setSelected(false);
            String url = service.getURL(service.FIXTURES)+"&from="+date+"&to="+date;
            String league = leaguesForMatchBox.getSelectionModel().getSelectedItem();
            if(league!=null && !league.isEmpty()){ //if there is league selected
                int leagueId = leaguesMap.get(league).getId();
                url+= "&"+service.LEAGUE_ID+"="+leagueId;
            }
            matchesController.setUpTable(url);
        }
    }

    private void setListsAsync()
    {
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching league data...");
                leaguesObsList = FXCollections.observableArrayList();
                leaguesList = LeagueService.getLeagues(service.getURL("Leagues"));
                //leaguesNamesList = LeagueService.getLeaguesNames(leaguesList);  // sync
                leaguesNamesList =  leaguesList.parallelStream().map(League::getName).toList(); // async
                leaguesObsList.addAll(leaguesNamesList);
                leaguesFilList = new FilteredList<>(leaguesObsList,p->true);
                leaguesForMatchFilList = new FilteredList<>(leaguesObsList,p->true);
                if(leaguesList!=null)
                {
                    setMap();
                    Platform.runLater(()->{
                        setSearchBox(leaguesSearchBox,leaguesFilList);
                        setSearchBox(leaguesForMatchBox,leaguesForMatchFilList);
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    private void setSearchBox(ComboBox<String>box,FilteredList<String>filteredList)
    {
        box.setItems(filteredList);
        box.setEditable(true);
        box.getEditor().textProperty().addListener(((observable, oldValue, newValue) -> {
            filteredList.setPredicate(item->{
                if(newValue==null || newValue.isEmpty())return true;
                return  item.toLowerCase().contains(newValue.toLowerCase());
            });
            box.hide();
            box.show();
        }));
        box.getEditor().focusedProperty().addListener(((observable, oldValue, newValue) -> {
            if(!newValue) validateBox(box);
        }));
    }


    private void setDatePicker()
    {
        Locale.setDefault(Locale.ENGLISH);
        // Set the DatePicker format to English
        datePicker.setConverter(new javafx.util.StringConverter<java.time.LocalDate>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd", Locale.ENGLISH);

            @Override
            public String toString(java.time.LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public java.time.LocalDate fromString(String string) {
                return (string != null && !string.isEmpty()) ? java.time.LocalDate.parse(string, formatter) : null;
            }
        });
    }

    private void setDaysButtons()
    {
        yesterdaybtn.setOnAction(e->setDayButton(yesterdaybtn,-1));
        todaybtn.setOnAction(e->setDayButton(todaybtn,0));
        tomorrowbtn.setOnAction(e->setDayButton(tomorrowbtn,1));
    }
    private Button currButton = null;
    private void setDayButton(Button btn , int days)
    {
        liveBtn.setSelected(false);
        if (currButton!=null)currButton.getStyleClass().remove("pressed");
        currButton = btn;
        currButton.getStyleClass().add("pressed");
        String url = MatchService.getDayMatchesURL(days);
        String selectedLeague = leaguesForMatchBox.getSelectionModel().getSelectedItem();
        if(selectedLeague != null&& !selectedLeague.isEmpty())
        {
           int selectedLeagueId = leaguesMap.get(selectedLeague).getId();
            url+= "&"+service.LEAGUE_ID+"="+selectedLeagueId;
        }
        matchesController.setUpTable(url);
    }

    private void setMap()
    {
        leaguesMap = LeagueService.getLeaguesMap(leaguesList);
    }

    private boolean validateBox(ComboBox<String> box)
    {
        String input = box.getSelectionModel().getSelectedItem();
        if(input==null || input.isEmpty() || !box.getItems().contains(input))
        {
            box.getEditor().setText("");
            box.setValue("");
            return false;
        }
        return true;
    }

    @FXML
    void PHI_clicked(MouseEvent event) {
        showPHIinfo();
    }

    private void switchScene(String selected)
    {
        try {
            loader = new FXMLLoader(App.class.getResource("View/"+selected+"/"+selected+"View.fxml"));
            root = loader.load();
            stage = (Stage)leaguesSearchBox.getScene().getWindow();  //loaded already
            scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(selected);
            stage.show();
        } catch (IOException e) {
            System.out.println("ERROR LOADING FXML FILE CHECK CONTROLLER!");
        }
    }

    private void setUpMatchesTable()
    {
         MatchesLoader = new FXMLLoader(App.class.getResource("View/MatchesView.fxml"));
        try{
            matchesTable = MatchesLoader.load();
            matchesController = MatchesLoader.getController();
            matchesController.setMethod(oneBYone,asyncLogo);
            matchesController.declareTable();
            Platform.runLater(()->contentPane.setCenter(matchesTable));
        } catch (Exception e) {
            System.out.println("Error Loading Matches Table!");
        }
    }

}
