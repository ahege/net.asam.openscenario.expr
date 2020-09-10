/** */
package net.asam.openscenario.expr;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

/** @author ahege */
public enum ExprType {
  UNSIGNED_INT,
  INT,
  UNSIGNED_SHORT,
  DOUBLE,
  UNKNOWN_NUMERIC_LONG,
  STRING,
  BOOLEAN,
  DATE_TIME;

  private static Hashtable<ExprType, Set<ExprType>> conversionTable = new Hashtable<>();

  static {
    Set<ExprType> exprSet = new HashSet<>();
    Set<ExprType> copiedSet = new HashSet<>();
    // UNSIGNED INT
    exprSet.add(UNSIGNED_INT);
    exprSet.add(INT);
    exprSet.add(UNSIGNED_SHORT);
    exprSet.add(DOUBLE);
    exprSet.add(UNKNOWN_NUMERIC_LONG);
    copiedSet = new HashSet<>();
    copiedSet.addAll(exprSet);
    exprSet.clear();
    conversionTable.put(UNSIGNED_INT, copiedSet);
    // INT
    exprSet.add(UNSIGNED_INT);
    exprSet.add(INT);
    exprSet.add(UNSIGNED_SHORT);
    exprSet.add(DOUBLE);
    exprSet.add(UNKNOWN_NUMERIC_LONG);
    copiedSet = new HashSet<>();
    copiedSet.addAll(exprSet);
    exprSet.clear();
    conversionTable.put(INT, copiedSet);
    // UNSIGNED_SHORT
    exprSet.add(UNSIGNED_INT);
    exprSet.add(INT);
    exprSet.add(UNSIGNED_SHORT);
    exprSet.add(DOUBLE);
    exprSet.add(UNKNOWN_NUMERIC_LONG);
    copiedSet = new HashSet<>();
    copiedSet.addAll(exprSet);
    exprSet.clear();
    conversionTable.put(UNSIGNED_SHORT, copiedSet);
    // DOUBLE
    exprSet.add(UNSIGNED_INT);
    exprSet.add(INT);
    exprSet.add(UNSIGNED_SHORT);
    exprSet.add(DOUBLE);
    exprSet.add(UNKNOWN_NUMERIC_LONG);
    copiedSet = new HashSet<>();
    copiedSet.addAll(exprSet);
    exprSet.clear();
    conversionTable.put(DOUBLE, copiedSet);
    // UNKNOWN_NUMERIC_LONG
    exprSet.add(UNSIGNED_INT);
    exprSet.add(INT);
    exprSet.add(UNSIGNED_SHORT);
    exprSet.add(DOUBLE);
    exprSet.add(UNKNOWN_NUMERIC_LONG);
    copiedSet = new HashSet<>();
    copiedSet.addAll(exprSet);
    exprSet.clear();
    conversionTable.put(UNKNOWN_NUMERIC_LONG, copiedSet);

  }

  public boolean isTypeConvertibleTo(ExprType targetType) {
    return conversionTable.get(this).contains(targetType);
  }
  
  @Override
  public String toString() {
    if (this == UNSIGNED_INT)
    {
      return "unsignedInt";
    }else if (this == INT)
    {
      return "int";
    }else if (this == STRING)
    {
      return "string";
    }else if (this == DATE_TIME)
    {
      return "dateTime";
    }else if (this == UNSIGNED_SHORT)
    {
      return "unsignedShort";
    }else if (this == DOUBLE)
    {
      return "double";
    }else if (this == BOOLEAN)
    {
      return "boolean";
    }else if (this == UNKNOWN_NUMERIC_LONG)
    {
      return "integer numerical literal";
    }
    return null;
  }
  

  public static ExprType fromString(String dataType) {
    if (dataType.equals("unsignedInt"))
    {
      return ExprType.UNSIGNED_INT;
    }else if (dataType.equals("int"))
    {
      return INT;
    }else if (dataType.equals("string"))
    {
      return STRING;
    }else if (dataType.equals("dateTime"))
    {
      return DATE_TIME;
    }else if (dataType.equals("unsignedShort"))
    {
      return UNSIGNED_SHORT;
    }else if (dataType.equals("double"))
    {
      return DOUBLE;
    }else if (dataType.equals("boolean"))
    {
      return BOOLEAN;
    }
    return null;
  }
  public boolean isIntegerNumeric()
  {
    return (this == INT || this == UNSIGNED_INT || this == UNSIGNED_SHORT);
  }
}
