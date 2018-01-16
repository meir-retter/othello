/**
 * Created by Meir on 1/13/2015.
 */
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;

public class OthelloGame {

    public static enum CellState {
        EMPTY,
        WHITE,
        BLACK
    }
    public static final int BOARD_SIZE = 8; // must be even, 8 for standard Othello
    public static final int[][] DIRECTIONS = {{1,0},{1,1},{0,1},{-1,1},{-1,0},{-1,-1},{0,-1},{1,-1}};
    public static final int[][] CORNERS = {{0,0},{0,BOARD_SIZE-1},{BOARD_SIZE-1,BOARD_SIZE-1},{BOARD_SIZE-1,0}};
    public static final String ALPH = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    public static final boolean GLOBAL_HINTS = true; // allows for the possibility of hints being included in displayBoard
                                                     // for the hints to actually be given, the localHints parameter of displayBoard must be set to *true*
    public static final int MAX_DEPTH = 4;
    public static final int HIGH = 10000000;
    public static final int LOW = -10000000; // better than Integer.MAX_VALUE etc. because of overflow issues


    public static void displayInstruct() {
        System.out.println();
        System.out.println("Welcome to Othello. You will make your move known by entering a letter`, A-H, followed by a number.");
        System.out.println("For example, d4.");
        System.out.println("A dot means a move is legal for you.");
        System.out.println();
    }

    public static String stringMultiplied(String s, int times) {
        String newString = "";
        for (int i = 0; i < times; i++) {
            newString += s;
        }
        return newString;
    }

    public static boolean areOneDiagAway(int[] p1, int[] p2) {
        /**
         * returns whether p1 and p2 are diagonally adjacent (e.g. {2,3} and {1,4}
         */
        int dx = p2[0] - p1[0];
        int dy = p2[1] - p1[1];
        return (Math.abs(dx) == 1) && (Math.abs(dy) == 1);
    }

    public static void printIntArray(int[] arr) {
        System.out.print("{");
        for (int i = 0; i < arr.length-1; i++) {
            System.out.print(arr[0]+", ");
        }
        System.out.println(arr[arr.length-1]+"}");
    }

