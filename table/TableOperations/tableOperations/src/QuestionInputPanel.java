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
    private String[] concepts = {"Renk", "Harf", "Rakam"};
    private JRadioButton[] conceptButtons;
    private ButtonGroup conceptGroup;
    QuestionInputPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        questionGenerateInput = new JTextField(20);
        questionGenerateInput.addActionListener((ActionEvent e) -> {});


        conceptButtons = new JRadioButton[concepts.length];
        conceptGroup = new ButtonGroup();

        JPanel radioPanel = new JPanel();
        radioPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        radioPanel.setBackground(Color.LIGHT_GRAY); // Set a slightly darker background color

        for (int i = 0; i < concepts.length; i++) {
            conceptButtons[i] = new JRadioButton(concepts[i]);
            conceptGroup.add(conceptButtons[i]);
            radioPanel.add(conceptButtons[i]);
        }

        add(radioPanel);

        generateButton = new JButton("Soru üret");
        generateButton.addActionListener(this);

        numberOfOperations = new JComboBox<>(operations);

        checkForAnswers = new JButton("Sorunun Cevabını Kontrol Et");
        checkForAnswers.addActionListener(this);

        compareTables = new JButton("İki Tablo Arasında Cevap Bul");
        compareTables.addActionListener(this);

        this.add(questionGenerateInput);
        this.add(generateButton);
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
                        String selectedConcept = null;
                        for (int i = 0; i < conceptButtons.length; i++) {
                            if (conceptButtons[i].isSelected()) {
                                selectedConcept = concepts[i];
                                break;
                            }
                        }

                        String generate = "";
                        if (selectedConcept != null) {
                            if (selectedConcept.equals("Renk")) {
                                generate += questionGenerateInput.getText();
                            }
                            else if (selectedConcept.equals("Harf")) {
                                generate += switchLetterCodeToColorQuestion(questionGenerateInput.getText());
                            }
                            else if (selectedConcept.equals("Rakam")) {
                                generate += switchNumberCodeToColorQuestion(questionGenerateInput.getText());
                            }
                            else {
                                throw new IllegalArgumentException("Unknown concept.");
                            }
                        }

                        mainPanel.questionPannel.question = new Squares(generate);
                        mainPanel.questionPannel.answer = new Squares(generate);
                        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
                        //Take a deep copy
                        for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                            newBoard.add(new ArrayList<>(row));
                        }
                        mainPanel.questionPannel.answer.board = newBoard;
                        mainPanel.questionInputPanel.questionGenerateInput.setText("");
                        mainPanel.questionPannel.answer.askQuestion(this.numberOfOperations.getSelectedIndex());
                        mainPanel.questionPannel.drawTables = true;
                        StringBuilder questionCode = new StringBuilder();
                        StringBuilder answerCode = new StringBuilder();
                        mainPanel.questionPannel.question.generateColorCode();
                        mainPanel.questionPannel.answer.generateColorCode();
                        switch (selectedConcept) {
                            case "Renk":
                                questionCode.append(mainPanel.questionPannel.question.getColorCode());
                                answerCode.append(mainPanel.questionPannel.answer.getColorCode());
                                break;
                            case "Harf":
                                questionCode.append(mainPanel.questionPannel.question.switchColorCodeToLetterAnswer());
                                answerCode.append(mainPanel.questionPannel.answer.switchColorCodeToLetterAnswer());
                                break;
                            case "Rakam":
                                questionCode.append(mainPanel.questionPannel.question.switchColorCodeToNumberAnswer());
                                answerCode.append(mainPanel.questionPannel.answer.switchColorCodeToNumberAnswer());
                                break;
                            default:
                                answerCode.append("Jotaro");
                        }
                        mainPanel.controlPanel.description.setText("Başlangıç tablosu kodu: " + questionCode +
                                "\nSoru tablosu kodu: " + answerCode +
                                "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
                        String finalSelectedConcept = selectedConcept;
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.setShape(finalSelectedConcept);
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
                        dialog.setLayout(new GridLayout(5, 2, 3, 3));

                        JTextField field1 = new JTextField();
                        JTextField field2 = new JTextField();
                        JCheckBox drawTablesCheckBox = new JCheckBox("Tabloları çiz");

                        dialog.add(new JLabel("Başlangıç tablosu:"));
                        dialog.add(field1);
                        dialog.add(new JLabel("Sonuç tablosu:"));
                        dialog.add(field2);


                        ButtonGroup dialogGrouping = new ButtonGroup();
                        JRadioButton[] dialogButtons = new JRadioButton[concepts.length];
                        JPanel conceptChoice = new JPanel(new GridLayout(1, concepts.length, 3, 3));
                        for (int i = 0; i < concepts.length; i++) {
                            dialogButtons[i] = new JRadioButton(concepts[i]);
                            dialogGrouping.add(dialogButtons[i]);
                            conceptChoice.add(dialogButtons[i]);
                        }
                        dialog.add(new JLabel("Konsept Seçimi:"));
                        dialog.add(conceptChoice);

                        JButton okButton = new JButton("OK");
                        JButton cancelButton = new JButton("İptal");

                        okButton.addActionListener(e1 -> {
                            String table1 = field1.getText();
                            String table2 = field2.getText();

                            //First, we must get the gameConcept
                            String gameConcept = "";
                            for (int i = 0; i < dialogButtons.length; i++) {
                                if (dialogButtons[i].isSelected()) {
                                    gameConcept = concepts[i];
                                    break;
                                }
                            }
                            String correctedTable1 = "";
                            String correctedTable2 = "";
                            if (gameConcept != null) {
                                if (gameConcept.equals("Renk")) {
                                    correctedTable1 += table1;
                                    correctedTable2 += table2;
                                }
                                else if (gameConcept.equals("Harf")) {
                                    correctedTable1 += switchLetterCodeToColorQuestion(table1);
                                    correctedTable2 += switchLetterCodeToColorQuestion(table2);
                                }
                                else if (gameConcept.equals("Rakam")) {
                                    correctedTable1 += switchNumberCodeToColorQuestion(table1);
                                    correctedTable2 += switchNumberCodeToColorQuestion(table2);
                                }
                                else {
                                    throw new IllegalArgumentException("Unknown concept.");
                                }
                            }

                            Solution solution = new Solution(new Squares(correctedTable1), new Squares(correctedTable2));
                            int number = solution.getSolutionWithBruteForce();
                            if (drawTablesCheckBox.isSelected()) {
                                mainPanel.questionPannel.question = new Squares(correctedTable1);
                                mainPanel.questionPannel.answer = new Squares(correctedTable2);
                                mainPanel.questionPannel.answer.generateColorCode();
                                mainPanel.controlPanel.description.setText("Başlangıç tablosu kodu: " + table1 +
                                        "\nSoru tablosu kodu: " + table2 +
                                        "\n" + solution.getSolutions());
                                mainPanel.questionPannel.drawTables = true;
                                String finalGameConcept = gameConcept;
                                SwingUtilities.invokeLater(() -> {
                                    mainPanel.questionPannel.setShape(finalGameConcept);
                                    mainPanel.questionPannel.repaint();
                                });
                            }
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

    public String switchLetterCodeToColorQuestion(String letterCode) {
        StringBuilder colorCode = new StringBuilder(); // Performans için StringBuilder kullanıyoruz

        for (int i = 0; i < letterCode.length(); i++) {
            char letter = letterCode.charAt(i);
            if (letter == 'f') colorCode.append("k"); // F -> Kırmızı
            else if (letter == 'd') colorCode.append("y"); // D -> Yeşil
            else if (letter == 'c') colorCode.append("l"); // C -> Lacivert
            else if (letter == 'g') colorCode.append("s"); // G -> Sarı
            else if (letter == 'a') colorCode.append("g"); // A -> Gri
            else if (letter == 'h') colorCode.append("t"); // H -> Turuncu
            else if (letter == 'b') colorCode.append("m"); // B -> Mor
            else if (letter == 'e') colorCode.append("p"); // E -> Pembe
            else if (letter == 'ı') colorCode.append("b"); // I -> Beyaz
            else if (letter == ',') continue; // Virgülleri atla
        }

        return colorCode.toString();
    }


    public String switchNumberCodeToColorQuestion(String numberCode) {
        StringBuilder colorCode = new StringBuilder(); // Performans için StringBuilder kullanıyoruz

        for (int i = 0; i < numberCode.length(); i++) {
            char number = numberCode.charAt(i);
            if (number == '5') colorCode.append("k"); // 5 -> Kırmızı
            else if (number == '3') colorCode.append("y"); // 3 -> Yeşil
            else if (number == '2') colorCode.append("l"); // 2 -> Lacivert
            else if (number == '6') colorCode.append("s"); // 6 -> Sarı
            else if (number == '0') colorCode.append("g"); // 0 -> Gri
            else if (number == '7') colorCode.append("t"); // 7 -> Turuncu
            else if (number == '1') colorCode.append("m"); // 1 -> Mor
            else if (number == '4') colorCode.append("p"); // 4 -> Pembe
            else if (number == '8') colorCode.append("b"); // 8 -> Beyaz
            else if (number == ',') continue; // Virgülleri atla
        }

        return colorCode.toString();
    }

}