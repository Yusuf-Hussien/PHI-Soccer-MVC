package phi.phisoccerii.Model.league;

public class League {
    private String name;
    private int id;

    public League(String name, int id) {
        this.name = name;
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString()
    {
        return id+"-> "+name+"\n";
    }
}

