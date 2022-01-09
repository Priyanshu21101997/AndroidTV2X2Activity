package com.example.finalProject.presenters

import android.app.PendingIntent.getActivity
import android.content.res.Resources
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import com.example.finalProject.R
import com.example.finalProject.models.Movies
import com.example.finalProject.models.Results
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext
import kotlin.properties.Delegates

class CardPresenter:Presenter() {

    private var sDefaultBackgroundColor: Int by Delegates.notNull()
    private var sSelectedBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {

        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context,
            R.color.selected_background
        )

        val cardView = object : ImageCardView(parent.context) {
            override fun setSelected(selected: Boolean) {
                updateCardBackgroundColor(this, selected)
                super.setSelected(selected)
            }
        }

        cardView.isFocusable = true
        cardView.isFocusableInTouchMode = true
        return Presenter.ViewHolder(cardView)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, item: Any) {

        val movie = item as Results

        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = movie.title
            cardView.contentText = movie.voteAverage.toString()
        cardView.setMainImageDimensions(313, 176)
            Glide.with(viewHolder.view.context)
                .load("https://image.tmdb.org/t/p/w400/"+movie.posterPath)
                .centerCrop()
//                .error(mDefaultCardImage)
                .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        // Free resources for garbage cleaning
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }


}