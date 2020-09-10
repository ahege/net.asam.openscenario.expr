/** */
package net.asam.openscenario.expr.eval;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;

import net.asam.expr.grammar.OscParamDefLexer;
import net.asam.expr.grammar.OscParamDefParser;
import net.asam.expr.grammar.OscParamDefParser.ParamDefContext;
import net.asam.openscenario.expr.ExprValue;

/** @author ahege */
public class ParamDefReader {

  Hashtable<String, ExprValue> readExpValueFromFile(File file) {

    Hashtable<String, ExprValue> result = new Hashtable<>();
    CharStream stream;
    try {

      InputStream in = new FileInputStream(file);
      stream = CharStreams.fromStream(in);
      OscParamDefLexer lexer = new OscParamDefLexer(stream);

      readParamDefs(result, lexer);
    } catch (IOException | Error e) {
      e.printStackTrace();
    }
    return result;
  }

  /**
   * @param result
   * @param lexer
   */
  public void readParamDefs(Hashtable<String, ExprValue> result, OscParamDefLexer lexer) {
    CommonTokenStream tokens = new CommonTokenStream(lexer);
    OscParamDefParser parser = new OscParamDefParser(tokens);
    List<ParamDefContext> paramDefContexts = parser.paramDefs().defs;
    for (ParamDefContext   paramDef: paramDefContexts) {
      String type = paramDef.type.getText();
      if (type.contentEquals("int")) {
        result.put(
            "$" + paramDef.id.getText(),
            ExprValue.createIntValue(Integer.parseInt(paramDef.literal.getText())));
      } else if (type.contentEquals("unsignedInt")) {
        result.put(
            "$" + paramDef.id.getText(),
            ExprValue.createUnsignedIntValue(Long.parseLong(paramDef.literal.getText())));
      } else if (type.contentEquals("unsignedShort")) {
        result.put(
            "$" + paramDef.id.getText(),
            ExprValue.createIntValue(Integer.parseInt(paramDef.literal.getText())));
      } else if (type.contentEquals("double")) {
        result.put(
            "$" + paramDef.id.getText(),
            ExprValue.createDoubleValue(Double.parseDouble(paramDef.literal.getText())));
      }
    }
  }
  
 
  
  public Hashtable<String, ExprValue> readExpValue(File file) {

    Hashtable<String, ExprValue> result = new Hashtable<>();
    CharStream stream;
    try {

      InputStream in = new FileInputStream(file);
      stream = CharStreams.fromStream(in);
      OscParamDefLexer lexer = new OscParamDefLexer(stream);

      readParamDefs(result, lexer);
    } catch (IOException | Error e) {
      e.printStackTrace();
    }
    return result;
  }
  public Hashtable<String, ExprValue> readExpValueFromString(String input) {

    Hashtable<String, ExprValue> result = new Hashtable<>();
    
    try {

      InputStream stream = new ByteArrayInputStream(input.getBytes(StandardCharsets.UTF_8));
      OscParamDefLexer lexer = new OscParamDefLexer(CharStreams.fromStream(stream, StandardCharsets.UTF_8));
      readParamDefs(result, lexer);
    } catch (IOException | Error e) {
      e.printStackTrace();
    }
    return result;
  }
}
