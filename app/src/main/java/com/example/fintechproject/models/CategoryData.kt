package com.example.fintechproject.models

data class CategoryData(
    val category: String,
    var lastPage: Int,
    var currentPostIndex: Int,
    var loadedPosts: MutableList<Post>,
    var endOfList: Boolean
)