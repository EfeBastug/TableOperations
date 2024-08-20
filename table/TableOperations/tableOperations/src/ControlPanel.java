import java.awt.*;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;

import com.itextpdf.text.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Image;

public class ControlPanel extends JPanel implements ActionListener, IPrintable {
    MainPanel mainPanel;
    JPanel buttonPanel;
    JButton newQuestionButton;
    JButton rulesButton;
    JTextArea description;
    JCheckBox checkUnique;
    boolean busy = false; // for new question button
    private Thread thread = null;  // for new question button
    HashSet<String> uniqueQuestions;
    HashSet<String> numberedQuestions;
    // for pdf
    JButton printButton;

    final String[] kopyalamaSecenekleri = { "Yan Yana", "Alt Alta", "Soru", "Cevap" };
    final String[] gameConcept = { "Harf", "Renk", "Rakam" };
    final String[] tableDimension = { "3", "4", "5" };


    JComboBox<String> copylist; // for kopyalamaSecenekleri
    JComboBox<String> letgameConcept; // for gameConcept
    JComboBox<String> letDimnum;// for tableDimension



    JButton ayarlar;

    JButton kopyala, cik, yazdir;

    JSpinner startIndex, endIndex;


    public ControlPanel( MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        setLayout(new BorderLayout());
        buttonPanel = new JPanel(new GridLayout(2, 3));
        uniqueQuestions = new HashSet<>();
        numberedQuestions = new HashSet<>();
        createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

    }

    void createButtonPanel() {
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        newQuestionButton = new JButton("Yeni Soru");
        newQuestionButton.addActionListener(this);
        buttonPanel.add(newQuestionButton);

        startIndex = new JSpinner();
        JComponent startSpinnerEditor = startIndex.getEditor();
        JFormattedTextField jftf = ((JSpinner.DefaultEditor) startSpinnerEditor).getTextField();
        jftf.setColumns(3);

        endIndex = new JSpinner();
        JComponent endSpinnerEditor = endIndex.getEditor();
        JFormattedTextField jftfend = ((JSpinner.DefaultEditor) endSpinnerEditor).getTextField();
        jftfend.setColumns(3);


        description = new JTextArea(4, 4);
        description.setVisible(true);
        description.setEditable(false);
        description.setBackground(Color.WHITE);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);
        description.setFont(new Font("Verdana", Font.PLAIN, 14));
        description.setPreferredSize(new Dimension(200, 60));
        add(description);
        JPanel comboPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label = new JLabel("Sekil:");

        comboPanel.add(label);
        letgameConcept = new JComboBox<>(gameConcept);
        letgameConcept.setPreferredSize(new Dimension(100, 30));
        letgameConcept.addActionListener(this);
        letgameConcept.setSelectedIndex(1);
        comboPanel.add(letgameConcept);
        buttonPanel.add(comboPanel);


        JLabel labelDim = new JLabel("Boyut:");
        comboPanel.add(labelDim);

        letDimnum = new JComboBox<>(tableDimension);
        letDimnum.setPreferredSize(new Dimension(100, 30));
        letDimnum.addActionListener(this);
        letDimnum.setSelectedIndex(1);
        comboPanel.add(letDimnum);



        JPanel comboPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel label1 = new JLabel("Boyut:");
        comboPanel1.add(label1);
        buttonPanel.add(new JLabel("İlk :"));
        buttonPanel.add(startIndex);
        buttonPanel.add(new JLabel("Son :"));
        buttonPanel.add(endIndex);



        yazdir = new JButton("Soru Oluştur");
        yazdir.addActionListener((ActionEvent event) -> {
            yazdir.setText("...");
            new Thread(() -> {
                try {
                    buildPDF(this);
                } catch (FileNotFoundException | DocumentException e) {
                    e.printStackTrace();
                } finally {
                    yazdir.setText("Soru Oluştur");
                }
            }).start();
        });
        buttonPanel.add(yazdir);

        ayarlar = new JButton("Ayarlar");
        ayarlar.addActionListener(this);
        buttonPanel.add(ayarlar);

        copylist = new JComboBox(kopyalamaSecenekleri);
        buttonPanel.add(copylist);

        kopyala = new JButton("Kopyala");
        kopyala.addActionListener(this);
        buttonPanel.add(kopyala);


        printButton = new JButton("Bu Soruyu Yazdır");
        printButton.addActionListener(this);
        buttonPanel.add(printButton);

        rulesButton = new JButton("Kurallar");
        rulesButton.addActionListener(this);
        buttonPanel.add(rulesButton);

        checkUnique = new JCheckBox("Önceden üretilmiş sorular üretilmesin");
        checkUnique.addActionListener(this);
        buttonPanel.add(checkUnique);


