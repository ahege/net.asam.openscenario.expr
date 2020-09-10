/**
 * 
 */
package net.asam.openscenario.expr.test;

/**
 * @author ahege
 *
 */
public abstract class ErrorLogger {

  public  abstract void issueError(String message , boolean fail);
  public void issueError(String message)
  {
    issueError(message, false);
    
  }
  
  
  
}
