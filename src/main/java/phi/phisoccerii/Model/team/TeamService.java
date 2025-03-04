package phi.phisoccerii.Model.team;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.Model.league.League;

import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TeamService {
    // Main Services
    public static List<String>getTeamsNames(List<Team>teams)
    {
        List<String>teamsNames = new ArrayList<>();
        for(Team team:teams) teamsNames.add(team.getName());
        return teamsNames;
    }

    public static HashMap<String ,Integer>getTeamsMap(List<Team>teams)
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
    /*
    public static List<Team> getTeams(String link)
    {
        JSONObject json = fetchData(link);
        JSONObject result = json.getJSONObject("resultt");
        JSONArray total = result.getJSONArray("total");
        List<Team>standing = getTeams(total);
        return standing;
    }*/


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

    private static Team getTeam(JSONObject JSONteam)
    {
        String name = JSONteam.get("standing_team").toString();
        int id = JSONteam.getInt("team_key");
        int rank = JSONteam.getInt("standing_place");
        int points = JSONteam.getInt("standing_PTS");
        int matches = JSONteam.getInt("standing_P");
        int goalDiff = JSONteam.getInt("standing_GD");
        int win = JSONteam.getInt("standing_W");
        int draw = JSONteam.getInt("standing_D");
        int lose = JSONteam.getInt("standing_L");
        String logoURL = JSONteam.get("team_logo").toString();
        //ImageView logo = new ImageView(new Image(logoURL,30,30,true,true)); //noise
        ImageView logo = new ImageView(new Image(logoURL));
        logo.setFitHeight(30);logo.setFitWidth(30);logo.setPreserveRatio(true);

        name = name.equals("Israel")? "Shit" : name;
        return new Team(name,id,logo,rank,points,matches,goalDiff,win,draw,lose);
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
    public static JSONArray getJSONarr(List<Team>teams)
    {
        JSONArray JSONarr = new JSONArray();
        for(Team team:teams)
        {
            JSONObject obj = new JSONObject();
            obj.put("name",team.getName());
            obj.put("rank",team.getRank());
            obj.put("points",team.getPoints());
            obj.put("matches",team.getMatches());
            JSONarr.put(obj);
        }
        return JSONarr;
    }
    public static void getJSONfile(String path ,List<Team>teams)
    {
        JSONArray JSONarr = getJSONarr(teams);
        try (FileWriter writer = new FileWriter(path+".json")){
            writer.write(JSONarr.toString(4));
        } catch (IOException e) {
            System.out.println("ERROR WHILE SAVING!");
        }
    }
    public static void getJSONfile(String path ,JSONArray arr)
    {
        try (FileWriter writer = new FileWriter(path+".json")){
            writer.write(arr.toString(4));
        } catch (IOException e) {
            System.out.println("ERROR WHILE SAVING!");
        }
    }
    public static void getTXTfile(String path , List<Team>teams)
    {
        try(FileWriter fileWriter = new FileWriter(path+".txt"))
        {
            for (Team team:teams)
                fileWriter.write(team.toString()+"\n");
        }catch (IOException ex){
            System.out.println("ERROR While Saving TXT");
        }
    }

}

