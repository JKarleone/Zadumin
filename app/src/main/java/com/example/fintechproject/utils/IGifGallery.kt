package com.example.fintechproject.utils

import com.example.fintechproject.models.Post
import com.example.fintechproject.models.Result

interface IGifGallery {

    fun changeCategory(toCategory: String)
    fun isLoadedPostsEmpty(category: String): Boolean

    fun getCurrentPost(): Result<Post>

    fun canMoveToPreviousPost(): Boolean
    fun moveToPreviousPost(): Result<Post>

    fun canMoveToNextPost(): Boolean
    suspend fun moveToNextPost(): Result<Post>

}