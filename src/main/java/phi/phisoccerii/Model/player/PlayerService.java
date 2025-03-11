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

import static phi.phisoccerii.Model.GeneralService.fetchData;

public class PlayerService {


   //need to add -> get TopScoreres by league id or add it in the controller

    public static List<Player> getPlayers(String link)
    {
        JSONObject json = fetchData(link);
        JSONArray result = json.getJSONArray("result");
        List<Player>standing = getPlayers(result);
        return standing;
    }


    private static List<Player> getPlayers(JSONArray arr)
    {
        List<Player>result = new ArrayList<>();
        for(int i =0 ;i<arr.length();i++)
        {
            JSONObject JSONplayer = arr.getJSONObject(i);
            Player player = getPlayer(JSONplayer);
            result.add(player);
        }
        return result;
    }

    private static Player getPlayer(JSONObject JSONPlayer)
    {
         int id = JSONPlayer.getInt("player_key");
         String name = JSONPlayer.getString("player_name");
         int rank = JSONPlayer.getInt("player_place");
         String team = JSONPlayer.getString("team_name");
         int goals = JSONPlayer.getInt("goals");
         int assists = JSONPlayer.isNull("assists")? 0 : JSONPlayer.getInt("assists");
         int penaltys = JSONPlayer.getInt("penalty_goals");

         return new Player(id,name,rank,team,goals,assists,penaltys);
    }




    public static List<Player>getPlayersLineup(JSONObject jsonTeam)
    {
        JSONArray startingLineup = jsonTeam.getJSONArray("starting_lineups");
        List<Player>lineUp = new ArrayList<>();
        for (int i=0 ;i<startingLineup.length();i++)
            lineUp.add(getPlayerLineup(startingLineup.getJSONObject(i)));
        return lineUp;
    }
    private static Player getPlayerLineup(JSONObject JSONPlayer)
    {
        int id = JSONPlayer.getInt("player_position");
        String name = JSONPlayer.getString("player");
        int rank = JSONPlayer.getInt("player_number");

        return new Player(id,name,rank);
    }

    public static String getCoach(JSONObject teamJson) {
        return teamJson.getJSONArray("coaches").getJSONObject(0).getString("coache");
    }
}
