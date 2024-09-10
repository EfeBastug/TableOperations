import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.*;

public class QuestionPannel extends JPanel implements Cloneable{
    public Squares question;
    public Squares answer;
    MainPanel mainPanel;
    private final double PRINT_SCALING_FACTOR = 0.5;
    public boolean drawTables = false;
    String duzen = "", sorubasligi = "", cevapbasligi = "";
    boolean kayitli = true;
    Color tablo = Color.DARK_GRAY,
            tabloIc = Color.WHITE,
            cerceve = Color.BLACK,
            disipucu = Color.WHITE,
            disarka = Color.WHITE,
            cevap = Color.BLACK,
            arkaPlan = Color.WHITE,
            icipucu = Color.BLACK,
            hints = Color.BLACK,
            c1 = Color.BLUE, c2 = Color.RED, c3 = Color.YELLOW;
    int hucreBoyu; // Hücrelerin boyu
    Font font = null;
    boolean print = false; // Yazdırma durumu
    int printWidth = 0, printHeight = 0; // Kağıt boyutları
    int printX = 0, printY = 0; // Kağıdın boşlukları
    int size = 4; // 4x4 board size
    int board_length = size * 50; // Hücre boyutu
    int middle_space = 20;
    int left_space = 50;
    int up_space = 50;
    int right_board = left_space + board_length + middle_space;

    String shape;

    public QuestionPannel(String shape, MainPanel mainPanel) {
        this.mainPanel = mainPanel;
        this.question = new Squares(size, 8); // 4x4 board and 8 colors
        this.answer = new Squares(size, 8);
        setBackground(Color.WHITE);
        hucreBoyu = Math.min(this.getWidth(), this.getHeight()) / (size + 2);
        setLayout(null);
        deepCopyBoard(this.question, this.answer);
        this.answer.askQuestion(0);

        this.shape = shape;
    }

