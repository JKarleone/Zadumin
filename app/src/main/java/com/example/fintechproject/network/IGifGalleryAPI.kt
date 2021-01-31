package com.example.fintechproject.network

import com.example.fintechproject.models.Post
import com.example.fintechproject.models.Result

interface IGifGalleryAPI {
    // Return URL of gif
    suspend fun getPage(category: String, pageIndex: Int): Result<List<Post>>
}