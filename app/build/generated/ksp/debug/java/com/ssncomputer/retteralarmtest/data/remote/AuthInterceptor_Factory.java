package com.ssncomputer.retteralarmtest.data.remote;

import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage;
import com.ssncomputer.retteralarmtest.util.TimeZoneProvider;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class AuthInterceptor_Factory implements Factory<AuthInterceptor> {
  private final Provider<SecureTokenStorage> tokenStorageProvider;

  private final Provider<TimeZoneProvider> timeZoneProvider;

  public AuthInterceptor_Factory(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<TimeZoneProvider> timeZoneProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.timeZoneProvider = timeZoneProvider;
  }

  @Override
  public AuthInterceptor get() {
    return newInstance(tokenStorageProvider.get(), timeZoneProvider.get());
  }

  public static AuthInterceptor_Factory create(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<TimeZoneProvider> timeZoneProvider) {
    return new AuthInterceptor_Factory(tokenStorageProvider, timeZoneProvider);
  }

  public static AuthInterceptor newInstance(SecureTokenStorage tokenStorage,
      TimeZoneProvider timeZoneProvider) {
    return new AuthInterceptor(tokenStorage, timeZoneProvider);
  }
}
