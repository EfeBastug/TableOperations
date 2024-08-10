import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Solution {
    private int size;
    private ArrayList<ArrayList<Integer>> answerBoard;
    private ArrayList<ArrayList<Integer>> questionBoard;

    private List<String> rowSwaps;
    private List<String> colShifts;
    private List<String> rotations;

    private List<List<String>> foundSolutions;  // List to store solutions
    private PrintWriter writer;

    public Solution(Squares question, Squares answer) {
        this.answerBoard = new ArrayList<>();
        this.questionBoard = new ArrayList<>();

        copyBoard(question.getBoard(), this.questionBoard);
        copyBoard(answer.getBoard(), this.answerBoard);
        this.size = question.getBoard().size(); // Assuming size is the dimension of the board

        this.rowSwaps = List.of("EF", "EG", "EH", "FG", "FH", "GH");
        this.colShifts = List.of("A", "B", "C", "D");
        this.rotations = List.of("90","180","270");// Combined list for all rotations

        this.foundSolutions = new ArrayList<>();  // Initialize list for storing solutions

        try {
            writer = new PrintWriter(new FileWriter("solutions.txt", false)); // Open file in overwrite mode
        } catch (IOException e) {
            System.err.println("Error initializing PrintWriter: " + e.getMessage());
        }
    }

    public int getSolutionWithBruteForce() {
        int solutions = 0;

        // Clear found solutions before each new run
        foundSolutions.clear();

        // Print initial states and row/col swaps
        printInit();

        // Generate all unique operation sequences with up to 4 operations
        for (int i = 1; i <= 3; i++) {
            solutions += generateOperationSequences(new ArrayList<>(), i);
        }

        printFoundSolutions();  // Print all found solutions
        writer.close();  // Close the PrintWriter
        return solutions;
    }

    private boolean isValidIndex(int index) {
        return index >= 0 && index < size;
    }

    private boolean boardsEqual(ArrayList<ArrayList<Integer>> board1, ArrayList<ArrayList<Integer>> board2) {
        return board1.equals(board2);
    }

    private void printInit() {
        writeToFile("Initial Board:");
        writeBoardToFile(this.questionBoard);
        writeToFile("Answer Board:");
        writeBoardToFile(this.answerBoard);
    }

    private void printFoundSolutions() {
        writeToFile("Found Solutions:");
        for (List<String> solution : foundSolutions) {
            writeToFile("Solution: " + solution);
        }
    }

    private void writeToFile(String text) {
        if (writer != null) {
            writer.println(text);
        }
    }

    private void writeBoardToFile(ArrayList<ArrayList<Integer>> board) {
        for (ArrayList<Integer> row : board) {
            writeToFile(row.toString());
        }
        writeToFile(""); // Add an empty line for better readability
    }

    private ArrayList<ArrayList<Integer>> deepCopy(ArrayList<ArrayList<Integer>> original) {
        ArrayList<ArrayList<Integer>> copy = new ArrayList<>();
        for (ArrayList<Integer> row : original) {
            copy.add(new ArrayList<>(row));
        }
        return copy;
    }

    private void swapRows(int row1, int row2, ArrayList<ArrayList<Integer>> board) {
        ArrayList<Integer> temp = board.get(row1);
        board.set(row1, board.get(row2));
        board.set(row2, temp);
    }

    private void swapColumns(int col1, int col2, ArrayList<ArrayList<Integer>> board) {
        for (int row = 0; row < size; row++) {
            ArrayList<Integer> rowData = board.get(row);
            Integer temp = rowData.get(col1);
            rowData.set(col1, rowData.get(col2));
            rowData.set(col2, temp);
        }
    }

    public void shiftColumn(int columnIndex, ArrayList<ArrayList<Integer>> board) {
        int temp = board.get(size-1).get(columnIndex);
        for(int i=size-1;i > 0; i--) {
            board.get(i).set(columnIndex, board.get(i-1).get(columnIndex));
        }
        board.get(0).set(columnIndex, temp);
    }

    private void rotate90Degrees(ArrayList<ArrayList<Integer>> board) {
        ArrayList<ArrayList<Integer>> temp = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            ArrayList<Integer> newRow = new ArrayList<>();
            for (int j = size - 1; j >= 0; j--) {
                newRow.add(board.get(j).get(i));
            }
            temp.add(newRow);
        }
        board.clear();
        board.addAll(temp);
    }

    private void rotate180Degrees(ArrayList<ArrayList<Integer>> board) {
        rotate90Degrees(board);
        rotate90Degrees(board);
    }

    private void rotate270Degrees(ArrayList<ArrayList<Integer>> board) {
        rotate180Degrees(board);
        rotate90Degrees(board);
    }

    private void copyBoard(ArrayList<ArrayList<Integer>> source, ArrayList<ArrayList<Integer>> destination) {
        destination.clear(); // If destination was used before, clear it
        for (ArrayList<Integer> row : source) {
            ArrayList<Integer> newRow = new ArrayList<>(row); // Copy the row
            destination.add(newRow); // Add the new row to destination
        }
    }

    private int generateOperationSequences(List<String> currentOps, int depth) {
        if (currentOps.size() == depth) {
            ArrayList<ArrayList<Integer>> tempBoard = deepCopy(this.questionBoard);
            writeToFile("Applying operations: " + currentOps);

            // Apply operations
            for (String operation : currentOps) {
                applyOperation(operation, tempBoard);
            }

            writeBoardToFile(tempBoard);
            if (boardsEqual(tempBoard, answerBoard)) {
                foundSolutions.add(new ArrayList<>(currentOps));  // Store the solution
                return 1;
            }
            return 0;
        }

        int solutions = 0;

        // Determine which operations have been used
        boolean hasRowSwap = currentOps.stream().anyMatch(rowSwaps::contains);
        boolean hasColShifts = currentOps.stream().anyMatch(colShifts::contains);
        boolean hasRotations = currentOps.stream().anyMatch(rotations::contains);

        List<String> allOps = new ArrayList<>(rowSwaps);
        allOps.addAll(colShifts);
        allOps.addAll(rotations);

        for (String operation : allOps) {
            // Check if the operation type is already used
            boolean isRowSwap = rowSwaps.contains(operation);
            boolean isColSwap = colShifts.contains(operation);
            boolean isRotations = rotations.contains(operation);

            if ((isRowSwap && hasRowSwap) || (isColSwap && hasColShifts) || (isRotations && hasRotations)) {
                continue; // Skip if the operation type is already used
            }

            // Create new lists for the next level of recursion
            List<String> newOps = new ArrayList<>(currentOps);
            newOps.add(operation);

            // Generate sequences with the updated set of operations
            solutions += generateOperationSequences(newOps, depth);
        }

        return solutions;
    }

    private void applyOperation(String operation, ArrayList<ArrayList<Integer>> board) {
        if (rotations.contains(operation)) {
            switch (operation) {
                case "90" -> rotate90Degrees(board);
                case "180" -> rotate180Degrees(board);
                case "270" -> rotate270Degrees(board);
            }
        }
        else if (rowSwaps.contains(operation)) {
            int firstRowIndex = operation.charAt(0) - 'E';
            int secondRowIndex = operation.charAt(1) - 'E';
            if (isValidIndex(firstRowIndex) && isValidIndex(secondRowIndex)) {
                swapRows(firstRowIndex, secondRowIndex, board);
            }
        } else if (colShifts.contains(operation)) {
            int firstColumnIndex = operation.charAt(0) - 'A';
            if (isValidIndex(firstColumnIndex)) {
                shiftColumn(firstColumnIndex, board);
            }
        }
    }
}