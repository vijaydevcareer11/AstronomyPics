package com.vmobile.astronomypics.utils

import android.content.Context
import android.util.Log
import android.widget.ImageView
import com.squareup.picasso.Callback
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import com.vmobile.astronomypics.R

object ImageUtils {

    /**
     * This method will load the images from cache
     *
     * @param context
     * @param url
     * @param view
     */
    fun loadImage(context: Context, url: String, view: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_placeholder_planet)
            .error(R.drawable.ic_placeholder_planet)
            .networkPolicy(NetworkPolicy.OFFLINE)
            .into(view, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: Exception) {
                    Log.d("ImageUtils", "Error : " + e.message)
                    retryImageLoading(context, url, view)
                }
            })
    }

    /**
     * This method will download the images from server when does not found in cache
     *
     * @param context
     * @param url
     * @param view
     */
    private fun retryImageLoading(context: Context, url: String, view: ImageView) {
        Picasso.get()
            .load(url)
            .placeholder(R.drawable.ic_placeholder_planet)
            .error(R.drawable.ic_placeholder_planet)
            .fit().into(view, object : Callback {
                override fun onSuccess() {

                }

                override fun onError(e: java.lang.Exception?) {

                }
            })
    }
}