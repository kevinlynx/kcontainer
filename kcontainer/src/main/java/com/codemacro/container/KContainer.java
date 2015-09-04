package com.codemacro.container;

import java.io.File;
import java.io.FileFilter;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * kcontainer directory:
 * -- lib
 * -- bundle
 *    |-- app
 *        |-- bundle.prop
 *        |-- lib
 * 
 */
public class KContainer {
  private static Logger logger = LoggerFactory.getLogger(KContainer.class);
  private static final String BUNDLE_PATH = "bundle";
  private SharedClassList sharedClasses;
  private Map<String, Bundle> bundles;
  
  public KContainer() {
    this.sharedClasses = new SharedClassList();
    this.bundles = new HashMap<String, Bundle>();
  }

  public boolean start() {
    loadBundles();
    startBundles();
    return true;
  }
  
  public void stop() {
    stopBundles();
    sharedClasses.clear();
    bundles.clear();
  }
  
  public Map<String, Bundle> getBundles() {
    return bundles;
  }

  private void loadBundles() {
    File root = new File(BUNDLE_PATH);
    logger.info("load bundles...");
    File[] bundleDirs = root.listFiles(new FileFilter() {
      public boolean accept(File file) {
        return file.isDirectory();
      }
    });
    if (bundleDirs == null) {
      logger.error("bundle path not exists");
      return ;
    }
    for (File f: bundleDirs) {
      Bundle bundle = loadBundle(f);
      if (bundle != null) {
        logger.info("load bundle [{}] done", bundle.getName());
        bundles.put(bundle.getName(), bundle);
      }
    }
  }

  private Bundle loadBundle(File home) {
    try {
      BundleConf bundle_conf = new BundleConf(home);
      Bundle bundle = Bundle.create(home, home.getName(), sharedClasses, bundle_conf);
      return bundle;
    } catch (IllegalArgumentException e) {
      logger.warn("load bundle [{}] failed", home.getName());
      return null;
    }
  }
  
  private void startBundles() {
    logger.info("start all bundles");
    for (Bundle bundle : bundles.values()) {
      BundleContext context = new BundleContext();
      context.setName(bundle.getName());
      bundle.start(context);
    }
  }

  private void stopBundles() {
    logger.info("stop all bundles");
    for (Bundle bundle : bundles.values()) {
      BundleContext context = new BundleContext();
      context.setName(bundle.getName());
      bundle.stop(context);
    }
  }

}
