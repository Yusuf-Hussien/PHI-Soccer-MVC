package phi.phisoccerii.Model.team;

import javafx.scene.image.ImageView;

public class Team {
    private String name;
    private ImageView logo;
    private int rank;
    private int points;
    private int matches;
    private int id;
    private int goalDiff;
    private int win;
    private int draw;
    private int lose;



    public Team(String name, int id, ImageView logo, int rank, int points, int matches, int goalDiff, int win, int draw, int lose ) {
        this.name = name;
        this.logo = logo;
        this.rank = rank;
        this.points = points;
        this.matches = matches;
        this.goalDiff = goalDiff;
        this.win = win;
        this.draw = draw;
        this.lose = lose;
    }


    public String getName() {
        return name;
    }

    public ImageView getLogo() {
        return logo;
    }

    public int getRank() {
        return rank;
    }

    public int getPoints() {
        return points;
    }

    public int getMatches() {
        return matches;
    }

    public int getId() {
        return id;
    }

    public int getDraw() {
        return draw;
    }

    public int getWin() {
        return win;
    }

    public int getLose() {
        return lose;
    }

    public int getGoalDiff() {
        return goalDiff;
    }


    @Override
    public String toString()
    {
        return "\nrank: "+rank+" , name: "+name.toUpperCase()+" , matches: "+matches+" , points: "+points;
    }
}
