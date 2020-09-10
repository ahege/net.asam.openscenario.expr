/** */
package net.asam.openscenario.expr.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

import net.asam.expr.grammar.OscExprLexer;
import net.asam.expr.grammar.OscExprParser;
import net.asam.expr.grammar.OscExprParser.ProgContext;
import net.asam.openscenario.expr.ExprType;
import net.asam.openscenario.expr.ExprValue;
import net.asam.openscenario.expr.SemanticError;
import net.asam.openscenario.expr.eval.EvaluatorListener;
import net.asam.openscenario.expr.eval.testspec.TestSpecification;

/** @author ahege */
public class ExprTestRunner {
  /**
   * @param tests
   * @throws IOException
   */
  public static void runTest(List<TestSpecification> tests, ErrorLogger errorLogger)
      throws IOException {
    for (TestSpecification test : tests) {
      boolean isSuccessfull = true;
      InputStream stream =
          new ByteArrayInputStream(test.getExpr().getBytes(StandardCharsets.UTF_8));
      OscExprLexer lexer = new OscExprLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));

      try {

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        OscExprParser parser = new OscExprParser(tokens);
        ProgContext progContext = parser.prog();

        EvaluatorListener baseListener = null;
        ParseTreeWalker parseTreeWalker = new ParseTreeWalker();
        if (!test.isErrorDef()) {
          ExprType expectedDatatype = test.getExpectedDatatype();
          if (expectedDatatype != null) {

            baseListener = new EvaluatorListener(test.getParameterDefinitions(), expectedDatatype);
          } else {
            baseListener = new EvaluatorListener(test.getParameterDefinitions(), null);
          }

          try {
            parseTreeWalker.walk(baseListener, progContext);
            ExprValue resultExprValue = baseListener.getResult();

            if (resultExprValue.isFloatingPointNumeric()) {
              if (test.getExpectedValue().getConvertedDoubleValue().doubleValue()
                  != resultExprValue.getDoubleValue()) {

                StringBuffer errors = new StringBuffer();
                errors.append(
                    "Expected Value: "
                        + test.getExpectedValue().getConvertedDoubleValue().doubleValue());

                errors.append("\nActual value: " + resultExprValue.getDoubleValue());
                issueError(test.getId(), errors.toString(), errorLogger);
                isSuccessfull = false;
              }

            } else if (resultExprValue.isIntegerNumeric()
                && test.getExpectedValue().isIntegerNumeric()) {

              if (test.getExpectedValue().getConvertedLongValue().doubleValue()
                  != resultExprValue.getLongValue()) {

                StringBuffer errors = new StringBuffer();
                errors.append(
                    "Expected Value: "
                        + test.getExpectedValue().getConvertedLongValue().longValue());
                errors.append("\nActual value: " + resultExprValue.getLongValue());
                issueError(test.getId(), errors.toString(), errorLogger);
                isSuccessfull = false;
              }

            } else {
              StringBuffer errors = new StringBuffer();
              errors.append("Expected Value: " + test.getExpectedValue().getDoubleValue());
              errors.append("\nActual value: " + resultExprValue.getLongValue());
              issueError(test.getId(), errors.toString(), errorLogger);
              isSuccessfull = false;
            }
          } catch (SemanticError e) {
            issueError(test.getId(), e.getMessage() + String.format("(%d)",e.getColumn()), errorLogger);
          }
        } else {
          // testException
          try {

            ExprType expectedDatatype = test.getExpectedDatatype();
            if (expectedDatatype != null) {

              baseListener = new EvaluatorListener(test.getParameterDefinitions(), expectedDatatype);
            } else {
              baseListener = new EvaluatorListener(test.getParameterDefinitions(), null);
            }
            parseTreeWalker.walk(baseListener, progContext);
            baseListener.getResult();
            
          } catch (SemanticError error) {
            boolean isColumnConsistent = test.getExpectedErrorColumn() == error.getColumn();
            boolean IsMessageConsistent = test.getExpectedError().equals(error.getMessage());
            if (!IsMessageConsistent || !isColumnConsistent) {
              isSuccessfull = false;
              StringBuffer errors = new StringBuffer();

              if (!IsMessageConsistent) {
                errors.append("Expected error:" + test.getExpectedError());
                errors.append("\nActual error: " + error.getMessage());
              }
              if (!isColumnConsistent) {
                if(!IsMessageConsistent)
                {
                  errors.append("\n");
                }
                errors.append("Expected Column:" + test.getExpectedErrorColumn());
                errors.append("\nActual column: " + error.getColumn());
              }
              issueError(test.getId(), errors.toString(), errorLogger);
            }
          }
        }
      } catch (Exception e) {
        issueError(test.getId(), e.getMessage(), errorLogger);
      }
      if (isSuccessfull)
        errorLogger.issueError(String.format("Test %d successful.", test.getId()), false);
    }
  }

  /** @param test */
  private static void issueError(int testId, String error, ErrorLogger errorLogger) {
    errorLogger.issueError(String.format("Error in test %d\n", testId) + error + "\n", true);
  }
}
