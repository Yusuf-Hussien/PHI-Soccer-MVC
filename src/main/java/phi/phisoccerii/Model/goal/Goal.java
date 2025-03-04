package phi.phisoccerii.Model.goal;

public class Goal {
    private String soccer;
    private String assist;
    private String time;
    private String score;

    public Goal(String soccer, String assist, String time, String score) {
        this.soccer = soccer;
        this.assist = assist;
        this.time = time;
        this.score = score;
    }

    public String getSoccer() {
        return soccer;
    }

    public String getAssist() {
        return assist;
    }

    public String getTime() {
        return time;
    }

    public String getScore() {
        return score;
    }

}
