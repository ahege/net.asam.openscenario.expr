/** */
package net.asam.openscenario.expr.eval;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Hashtable;
import java.util.Stack;

import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;

import net.asam.expr.grammar.OscExprBaseListener;
import net.asam.expr.grammar.OscExprLexer;
import net.asam.expr.grammar.OscExprParser;
import net.asam.expr.grammar.OscExprParser.FunctionOneArgumentContext;
import net.asam.expr.grammar.OscExprParser.FunctionTwoArgumentsContext;
import net.asam.openscenario.expr.ExprType;
import net.asam.openscenario.expr.ExprValue;
import net.asam.openscenario.expr.SemanticError;

/** @author ahege */
public class EvaluatorListener extends OscExprBaseListener {

  private Hashtable<String, ExprValue> definedParameters;

  private Stack<ExprValue> valueStack = new Stack<>();

  private ExprType expectedDatatype;

  private static final BigDecimal maxLong = BigDecimal.valueOf(Long.MAX_VALUE);
  private static final BigDecimal minLong = BigDecimal.valueOf(Long.MIN_VALUE);
  private static final BigDecimal maxDouble = BigDecimal.valueOf(Double.MAX_VALUE);
  private static final BigDecimal minDouble = BigDecimal.valueOf(Double.MIN_VALUE);

  /**
   * @param definedParameters
   * @param expectedDatatype
   */
  public EvaluatorListener(
      Hashtable<String, ExprValue> definedParameters, ExprType expectedDatatype) {
    super();
    this.definedParameters = definedParameters;
    this.expectedDatatype = expectedDatatype;
  }

  /**
   * @param definedParameters public EvaluatorListener(Hashtable<String, ExprValue>
   *     definedParameters) { super(); this.definedParameters = definedParameters; }
   *     <p>/** {@inheritDoc}
   *     <p>The default implementation does nothing.
   */
  @Override
  public void exitIdExpr(OscExprParser.IdExprContext ctx) {
    // Put the value on the stack or throw an error
    ExprValue exprValue = this.definedParameters.get(ctx.getText());
    if (exprValue == null) {
      throw new SemanticError(
          String.format("Parameter '%s' is not defined.", ctx.getText()), getColumn(ctx));
    }

    if (exprValue.isOfType(
        new ExprType[] {ExprType.BOOLEAN, ExprType.STRING, ExprType.DATE_TIME})) {
      throw new SemanticError(
          String.format(
              "Expressions are exclusively supported for numeric types. Paramter type '%s' is not supported",
              exprValue.toString()),
          getColumn(ctx));
    }
    this.valueStack.push(exprValue);
  }

  @Override
  public void exitTypecast(OscExprParser.TypecastContext ctx) {
    String typeCastString = ctx.type.getText();
    ExprValue firstExprValue = this.valueStack.pop();
    ExprValue result = convert(firstExprValue, typeCastString, getColumn(ctx));
    this.valueStack.push(result);
  }

  /**
   * @param ctx
   * @param typeCastString
   * @param firstExprValue
   * @param result
   * @return
   * @throws SemanticError
   */
  private ExprValue convert(ExprValue firstExprValue, String typeCastString, int column)
      throws SemanticError {
    ExprValue result = null;
    if (typeCastString.contentEquals("int")) {
      result = firstExprValue.convertToInt();
    } else if (typeCastString.contentEquals("unsignedInt")) {
      result = firstExprValue.convertToUnsignedInt();
    } else if (typeCastString.contentEquals("unsignedShort")) {
      result = firstExprValue.convertToUnsignedShort();
    } else if (typeCastString.contentEquals("double")) {
      result = firstExprValue.convertToDouble();
    }
    if (result == null) {
      throw new SemanticError(
          String.format(
              "Value '%s' cannot be converted to type '%s'",
              firstExprValue.toString(), typeCastString),
          column);
    }
    return result;
  }

  @Override
  public void exitFunctionOneArgument(FunctionOneArgumentContext ctx) {
    ExprValue firstExprValue = this.valueStack.pop();
    ExprValue result = null;
    // If for future use with more functions
    if (ctx.func.getType() == OscExprLexer.SQRT) {
      double firstValue = -1;
      if (firstExprValue.isFloatingPointNumeric()) {
        firstValue = firstExprValue.getConvertedDoubleValue();
      } else {
        // is Integer Numeric
        firstValue = firstExprValue.getConvertedDoubleValue();
      }
      if (firstValue < 0.0) {
        throw new SemanticError(
            "Cannot calculate square root from a negative value.",
            getColumn((ParserRuleContext) ctx.getChild(2)));
      }
      result = ExprValue.createDoubleValue(Math.sqrt(firstValue));
      this.valueStack.push(result);
    }
  }

