/** */
package net.asam.openscenario.expr;

import java.util.Formatter;

/** @author ahege */
public class SemanticError extends Error{

  private String message;
  private int column;
  /**
   * @param message
   * @param column
   */
  public SemanticError(String message, int column) {
    super();
    this.message = message;
    this.column = column;
  }

  /** @return the column */
  public int getColumn() {
    return this.column;
  }
  /** @return the message */
  @Override
  public String getMessage() {
    return this.message;
  }
 
}
