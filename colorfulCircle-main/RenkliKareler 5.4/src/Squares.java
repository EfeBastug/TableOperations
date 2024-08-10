import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;
import javax.swing.*;
public class Squares extends JPanel {
    public int size;
    public int typeCount;
    public ArrayList<ArrayList<Integer>> board;
    private Map<Color, Integer> colorUsage;
    private Random random;
    private String answerString;
    private String keyCode;
    StringBuilder answerColorCode = new StringBuilder();


    public Squares(int size, int typeCount) {
        //Since it is kind of like a matrix, the row and column lengths
        //will be the same as each other and size
        this.size = size;
        this.typeCount = typeCount;
        this.board = new ArrayList<>();
        this.colorUsage = new HashMap<>();
        this.random = new Random();
        // Initialize the colorUsage map with all colors set to 0
        initializeColorUsage();
        initializeBoard();
    }


    private void initializeColorUsage() {
        colorUsage.put(new Color(207,16,32), 0);//Lava Red
        colorUsage.put(new Color(60,179,113), 0);//Medium Sea Green
        colorUsage.put(new Color(42,82,190), 0);//Cerulean Blue
        colorUsage.put(new Color(255,223,0), 0);//Golden Yellow
        colorUsage.put(new Color(192,192,192), 0);//Silver Gray
        colorUsage.put(new Color(181,101,29), 0);//Otter Brown
        colorUsage.put(new Color(191,0,255), 0);//Electric Purple
        colorUsage.put(new Color(255,182,193), 0);//Light Pink
    }



    public String getAnswer() {
        System.out.println(answerString);
        return answerString;
    }