    public static int randint(int min, int max) {
        /**
         * returns a random integer between min and max, inclusive like in python
         */
        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    public static String input(String instructions) {
        System.out.print(instructions);
        Scanner scanner = new Scanner(System.in);
        String ret = scanner.nextLine();
        return ret;
    }

    public static int intInput(String instructions) {
        System.out.print(instructions);
        Scanner scanner = new Scanner(System.in);
        int ret = scanner.nextInt();
        return ret;
    }

    public static String letterAtIndex(String string, int index) {
        return string.substring(index, index+1);
    }

    public static int[][] stripTrailingNulls(int[][] arr) {
        /**
         * for use to make faster implementation of ArrayList when you know a max size it will become
         */
        int trueLength = 0; // length without trailing nulls
        while (arr[trueLength] != null) {
            trueLength++;
        }
        int[][] newArr = new int[trueLength][2];
        for (int i = 0; i < trueLength; i++) {
            newArr[i] = arr[i].clone();
        }
        return newArr;
    }

    public static boolean isInBoard(int[] cell) {
        int x = cell[0];
        int y = cell[1];
        return (x >= 0) && (x < BOARD_SIZE) && (y >= 0) && (y < BOARD_SIZE);
    }



    public static void displayBoard(Board board, CellState computer, CellState human, boolean localHints) {
        HashMap<CellState, String> colorToSymbol = new HashMap<CellState, String>();
        colorToSymbol.put(CellState.EMPTY, " ");
        colorToSymbol.put(CellState.BLACK, "#");
        colorToSymbol.put(CellState.WHITE, "O");
        String horizontalLine = "  +" + stringMultiplied("-----+", BOARD_SIZE);
        for (int i = 0; i < BOARD_SIZE; i++) {
            String letter = letterAtIndex(ALPH, i);
            System.out.print("     " + letter);
        }
        System.out.println();
        System.out.println(horizontalLine);
        for (int y = 0; y < BOARD_SIZE; y++) {
            String line = (y+1) + " |  ";
            for (int x = 0; x < BOARD_SIZE; x ++) {
                int[] loc = {x,y};
                if (board.isLegal(loc, human) && GLOBAL_HINTS && localHints) {
                    line += '.' + "  |  ";
                } else {
                    line += colorToSymbol.get(board.data[x][y]) + "  |  ";
                }
            }
            System.out.println(line);
            System.out.println(horizontalLine);
        }
        System.out.println();
        System.out.println("computer(" + colorToSymbol.get(computer) + "): " + board.count(computer));
        System.out.println("human   (" + colorToSymbol.get(human) + "): " + board.count(human));
        System.out.println();
    }

    public static Board copy(Board board) {
        Board newBoard = new Board();
        CellState[][] newData = new CellState[BOARD_SIZE][BOARD_SIZE];
        for (int x = 0; x < BOARD_SIZE; x++) {
            newData[x] = board.data[x].clone();
        }
        newBoard.data = newData;
        return newBoard;
    }

    public static CellState enemy(CellState piece) {
        if (piece == CellState.BLACK) {
            return CellState.WHITE;
        } else if (piece == CellState.WHITE) {
            return CellState.BLACK;
        }
        return null;
    }

    public static boolean jumpIsSafe(int[] startCell, int[] direction) {
        int x = startCell[0];
        int y = startCell[1];
        int dx = direction[0];
        int dy = direction[1];
        int[] destination = {x + dx, y + dy};
        return isInBoard(destination);
    }

    public static int[] letterNumberToNumberNumber(String letterNumber) {
        /**
         * e.g. converts "d3" or "D3" to {3, 2}
         */
        int[] numberNumber = {ALPH.indexOf(letterNumber.substring(0,1).toUpperCase()), Integer.parseInt(letterNumber.substring(1,2))-1};
        return numberNumber;
    }

    public static String numberNumberToLetterNumber(int[] numberNumber) {
        /**
         * e.g. converts {3,2} to "D3"
         */
        int let = numberNumber[0]; // number that will become a letter
        int num = numberNumber[1]; // number that will become a number
        return ALPH.substring(let, let+1) + (num + 1);
    }

    public static int[][] cellsThrough(int[] startCell, int[] endCell) {
        /**
         * 1. assumes startCell and endCell are either in the same row or column
         * 2. returns an int[][] of all cells between them, including sC and eC themselves
         */
        if (!(startCell[0] == endCell[0] || startCell[1] == endCell[1])) {
            return null;
        }
        if (startCell == endCell) {
            int[][] ret =  {startCell.clone()};
            return ret;
        }

        int dx = endCell[0] - startCell[0];
        int dy = endCell[1] - startCell[1];
        int length = 0; // initialize length
        int[] direction = {0,0}; // initialize direction
        if (dx == 0) {
            length = dy + 1;
            direction[1] = dy/Math.abs(dy);
        } else if (dy == 0) {
            length = dx + 1;
             direction[0] = dx/Math.abs(dx);
        }
        int[][] ret = new int[length][2];
        int counter = 0;
        int[] focus = startCell.clone();
        for (int i = 0; i < length; i++) {
            ret[counter] = focus.clone();
            counter++;
            focus[0] += direction[0];
            focus[1] += direction[1];
        }
        return ret;
    }

    public static Board boardAdjusted(Board board, int[] loc) {
        /**
         * like board.adjust(), but functional
         */
        Board newBoard = copy(board);
        for (int[] direction : DIRECTIONS) {
            int dx = direction[0];
            int dy = direction[1];
            int[][] toBeFlipped = new int[BOARD_SIZE*BOARD_SIZE][2];
            for (int i = 0; i < BOARD_SIZE*BOARD_SIZE; i++) {
                toBeFlipped[i] = null;
            }
            int arrCounter = 0; // faster ArrayList
            boolean keepGoing = true;
            int[] focus = loc.clone();
            while (isInBoard(focus) && keepGoing && jumpIsSafe(focus, direction)) {
                focus[0] += dx;
                focus[1] += dy;
                if (isInBoard(focus)) {
                    if (board.pieceAt(focus) != enemy(board.pieceAt(loc))) {
                        keepGoing = false;
                    }
                }
                if (keepGoing) {
                    toBeFlipped[arrCounter] = focus.clone();
                    arrCounter++;
                }
            }
            toBeFlipped = stripTrailingNulls(toBeFlipped);
            if (isInBoard(focus)) {
                if (board.pieceAt(focus) == board.pieceAt(loc)) {
                    for (int[] cell : toBeFlipped) {
                        newBoard.updateCell(cell, enemy(newBoard.pieceAt(cell)));
                    }
                }
            }
        }
        return copy(newBoard);
    }

    public static int evaluate(Board board, int pieceWeight,
                               int mobilityWeight, int cornerWeight,
                               int permWeight, int nearCornerWeight) {
        /**
         * positive favors black; negative favors white.
         * as of now, permWeight is not used; if the implicit preference for stable discs
         * created by mobility maximizing proves insufficient, I might include it.
         */
        CellState winner = winner(board);
        if (winner == CellState.BLACK) {
            return 1000000000;
        }
        if (winner == CellState.WHITE) {
            return -1000000000;
        }
        int ret = 0;

        // number of pieces for each side
        ret += board.count(CellState.BLACK) * pieceWeight;
        ret -= board.count(CellState.WHITE) * pieceWeight;

        // each side's mobility
        ret += board.legalMoves(CellState.BLACK).length * mobilityWeight;
        ret -= board.legalMoves(CellState.WHITE).length * mobilityWeight;

        // number of corners occupied by each side
        int blacksInCorners = 0;
        int whitesInCorners = 0;
        for (int[] corner : CORNERS) {
            CellState pieceAtCorner = board.pieceAt(corner);
            if (pieceAtCorner == CellState.BLACK) {
                blacksInCorners++;
            } else if (pieceAtCorner == CellState.WHITE) {
                whitesInCorners++;
            }
        }
        ret += blacksInCorners * cornerWeight;
        ret -= whitesInCorners * cornerWeight;

        // cells next to corners occupied by each side (not desirable)
        int blacksNearCorners = 0;
        int whitesNearCorners = 0;
        HashMap<int[], int[][]> cornerToNears = new HashMap<>(); // maps a corner to an int[][] of its three neighbors
        int[][] neighbors0 = {{0, 1}, {1, 0}, {1, 1}};
        int[][] neighbors1 = {{0, BOARD_SIZE-2}, {1, BOARD_SIZE-2}, {1, BOARD_SIZE-1}};
        int[][] neighbors2 = {{BOARD_SIZE-1, BOARD_SIZE-2}, {BOARD_SIZE-2, BOARD_SIZE-2}, {BOARD_SIZE-2, BOARD_SIZE-1}};
        int[][] neighbors3 = {{BOARD_SIZE-1, 1}, {BOARD_SIZE-2, 0}, {BOARD_SIZE-2, 1}};
        cornerToNears.put(CORNERS[0], neighbors0);
        cornerToNears.put(CORNERS[1], neighbors1);
        cornerToNears.put(CORNERS[2], neighbors2);
        cornerToNears.put(CORNERS[3], neighbors3);

        for (int[] corner : CORNERS) {
            if (board.pieceAt(corner) == CellState.EMPTY) {
                int[][] nears = cornerToNears.get(corner);
                for (int[] near : nears) {
                    // first make sure the piece at *near* isn't backed up all the way to the other corner (in which case it is a perm)
                    if (board.pieceAt(near) != CellState.EMPTY) {
                        int[][] backers = new int[BOARD_SIZE - 2][2];
                        int[] directionFromCornerToNear = {near[0] - corner[0], near[1] - corner[1]};
                        for (int i = 0; i < backers.length; i++) {
                            int x = near[0] + directionFromCornerToNear[0] * (i+1);
                            int y = near[1] + directionFromCornerToNear[1] * (i+1);
                            int[] backer = {x, y};
                            backers[i] = backer;
                        }
                        if (board.pieceAt(near) == CellState.BLACK && (areOneDiagAway(corner, near) || !board.allContain(backers, CellState.BLACK))) {
                            blacksNearCorners++;
                        } else if (board.pieceAt(near) == CellState.WHITE && (areOneDiagAway(corner, near) || !board.allContain(backers, CellState.BLACK))) {
                            whitesNearCorners++;
                        }
                    }
                }
            }
        }
        ret -= blacksNearCorners * nearCornerWeight; // negative for black, positive for white
        ret += whitesNearCorners * nearCornerWeight; // because occupying nears is not good

        // now calculate the number of "perms" (permanents) for each color
        // these are pieces that cannot be flipped for the rest of the game
        return ret;
    }

    public static CellState winner(Board board) {
        int blacksCount = board.count(CellState.BLACK);
        int whitesCount = board.count(CellState.WHITE);
        if ((board.count(CellState.EMPTY) == 0) ||
            (!board.hasLegalMove(CellState.BLACK) &&
             !board.hasLegalMove(CellState.WHITE))) {
            if (blacksCount == whitesCount) {
                return CellState.EMPTY; // returning EMPTY means the game is over and has resulted in a tie
            } else if (blacksCount > whitesCount) {
                return CellState.BLACK;
            } else if (whitesCount > blacksCount) {
                return CellState.WHITE;
            }
        }
        return null; // returning null means the game is not over
    }

    public static int alphaBeta(Player player, CellState currentColor, Board board, int alpha, int beta, int depth) {
        /**
         * player is only to give the weights
         * color is determined by currentColor
         * this allows colors to be swapped without knowing information about the other player
         */
        if (winner(board) == CellState.BLACK) {
            return HIGH;
        }
        if (winner(board) == CellState.WHITE) {
            return LOW;
        }
        if (winner(board) == CellState.EMPTY) {
            return 0;
        }
        if (!board.hasLegalMove(currentColor)) { // other player must have a move or the game would be over, so no infinite loops
            return alphaBeta(player, enemy(currentColor), board, alpha, beta, depth);
        }
        int[][] legalMoves = board.legalMoves(currentColor);
        if (depth == 0) {
            return evaluate(board, player.pieceWeight,
                    player.mobilityWeight, player.cornerWeight,
                    player.permWeight, player.nearCornerWeight);
        }
        if (currentColor == CellState.BLACK) {
            int ret = LOW;
            for (int[] legalMove : legalMoves) {
                Board tempBoard = copy(board);
                tempBoard.updateCell(legalMove, currentColor);
                ret = Integer.max(ret, alphaBeta(player, enemy(currentColor), boardAdjusted(tempBoard, legalMove), alpha, beta, depth - 1));
                alpha = Integer.max(alpha, ret);
                if (beta <= alpha) {
                    break;
                }
            }
            return ret;
        } else { // if currentColor == CellState.White
            int ret = HIGH;
            for (int[] legalMove : legalMoves) {
                Board tempBoard = copy(board);
                tempBoard.updateCell(legalMove, currentColor);
                ret = Integer.min(ret, alphaBeta(player, enemy(currentColor), boardAdjusted(tempBoard, legalMove), alpha, beta, depth - 1));
                beta = Integer.min(beta, ret);
                if (beta <= alpha) {
                    break;
                }
            }
            return ret;
        }
    }

    public static int[] getMove(Player player, Board board) {
        int[][] legalMoves = board.legalMoves(player.color);
        int[] bestMove = null;
        int bestValue = 0; // initialize
        if (player.color == CellState.BLACK) {
            bestValue = LOW;
            for (int[] legalMove : legalMoves) {
                Board tempBoard = copy(board);
                tempBoard.updateCell(legalMove, player.color);
                tempBoard = boardAdjusted(tempBoard, legalMove);
                int value = alphaBeta(player, enemy(player.color), tempBoard, LOW, HIGH, MAX_DEPTH);
                if (value >= bestValue) {
                    bestValue = value;
                    bestMove = legalMove;
                }
            }
        } else if (player.color == CellState.WHITE) {
            bestValue = HIGH;
            for (int[] legalMove : legalMoves) {
                Board tempBoard = copy(board);
                tempBoard.updateCell(legalMove, player.color);
                tempBoard = boardAdjusted(tempBoard, legalMove);
                int value = alphaBeta(player, enemy(player.color), tempBoard, LOW, HIGH, MAX_DEPTH);
                if (value <= bestValue) {
                    bestValue = value;
                    bestMove = legalMove;
                }
            }
        }
        System.out.println(bestValue);
        return bestMove;
    }

    public static String getHumanMove(Board board, CellState humanColor) {
        String ret = input("Enter a move: ");
        if (ret.length() != 2) {
            System.out.println("Move must be a letter followed by a number.");
            return getHumanMove(board, humanColor);
        }
        if (!ALPH.toLowerCase().substring(0,BOARD_SIZE).contains(letterAtIndex(ret, 0).toLowerCase())) {
            System.out.println("First character must be a letter (A - " + letterAtIndex(ALPH, BOARD_SIZE - 1) + ").");
            return getHumanMove(board, humanColor);
        }
        if (!"123456789".substring(0,BOARD_SIZE).contains(letterAtIndex(ret, 1))) {
            System.out.println("Second character must be a number (1 - " + letterAtIndex("123456789", BOARD_SIZE - 1) + ").");
            return getHumanMove(board, humanColor);
        }
        if (!board.isLegal(letterNumberToNumberNumber(ret), humanColor)) {
            System.out.println("That is not a legal move.");
            return getHumanMove(board, humanColor);
        }
        return ret;
    }

    public static void congratWinner(CellState theWinner, CellState computer, CellState human) {
        if (theWinner == human) {
            System.out.println("You win!");
        } else if (theWinner == computer) {
            System.out.println("You lose.");
        } else {
            System.out.println("Tie game.");
        }
    }

    public static int[] getRandomMove(Board board, CellState color) {
        int[][] legals = board.legalMoves(color);
        Random ran = new Random();
        int r = ran.nextInt(legals.length);
        return legals[r].clone();
    }

    public static void startComputerVsHumanGameWithGUI(Player computer) {
        Board board = new Board();
        CellState turn = CellState.BLACK;
        int binaryHumanGoFirst = 0;
        while (binaryHumanGoFirst != 1 && binaryHumanGoFirst != 2) {
            binaryHumanGoFirst = intInput("Enter 1 to go first, 2 to go second. ");
        }
        CellState humanColor;
        if (binaryHumanGoFirst == 1) {
            computer.color = CellState.WHITE;
            humanColor = CellState.BLACK;
        } else {
            computer.color = CellState.BLACK;
            humanColor = CellState.WHITE;
        }
        final GUI g = new GUI();
        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                g.setVisible(true);
            }
        });
        g.update(board, computer.color, humanColor, humanColor == CellState.BLACK);

        ////////////
        displayBoard(board, computer.color, humanColor, humanColor == CellState.BLACK);
        while (winner(board) == null) {
            if (turn == humanColor) {
                if (board.legalMoves(humanColor).length == 0) {
                    JOptionPane.showMessageDialog(null, "You have no moves.","Message", JOptionPane.INFORMATION_MESSAGE);
                    //input("You have no moves. Press Enter.");
                } else {
                    int[] humanMove = new int[]{-1,-1};
                    while (g.currentMove[0] == -1 || !board.isLegal(g.currentMove, humanColor)) {
                        g.enableAll();
                        humanMove = g.currentMove;
                    }
                    g.update(board, computer.color, humanColor, false);
                    displayBoard(board, computer.color, humanColor, false);
                    board.updateCell(humanMove, humanColor);
                    board.adjust(humanMove);
                }
            } else if (turn == computer.color) {
                if (board.legalMoves(computer.color).length == 0) {
                    JOptionPane.showMessageDialog(null, "I have no moves.","Message", JOptionPane.INFORMATION_MESSAGE);
                    //System.out.println("I have no moves. Press Enter.");
                } else {
                    System.out.println("Computer is thinking...");
                    int[] computerMove = getMove(computer, board);
                    System.out.println("I will place my piece in square " + numberNumberToLetterNumber(computerMove));
                    board.updateCell(computerMove, computer.color);
                    displayBoard(board, computer.color, humanColor, false);
                    g.update(board, computer.color, humanColor, false);
                    System.out.println();
                    System.out.println("Computer's piece has been placed " + "in " + numberNumberToLetterNumber(computerMove)+". Press Enter to flip over pieces.");
                    board.adjust(computerMove);
                }
            }
            turn = enemy(turn);

            g.update(board, computer.color, humanColor, turn == humanColor);
        }
        CellState theWinner = winner(board);
        displayBoard(board, computer.color, humanColor, false);
        congratWinner(theWinner, computer.color, humanColor);



    }

    public static void startComputerVsHumanGame(Player computer) {
        displayInstruct();
        Board board = new Board();
        CellState turn = CellState.BLACK;
        int binaryHumanGoFirst = 0;
        while (binaryHumanGoFirst != 1 && binaryHumanGoFirst != 2) {
            binaryHumanGoFirst = intInput("Enter 1 to go first, 2 to go second. ");
        }
        CellState humanColor;
        if (binaryHumanGoFirst == 1) {
            computer.color = CellState.WHITE;
            humanColor = CellState.BLACK;
        } else {
            computer.color = CellState.BLACK;
            humanColor = CellState.WHITE;
        }
        displayBoard(board, computer.color, humanColor, humanColor == CellState.BLACK);
        while (winner(board) == null) {
            if (turn == humanColor) {
                if (board.legalMoves(humanColor).length == 0) {
                    input("You have no moves. Press Enter.");
                } else {
                    int[] humanMove = letterNumberToNumberNumber(getHumanMove(board, humanColor));
                    board.updateCell(humanMove, humanColor);
                    displayBoard(board, computer.color, humanColor, false);
                    System.out.println();
                    System.out.println("Your piece has been placed. Press Enter again to flip over pieces.");
                    input("");
                    board.adjust(humanMove);
                }
            } else if (turn == computer.color) {
                if (board.legalMoves(computer.color).length == 0) {
                    System.out.println("I have no moves. Press Enter.");
                } else {
                    System.out.println("Computer is thinking...");
                    int[] computerMove = getMove(computer, board);
                    System.out.println("I will place my piece in square " + numberNumberToLetterNumber(computerMove));
                    board.updateCell(computerMove, computer.color);
                    displayBoard(board, computer.color, humanColor, false);
                    System.out.println();
                    System.out.println("Computer's piece has been placed " + "in " + numberNumberToLetterNumber(computerMove)+". Press Enter to flip over pieces.");
                    input("");
                    board.adjust(computerMove);
                }
            }
            turn = enemy(turn);
            displayBoard(board, computer.color, humanColor, turn == humanColor);
        }
        CellState theWinner = winner(board);
        congratWinner(theWinner, computer.color, humanColor);
    }

    public static void startComputerVsComputerGame(Player computer1, Player computer2, int binaryComputer1GoFirst) {
        /**
         * computer2 is human for printing purposes
         * binaryComputer1GoFirst should be 0 or 1
         */
        Board board = new Board();
        CellState turn = CellState.BLACK;
        if (binaryComputer1GoFirst != 0) {
            computer1.color = CellState.BLACK;
            computer2.color = CellState.WHITE;
        } else {
            computer2.color = CellState.BLACK;
            computer1.color = CellState.WHITE;
        }
        displayBoard(board, computer1.color, computer2.color, false);
        while (winner(board) == null) {
            if (turn == computer2.color) {
                if (board.legalMoves(computer2.color).length == 0) {
                    System.out.println("computer2 has no moves.");
                } else {
                    int[] computer2Move = getMove(computer2, board);//getRandomMove(board, computer2.color); // to test a computer against a random mover
                    System.out.println("computer2 will place its piece in square " + numberNumberToLetterNumber(computer2Move));
                    board.updateCell(computer2Move, computer2.color);
                    displayBoard(board, computer1.color, computer2.color, false);
                    //input("");
                    //board.adjust(computer2Move);
                    board = boardAdjusted(board, computer2Move);
                }
            } else if (turn == computer1.color) {
                if (board.legalMoves(computer1.color).length == 0) {
                    System.out.println("computer1 has no moves.");
                } else {
                    int[] computer1Move = getMove(computer1, board);
                    System.out.println("computer1 will place its piece in square " + numberNumberToLetterNumber(computer1Move));
                    board.updateCell(computer1Move, computer1.color);
                    displayBoard(board, computer1.color, computer2.color, false);
                    //input("");
                    //board.adjust(computer1Move);
                    board = boardAdjusted(board, computer1Move);
                }
            }
            displayBoard(board, computer1.color, computer2.color, false);
            turn = enemy(turn);
        }
        CellState theWinner = winner(board);
        congratWinner(theWinner, computer1.color, computer2.color);
    }

    public static void main(String[] args) {
        // one can experiment with different stats, which offer different AI strategies
        Player computer1 = new Player(CellState.EMPTY, 5,100,10000,0,30); // tested and tried
        Player computer2 = new Player(CellState.EMPTY, 1,400,8000,0,700);
        Player computer3 = new Player(CellState.EMPTY, 1,100,1000,0,10);
        Player computer4 = new Player(CellState.EMPTY, -3,365,9463,0,700);
        Player computer5 = new Player(CellState.EMPTY, 1,100,1000,0,1);
        Player computer6 = new Player(CellState.EMPTY, 1,2,3,0,1);
        Player computer7 = new Player(CellState.EMPTY, 5,100,10000,0,30);
        Player computer8 = new Player(CellState.EMPTY, 1,1,1,0,1);
        Player computer9 = new Player(CellState.EMPTY, -1,100,1000,0,0);
        //startComputerVsHumanGame(computer1);
        startComputerVsHumanGameWithGUI(computer1);
        //startComputerVsComputerGame(computer1, computer9, 0);

    }
}
