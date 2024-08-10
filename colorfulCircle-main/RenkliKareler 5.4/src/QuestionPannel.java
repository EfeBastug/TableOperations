import java.awt.*;
import java.util.ArrayList;
import javax.swing.*;



@SuppressWarnings("serial")
public class QuestionPannel extends JPanel {
    public Squares question;
    public Squares answer;
    public QuestionPannel() {
        this.question = new Squares(4, 8); // 4x4 board and 8 colors
        this.answer = new Squares(4, 8);
        setBackground(Color.WHITE);
        // Create a deep copy of the question board for the answer board
        deepCopyBoard(this.question, this.answer);
        this.answer.askQuestion(0);
    }

    public void deepCopyBoard(Squares source, Squares target) {
        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
        for (ArrayList<Integer> row : source.board) {
            newBoard.add(new ArrayList<>(row));
        }
        target.board = newBoard;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int size = 4; // 4x4 kare boyutu
        int componentWidth = getWidth();
        int componentHeight = getHeight();
        int cellSize = Math.min(componentWidth, componentHeight) / (size + 2);
        int boardLength = size * cellSize;
        int leftSpace = (componentWidth - boardLength) / 6;
        int upSpace = (componentHeight - boardLength) / 2;

        drawGridAndCircles(g, question, cellSize, leftSpace, upSpace);

        // Cevap tablosunu çiz (sağ tarafa)
        int leftSpaceForAnswer = componentWidth / 2 + leftSpace;
        drawGridAndCircles(g, answer, cellSize, leftSpaceForAnswer, upSpace);

    }


    private void drawGridAndCircles(Graphics g, Squares squares, int cellSize, int leftSpace, int upSpace) {
        int size = squares.size;
        int boardLength = size * cellSize;

        // Izgara çizgilerini çiz
        g.setColor(Color.BLACK);
        for (int i = 0; i <= size; i++) {
            g.drawLine(leftSpace, upSpace + i * cellSize, leftSpace + boardLength, upSpace + i * cellSize);
            g.drawLine(leftSpace + i * cellSize, upSpace, leftSpace + i * cellSize, upSpace + boardLength);
        }

        // Daireleri çiz
        squares.drawCircles(g, cellSize, leftSpace, upSpace);
        squares.drawHeaders(g, cellSize, leftSpace, upSpace);
    }

}
