package phi.phisoccerii.Controller.Match;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import phi.phisoccerii.Model.player.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LineupControllerBackup {
    String coach;
    List<Integer>format;
    List<Player>lineup;

    @FXML private StackPane playground;
    @FXML private Label coach_and_format;
    GridPane lineupGrid = new GridPane();

    public void setData(String coach, List<Integer>format, List<Player>lineup) {
        this.coach = coach;
        this.format =new ArrayList<>(format);
        this .lineup = lineup;
        setContent();

    }

    public void setContent() {
        coach_and_format.setText("coach: "+coach+" "+format.toString());


        lineupGrid.setAlignment(Pos.CENTER);
        lineupGrid.setHgap(5);
        lineupGrid.setVgap(50);
        lineupGrid.setMaxSize(345,430);
        playground.getChildren().add(lineupGrid);
        this.format.addFirst(1);
        Collections.reverse(format);
        Collections.reverse(lineup);
        addPlayersToGrid(lineupGrid,lineup,format);
    }

    private void addPlayersToGrid(GridPane lineupGrid, List<Player> lineup, List<Integer> format)
    {
        int playerIndex=0;
        int totalRows = format.size();

        for(int row=0;row<totalRows;row++)
        {
            int playersInRow = format.get(row);

            for (int col=0;col<playersInRow;col++)
            {
                if (playerIndex>=lineup.size()) return;

                Player player = lineup.get(playerIndex++);

                Text name = new Text(player.getName());
                name.setFill(Color.WHITE);

                Text number = new Text(String.valueOf(player.getRank()));
                number.setFill(Color.WHITE);

                Circle circle = new Circle(15);
                circle.setFill(Color.valueOf("#34495e"));
                circle.setStroke(Color.WHITE);
                StackPane playerCircle = new StackPane();
                playerCircle.getChildren().addAll(circle,number);

                VBox playerBox = new VBox(5,playerCircle,name);
                playerBox.setAlignment(Pos.CENTER);

                int totalCols=9;
                int startCol=(totalCols-(playersInRow*2)/2);
                int colIndex = startCol+(col*2);
                lineupGrid.add(playerBox,colIndex,row);
            }

        }
    }
}
