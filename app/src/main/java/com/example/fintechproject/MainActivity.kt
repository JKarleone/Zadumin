package com.example.fintechproject

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.graphics.drawable.Drawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.fintechproject.databinding.ActivityMainBinding
import com.example.fintechproject.models.viewmodels.GalleryViewModel
import com.example.fintechproject.models.viewmodels.GalleryViewModelFactory
import com.example.fintechproject.network.GifGalleryAPI
import com.example.fintechproject.utils.GifGallery
import com.example.fintechproject.utils.exceptions.ContentException
import kotlinx.coroutines.*
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity() {

    private val gifGalleryAPI = GifGalleryAPI()
    private val gifGallery = GifGallery(gifGalleryAPI)

    private val galleryViewModelFactory = GalleryViewModelFactory(gifGallery)
    private val galleryViewModel = galleryViewModelFactory.create(GalleryViewModel::class.java)

    private var previousCheckedButtonId = R.id.latestButton

    private lateinit var drawablePlaceholder: CircularProgressDrawable

    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        supportActionBar?.hide()

        setObservers()
    }

    private fun setObservers() {

        setDrawablePlaceholder()

        galleryViewModel.gifURL.observe(this) {
            tryToLoadGif(it)
        }

        galleryViewModel.description.observe(this) {
            if (it != null)
                binding.gifDescription.text = it
        }

        galleryViewModel.canMoveNext.observe(this) {
            if (it != null)
                binding.nextButton.isEnabled = it
        }

        galleryViewModel.canMovePrevious.observe(this) {
            if (it != null)
                binding.previousButton.isEnabled = it
        }

        galleryViewModel.isLoading.observe(this) {
            if (it != null)
                binding.nextButton.isEnabled = !it
        }

        galleryViewModel.isError.observe(this) {
            if (it) {
                //                makeViewInvisible(binding.cardLayout)
                binding.cardLayout.visibility = View.INVISIBLE
                coroutineScope.launch {
                    delay(300)
                    turnOffProgressBar()

                    makeViewVisible(binding.errorLayout)
//                binding.errorLayout.visibility = View.VISIBLE
                }
                if (galleryViewModel.error is ContentException) {
                    coroutineScope.launch {
                        delay(300)
                        binding.repeatConnectionButton.visibility = View.INVISIBLE
                        binding.errorMessage.text = getString(R.string.contentErrorMessage)
                    }
                }
                binding.nextButton.visibility = View.INVISIBLE
                binding.previousButton.visibility = View.INVISIBLE
            } else {
                resetCardAndErrorLayout()
            }
        }

    }

    private fun resetCardAndErrorLayout() {
//        makeViewVisible(binding.cardLayout)
        binding.cardLayout.visibility = View.VISIBLE
//        makeViewInvisible(binding.errorLayout)
        binding.errorLayout.visibility = View.INVISIBLE
        binding.nextButton.visibility = View.VISIBLE
        binding.previousButton.visibility = View.VISIBLE
        binding.repeatConnectionButton.visibility = View.VISIBLE
        binding.errorMessage.text = getString(R.string.internetErrorMessage)
    }

    private fun setDrawablePlaceholder() {
        drawablePlaceholder = CircularProgressDrawable(this)
        drawablePlaceholder.apply {
            strokeWidth = 10f
            centerRadius = 50f
        }
        drawablePlaceholder.start()
    }

    private fun tryToLoadGif(url: String) {

        makeViewVisible(binding.gifLayout)
        makeViewInvisible(binding.gifErrorInclude.gifErrorLayout)

        Glide.with(this)
                .load(url)
                .placeholder(drawablePlaceholder)
                .listener(object : RequestListener<Drawable> {

                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                        coroutineScope.launch {
                            delay(500)
                            makeViewInvisible(binding.gifLayout)
                            makeViewVisible(binding.gifErrorInclude.gifErrorLayout)
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        return false
                    }
                })
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.gifImage)

    }

    fun onNextButtonClickHandler(view: View) {
        galleryViewModel.moveNext()
    }

    fun onPreviousButtonClickHandler(view: View) {
        galleryViewModel.movePrevious()
    }

    fun onRadioButtonClickHandler(view: View) {

        turnOnProgressBar()

        if (previousCheckedButtonId != view.id)
            findViewById<RadioButton>(previousCheckedButtonId).isChecked = false

        makeViewInvisible(binding.errorLayout)

        when(view.id) {
            R.id.latestButton -> {
                galleryViewModel.categoryIndex = 0
            }
            R.id.topButton -> {
                galleryViewModel.categoryIndex = 1
            }
            R.id.hotButton -> {
                galleryViewModel.categoryIndex = 2
            }
        }

        previousCheckedButtonId = view.id

    }

    fun onRepeatConnectionButtonClickHandler(view: View) {
        turnOnProgressBar()
        makeViewInvisible(binding.errorLayout, 200)
        galleryViewModel.moveNext()
    }

    fun onReDownloadGifButtonClickHandler(view: View) {
        tryToLoadGif(galleryViewModel.gifURL.value.toString())
    }

    private fun turnOnProgressBar() {
        binding.progressLayout.visibility = View.VISIBLE
    }

    private fun turnOffProgressBar() {
        binding.progressLayout.visibility = View.INVISIBLE
    }

    private fun makeViewInvisible(view: View, duration: Long = 400) {
        view.animate()
                .alpha(0.0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.INVISIBLE
                    }
                })
    }

    private fun makeViewVisible(view: View, duration: Long = 300) {
        view.animate()
                .alpha(1.0f)
                .setDuration(duration)
                .setListener(object : AnimatorListenerAdapter() {
                    override fun onAnimationEnd(animation: Animator?) {
                        super.onAnimationEnd(animation)
                        view.visibility = View.VISIBLE
                    }
                })
    }

}