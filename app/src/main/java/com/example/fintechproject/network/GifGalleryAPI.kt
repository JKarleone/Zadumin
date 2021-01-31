package com.example.fintechproject.network

import com.example.fintechproject.utils.exceptions.ContentException
import com.example.fintechproject.utils.exceptions.InternetException
import com.example.fintechproject.models.Post
import com.example.fintechproject.models.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject
import java.net.URL

class GifGalleryAPI: IGifGalleryAPI {

    override suspend fun getPage(category: String, pageIndex: Int): Result<List<Post>> =
        withContext(Dispatchers.IO) {
            try {
                val url = "https://developerslife.ru/${category}/${pageIndex}?json=true"
                val json = URL(url).readText()

                parseNewPage(json)
            } catch (exception: Exception) {
                Result.Error(InternetException())
            }
        }

    private fun parseNewPage(json: String): Result<List<Post>> {

        try {
            val postsJson = JSONObject(json).getJSONArray("result")
            var posts = mutableListOf<Post>()

            for (i in 0 until postsJson.length()) {
                val jsonPost = postsJson.getJSONObject(i)
                val newPost = Post(
                    jsonPost.getInt("id").toString(),
                    jsonPost.getString("description"),
                    jsonPost.getString("gifURL")
                )
                posts.add(newPost)
            }

            return Result.Success(posts)

        } catch (exception: JSONException) {
            return Result.Error(ContentException())
        }
    }

}