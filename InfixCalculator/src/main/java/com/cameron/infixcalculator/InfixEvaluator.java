/**
 * @author Cameron Roe
 * @version 4/30/2013
 */
package com.cameron.infixcalculator;

import java.util.*;
 
public class InfixEvaluator {
	
	// Nested Exception class
	public static class SyntaxErrorException extends Exception {
		SyntaxErrorException(String message) {
			super(message);
		}
	}
	
	private Stack<Character> operatorStack;
	private Stack<Double> operandStack;
	private static final String OPERATORS = "+-*/()%^{}[]";
	private static final int[] PRECEDENCE = {1, 1, 2, 2, -1, -1, 2, 3, -1, -1, -1, -1};
	private static final String REGEX = "[\\p{L}\\p{N}\\.]+|[+-/\\*()\\^%{}\\[\\]]";
	/**
	 * error codes:
	 * 0 = no error
	 * 1 = not an infix
	 * 2 = stack should be empty
	 * 3 = unexpected character
	 * 4 = unmatched parenthesis
	 */
	public int error = 0;
	
	// Uses the combined algorithm to evaluate an infix expression
	public double eval(String expr) throws SyntaxErrorException {
		operandStack = new Stack<Double>();
		operatorStack = new Stack<Character>();
		if(isInfix(expr)) {
			try {
				String nextToken;
				Scanner scan = new Scanner(expr);
				boolean digitEncountered = false;
				while((nextToken = scan.findInLine(REGEX)) != null) {
					char firstChar = nextToken.charAt(0);
					if(Character.isDigit(firstChar)) {
						operandStack.push(Double.parseDouble(nextToken));
					} else if(isOperator(firstChar)) {
						processOperator(firstChar);
					} else {
						error = 3;
						return 0;
					}
				}
				while(!operatorStack.empty()) {
					char op = operatorStack.pop();
					if(op == '(' || op == '{' || op == '[') {
						error = 4;
						return 0;
					}
					evalOp(op);
                    if(error == 403) {
                        return 0;
                    }
				}
				double answer = operandStack.pop();
				if(operandStack.empty()) {
					error = 0;
					return answer;
				} else {
					error = 2;
					return 0;
				}
			} catch(Exception ex) {
				error = 404;
				return 0;
			}
		} else {
			error = 1;
			return 0;
		}
	}
	
	// Helper methods. Private methods not meant to be used outside of the class
	
	// Checks whether a character is an operator
	private boolean isOperator(char ch) {
		return OPERATORS.indexOf(ch) != -1;
	}
	
	private boolean isInfix(String expr) {
		String nextToken;
		boolean digitNext = true;
		boolean operatorNext = false;
		Scanner scan = new Scanner(expr);
		while((nextToken = scan.findInLine(REGEX)) != null) {
			char firstChar = nextToken.charAt(0);
			boolean bracket = false;
			if(isOperator(firstChar) && (precedence(firstChar) < 0))
				bracket = true;
			if(digitNext && isOperator(firstChar) && !bracket) {
				return false;
			}
			if(operatorNext && Character.isDigit(firstChar)) {
				return false;
			}
			if(digitNext && Character.isDigit(firstChar)) {
				digitNext = false;
				operatorNext = true;
			}
			if(operatorNext && isOperator(firstChar) && !bracket) {
				digitNext = true;
				operatorNext = false;
			}
		}
		return true;
	}
	
	// Evaluates the current operation
	// Pops two operands and applies the operator
	private void evalOp(char op) {
		double rhs = operandStack.pop();
		double lhs = operandStack.pop();
		double result = 0;
		switch(op) {
			case '+' : result = lhs + rhs;
						break;
			case '-' : result = lhs - rhs;
						break;
			case '/' : result = lhs / rhs;
                        if(rhs == 0) {
                            error = 403;
                        }
						break;
			case '*' : result = lhs * rhs;
						break;
			case '%' : result = lhs % rhs;
						break;
			case '^' : result = Math.pow(lhs, rhs);
						break;
		}
		operandStack.push(result);
	}
	
	// Get the precedence of an operator
	private int precedence(char op) {
		return PRECEDENCE[OPERATORS.indexOf(op)];
	}
	
	// Process operator
	private void processOperator(char op) {
		if(operatorStack.empty() || op == '(' || op == '{' || op == '[') {
			operatorStack.push(op);
		} else {
			char topOp = operatorStack.peek();
			if(precedence(op) > precedence(topOp)) {
				operatorStack.push(op);
			} else {
				while(!operatorStack.empty() && precedence(op) <= precedence(topOp)) {
						operatorStack.pop();
						if(topOp == '(' || topOp == '{' || topOp == '[') {
								break;
						}
						if(topOp != '(' && topOp != '{' && topOp != '[') {
							evalOp(topOp);
						}
						if(!operatorStack.empty()) {
							topOp = operatorStack.peek();
						}
				}
				if(op != ')' && op != '}' && op != ']') {
						operatorStack.push(op);
				}
			}
		}
	}
	
	public int checkError() {
		return error;
	}
	
	public static void main(String[] args) throws SyntaxErrorException {
		final String FLAG = "exit";
		
		String infixExpression = "";
		double answer = 0;
	
		Scanner in = new Scanner(System.in);
		InfixEvaluator evaluator = new InfixEvaluator();
		
		do {
			System.out.print("Input your infix expression (exit to quit): ");
			infixExpression = in.nextLine();
			if(!infixExpression.toUpperCase().equals(FLAG.toUpperCase())) {
				answer = evaluator.eval(infixExpression);
				switch(evaluator.error) {
					case 0: System.out.println(answer);
							break;
					case 1: System.out.println("That wasn't an infix expression bro, try again!");
							break;
					case 2: System.out.println("Something went wrong and the stack wasn't empty when it should have been!");
							break;
					case 3: System.out.println("You should only be used operands and operators, bro.");
							break;
					case 4: System.out.println("Finish your parenthesis please!");
							break;
				}
			}
		} while(!infixExpression.toUpperCase().equals(FLAG.toUpperCase()));
	}
}
