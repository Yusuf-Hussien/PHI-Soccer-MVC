package phi.phisoccerii.Model.league;

import com.fasterxml.jackson.databind.JsonNode;
import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.team.TeamService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class LeagueService {
    //private HashMap<String , String>leagues;



    public static HashMap<String , Integer>getLeaguesMap(List<League> leagues)
    {
        HashMap<String , Integer>leaguesMap=new HashMap<>();
        for(League league : leagues)
            leaguesMap.put(league.getName(),league.getId());
        return leaguesMap;
    }

    public static List<String>getLeaguesNames(List<League>leagues)
    {
        List<String>leaguesNames = new ArrayList<>();
        for(League league:leagues) leaguesNames.add(league.getName());
        return leaguesNames;
    }

    public static List<League>getLeagues(String link)
    {
        JSONObject obj = GeneralService.fetchData(link);
        JSONArray arr = null;
        try{
         arr = obj.getJSONArray("result");
        }catch (Exception e){
            System.out.println("Error Getting Leagues!!!!!!!!!!!\n --Check LeagueService Class--");
        }
        if (arr==null)return List.of();
        List<League>leagues = getLeagues(arr);
        return leagues;
    }

    private static List<League> getLeagues(JSONArray arr)
    {
        List<League>leagues = new ArrayList<>();
        for(int i=0;i<arr.length();i++)
        {
            JSONObject obj = arr.getJSONObject(i);
            League league = getLeague(obj);
            leagues.add(league);
        }
        return leagues;
    }
    private static League getLeague(JSONObject obj)
    {
        String name = obj.getString("league_name");
        String country = obj.getString("country_name");
        int id = obj.getInt("league_key");

        return new League(name+" -"+country , id);
    }






    public static CompletableFuture<List<League>> getLeaguesAsync(String url)
    {
        return GeneralService.fetchDataAsync(url).thenApply(jsonNode -> {
           if(jsonNode==null || !jsonNode.has("result"))
           {
               System.out.println("Error getting Leagues Check League Service class!");
               return List.of();
           }
           return getLeagues(jsonNode.get("result"));
        });
    }

    private static List<League>getLeagues(JsonNode arr)
    {
        if (!arr.isArray()) return List.of();
        List<League>leagues = new ArrayList<>();
        for(JsonNode node: arr)
        {
            League league = getLeague(node);
            leagues.add(league);
        }
        return leagues;
    }

    private static League getLeague(JsonNode node)
    {
        String name = node.get("league_name").asText();
        String country = node.get("country_name").asText();
        int id = node.get("league_key").asInt();

        return new League(name+" -"+country , id);
    }


}
