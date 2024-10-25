package org.example;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileReader;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        String filePath = "src/main/data.json";

        List<List<double[]>> allTestCasesPoints = PolynomialSolver.parseAndDecode(filePath);
        int num = 0;
        for (List<double[]> points : allTestCasesPoints) {
            double[] coefficients = GaussJordan.solvePolynomial(points);

            System.out.printf("%.4f%n", coefficients[0]);

//            System.out.println("Polynomial coefficients:");
//            for (int i = 0; i < coefficients.length; i++) {
//                System.out.printf("a%d = %.4f%n", i, coefficients[i]);
//            }
            System.out.println();
        }
    }

}


class GaussJordan {

    public static double[] solvePolynomial(List<double[]> points) {
        int degree = (int)(points.get(0)[2]);
        int n = degree + 1;
        double[][] A = new double[n][n];
        double[] B = new double[n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                A[i][j] = 0;
                for (double[] point : points) {
                    A[i][j] += Math.pow(point[0], i + j);
                }
            }
            B[i] = 0;
            for (double[] point : points) {
                B[i] += point[1] * Math.pow(point[0], i);
            }
        }

        return gaussJordanElimination(A, B);
    }

    private static double[] gaussJordanElimination(double[][] A, double[] B) {
        int n = B.length;
        double[][] augmentedMatrix = new double[n][n + 1];

        for (int i = 0; i < n; i++) {
            System.arraycopy(A[i], 0, augmentedMatrix[i], 0, n);
            augmentedMatrix[i][n] = B[i];
        }

        for (int i = 0; i < n; i++) {
            double diagElement = augmentedMatrix[i][i];
            for (int j = 0; j <= n; j++) {
                augmentedMatrix[i][j] /= diagElement;
            }

            for (int k = 0; k < n; k++) {
                if (k != i) {
                    double factor = augmentedMatrix[k][i];
                    for (int j = 0; j <= n; j++) {
                        augmentedMatrix[k][j] -= factor * augmentedMatrix[i][j];
                    }
                }
            }
        }

        double[] solution = new double[n];
        for (int i = 0; i < n; i++) {
            solution[i] = augmentedMatrix[i][n];
        }
        return solution;
    }
}


class PolynomialSolver {

    public static List<List<double[]>> parseAndDecode(String filePath) {
        List<List<double[]>> allPoints = new ArrayList<>();

        try (FileReader reader = new FileReader(filePath)) {
            JSONArray testCases = new JSONArray(new JSONTokener(reader));

            for (int i = 0; i < testCases.length(); i++) {
                JSONObject testCase = testCases.getJSONObject(i);
                JSONObject keys = testCase.getJSONObject("keys");
                int n = keys.getInt("n");
                int k = keys.getInt("k") - 1;
                List<double[]> points = new ArrayList<>();

                for (String key : testCase.keySet()) {
                    if (!key.equals("keys")) {
                        JSONObject pointData = testCase.getJSONObject(key);
                        int base = Integer.parseInt(pointData.getString("base"));
                        int x = Integer.parseInt(key);

                        BigInteger yValue = new BigInteger(pointData.getString("value"), base);
                        points.add(new double[]{x, yValue.doubleValue(), k});
                    }
                }
                allPoints.add(points);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return allPoints;
    }
}