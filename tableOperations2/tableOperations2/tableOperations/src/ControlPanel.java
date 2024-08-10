import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.text.ParseException;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.Image;
import org.w3c.dom.ls.LSOutput;

public class ControlPanel extends JPanel implements ActionListener, IPrintable {
    MainPanel mainPanel;
    JPanel buttonPanel;
    JButton newQuestionButton;
    JButton rulesButton;
    JTextArea description;
    boolean busy = false; // for new question button
    private Thread thread = null;  // for new question button
    HashSet<String> uniqueQuestions;
    HashSet<String> numberedQuestions;
    // for pdf
    JButton printButton;
    JLabel arrow;
    private String[] gameConcept = { "Harf", "Renk", "Rakam" };
    JComboBox<String> boyut, copylist, type;


    JComboBox<String> letcolnum;

    JButton ayarlar;

    JButton kopyala, cik, yazdir;


    JSpinner startIndex, endIndex;



    private String[] kopyalamaSecenekleri = { "Yan Yana", "Alt Alta", "Soru", "Cevap" };

    public ControlPanel(MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout());
        // Initialize components
        buttonPanel = new JPanel(new GridLayout(2, 3)); // Adjusted grid layout to fit all components
        Font font = new Font("Windows", Font.PLAIN, 14);

        uniqueQuestions = new HashSet<>();

