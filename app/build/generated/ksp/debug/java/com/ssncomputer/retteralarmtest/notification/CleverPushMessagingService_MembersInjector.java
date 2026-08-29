package com.ssncomputer.retteralarmtest.notification;

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
public final class CleverPushMessagingService_MembersInjector implements MembersInjector<CleverPushMessagingService> {
  private final Provider<NotificationEventBus> eventBusProvider;

  public CleverPushMessagingService_MembersInjector(
      Provider<NotificationEventBus> eventBusProvider) {
    this.eventBusProvider = eventBusProvider;
  }

  public static MembersInjector<CleverPushMessagingService> create(
      Provider<NotificationEventBus> eventBusProvider) {
    return new CleverPushMessagingService_MembersInjector(eventBusProvider);
  }

  @Override
  public void injectMembers(CleverPushMessagingService instance) {
    injectEventBus(instance, eventBusProvider.get());
  }

  @InjectedFieldSignature("com.ssncomputer.retteralarmtest.notification.CleverPushMessagingService.eventBus")
  public static void injectEventBus(CleverPushMessagingService instance,
      NotificationEventBus eventBus) {
    instance.eventBus = eventBus;
  }
}
