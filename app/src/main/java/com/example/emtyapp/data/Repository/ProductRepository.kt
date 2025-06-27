package com.example.emtyapp.data.Repository
import com.example.emtyapp.data.Entities.Product
import com.example.emtyapp.data.api.ApiService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ProductRepository @Inject constructor(
    private val apiService: ApiService
) {

    suspend fun getProducts(): Result<List<Product>> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful) {
                response.body()?.let { products ->
                    Result.success(products)
                } ?: Result.failure(Exception("Données vides"))
            } else {
                Result.failure(Exception("Erreur HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductById(id: String): Result<Product> {
        return try {
            val response = apiService.getProductById(id)
            if (response.isSuccessful) {
                response.body()?.let { product ->
                    Result.success(product)
                } ?: Result.failure(Exception("Produit non trouvé"))
            } else {
                Result.failure(Exception("Erreur HTTP: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
