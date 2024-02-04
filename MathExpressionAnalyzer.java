import java.io.*;
import java.util.Stack;


class MathOperationException extends Exception {
    public MathOperationException(String message) {
        super(message);
    }
}

class Calculator {
    public static double calculate(String expression) throws MathOperationException {
        
        if (!expression.matches("[0-9+\\-*/()= ]+")) {
            throw new MathOperationException("Illegal characters");
        } expression = expression.replaceAll("\\s", "");

        int openParentheses = 0;
        for (char c : expression.toCharArray()) {
            if (c == '(') {
                openParentheses++;
            } else if (c == ')') {
                openParentheses--;
                if (openParentheses < 0) {
                    throw new MathOperationException("Syntax error: '(' expected");
                }
            }
        }
        if (openParentheses != 0) {
            throw new MathOperationException("Syntax error: ')' expected");
        }
        if (!expression.contains("=")) {
            throw new MathOperationException("Syntax error: '=' expected");
        }
        try {
            return evaluate(expression);
        } catch (ArithmeticException e) {
            throw new MathOperationException("Arithmetic error");
        } catch (NumberFormatException e) {
            throw new MathOperationException("Runtime error: Invalid number in expression: " + expression);
        }
    }

    private static double evaluate(String expression) {
        char[] tokens = expression.toCharArray();
        Stack<Double> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        for (int i = 0; i < tokens.length; i++) {
            if (tokens[i] >= '0' && tokens[i] <= '9') {
                StringBuilder sb = new StringBuilder();
                while (i < tokens.length && (tokens[i] >= '0' && tokens[i] <= '9' || tokens[i] == '.')) {
                    sb.append(tokens[i++]);
                }
                i--;
                values.push(Double.parseDouble(sb.toString()));
            } else if (tokens[i] == '(') {
                operators.push(tokens[i]);
            } else if (tokens[i] == ')') {
                while (operators.peek() != '(') {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.pop();
            } else if (tokens[i] == '+' || tokens[i] == '-' || tokens[i] == '*' || tokens[i] == '/') {
                while (!operators.empty() && hasPrecedence(tokens[i], operators.peek())) {
                    values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
                }
                operators.push(tokens[i]);
            }
        }

        while (!operators.empty()) {
            values.push(applyOperator(operators.pop(), values.pop(), values.pop()));
        } return values.pop();
    }

    private static boolean hasPrecedence(char op1, char op2) {
        if (op2 == '(' || op2 == ')') {
            return false;
        } return (op1 != '*' && op1 != '/') || (op2 != '+' && op2 != '-');
    }

    private static double applyOperator(char operator, double b, double a) {
        switch (operator) {
            case '+':
                return a + b;
            case '-':
                return a - b;
            case '*':
                return a * b;
            case '/':
                if (b == 0) {
                    throw new ArithmeticException("Division by zero");
                } return a / b;
        } return 0;
    }
}

public class MathExpressionAnalyzer {
    public static void main(String[] args) {
        String fileName = "input.txt";

        try (BufferedReader br = new BufferedReader(new FileReader(fileName));
             BufferedWriter bw = new BufferedWriter(new FileWriter("output.txt"))) {

            String line;
            while ((line = br.readLine()) != null) {
                try {
                    double result = Calculator.calculate(line);
                    System.out.println(line +" " + result);
                    bw.write(line + " " + result);
                    bw.newLine();
                } catch (MathOperationException e) {
                    System.err.println("Error processing line: " + line);
                    System.err.println("Error message: " + e.getMessage());
                    bw.write(line + "  //" + e.getMessage());
                    bw.newLine();
                }
            }
            System.out.println("Output.txt created");
        } catch (IOException e) {
            System.err.println("Error reading/writing file: " + e.getMessage());
        }
    }
}
