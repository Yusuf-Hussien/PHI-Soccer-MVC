package phi.phisoccerii.Model.match;


import phi.phisoccerii.Model.team.Team;

public class Match {

    private Team homeTeam;
    private String status;
    private Team awayTeam;
    private int matchId;
    private String score;
    //private List<Palyer>homeTeamLineUp;

    public Match(Team homeTeam,String status ,Team awayTeam ) {
        this.homeTeam = homeTeam;
        this.awayTeam = awayTeam;
        this.status = status;
    }

    public Team getHomeTeam() {
        return homeTeam;
    }

    public String getStatus() {
        return status;
    }

    public Team getAwayTeam() {
        return awayTeam;
    }


    @Override
    public String toString()
    {
        return homeTeam+" "+status+" "+awayTeam+"\n";
    }
}
