/**
 * 
 */
package net.asam.openscenario.expr;

/**
 * @author ahege
 *
 */
public class TestShort {

  /**
   * @param args
   */
  public static void main(String[] args) {
  
    short varShort1 = Short.MAX_VALUE;
    short varShort2 = 32;
    // Multiply operator is interpreted as an int operation
    System.out.println((varShort1*varShort2)); 
    // result is 1048544
    
    int maxInt = Integer.MAX_VALUE;
    // Multiply operator is interpreted as an int operation
    System.out.println((maxInt*32));
    // Result is -32
    
    // Multiply operator is interpreted as an int operation
    System.out.println((maxInt*32L));
    // Result is 68719476704
    

    long maxIntLong = Integer.MAX_VALUE;
    int varInt3 = 32;
    // Multiply operator is interpreted as an long operation
    System.out.println((maxIntLong*varInt3));
    // Result is 68719476704
    
    // Multiply operator is interpreted as an long operation
    System.out.println((varInt3*maxIntLong));
    // Result is 68719476704
    
  }

}
