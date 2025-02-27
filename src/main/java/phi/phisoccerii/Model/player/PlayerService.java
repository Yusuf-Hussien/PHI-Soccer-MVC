package phi.phisoccerii.Model.player;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.team.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerService {

    // Main Services
   /* public static List<String> getTeamsNames(List<League>leagues)
    {
        List<String>leaguesNames = new ArrayList<>();
        for(League league:leagues) leaguesNames.add(league.getName());
        return leaguesNames;
    }

    public static HashMap<String ,Integer> getTeamsMap(List<Team>teams)
    {
        HashMap<String,Integer>teamsMap=new HashMap<>();
        for(Team team:teams)
            teamsMap.put(team.getName(),team.getId());
        return teamsMap;
    }

    public static List<Team> getTeams(String link)
    {
        JSONObject json = fetchData(link);
        JSONObject result=null;
        try {
            result = json.getJSONObject("result");
        }catch (Exception e){
            //System.out.println("No Live Matches Now!");
            System.out.println("ERROR GETTING TEAMS!!!!!!!!!!!\n ----Check TeamService Class----");
        }
        if (result == null) return List.of();
        JSONArray total = result.getJSONArray("total");
        List<Team>standing = getTeams(total);
        return standing;
    }

    public static List<Team> getTeams(String link)
    {
        JSONObject json = fetchData(link);
        JSONObject result = json.getJSONObject("resultt");
        JSONArray total = result.getJSONArray("total");
        List<Team>standing = getTeams(total);
        return standing;
    }


    private static List<Team> getTeams(JSONArray arr)
    {
        List<Team>result = new ArrayList<>();
        for(int i =0 ;i<arr.length();i++)
        {
            JSONObject JSONteam = arr.getJSONObject(i);
            Team team = getTeam(JSONteam);
            result.add(team);
        }
        return result;
    }

    private static Player getPlayer(JSONObject JSONPlayer)
    {
        String name = JSONPlayer.get("standing_team").toString();
        int id = JSONPlayer.getInt("team_key");
        int rank = JSONPlayer.getInt("standing_place");
        int points = JSONPlayer.getInt("standing_PTS");
        int matches = JSONPlayer.getInt("standing_P");
        int goalDiff = JSONPlayer.getInt("standing_GD");
        int win = JSONPlayer.getInt("standing_W");
        int draw = JSONPlayer.getInt("standing_D");
        int lose = JSONPlayer.getInt("standing_L");
        String logoURL = JSONPlayer.get("team_logo").toString();

        return new Player(name,id,age,position);
    }*/

}