    //Başlangıç ve soru tablolarını kopyalamak için deep copy fonksiyonu
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
        if (drawTables) {
            int componentWidth = print ? (int) (getWidth() * PRINT_SCALING_FACTOR) : getWidth();
            int componentHeight = print ? (int) (getHeight() * PRINT_SCALING_FACTOR) : getHeight();
            int cellSize = Math.min(componentWidth, componentHeight) / (size + 2);
            board_length = size * cellSize;
            left_space = (int) ((componentWidth - board_length) / ((2*size) - 1.4));
            up_space = (int) ((componentHeight - board_length) / 2.5);
            //3 -> 4
            //4 -> 6.5
            //5 -> 9


            switch (shape){
                case "Harf":
                    drawGridAndLetters(g, question, cellSize, left_space - 40, up_space);
                    int leftSpaceForAnswer = componentWidth / 2 + left_space;
                    drawGridAndLetters(g, answer, cellSize, leftSpaceForAnswer, up_space);
                    break;
                case "Renk":
                    drawGridAndCircles(g, question, cellSize, left_space - 40, up_space);
                    leftSpaceForAnswer = componentWidth / 2 + left_space;
                    drawGridAndCircles(g, answer, cellSize, leftSpaceForAnswer, up_space);
                    break;
                case "Rakam":
                    drawGridAndNumbers(g, question, cellSize, left_space - 40, up_space);
                    leftSpaceForAnswer = componentWidth / 2 + left_space;
                    drawGridAndNumbers(g, answer, cellSize, leftSpaceForAnswer, up_space);
                    break;
                default:
                    drawGridAndLetters(g, question, cellSize, left_space - 40, up_space);
                    leftSpaceForAnswer = componentWidth / 2 + left_space;
                    drawGridAndLetters(g, answer, cellSize, leftSpaceForAnswer, up_space);
                    break;
            }

            //drawArrow(g, componentWidth / 2, up_space + (board_length / 2), cellSize);
        }
    }
    public void setShape(String newShape) {
        this.shape = newShape;
    }

    //Soru panelini PDF olarak yazdırma fonksiyonu
    public void printPanelContent() {
        PrintJob pj = Toolkit.getDefaultToolkit().getPrintJob(mainPanel, "Print Question Panel", new java.util.Properties());
        if (pj == null)
            return;

        Graphics g = pj.getGraphics();
        if (g != null) {
            g.setFont(new Font("Times New Roman", Font.BOLD, 18));
            int x = 0, y = 40;
            g.drawString("\u00c7i\u00e7ekler", x + 30, y);
            y += 25;
            g.setFont(new Font("Times New Roman", Font.PLAIN, 12));

            int width =  (pj.getPageDimension().width) - 60;
            java.util.StringTokenizer st = new java.util.StringTokenizer("");
            java.awt.FontMetrics fm = g.getFontMetrics();
            while (st.hasMoreTokens()) {
                String temp = st.nextToken();
                if ((x + fm.stringWidth(temp) + 30) > (30 + width)) {
                    x = 0;
                    y += 15;
                }
                g.drawString(temp, x + 30, y);
                x += (fm.stringWidth(temp + " "));
            }

            this.printWidth = (pj.getPageDimension().width);
            this.printHeight = (pj.getPageDimension().height) - 50;
            this.printX = (25);
            this.printY = ((y + 30));
            this.print = true;
            this.paintComponent(g);
            this.print = false;
            this.printX = 0;
            this.printY = 0;
            this.printWidth = 0;
            this.printHeight = 0;

            g.dispose();
        }
        pj.end();
    }

    public void drawGridAndCircles(Graphics g, Squares squares, int cellSize, int leftSpace, int upSpace) {
        int size = squares.size;
        int boardLength = size * cellSize;

        g.setColor(tabloIc);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize;
                int y = upSpace + row * cellSize;
                g.fillRect(x, y, cellSize, cellSize);
            }
        }

        g.setColor(tablo);
        for (int i = 0; i <= size; i++) {
            g.drawLine(leftSpace, upSpace + i * cellSize, leftSpace + boardLength, upSpace + i * cellSize);
            g.drawLine(leftSpace + i * cellSize, upSpace, leftSpace + i * cellSize, upSpace + boardLength);
        }


        squares.drawCircles(g, cellSize, leftSpace, upSpace);
        squares.drawHeaders(g, cellSize, leftSpace, upSpace);
    }

    void duzenAl(String dosya) {
        try {
            BufferedReader br = new BufferedReader(new FileReader("TableOperations/tableOperations/ayarlar/" + dosya));
            arkaPlan = new Color(Integer.parseInt(br.readLine()));
            setBackground(arkaPlan);
            mainPanel.getContentPane().setBackground(arkaPlan);
            cerceve = new Color(Integer.parseInt(br.readLine()));
            tabloIc = new Color(Integer.parseInt(br.readLine()));
            tablo = new Color(Integer.parseInt(br.readLine()));
            cevap = new Color(Integer.parseInt(br.readLine()));
            disipucu = new Color(Integer.parseInt(br.readLine()));
            disarka = new Color(Integer.parseInt(br.readLine()));
            icipucu = new Color(Integer.parseInt(br.readLine()));
            font = new Font(br.readLine(), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()));
            sorubasligi = br.readLine();
            cevapbasligi = br.readLine();
            mainPanel.setBounds(Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine()),
                    Integer.parseInt(br.readLine()));
            int tsize = Integer.parseInt(br.readLine());
            int tlights = Integer.parseInt(br.readLine());
            br.close();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(mainPanel, "Renk d\u00fczeni al\u0131namad\u0131", "Hata",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            kayitli = false;
        }
        setBackground(arkaPlan);
        repaint();
    }

    @Override
    public QuestionPannel clone() throws CloneNotSupportedException {
        try {
            return (QuestionPannel) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public void drawGridAndLetters(Graphics g, Squares squares, int cellSize, int leftSpace, int upSpace) {
        int size = squares.size;
        int boardLength = size * cellSize;

        // Hücreleri doldur
        g.setColor(tabloIc);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize;
                int y = upSpace + row * cellSize;
                g.fillRect(x, y, cellSize, cellSize);
            }
        }

        // Grid çizgilerini çiz
        g.setColor(tablo);
        for (int i = 0; i <= size; i++) {
            g.drawLine(leftSpace, upSpace + i * cellSize, leftSpace + boardLength, upSpace + i * cellSize);
            g.drawLine(leftSpace + i * cellSize, upSpace, leftSpace + i * cellSize, upSpace + boardLength);
        }

        // Hücrelerin içine harfleri yaz
        g.setColor(Color.BLACK); // Harflerin rengi
        Font font = new Font("Arial", Font.PLAIN, cellSize / 2); // Font ayarları
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int fontHeight = metrics.getHeight();
        int fontAscent = metrics.getAscent();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize;
                int y = upSpace + row * cellSize;

                // Mevcut hücre için sayıyı al
                int number = squares.getNumberAt(row, col);

                // Sayıyı harfe dönüştür
                char letter = (char) ('A' + number ); // Örneğin, 1 -> 'A', 2 -> 'B', ..., 26 -> 'Z'

                // Harfi ortalamak için x ve y koordinatlarını hesapla
                int letterX = x + (cellSize - metrics.stringWidth(String.valueOf(letter))) / 2;
                int letterY = y + (cellSize - fontHeight) / 2 + fontAscent;

                // Harfi çiz
                g.drawString(String.valueOf(letter), letterX, letterY);
            }
        }

        // Eğer varsa, diğer öğeleri çiz
        squares.drawHeaders(g, cellSize, leftSpace, upSpace);
    }

    public void drawGridAndNumbers(Graphics g, Squares squares, int cellSize, int leftSpace, int upSpace) {
        int size = squares.size;
        int boardLength = size * cellSize;

        // Hücreleri doldur
        g.setColor(tabloIc);
        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize;
                int y = upSpace + row * cellSize;
                g.fillRect(x, y, cellSize, cellSize);
            }
        }

        // Grid çizgilerini çiz
        g.setColor(tablo);
        for (int i = 0; i <= size; i++) {
            g.drawLine(leftSpace, upSpace + i * cellSize, leftSpace + boardLength, upSpace + i * cellSize);
            g.drawLine(leftSpace + i * cellSize, upSpace, leftSpace + i * cellSize, upSpace + boardLength);
        }

        // Hücrelerin içine rakamları yaz
        g.setColor(Color.BLACK); // Rakamların rengi
        Font font = new Font("Arial", Font.PLAIN, cellSize / 2); // Font ayarları
        g.setFont(font);

        FontMetrics metrics = g.getFontMetrics(font);
        int fontHeight = metrics.getHeight();
        int fontAscent = metrics.getAscent();

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize;
                int y = upSpace + row * cellSize;

                // Rakamı ortalamak için x ve y koordinatları
                int numberX = x + (cellSize - metrics.stringWidth(String.valueOf(squares.getNumberAt(row, col)))) / 2;
                int numberY = y + (cellSize - fontHeight) / 2 + fontAscent;

                // Rakamı çiz
                g.drawString(String.valueOf(squares.getNumberAt(row, col)), numberX, numberY);
            }
        }

        // Eğer varsa, diğer öğeleri çiz
        squares.drawHeaders(g, cellSize, leftSpace, upSpace);
    }


    public void drawforCopy(String shapes, Graphics g, Squares square,int cellSize,int left_space, int up_space ){
        switch (shapes){
            case "Harf":
                drawGridAndLetters(g, square, cellSize, left_space, up_space);
                break;
            case "Renk":
                drawGridAndCircles(g, square, cellSize, left_space, up_space);
                break;
            case "Rakam":
                drawGridAndNumbers(g, square, cellSize, left_space , up_space);
                break;
            default:
                drawGridAndLetters(g, square, cellSize, left_space , up_space);

                break;
        }
    }


}