        // Add button panel to the control panel
        createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

    }

    void createButtonPanel() {
        // Set FlowLayout for buttonPanel to keep all components in a single row
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        // Add "New Question" button
        newQuestionButton = new JButton("New Question");
        newQuestionButton.addActionListener(this);
        buttonPanel.add(newQuestionButton);

        // Add "Print PDF" button
        printButton = new JButton("Print Question");
        printButton.addActionListener(this);
        buttonPanel.add(printButton);

        startIndex = new JSpinner();
        JComponent startSpinnerEditor = startIndex.getEditor();
        JFormattedTextField jftf = ((JSpinner.DefaultEditor) startSpinnerEditor).getTextField();
        jftf.setColumns(3);

        endIndex = new JSpinner();
        JComponent endSpinnerEditor = endIndex.getEditor();
        JFormattedTextField jftfend = ((JSpinner.DefaultEditor) endSpinnerEditor).getTextField();
        jftfend.setColumns(3);

        //Create description
        mainPanel.questionPannel.answer.generateColorCode();
        description = new JTextArea("Soru kodu: " + mainPanel.questionPannel.question.getColorCode() + "\nCevap kodu: " + mainPanel.questionPannel.answer.getColorCode());
        description.setVisible(true);
        description.setEditable(false);
        description.setBackground(Color.WHITE);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("Verdana", Font.PLAIN, 14));
        description.setPreferredSize(new Dimension(200, 60));
        add(description);
        // Create a panel for label and JComboBox to keep them together
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel label = new JLabel("Sekil:");
        comboPanel.add(label);

        letcolnum = new JComboBox<>(gameConcept);
        letcolnum.setPreferredSize(new Dimension(100, 30)); // Ensure JComboBox is visible
        letcolnum.addActionListener(this);
        letcolnum.setSelectedIndex(1);

        comboPanel.add(letcolnum);

        // Add comboPanel to buttonPanel
        buttonPanel.add(comboPanel);

        ayarlar = new JButton("Ayarlar");
        ayarlar.addActionListener(this);
        buttonPanel.add(ayarlar);

        kopyala = new JButton("Kopyala");
        kopyala.addActionListener(this);
        buttonPanel.add(kopyala);

        yazdir = new JButton("Generate Questions PDF");
        yazdir.addActionListener((ActionEvent event) -> {
            yazdir.setText("...");
            new Thread(() -> {
                try {
                    buildPDF(this);
                } catch (FileNotFoundException | DocumentException e) {
                    e.printStackTrace();
                } finally {
                    yazdir.setText("Yazdır");
                }
            }).start();
        });
        buttonPanel.add(yazdir);

        cik = new JButton("\u00c7\u0131k");
        cik.addActionListener(this);

        copylist = new JComboBox(kopyalamaSecenekleri);
        buttonPanel.add(copylist);

        buttonPanel.add(new JLabel("İlk :"));
        buttonPanel.add(startIndex);
        buttonPanel.add(new JLabel("Son :"));
        buttonPanel.add(endIndex);

        rulesButton = new JButton("Kurallar");
        rulesButton.addActionListener(this);
        buttonPanel.add(rulesButton);

        // Add buttonPanel to ControlPanel
        add(buttonPanel, BorderLayout.SOUTH);
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
                        int numberOfAnswers = -1;
                        String key;
                        do {
                            key = "";
                            mainPanel.questionPannel.question = new Squares(4, 4);
                            ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
                            // Take a deep copy
                            for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                                newBoard.add(new ArrayList<>(row));
                            }
                            mainPanel.questionPannel.answer.board = newBoard;
                            mainPanel.questionPannel.answer.askQuestion(mainPanel.questionInputPanel.numberOfOperations.getSelectedIndex());
                            Solution solution = new Solution(mainPanel.questionPannel.question,mainPanel.questionPannel.answer);
                            numberOfAnswers = solution.getSolutionWithBruteForce();
                            key = mainPanel.questionPannel.question + " " + mainPanel.questionPannel.answer;
                        } while (numberOfAnswers != 1 && !uniqueQuestions.contains(key));
                        uniqueQuestions.add(key);
                        mainPanel.questionPannel.answer.generateColorCode();
                        description.setText("Soru kodu: " + mainPanel.questionPannel.question.getColorCode() + "\nCevap kodu: " + mainPanel.questionPannel.answer.getColorCode());
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.repaint();
                        });
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            newQuestionButton.setText("New Question");
                            mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

                            // Increase the end index by 1
                            int currentEndIndex = (Integer) endIndex.getValue();
                            endIndex.setValue(currentEndIndex + 1);
                        });
                    }
                });
                thread.start();
            }
        }

        if (source == rulesButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                rulesButton.setText("Kurallar");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                rulesButton.setText("Stop");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));

                thread = new Thread(() -> {
                    try {
                        String description = """
                    Soldaki tabloya bazı işlemler yaparak sağdaki tablo elde edilmiştir.
                    İşlemler:
                   -Tablo saat yönünde 90, 180 ya da 270 derece döndürülebilir.
                   -İki satırın yerleri değiştirilebilir.
                   -Bir sütun birer kare aşağıya doğru kaydırılabilir (tablonun altına taşan kare en üste gelir).
                    Kural:
                    Bu üç işlemden her biri en fazla bir kez uygulanabilir ve döndürme işlemi sadece bir kez yapılabilir.
                    Cevap olarak hangi işlemlerin yapıldığını sırasıyla ve aralarına virgül koyarak giriniz.
                   -Tablo döndürülmesinde dönme derecesini giriniz.
                   -Satır değiştirlmesinde değişen iki satırın harflerini yan yana giriniz.
                   -Kaydırma işleminde kaydırdığınız sütunun harfini giriniz.
                   """;
                        JOptionPane.showMessageDialog(mainPanel, description, "Kurallar", JOptionPane.INFORMATION_MESSAGE);
                    } finally {
                        busy = false;
                        SwingUtilities.invokeLater(() -> {
                            rulesButton.setText("Kurallar");
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
                mainPanel.questionPannel.printPanelContent();
            /*} catch (FileNotFoundException | DocumentException ex) {
                ex.printStackTrace();*/
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        }
        else if (source == ayarlar) {
            new Settings(mainPanel);
        }
        else if (source == kopyala) {
            kopyala();
        }
        else if (source == cik) {
            System.exit(0);
        }
        else if (source == yazdir) {

        }
    }


    void buildPDF(IPrintable printable) throws FileNotFoundException, DocumentException {
        Integer start = (Integer) startIndex.getValue();
        Integer range = (Integer) endIndex.getValue();
        com.itextpdf.text.Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream("Sorular.pdf"));
        document.open();
        for (Integer i = start; i <= range; i++) {
            BufferedImage question = printable.getQuestionImage();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(question, "png", baos);
                Image iText = Image.getInstance(baos.toByteArray());
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin()) / iText.getWidth()) * 100;
                iText.scalePercent(scaler);
                document.setPageSize(new Rectangle(document.getPageSize().getWidth(), iText.getHeight() + 20));
                document.newPage();
                document.add(new Paragraph(Integer.toString(i) + ". Soru"));
                document.add(iText);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document.close();
    }

    @Override
    public BufferedImage getQuestionImage() {
        int numberOfAnswers = -1;
        String key;
        do {
            key = "";
            mainPanel.questionPannel.question = new Squares(4, 4);
            ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
            // Take a deep copy
            for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                newBoard.add(new ArrayList<>(row));
            }
            mainPanel.questionPannel.answer.board = newBoard;
            mainPanel.questionPannel.answer.askQuestion(mainPanel.questionInputPanel.numberOfOperations.getSelectedIndex());
            Solution solution = new Solution(mainPanel.questionPannel.question,mainPanel.questionPannel.answer);
            numberOfAnswers = solution.getSolutionWithBruteForce();
            key = mainPanel.questionPannel.question + " " + mainPanel.questionPannel.answer;
        } while (numberOfAnswers != 1 && !numberedQuestions.contains(key));
        numberedQuestions.add(key);
        BufferedImage image = new BufferedImage(mainPanel.questionPannel.getWidth(),
                mainPanel.questionPannel.getHeight(),
                BufferedImage.TYPE_INT_RGB);

        mainPanel.questionPannel.paintComponent(image.createGraphics());
        int hucreBoyu = Math.min(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight()) / 6;
        BufferedImage question = image.getSubimage(
                mainPanel.questionPannel.left_space - (int) (hucreBoyu * 0.2) - 50,
                mainPanel.questionPannel.up_space - (int) (hucreBoyu * 0.2) - 10,
                mainPanel.questionPannel.board_length + (int) (hucreBoyu * 0.4) + 3,
                mainPanel.questionPannel.board_length + (int) (hucreBoyu * 0.4) + 3);
        BufferedImage answer = image.getSubimage(
                mainPanel.questionPannel.right_board - (int) (hucreBoyu * 0.2) + 435 ,
                mainPanel.questionPannel.up_space - (int) (hucreBoyu * 0.2) - 10,
                mainPanel.questionPannel.board_length + (int) (hucreBoyu * 0.4) + 3,
                mainPanel.questionPannel.board_length + (int) (hucreBoyu * 0.4) + 3);
        BufferedImage retval = new BufferedImage(
                question.getWidth() + answer.getWidth() + mainPanel.questionPannel.middle_space,
                question.getHeight(),
                BufferedImage.TYPE_INT_RGB);
        Graphics g = retval.getGraphics();
        g.setColor(Color.white);
        g.fillRect(0, 0, image.getWidth(), image.getHeight());
        g.drawImage(question, 0, 0, this);
        g.drawImage(answer, question.getWidth() + mainPanel.questionPannel.middle_space, 0, this);
        return retval;
    }

    private BufferedImage getAnswerImage() {
        return mainPanel.questionPannel.answer.getImage3();
    }

    public void kopyala() {
        int copy_mode = copylist.getSelectedIndex();
        int width = mainPanel.questionPannel.getWidth();
        int height = mainPanel.questionPannel.getHeight();

        // Debugging: Check the dimensions of the main panel
        System.out.println("Main Panel Dimensions: Width = " + width + ", Height = " + height);

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        mainPanel.questionPannel.paintComponent(g2d);
        g2d.dispose(); // Ensure graphics object is disposed properly

        // Debugging: Save the image to a file for verification
        try {
            ImageIO.write(image, "png", new File("mainPanel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Correctly extract the subimages for question and answer
        int leftX = mainPanel.questionPannel.left_space;
        int rightX = mainPanel.questionPannel.right_board;
        int upY = mainPanel.questionPannel.up_space;
        int boardLength = mainPanel.questionPannel.board_length;

        // Debugging: Print the calculated values for subimage extraction
        System.out.println("leftX: " + leftX + ", rightX: " + rightX + ", upY: " + upY + ", boardLength: " + boardLength);

        // Extract the subimages
        BufferedImage question = image.getSubimage(leftX - 1, upY - 1, boardLength + 3, boardLength + 3);
        BufferedImage answer = image.getSubimage(rightX + 450, upY - 20, boardLength + 35, boardLength + 35);

        // Debugging: Save subimages for verification
        try {
            ImageIO.write(question, "png", new File("question.png"));
            ImageIO.write(answer, "png", new File("answer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (copy_mode == 0) {
            image = new BufferedImage(question.getWidth() + answer.getWidth() + mainPanel.questionPannel.middle_space,
                    question.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(question, 0, 0, null);
            g.drawImage(answer, question.getWidth() + mainPanel.questionPannel.middle_space, 0, null);
        } else if (copy_mode == 1) {
            image = new BufferedImage(question.getWidth(),
                    question.getHeight() + answer.getHeight() + mainPanel.questionPannel.middle_space,
                    BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(question, 0, 0, null);
            g.drawImage(answer, 0, question.getHeight() + mainPanel.questionPannel.middle_space, this);
        } else if (copy_mode == 2) {
            image = question;
        } else if (copy_mode == 3) {
            image = answer;
        }

        // Debugging: Save final image for verification
        try {
            ImageIO.write(image, "png", new File("finalImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(image), null);
    }
}
