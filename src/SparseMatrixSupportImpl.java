import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class SparseMatrixSupportImpl implements SparseMatrixSupport<SparseMatrix>{

    @Override
    public Stream<Integer> toStream(SparseMatrix matrix) {
        return null;
    }

    @Override
    public SparseMatrix fromStream(Stream<Integer> stream) {
        return null;
    }

    @Override
    public SparseMatrix multiply(SparseMatrix first, SparseMatrix second) {
        if (first.getColNum() != second.getRowNum()) {
            throw new IllegalArgumentException("Matrix inner dimensions must agree.");
        }
        SparseMatrix secondT = second.transpose();
        List<Integer> alValues = new ArrayList<>(first.getColNum());
        List<Integer> alCols = new ArrayList<>(first.getColNum());
        int [] pointerRes = new int[first.getRowNum() + 1];
        Map<Integer, Integer> hmColsWithVals;
        for (int i = 0; i < first.getRowNum(); i++) {
            hmColsWithVals = getMapColsWithVals(first,  i);
            calcAndFillMatrixRow(secondT, hmColsWithVals, alValues, alCols);
            pointerRes[i+1] = alValues.size();
        }

        return new SparseMatrix(
            alValues.stream().mapToInt(i->i).toArray(),
            alCols.stream().mapToInt(i->i).toArray(),
            pointerRes, first.getRowNum(), second.getColNum());
    }

    private void calcAndFillMatrixRow(SparseMatrix matrix,
                                      Map<Integer, Integer> hmColsWithVals,
                                      List<Integer> alValues, List<Integer> alCols) {
        int [] pointer = matrix.getPointer();
        int [] values = matrix.getValues();
        for(int j = 0; j < matrix.getRowNum(); j ++) {
            int sum = 0;
            for (int idx = pointer[j];  idx < pointer[j + 1]; idx++) {
                int colNum = matrix.getCols()[idx];
                if (hmColsWithVals.containsKey(colNum)) {
                    sum += values[idx] * hmColsWithVals.get(colNum);
                }
            }
            if (sum != 0) {
                alValues.add(sum);
                alCols.add(j);
            }
        }
    }

    /** Calculate Map in format column number to value in given row
     *  for a row of matrix
     @param matrix  Sparce matrix.
     @param  idxRow   Index of row.
     @return Map<Integer, Integer> with keys column num and values matrix values of this column
     */
    public static Map<Integer, Integer> getMapColsWithVals(
        SparseMatrix matrix, int idxRow) {

        Map<Integer, Integer> hmColsWithVals =  new HashMap<>();
        int [] pointer = matrix.getPointer();

        for (int colIdx = pointer[idxRow]; colIdx < pointer[idxRow + 1]; colIdx++) {
            int colNum = matrix.getCols()[colIdx];
            hmColsWithVals.put(colNum, matrix.getValues()[colIdx]);
        }

        return hmColsWithVals;
    }

    /** Generate sparse array with random elements
     @param m    Number of rows.
     @param n    Number of colums.
     @param nullValues   ratio of null elements 0 to 1.
     @return     int [m][n] with uniformly distributed random elements.
     */
    public static int[][] randomSparseArr(int m, int n, double nullValues) {
        int[][] X = new int[m][n];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                X[i][j] = randomSparseInt(nullValues);
            }
        }

        return X;
    }

    /** Generate SparceMatrix with random elements
     @param rowNum    Number of rows.
     @param colNum    Number of colums.
     @param nullValues   ratio of null elements 0 to 1.
     @return     SparceMatrix with uniformly distributed random elements.
     */
    public SparseMatrix getRandomSparceMatrix(
        int rowNum, int colNum, double nullValues) {

        long startTime = System.currentTimeMillis();
        List<Integer> alValues = new ArrayList<>();
        List<Integer> alCols = new ArrayList<>();
        int [] pointer = new int[rowNum+1];
        pointer[0] = 0;
        for (int i = 0; i < rowNum; i++) {
            for(int j = 0; j < colNum; j++) {
                int val = randomSparseInt(nullValues);
                if (val != 0) {
                    alValues.add(val);
                    alCols.add(j);
                }
            }
            pointer[i+1] = alValues.size();
            if (i%1000==0) {
                System.out.println("Milis initing = " + (System.currentTimeMillis() - startTime));
            }
        }

        System.out.println("Milis init = " + (System.currentTimeMillis() - startTime));

        return new SparseMatrix(
            alValues.stream().mapToInt(i->i).toArray(),
            alCols.stream().mapToInt(i->i).toArray(),
            pointer, rowNum, colNum);
    }

    /** Generate random int with ratio that it could be zero
     @param nullValues   ratio of null elements 0 to 1.
     @return     Random int.
     */
    private static int randomSparseInt(double nullValues) {
        double a = Math.random();
        if (a >= nullValues) {
            return   (int)(Math.random() * 11);
        } else {
            return 0;
        }
    }
}
