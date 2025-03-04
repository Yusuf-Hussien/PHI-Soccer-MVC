package phi.phisoccerii.Model.match;


import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.image.ImageView;
import phi.phisoccerii.Model.goal.Goal;
import phi.phisoccerii.Model.league.League;
import phi.phisoccerii.Model.team.Team;

import java.util.List;

public class Match {

    private String homeTeam;
    private String status;
    private String awayTeam;
    private String  league;
    private String round;
    private SimpleObjectProperty<ImageView> homeLogo;
    private SimpleObjectProperty<ImageView> awayLogo;

    //private List<Goal>goals;

    //private int matchId;
    // private String score;
    //private List<Palyer>homeTeamLineUp;

    public Match(String homeTeam, String status, String awayTeam, String league, String round, ImageView homeLogo, ImageView awayLogo) {
        this.homeTeam = homeTeam;
        this.status = status;
        this.awayTeam = awayTeam;
        this.league = league;
        this.round = round;
        this.homeLogo = new SimpleObjectProperty<>(homeLogo);
        this.awayLogo = new SimpleObjectProperty<>(awayLogo);
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getStatus() {
        return status;
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
