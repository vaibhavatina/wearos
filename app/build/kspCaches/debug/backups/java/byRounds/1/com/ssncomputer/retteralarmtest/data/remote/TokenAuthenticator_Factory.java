package com.ssncomputer.retteralarmtest.data.remote;

import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage;
import dagger.internal.DaggerGenerated;
import dagger.internal.Factory;
import dagger.internal.QualifierMetadata;
import dagger.internal.ScopeMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

@ScopeMetadata("javax.inject.Singleton")
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
public final class TokenAuthenticator_Factory implements Factory<TokenAuthenticator> {
  private final Provider<SecureTokenStorage> tokenStorageProvider;

  private final Provider<AuthApi> refreshApiServiceProvider;

  public TokenAuthenticator_Factory(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<AuthApi> refreshApiServiceProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.refreshApiServiceProvider = refreshApiServiceProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(tokenStorageProvider.get(), refreshApiServiceProvider);
  }

  public static TokenAuthenticator_Factory create(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<AuthApi> refreshApiServiceProvider) {
    return new TokenAuthenticator_Factory(tokenStorageProvider, refreshApiServiceProvider);
  }

  public static TokenAuthenticator newInstance(SecureTokenStorage tokenStorage,
      Provider<AuthApi> refreshApiServiceProvider) {
    return new TokenAuthenticator(tokenStorage, refreshApiServiceProvider);
  }
}
