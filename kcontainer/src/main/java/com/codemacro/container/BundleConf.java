package com.codemacro.container;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Properties;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

// bundle properties, located at `bundle-name/name.prop`
public class BundleConf {
  private static final String KEY_INIT = "init";
  private static final String KEY_EXPORT_CLASS = "export-class";
  private static final String KEY_IMPORT_CLASS = "import-class";
  private List<String> exportClassNames;
  private List<String> importClassNames;
  private String initClassName;
  
  public BundleConf(File home) {
    String name = home.getName();
    File prop = new File(home.getAbsolutePath() + File.separator + name + ".prop");
    if (!prop.exists()) {
      throw new IllegalArgumentException("bundle prop file not exist");
    }
    parse(prop);
  }

  public List<String> getExportClassNames() {
    return exportClassNames;
  }

  public List<String> getImportClassNames() {
    return importClassNames;
  }

  public String getInitClassName() {
    return initClassName;
  }
  
  private void parse(File prop) {
    Properties properties = new Properties();
    try {
      properties.load(new FileInputStream(prop));
      
    } catch (IOException e) {
      throw new IllegalArgumentException("parse bundle file failed");
    }
    this.initClassName = properties.getProperty(KEY_INIT);
    this.exportClassNames = parseList(KEY_EXPORT_CLASS, properties);
    this.importClassNames = parseList(KEY_IMPORT_CLASS, properties);
  }
  
  private List<String> parseList(String key, Properties properties) {
    String val = properties.getProperty(key);
    return val == null ? null : Lists.transform(Lists.newArrayList(val.split(";")),
        new Function<String, String>() {
          public String apply(String from) {
            return from.trim();
          }
    });
  }
}
