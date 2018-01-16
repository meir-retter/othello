/**
 * Created by Meir on 1/30/2015.
 */
public class Player {
    OthelloGame.CellState color;
    int pieceWeight;
    int mobilityWeight;
    int cornerWeight;
    int permWeight;
    int nearCornerWeight;

    public Player(OthelloGame.CellState myColor, int myPieceWeight,
                  int myMobilityWeight, int myCornerWeight,
                  int myPermWeight, int myNearCornerWeight) {
        color = myColor;
        pieceWeight = myPieceWeight;
        mobilityWeight = myMobilityWeight;
        cornerWeight = myCornerWeight;
        permWeight = myPermWeight;
        nearCornerWeight = myNearCornerWeight;
    }
}
