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
                        description.setText("Başlangıç tablosu kodu: " + mainPanel.questionPannel.question.getColorCode() +
                                "\nSoru tablosu kodu: " + mainPanel.questionPannel.answer.getColorCode() +
                                "\nCevap: " + mainPanel.questionPannel.answer.getAnswer());
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

        for (Integer i = start; i <= range; i++) {
            ArrayList al = printable.getQuestionImage();
            BufferedImage question = (BufferedImage) al.get(0);
            String ans = (String) al.get(1);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                ImageIO.write(question, "png", baos);
                com.itextpdf.text.Image iTextImage = com.itextpdf.text.Image.getInstance(baos.toByteArray());

                // Görüntü boyutunu PDF sayfasına uygun şekilde ölçekle
                float scaler = ((document.getPageSize().getWidth() - document.leftMargin()
                        - document.rightMargin()) / iTextImage.getWidth()) * 105;
                iTextImage.scalePercent(scaler);

                // Sayfa boyutunu görüntüye göre ayarla
                //document.setPageSize(new Rectangle((float) (mainPanel.questionPannel.getWidth() * 1.4), (float) (mainPanel.questionPannel.getHeight() * 1.5)));
                document.newPage();

                // PDF'e soru ve cevabı ekle
                document.add(new Paragraph(Integer.toString(i) + ". Soru"));
                document.add(iTextImage);
                document.add(new Paragraph(""));
                document.add(new Paragraph("Cevap: " + ans));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document.close();
    }

    @Override
    public ArrayList getQuestionImage() {
        ArrayList<Object> list = new ArrayList<>();
        boolean unique = checkUnique.isSelected();
        int numberOfAnswers = -1;
        String key;
        int selectedDimension = Integer.parseInt(letDimnum.getSelectedItem().toString());

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

        // Capture the entire questionPannel as an image
        BufferedImage image = new BufferedImage(mainPanel.questionPannel.getWidth(), mainPanel.questionPannel.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        mainPanel.questionPannel.paint(g2d);
        g2d.dispose();

        list.add(image);
        list.add(mainPanel.questionPannel.answer.getAnswer());
        return list;
    }


    /*void buildPDF(IPrintable printable) throws FileNotFoundException, DocumentException {
        Integer start = (Integer) startIndex.getValue();
        Integer range = (Integer) endIndex.getValue();
        com.itextpdf.text.Document document = new Document(PageSize.A4);
        PdfWriter.getInstance(document, new FileOutputStream("Sorular.pdf"));
        document.open();
        for (Integer i = start; i <= range; i++) {
            ArrayList al = printable.getQuestionImage();
            BufferedImage question = (BufferedImage) al.get(0);
            String ans = (String) al.get(1);
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
                document.add(new Paragraph("Cevap: " + ans));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        document.close();
    }

    @Override
    public ArrayList getQuestionImage() {
        ArrayList<Object> list = new ArrayList<>();
        boolean unique = checkUnique.isSelected();
        int numberOfAnswers = -1;
        String key;
        do {
            key = "";
            mainPanel.questionPannel.question = new Squares(4, 8);
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
        } while (numberOfAnswers != 1 && (!unique || !numberedQuestions.contains(key)));
        if (unique) {
            numberedQuestions.add(key);
        }
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
        list.add(retval);
        list.add(mainPanel.questionPannel.answer.getAnswer());
        return list;
    }*/


    public void kopyala() {
        int copy_mode = copylist.getSelectedIndex();
        int width = mainPanel.questionPannel.getWidth();
        int height = mainPanel.questionPannel.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        mainPanel.questionPannel.paintComponent(g2d);
        g2d.dispose();

        try {
            ImageIO.write(image, "png", new File("mainPanel.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        int leftX = mainPanel.questionPannel.left_space;
        int rightX = mainPanel.questionPannel.right_board;
        int upY = mainPanel.questionPannel.up_space;
        int boardLength = mainPanel.questionPannel.board_length;
        BufferedImage question = image.getSubimage(leftX - 60 , upY - 22, boardLength + 25, boardLength + 25);
        BufferedImage answer = image.getSubimage(1368 - rightX - boardLength, upY - 22, boardLength + 25, boardLength + 25);

        try {
            ImageIO.write(question, "png", new File("question.png"));
            ImageIO.write(answer, "png", new File("answer.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (copy_mode == 0) {
            /*image = new BufferedImage(question.getWidth() + answer.getWidth() + mainPanel.questionPannel.middle_space,
                    question.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics g = image.getGraphics();
            g.setColor(Color.white);
            g.fillRect(0, 0, image.getWidth(), image.getHeight());
            g.drawImage(question, 0, 0, null);
            g.drawImage(answer, question.getWidth() + mainPanel.questionPannel.middle_space, 0, null);
            */
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

        try {
            ImageIO.write(image, "png", new File("finalImage.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new ImageSelection(image), null);
    }


}
