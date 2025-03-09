package phi.phisoccerii.Controller.League;

import phi.phisoccerii.Model.league.League;

public interface IController {
    public void setLeague(League league);
    public void setMethod(boolean oneBYone,boolean async);
    public void setUpTable();
}
