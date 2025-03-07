package phi.phisoccerii.Model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.player.Player;
import phi.phisoccerii.Model.team.Team;

import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.HashMap;
import java.util.Map;

import static phi.phisoccerii.Model.team.TeamService.getJSONarr;

public class GeneralService {
    private static Properties prop;
    //private String LEAGUES, TEAMS, PLAYERS;

   // private static final HttpClient client =HttpClient.newHttpClient();
    private static final HashMap<String ,JSONObject> cached = new HashMap<>();

    //API parameters
    public final String LIVE = "Livescore"
            ,FIXTURES = "Fixtures"
            ,LEAGUE_ID = "leagueId";
    public GeneralService()
    {
        setProp();
    }

    private static void setProp()
    {
        prop = new Properties();
        try {
            InputStream input = App.class.getResourceAsStream("config.properties");
            if(input==null){
                System.out.println("ERROR LOADING config.properties");
                return;
            }
            prop.load(input);
        } catch (
                IOException e) {
            System.out.println("Error getting property file");;
        }
        //LEAGUES = prop.getProperty("leagues");
        //TEAMS = prop.getProperty("teams");
        //PLAYERS = prop.getProperty("players");

    }
    public String getURL(String type)
    {
        String Base="" ;
        try{
           Base = prop.getProperty("BASE_URL");
        }catch (Exception e){
            System.out.println("failed Getting BASE_URL");
        }
        return Base+type;
    }
    public String getLeagueRoutes(int leagueId, String type)
    {
        String Base="" ;
        try{
            Base = prop.getProperty("BASE_URL");
        }catch (Exception e){
            System.out.println("failed Getting BASE_URL");
        }
        return Base+type+"&leagueId="+leagueId;
    }

    public static String from24Hto12H(String time24H)
    {
        LocalTime time = LocalTime.parse(time24H,DateTimeFormatter.ofPattern("HH:mm"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
        String time12H = time.format(formatter);
        return time12H;
    }

    /*public static <T>List<String>getNames(List<T>objList)
    {
        List<String>names=new ArrayList<>();
        for(T t: objList) names.add(t.getName());
        return names;
    }*/

    public static<T>ObservableList<T>getObsList(List<T>list)
    {
        ObservableList<T> obsList = FXCollections.observableArrayList();
        obsList.addAll(list);
        return obsList;
    }

    public static void showAlert(String title,String msg)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(msg);
        alert.showAndWait();
    }


    private static Boolean isFailed=false;
    public static JSONObject fetchData(String link)
    {
        HttpClient client;
        HttpRequest request;
        HttpResponse<String> response=null;
        JSONObject jsonResponse = null;
        try{

            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .GET()
                    .build();
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
            jsonResponse = new JSONObject(response.body());
            cached.put(link,jsonResponse);

        }catch (IOException e){
            if( cached.containsKey(link) && cached.get(link)!=null )
            {
                jsonResponse = cached.get(link);
                System.out.println("Cached");
                return jsonResponse;
            }
            //System.out.println("Failed To Connect");
            if(!isFailed)
            {
            Platform.runLater(()->{showAlert("Connection Failed","Check Ur network!");});
            isFailed=true;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(response==null || response.statusCode()>299) return null;
        return jsonResponse;
    }











    private static final HttpClient CLIENT = HttpClient.newHttpClient();
    private static final Map<String , JsonNode>cache = new ConcurrentHashMap<>();
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static CompletableFuture<JsonNode> fetchDataAsync(String url)
    {
        if(cache.containsKey(url)) return CompletableFuture.completedFuture(cache.get(url));

        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).build();
        return CLIENT.sendAsync(request,HttpResponse.BodyHandlers.ofString())
                .thenApply(response->{
                    try {
                        JsonNode json = OBJECT_MAPPER.readTree(response.body());
                        if (json!=null) cache.put(url,json);
                        return json;
                    } catch (Exception e) {
                        System.out.println("Error fetching Data!");
                        return null;
                    }
                });
    }
}
