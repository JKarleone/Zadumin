package com.example.fintechproject.utils

import android.util.Log
import com.example.fintechproject.utils.exceptions.ContentException
import com.example.fintechproject.models.CategoryData
import com.example.fintechproject.models.Post
import com.example.fintechproject.models.Result
import com.example.fintechproject.network.IGifGalleryAPI
import java.lang.Exception

class GifGallery(private var loader: IGifGalleryAPI) : IGifGallery {

    private var categories: MutableMap<String, CategoryData> = mutableMapOf(
        "latest" to CategoryData(
            "latest", -1, -1, mutableListOf(), false
        ),
        "hot" to CategoryData(
            "hot", -1, -1, mutableListOf(), false
        ),
        "top" to CategoryData(
            "top", -1, -1, mutableListOf(), false
        )
    )
    private var current: String = "latest"

    override fun changeCategory(toCategory: String) {
        current = toCategory
    }

    override suspend fun getCurrentPost(): Result<Post> {

        val categoryData = categories.getValue(current)
        val currentCategory = categoryData.category

        return if (categoryData.loadedPosts.size != 0) {
            try {
//                Result.Success(categoryData.loadedPosts[categoryData.currentPostIndex])
                Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.currentPostIndex])
            } catch (e: java.lang.IndexOutOfBoundsException) {
//                Result.Success(categoryData.loadedPosts[categoryData.loadedPosts.size - 1])
                Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.loadedPosts.size - 1])
            }
        }
        else
            moveToNextPost()
    }

    override fun canMoveToPreviousPost(): Boolean {
        return categories.getValue(current).currentPostIndex != 0
    }

    override fun moveToPreviousPost(): Result<Post> {

        if (!canMoveToPreviousPost())
            return Result.Error(Exception("No previous post"))

        val categoryData = categories.getValue(current)
        val currentCategory = categoryData.category
        categoryData.currentPostIndex--
        categories[currentCategory] = categoryData

        try {
//            return Result.Success(categoryData.loadedPosts[categoryData.currentPostIndex])
            return Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.currentPostIndex])
        } catch (e: java.lang.IndexOutOfBoundsException) {
//            return Result.Success(categoryData.loadedPosts[categoryData.loadedPosts.size - 1])
            return Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.loadedPosts.size - 1])
        }

    }

    override fun canMoveToNextPost(): Boolean {
        return !categories.getValue(current).endOfList
    }

    override suspend fun moveToNextPost(): Result<Post> {

        if (!canMoveToNextPost())
            return Result.Error(ContentException())

        val categoryData = categories.getValue(current)
        categoryData.currentPostIndex++
        val currentCategory = categoryData.category

        if (categoryData.currentPostIndex == categoryData.loadedPosts.size || categoryData.lastPage == -1) {

            val result = loader.getPage(currentCategory, categoryData.lastPage + 1)

            when(result) {
                is Result.Success<List<Post>> -> {
                    val newPosts = result.data
                    categoryData.loadedPosts.addAll(newPosts)
                    categoryData.lastPage++
                    categories[currentCategory] = categoryData
                    Log.d("fintech", "New posts: $newPosts")
                }
                is Result.Error -> {
                    if (result.exception is ContentException)
                        categoryData.endOfList = true
                    categoryData.currentPostIndex--
                    categories[currentCategory] = categoryData
                    return result
                }
            }
        }

        categories[currentCategory] = categoryData

        if (categoryData.loadedPosts.size == 0)
            return Result.Error(ContentException())
        try {
//            return Result.Success(categoryData.loadedPosts[categoryData.currentPostIndex])
            return Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.currentPostIndex])
        } catch (e: IndexOutOfBoundsException){
//            return Result.Success(categoryData.loadedPosts[categoryData.loadedPosts.size - 1])
            return Result.Success(categories[currentCategory]!!.loadedPosts[categories[currentCategory]!!.loadedPosts.size - 1])
        }
    }
}