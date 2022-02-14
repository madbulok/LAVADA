package com.uzlov.dating.lavada.di.modules

import com.google.firebase.storage.FirebaseStorage
import com.uzlov.dating.lavada.storage.FirebaseStorageService
import com.uzlov.dating.lavada.storage.IStorage
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class StorageModule {

    @Singleton
    @Provides
    fun provideStorageService(storage: FirebaseStorage): IStorage =
        FirebaseStorageService(storage)
}