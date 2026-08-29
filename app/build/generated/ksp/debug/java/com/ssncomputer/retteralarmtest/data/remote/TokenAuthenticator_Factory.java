package com.ssncomputer.retteralarmtest.data.remote;

import com.squareup.moshi.Moshi;
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

  private final Provider<WatchApiService> refreshApiServiceProvider;

  private final Provider<Moshi> moshiProvider;

  public TokenAuthenticator_Factory(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<WatchApiService> refreshApiServiceProvider, Provider<Moshi> moshiProvider) {
    this.tokenStorageProvider = tokenStorageProvider;
    this.refreshApiServiceProvider = refreshApiServiceProvider;
    this.moshiProvider = moshiProvider;
  }

  @Override
  public TokenAuthenticator get() {
    return newInstance(tokenStorageProvider.get(), refreshApiServiceProvider, moshiProvider.get());
  }

  public static TokenAuthenticator_Factory create(Provider<SecureTokenStorage> tokenStorageProvider,
      Provider<WatchApiService> refreshApiServiceProvider, Provider<Moshi> moshiProvider) {
    return new TokenAuthenticator_Factory(tokenStorageProvider, refreshApiServiceProvider, moshiProvider);
  }

  public static TokenAuthenticator newInstance(SecureTokenStorage tokenStorage,
      Provider<WatchApiService> refreshApiServiceProvider, Moshi moshi) {
    return new TokenAuthenticator(tokenStorage, refreshApiServiceProvider, moshi);
  }
}
