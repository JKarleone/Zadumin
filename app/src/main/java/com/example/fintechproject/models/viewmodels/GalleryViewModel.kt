package com.example.fintechproject.models.viewmodels

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fintechproject.utils.IGifGallery
import com.example.fintechproject.models.Post
import com.example.fintechproject.models.Result
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class GalleryViewModel(private var gallery: IGifGallery) : ViewModel() {

    var categories: List<String> = listOf(
        "latest", "top", "hot"
    )
    var description = MutableLiveData<String>()
    var gifURL = MutableLiveData<String>()
    var canMovePrevious = MutableLiveData<Boolean>()
    var canMoveNext = MutableLiveData<Boolean>()
    var isLoading = MutableLiveData<Boolean>()
    var isError = MutableLiveData<Boolean>()
    var error: Exception? = null

    var categoryIndex: Int by Delegates.observable(0) { _, _, newValue ->
        gallery.changeCategory(categories[newValue])
        error = null
        if (gallery.isLoadedPostsEmpty(categories[newValue]))
            moveNext()
        else
            getCurrent()
    }

    init {
        canMovePrevious.value = false
        canMoveNext.value = false
        isError.value = false
        moveNext()
        Log.d("fintech", "GalleryViewModel init block end: title: ${description.value}, ${gifURL.value}")
    }

    fun movePrevious() {
        val post = gallery.moveToPreviousPost()

        when (post) {
            is Result.Success<Post> -> {
                val newPost = post.data
                description.value = newPost.description
                gifURL.value = newPost.gifUrl
                isError.value = false
            }
            else -> {
                isError.value = true
            }
        }

        canMovePrevious.value = gallery.canMoveToPreviousPost()
        canMoveNext.value = gallery.canMoveToNextPost()
    }

    fun moveNext() {
        isLoading.value = true
        viewModelScope.launch {
            val post = gallery.moveToNextPost()

            handleResults(post)

            isLoading.value = false
        }
    }

    fun getCurrent() {
        val post = gallery.getCurrentPost()

        handleResults(post)
    }

    private fun handleResults(post: Result<Post>){
        when (post) {
            is Result.Success<Post> -> {
                val newPost = post.data
                description.value = newPost.description
                gifURL.value = newPost.gifUrl
                isError.value = false
            }
            is Result.Error -> {
                isError.value = true
                error = post.exception
            }
        }

        canMovePrevious.value = gallery.canMoveToPreviousPost()
        canMoveNext.value = gallery.canMoveToNextPost()
    }

}
