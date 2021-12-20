package com.example.scratch


import androidx.leanback.widget.AbstractDetailsDescriptionPresenter

class DetailsDescriptionPresenter : AbstractDetailsDescriptionPresenter() {

    override fun onBindDescription(
        viewHolder: AbstractDetailsDescriptionPresenter.ViewHolder,
        item: Any
    ) {
        val movie = item as Movies

        viewHolder.title.text = movie.title
        viewHolder.subtitle.text = movie.desc
        viewHolder.body.text = movie.desc
    }
}