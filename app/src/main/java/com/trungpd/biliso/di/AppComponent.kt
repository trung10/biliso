package com.trungpd.biliso.di

import android.content.Context
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component
interface AppComponent {
    fun getContext(): Context

    //fun getRetrofitHelper(): RetrofitHelper
}