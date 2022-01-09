package com.example.finalProject

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.leanback.app.DetailsSupportFragment
import androidx.leanback.app.DetailsSupportFragmentBackgroundController
import androidx.leanback.widget.*
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.finalProject.database.MoviesEntity
import com.example.finalProject.models.Results
import com.example.finalProject.models.ViewModel
import com.example.finalProject.presenters.DetailsDescriptionPresenter


/**
 * A wrapper fragment for leanback details screens.
 * It shows a detailed view of video and its metadata plus related videos.
 */
class VideoDetailsFragment : DetailsSupportFragment() {

    private lateinit var mSelectedMovie: Results

    private lateinit var mDetailsBackground: DetailsSupportFragmentBackgroundController
    private lateinit var mPresenterSelector: ClassPresenterSelector
    private lateinit var mAdapter: ArrayObjectAdapter
    private lateinit var sharedPreferences: SharedPreferences
    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = this.requireActivity().getSharedPreferences(PREF_NAME, MODE)

        mDetailsBackground = DetailsSupportFragmentBackgroundController(this)

        mSelectedMovie =activity!!.intent.getSerializableExtra(DetailsActivity.MOVIE) as Results
        if (mSelectedMovie != null) {
            Log.d("Yeh","$mSelectedMovie")
            mPresenterSelector = ClassPresenterSelector()
            mAdapter = ArrayObjectAdapter(mPresenterSelector)
            setupDetailsOverviewRow()
            setupDetailsOverviewRowPresenter()

            adapter = mAdapter

            initializeBackground(mSelectedMovie)
            Log.d("Helloworld","hi")

//            onItemViewClickedListener = ItemViewClickedListener()
        } else {
            val intent = Intent(activity!!, MainActivity::class.java)
            startActivity(intent)
        }
    }
//
    private fun initializeBackground(movie: Results?) {
        mDetailsBackground.enableParallax()
        Glide.with(requireActivity())
            .asBitmap()
            .centerCrop()
            .error(R.drawable.default_background)
            .load("https://image.tmdb.org/t/p/w400/"+movie?.posterPath)
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
//
    private fun setupDetailsOverviewRow() {
    val row = DetailsOverviewRow(mSelectedMovie)
    row.imageDrawable = ContextCompat.getDrawable(activity!!, R.drawable.default_background)
    val width = convertDpToPixel(activity!!, DETAIL_THUMB_WIDTH)
    val height = convertDpToPixel(activity!!, DETAIL_THUMB_HEIGHT)
    Glide.with(activity!!)
        .load("https://image.tmdb.org/t/p/w400/" + mSelectedMovie?.posterPath)
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


    val actionAdapter = ArrayObjectAdapter()

    actionAdapter.add(
        Action(
            ADD_TO_FAVOURITES,
            resources.getString(R.string.watch_trailer_1),
            resources.getString(R.string.watch_trailer_2)
        )
    )
    actionAdapter.add(
        Action(
            ACTION_RENT,
            resources.getString(R.string.rent_1),
            resources.getString(R.string.rent_2)
        )
    )
    actionAdapter.add(
        Action(
            ACTION_BUY,
            resources.getString(R.string.buy_1),
            resources.getString(R.string.buy_2)
        )
    )
    row.actionsAdapter = actionAdapter

    mAdapter.add(row)

}

private fun setupDetailsOverviewRowPresenter() {
    // Set detail background.
    val detailsPresenter = FullWidthDetailsOverviewRowPresenter(DetailsDescriptionPresenter())
    detailsPresenter.backgroundColor =
        ContextCompat.getColor(activity!!, R.color.selected_background)

    detailsPresenter.isParticipatingEntranceTransition = true

    detailsPresenter.onActionClickedListener = OnActionClickedListener { action ->
        if (action.id == ADD_TO_FAVOURITES) {
            var mViewModel = ViewModelProvider(this)[ViewModel::class.java]
            var isPresent = sharedPreferences.getInt(mSelectedMovie.id.toString(),0)
            if(isPresent == 0) {
                mViewModel.addMovieToFavourites(
                    MoviesEntity(
                        id = mSelectedMovie.id,
                        result = mSelectedMovie
                    )
                )
                sharedPreferences.edit().putInt(mSelectedMovie.id.toString(), 1).apply()
                Toast.makeText(
                    requireActivity(),
                    "${mSelectedMovie?.title} successfully added to favourites}",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else{
                Toast.makeText(requireActivity(),"Movie already added to Favourites",Toast.LENGTH_LONG).show()
            }
        } else
            Toast.makeText(requireActivity(), action.toString(), Toast.LENGTH_SHORT).show()
}

        mPresenterSelector.addClassPresenter(DetailsOverviewRow::class.java, detailsPresenter)
//
}
    //
//
    private fun convertDpToPixel(context: Context, dp: Int): Int {
        val density = context.applicationContext.resources.displayMetrics.density
        return Math.round(dp.toFloat() * density)
    }

    companion object {
        private val TAG = "VideoDetailsFragment"

        private val ADD_TO_FAVOURITES = 1L
        private val ACTION_RENT = 2L
        private val ACTION_BUY = 3L

        private val DETAIL_THUMB_WIDTH = 274
        private val DETAIL_THUMB_HEIGHT = 274

        private val NUM_COLS = 20
        private var MODE = MODE_PRIVATE
        private val PREF_NAME = "final_project"


    }

}
