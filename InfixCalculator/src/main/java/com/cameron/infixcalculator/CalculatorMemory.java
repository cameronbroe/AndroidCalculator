package com.cameron.infixcalculator;

/**
 * Singleton class to act as the calculator's memory unit
 */
public class CalculatorMemory {
    private static CalculatorMemory instance = null;
    private double value;

    protected CalculatorMemory() {
        value = 0;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public static void clearMemory() {
        instance = null;
    }

    public static CalculatorMemory get() {
        if(instance == null) {
            instance = new CalculatorMemory();
        }
        return instance;
    }
}
