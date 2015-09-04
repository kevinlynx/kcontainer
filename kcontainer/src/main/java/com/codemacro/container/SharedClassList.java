package com.codemacro.container;

import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedClassList {
  private static Logger logger = LoggerFactory.getLogger(SharedClassList.class);
  private ConcurrentHashMap<String, Class<?>> classes;

  public SharedClassList() {
    classes = new ConcurrentHashMap<String, Class<?>>();
  }
  
  public void put(String full_name, Class<?> claz) {
    classes.put(full_name, claz);
    logger.debug("add shared class {}", full_name);
  }
  
  public Class<?> get(String full_name) {
    return classes.containsKey(full_name) ? classes.get(full_name) : null;
  }
  
  public void clear() {
    classes.clear();
  }
}
