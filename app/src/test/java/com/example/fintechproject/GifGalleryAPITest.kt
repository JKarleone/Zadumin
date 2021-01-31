package com.example.fintechproject

import com.example.fintechproject.models.Result
import com.example.fintechproject.network.GifGalleryAPI
import com.example.fintechproject.utils.exceptions.ContentException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.junit.Test

class GifGalleryAPITest {

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private val gifGalleryAPI = GifGalleryAPI()

    @Test
    fun right_data() {
        ioScope.launch {
            val page = gifGalleryAPI.getPage("latest", 0)

            when(page) {
                is Result.Success -> {
                    assert(page.data.size == 5)
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun wrong_category_name() {
        ioScope.launch {
            val page = gifGalleryAPI.getPage("hello_world", 0)

            when (page) {
                is Result.Error -> {
                    assert(page.exception is ContentException)
                }
                else -> assert(false)
            }
        }
    }

    @Test
    fun page_not_exist() {
        ioScope.launch {
            val page = gifGalleryAPI.getPage("hello_world", 15000)

            when (page) {
                is Result.Error -> {
                    assert(page.exception is ContentException)
                }
                else -> assert(false)
            }
        }
    }
}