  @Override
  public void exitFunctionTwoArguments(FunctionTwoArgumentsContext ctx) {
    ExprValue secondExprValue = this.valueStack.pop();
    ExprValue firstExprValue = this.valueStack.pop();
    ExprValue result = null;
    // If for future use with more functions
    if (ctx.func.getType() == OscExprLexer.POW) {
      double firstValue = -1;
      double secondValue = -1;
      if (firstExprValue.isFloatingPointNumeric()) {
        firstValue = firstExprValue.getConvertedDoubleValue();
      } else {
        // is Integer Numeric
        firstValue = firstExprValue.getConvertedDoubleValue();
      }
      if (secondExprValue.isFloatingPointNumeric()) {
        secondValue = secondExprValue.getConvertedDoubleValue();
      } else {
        // is Integer Numeric
        secondValue = secondExprValue.getConvertedDoubleValue();
      }
      if (firstValue < 0.0 && secondValue != 0.0 && secondValue != 1.0 && secondValue != -1.0) {
        throw new SemanticError(
            "Raising a negative basis to an exponent that is not 0 or 1 or -1 is not allowed",
            getColumn((ParserRuleContext) ctx.getChild(2)));
      }
      if (firstValue == 0.0 && secondValue < 0) {
        throw new SemanticError(
            "Raising the value 0 to an exponent < 0 is not allowed",
            getColumn((ParserRuleContext) ctx.getChild(2)));
      }
      double powResult = Math.pow(firstValue, secondValue);
      if (Double.isInfinite(powResult) || Double.isNaN(powResult)) {
        throw new SemanticError(
            "The result of the pow operation ins infinite or NaN",
            getColumn((ParserRuleContext) ctx.getChild(2)));
      }

      result = ExprValue.createDoubleValue(powResult);
      this.valueStack.push(result);
    }
  }
  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   */
  @Override
  public void exitNumLiteral(OscExprParser.NumLiteralContext ctx) {
    // try to parse long value first
    BigDecimal bigDecimal = new BigDecimal(ctx.getText());
    if (bigDecimal.scale() == 0) {
      testOutOfRange(bigDecimal, ctx.num);
      this.valueStack.push(ExprValue.createUnknownNumericLongValue(bigDecimal.longValue()));

    } else {
      testOutOfRangeDouble(bigDecimal, ctx.num);
      // It must be a double
      this.valueStack.push(ExprValue.createDoubleValue(bigDecimal.doubleValue()));
    }
  }

  @Override
  public void exitUnaryMinus(OscExprParser.UnaryMinusContext ctx) {
    // getValueFromStack
    ExprValue exprValue = this.valueStack.pop();
    if (exprValue.isIntegerNumeric()) {
      this.valueStack.push(
          ExprValue.cloneIntegerNumericExprValue(exprValue, (-1) * exprValue.getLongValue()));
    } else if (exprValue.isFloatingPointNumeric()) {
      this.valueStack.push(ExprValue.createDoubleValue((-1) * exprValue.getDoubleValue()));
    } else {
      throw new SemanticError("Value must be of type 'int' or of type 'double'", getColumn(ctx));
    }
  }

  @Override
  public void exitPlusMinus(OscExprParser.PlusMinusContext ctx) {
    ExprValue secondExprValue = this.valueStack.pop();
    ExprValue firstExprValue = this.valueStack.pop();
    ExprValue result = null;

    // second Expr must be the same type as the first
    if (ctx.op.getType() == OscExprParser.PLUS) {
      if (firstExprValue.isFloatingPointNumeric() || secondExprValue.isFloatingPointNumeric()) {
        Double firstValue = firstExprValue.getConvertedDoubleValue();
        Double secondValue = secondExprValue.getConvertedDoubleValue();
        testOutOfRangeDouble(BigDecimal.valueOf(firstValue).add(BigDecimal.valueOf(secondValue)), ctx.op);
        result = ExprValue.createDoubleValue(firstValue + secondValue);
      } else if (firstExprValue.isIntegerNumeric() && secondExprValue.isIntegerNumeric()) {
        Long firstValue = firstExprValue.getConvertedLongValue();
        Long secondValue = secondExprValue.getConvertedLongValue();
        // Test for overflow
        testOutOfRange(BigDecimal.valueOf(firstValue).add(BigDecimal.valueOf(secondValue)), ctx.op);       
        result = ExprValue.cloneIntegerNumericExprValue(firstExprValue, firstValue + secondValue);
      }
    } else // MINUS
    {
      if (firstExprValue.isFloatingPointNumeric() || secondExprValue.isFloatingPointNumeric()) {
        Double firstValue = firstExprValue.getConvertedDoubleValue();
        Double secondValue = secondExprValue.getConvertedDoubleValue();
        testOutOfRangeDouble(BigDecimal.valueOf(firstValue).subtract(BigDecimal.valueOf(secondValue)), ctx.op);
        result = ExprValue.createDoubleValue(firstValue - secondValue);
      } else if (firstExprValue.isIntegerNumeric() && secondExprValue.isIntegerNumeric()) {
        Long firstValue = firstExprValue.getConvertedLongValue();
        Long secondValue = secondExprValue.getConvertedLongValue();
        // Test for overflow
        testOutOfRange(BigDecimal.valueOf(firstValue).subtract(BigDecimal.valueOf(secondValue)), ctx.op);
        
        result = ExprValue.cloneIntegerNumericExprValue(firstExprValue, firstValue - secondValue);
      }
    }

    if (result != null) {
      this.valueStack.push(result);
    }

    //

  }

