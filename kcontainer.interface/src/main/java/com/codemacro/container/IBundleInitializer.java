package com.codemacro.container;

public interface IBundleInitializer {
  void start(BundleContext context);
  void stop(BundleContext context);
}
