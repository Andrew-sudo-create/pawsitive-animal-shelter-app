package com.example.pawsitive_app

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ShelterRepository {
    // Lazy + nullable: if FirebaseApp isn't initialized (missing/misconfigured
    // google-services.json, no plugin applied, etc.), this returns null instead
    // of throwing, so the app never crashes just from constructing the repository.
    private val db: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    suspend fun getNewsPosts(): List<NewsPost> {
        val firestore = db ?: return mockNews
        return try {
            val snapshot = firestore.collection("news_posts").get().await()
            snapshot.documents.mapNotNull { doc ->
                val typeStr = doc.getString("type") ?: return@mapNotNull null
                val type = try { NewsType.valueOf(typeStr) } catch (e: Exception) { NewsType.SPOTLIGHT }
                
                NewsPost(
                    id = doc.id,
                    type = type,
                    title = doc.getString("title") ?: "",
                    description = doc.getString("description") ?: "",
                    imageUrl = doc.getString("imageUrl") ?: "",
                    date = doc.getString("date") ?: "",
                    meta = doc.getString("meta") ?: "",
                    tags = doc.get("tags") as? List<String> ?: emptyList(),
                    amountRaised = doc.getLong("amountRaised")?.toInt() ?: 0,
                    goal = doc.getLong("goal")?.toInt() ?: 0
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to mock data if Firebase isn't configured yet
            mockNews
        }
    }

    suspend fun getDogs(): List<Dog> {
        val firestore = db ?: return mockDogs
        return try {
            val snapshot = firestore.collection("dogs").get().await()
            snapshot.documents.mapNotNull { doc ->
                Dog(
                    id = doc.id,
                    name = doc.getString("name") ?: "",
                    breed = doc.getString("breed") ?: "",
                    age = doc.getString("age") ?: "",
                    sex = doc.getString("sex") ?: "",
                    description = doc.getString("description") ?: "",
                    imageUrls = doc.get("imageUrls") as? List<String> ?: emptyList(),
                    isFavorite = false
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // Fallback to mock data if Firebase isn't configured yet
            mockDogs
        }
    }
}
