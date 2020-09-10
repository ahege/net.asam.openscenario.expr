/** */
package net.asam.openscenario.expr.test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import net.asam.expr.grammar.OscExprLexer;
import net.asam.expr.grammar.OscExprParser;
import net.asam.expr.grammar.OscExprParser.ProgContext;
import net.asam.openscenario.expr.ExprValue;
import net.asam.openscenario.expr.SemanticError;
import net.asam.openscenario.expr.eval.EvaluatorListener;
import net.asam.openscenario.expr.eval.ParamDefReader;
import net.asam.openscenario.expr.eval.testspec.TestSpecification;
import net.asam.openscenario.expr.eval.testspec.TestSpecificationReader;

/** @author ahege */
public class BasicTest {
  

  @Test
  public void testFromTestSpecification() {

    File file =
        new File(
            getClass()
                .getClassLoader()
                .getResource("net/asam/openscenario/expr/testfiles/testDefinitions.json")
                .getFile());

    try {
      List<TestSpecification> tests = TestSpecificationReader.readFromJson(file);

      ExprTestRunner.runTest(tests, new ErrorLogger(){
      
        @Override
        public void issueError(String message, boolean fail) {
          if (fail)
          {
            fail(message);
          }
          
        }
      });

    } catch (FileNotFoundException e) {
      e.printStackTrace();
      fail();
    } catch (IOException e) {
      e.printStackTrace();
      fail();
    }
  }

  private File[] getAllFiles(String resourceDirectoryName) throws IOException {
    File[] result = null;
    try {
      Enumeration<URL> en = getClass().getClassLoader().getResources(resourceDirectoryName);
      if (en.hasMoreElements()) {
        URL metaInf = en.nextElement();
        File fileMetaInf = new File(metaInf.toURI());

        result = fileMetaInf.listFiles();
      }
    } catch (URISyntaxException e) {
      // Do nothing
    }
    return result;
  }

  private HashSet<File[]> extractFilesets(String resourceDirectoryName) throws Exception {
    File[] sets = getAllFiles(resourceDirectoryName);
    HashSet<File[]> result = new HashSet<>();
    Hashtable<String, File> exprFiles = new Hashtable<>();
    Hashtable<String, File> parFiles = new Hashtable<>();
    for (File file : sets) {
      if (file.isFile()) {
        String filename = file.getName();
        if (filename.endsWith(".expr")) {
          exprFiles.put(filename.substring(0, filename.length() - ".expr".length()), file);
        } else if ((filename.endsWith(".par"))) {
          parFiles.put(filename.substring(0, filename.length() - ".par".length()), file);
        } else {
          throw new Exception(
              "File must end with either '.expr'  for expression files or with '.par' for parameter files.");
        }
      }
    }

    for (String key : parFiles.keySet()) {
      if (exprFiles.get(key) == null)
        throw new Exception(
            String.format("File '%s.expr' must be defined when defining '%s.par", key, key));
    }

    for (String key : exprFiles.keySet()) {
      File[] files = null;
      if (parFiles.get(key) != null) {
        files = new File[2];
        files[1] = parFiles.get(key);

      } else {
        files = new File[1];
      }
      files[0] = exprFiles.get(key);
      result.add(files);
    }
    return result;
  }
}
