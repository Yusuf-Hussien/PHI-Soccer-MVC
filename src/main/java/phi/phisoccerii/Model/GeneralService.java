package phi.phisoccerii.Model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static phi.phisoccerii.Model.team.TeamService.getJSONarr;

public class GeneralService {
    //public static final String BASE_URL="https://apiv2.allsportsapi.com/football/?APIkey=61cb19bbb2ebed263a52388fceca6a9affe7db36d0b9d0bc1cd25a6a8b03cede&leagueId=3&met=Standings&";
    private static Properties prop;
    //private String LEAGUES, TEAMS, PLAYERS;

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
        System.out.println(Base+type+"&leagueId="+leagueId);
        return Base+type+"&leagueId="+leagueId;
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
    public static JSONObject fetchData(String link)
    {
        HttpClient client;
        HttpRequest request;
        HttpResponse<String> response=null;
        try{
            client = HttpClient.newHttpClient();
            request = HttpRequest.newBuilder()
                    .uri(URI.create(link))
                    .GET()
                    .build();
            response = client.send(request,HttpResponse.BodyHandlers.ofString());
        }catch (IOException e){
            System.out.println("Failed To Connect");
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        if(response==null || response.statusCode()>299) return null;
        return new JSONObject(response.body());
    }
    // Extra Services


}
