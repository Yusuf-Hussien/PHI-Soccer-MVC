package phi.phisoccerii.Model;
import phi.phisoccerii.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
public class LinksModel {
    private static Properties prop;
    private  String PREMIER_LEAGUE_LINK;
    private String CHAMPIONS_LEAGUE_LINK;
    private String EGYPTIAN_LEAGUE_LINK;
    private String LEAGUES_LINK;
    private String BASE_URL;


    public String getBASE_URL()
    {
        return BASE_URL;
    }
    public LinksModel()
    {
        setProp();
    }
    public  String getPremierLeagueLink() {
        return PREMIER_LEAGUE_LINK;
    }

    public  String getChampionsLeagueLink() {
        return CHAMPIONS_LEAGUE_LINK;
    }

    public  String getEgyptianLeagueLink() {
        return EGYPTIAN_LEAGUE_LINK;
    }
    public String getLEAGUES_LINK() {
        return LEAGUES_LINK;
    }


    private void setProp()
    {
        prop = new Properties();
        try {
            InputStream input = App.class.getResourceAsStream("config.properties");
            if(input==null){
                System.out.println("ERROR LOADING config.properties");
                return;
            }
            prop.load(input);
        } catch (
                IOException e) {
            System.out.println("Error getting property file");;
        }
        PREMIER_LEAGUE_LINK = prop.getProperty("premier_league_link");
        CHAMPIONS_LEAGUE_LINK =  prop.getProperty("champions_league_link");
        EGYPTIAN_LEAGUE_LINK =  prop.getProperty("egyptian_league_link");
        LEAGUES_LINK = prop.getProperty("leagues");
        BASE_URL = prop.getProperty("BASE_URL");

    }

}
