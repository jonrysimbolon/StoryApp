package com.story.application

import android.app.Application
import com.story.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.androidx.fragment.koin.fragmentFactory
import org.koin.core.context.startKoin

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@MyApplication)
            fragmentFactory()
            modules(
                gsonModule,
                retrofitModule(BuildConfig.BASE_URL),
                localModule,
                dataStoreModule,
                glideModule,
                repositoryModule,
                bitmapModule,
                factoryModule,
                customDialogModule,
                adapterModule,
                viewModelModule,
            )
        }
    }
}