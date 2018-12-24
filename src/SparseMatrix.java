import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SparseMatrix {

    private int[] values;
    private int[] cols;
    private int[] pointer;
    private int colNum;
    private int rowNum;
    private int avgValsInRow;

    public int[] getValues() {
        return values;
    }

    public int[] getCols() {
        return cols;
    }

    public int[] getPointer() {
        return pointer;
    }

    public int getColNum() {
        return colNum;
    }

    public int getAvgValsInRow() {
        return avgValsInRow;
    }

    public int getRowNum() {
        return rowNum;
    }

    SparseMatrix(int[] values, int[] cols, int[] pointer, int rowNum,  int colNum) {
        this.values = values;
        this.cols = cols;
        this.pointer = pointer;
        this.colNum = colNum;
        this.rowNum = rowNum;
        this.avgValsInRow = values.length / colNum;
    }

    SparseMatrix(List<List<Integer>> lColumnVectors, List<List<Integer>> lValueVectors,
                 int colNum, int rowNum, int valuesLength) {
        this.colNum = colNum;
        this.rowNum = rowNum;
        this.values = new int[valuesLength];
        this.cols = new int[valuesLength];
        this.pointer = new int[rowNum+1];
        this.avgValsInRow = valuesLength / colNum;
        pointer[0] = 0;

        int idxVal = 0;
        int idxCols = 0;
        for (int j = 0; j < rowNum; j++) {
            for (Integer value: lValueVectors.get(j)) {
                values[idxVal++] = value;
            }
            for (Integer col : lColumnVectors.get(j)) {
                cols[idxCols++] = col;
            }
            pointer[j+1] = pointer[j] + lValueVectors.get(j).size();
        }
    }

    SparseMatrix(int[][] matrix) {
        rowNum = matrix.length;
        colNum = matrix[0].length;
        List<Integer> alValues = new ArrayList<>();
        List<Integer> alCols = new ArrayList<>();
        pointer = new int[rowNum+1];
        pointer[0] = 0;
        for (int i = 0; i < rowNum; i++) {
            if (matrix[i].length != colNum) {
                throw new IllegalArgumentException("All rows must have the same length.");
            }
            for(int j = 0; j < colNum; j++) {
                if (matrix[i][j] != 0) {
                    alValues.add(matrix[i][j]);
                    alCols.add(j);
                }
            }
            pointer[i+1] = alValues.size();
        }
        values = alValues.stream().mapToInt(i->i).toArray();
        cols = alCols.stream().mapToInt(i->i).toArray();
        avgValsInRow = values.length / colNum;
    }

    public int[][] to2DArray() {
        int [][] a = new int[rowNum][colNum];
        Map<Integer, Integer> hmColsWithVals;
        for (int i = 0; i < rowNum; i++) {
            hmColsWithVals =
                SparseMatrixSupportImpl.getMapColsWithVals(this, i);
            for (int j = 0; j < colNum; j++) {
                a[i][j] = hmColsWithVals.getOrDefault(j, 0);
            }
        }

        return a;
    }

    public SparseMatrix transpose(){
        int trasposedColNum = rowNum;
        int trasposedRowNum = colNum;
        List<List<Integer>> lColumnVectors = new ArrayList<>(colNum);
        List<List<Integer>> lValueVectors = new ArrayList<>(colNum);

        for (int j = 0; j < colNum; j++) {
            lColumnVectors.add(new ArrayList<>());
            lValueVectors.add(new ArrayList<>());
        }

        for (int i = 0; i < rowNum; i++) {
            for (int idx = pointer[i]; idx < pointer[i+1];  idx++) {
                int colNum = cols[idx];
                lColumnVectors.get(colNum).add(i);
                lValueVectors.get(colNum).add(values[idx]);
            }
        }

        return new SparseMatrix(
            lColumnVectors, lValueVectors,
            trasposedColNum, trasposedRowNum , values.length);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        int [][] a = this.to2DArray();
        for (int i = 0; i < rowNum; i++) {
            for (int j = 0; j < colNum; j++) {
                sb.append(a[i][j]).append(" ");
            }
            sb.append("\n");
        }

        return sb.toString();
    }
}
