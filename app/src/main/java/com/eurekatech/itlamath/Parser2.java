package com.eurekatech.itlamath;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.eurekatech.itlamath.big.BigDecimalMath;
import com.eurekatech.itlamath.big.DefaultBigDecimalMath;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Stack;
import java.util.StringTokenizer;

import static android.widget.Toast.LENGTH_LONG;
import static android.widget.Toast.makeText;

public class Parser2 {

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
                    "floor", "eXp", "ln", "cot", "sct", "csc", "fact"
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
                    new Precedence(7, 6), // FUNCT

            };
    private Angulos tipoAngulo;
    private Context contexto;
    private String string;
    private Stack<Integer> opStack; // Operator stack for     conversion
    private Stack<BigDecimal> postfixStack; // Stack for postfix     machine

    // . . . continua codigo de los metodos de la clase .            ..
    private StringTokenizer str; // StringTokenizer     stream

    /**
     * Construct an evaluator object.
     *
     * @param s the string containing the expression.
     */
    Parser2(String s, Context context, Angulos TipoDeAngulo) {
        tipoAngulo = TipoDeAngulo;
        if (s.contains("e")) {

            s = s.replace("e", Double.toString(Math.E));
        }

        if (s.contains("π")) {

            s = s.replace("π", Double.toString(Math.PI));
        }


        if (s.contains("√")) {
            s = s.replace("√", "sqrt ");
        }


        contexto = context;
        opStack = new Stack<>();
        postfixStack = new Stack<>();
        string = unary2Binary(s);
        str = new StringTokenizer(string, "+*-/^()xyz ", true);
        opStack.push(EOL);

    }


    /**
     * Internal routine to compute x^n.
     */
    private BigDecimal pow(BigDecimal x, BigDecimal n) {
        try {
            return BigDecimalMath.pow(x, n, MathContext.DECIMAL32);
        } catch (Exception e) {
            mensaje("Error");
        }
        return new BigDecimal("0");
     /*   if(x.compareTo(new BigDecimal("0") )==0){

        }
        if (x.compareTo(new BigDecimal("0") )==0) {
            if (n.compareTo(new BigDecimal("0") )==0)
                mensaje("0^0 no está definido");
            //  System.err.println("0^0 is undefined");
            return new BigDecimal("0");
        }
        if (n.compareTo(new BigDecimal("0")) == -1) {
            mensaje("Negative exponent");
            // System.err.println("Negative exponent");
            return new BigDecimal("0") ;
        }
        if (n.compareTo(new BigDecimal("0") )==0)
            return new BigDecimal("1");
        if (n.remainder(new BigDecimal("2")).compareTo(new BigDecimal("0"))   == 0)
            return pow(x.multiply(x) , n.divide(new BigDecimal("2")));
        else
            return x.multiply( pow(x, n.subtract(new BigDecimal( "1"))));*/
    }

    BigDecimal getValue(BigDecimal x) {
        return getValue(x, new BigDecimal("0"), new BigDecimal("0"));
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y) {
        return getValue(x, y, new BigDecimal("0"));
    }

    public BigDecimal getValue(BigDecimal x, BigDecimal y, BigDecimal z) {
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
            return new BigDecimal("0");
        }
        BigDecimal theResult = postFixTopAndPop();
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
            BigDecimal d = getTop();
            postfixStack.push(functionEval(topOp, d));
            opStack.pop();
            return;
        }
        BigDecimal rhs = getTop();
        BigDecimal lhs = getTop();
        if (topOp == EXP)
            postfixStack.push(pow(lhs, rhs));
        else if (topOp == PLUS)
            postfixStack.push(lhs.add(rhs));
        else if (topOp == MINUS)
            postfixStack.push(lhs.subtract(rhs));
        else if (topOp == MULT)
            postfixStack.push(lhs.multiply(rhs));
        else if (topOp == DIV)
            if (rhs.compareTo(new BigDecimal("0")) != 0)
                postfixStack.push(lhs.divide(rhs, 20, BigDecimal.ROUND_HALF_UP));
            else {
                mensaje("Division por cero");
                // System.err.println("Division by zero");
                postfixStack.push(lhs);
            }
        opStack.pop();
    }


    private BigDecimal functionEval(int topOp, BigDecimal d) {
        BigDecimal y = new BigDecimal("0");
        String pi = Double.toString(Math.PI);

        if ((topOp > 9 && topOp < 13) || (topOp > 19 && topOp < 23)) {
            //  mensaje("entra "+topOp);
            switch (tipoAngulo) {

                case Degree:
/*                    d=d.multiply(BigDecimalMath.pi(MathContext.DECIMAL32));
                    d=DefaultBigDecimalMath.divide(d,new BigDecimal("180"));
                    d=d.divide(new BigDecimal("180"), 20, BigDecimal.ROUND_HALF_UP);*/
                    d = d.multiply(new BigDecimal(pi).divide(new BigDecimal("180"), 20, BigDecimal.ROUND_HALF_UP));
                    break;

                case gradia:
                    /*d=d.multiply(BigDecimalMath.pi(MathContext.DECIMAL32));
                    d=d.divide(new BigDecimal("200"), 20, BigDecimal.ROUND_HALF_UP);*/
                    d = d.multiply(new BigDecimal(pi).divide(new BigDecimal("200"), 20, BigDecimal.ROUND_HALF_UP));
                    break;
            }
        }
        try {
            switch (topOp) {
                case 9:

                    y = BigDecimalMath.sqrt(d, MathContext.DECIMAL32);
                    //  y = new BigDecimal(Math.sqrt(d.doubleValue()));


                    break;
                case 10:
                    y = BigDecimalMath.sin(d, MathContext.DECIMAL32);
                    // y=new BigDecimal( Math.sin(d.doubleValue()));

                    break;
                case 11:
                    y = BigDecimalMath.cos(d, MathContext.DECIMAL32);
                    //  y=new BigDecimal( Math.cos(d.doubleValue()));
                    break;
                case 12:
                    y = BigDecimalMath.tan(d, MathContext.DECIMAL32);
                    // y=new BigDecimal( Math.tan(d.doubleValue()));
                    break;
                case 13:
                    y = BigDecimalMath.asin(d, MathContext.DECIMAL32);
                    // y = new BigDecimal(Math.asin(d.doubleValue()));
                    break;
                case 14:
                    y = new BigDecimal(Math.acos(d.doubleValue()));
                    break;
                case 15:
                    y = new BigDecimal(Math.atan(d.doubleValue()));
                    break;
                case 16:
                    y = new BigDecimal(Math.log10(d.doubleValue()));
                    break;
                case 17:
                    y = new BigDecimal(Math.floor(d.doubleValue()));
                    break;
                case 18:

                    y = new BigDecimal(Math.exp(d.doubleValue()));
                    break;
                case 19:
                    y = BigDecimalMath.log(d, MathContext.DECIMAL32);
                    //  y=new BigDecimal( Math.log(d.doubleValue()));
                    break;
                case 20:
                    y = BigDecimalMath.cot(d, MathContext.DECIMAL32);
                    //  y=new BigDecimal("1").divide(new BigDecimal( Math.tan(d.doubleValue())),20,BigDecimal.ROUND_HALF_UP);
                    break;
                case 21:
                    y = DefaultBigDecimalMath.divide(new BigDecimal("1"), BigDecimalMath.cos(d, MathContext.DECIMAL32));

                    // y=new BigDecimal("1").divide(new BigDecimal( Math.cos(d.doubleValue())),20,BigDecimal.ROUND_HALF_UP);
                    break;
                case 22:
                    y = DefaultBigDecimalMath.divide(new BigDecimal("1"), BigDecimalMath.sin(d, MathContext.DECIMAL32));
                    //   y=new BigDecimal("1").divide(new BigDecimal( Math.sin(d.doubleValue())),20,BigDecimal.ROUND_HALF_UP);

                    break;
                case 23:
                    Integer num = Integer.parseInt(d.toString());
                    if (num < 0) {
                        mensaje("Error: no factorial de numero negativo");
                        y = new BigDecimal("0");
                        return y;
                    }
                    int factorial = 1;
                    for (Integer i = 1; i <= num; i++) {
                        factorial = factorial * i;
                    }


                    y = new BigDecimal(factorial);


            }
            if (topOp > 12 && topOp < 16) {
                mensaje("entra " + topOp);
                switch (tipoAngulo) {

                    case Degree:
                        y = y.divide(new BigDecimal(pi), 20, BigDecimal.ROUND_HALF_UP);
                        y = y.multiply(new BigDecimal("180"), MathContext.DECIMAL32);

                        break;

                    case gradia:
                        y = y.multiply(new BigDecimal(pi).divide(new BigDecimal("200"), 20, BigDecimal.ROUND_HALF_UP));
                        break;
                }
            }
            return y;
        } catch (Exception e) {
            mensaje("Error");
        }
        return new BigDecimal("0");
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
    private BigDecimal postFixTopAndPop() {
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
    private BigDecimal getTop() {
        if (postfixStack.isEmpty()) {
            mensaje("Falta operando");
            //  System.err.println("Missing operand");
            return new BigDecimal("0");
        }
        return postFixTopAndPop();
    }

    public void mensaje(String mensaje) {

        Toast men;
        men = makeText(contexto, "" + mensaje, LENGTH_LONG);

        men.setGravity(Gravity.CENTER, 0, 0);

        men.show();
    }

    public enum Angulos {
        Degree, radian, gradia
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
        private BigDecimal value;

        Token() {
            this(EOL);
        }

        Token(int t) {
            this(t, new BigDecimal("0"));
        }

        Token(int t, BigDecimal v) {
            type = t;
            value = v;
        }

        int getType() {
            return type;
        }

        BigDecimal getValue() {
            return value;
        }
    }

    private class EvalTokenizer {
        private StringTokenizer str;
        private BigDecimal equis;
        private BigDecimal ye;
        private BigDecimal zeta;

        EvalTokenizer(StringTokenizer is, BigDecimal x,
                      BigDecimal y, BigDecimal z) {
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
            BigDecimal theValue;
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
                theValue = new BigDecimal(s);
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


}
