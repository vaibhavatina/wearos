package com.ssncomputer.retteralarmtest.presentation.notificationdetail;

import androidx.lifecycle.SavedStateHandle;
import com.ssncomputer.retteralarmtest.data.repository.NotificationActionRepository;
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
public final class NotificationDetailViewModel_Factory implements Factory<NotificationDetailViewModel> {
  private final Provider<NotificationActionRepository> repositoryProvider;

  private final Provider<SavedStateHandle> savedStateHandleProvider;

  public NotificationDetailViewModel_Factory(
      Provider<NotificationActionRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    this.repositoryProvider = repositoryProvider;
    this.savedStateHandleProvider = savedStateHandleProvider;
  }

  @Override
  public NotificationDetailViewModel get() {
    return newInstance(repositoryProvider.get(), savedStateHandleProvider.get());
  }

  public static NotificationDetailViewModel_Factory create(
      Provider<NotificationActionRepository> repositoryProvider,
      Provider<SavedStateHandle> savedStateHandleProvider) {
    return new NotificationDetailViewModel_Factory(repositoryProvider, savedStateHandleProvider);
  }

  public static NotificationDetailViewModel newInstance(NotificationActionRepository repository,
      SavedStateHandle savedStateHandle) {
    return new NotificationDetailViewModel(repository, savedStateHandle);
  }
}
