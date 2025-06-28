package com.example.emtyapp.data.api

import com.example.emtyapp.data.Entities.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {


    @GET("products")
    suspend fun getProducts(): Response<List<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<Product>


    @POST("users")
    suspend fun registerUser(@Body user: User): Response<User>

    @GET("users")
    suspend fun getUsers(): Response<List<User>>

    @GET("users/{id}")
    suspend fun getUserById(@Path("id") id: String): Response<User>


    @POST("favorites")
    suspend fun addToFavorites(@Body favorite: Favorite): Response<Favorite>

    @GET("favorites")
    suspend fun getFavoritesByUser(@Query("userId") userId: String): Response<List<Favorite>>

    @DELETE("favorites/{id}")
    suspend fun removeFromFavorites(@Path("id") id: String): Response<Unit>


    @GET("cart")
    suspend fun getCartItems(@Query("userId") userId: String): Response<List<CartItem>>

    @POST("cart")
    suspend fun addToCart(@Body cartItem: CartItem): Response<CartItem>

    @PUT("cart/{id}")
    suspend fun updateCartItem(@Path("id") id: String, @Body cartItem: CartItem): Response<CartItem>

    @DELETE("cart/{id}")
    suspend fun removeCartItem(@Path("id") id: String): Response<Unit>

    @DELETE("cart")
    suspend fun clearCart(@Query("userId") userId: String): Response<Unit>
    @POST("orders")
    suspend fun createOrder(@Body order: Order): Response<Order>


    @GET("orders")
    suspend fun getAllOrders(): Response<List<Order>>


    @GET("orders/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: String): Response<List<Order>>


    @PUT("orders/{orderId}/cancel")
    suspend fun cancelOrder(@Path("orderId") orderId: String): Response<ApiResponse>


    @POST("orders/{orderId}/reorder")
    suspend fun reorderItems(@Path("orderId") orderId: String): Response<Order>


    @PUT("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body status: OrderStatusUpdate
    ): Response<ApiResponse>
}
suspend fun ApiService.getUserOrdersFiltered(userId: String): Response<List<Order>> {
    return try {
        val allOrdersResponse = getAllOrders()
        if (allOrdersResponse.isSuccessful) {
            val allOrders = allOrdersResponse.body() ?: emptyList()
            val userOrders = allOrders.filter { it.userId == userId }
            Response.success(userOrders)
        } else {
            allOrdersResponse
        }
    } catch (e: Exception) {
        throw e
    }
}