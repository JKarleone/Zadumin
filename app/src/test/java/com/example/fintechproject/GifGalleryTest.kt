package com.example.fintechproject

import com.example.fintechproject.models.Result
import com.example.fintechproject.network.GifGalleryAPI
import com.example.fintechproject.utils.GifGallery
import com.example.fintechproject.utils.exceptions.ContentException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

class GifGalleryTest {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val gifGalleryAPI = GifGalleryAPI()

    @Test
    fun check_initial_latest_post() {
        val gifGallery = GifGallery(gifGalleryAPI)

        ioScope.launch {
            val post = gifGallery.moveToNextPost()

            when (post) {
                is Result.Success -> {
                    assert(post.data.id.toInt() == 17075)
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun check_initial_top_post() {
        val gifGallery = GifGallery(gifGalleryAPI)
        gifGallery.changeCategory("top")

        ioScope.launch {
            val post = gifGallery.getCurrentPost()

            when (post) {
                is Result.Success -> {
                    assert(post.data.id.toInt() == 996)
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun check_initial_hot_post() {
        val gifGallery = GifGallery(gifGalleryAPI)
        gifGallery.changeCategory("hot")

        ioScope.launch {
            val post = gifGallery.getCurrentPost()

            when (post) {
                is Result.Error -> {
                    assert(post.exception is ContentException)
                }
                else -> assert(false)
            }
        }
    }
}