/** */
package net.asam.openscenario.expr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import net.asam.openscenario.expr.eval.testspec.TestSpecification;
import net.asam.openscenario.expr.eval.testspec.TestSpecificationReader;
import net.asam.openscenario.expr.test.ErrorLogger;
import net.asam.openscenario.expr.test.ExprTestRunner;

/** @author ahege */
public class TestExprMain {

  /** @param args */
  public static void main(String[] args) {
    try {
      // get the expected Exception
      // get the expected column number;

      if (args.length != 1)
      {
        System.out.println("Usage: TestExprMain <inputFile>" ); 
      }
      List<TestSpecification> tests = TestSpecificationReader.readFromJson(new File(args[0]));
      ExprTestRunner.runTest(
          tests,
          new ErrorLogger() {

            @Override
            public void issueError(String message, boolean fail) {
              System.out.println(message + "\n");
            }
          });

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
}
