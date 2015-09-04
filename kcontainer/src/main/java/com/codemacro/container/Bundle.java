package com.codemacro.container;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bundle {
  private static Logger logger = LoggerFactory.getLogger(Bundle.class);
  private ClassLoader claz_loader;
  private String name;
  private String initClass;
  private IBundleInitializer initializer;

  public Bundle(String name, String initClass, ClassLoader loader) {
    this.name = name;
    this.initClass = initClass;
    this.claz_loader = loader;
  }

  public void start(BundleContext context) {
    if (initClass == null || initClass.isEmpty()) {
      logger.info("bundle {} has no init class", name);
      return ;
    }
    try {
      Class<?> claz = claz_loader.loadClass(initClass);
      initializer = (IBundleInitializer) claz.newInstance();
      initializer.start(context);
      logger.info("bundle {} start done", name);
    } catch (Exception e) {
      logger.warn("bundle {} init failed {}", name, e);
    }
  }
  
  public void stop(BundleContext context) {
    if (initializer == null) return ;
    initializer.stop(context);
    logger.info("bundle {} stop done", name);
  }

  public String getName() {
    return name;
  }

  public ClassLoader getClassLoader() {
    return claz_loader;
  }

  public static Bundle create(File home, String name, SharedClassList sharedClasses, 
      BundleConf conf) {
    BundleClassLoader loader = new BundleClassLoader(home, sharedClasses);
    List<String> exports = conf.getExportClassNames();
    if (exports != null) {
      logger.info("load exported classes for {}", name);
      loadExports(loader, sharedClasses, exports);
    }
    return new Bundle(name, conf.getInitClassName(), loader);
  }
  
  private static void loadExports(ClassLoader loader, SharedClassList sharedClasses,
      List<String> exports) {
      for (String claz_name: exports) {
        try {
          Class<?> claz = loader.loadClass(claz_name);
          sharedClasses.put(claz_name, claz);
        } catch (ClassNotFoundException e) {
          logger.warn("load class {} failed", claz_name);
        }
      }
  }

}
