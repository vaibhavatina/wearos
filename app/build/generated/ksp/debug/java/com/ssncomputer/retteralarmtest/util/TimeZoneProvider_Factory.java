package com.ssncomputer.retteralarmtest.util;

import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;

@ScopeMetadata
@QualifierMetadata
@DaggerGenerated
@Generated(
    value = "dagger.internal.codegen.ComponentProcessor",
    comments = "https://dagger.dev"
)
@SuppressWarnings({
    "unchecked",
    "rawtypes",
    "KotlinInternal",
    "KotlinInternalInJava",
    "cast"
})
public final class TimeZoneProvider_Factory implements Factory<TimeZoneProvider> {
  @Override
  public TimeZoneProvider get() {
    return newInstance();
  }

  public static TimeZoneProvider_Factory create() {
    return InstanceHolder.INSTANCE;
  }

  public static TimeZoneProvider newInstance() {
    return new TimeZoneProvider();
  }

  private static final class InstanceHolder {
    private static final TimeZoneProvider_Factory INSTANCE = new TimeZoneProvider_Factory();
  }
}
