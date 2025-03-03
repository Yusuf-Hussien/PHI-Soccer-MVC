package phi.phisoccerii.Model.league;

import javafx.concurrent.Task;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.team.TeamService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class LeagueService {
    //private HashMap<String , String>leagues;


    public Task<List<League>>getLeaguesAsync(String url)
    {
        new Task<List<League>>(){
            @Override
            protected List<League> call() throws Exception {
                return List.of();
            }
        };
        return null;
    }


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
}
