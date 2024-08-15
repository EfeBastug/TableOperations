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
    boolean busy = false;
    Thread thread;
    Solution solution;
    JComboBox<String> numberOfOperations;
    String[] operations = {"Rastgele", "1", "2", "3"};

    QuestionInputPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        questionGenerateInput = new JTextField(20);
        questionGenerateInput.addActionListener((ActionEvent e) -> {});

        generateButton = new JButton("Soru üret");
        generateButton.addActionListener(this);

        numberOfOperations = new JComboBox<>(operations);

        checkForAnswers = new JButton("Sorunun Cevabını Kontrol Et");
        checkForAnswers.addActionListener(this);

        compareTables = new JButton("İki Tablo Arasında Cevap Bul");
        compareTables.addActionListener(this);

        this.add(generateButton);
        this.add(questionGenerateInput);
        this.add(new JLabel("Minimum hamle sayısı:"));
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
                        mainPanel.questionPannel.drawTables = true;
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
                        if (!mainPanel.questionPannel.drawTables) {
                            JOptionPane.showMessageDialog(mainPanel, "Lütfen soru oluşturunuz.", "Cevap", JOptionPane.INFORMATION_MESSAGE);
                            return;
                        }
                        solution = new Solution(mainPanel.questionPannel.question, mainPanel.questionPannel.answer);
                        int numberOfSolution = solution.getSolutionWithBruteForce();
                        JOptionPane.showMessageDialog(mainPanel, numberOfSolution + " tane cevap bulundu.", "Cevap", JOptionPane.INFORMATION_MESSAGE);
                        JOptionPane.showMessageDialog(mainPanel, solution.getSolutions(), "Cevap", JOptionPane.INFORMATION_MESSAGE);
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
                        JDialog dialog = new JDialog((Frame) null, "Tabloları Karşılaştır", false);
                        dialog.setLayout(new GridLayout(3, 2, 10, 10));

                        JTextField field1 = new JTextField();
                        JTextField field2 = new JTextField();
                        JCheckBox drawTablesCheckBox = new JCheckBox("Tabloları çiz");

                        dialog.add(new JLabel("Başlangıç tablosu:"));
                        dialog.add(field1);
                        dialog.add(new JLabel("Sonuç Tablosu:"));
                        dialog.add(field2);

                        JButton okButton = new JButton("OK");
                        JButton cancelButton = new JButton("İptal");

                        okButton.addActionListener(e1 -> {
                            String table1 = field1.getText();
                            String table2 = field2.getText();
                            if (drawTablesCheckBox.isSelected()) {
                                mainPanel.questionPannel.question = new Squares(table1);
                                mainPanel.questionPannel.answer = new Squares(table2);
                                mainPanel.questionPannel.answer.generateColorCode();
                                mainPanel.controlPanel.description.setText("Başlangıç tablosu kodu: " + mainPanel.questionPannel.question.getColorCode() +
                                        "\nSoru tablosu kodu: " + mainPanel.questionPannel.answer.getColorCode() +
                                        "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
                                mainPanel.questionPannel.drawTables = true;
                                SwingUtilities.invokeLater(() -> {
                                    mainPanel.questionPannel.repaint();
                                });
                            }
                            Solution solution = new Solution(new Squares(table1), new Squares(table2));
                            JOptionPane.showMessageDialog(
                                    mainPanel,
                                    solution.getSolutions(),
                                    "Tabloları Karşılaştır",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            dialog.dispose();
                        });

                        cancelButton.addActionListener(e12 -> dialog.dispose());
                        JPanel buttonPanel = new JPanel();
                        buttonPanel.add(okButton);
                        buttonPanel.add(cancelButton);
                        dialog.add(buttonPanel);
                        dialog.add(drawTablesCheckBox);
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