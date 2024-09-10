import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import javax.swing.*;
public class Squares extends JPanel  implements Cloneable {
    public int size;
    public int typeCount;
    public ArrayList<ArrayList<Integer>> board;
    private Map<Color, Integer> colorUsage;
    private Random random;
    private String answerString;
    private String colorCode;
    StringBuilder answerColorCode = new StringBuilder();

    private String letterCode = "";

    public Squares(int size, int typeCount) {
        //Since it is kind of like a matrix, the row and column lengths
        //will be the same as each other and size
        this.size = size;
        this.typeCount = typeCount;
        this.board = new ArrayList<>();
        this.colorUsage = new HashMap<>();
        this.random = new Random();
        initializeColorUsage();
        initializeBoard();
        generateColorCode();

    }

    public Squares(String colorCode) {
        this.size = (int) Math.sqrt(colorCode.replace(",", "").length());
        this.board = new ArrayList<>();
        this.colorUsage = new HashMap<>();
        this.random = new Random();
        this.colorCode = colorCode;

        initializeColorUsage();
        constructBoard();
    }

    private void initializeColorUsage() {
        colorUsage.put(new Color(207, 16, 32), 0);//Lava Red
        colorUsage.put(new Color(60, 179, 113), 0);//Medium Sea Green
        colorUsage.put(new Color(42, 82, 190), 0);//Cerulean Blue
        colorUsage.put(new Color(255, 223, 0), 0);//Golden Yellow
        colorUsage.put(new Color(192, 192, 192), 0);//Silver Gray
        colorUsage.put(new Color(255, 126, 0), 0);//Amber Orange
        colorUsage.put(new Color(191, 0, 255), 0);//Electric Purple
        colorUsage.put(new Color(255, 182, 193), 0);//Light Pink
        colorUsage.put(new Color(255, 240, 240), 0);//White
    }

