package org.pangolincrawler.cli.cmd;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.pangolincrawler.cli.SdkClient;
import org.pangolincrawler.sdk.ApiResponse;
import org.pangolincrawler.sdk.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;

import de.vandermeer.asciitable.AsciiTable;
import de.vandermeer.asciitable.CWC_LongestLine;
import de.vandermeer.asciitable.CWC_LongestWordMax;
import de.vandermeer.asciithemes.a7.A7_Grids;

public class BaseCommands {

  public static final class TableHead {

    protected static interface Conventor {
      public String convent(String value);
    }

    private String key;
    private String alias;
    private Conventor conventor;

    public TableHead(String key, String alias) {
      super();
      this.key = key;
      this.alias = alias;
    }

    public TableHead(String key, String alias, Conventor conventor) {
      super();
      this.key = key;
      this.alias = alias;
      this.conventor = conventor;
    }

    @Override
    public String toString() {
      String ret = null;
      if (null != alias && alias.trim().length() > 0) {
        ret = alias;
      } else if (null != key && key.trim().length() > 0) {
        ret = key;
      } else {
        ret = "Unkown";
      }

      return ret;
    }
  }

  @SuppressWarnings("unused")
  protected static class WaitingProcessBar extends Thread {

    private String message;

    public static WaitingProcessBar show(String message) {
      WaitingProcessBar bar = new WaitingProcessBar(message);

      System.out.println(message);

      return bar;
    }

    private WaitingProcessBar(String msg) {
      this.message = msg;
    }

    public void shutdown() {
      this.interrupt();
      System.out.println();
    }

    @Override
    public void run() {
      super.run();
      System.out.print(message);
      while (true) {
        System.out.print(".");
        try {
          Thread.sleep(1000);
        } catch (InterruptedException e) {
        }

      }
    }
  }

  @Autowired
  protected SdkClient sdkClient;

  protected String errorReport(ApiResponse res) {
    if (StringUtils.isBlank(res.getMessage())) {
      return "Unkown Error(" + res.getCode() + "), Please check the server's log.";
    }
    return res.getMessage();
  }

  protected String errorReportFromException(String msg, Exception e) {
    return msg + ", cause by " + e.getLocalizedMessage();
  }

  protected boolean writeFile(String content, String path) {

    try (FileOutputStream o = new FileOutputStream(path)) {
      o.write(content.getBytes(StandardCharsets.UTF_8));
      return true;
    } catch (IOException e) {
      return false;
    }
  }

  protected String readFile(File file) throws IOException {
    return FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
  }

  /**
   * http://www.vandermeer.de/projects/skb/java/asciitable/
   */
  protected String createConsoleTable(List<TableHead> title,
      List<Map<String, Object>> values, boolean vertical,
      List<String> prettyJsonKeyList) {
    if (vertical) {
      if (null != prettyJsonKeyList) {
        convertToPrettyJson(false, values, prettyJsonKeyList.toArray(new String[] {}));
      }
      return createConsoleResultAsList(title, values);
    } else {
      if (null != prettyJsonKeyList) {
        convertToPrettyJson(true, values, prettyJsonKeyList.toArray(new String[] {}));
      }
      return createConsoleResultAsTable(title, values, -1);
    }
  }

  private String createConsoleResultAsList(List<TableHead> titles,
      List<Map<String, Object>> values) {

    if (null == values || null == titles) {
      return "Emtpy";
    }

    StringJoiner sj1 = new StringJoiner(
        System.lineSeparator() + "---------------------------" + System.lineSeparator());

    values.forEach(m -> {
      StringJoiner sj2 = new StringJoiner(System.lineSeparator());
      m.forEach((k, v) -> {
        titles.forEach(t -> {
          if (StringUtils.equals(t.key, k)) {
            String rv = String.valueOf(v);
            if (null != t.conventor) {
              rv = t.conventor.convent(rv);
            }
            sj2.add(t.alias + ":\t\t\t" + rv);
          }
        });
      });
      sj1.add(sj2.toString());
    });

    return sj1.toString();
  }

  protected String createConsoleResultAsTable(List<TableHead> title,
      List<Map<String, Object>> values, int width) {

    AsciiTable at = new AsciiTable();

    at.getContext().setGrid(A7_Grids.minusBarPlusEquals());

    if (width > 0) {
      at.getRenderer().setCWC(new CWC_LongestWordMax(100));
    } else {
      at.getRenderer().setCWC(new CWC_LongestLine());
    }

    at.addRule();
    at.addRow(title);
    at.addRule();

    values.stream().forEach(eachMap -> {
      List<Object> rowValues = new ArrayList<>();
      title.forEach(eachTitle -> {
        if (eachMap.containsKey(eachTitle.key)) {
          Object v = eachMap.get(eachTitle.key);
          if (v == null) {
            rowValues.add("NULL");
          } else {
            if (null != eachTitle.conventor) {
              v = eachTitle.conventor.convent(String.valueOf(v));
            }
          }
          rowValues.add(v);
        } else {
          rowValues.add("<empty>");
        }

      });
      at.addRow(rowValues);
      at.addRule();
    });
    at.getContext().setWidth(130);
    return at.render();
  }

  /**
   * @param htmlLineSp
   *          used for display as table
   * @param valueMap
   * @param keys
   * @return
   */
  protected List<Map<String, Object>> convertToPrettyJson(boolean htmlLineSp,
      List<Map<String, Object>> valueMap, String... keys) {

    for (Map<String, Object> each : valueMap) {
      for (String eachKey : keys) {
        if (each.containsKey(eachKey)) {
          String tmp = JsonUtils.convertPrettyJson(String.valueOf(each.get(eachKey)));
          if (htmlLineSp) {
            tmp = tmp.replace("\n", "<br/>").replace(" ", ".");
          }
          each.put(eachKey, tmp);
        }
      }
    }

    return Collections.emptyList();
  }

}
