/**
 * Created by Meir on 1/21/2015.
 */
public class Board {

    OthelloGame.CellState[][] data = new OthelloGame.CellState[SIZE][SIZE];
    public static final int SIZE = OthelloGame.BOARD_SIZE;
    

    public Board() {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                data[x][y] = OthelloGame.CellState.EMPTY;
            }
        }
        data[SIZE/2][SIZE/2 - 1] = OthelloGame.CellState.BLACK;
        data[SIZE/2 - 1][SIZE/2] = OthelloGame.CellState.BLACK;
        data[SIZE/2 - 1][SIZE/2 - 1] = OthelloGame.CellState.WHITE;
        data[SIZE/2][SIZE/2] = OthelloGame.CellState.WHITE;

    }
    public OthelloGame.CellState pieceAt(int[] loc) {
        return data[loc[0]][loc[1]];
    }
    public void updateCell(int[] loc, OthelloGame.CellState newValue) {
        data[loc[0]][loc[1]] = newValue;
    }
    public boolean containsEmptyIn(int[] cell) {
        return pieceAt(cell) == OthelloGame.CellState.EMPTY;
    }

    public boolean containsComputerIn(int[] cell, OthelloGame.CellState computer) {
        return pieceAt(cell) == computer;
    }
    public boolean allContain(int[][] arr, OthelloGame.CellState piece) {
        /**
         * returns whether or not all locations on the board specified by *arr* contain *piece*
         */
        for (int i = 0; i < arr.length; i++) {
            if (pieceAt(arr[i]) != piece) {
                return false;
            }
        }
        return true;
    }

    public boolean containsHumanIn(int[] cell, OthelloGame.CellState human) {
        return pieceAt(cell) == human;
    }
    public int count(OthelloGame.CellState piece) {
        int ret = 0;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int[] loc = {x,y};
                if (pieceAt(loc) == piece) {
                    ret++;
                }
            }
        }
        return ret;
    }
    public void adjust(int[] loc) {
        /**
         * adjusts the board by assuming that the piece at loc was just placed
         */
        for (int[] direction : OthelloGame.DIRECTIONS) {
            int dx = direction[0];
            int dy = direction[1];
            int[][] toBeFlipped = new int[SIZE*SIZE][2];
            for (int i = 0; i < SIZE*SIZE; i++) {
                toBeFlipped[i] = null;
            }
            int arrCounter = 0; // faster ArrayList
            boolean keepGoing = true;
            int[] focus = loc.clone();
            while (OthelloGame.isInBoard(focus) && keepGoing && OthelloGame.jumpIsSafe(focus, direction)) {
                focus[0] += dx;
                focus[1] += dy;
                if (OthelloGame.isInBoard(focus)) {
                    if (this.pieceAt(focus) != OthelloGame.enemy(this.pieceAt(loc))) {
                        keepGoing = false;
                    }
                }
                if (keepGoing) {
                    toBeFlipped[arrCounter] = focus.clone();
                    arrCounter ++;
                }
            }
            toBeFlipped = OthelloGame.stripTrailingNulls(toBeFlipped);
            if (OthelloGame.isInBoard(focus)) {
                if (pieceAt(focus) == pieceAt(loc)) {
                    for (int[] cell : toBeFlipped) {
                        /*newBoard.*/updateCell(cell, OthelloGame.enemy(pieceAt(cell)));
                    }
                }
            }
        }
        //this.data = newBoard.data.clone();
    }
    public boolean isLegal(int[] move, OthelloGame.CellState piece) {
        /**
         * returns whether is it legal for *piece* to be place in location *move*
         */
        if (pieceAt(move) != OthelloGame.CellState.EMPTY) {
            return false; // cell is already occupied
        }
        for (int[] direction : OthelloGame.DIRECTIONS) {
            int dx = direction[0];
            int dy = direction[1];
            int[] focus = move.clone();
            focus[0] += dx;
            focus[1] += dy;
            if (OthelloGame.isInBoard(focus)) {
                if (this.data[focus[0]][focus[1]] == OthelloGame.enemy(piece)) {
                    while (OthelloGame.jumpIsSafe(focus, direction) && pieceAt(focus) == OthelloGame.enemy(piece)) {
                        focus[0] += dx;
                        focus[1] += dy;
                    }
                    if (pieceAt(focus) == piece) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public boolean hasLegalMove(OthelloGame.CellState color) {
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int[] loc = {x,y};
                if (isLegal(loc, color)) {
                    return true;
                }
            }
        }
        return false;
    }
    public int[][] legalMoves(OthelloGame.CellState piece) {
        /**
         * returns array of all legal moves for a piece
         */
        int[][] legalsArr = new int[SIZE*SIZE][2]; // there can never actually be 64 legal moves obviously
        for (int i = 0; i < SIZE*SIZE; i++) {
            legalsArr[i] = null;
        }
        int counter = 0;
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                int[] move = {x, y};
                if (isLegal(move, piece)) {
                    legalsArr[counter] = move;
                    counter += 1;
                }
            }
        }
        return OthelloGame.stripTrailingNulls(legalsArr);
    }

}