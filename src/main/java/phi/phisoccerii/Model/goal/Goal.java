package phi.phisoccerii.Model.goal;

public class Goal {
    private String soccer;
    private String assist;
    private String time;
    private String score;
    private boolean isHomeScored;

    public Goal(String soccer, String assist, String time, String score,boolean isHomeScored) {
        this.soccer = soccer;
        this.assist = assist;
        this.time = time;
        this.score = score;
        this.isHomeScored =isHomeScored;
    }

    public boolean isHomeScored() {
        return isHomeScored;
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
    @Override
    public String toString()
    {
        return "Scorer: "+soccer+
                "\nAssist: "+assist+
                "\nTime: "+time+
                "\nScore: "+score+"\n\n";
    }

}