    // initialize board düzenlenecek
    private void initializeBoard() {
        ArrayList<Color> colors = new ArrayList<>(colorUsage.keySet());
        ArrayList<Color> availableColors = new ArrayList<>();

        int maxCountPerColor;

        if (size == 3 || size == 4) {
            maxCountPerColor = 2; // 3x3 ve 4x4 tahtalar için maksimum renk kullanım sayısı 2
        } else if (size == 5) {
            maxCountPerColor = 3; // 5x5 tahtalar için maksimum renk kullanım sayısı 3
        } else {
            throw new IllegalArgumentException("Unsupported board size");
        }

        int requiredColors = size * size; // Gereken toplam renk sayısı

        // Renk kullanım sayısını sıfırla
        colorUsage.replaceAll((color, count) -> 0);

        // Renkleri availableColors listesine ekle
        for (Color color : colors) {
            for (int i = 0; i < maxCountPerColor; i++) {
                availableColors.add(color);
            }
        }

        // Eğer yeterli renk yoksa, rastgele renkler ekleyerek listeyi tamamla
        while (availableColors.size() < requiredColors) {
            availableColors.addAll(availableColors); // Mevcut renkleri iki katına çıkar
        }

        // availableColors listesinin boyutunu requiredColors ile sınırla
        Collections.shuffle(availableColors);
        availableColors = new ArrayList<>(availableColors.subList(0, requiredColors));

        // Tahtayı oluştur
        board.clear();
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                // availableColors listesinden renk seç
                Color color = availableColors.remove(0);
                int colorIndex = colors.indexOf(color);
                row.add(colorIndex);
            }
            board.add(row);
        }
    }

    public void constructBoard() {
        String generationCode = colorCode.replace(",", "");
        int count = 0;
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                int colorIndex = getColorFromChar(generationCode.charAt(count));
                row.add(colorIndex);
                count++;
            }
            board.add(row);
        }
    }

    private int getColorFromChar(char character) {
        //G - gri - 0
        //M - mor - 1
        //L - lacivert - 2
        //Y - yeşil - 3
        //P - pembe - 4
        //K - kırmızı - 5
        //S - sarı - 6
        //T - turuncu - 7
        //B - beyaz - 8
        if (character == 'g') return 0;
        else if (character == 'm') return 1;
        else if (character == 'l') return 2;
        else if (character == 'y') return 3;
        else if (character == 'p') return 4;
        else if (character == 'k') return 5;
        else if (character == 's') return 6;
        else if (character == 't') return 7;
        else if (character == 'b') return 8;
        else return -1;
    }


    public Color getColorByIndex(int index) {
        Color[] colors = colorUsage.keySet().toArray(new Color[0]);
        return colors[index];
    }


    public void rotate90Degrees() {
        transpose();
        reverseColumns();
    }


    //270 degrees = -90 degrees
    public void rotate270Degrees() {
        transpose();
        reverseRows();
    }

    public void swapRows(char firstRow, char secondRow) {
        // Satırların başlangıç harflerini belirlemek
        char startRow = 'D';
        switch (size) {
            case 3:
                startRow = 'D'; // 3x3 için satırlar D, E, F
                break;
            case 4:
                startRow = 'E'; // 4x4 için satırlar E, F, G, H
                break;
            case 5:
                startRow = 'F'; // 5x5 için satırlar F, G, H, I, J
                break;
        }

        // Satırların indekslerini hesaplama
        int firstRowIndex = firstRow - startRow;
        int secondRowIndex = secondRow - startRow;

        // Satırları değiştir
        ArrayList<Integer> temp = board.get(firstRowIndex);
        board.set(firstRowIndex, board.get(secondRowIndex));
        board.set(secondRowIndex, temp);
    }

    public void shiftRow(char row) {
        int rowIndex = row - 65 - size;
        int temp = board.get(rowIndex).get(size-1);
        for(int i=size-1;i > 0; i--) {
            board.get(rowIndex).set(i, board.get(rowIndex).get(i-1));
        }
        board.get(rowIndex).set(0, temp);
    }

    public void shiftColumn(char column) {
        int columnIndex = column - 65;
        int temp = board.get(size - 1).get(columnIndex);
        for (int i = size - 1; i > 0; i--) {
            board.get(i).set(columnIndex, board.get(i - 1).get(columnIndex));
        }
        board.get(0).set(columnIndex, temp);
    }

    public void transpose() {
        ArrayList<ArrayList<Integer>> transposedBoard = new ArrayList<>();

        for (int i = 0; i < size; i++) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (ArrayList<Integer> row : board) {
                newRow.add(row.get(i));
            }
            transposedBoard.add(newRow);
        }
        this.board = transposedBoard;
    }

    public void reverseRows() {
        for (int i = 0; i < size / 2; i++) {
            ArrayList<Integer> temp = board.get(i);
            board.set(i, board.get(size - 1 - i));
            board.set(size - 1 - i, temp);
        }
    }

    public void reverseColumns() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size / 2; j++) {
                int temp = board.get(i).get(j);
                board.get(i).set(j, board.get(i).get(size - 1 - j));
                board.get(i).set(size - 1 - j, temp);
            }
        }
    }


    public void askQuestion(int num) {
        StringBuilder answer = new StringBuilder();
        int numberOfOperations = num == 0 ? random.nextInt(1, 5) : random.nextInt(num, 5);
        HashSet<Integer> set = new HashSet<>();
        boolean rotated = false;
        for (int i = 0; i < numberOfOperations; i++) {
            int randomOperation = random.nextInt(1, 5);
            while (set.contains(randomOperation)) {
                randomOperation = random.nextInt(1, 5);

            }
            set.add(randomOperation);
            if (i == numberOfOperations - 1) {
                answer.append(doOperation(randomOperation));
                break;
            }
            answer.append(doOperation(randomOperation)).append(", ");
        }
        this.answerString = answer.toString();
    }

    public String doOperation(int questionType) {
        int startingChar = 65; // A
        int rowChar; // The char value that rows will start
        String answer = "";

        // Satırların başlangıç harflerini belirlemek
        switch (size) {
            case 3 -> rowChar = 'D'; // 3x3 için satırlar D, E, F
            case 4 -> rowChar = 'E'; // 4x4 için satırlar E, F, G, H
            case 5 -> rowChar = 'F'; // 5x5 için satırlar F, G, H, I, J
            default -> throw new IllegalArgumentException("Unsupported board size");
        }

        switch (questionType) {
            case 1 -> {
                // rowChar'dan başlayarak size kadar olan satır aralığından rastgele iki satır seçer
                char firstRow;
                char secondRow;
                do {
                    firstRow = (char) (random.nextInt(rowChar, rowChar + size));
                    secondRow = (char) (random.nextInt(rowChar, rowChar + size));
                } while (firstRow == secondRow);

                // Satırları swap eder
                if (firstRow > secondRow) {
                    char temp = firstRow;
                    firstRow = secondRow;
                    secondRow = temp;
                }
                this.swapRows(firstRow, secondRow);
                answer = "" + firstRow + secondRow;
            }
            case 2 -> {
                // A'dan başlayarak rowChar'dan önceki sütun aralığından rastgele bir sütun seçer
                char column = (char) (random.nextInt(startingChar, rowChar));
                shiftColumn(column);
                answer = "" + column;
            }
            case 3 -> {
                char row = (char) (random.nextInt(rowChar, rowChar + size));
                shiftRow(row);
                answer = "" + row;
            }
            case 4 -> {
                this.rotate90Degrees();
                answer = "90";
            }
            default -> System.out.println("Unknown question type.");
        }
        return answer;
    }


    public String getAnswer() {
        return answerString;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }


    public boolean equals(Object o) {
        if (o instanceof Squares s) {
            return s.board.equals(((Squares) o).board) && s.colorCode.equals(((Squares) o).colorCode);
        }
        return false;
    }

    public int hashCode() {
        return Integer.hashCode(size) * 31 + Integer.hashCode(typeCount);
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void drawCircles(Graphics g, int cellSize, int leftSpace, int upSpace) {
        int circleDiameter = (int) (cellSize / 1.2); // Adjusted circle diameter

        for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                int x = leftSpace + col * cellSize + (cellSize - circleDiameter) / 2;
                int y = upSpace + row * cellSize + (cellSize - circleDiameter) / 2;

                // Get color from the board
                int colorIndex = board.get(row).get(col); // Retrieve color index from board
                Color circleColor = getColorByIndex(colorIndex);

                g.setColor(circleColor);
                g.fillOval(x, y, circleDiameter, circleDiameter);

                // Draw border around circle
                g.setColor(Color.BLACK); // Set border color
                g.drawOval(x, y, circleDiameter, circleDiameter); // Draw the border
            }
        }
    }

    public void drawHeaders(Graphics g, int cellSize, int leftSpace, int upSpace) {
        // Set font for the headers
        g.setFont(g.getFont().deriveFont(24f));
        g.setColor(Color.BLACK);

        // Draw column headers
        char colHeader = 'A';
        for (int col = 0; col < size; col++) {
            int x = leftSpace + col * cellSize + cellSize / 2;
            int y = upSpace - 5; // Positioning above the grid
            g.drawString(String.valueOf(colHeader++), x, y);
        }

        // Draw row headers
        char rowHeader = colHeader;
        for (int row = 0; row < size; row++) {
            int x = leftSpace - 20; // Positioning to the left of the grid
            int y = upSpace + row * cellSize + cellSize / 2;
            g.drawString(String.valueOf(rowHeader++), x, y);
        }
    }


    public int getNumberAt(int row, int col) {
        // Validate the row and column indexes
        if (row < 0 || row >= size || col < 0 || col >= size) {
            throw new IndexOutOfBoundsException("Invalid row or column index.");
        }
        // Retrieve and return the value at the specified row and column
        return board.get(row).get(col);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = 60; // Size of each cell
        int leftSpace = 40; // Space on the left
        int upSpace = 40; // Space at the top
        drawHeaders(g, cellSize, leftSpace, upSpace);
        drawCircles(g, cellSize, leftSpace, upSpace);
    }

    private char getColorCharacter(Color color) {
        if (color.equals(new Color(207, 16, 32))) return 'k'; // Kırmızı
        else if (color.equals(new Color(60, 179, 113))) return 'y'; // Yeşil
        else if (color.equals(new Color(42, 82, 190))) return 'l'; // Lacivert
        else if (color.equals(new Color(255, 223, 0))) return 's'; // Sarı
        else if (color.equals(new Color(192, 192, 192))) return 'g'; // Gri
        else if (color.equals(new Color(255, 126, 0))) return 't'; // Turuncu
        else if (color.equals(new Color(191, 0, 255))) return 'm'; // Mor
        else if (color.equals(new Color(255, 182, 193))) return 'p'; // Pembe
        else if (color.equals(new Color(255, 240, 240))) return 'b'; // beyaz
        return ' ';
    }

    public String getColorCode() {
        return this.colorCode;
    }

    public void generateColorCode() {
        answerColorCode.setLength(0);
        for (ArrayList<Integer> row : board) {
            for (int index : row) {
                Color color = getColorByIndex(index);
                answerColorCode.append(getColorCharacter(color));
            }
            answerColorCode.append(",");
        }
        answerColorCode.replace(answerColorCode.length() - 1, answerColorCode.length(), "");
        this.colorCode = answerColorCode.toString();
    }


    // color code ları string olarak harflere dönüştürmektedir.
    public String switchColorCodeToLetterQuestion() {
        StringBuilder letterCode = new StringBuilder(); // Use StringBuilder for better performance
        for (int i = 0; i < colorCode.length(); i++) {
            char code = colorCode.charAt(i);
            if (code == 'k') letterCode.append("f"); // Kırmızı
            else if (code == 'y') letterCode.append("d"); // Yeşil
            else if (code == 'l') letterCode.append("c"); // Lacivert
            else if (code == 's') letterCode.append("g"); // Sarı
            else if (code == 'g') letterCode.append("a"); // Gri
            else if (code == 't') letterCode.append("h"); // Turuncu
            else if (code == 'm') letterCode.append("b"); // Mor
            else if (code == 'p') letterCode.append("e"); // Pembe
            else if (code == 'b') letterCode.append("ı"); // Beyaz
        }

        // Convert StringBuilder to String
        String result = letterCode.toString();

        // Format the string to insert commas after every 4 characters
        StringBuilder formattedResult = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            formattedResult.append(result.charAt(i));
            // Insert comma after every 4 characters
            if ((i + 1) % size == 0 && i != result.length() - 1) {
                formattedResult.append(",");
            }
        }

        return formattedResult.toString();
    }

    public String switchColorCodeToLetterAnswer() {
        StringBuilder letterCodeAnswer = new StringBuilder(); // Use StringBuilder for better performance

        for (int i = 0; i < answerColorCode.length(); i++) {
            char code = answerColorCode.charAt(i);
            if (code == 'k') letterCodeAnswer.append("f"); // Kırmızı
            else if (code == 'y') letterCodeAnswer.append("d"); // Yeşil
            else if (code == 'l') letterCodeAnswer.append("c"); // Lacivert
            else if (code == 's') letterCodeAnswer.append("g"); // Sarı
            else if (code == 'g') letterCodeAnswer.append("a"); // Gri
            else if (code == 't') letterCodeAnswer.append("h"); // Turuncu
            else if (code == 'm') letterCodeAnswer.append("b"); // Mor
            else if (code == 'p') letterCodeAnswer.append("e"); // Pembe
            else if (code == 'b') letterCodeAnswer.append("ı"); // Beyaz
        }

        // Convert StringBuilder to String
        String result = letterCodeAnswer.toString();

        // Format the string to insert commas after every 4 characters
        StringBuilder formattedResult = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            formattedResult.append(result.charAt(i));

            if ((i + 1) % size == 0 && i != result.length() - 1) {
                formattedResult.append(",");
            }
        }
        return formattedResult.toString();
    }

    // color code ve answer codu rakamlara dönüştürmektedir.
    public String switchColorCodeToNumberQuestion() {
        StringBuilder letterCode = new StringBuilder(); // Use StringBuilder for better performance

        for (int i = 0; i < colorCode.length(); i++) {
            char code = colorCode.charAt(i);
            if (code == 'k') letterCode.append("5"); // Kırmızı
            else if (code == 'y') letterCode.append("3"); // Yeşil
            else if (code == 'l') letterCode.append("2"); // Lacivert
            else if (code == 's') letterCode.append("6"); // Sarı
            else if (code == 'g') letterCode.append("0"); // Gri
            else if (code == 't') letterCode.append("7"); // Turuncu
            else if (code == 'm') letterCode.append("1"); // Mor
            else if (code == 'p') letterCode.append("4"); // Pembe
            else if (code == 'b') letterCode.append("8"); // Beyaz
        }

        // Convert StringBuilder to String
        String result = letterCode.toString();

        // Format the string to insert commas after every 4 characters
        StringBuilder formattedResult = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            formattedResult.append(result.charAt(i));
            // Insert comma after every 4 characters
            if ((i + 1) % size == 0 && i != result.length() - 1) {
                formattedResult.append(",");
            }
        }

        return formattedResult.toString();
    }

    public String switchColorCodeToNumberAnswer() {
        StringBuilder letterCodeAnswer = new StringBuilder(); // Use StringBuilder for better performance

        for (int i = 0; i < answerColorCode.length(); i++) {
            char code = answerColorCode.charAt(i);
            if (code == 'k') letterCodeAnswer.append("5"); // Kırmızı
            else if (code == 'y') letterCodeAnswer.append("3"); // Yeşil
            else if (code == 'l') letterCodeAnswer.append("2"); // Lacivert
            else if (code == 's') letterCodeAnswer.append("6"); // Sarı
            else if (code == 'g') letterCodeAnswer.append("0"); // Gri
            else if (code == 't') letterCodeAnswer.append("7"); // Turuncu
            else if (code == 'm') letterCodeAnswer.append("1"); // Mor
            else if (code == 'p') letterCodeAnswer.append("4"); // Pembe
            else if (code == 'b') letterCodeAnswer.append("8"); // Beyaz
        }

        // Convert StringBuilder to String
        String result = letterCodeAnswer.toString();

        // Format the string to insert commas after every 4 characters
        StringBuilder formattedResult = new StringBuilder();
        for (int i = 0; i < result.length(); i++) {
            formattedResult.append(result.charAt(i));

            if ((i + 1) % size == 0 && i != result.length() - 1) {
                formattedResult.append(",");
            }
        }
        return formattedResult.toString();
    }
}

