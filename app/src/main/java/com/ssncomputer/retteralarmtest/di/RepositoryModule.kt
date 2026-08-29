package com.ssncomputer.retteralarmtest.di

import com.ssncomputer.retteralarmtest.data.repository.AuthRepository
import com.ssncomputer.retteralarmtest.data.repository.AuthRepositoryImpl
import com.ssncomputer.retteralarmtest.data.repository.NotificationActionRepository
import com.ssncomputer.retteralarmtest.data.repository.NotificationActionRepositoryImpl
import com.ssncomputer.retteralarmtest.util.DeviceQrCodeScanner
import com.ssncomputer.retteralarmtest.util.QrCodeScanner
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindNotificationActionRepository(
        impl: NotificationActionRepositoryImpl
    ): NotificationActionRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindQrCodeScanner(impl: DeviceQrCodeScanner): QrCodeScanner
}
