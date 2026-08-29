package com.ssncomputer.retteralarmtest.presentation.login;

import com.ssncomputer.retteralarmtest.data.repository.AuthRepository;
import com.ssncomputer.retteralarmtest.util.QrCodeScanner;
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
public final class LoginViewModel_Factory implements Factory<LoginViewModel> {
  private final Provider<AuthRepository> repositoryProvider;

  private final Provider<QrCodeScanner> qrCodeScannerProvider;

  public LoginViewModel_Factory(Provider<AuthRepository> repositoryProvider,
      Provider<QrCodeScanner> qrCodeScannerProvider) {
    this.repositoryProvider = repositoryProvider;
    this.qrCodeScannerProvider = qrCodeScannerProvider;
  }

  @Override
  public LoginViewModel get() {
    return newInstance(repositoryProvider.get(), qrCodeScannerProvider.get());
  }

  public static LoginViewModel_Factory create(Provider<AuthRepository> repositoryProvider,
      Provider<QrCodeScanner> qrCodeScannerProvider) {
    return new LoginViewModel_Factory(repositoryProvider, qrCodeScannerProvider);
  }

  public static LoginViewModel newInstance(AuthRepository repository, QrCodeScanner qrCodeScanner) {
    return new LoginViewModel(repository, qrCodeScanner);
  }
}
