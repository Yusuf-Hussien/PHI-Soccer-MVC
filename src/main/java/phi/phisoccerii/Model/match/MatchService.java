package phi.phisoccerii.Model.match;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.concurrent.Task;
import javafx.scene.control.TableView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.json.JSONArray;
import org.json.JSONObject;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.GeneralService;
import phi.phisoccerii.Model.goal.Goal;
import phi.phisoccerii.Model.goal.GoalService;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.player.Player;
import phi.phisoccerii.Model.player.PlayerService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MatchService {


    public static void fetchAndUpdateMatches(String url, ObservableList<Match>matchesObsList, TableView<Match>matchTable)
    {
        if (matchesObsList == null) {
            System.out.println("matchesObsList is NULL! Initialize it in HomeController.");
            return;
        }
        Platform.runLater(()->{matchesObsList.clear();});
        Task<Void>task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = GeneralService.fetchData(url);
                if (response==null) return null;

                JSONArray matchesArr;
                try {
                    matchesArr = response.getJSONArray("result");
                }catch (Exception e){
                    System.out.println("There is No Matches!");
                    return null;
                }

                for (int i=0;i<matchesArr.length();i++)
                {
                    JSONObject matchJson = matchesArr.getJSONObject(i);
                    Match match = getMatch(matchJson);

                    Platform.runLater(()->{
                        matchesObsList.add(match);
                        matchTable.refresh();
                    });
                }
                return null;
            }
        };
        new Thread(task).start();
    }

    public static ImageView defaultLogo = new ImageView(new Image(App.class.getResourceAsStream("logo.png")));

    public static void setLogos(String url, ObservableList<Match>matches)
    {
                List<List<ImageView>>logos = getLogos(url);
               for(int i=0;i<matches.size();i++)
               {
                   ImageView homeLogo = logos.get(i).get(0);
                   ImageView awayLogo = logos.get(i).get(1);
                   matches.get(i).setHomeLogo(homeLogo);
                   matches.get(i).setAwayLogo(awayLogo);
               }
    }
    public static List<List<ImageView>>getLogos(String url)
    {
        JSONObject response = GeneralService.fetchData(url);
        JSONArray arr = null;
        try{
            arr = response.getJSONArray("result");
        }catch (Exception e){
            System.out.println("ERROR Fetching Data Check Match Service Class!");
        }
        if(arr==null) return  List.of();
        return getLogos(arr);
    }

    private static List<List<ImageView>>getLogos(JSONArray arr)
    {
        List<List<ImageView>>logos=new ArrayList<>();
        for(int i=0; i<arr.length();i++)
        {
            JSONObject obj = arr.getJSONObject(i);
            List<ImageView>matchLogos = getMatchLogos(obj);
            logos.add(matchLogos);
        }
        return logos;
    }
    public static List<ImageView>getMatchLogos(JSONObject obj)
    {
        ImageView homeLogo = getLogo(obj,"home");
        ImageView awayLogo = getLogo(obj,"away");
        List<ImageView>logos = new ArrayList<>();
        logos.add(homeLogo);
        logos.add(awayLogo);
        return logos;
    }
    private static ImageView getLogo(JSONObject obj,String teamType)
    {
        String team = teamType+"_team_logo";
        String url = obj.isNull(team)? null : obj.getString(team);
        ImageView logo = url==null? defaultLogo:new ImageView(new Image(url,true));
        logo.setFitHeight(30);logo.setFitWidth(30);logo.setPreserveRatio(true);
        return logo;
    }

    private static Task<Void>currentTask=null;

    public static void getMatches1by1(String url, ObservableList<Match>matchesObsList ,FilteredList<Match>matcheFilList, TableView<Match>matchesTable)
    {
        if(currentTask!=null && currentTask.isRunning()) currentTask.cancel();

        matchesObsList.clear();
        matchesTable.refresh();
        matcheFilList = new FilteredList<>(matchesObsList, p -> true);
        matchesTable.setItems(matcheFilList);

        currentTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                JSONObject response = GeneralService.fetchData(url);
                JSONArray arr = response.getJSONArray("result");
                for(int i =0 ;i<arr.length();i++)
                {
                    if(isCancelled()) break;
                    Match match = getMatch(arr.getJSONObject(i),true,false);
                    Platform.runLater(() -> {
                        if(!isCancelled())
                        {
                            matchesObsList.add(match);
                            matchesTable.refresh();
                        }
                    });
                }
                return null;
            }
        };
        new Thread(currentTask).start();
    }

    public static List<Match> getMatches(String url)
    {
        //System.out.println("Fetching matches Statrted");
        JSONObject response = GeneralService.fetchData(url);
        JSONArray arr = null;
        try{
           arr = response.getJSONArray("result");
        }catch (Exception e){
            System.out.println("ERROR Fetching Data Check Match Service Class!");
        }
        //System.out.println("Fetching matches Statrted");
        if(arr==null) return  List.of();
        return getMatches(arr);
    }
    private static List<Match>getMatches(JSONArray matchesArr)
    {
        List<Match>matches = new ArrayList<>();
        for(int i =0 ;i<matchesArr.length();i++)
        {
            Match match = getMatch(matchesArr.getJSONObject(i));
            matches.add(match);
        }
        return matches;
    }
    public static Match getMatch(JSONObject matchJson)
    {
        String homeTeam = matchJson.getString("event_home_team");
        String awayTeam = matchJson.getString("event_away_team");
        String status = matchJson.getString("event_status");
        String time = matchJson.getString("event_time");
        String score = matchJson.getString("event_final_result");
        String leagueName = matchJson.getString("league_name");
        String country = matchJson.getString("country_name");
        String round = matchJson.getString("league_round");
        String date = matchJson.getString("event_date");
        //JSONArray goalsJsonArr = matchJson.getJSONArray("goalscorers");
        //List<Goal> goals = GoalService.getGoals(goalsJsonArr);

        /*String homeLogoURL = matchJson.isNull("home_team_logo")? null : matchJson.getString("home_team_logo");
        String awayLogoURL = matchJson.isNull("away_team_logo")? null :matchJson.getString("away_team_logo");
        ImageView homeLogo = homeLogoURL==null? defaultLogo: new ImageView(new Image(homeLogoURL));
        ImageView awayLogo = awayLogoURL==null? defaultLogo:new ImageView(new Image(awayLogoURL));
        homeLogo.setFitHeight(30);homeLogo.setFitWidth(30);homeLogo.setPreserveRatio(true);
        awayLogo.setFitHeight(30);awayLogo.setFitWidth(30);awayLogo.setPreserveRatio(true);*/

        return new Match(homeTeam,status,GeneralService.from24Hto12H(time),score,awayTeam, country+" | "+leagueName , round,date,null,null,null,null,null,null,null);
    }

    public static Match getMatch(JSONObject matchJson, boolean logo, boolean async)
    {
        String homeTeam = matchJson.getString("event_home_team");
        String awayTeam = matchJson.getString("event_away_team");
        String status = matchJson.getString("event_status");
        String time = matchJson.getString("event_time");
        String score = matchJson.getString("event_final_result");
        String leagueName = matchJson.getString("league_name");
        String country = matchJson.getString("country_name");
        String round = matchJson.getString("league_round");
        String date = GeneralService.to_mm_dd_format(matchJson.getString("event_date")) ;
        JSONArray goalsJsonArr = matchJson.getJSONArray("goalscorers");
        List<Goal> goals = GoalService.getGoals(goalsJsonArr);

        JSONObject linupsJson = matchJson.getJSONObject("lineups");
        JSONObject homeJson = linupsJson.getJSONObject("home_team");
        JSONObject awayJson = linupsJson.getJSONObject("away_team");
        List<Player>homeLineup = PlayerService.getPlayersLineup(homeJson);
        List<Player>awayLinup = PlayerService.getPlayersLineup(awayJson);
        String homeCoach = PlayerService.getCoach(homeJson);
        String awayCoach = PlayerService.getCoach(awayJson);

        String homeLogoURL = null;
        String awayLogoURL = null;
        ImageView homeLogo = null;
        ImageView awayLogo = null;
        if(logo)
        {
         homeLogoURL = matchJson.isNull("home_team_logo")? null : matchJson.getString("home_team_logo");
         awayLogoURL = matchJson.isNull("away_team_logo")? null :matchJson.getString("away_team_logo");
         homeLogo = homeLogoURL==null? defaultLogo: new ImageView(new Image(homeLogoURL,async));
         awayLogo = awayLogoURL==null? defaultLogo:new ImageView(new Image(awayLogoURL,async));
         homeLogo.setFitHeight(30);homeLogo.setFitWidth(30);homeLogo.setPreserveRatio(true);
         awayLogo.setFitHeight(30);awayLogo.setFitWidth(30);awayLogo.setPreserveRatio(true);
        }
        return new Match(homeTeam,status,GeneralService.from24Hto12H(time),score,awayTeam, country+" | "+leagueName , round,date,homeLogo,awayLogo,goals,homeLineup,awayLinup,homeCoach,awayCoach);
    }

    private static final GeneralService service = new GeneralService();
    /*public static String getDayMatches(String date)
    {
         if(date.length()>0 && date.length()<3)
            date = getDateFromToday(Integer.parseInt(date));
        String url = service.getURL(service.FIXTURES)+"&from="+date+"&to="+date;
        return url;
    } */

    public static String getDayMatchesURL(String startDate,String endDate)
    {
        String url = service.getURL(service.FIXTURES)+"&from="+startDate+"&to="+endDate;
        return url;
    }

    public static String getDayMatchesURL(String date)
    {
        String url = service.getURL(service.FIXTURES)+"&from="+date+"&to="+date;
        return url;
    }

    public static String getDayMatchesURL(int days)
    {
        String date = getDateFromToday(days);
        return getDayMatchesURL(date);
    }
    public static String getWeakMatchesURL(int start)
    {
        String startDate = getDateFromToday(start);
        String endDate = getDateFromToday(start+7);
        return getDayMatchesURL(startDate,endDate);
    }
    public static String getSeasonMatchesURL(int season)
    {
        String startDate = getSeasonStartFromCurrent(season);
        String endDate = getSeasonEnd(startDate);
        return getDayMatchesURL(startDate,endDate);
    }

    private static String getDateFromToday(int days)
    {
        LocalDate today = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String date="";
        if(days==0) date = today.format(formatter);
        else if(days>0)
        {
            LocalDate next = today.plusDays(days);
            date = next.format(formatter);
        }
        else if(days<0)
        {
            LocalDate prev = today.minusDays(Math.abs(days));
            date = prev.format(formatter);
        }
        return date;
    }

    private static String getSeasonStartFromCurrent(int season)
    {
        LocalDate currSeason = LocalDate.parse("2024-07-15",DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate="";
        if(season==0) startDate = currSeason.format(formatter);
        else if(season>0)
        {
            LocalDate next = currSeason.plusYears(season);
            startDate = next.format(formatter);
        }
        else if(season<0)
        {
            LocalDate prev = currSeason.minusYears(Math.abs(season));
            startDate = prev.format(formatter);
        }
        return startDate;
    }
    private static String getSeasonEnd(String season)
    {
        LocalDate currSeason = LocalDate.parse(season,DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        String startDate="";
            LocalDate next = currSeason.plusYears(1);
            startDate = next.format(formatter);
        return startDate;
    }
}
