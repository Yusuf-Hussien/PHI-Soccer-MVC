package phi.phisoccerii.Model.league;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import phi.phisoccerii.App;
import phi.phisoccerii.Model.match.MatchService;

public class League {
    private String name;
    private int id;
    private String logoUrl;
    private Image logo;

    public League(String name, int id,String logoUrl) {
        this.name = name;
        this.id = id;
        this.logoUrl = logoUrl;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public Image getLogo()
    {
        return logo;
    }

    private static Image defaultLogo = new Image(App.class.getResourceAsStream("logo.png"));
    public void setLogo()
    {
        if(logoUrl.equals("logo.png")) logo = defaultLogo;
        else logo = new Image(logoUrl,true);
        //logo.setFitHeight(50);logo.setFitWidth(50);logo.setPreserveRatio(true);
    }
    @Override
    public String toString()
    {
        return id+"-> "+name+"\n";
    }
}

