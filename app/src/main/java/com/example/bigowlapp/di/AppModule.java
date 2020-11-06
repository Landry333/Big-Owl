package com.example.bigowlapp.di;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.components.ApplicationComponent;

@Module
@InstallIn(ApplicationComponent.class)
public class AppModule {

    @Singleton
    @Provides
    public static FirebaseFirestore provideDatabase() {
        return FirebaseFirestore.getInstance();
    }

    @Singleton
    @Provides
    public static FirebaseAuth provideAuthDatabase() {
        return FirebaseAuth.getInstance();
    }

}
