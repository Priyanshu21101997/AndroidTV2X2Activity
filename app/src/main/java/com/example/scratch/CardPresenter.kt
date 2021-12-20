package com.example.scratch

import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.Presenter
import com.bumptech.glide.Glide
import kotlin.properties.Delegates

class CardPresenter:Presenter() {

    private var sDefaultBackgroundColor: Int by Delegates.notNull()
    private var sSelectedBackgroundColor: Int by Delegates.notNull()

    override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {

        sDefaultBackgroundColor = ContextCompat.getColor(parent.context, R.color.default_background)
        sSelectedBackgroundColor = ContextCompat.getColor(parent.context, R.color.selected_background)

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

    override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {

        val movie = item as Movies

        val cardView = viewHolder.view as ImageCardView

        cardView.titleText = movie.title
            cardView.contentText = movie.desc
            cardView.setMainImageDimensions(313, 176)
            Glide.with(viewHolder.view.context)
                .load(movie.url)
                .centerCrop()
//                .error(mDefaultCardImage)
                .into(cardView.mainImageView)
    }

    override fun onUnbindViewHolder(viewHolder: ViewHolder?) {
        // Free resources for garbage cleaning
        TODO("Not yet implemented")
    }

    private fun updateCardBackgroundColor(view: ImageCardView, selected: Boolean) {
        val color = if (selected) sSelectedBackgroundColor else sDefaultBackgroundColor
        // Both background colors should be set because the view"s background is temporarily visible
        // during animations.
        view.setBackgroundColor(color)
        view.setInfoAreaBackgroundColor(color)
    }


}