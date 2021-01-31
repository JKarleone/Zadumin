package com.example.fintechproject.animations

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.view.View

object FadeAnimator {

    fun fadeOut(view: View, duration: Long = 400) {
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

    fun fadeIn(view: View, duration: Long = 300) {
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