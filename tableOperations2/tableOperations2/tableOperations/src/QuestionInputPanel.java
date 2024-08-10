import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.*;

public class QuestionInputPanel extends JPanel implements ActionListener {
    MainPanel mainPanel;
    JTextField questionGenerateInput;
    JButton generateButton;
    JButton checkForAnswers;
    JButton compareTables;
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

        checkForAnswers = new JButton("Check Answers");
        checkForAnswers.addActionListener(this);

        compareTables = new JButton("Compare Two Tables");
        compareTables.addActionListener(this);

        this.add(generateButton);
        this.add(questionGenerateInput);
        this.add(new JLabel("Minimum Number of operations:"));
        this.add(numberOfOperations);
        this.add(checkForAnswers);
        this.add(compareTables);
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
                        String generate = questionGenerateInput.getText();
                        mainPanel.questionPannel.question = new Squares(generate);
                        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
                        //Take a deep copy
                        for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                            newBoard.add(new ArrayList<>(row));
                        }
                        mainPanel.questionPannel.answer.board = newBoard;
                        mainPanel.questionInputPanel.questionGenerateInput.setText("");
                        mainPanel.questionPannel.answer.askQuestion(this.numberOfOperations.getSelectedIndex());
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
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
        else if (source == checkForAnswers) {
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
                        JOptionPane.showMessageDialog(mainPanel, "Found " + numberOfSolution + " solutions.", "Solutions", JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(mainPanel, solution.getSolutions(), "Solutions", JOptionPane.INFORMATION_MESSAGE);
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
        else if (source == compareTables) {
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
                        JDialog dialog = new JDialog((Frame) null, "Compare Two Tables", false);
                        dialog.setLayout(new GridLayout(3, 2, 10, 10));

                        JTextField field1 = new JTextField();
                        JTextField field2 = new JTextField();
                        JCheckBox drawTablesCheckBox = new JCheckBox("Draw the tables");

                        dialog.add(new JLabel("First Table:"));
                        dialog.add(field1);
                        dialog.add(new JLabel("Second Table:"));
                        dialog.add(field2);

                        JButton okButton = new JButton("OK");
                        JButton cancelButton = new JButton("Cancel");

                        // Add action listeners to buttons
                        okButton.addActionListener(e1 -> {
                            String table1 = field1.getText();
                            String table2 = field2.getText();
                            if (drawTablesCheckBox.isSelected()) {
                                mainPanel.questionPannel.question = new Squares(table1);
                                mainPanel.questionPannel.answer = new Squares(table2);
                                mainPanel.questionPannel.answer.generateColorCode();
                                mainPanel.controlPanel.description.setText("Soru kodu: " + mainPanel.questionPannel.question.getColorCode() + "\nCevap kodu: " + mainPanel.questionPannel.answer.getColorCode());
                                SwingUtilities.invokeLater(() -> {
                                    mainPanel.questionPannel.repaint();
                                });
                            }
                            Solution solution = new Solution(new Squares(table1), new Squares(table2));
                            int numberOfSolutions = solution.getSolutionWithBruteForce();
                            // Add your comparison logic here using table1 and table2
                            JOptionPane.showMessageDialog(
                                    mainPanel,
                                    solution.getSolutions(),
                                    "Tables Comparison",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            dialog.dispose();
                        });

                        cancelButton.addActionListener(e12 -> dialog.dispose());

                        // Add buttons to the dialog
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(okButton);
                        buttonPanel.add(cancelButton);
                        dialog.add(buttonPanel);
                        dialog.add(drawTablesCheckBox);
                        // Set dialog properties
                        dialog.pack();
                        dialog.setLocationRelativeTo(mainPanel);
                        dialog.setVisible(true);
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
