import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

public class QuestionInputPanel extends JPanel implements ActionListener {
    MainPanel mainPanel;
    JTextField questionGenerateInput;
    JButton generateButton;
    StringBuilder questionInputString;
    boolean busy = false;
    Thread thread;
    Solution solution;
    JComboBox<String> numberOfOperations;
    String[] operations = {"Random", "1", "2", "3"};

    QuestionInputPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        questionGenerateInput = new JTextField(20);
        questionGenerateInput.addActionListener((ActionEvent e) -> {});
        generateButton = new JButton("Generate question");
        generateButton.addActionListener(this);
        numberOfOperations = new JComboBox<>(operations);
        this.add(generateButton);
        this.add(questionGenerateInput);
        this.add(new JLabel("Number of operations:"));
        this.add(numberOfOperations);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
         if (source == generateButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionInputPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                mainPanel.questionInputPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                mainPanel.questionInputPanel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                thread = new Thread(() -> {
                    try {
                        solution = new Solution(mainPanel.questionPannel.question, mainPanel.questionPannel.answer);
                        int numberOfSolution = solution.getSolutionWithBruteForce();
                        //System.out.println("işlem sayısı: " + numberOfSolution);
                        // Get the number of solutions


                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionInputPanel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }
    }
}
