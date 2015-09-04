package com.codemacro.container;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codemacro.container.util.UnzipJar;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

// bundle-name/classes
// bundle-name/lib/*.jar
// bundle-name/lib/*.jar (with inner jars)
public class BundleClassLoader extends URLClassLoader {
  private static Logger logger = LoggerFactory.getLogger(BundleClassLoader.class);
  private static final String TMP_PATH = new File("./tmp").getAbsolutePath();
  private SharedClassList sharedClasses;
  
  public BundleClassLoader(File home, SharedClassList sharedClasses) {
    super(getClassPath(home));
    this.sharedClasses = sharedClasses;
  }

  @Override
  protected Class<?> findClass(String name) throws ClassNotFoundException {
    logger.debug("try find class {}", name);
    Class<?> claz = null;
    try {
      claz = super.findClass(name);
    } catch (ClassNotFoundException e) {
      claz = null;
    }
    if (claz != null) {
      logger.debug("load from class path for {}", name);
      return claz;
    }
    claz = sharedClasses.get(name);
    if (claz != null) {
      logger.debug("load from shared class for {}", name);
      return claz;
    }
    logger.warn("not found class {}", name);
    throw new ClassNotFoundException(name);
  }

  private static URL[] getClassPath(File home) {
    File lib = new File(home.getAbsoluteFile() + "/lib");
    File[] jars = lib.listFiles(new FilenameFilter() {
      public boolean accept(File file, String name) {
        return name.endsWith(".jar");
      }
    });
    Function<File, URL> tran_fn = new Function<File, URL>() {
      public URL apply(File file) {
        String s = file.isFile() ? "file:/" + file.getAbsolutePath() :
          "file:/" + file.getAbsolutePath() + "/";
        try {
          return new URL(s);
        } catch (MalformedURLException e) {
          logger.warn("URL exception {}", e);
          return null;
        }
      }
    };
    Collection<URL> col_urls = Lists.newArrayList();
    if (jars != null) {
      col_urls.addAll(Lists.transform(Lists.newArrayList(jars), tran_fn));
      for (File jar: jars) { // add inner jar to class path
        if (UnzipJar.hasEntry(jar.getAbsolutePath(), ".jar")) {
          List<File> inner_jars = extractInnerJars(home.getName(), jar.getAbsolutePath());
          if (inner_jars != null) {
            col_urls.addAll(Lists.transform(inner_jars, tran_fn));
          }
        }
      }
    }
    String classes_path = home.getAbsolutePath() + "/classes/";
    File classes = new File(classes_path);
    if (classes.exists()) {
      col_urls.add(tran_fn.apply(classes));
    }
    
    URL[] urls = new URL[col_urls.size()];
    return Lists.newArrayList(Collections2.filter(col_urls, new Predicate<URL>() {
      public boolean apply(URL url) {
        return url != null;
      }
    })).toArray(urls);
  }
  
  private static List<File> extractInnerJars(String name, String jarFile) {
    logger.debug("extract inner jars {}", jarFile);
    String dst = TMP_PATH + File.separator + name;
    try {
      return UnzipJar.unzipJar(dst, jarFile, ".jar");
    } catch (IOException e) {
      logger.warn("extract jar file {} failed", jarFile);
    }
    return null;
  }
  
  public static void main(String[] args) throws ClassNotFoundException, IOException, InterruptedException {
    File jcm = new File("bundle/jcm");
    for (URL u: BundleClassLoader.getClassPath(jcm)) {
      System.err.println(u);
    }
    BundleClassLoader loader = new BundleClassLoader(jcm, null);
    System.out.println(loader.loadClass("com.codemacro.jcm.util.Hash"));
    System.out.println(loader.loadClass("com.codemacro.jcm.JCMMain"));
    loader.close();
  }
}
