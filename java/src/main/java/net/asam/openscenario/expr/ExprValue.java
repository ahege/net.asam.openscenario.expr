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


  private ExprType exprType = null;
  public static final boolean strict = true;

  /** Private constructor */
  private ExprValue() {
    super();
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
  public static ExprValue createUnsignedShortValue(double unsignedShortValue) {
    ExprValue result = new ExprValue();
    result.doubleValue =  unsignedShortValue;
    result.exprType = ExprType.UNSIGNED_SHORT;
    return result;
  }

  /**
   * @param unsignedIntValue
   * @return the created ExprValue
   */
  public static ExprValue createUnsignedIntValue(double unsignedIntValue) {
    ExprValue result = new ExprValue();
    result.doubleValue = unsignedIntValue;
    result.exprType = ExprType.UNSIGNED_INT;
    return result;
  }

  /**
   * @param intValue
   * @return the created ExprValue
   */
  public static ExprValue createIntValue(double intValue) {

    ExprValue result = new ExprValue();
    result.doubleValue = intValue;
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


  
    

  public double getDoubleValue() {
    return this.doubleValue;
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
    
 
  public ExprValue convertToInt() {
    ExprValue result = null;
    Double convertedValue = null;
    convertedValue = getDoubleValue();

    if (convertedValue >= Integer.MIN_VALUE && convertedValue <= Integer.MAX_VALUE)
    {
      result = ExprValue.createIntValue(convertedValue); 
    }

    return result;
  }

 
  
  /**
   * @return
   */
  public Double getConvertedDoubleValue() {
    Double convertedValue;
     convertedValue  =  getDoubleValue();
    return convertedValue;
  }
  
  public ExprValue convertToUnsignedInt() {
    ExprValue result = null;
    Double convertedValue = null;
    convertedValue = getDoubleValue();

    if (convertedValue >= 0 && convertedValue < ((long) Integer.MAX_VALUE) * 2 +1 && convertedValue % 1 != 0.0)
    {
      result = ExprValue.createUnsignedIntValue(convertedValue); 
    }

    return result;
  }

  public ExprValue convertToUnsignedShort() {
    ExprValue result = null;
    Double convertedValue = null;
    convertedValue = getDoubleValue();

    if (convertedValue >= 0 && convertedValue < Short.MAX_VALUE * 2 +1)
    {
    	 result = ExprValue.createUnsignedIntValue(convertedValue); 
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
      return Long.toString(Double.doubleToLongBits(getDoubleValue()));
    }else if (getExprType() == ExprType.INT)
    {
      return Long.toString(Double.doubleToLongBits(getDoubleValue()));
    }else if (getExprType() == ExprType.UNSIGNED_SHORT)
    {
      return Long.toString(Double.doubleToLongBits(getDoubleValue()));
    }else if (getExprType() == ExprType.DOUBLE)
    {
      return Double.toString(getDoubleValue());
    }
    return null;
  }

}
