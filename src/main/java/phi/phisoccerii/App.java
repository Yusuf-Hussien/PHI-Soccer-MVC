package phi.phisoccerii;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import phi.phisoccerii.Controller.League.MatchesController;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.league.LeagueService;
import phi.phisoccerii.Model.match.Match;
import phi.phisoccerii.Model.match.MatchService;
import phi.phisoccerii.Model.player.PlayerService;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CompletableFuture;

// v3 Branch
public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {


        /*FXMLLoader MatchesLoader = new FXMLLoader(App.class.getResource("View/League/MatchesView.fxml"));
        Parent matchesTable= null;
        final MatchesController matchesController ;
        try{
            matchesTable = MatchesLoader.load();
        } catch (Exception e) {
            System.out.println("Error Loading Matches Table!");
        }
        matchesController = MatchesLoader.getController();
        matchesController.setMethod(true,false);
        matchesController.declareTable();
        matchesController.setLeague(new League("test",152,null));
        matchesController.setUpTable();
        Scene scene = new Scene(matchesTable);


         */
        //MatchesController finalMatchesController1 = matchesController;
       /* Task<Void>task=new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(500000);
                String url2 = "https://apiv2.allsportsapi.com/football/?met=Fixtures&APIkey=2dab65248f3aa20cfd3443712133ec1b37d00b40e45098fec59818ef713be4a9&from=2025-03-12&to=2025-03-12&timezone=Africa/cairo&leagueId=3";
                Platform.runLater(()->
                {
                    if(matchesController!=null)
                        matchesController.setUpTable(url2);
                });
                return null;
            }
        };
        new Thread(task).start();*/

        /*FXMLLoader MatchesLoader = new FXMLLoader(App.class.getResource("View/MatchesView.fxml"));
        Parent matchesTable= null;
        final MatchesController matchesController ;
        try{
            matchesTable = MatchesLoader.load();
        } catch (Exception e) {
            System.out.println("Error Loading Matches Table!");
        }
            matchesController = MatchesLoader.getController();
            matchesController.setMethod(true,false);
            matchesController.declareTable();
            matchesController.setUpTable("https://apiv2.allsportsapi.com/football/?met=Fixtures&APIkey=2dab65248f3aa20cfd3443712133ec1b37d00b40e45098fec59818ef713be4a9&from=2025-03-11&to=2025-03-11&timezone=Africa/cairo&leagueId=3");
        Scene scene = new Scene(matchesTable);
        //MatchesController finalMatchesController1 = matchesController;
        Task<Void>task=new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                Thread.sleep(5000);
                String url2 = "https://apiv2.allsportsapi.com/football/?met=Fixtures&APIkey=2dab65248f3aa20cfd3443712133ec1b37d00b40e45098fec59818ef713be4a9&from=2025-03-12&to=2025-03-12&timezone=Africa/cairo&leagueId=3";
                Platform.runLater(()->
                {
                    if(matchesController!=null)
                    matchesController.setUpTable(url2);
                });
                return null;
            }
        };
        new Thread(task).start();*/


       FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("View/HomeView.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("PHI Soccer");
        stage.getIcons().add(new Image(getClass().getResourceAsStream("logo.png")));
        stage.setScene(scene);
        stage.show();
        stage.setOnCloseRequest(e->{Platform.exit();});
    }

    public static void main(String[] args) {


        //System.out.println(service.getLeagueRoutes(152,"Standings"));
        //System.out.println(service.getURL("Livescore"));
        //List<Match>matches = MatchService.getMatches("https://apiv2.allsportsapi.com/football/?APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede&met=Livescore");
       // System.out.println(matches);
         //List<List<ImageView>>logos = MatchService.getLogos(service.getURL("Livescore"));
        // List<List<ImageView>>logos = MatchService.getLogos("https://apiv2.allsportsapi.com/football/?APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede&met=Fixtures&from=2021-05-18&to=2021-05-18");
          // System.out.println(logos);

        //System.out.println(GeneralService.from24Hto12H("22:05"));

        /*GeneralService service = new GeneralService();
        System.out.println( PlayerService.getPlayers(service.getLeagueRoutes(141,service.TOP_SCORERS)));*/
        launch();


    }
    private static void test()
    {
        /* System.out.println("before");
        String url = "https://apiv2.allsportsapi.com/football/?met=Leagues&APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede";
        long startSync = System.nanoTime();
        //List<League>leagues = LeagueService.getLeaguesAsync(url).join();
        //System.out.println(leagues);
        LeagueService.getLeaguesAsync(url).thenAccept(l->{
            System.out.println("JsonNode"+l);
        });

        List<League>leagues2 = LeagueService.getLeagues(url);
        System.out.println("JSONObject"+leagues2);

        System.out.println("after");*/

        System.out.println("before");
        String url = "https://apiv2.allsportsapi.com/football/?met=Leagues&APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede";

        // Measure time for synchronous method
        long startSync = System.nanoTime();
        List<League> leaguesSync = LeagueService.getLeagues(url);
        long endSync = System.nanoTime();
        System.out.println("JSONObject (Sync) Time: " + (endSync - startSync) / 1_000_000.0 + " ms");
        //System.out.println("JSONObject " + leaguesSync);
        // Measure time for asynchronous method
        long startAsync = System.nanoTime();
        CompletableFuture<List<League>> leaguesAsync = LeagueService.getLeaguesAsync(url);

        List<League> leaguesAsync2 = LeagueService.getLeaguesAsync(url).join();
        long endAsync = System.nanoTime();
        System.out.println("JsonNode (Async) Time: " + (endAsync - startAsync) / 1_000_000.0 + " ms");
        /*leaguesAsync.thenAccept(l -> {
            long endAsync = System.nanoTime();
            System.out.println("JsonNode (Async) Time: " + (endAsync - startAsync) / 1_000_000.0 + " ms");
            //System.out.println("JsonNode " + l);
        });*/


        System.out.println("after");
    }
}