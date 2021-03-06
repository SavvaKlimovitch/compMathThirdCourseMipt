package com.klimovitch.compmath;

import java.util.function.DoubleUnaryOperator;

public class LinearSecondOrderODE extends SecondOrderODE {
    public LinearSecondOrderODE(DoubleUnaryOperator functionAtFirstOrderDerivative,
                                DoubleUnaryOperator functionAtZeroOrderDerivative,
                                DoubleUnaryOperator rightHandSideFunction) {
        super(functionAtFirstOrderDerivative, functionAtZeroOrderDerivative, rightHandSideFunction);
    }

    public LinearSecondOrderODE(double q, double p, double f) {
        this(operand -> q, operand -> p, operand -> f);
    }

    @Override
    public double[] calculateSolution(int numberOfPoints) {
        double h = computeStep(numberOfPoints);
        double[] gridSolution = new double[numberOfPoints];
        gridSolution[0] = u0;
        gridSolution[1] = uDerivative0 * h + u0;
        for (int i = 2; i != numberOfPoints; i++) {
            double x = x0 + i * h;
            double qX = functionAtFirstOrderDerivative.applyAsDouble(x);
            double pX = functionAtZeroOrderDerivative.applyAsDouble(x);
            double fX = rightHandSideFunction.applyAsDouble(x);
            gridSolution[i] = (fX + (2 / (h * h) - pX) * gridSolution[i - 1]
                    + (qX / (2 * h) - 1 / (h * h)) * gridSolution[i - 2])
                    / (1 / (h * h) + qX / (2 * h));
        }
        return gridSolution;
    }

    public void drawGeneralSolution(int numberOfPoints) {
        DoubleUnaryOperator f = this.rightHandSideFunction;
        this.rightHandSideFunction = operand -> 0;
        setLeftBoundaryConditions(1, 0);
        double[] firstGeneralSolution = calculateSolution(numberOfPoints);
        setLeftBoundaryConditions(0, 1);
        double[] secondGeneralSolution = calculateSolution(numberOfPoints);

        this.rightHandSideFunction = f;
        setLeftBoundaryConditions(0, 0);
        double[] particularSolution = calculateSolution(numberOfPoints);

        double[][] solutions = { firstGeneralSolution, secondGeneralSolution, particularSolution };
        String[] titles = { "firstGeneralSolution", "secondGeneralSolution", "particularSolution" };
        drawGraph(argumentDesignation, functionDesignation, solutions, titles);
    }
}
