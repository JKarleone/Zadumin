package com.example.fintechproject.models.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.fintechproject.utils.IGifGallery

class GalleryViewModelFactory(val gallery: IGifGallery) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.getConstructor(IGifGallery::class.java)
            .newInstance(gallery)
    }

}