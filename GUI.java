import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;


public class GUI extends JFrame {
    final JButton[][] buttons;
    final int[] currentMove;
    int goFirst;

    public GUI() {
        buttons = new JButton[OthelloGame.BOARD_SIZE][OthelloGame.BOARD_SIZE];
        currentMove = new int[]{-1,-1};
        goFirst = 0;
        initUI();
    }

    public final void initUI() {

        setLayout(null);




//        JTextField field = new JTextField();
//        field.setBounds(0, 500, 500, 40);
//        add(field);


        for (int i = 0; i < OthelloGame.BOARD_SIZE; i++) {
            for (int j = 0; j < OthelloGame.BOARD_SIZE; j++) {
                final String name = "";
                final int first = i;
                final int second = j;
                buttons[i][j] = new JButton(name);
                final JButton b = buttons[i][j];
                b.setBackground(Color.green);
                b.setEnabled(false);

                b.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent event) {
                        currentMove[0] = first;
                        currentMove[1] = second;
                        disableAll();


                        //System.out.println("Pressed " + name);
                    }
                });
                buttons[i][j].setBounds(i*60, j*60, 60, 60);
                add(buttons[i][j]);

            }
        }






        setTitle("Othello Player");
        //setSize(497,519);
        setSize(700,700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }
    public void update(Board board, OthelloGame.CellState computer, OthelloGame.CellState human, boolean localHints) {
        HashMap<OthelloGame.CellState, Color> stateToColor = new HashMap<OthelloGame.CellState, Color>();
        stateToColor.put(OthelloGame.CellState.EMPTY, Color.green);
        stateToColor.put(OthelloGame.CellState.BLACK, Color.black);
        stateToColor.put(OthelloGame.CellState.WHITE, Color.white);
        for (int i = 0; i < OthelloGame.BOARD_SIZE; i++) {
            for (int j = 0; j < OthelloGame.BOARD_SIZE; j++) {
                buttons[i][j].setBackground(stateToColor.get(board.data[i][j]));
            }
        }
    }

    public void enableAll() {
        for (int i = 0; i < OthelloGame.BOARD_SIZE; i++) {
            for (int j = 0; j < OthelloGame.BOARD_SIZE; j++) {
                buttons[i][j].setEnabled(true);
            }
        }
    }

    public void disableAll() {
        for (int i = 0; i < OthelloGame.BOARD_SIZE; i++) {
            for (int j = 0; j < OthelloGame.BOARD_SIZE; j++) {
                buttons[i][j].setEnabled(false);
            }
        }
    }
}
