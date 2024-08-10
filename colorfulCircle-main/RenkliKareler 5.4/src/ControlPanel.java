import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;

import javax.imageio.ImageIO;
import javax.swing.*;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

public class ControlPanel extends JPanel implements ActionListener {
    MainPanel mainPanel;
    JPanel buttonPanel;
    JButton newQuestionButton;
    JButton ninetyDegreeButton;
    JButton hundredEightyDegreeButton;
    JButton rotate270DegreesButton;
    JButton swapRowsButton;
    JButton shiftColumnButton;
    JButton answerButton;
    JTextArea description;
    boolean busy = false; // for new question button
    private Thread thread = null;  // for new question button

    JSpinner startIndex;
    JSpinner endIndex;
    // for pdf
    JButton printButton;


    public ControlPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout());
        // Initialize components
        buttonPanel = new JPanel(new GridLayout(1, 2));
        Font font = new Font("Windows", Font.PLAIN, 14);
        this.description = new JTextArea(description());
        description.setLineWrap(true);
        description.setFont(font);
        createButtonPanel();

        // Add button panel to the control panel
        add(buttonPanel, BorderLayout.SOUTH);
        add(description, BorderLayout.CENTER);

        startIndex = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); // example initialization
        endIndex = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1)); // example initialization


    }

    void createButtonPanel() {
        newQuestionButton = new JButton("New Question");
        newQuestionButton.addActionListener(this);
        buttonPanel.add(newQuestionButton);

        ninetyDegreeButton = new JButton("Rotate 90 Degrees");
        ninetyDegreeButton.addActionListener(this);
        buttonPanel.add(ninetyDegreeButton);

        hundredEightyDegreeButton = new JButton("Rotate 180 Degrees");
        hundredEightyDegreeButton.addActionListener(this);
        buttonPanel.add(hundredEightyDegreeButton);

        rotate270DegreesButton = new JButton("Rotate 270 Degrees");
        rotate270DegreesButton.addActionListener(this);
        buttonPanel.add(rotate270DegreesButton);

        swapRowsButton = new JButton("Swap rows");
        swapRowsButton.addActionListener(this);
        buttonPanel.add(swapRowsButton);

        shiftColumnButton = new JButton("Shift column");
        shiftColumnButton.addActionListener(this);
        buttonPanel.add(shiftColumnButton);

        answerButton = new JButton("See answer");
        answerButton.addActionListener(this);
        buttonPanel.add(answerButton);

        // add pdf properties

        printButton = new JButton("Print PDF");
        printButton.addActionListener(this);
        buttonPanel.add(printButton);

    }

    public String description() {
        return """
                Soldaki tabloya bazı işlemler yaparak sağdaki tablo elde edilmiştir. İşlemler:
                -İki satırın yerleri değiştirilebilir.
                -İki sütunun yerleri değiştirilebilir.
                -Tablo 90 derece döndürülebilir.
                -Tablo 180 derece döndürülebilir.

                Kural:
                Bu dört işlemden her biri en fazla bir kez uygulanabilir.
                Cevap olarak hangi işlemlerin yapıldığını sırasıyla ve aralarına virgül koyarak giriniz. 

                -Satır ve sütunların yer değiştirlmesinde değişen satırların ve ve sütunların harflerini giriniz.
                -Tablo döndürülmesinde ise dönme derecesini giriniz.
                """;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        // Handle new question button actions
        if (source == newQuestionButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                newQuestionButton.setText("New Question");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                newQuestionButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        // Generate a new question
                        mainPanel.questionPannel.question = new Squares(4, 4);
                        ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
                        //Take a deep copy
                        for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                            newBoard.add(new ArrayList<>(row));
                        }
                        mainPanel.questionPannel.answer.board = newBoard;
                        mainPanel.questionPannel.answer.askQuestion(mainPanel.questionInputPanel.numberOfOperations.getSelectedIndex());
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            newQuestionButton.setText("Yeni Soru");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }


        //Handle transpose operation
        else if (source == rotate270DegreesButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                rotate270DegreesButton.setText("Rotate 270 Degrees");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                rotate270DegreesButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        // Transpose the current board
                        mainPanel.questionPannel.question.rotate270Degrees();
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            rotate270DegreesButton.setText("Rotate 270 Degrees");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }

        //Handle rotating 90 degrees
        else if (source == ninetyDegreeButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                ninetyDegreeButton.setText("Rotate 90 degrees");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                ninetyDegreeButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        //Rotate the current board
                        mainPanel.questionPannel.question.rotate90Degrees();
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            ninetyDegreeButton.setText("Rotate 90 degrees");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }
        //Handle rotating 180 degrees
        else if (source == hundredEightyDegreeButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                hundredEightyDegreeButton.setText("Rotate 180 degrees");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                hundredEightyDegreeButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        mainPanel.questionPannel.question.rotate180Degrees();
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            hundredEightyDegreeButton.setText("Rotate 180 degrees");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }
        //Handle swapping rows
        else if (source == swapRowsButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                swapRowsButton.setText("Swap Rows");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                swapRowsButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                JTextField textField = new JTextField();
                int result = JOptionPane.showConfirmDialog(
                        null,
                        textField,
                        "Swap rows",
                        JOptionPane.OK_CANCEL_OPTION
                );

                if (result == JOptionPane.OK_OPTION) {
                    String inputText = textField.getText();
                    if (inputText.length() > 2) {
                        throw new InputMismatchException("Please enter only two letters.");
                    }
                    else {
                        mainPanel.questionPannel.question.swapRows(inputText.charAt(0), inputText.charAt(1));
                    }
                    System.out.println("User input: " + inputText);
                }

                thread = new Thread(() -> {
                    try {
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            swapRowsButton.setText("Swap Rows");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }
        //Handle swapping columns
        else if (source == shiftColumnButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }

                shiftColumnButton.setText("Shift Column");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                shiftColumnButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                JTextField textField = new JTextField();
                int result = JOptionPane.showConfirmDialog(
                        null,
                        textField,
                        "Shift column",
                        JOptionPane.OK_CANCEL_OPTION
                );

                if (result == JOptionPane.OK_OPTION) {
                    String inputText = textField.getText();
                    if (inputText.length() > 2) {
                        throw new InputMismatchException("Please enter only two letters.");
                    }
                    else {
                        mainPanel.questionPannel.question.shiftColumn(inputText.charAt(0));
                    }
                    System.out.println("User input: " + inputText);
                }

                thread = new Thread(() -> {
                    try {
                        JTextField field = new JTextField();
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            shiftColumnButton.setText("Shift Column");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }
        else if (source == answerButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                answerButton.setText("See answer");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                answerButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        String answer = mainPanel.questionPannel.answer.getAnswer();
                        JOptionPane.showMessageDialog(null, "The answer is: " + answer, "Answer", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            answerButton.setText("See answer");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                        });
                    }
                });
                thread.start();
            }
        }

        else if (source == printButton) {
            System.out.println("Print Button Clicked"); // Debug statement
            try {
                buildPDF();
                System.out.println("PDF build successful"); // Debug statement
            } catch (FileNotFoundException | DocumentException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    private void buildPDF() throws FileNotFoundException, DocumentException, IOException {
        com.itextpdf.text.Document document = new com.itextpdf.text.Document(PageSize.A4);
        try (FileOutputStream fos = new FileOutputStream("output.pdf")) {
            PdfWriter writer = PdfWriter.getInstance(document, fos);
            document.open();

            BufferedImage questionImage = getQuestionImage();
            if (questionImage != null) {
                addImageToDocument(document, questionImage, "Question Image");
            }

            BufferedImage answerImage = getAnswerImage();
            if (answerImage != null) {
                addImageToDocument(document, answerImage, "Answer Image");
            }

            document.add(new Paragraph(description.getText()));

            document.close(); // Ensure document is closed before finishing the method
        } catch (IOException e) {
            e.printStackTrace();
            throw e; // Re-throw to ensure the caller is aware of the issue
        } catch (DocumentException e) {
            e.printStackTrace();
            throw e; // Re-throw to ensure the caller is aware of the issue
        }
    }

    private void addImageToDocument(com.itextpdf.text.Document document, BufferedImage image, String title) throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        baos.flush(); // Ensure data is written to ByteArrayOutputStream
        com.itextpdf.text.Image pdfImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());
        baos.close(); // Close ByteArrayOutputStream

        pdfImage.scaleToFit(PageSize.A4.getWidth(), PageSize.A4.getHeight());
        pdfImage.setAlignment(com.itextpdf.text.Image.ALIGN_CENTER);
        document.add(pdfImage);
        document.add(new Paragraph(title));
    }



    private BufferedImage getQuestionImage() {
        return mainPanel.questionPannel.question.getImage();
    }

    private BufferedImage getAnswerImage() {
        return mainPanel.questionPannel.answer.getImage();
    }



}
