/** */
package net.asam.openscenario.expr;

import java.io.ObjectInputStream.GetField;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Date;

/**
 * Immutable ExprValue
 *
 * @author ahege
 */
public class ExprValue {

  private double doubleValue;
  private long longValue;

  private ExprType exprType = null;
  public static final boolean strict = true;

  /** Private constructor */
  private ExprValue() {
    super();
  }

  public static ExprValue cloneIntegerNumericExprValue(ExprValue exprValue, long newValue)
  {
    assert(exprValue.isIntegerNumeric());
    ExprValue result = new ExprValue();
    result.exprType = exprValue.exprType;
    result.longValue = newValue;
    return result;
  }
  
  public static ExprValue cloneFloatingPointExprValue(ExprValue exprValue, double newValue)
  {
    assert(exprValue.isFloatingPointNumeric());
    ExprValue result = new ExprValue();
    result.exprType = exprValue.exprType;
    result.doubleValue = newValue;
    return result;
  }
  /**
   * @param doubleValue initial double value
   * @return the created ExprValue
   */
  public static ExprValue createDoubleValue(double doubleValue) {
    ExprValue result = new ExprValue();
    result.doubleValue = doubleValue;
    result.exprType = ExprType.DOUBLE;
    return result;
  }
  
  /**
   * @param unsignedShortValue
   * @return the created ExprValue
   */
  public static ExprValue createUnsignedShortValue(int unsignedShortValue) {
    ExprValue result = new ExprValue();
    result.longValue = unsignedShortValue;
    result.exprType = ExprType.UNSIGNED_SHORT;
    return result;
  }

  /**
   * @param unsignedIntValue
   * @return the created ExprValue
   */
  public static ExprValue createUnsignedIntValue(long unsignedIntValue) {
    ExprValue result = new ExprValue();
    result.longValue = unsignedIntValue;
    result.exprType = ExprType.UNSIGNED_INT;
    return result;
  }

  /**
   * @param intValue
   * @return the created ExprValue
   */
  public static ExprValue createIntValue(int intValue) {

    ExprValue result = new ExprValue();
    result.longValue = intValue;
    result.exprType = ExprType.INT;
    return result;
  }
  
  /**
   * @return the created ExprValue
   */
  public static ExprValue createStringValue() {

    ExprValue result = new ExprValue();
    result.exprType = ExprType.STRING;
    return result;
  }
  
  /**
   * @return the created ExprValue
   */
  public static ExprValue createDateTimeValue() {

    ExprValue result = new ExprValue();
    result.exprType = ExprType.DATE_TIME;
    return result;
  }
  
  /**
   * @return the created ExprValue
   */
  public static ExprValue createBooleanValue() {

    ExprValue result = new ExprValue();
    result.exprType = ExprType.DATE_TIME;
    return result;
  }


  /**
   * @param intValue
   * @return the created ExprValue
   */
  public static ExprValue createUnknownNumericLongValue(long unknownNumericLongValue) {

    ExprValue result = new ExprValue();
    result.longValue = unknownNumericLongValue;
    result.exprType = ExprType.UNKNOWN_NUMERIC_LONG;
    return result;
  }
    

  public double getDoubleValue() {
    return this.doubleValue;
  }

  public long getLongValue() {
    return this.longValue;
  }

  
  public ExprType getExprType() {
    return this.exprType;
  }

  public boolean isOfType(ExprType[] types) {
    return (Arrays.asList(types).contains(this.exprType));
  }

  public boolean isFloatingPointNumeric()
  {
    return isOfType(new ExprType[] {ExprType.DOUBLE});
  }
    
  public boolean isIntegerNumeric()
  {
    return isOfType(new ExprType[] {ExprType.UNSIGNED_SHORT,ExprType.UNSIGNED_INT, ExprType.INT, ExprType.UNKNOWN_NUMERIC_LONG});
  }
  public ExprValue convertToInt() {
    ExprValue result = null;
    Long convertedValue = null;
    convertedValue = getConvertedLongValue();

    if (convertedValue >= Integer.MIN_VALUE && convertedValue <= Integer.MAX_VALUE)
    {
      result = ExprValue.createIntValue(convertedValue.intValue()); 
    }

    return result;
  }

  /**
   * @return
   */
  public Long getConvertedLongValue() {
    Long convertedValue;
    if (isIntegerNumeric()) {
        convertedValue = getLongValue(); 
    }else
    {
      convertedValue  = (long) getDoubleValue();
    }
    return convertedValue;
  }
  
  /**
   * @return
   */
  public Double getConvertedDoubleValue() {
    Double convertedValue;
    if (isIntegerNumeric()) {
        convertedValue = (double) getLongValue(); 
    }else
    {
      convertedValue  =  getDoubleValue();
    }
    return convertedValue;
  }
  
  public ExprValue convertToUnsignedInt() {
    ExprValue result = null;
    Long convertedValue = null;
    convertedValue = getConvertedLongValue();

    if (convertedValue >= 0 && convertedValue < ((long) Integer.MAX_VALUE) * 2 +1)
    {
      result = ExprValue.createUnsignedIntValue(convertedValue); 
    }

    return result;
  }

  public ExprValue convertToUnsignedShort() {
    ExprValue result = null;
    Long convertedValue = null;
    convertedValue = getConvertedLongValue();

    if (convertedValue >= 0 && convertedValue < Short.MAX_VALUE * 2 +1)
    {
      result = ExprValue.createUnsignedShortValue(convertedValue.intValue()); 
    }

    return result;
  }

  public ExprValue convertToDouble() {
    return createDoubleValue(getConvertedDoubleValue());
  }
  
  @Override
  public String toString() {
    if (getExprType() == ExprType.UNSIGNED_INT)
    {
      return Long.toString(getLongValue());
    }else if (getExprType() == ExprType.INT)
    {
      return Long.toString(getLongValue());
    }else if (getExprType() == ExprType.UNSIGNED_SHORT)
    {
      return Long.toString(getLongValue());
    }else if (getExprType() == ExprType.DOUBLE)
    {
      return Double.toString(getDoubleValue());
    }else if (getExprType() == ExprType.UNKNOWN_NUMERIC_LONG)
    {
      return Long.toString(getLongValue());
    }
    return null;
  }

}
