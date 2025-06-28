package com.example.emtyapp.di

import com.example.emtyapp.data.Repository.CartRepository
import com.example.emtyapp.data.Repository.CartRepositoryImpl
import com.example.emtyapp.data.Repository.OrderRepository
import com.example.emtyapp.data.Repository.OrderRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class CartModule {

    @Binds
    @Singleton
    abstract fun bindCartRepository(
        cartRepositoryImpl: CartRepositoryImpl
    ): CartRepository
    @Binds
    abstract fun bindOrderRepository(
        orderRepositoryImpl: OrderRepositoryImpl
    ): OrderRepository

}

