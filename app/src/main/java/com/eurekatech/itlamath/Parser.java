package com.eurekatech.itlamath;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import java.util.Stack;
import java.util.StringTokenizer;

import static android.widget.Toast.*;

public class Parser

{
    Context contexto;
    private static final int EOL = 0;
    private static final int VALUE = 1;
    private static final int OPAREN = 2;
    private static final int CPAREN = 3;
    private static final int EXP = 4;
    private static final int MULT = 5;
    private static final int DIV = 6;
    private static final int PLUS = 7;
    private static final int MINUS = 8;
    private static final int FUNCT = 9;
    private static String[] function =
            {
                    "sqrt", "sin",
                    "cos", "tan",
                    "asin", "acos",
                    "atan", "log",
                    "floor", "eXp","ln","cot","sec","csc"
            };
    // PrecTable matches order of Token enumeration
    private static Precedence[] precTable = new
            Precedence[]
            {
                    new Precedence(0, -1), // EOL
                    new Precedence(0, 0), // VALUE
                    new Precedence(100, 0), // OPAREN
                    new Precedence(0, 99), // CPAREN
                    new Precedence(6, 5), // EXP
                    new Precedence(3, 4), // MULT
                    new Precedence(3, 4), // DIV
                    new Precedence(1, 2), // PLUS
                    new Precedence(1, 2), // MINUS
                    new Precedence(7, 6) // FUNCT
            };
    private String string;
    private Stack<Integer> opStack; // Operator stack for     conversion
    private Stack<Double> postfixStack; // Stack for postfix     machine

    // . . . continua codigo de los metodos de la clase .            ..
    private StringTokenizer str; // StringTokenizer     stream

    /**
     * Construct an evaluator object.
     *
     * @param s the string containing the expression.
     */
    Parser(String s, Context context) {

        if(s.contains("e")){

           s=   s.replace("e",Double.toString( Math.E));}

        if(s.contains("π")){

            s=   s.replace("π",Double.toString( Math.PI));}


        contexto=context;
        opStack = new Stack<>();
        postfixStack = new Stack<>();
        string = unary2Binary(s);
        str = new StringTokenizer(string, "+*-/^()xyz ", true);
        opStack.push(EOL);

    }


    /**
     * Internal routine to compute x^n.
     */
    private  double pow(double x, double n) {
        if (x == 0) {
            if (n == 0)
                mensaje("0^0 no está definido");
              //  System.err.println("0^0 is undefined");
            return 0;
        }
        if (n < 0) {
            mensaje("Negative exponent");
           // System.err.println("Negative exponent");
            return 0;
        }
        if (n == 0)
            return 1;
        if (n % 2 == 0)
            return pow(x * x, n / 2);
        else
            return x * pow(x, n - 1);
    }

    double getValue(double x) {
        return getValue(x, 0, 0);
    }

    public double getValue(double x, double y) {
        return getValue(x, y, 0);
    }

    public double getValue(double x, double y, double z) {
        // for each call
        opStack = new Stack<>();
        postfixStack = new Stack<>();
        str = new StringTokenizer(string, "+*-/^()xyz ", true);
        opStack.push(EOL);
        EvalTokenizer tok = new EvalTokenizer(str, x, y, z);
        Token lastToken;
        do {
            lastToken = tok.getToken();
            processToken(lastToken);
        } while (lastToken.getType() != EOL);
        if (postfixStack.isEmpty()) {
            mensaje("Falta operando!");
         //   System.err.println("Missing operand!");
            return 0;
        }
        double theResult = postFixTopAndPop();
        if (!postfixStack.isEmpty())
            mensaje("Aviso: faltan operadores!");
         //   System.err.println("Warning: missing operators!" );
        return theResult;
    }
// The only publicy visible routine
    /*
      Public routine that performs the evaluation.
      Examine the postfix machine to see if a single result is
      left and if so, return it; otherwise print error.
      @return the result.
     */

    /**
     * Process an operator by taking two items off the
     * postfix stack, applying the operator, and pushing the
     * result. Print error if missing closing parenthesis or
     * division by 0.
     */
    private void binaryOp(int topOp) {
        if (topOp == OPAREN) {
            mensaje("Parentesis no balanceados");
           // System.err.println("Unbalanced parentheses" );
            opStack.pop();
            return;
        }
        if (topOp >= FUNCT) {
            double d = getTop();
            postfixStack.push(functionEval(topOp, d));
            opStack.pop();
            return;
        }
        double rhs = getTop();
        double lhs = getTop();
        if (topOp == EXP)
            postfixStack.push(pow(lhs, rhs));
        else if (topOp == PLUS)
            postfixStack.push(lhs + rhs);
        else if (topOp == MINUS)
            postfixStack.push(lhs - rhs);
        else if (topOp == MULT)
            postfixStack.push(lhs * rhs);
        else if (topOp == DIV)
            if (rhs != 0)
                postfixStack.push(lhs / rhs);
            else {
                mensaje("Division por cero");
               // System.err.println("Division by zero");
                postfixStack.push(lhs);
            }
        opStack.pop();
    }

