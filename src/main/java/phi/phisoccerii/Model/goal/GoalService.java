package phi.phisoccerii.Model.goal;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class GoalService {


    public static List<Goal>getGoals(JSONArray JsonArr)
    {
        List<Goal>goals = new ArrayList<>();
        for(int i=0;i<JsonArr.length();i++)
        {
            JSONObject obj = JsonArr.getJSONObject(i);
            Goal goal = getGoal(obj);
            goals.add(goal);
        }
        return goals;
    }

    private static Goal getGoal(JSONObject obj)
    {
        String score = obj.getString("score");
        String time = obj.getString("time");
        String soccer = obj.getString("home_scorer");
        String assist;
        boolean isHomeScored=true;
        if(soccer==null || soccer.isEmpty())
        {
            isHomeScored=false;
            soccer = obj.getString("away_scorer");
            assist = obj.isNull("away_assist")? "": obj.getString("away_assist");
        }
        else assist = obj.isNull("home_assist")?"":obj.getString("home_assist");

        return new Goal(soccer,assist,time,score,isHomeScored);
    }
}
