package com.example.scratch

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.graphics.drawable.Drawable
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.Action
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.ClassPresenterSelector
import androidx.leanback.widget.DetailsOverviewRow
import androidx.leanback.widget.FullWidthDetailsOverviewRowPresenter
import androidx.leanback.widget.FullWidthDetailsOverviewSharedElementHelper
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnActionClickedListener
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

import java.util.Collections

/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class VideoDetailsFragment : DetailsSupportFragment() {

    private var mSelectedMovie: Movies? = null

    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

        mSelectedMovie = activity!!.intent.getSerializableExtra(DetailsActivity.MOVIE) as Movies
        if (mSelectedMovie != null) {
            Log.d("Yeh","$mSelectedMovie")
            mPresenterSelector = ClassPresenterSelector()
            mAdapter = ArrayObjectAdapter(mPresenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()
//            setupRelatedMovieListRow()
            adapter = mAdapter
            initializeBackground(mSelectedMovie)
//            onItemViewClickedListener = ItemViewClickedListener()
        } else {
            val intent = Intent(activity!!, MainActivity::class.java)
            startActivity(intent)
        }
    }

    private fun initializeBackground(movie: Movies?) {
        mDetailsBackground.enableParallax()
        Glide.with(activity!!)
            .asBitmap()
            .centerCrop()
            .error(R.drawable.default_background)
            .load(movie?.url)
            .into<SimpleTarget<Bitmap>>(object : SimpleTarget<Bitmap>() {
                override fun onResourceReady(
                    bitmap: Bitmap,
                    transition: Transition<in Bitmap>?
                ) {
                    mDetailsBackground.coverBitmap = bitmap
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })
    }

    private fun setupDetailsOverviewRow() {
        val row = DetailsOverviewRow(mSelectedMovie)
        row.imageDrawable = ContextCompat.getDrawable(activity!!, R.drawable.default_background)
        val width = convertDpToPixel(activity!!, DETAIL_THUMB_WIDTH)
        val height = convertDpToPixel(activity!!, DETAIL_THUMB_HEIGHT)
        Glide.with(activity!!)
            .load(mSelectedMovie?.url)
            .centerCrop()
            .error(R.drawable.default_background)
            .into<SimpleTarget<Drawable>>(object : SimpleTarget<Drawable>(width, height) {
                override fun onResourceReady(
                    drawable: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    row.imageDrawable = drawable
                    mAdapter.notifyArrayItemRangeChanged(0, mAdapter.size())
                }
            })

        mAdapter.add(row)
    }


    private fun setupDetailsOverviewRowPresenter() {
        // Set detail background.
        val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
        detailsPresenter.backgroundColor =
            ContextCompat.getColor(activity!!, R.color.selected_background)

        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
    }


    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    companion object {
        private val TAG = "VideoDetailsFragment"

        private val DETAIL_THUMB_WIDTH = 274
        private val DETAIL_THUMB_HEIGHT = 274

        private val NUM_COLS = 2
    }
}
