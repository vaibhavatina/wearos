package com.ssncomputer.retteralarmtest.data.repository;

import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage;
import com.ssncomputer.retteralarmtest.data.remote.WatchApiService;
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
public final class AuthRepositoryImpl_Factory implements Factory<AuthRepositoryImpl> {
  private final Provider<WatchApiService> apiServiceProvider;

  private final Provider<SecureTokenStorage> tokenStorageProvider;

  public AuthRepositoryImpl_Factory(Provider<WatchApiService> apiServiceProvider,
      Provider<SecureTokenStorage> tokenStorageProvider) {
    this.apiServiceProvider = apiServiceProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  @Override
  public AuthRepositoryImpl get() {
    return newInstance(apiServiceProvider.get(), tokenStorageProvider.get());
  }

  public static AuthRepositoryImpl_Factory create(Provider<WatchApiService> apiServiceProvider,
      Provider<SecureTokenStorage> tokenStorageProvider) {
    return new AuthRepositoryImpl_Factory(apiServiceProvider, tokenStorageProvider);
  }

  public static AuthRepositoryImpl newInstance(WatchApiService apiService,
      SecureTokenStorage tokenStorage) {
    return new AuthRepositoryImpl(apiService, tokenStorage);
  }
}