  /**
   * {@inheritDoc}
   *
   * <p>The default implementation does nothing.
   */
  @Override
  public void exitMultiDivMod(OscExprParser.MultiDivModContext ctx) {
    ExprValue secondExprValue = this.valueStack.pop();
    ExprValue firstExprValue = this.valueStack.pop();
    ExprValue result = null;
    if (ctx.op.getType() == OscExprParser.MULTIPLY) {
      if (firstExprValue.isFloatingPointNumeric() || secondExprValue.isFloatingPointNumeric()) {
        Double firstValue = firstExprValue.getConvertedDoubleValue();
        Double secondValue = secondExprValue.getConvertedDoubleValue();
        testOutOfRangeDouble(BigDecimal.valueOf(firstValue).multiply(BigDecimal.valueOf(secondValue)), ctx.op);
        result = ExprValue.createDoubleValue(firstValue * secondValue);
      } else if (firstExprValue.isIntegerNumeric() && secondExprValue.isIntegerNumeric()) {
        Long firstValue = firstExprValue.getConvertedLongValue();
        Long secondValue = secondExprValue.getConvertedLongValue();
        testOutOfRange(BigDecimal.valueOf(firstValue).multiply(BigDecimal.valueOf(secondValue)), ctx.op);
        result = ExprValue.cloneIntegerNumericExprValue(firstExprValue, firstValue * secondValue);
      }
    } else if (ctx.op.getType() == OscExprParser.DIVIDE) {
      if (firstExprValue.isFloatingPointNumeric() || secondExprValue.isFloatingPointNumeric()) {
        Double firstValue = firstExprValue.getConvertedDoubleValue();
        Double secondValue = secondExprValue.getConvertedDoubleValue();
        if (secondValue == 0.0) {
          throw new SemanticError(
              "Divison by zero", getColumn((ParserRuleContext) ctx.getChild(2)));
        }
        testOutOfRangeDouble(BigDecimal.valueOf(firstValue).divide(BigDecimal.valueOf(secondValue), RoundingMode.HALF_UP), ctx.op);        
        result = ExprValue.createDoubleValue(firstValue / secondValue);
      } else if (firstExprValue.isIntegerNumeric() && secondExprValue.isIntegerNumeric()) {
        Long firstValue = firstExprValue.getConvertedLongValue();
        Long secondValue = secondExprValue.getConvertedLongValue();
        if (secondValue == 0L) {
          throw new SemanticError(
              "Divison by zero", getColumn((ParserRuleContext) ctx.getChild(2)));
        }
        result = ExprValue.cloneIntegerNumericExprValue(firstExprValue, firstValue / secondValue);
      }

    } else if (ctx.op.getType() == OscExprParser.MODULO) {
      if (firstExprValue.isFloatingPointNumeric() || secondExprValue.isFloatingPointNumeric()) {
        Double firstValue = firstExprValue.getConvertedDoubleValue();
        Double secondValue = secondExprValue.getConvertedDoubleValue();
        testOutOfRangeDouble(BigDecimal.valueOf(firstValue).remainder(BigDecimal.valueOf(secondValue)), ctx.op);
        result = ExprValue.createDoubleValue(firstValue % secondValue);
      } else if (firstExprValue.isIntegerNumeric() && secondExprValue.isIntegerNumeric()) {
        Long firstValue = firstExprValue.getConvertedLongValue();
        Long secondValue = secondExprValue.getConvertedLongValue();
        result = ExprValue.cloneIntegerNumericExprValue(firstExprValue, firstValue % secondValue);
      }
    }

    if (result != null) {
      this.valueStack.push(result);
    }
  }

  private int getColumn(ParserRuleContext ruleContext) {
    return ruleContext.getStart().getCharPositionInLine();
  }

  private int getColumn(Token token) {
    return token.getCharPositionInLine();
  }

  public ExprValue getResult() {
    assert (this.valueStack.size() == 1);
    ExprValue result = this.valueStack.pop();
    if (this.expectedDatatype != null) {
      if (result.isFloatingPointNumeric() && expectedDatatype.isIntegerNumeric()) {
        throw new SemanticError(
            "Double values must be explicitly casted. Use (int), (unsignedInt) or (unsignedShort) for explicit cast.",
            2);
      } else {
        result = convert(result, expectedDatatype.toString(), 2);
      }
    }
    return result;
  }

  private void testOutOfRange(BigDecimal tester, Token token) throws Error {
    if (tester.compareTo(maxLong) > 0 || tester.compareTo(minLong) < 0)
      throw new SemanticError(
          "Internal Overflow (limits of internal 64 bit integer value exceeded)", getColumn(token));
  }
  
  private void testOutOfRangeDouble(BigDecimal tester, Token token) throws Error {

    if (tester.abs().compareTo(maxDouble) > 0 || tester.abs().compareTo(minDouble) < 0)
      throw new SemanticError(
          "Internal Overflow (limits of internal 64 bit integer value exceeded)", getColumn(token));
  }
}
