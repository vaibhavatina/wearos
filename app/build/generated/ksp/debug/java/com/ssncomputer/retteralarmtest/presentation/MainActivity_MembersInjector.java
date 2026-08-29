package com.ssncomputer.retteralarmtest.presentation;

import com.ssncomputer.retteralarmtest.data.local.SecureTokenStorage;
import com.ssncomputer.retteralarmtest.notification.NotificationEventBus;
import dagger.MembersInjector;
import dagger.internal.DaggerGenerated;
import dagger.internal.InjectedFieldSignature;
import dagger.internal.QualifierMetadata;
import javax.annotation.processing.Generated;
import javax.inject.Provider;

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
public final class MainActivity_MembersInjector implements MembersInjector<MainActivity> {
  private final Provider<NotificationEventBus> eventBusProvider;

  private final Provider<SecureTokenStorage> tokenStorageProvider;

  public MainActivity_MembersInjector(Provider<NotificationEventBus> eventBusProvider,
      Provider<SecureTokenStorage> tokenStorageProvider) {
    this.eventBusProvider = eventBusProvider;
    this.tokenStorageProvider = tokenStorageProvider;
  }

  public static MembersInjector<MainActivity> create(
      Provider<NotificationEventBus> eventBusProvider,
      Provider<SecureTokenStorage> tokenStorageProvider) {
    return new MainActivity_MembersInjector(eventBusProvider, tokenStorageProvider);
  }

  @Override
  public void injectMembers(MainActivity instance) {
    injectEventBus(instance, eventBusProvider.get());
    injectTokenStorage(instance, tokenStorageProvider.get());
  }

  @InjectedFieldSignature("com.ssncomputer.retteralarmtest.presentation.MainActivity.eventBus")
  public static void injectEventBus(MainActivity instance, NotificationEventBus eventBus) {
    instance.eventBus = eventBus;
  }

  @InjectedFieldSignature("com.ssncomputer.retteralarmtest.presentation.MainActivity.tokenStorage")
  public static void injectTokenStorage(MainActivity instance, SecureTokenStorage tokenStorage) {
    instance.tokenStorage = tokenStorage;
  }
}
