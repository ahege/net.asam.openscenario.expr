/** */
package net.asam.openscenario.expr.eval.testspec;

import java.util.Hashtable;

import net.asam.openscenario.expr.ExprType;
import net.asam.openscenario.expr.ExprValue;

/** @author ahege */
public class TestSpecification {

  private String expectedError;
  private String expr;
  private int expectedErrorColumn;
  private ExprValue expectedValue;
  private Hashtable<String, ExprValue> parameterDefinitions = new Hashtable<>();
  private boolean isErrorDef;
  private int id;
  private ExprType expectedDatatype;

  /**
   * @param id
   * @param expr
   * @param expectedError
   * @param expectedErrorColumn
   * @param parameterDefinitions
   */
  public TestSpecification(
      int id,
      String expr,
      String expectedError,
      int expectedErrorColumn,
      Hashtable<String, ExprValue> parameterDefinitions) {
    super();
    this.id = id;
    this.expr = expr;
    this.isErrorDef = true;
    this.expectedError = expectedError;
    this.expectedErrorColumn = expectedErrorColumn;
    this.parameterDefinitions = parameterDefinitions;
  }

  /**
   * @param id
   * @param expr
   * @param expectedValue
   * @param parameterDefinitions
   * @param expectedDatatype
   */
  public TestSpecification(
      int id,
      String expr,
      ExprValue expectedValue,
      Hashtable<String, ExprValue> parameterDefinitions,
      ExprType expectedDatatype) {
    super();
    this.id = id;
    this.expr = expr;
    this.expectedValue = expectedValue;
    this.parameterDefinitions = parameterDefinitions;
    this.isErrorDef = false;
    this.expectedDatatype = expectedDatatype;
  }

  /** @return the expectedError */
  public String getExpectedError() {
    return this.expectedError;
  }

  /** @return the expr */
  public String getExpr() {
    return this.expr;
  }

  /** @return the expectedErrorColumn */
  public int getExpectedErrorColumn() {
    return this.expectedErrorColumn;
  }

  /** @return the expectedValue */
  public ExprValue getExpectedValue() {
    return this.expectedValue;
  }

  /** @return the parameterDefinitions */
  public Hashtable<String, ExprValue> getParameterDefinitions() {
    return this.parameterDefinitions;
  }

  /** @return the isErrorDef */
  public boolean isErrorDef() {
    return this.isErrorDef;
  }
  /** @return the id */
  public int getId() {
    return this.id;
  }

  /** @return the expectedDatatype */
  public ExprType getExpectedDatatype() {
    return this.expectedDatatype;
  }
}