    private void initializeBoard() {
        ArrayList<Color> colors = new ArrayList<>(colorUsage.keySet());
        ArrayList<Color> availableColors = new ArrayList<>();
        int maxCountPerColor = 2; // Max times a color can appear

        // Initialize color usage count
        colorUsage.replaceAll((color, count) -> 0);

        // Create a list of colors with the max count
        for (Color color : colors) {
            for (int i = 0; i < maxCountPerColor; i++) {
                availableColors.add(color);
            }
        }

        // Shuffle the colors to ensure random placement
        Collections.shuffle(availableColors);

        // Initialize the board
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> row = new ArrayList<>();
            for (int j = 0; j < size; j++) {
                // Assign colors from availableColors list
                Color color = availableColors.remove(0);
                int colorIndex = colors.indexOf(color);
                row.add(colorIndex);
            }
            board.add(row);
        }
    }

    public void regenerateBoard() {
        // Clear the board and reinitialize
        board.clear();
        initializeBoard();
    }

    public Color getColorByIndex(int index) {
        // Ensure index is within bounds
        Color[] colors = colorUsage.keySet().toArray(new Color[0]);
        return colors[index];
    }

    public String getKeycode() {
        StringBuilder keycode = new StringBuilder();
        int row = board.size();
        int col = board.get(0).size();
        for(int i=0;i<row;i++) {
            for(int j=0;j<col;j++) {
                keycode.append(board.get(i).get(j));
            }
        }
        return keycode.toString();
    }

    public void generate(String keycode) {
        int row = board.size();
        int col = board.get(0).size();
        for(int i=0;i<row;i++) {
            for(int j=0;j<col;j++) {
                board.get(i).set(j, keycode.charAt((i+1)* j) - '0');
            }
        }
    }

    public void rotate90Degrees() {
        transpose();
        reverseColumns();
    }

    public void rotate180Degrees() {
        rotate90Degrees();
        rotate90Degrees();
    }

    //270 degrees = -90 degrees
    public void rotate270Degrees() {
        transpose();
        reverseRows();
    }

    public void shiftColumn(char column) {
        int columnIndex = column - 65;
        int temp = board.get(size-1).get(columnIndex);
        for(int i=size-1;i > 0; i--) {
            board.get(i).set(columnIndex, board.get(i-1).get(columnIndex));
        }
        board.get(0).set(columnIndex, temp);
    }

    public void swapRows(char firstRow, char secondRow) {
        int firstRowIndex = firstRow - 65 - size;
        int secondRowIndex = secondRow - 65 - size;
        ArrayList<Integer> temp = board.get(firstRowIndex);
        board.set(firstRowIndex, board.get(secondRowIndex));
        board.set(secondRowIndex, temp);
    }

    public void swapColumns(char firstColumn, char secondColumn) {
        int firstColumnIndex = firstColumn - 65;
        int secondColumnIndex = secondColumn - 65;
        for (int i = 0; i < size; i++) {
            int temp = board.get(i).get(firstColumnIndex);
            board.get(i).set(firstColumnIndex, board.get(i).get(secondColumnIndex));
            board.get(i).set(secondColumnIndex, temp);
        }
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
        int numberOfOperations = num == 0 ? random.nextInt(1,4) : random.nextInt(num, 4);
        HashSet<Integer> set = new HashSet<>();
        boolean rotated = false;
        for(int i=0;i<numberOfOperations;i++) {
            int randomOperation = rotated ? random.nextInt(1,3) : random.nextInt(1, 6);
            while (set.contains(randomOperation)) {
                    randomOperation = rotated ? random.nextInt(1,3) : random.nextInt(1, 6);

            }
            if (randomOperation > 2) {
                rotated = true;
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

    /*public void askQuestion() {
        StringBuilder answer = new StringBuilder();
        // Define the fixed operations
        int[] operations = {2,4,1}; // List of operations you want to use
        int numberOfOperations = operations.length;

        for (int i = 0; i < numberOfOperations; i++) {
            answer.append(doOperation(operations[i]));
            if (i < numberOfOperations - 1) {
                answer.append(", ");
            }
        }

        this.answerString = answer.toString();
    }*/


    public String doOperation(int questionType) {
        int startingChar = 65; //A
        int rowChar = startingChar+size; //The char value that rows will start
        String answer= "";
        switch (questionType) {
            case 1 -> {
                //Goes from A + size to A + 2 * size
                char firstRow;
                char secondRow;
                do {
                    firstRow = (char) (random.nextInt(rowChar, rowChar+size));
                    secondRow = (char) (random.nextInt(rowChar, rowChar+size));
                } while (firstRow == secondRow);
                if (firstRow > secondRow) {
                    char temp = firstRow;
                    firstRow = secondRow;
                    secondRow = temp;
                }
                this.swapRows(firstRow, secondRow);
                answer = "" + firstRow + secondRow;
            }
            case 2 -> {
                char column = (char) (random.nextInt(startingChar, rowChar));
                shiftColumn(column);
                answer = "" + column;
            }
            case 3 -> {
                this.rotate90Degrees();
                answer = "90";
            }
            case 4 -> {
                this.rotate180Degrees();
                answer = "180";
            }
            case 5 -> {
                this.rotate270Degrees();
                answer = "270";
            }
            default -> System.out.println("Unknown question type.");
        }
        return answer;
    }


    /*public String doOperation(int questionType) {
        int startingChar = 65; //A
        int rowChar = startingChar+size; //The char value that rows will start
        String answer= "";
        switch (questionType) {
            case 1 -> {

                this.swapRows('E', 'G');
                answer = "" + "E" + "G";
            }
            case 2 -> {
                //Goes from A to A + size

                this.swapColumns('B', 'C');
                answer = "B" + "C";
            }
            case 3 -> {
                this.rotate90Degrees();
                answer = "90";
            }
            case 4 -> {
                this.rotate180Degrees();
                answer = "180";
            }
            default -> System.out.println("Unknown question type.");
        }
        return answer;
    }*/

    public String doOperation(int questionType, char first, char second) {
        String answer= "";
        switch (questionType) {
            case 1 -> {
                //Goes from A + size to A + 2 * size
                this.swapRows(first, second);
                answer = "" + first + second;
            }
            case 2 -> {
                //Goes from A to A + size
                this.swapColumns(first, second);
                answer = "" + first + second;
            }
            default -> System.out.println("Unknown question type.");
        }
        return answer;
    }

    public ArrayList<ArrayList<Integer>> getBoard() {
        return board;
    }

    public Color getColor() {
        // Obtain a color from colorUsage
        Color[] availableColors = getAvailableColors();

        if (availableColors.length == 0) {
            throw new IllegalStateException("No available colors.");
        }

        // Select a random color from the available colors
        Color selectedColor = availableColors[random.nextInt(availableColors.length)];

        // Update the usage count
        colorUsage.put(selectedColor, colorUsage.get(selectedColor) + 1);

        return selectedColor;
    }

    private Color[] getAvailableColors() {
        return colorUsage.entrySet().stream()
                .filter(entry -> entry.getValue() < 2)
                .map(Map.Entry::getKey)
                .toArray(Color[]::new);
    }





    private char getColorCharacter(Color color) {
        if (color.equals(new Color(207, 16, 32))) return 'r'; // Lava Red
        else if (color.equals(new Color(60, 179, 113))) return 'g'; // Medium Sea Green
        else if (color.equals(new Color(42, 82, 190))) return 'b'; // Cerulean Blue
        else if (color.equals(new Color(255, 223, 0))) return 'y'; // Golden Yellow
        else if (color.equals(new Color(192, 192, 192))) return 's'; // Silver Gray
        else if (color.equals(new Color(181, 101, 29))) return 'o'; // Otter Brown
        else if (color.equals(new Color(191, 0, 255))) return 'p'; // Electric Purple
        else if (color.equals(new Color(255, 182, 193))) return 'l'; // Light Pink
        return ' ';
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

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int cellSize = 60; // Size of each cell
        int leftSpace = 40; // Space on the left
        int upSpace = 40; // Space at the top
        drawHeaders(g, cellSize, leftSpace, upSpace);
        drawCircles(g, cellSize, leftSpace, upSpace);
    }



    private void copyBoard(ArrayList<ArrayList<Integer>> source, ArrayList<ArrayList<Integer>> destination) {
        destination.clear(); // Eğer destination daha önce kullanıldıysa temizle
        for (ArrayList<Integer> row : source) {
            ArrayList<Integer> newRow = new ArrayList<>(row); // Satırı kopyala
            destination.add(newRow); // Yeni satırı destination'a ekle
        }
    }

    public BufferedImage getImage() {
        int cellSize = 50; // Size of each cell in pixels
        int width = 4 * cellSize;
        int height = 4 * cellSize;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        // Draw background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Draw grid and cells
        g2d.setColor(Color.BLACK);
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                g2d.drawRect(j * cellSize, i * cellSize, cellSize, cellSize);
                // Optionally draw the cell value here
                String value = String.valueOf(board.get(i).get(j));
                g2d.drawString(value, j * cellSize + cellSize / 2, i * cellSize + cellSize / 2);
            }
        }

        g2d.dispose();
        return image;
    }
}