    private double functionEval(int topOp, double d) {
        double y = 0;
        switch (topOp) {
            case 9:
                y = Math.sqrt(d);
                break;
            case 10:
                y = Math.sin(d);
                break;
            case 11:
                y = Math.cos(d);
                break;
            case 12:
                y = Math.tan(d);
                break;
            case 13:
                y = Math.asin(d);
                break;
            case 14:
                y = Math.acos(d);
                break;
            case 15:
                y = Math.atan(d);
                break;
            case 16:
                y = Math.log10(d);
                break;
            case 17:
                y = Math.floor(d);
                break;
            case 18:
                y = Math.exp(d);
                break;
            case 19:
                y=Math.log(d);
                break;
            case 20:
                y=1/Math.tan(d);
                break;
            case 21:
                y=1/Math.cos(d);
                break;
            case 22:
                y=1/Math.sin(d);



        }
        return y;
    }

    /**
     * Internal method that unary to binary.
     */
    private String unary2Binary(String s) {
        int i;
        s = s.trim();
        if (s.charAt(0) == '-')
            s = "0.0" + s;
        while ((i = s.indexOf("(-")) >= 0)
            s = s.substring(0, i + 1) + "0.0" +
                    s.substring(i + 1);
        return s;
    }

    /**
     * Internal method that hides type-casting.
     */
    private double postFixTopAndPop() {
        return postfixStack.pop();
    }

    /**
     * Another internal method that hides type-casting.
     */
    private int opStackTop() {
        return opStack.peek();
    }

    /**
     * After a token is read, use operator precedence parsing
     * algorithm to process it; missing opening parentheses
     * are detected here.
     */
    private void processToken(Token lastToken) {
        int topOp;
        int lastType = lastToken.getType();
        switch (lastType) {
            case VALUE:
                postfixStack.push(lastToken.getValue());
                return;
            case CPAREN:
                while ((topOp = opStackTop()) != OPAREN && topOp
                        != EOL)
                    binaryOp(topOp);
                if (topOp == OPAREN)
                    opStack.pop(); // Get rid of opening parentheseis
                else
                    mensaje("Falta parentesis de apertura");
                   // System.err.println("Missing open parenthesis");
                break;
            default: // General operator case
                int last = (lastType >= FUNCT ? FUNCT : lastType);
                while (precTable[last].inputSymbol <=
                        precTable[opStackTop() >= FUNCT ? FUNCT : opStackTop()].topOfStack)
                    binaryOp(opStackTop());
                if (lastType != EOL)
                    opStack.push(lastType);
                break;
        }
    }

    /*
     * topAndPop the postfix machine stack; return the result.
     * If the stack is empty, print an error message.
     */
    private double getTop() {
        if (postfixStack.isEmpty()) {
            mensaje("Falta operando");
          //  System.err.println("Missing operand");
            return 0;
        }
        return postFixTopAndPop();
    }

    private static class Precedence {
        int inputSymbol;
        int topOfStack;

        Precedence(int inSymbol, int topSymbol
        ) {
            inputSymbol = inSymbol;
            topOfStack = topSymbol;
        }
    }

    private static class Token {
        private int type;
        private double value;

        Token() {
            this(EOL);
        }

        Token(int t) {
            this(t, 0);
        }

        Token(int t, double v) {
            type = t;
            value = v;
        }

        int getType() {
            return type;
        }

        double getValue() {
            return value;
        }
    }


    private    class EvalTokenizer {
        private StringTokenizer str;
        private double equis;
        private double ye;
        private double zeta;
        EvalTokenizer(StringTokenizer is, double x,
                      double y, double z) {
            str = is;
            equis = x;
            ye = y;
            zeta = z;
        }

        /**
         * Find the next token, skipping blanks, and return it.
         * For VALUE token, place the processed value in
         * currentValue.
         * Print error message if input is unrecognized.
         */
        Token getToken() {
            double theValue;
            if (!str.hasMoreTokens())
                return new Token();
            String s = str.nextToken();
            if (s.equals(" ")) return getToken();
            if (s.equals("^")) return new Token(EXP);
            if (s.equals("/")) return new Token(DIV);
            if (s.equals("*")) return new Token(MULT);
            if (s.equals("(")) return new Token(OPAREN);
            if (s.equals(")")) return new Token(CPAREN);
            if (s.equals("+")) return new Token(PLUS);
            if (s.equals("-")) return new Token(MINUS);
            if (s.equals("x")) return new Token(VALUE, equis);
            if (s.equals("y")) return new Token(VALUE, ye);
            if (s.equals("z")) return new Token(VALUE, zeta);
            if (Character.isLetter(s.charAt(0))) {
                int i = searchFunction(s);
                if (i >= 0)
                    return new Token(FUNCT + i);
                else {
                    mensaje("Parse error");
                    //System.err.println("Parse error");
                    return new Token();
                }
            }
            try {
                theValue = Double.valueOf(s);
            } catch (NumberFormatException e) {

                mensaje("Parse error");
               // System.err.println("Parse error");
                return new Token();
            }
            return new Token(VALUE, theValue);
        }

        int searchFunction(String s) {
            for (int i = 0; i < function.length; i++)
                if (s.equals(function[i]))
                    return i;
            return -1;
        }
    }


    public  void mensaje(String mensaje){

       Toast men;
     men=   makeText(contexto,  ""+mensaje, LENGTH_LONG);

        men.setGravity(Gravity.TOP,0,0);

                men.show();
}}