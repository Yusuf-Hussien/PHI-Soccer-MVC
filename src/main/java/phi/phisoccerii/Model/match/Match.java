package phi.phisoccerii.Model.match;


import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import phi.phisoccerii.Model.goal.Goal;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.player.Player;
import phi.phisoccerii.Model.team.Team;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class Match {

    private String homeTeam;
    private String score;
    private String awayTeam;
    private String  league;
    private String round;
    private String status;
    private String  time;
    private String date;
    private List<Goal> goals;
    private List<Player>homeLineup;
    private List<Player>awayLineup;
    private String homeCoach;
    private String awayCoach;
    private SimpleObjectProperty<ImageView> homeLogo;
    private SimpleObjectProperty<ImageView> awayLogo;

    private String stadium;
    private String judg;
    private String homeFormat;
    private String awayFormat;



    //private List<Goal>goals;

    //private int matchId;
    // private String score;
    //private List<Palyer>homeTeamLineUp;

    public Match(String homeTeam, String status, String time, String score, String awayTeam, String league, String round, String date, ImageView homeLogo, ImageView awayLogo, List<Goal> goals,String homeFormat, List<Player>homeLineup,String awayFormat,List<Player>awayLineup,String homeCoach,String awayCoach,String stadium,String judg) {
        this.homeTeam = homeTeam;
        this.status = status;
        this.time = time;
        this.score = score;
        this.awayTeam = awayTeam;
        this.league = league;
        this.round = round;
        this.date = date;
        this.goals = goals;
        this.homeLineup = homeLineup;
        this.awayLineup = awayLineup;
        this.homeCoach = homeCoach;
        this.awayCoach = awayCoach;
        this.homeLogo = new SimpleObjectProperty<>(homeLogo);
        this.awayLogo = new SimpleObjectProperty<>(awayLogo);
        this.stadium = stadium;
        this.judg =judg;
        this.homeFormat =homeFormat;
        this.awayFormat = awayFormat;
    }

    /*private List<Integer> setFormat(String formation) {
        if (formation==null || formation.isEmpty())return List.of();
        String[]parts = formation.split("-");
        return Arrays.stream(parts).map(Integer::parseInt).collect(Collectors.toList());
    }*/

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getStatus() {
        return status;
    }

    public String getTime() {
        return time;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public String getLeague() {
        return league;
    }

    public String getRound() {
        return round;
    }

    public String getScore() {
        return score;
    }

    public String getDate() {
        return date;
    }

    public List<Goal> getGoals() {
        return goals;
    }

    public List<Player> getHomeLineup() {
        return homeLineup;
    }

    public List<Player> getAwayLineup() {
        return awayLineup;
    }

    public String getHomeCoach() {
        return homeCoach;
    }

    public String getAwayCoach() {
        return awayCoach;
    }

    public String getStadium() {
        return stadium;
    }

    public String getJudg() {
        return judg;
    }

    public String getHomeFormat() {
        return homeFormat;
    }

    public String getAwayFormat() {
        return awayFormat;
    }

    public ImageView getHomeLogo() {
        return homeLogo.get();
    }

    public ImageView getAwayLogo() {
        return awayLogo.get();
    }

    public SimpleObjectProperty<ImageView> getHomeLogoProp() {
        return homeLogo;
    }

    public SimpleObjectProperty<ImageView> getAwayLogoProp() {
        return awayLogo;
    }

    public void setAwayLogo(ImageView awayLogo) {
        this.awayLogo = new SimpleObjectProperty<>(awayLogo);
    }

    public void setHomeLogo(ImageView homeLogo) {
        this.homeLogo = new SimpleObjectProperty<>(homeLogo);
    }

    @Override
    public String toString()
    {
        return homeTeam+" "+status+" "+awayTeam+"\n"+league+" ->"+round+"\n\n";
    }
}
