package phi.phisoccerii.Model.player;

public class Player {
    private int id;
    private String name;
    private int rank;
    private String team;
    private int goals;
    private int assists;
    private int penaltys;

    /*private String name;
    private int age;
    private String position;*/
    //private ImageView img;


    public Player(int id, String name, int rank, String team, int goals, int assists, int penaltys) {
        this.id = id;
        this.name = name;
        this.rank = rank;
        this.team = team;
        this.goals = goals;
        this.assists = assists;
        this.penaltys = penaltys;
    }

    public int getRank() {
        return rank;
    }

    public String getTeam() {
        return team;
    }

    public int getGoals() {
        return goals;
    }

    public int getAssists() {
        return assists;
    }

    public int getPenaltys() {
        return penaltys;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }
    @Override
    public  String toString()
    {
        return rank+" - "+name+" "+goals+"\n";
    }

}