        add(buttonPanel, BorderLayout.SOUTH);
    }


    public String getShape(){
        return letgameConcept.getSelectedItem().toString();
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        if (source == newQuestionButton) {
            if (busy) {
                busy = false;
                if (thread != null && thread.isAlive()) {
                    thread.interrupt();
                    mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                }
                newQuestionButton.setText("Yeni Soru");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            } else {
                busy = true;
                newQuestionButton.setText("Durdur");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                thread = new Thread(() -> {
                    try {
                        boolean unique = checkUnique.isSelected();
                        int numberOfAnswers = -1;
                        String key;
                        int selectedDimension = Integer.parseInt(letDimnum.getSelectedItem().toString());

                        do {
                            key = "";
                            mainPanel.questionPannel.question = new Squares(selectedDimension, 8);
                            mainPanel.questionPannel.answer = new Squares(selectedDimension, 8);

                            ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
                            // Take a deep copy
                            for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                                newBoard.add(new ArrayList<>(row));
                            }
                            mainPanel.questionPannel.answer.board = newBoard;
                            mainPanel.questionPannel.answer.askQuestion(mainPanel.questionInputPanel.numberOfOperations.getSelectedIndex());

                            Solution solution = new Solution(mainPanel.questionPannel.question, mainPanel.questionPannel.answer);
                            numberOfAnswers = solution.getSolutionWithBruteForce();
                            key = mainPanel.questionPannel.question + " " + mainPanel.questionPannel.answer;

                            // Eğer benzersiz bir soru istiyorsak ve bu soru zaten varsa, döngü devam edecek
                        } while (numberOfAnswers != 1 && (!unique || uniqueQuestions.contains(key)));

                        if (unique) {
                            uniqueQuestions.add(key);
                        }

                        mainPanel.questionPannel.answer.generateColorCode();


                        switch(getShape()){
                            case "Rakam":
                                description.setText("Başlangıç tablosu kodu: " + mainPanel.questionPannel.question.switchColorCodeToNumberQuestion() +
                                        "\nSoru tablosu kodu: " + mainPanel.questionPannel.answer.switchColorCodeToNumberQuestion() +
                                        "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
                            break;
                            case "Harf":
                                description.setText("Başlangıç tablosu kodu: " + mainPanel.questionPannel.question.switchColorCodeToLetterQuestion() +
                                        "\nSoru tablosu kodu: " + mainPanel.questionPannel.answer.switchColorCodeToLetterQuestion() +
                                        "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
                            break;
                            case "Renk":
                                description.setText("Başlangıç tablosu kodu: " + mainPanel.questionPannel.question.getColorCode() +
                                        "\nSoru tablosu kodu: " + mainPanel.questionPannel.answer.getColorCode() +
                                        "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
                            break;
                        }


                        mainPanel.questionPannel.drawTables = true;
                        SwingUtilities.invokeLater(() -> {
                            mainPanel.questionPannel.setShape(getShape());
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
                rulesButton.setText("Durdur");
                mainPanel.questionPannel.setCursor(new Cursor(Cursor.WAIT_CURSOR));
                thread = new Thread(() -> {
                    try {
                        String rules = """
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
                   
                   Renk kodları: Gri -> g, Kırmızı -> k, Lacivert -> l, Mor -> m, Pembe -> p, Sarı -> s, Turuncu -> t, Yeşil -> y
                   """;
                        JOptionPane.showMessageDialog(mainPanel, rules, "Kurallar", JOptionPane.INFORMATION_MESSAGE);
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

    }



    void buildPDF(IPrintable printable) throws FileNotFoundException, DocumentException {
        Integer start = (Integer) startIndex.getValue();
        Integer range = (Integer) endIndex.getValue();
        com.itextpdf.text.Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream("Sorular.pdf"));
        document.open();

        int questionsPerPage = 2; // Number of questions per page
        int currentQuestionIndex = start;

        while (currentQuestionIndex <= range) {
            // Create a new page if needed
            if (currentQuestionIndex > start && (currentQuestionIndex - start) % questionsPerPage == 0) {
                document.newPage();
            }

            for (int i = 0; i < questionsPerPage && currentQuestionIndex <= range; i++, currentQuestionIndex++) {
                ArrayList<Object> al = printable.getQuestionImage();
                BufferedImage sideBySideImage = (BufferedImage) al.get(0);
                String ans = (String) al.get(1);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                try {
                    ImageIO.write(sideBySideImage, "png", baos);
                    com.itextpdf.text.Image iTextImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());

                    // Scale the image to fit the page
                    float scaler = ((document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin()) / iTextImage.getWidth()) * 100; // Adjust scale if necessary
                    iTextImage.scalePercent(scaler);

                    // Add question and answer to the PDF
                    document.add(new Paragraph(Integer.toString(currentQuestionIndex) + ". Soru"));
                    document.add(iTextImage);
                    document.add(new Paragraph(""));
                    document.add(new Paragraph("Cevap: " + ans));

                    // Add a space after each question/answer pair, except the last one on the page
                    if (i < questionsPerPage - 1 && currentQuestionIndex <= range) {
                        document.add(new Paragraph(" "));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        document.close();
    }
    @Override
    public ArrayList<Object> getQuestionImage() {
        ArrayList<Object> list = new ArrayList<>();
        boolean unique = checkUnique.isSelected();
        int numberOfAnswers = -1;
        String key;
        int selectedDimension = Integer.parseInt(letDimnum.getSelectedItem().toString());
        String selectedGameConcept = letgameConcept.getSelectedItem().toString();

        do {
            key = "";
            mainPanel.questionPannel.question = new Squares(selectedDimension, 8);
            mainPanel.questionPannel.answer = new Squares(selectedDimension, 8);

            ArrayList<ArrayList<Integer>> newBoard = new ArrayList<>();
            // Deep copy the board
            for (ArrayList<Integer> row : mainPanel.questionPannel.question.board) {
                newBoard.add(new ArrayList<>(row));
            }
            mainPanel.questionPannel.answer.board = newBoard;
            mainPanel.questionPannel.answer.askQuestion(mainPanel.questionInputPanel.numberOfOperations.getSelectedIndex());

            Solution solution = new Solution(mainPanel.questionPannel.question, mainPanel.questionPannel.answer);
            numberOfAnswers = solution.getSolutionWithBruteForce();
            key = mainPanel.questionPannel.question + " " + mainPanel.questionPannel.answer;

            // Ensure unique questions if requested
        } while (numberOfAnswers != 1 && (!unique || uniqueQuestions.contains(key)));

        if (unique) {
            uniqueQuestions.add(key);
        }

        // Generate side-by-side image
        BufferedImage sideBySideImage = exportSideBySideImage();

        list.add(sideBySideImage);
        list.add(mainPanel.questionPannel.answer.getAnswer());
        return list;
    }

    private BufferedImage exportSideBySideImage() {
        // Get the question and answer objects
        Squares question = mainPanel.questionPannel.question;
        Squares answer = mainPanel.questionPannel.answer;

        // Determine sizes
        int questionSize = question.size;
        int answerSize = answer.size;
        int cellSize = Math.min(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight()) / (Math.max(questionSize, answerSize) + 2);
        int questionBoardLength = questionSize * cellSize;
        int answerBoardLength = answerSize * cellSize;

        // Calculate total width and height with additional margins
        int leftSpace = 40;   // Increased left space
        int topSpace = 40;    // Increased top space
        int betweenSpace = 50;  // Increased space between question and answer
        int rightSpace = 20;  // Increased right space
        int bottomSpace = 40; // Increased bottom space

        int totalWidth = questionBoardLength + answerBoardLength + betweenSpace + leftSpace + rightSpace;
        int maxHeight = Math.max(questionBoardLength, answerBoardLength) + topSpace + bottomSpace;

        // Create BufferedImage
        BufferedImage bufferedImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Fill with white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Draw question and answer side by side
        g2d.translate(leftSpace, topSpace);

        // Draw question
        mainPanel.questionPannel.drawforCopy(getShape(), g2d, question, cellSize, 0, 0);
        question.drawHeaders(g2d, cellSize, 0, 0);

        // Move to the position for drawing the answer
        g2d.translate(questionBoardLength + betweenSpace, 0);

        // Draw answer
        mainPanel.questionPannel.drawforCopy(getShape(),g2d, answer, cellSize, 0, 0);
        answer.drawHeaders(g2d, cellSize, 0, 0);

        g2d.dispose();

        return bufferedImage;
    }



    public void kopyala() {
        int copy_mode = copylist.getSelectedIndex();

        switch (copy_mode) {
            case 0:
                // Side-by-Side Layout
                exportSideBySide();
                break;
            case 1:
                // Top-to-Bottom Layout
                exportTopToBottom();
                break;
            case 2:
                // Export Question
                exportAsImage(mainPanel.questionPannel.question, "soru.png");
                break;
            case 3:
                // Export Answer
                exportAsImage(mainPanel.questionPannel.answer, "cevap.png");
                break;

            default:
                System.out.println("Invalid copy mode selected.");
                break;
        }
    }

    private void exportAsImage(Squares squares, String imageFileName) {
        int size = squares.size;
        int cellSize = Math.min(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight()) / (size + 2);
        int boardLength = size * cellSize;

        // Define spaces
        int leftSpace = cellSize / 2 -10;  // Space for headers
        int upSpace = cellSize / 2 ;
        int rightSpace = 10;  // Small margin on the right
        int downSpace = 10;   // Small margin on the bottom

        // Create BufferedImage
        BufferedImage bufferedImage = new BufferedImage(boardLength + leftSpace + rightSpace, boardLength + upSpace + downSpace, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Fill with white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Draw the grid and headers
        mainPanel.questionPannel.drawforCopy(getShape(),g2d, squares, cellSize, leftSpace, upSpace);
        squares.drawHeaders(g2d, cellSize, leftSpace, upSpace);

        // Dispose graphics
        g2d.dispose();

        // Save the BufferedImage as PNG
        try {
            File outputfile = new File(imageFileName);
            ImageIO.write(bufferedImage, "png", outputfile);
            System.out.println("Image saved as PNG: " + outputfile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Copy to clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(bufferedImage), null);
    }

    private void exportSideBySide() {
        // Get the question and answer objects
        Squares question = mainPanel.questionPannel.question;
        Squares answer = mainPanel.questionPannel.answer;

        // Determine sizes
        int questionSize = question.size;
        int answerSize = answer.size;
        int cellSize = Math.min(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight()) / (Math.max(questionSize, answerSize) + 2);
        int questionBoardLength = questionSize * cellSize;
        int answerBoardLength = answerSize * cellSize;

        // Calculate total width and height with additional margins
        int leftSpace = 40;   // Increased left space
        int topSpace = 40;    // Increased top space
        int betweenSpace = 50;  // Increased space between question and answer
        int rightSpace = 20;  // Increased right space
        int bottomSpace = 40; // Increased bottom space

        int totalWidth = questionBoardLength + answerBoardLength + betweenSpace + leftSpace + rightSpace;
        int maxHeight = Math.max(questionBoardLength, answerBoardLength) + topSpace + bottomSpace;

        // Create BufferedImage
        BufferedImage bufferedImage = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Fill with white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Draw question and answer side by side
        g2d.translate(leftSpace, topSpace);

        // Draw question
        mainPanel.questionPannel.drawforCopy(getShape(),g2d, question, cellSize, 0, 0);
        question.drawHeaders(g2d, cellSize, 0, 0);

        // Move to the position for drawing the answer
        g2d.translate(questionBoardLength + betweenSpace, 0);

        // Draw answer
        mainPanel.questionPannel.drawforCopy(getShape(),g2d, answer, cellSize, 0, 0);
        answer.drawHeaders(g2d, cellSize, 0, 0);

        g2d.dispose();

        // Save and copy to clipboard
        saveAndCopyImage(bufferedImage, "side_by_side.png");
    }

    private void exportTopToBottom() {
        // Get the question and answer objects
        Squares question = mainPanel.questionPannel.question;
        Squares answer = mainPanel.questionPannel.answer;

        // Determine sizes
        int questionSize = question.size;
        int answerSize = answer.size;
        int cellSize = Math.min(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight()) / (Math.max(questionSize, answerSize) + 2);
        int questionBoardLength = questionSize * cellSize;
        int answerBoardLength = answerSize * cellSize;

        // Calculate total width and height with additional margins
        int leftSpace = 30;   // Increased left space
        int topSpace = 30;    // Increased top space
        int betweenSpace = 60; // Increased space between question and answer
        int rightSpace = 30;  // Increased right space
        int bottomSpace = 30; // Increased bottom space

        int maxWidth = Math.max(questionBoardLength, answerBoardLength) + leftSpace + rightSpace;
        int totalHeight = questionBoardLength + answerBoardLength + betweenSpace + topSpace + bottomSpace;

        // Create BufferedImage
        BufferedImage bufferedImage = new BufferedImage(maxWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();

        // Fill with white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight());

        // Draw question on top
        g2d.translate(leftSpace, topSpace);
        mainPanel.questionPannel.drawforCopy(getShape(), g2d, question, cellSize, 0, 0);
        question.drawHeaders(g2d, cellSize, 0, 0);

        // Draw answer below question
        g2d.translate(0, questionBoardLength + betweenSpace);
        mainPanel.questionPannel.drawforCopy(getShape(), g2d, answer, cellSize, 0, 0);
        answer.drawHeaders(g2d, cellSize, 0, 0);

        g2d.dispose();

        // Save and copy to clipboard
        saveAndCopyImage(bufferedImage, "top_to_bottom.png");
    }

    private void saveAndCopyImage(BufferedImage bufferedImage, String fileName) {
        // Save the BufferedImage as PNG
        try {
            File outputfile = new File(fileName);
            ImageIO.write(bufferedImage, "png", outputfile);
            System.out.println("Image saved as PNG: " + outputfile.getAbsolutePath());
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        // Set the image to the clipboard
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(bufferedImage), null);
    }


}
