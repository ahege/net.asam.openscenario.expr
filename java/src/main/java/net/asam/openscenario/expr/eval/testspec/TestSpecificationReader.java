/** */
package net.asam.openscenario.expr.eval.testspec;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import net.asam.openscenario.expr.ExprType;
import net.asam.openscenario.expr.ExprValue;
import net.asam.openscenario.expr.eval.ParamDefReader;

/** @author ahege */
public class TestSpecificationReader {

  public static List<TestSpecification> readFromJson(File file)
      throws FileNotFoundException, IOException {

    InputStream is;
    List<TestSpecification> result = new ArrayList<>();

    is = new FileInputStream(file);
    JSONTokener tokener = new JSONTokener(is);
    JSONArray array = new JSONArray(tokener);

    for (int i = 0; i < array.length(); i++) {
      ExprValue expectedExprValue = null;
      String expectedErrorMessage = null;
      int expectedErrorColumn = -1;
      String parameterDefinitionString = null;
      // get the object
      JSONObject testObject = array.getJSONObject(i);
      int id = testObject.getInt("id");
      // get the Definitions
      if (!testObject.isNull("parameterDefinitions")) {
        JSONArray parameterDefinitions = (JSONArray) testObject.get("parameterDefinitions");
        StringBuffer buffer = new StringBuffer();
        for (int k = 0; k < parameterDefinitions.length(); k++) {
          buffer.append(parameterDefinitions.getString(k) + "\n");
        }
        parameterDefinitionString = buffer.toString();
      }
      Hashtable<String, ExprValue> definedParameters = new Hashtable<>();
      if (parameterDefinitionString != null) {
        ParamDefReader paramDefReader = new ParamDefReader();
        definedParameters = paramDefReader.readExpValueFromString(parameterDefinitionString);
      }
      // get the expr
      String expr = testObject.getString("expr");
      if (!testObject.isNull("expectedValue")) {
        Object object = testObject.get("expectedValue");
        if (object.getClass() == Double.class) {
          expectedExprValue = ExprValue.createDoubleValue((Double) object);
        } else {
          expectedExprValue =
              ExprValue.createUnknownNumericLongValue(testObject.getLong("expectedValue"));
        }
        ExprType exprType = null;
        if (!testObject.isNull("expectedDatatype")) {
          String expectedDatatypeString = testObject.getString("expectedDatatype");
          exprType = ExprType.fromString(expectedDatatypeString);
          if (exprType == null) {
            throw new IOException(
                String.format("Unknown datatype '%s' in input file", expectedDatatypeString));
          }
        }
        result.add(new TestSpecification(id, expr, expectedExprValue, definedParameters, exprType));

      } else {
        // There must be an exception
        JSONObject errorObject = testObject.getJSONObject("expectedError");
        expectedErrorMessage = errorObject.getString("message");
        expectedErrorColumn = errorObject.getInt("column");
        result.add(
            new TestSpecification(
                id, expr, expectedErrorMessage, expectedErrorColumn, definedParameters));
      }
    }

    return result;
  }
}
