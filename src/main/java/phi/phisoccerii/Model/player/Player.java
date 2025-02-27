package phi.phisoccerii.Model.player;

public class Player {
    private int id;
    private String name;
    private int age;
    private String position;
    //private ImageView img;

    public Player(String name, int id, int age, String position) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.position = position;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getPosition() {
        return position;
    }
}
