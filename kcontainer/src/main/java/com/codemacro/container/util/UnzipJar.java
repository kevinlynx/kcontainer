package com.codemacro.container.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;

public class UnzipJar {
  private static int DEFAULT_BUFFER_SIZE = 4 * 1024;
  
  // unzipJar("../tmp/jcm", "../bundle/jcm/lib/jcm.server-0.1.0.jar", ".jar");
  public static List<File> unzipJar(String destinationDir, String jarPath, String suffix) throws IOException {
    File file = new File(jarPath);
    JarFile jar = new JarFile(file);
    List<File> results = new LinkedList<File>();
    for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
      JarEntry entry = (JarEntry) enums.nextElement();
      String fileName = destinationDir + File.separator + entry.getName();
      File f = new File(fileName);
      if (!entry.isDirectory() && entry.getName().endsWith(suffix)) {
        f.getParentFile().mkdirs();
        InputStream is = jar.getInputStream(entry);
        FileOutputStream fos = new FileOutputStream(f);
        try {
          copyStream(is, fos);
          results.add(f);
        } finally {
          fos.close();
          is.close();
        }
      } 
    }
    jar.close();
    return results;
  }
  
  public static boolean hasEntry(String jarPath, String suffix) {
    JarFile jar;
    boolean ret = false;
    try {
      jar = new JarFile(jarPath);
      for (Enumeration<JarEntry> enums = jar.entries(); enums.hasMoreElements();) {
        JarEntry entry = (JarEntry) enums.nextElement();
        if (entry.getName().endsWith(suffix)) {
          ret = true;
          break;
        }
      } 
      jar.close();
    } catch (IOException e) {
      return false;
    }
    return ret;
  }

  private static void copyStream(InputStream is, OutputStream out) throws IOException {
    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
    int n = 0;
    while (-1 != (n = is.read(buffer))) {
        out.write(buffer, 0, n);
    }
  }
}
