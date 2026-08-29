package com.ssncomputer.retteralarmtest.util;

import android.content.Context;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
@QualifierMetadata("dagger.hilt.android.qualifiers.ApplicationContext")
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
public final class DeviceQrCodeScanner_Factory implements Factory<DeviceQrCodeScanner> {
  private final Provider<Context> contextProvider;

  public DeviceQrCodeScanner_Factory(Provider<Context> contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Override
  public DeviceQrCodeScanner get() {
    return newInstance(contextProvider.get());
  }

  public static DeviceQrCodeScanner_Factory create(Provider<Context> contextProvider) {
    return new DeviceQrCodeScanner_Factory(contextProvider);
  }

  public static DeviceQrCodeScanner newInstance(Context context) {
    return new DeviceQrCodeScanner(context);
  }
}
