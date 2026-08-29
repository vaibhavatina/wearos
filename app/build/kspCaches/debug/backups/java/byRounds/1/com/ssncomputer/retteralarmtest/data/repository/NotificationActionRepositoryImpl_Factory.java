package com.ssncomputer.retteralarmtest.data.repository;

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
public final class NotificationActionRepositoryImpl_Factory implements Factory<NotificationActionRepositoryImpl> {
  private final Provider<WatchApiService> apiServiceProvider;

  public NotificationActionRepositoryImpl_Factory(Provider<WatchApiService> apiServiceProvider) {
    this.apiServiceProvider = apiServiceProvider;
  }

  @Override
  public NotificationActionRepositoryImpl get() {
    return newInstance(apiServiceProvider.get());
  }

  public static NotificationActionRepositoryImpl_Factory create(
      Provider<WatchApiService> apiServiceProvider) {
    return new NotificationActionRepositoryImpl_Factory(apiServiceProvider);
  }

  public static NotificationActionRepositoryImpl newInstance(WatchApiService apiService) {
    return new NotificationActionRepositoryImpl(apiService);
  }
}
