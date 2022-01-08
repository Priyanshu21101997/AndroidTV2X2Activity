package com.example.scratch

import java.util.Collections
import java.util.Timer
import java.util.TimerTask

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import androidx.leanback.app.BackgroundManager
import androidx.leanback.app.BrowseSupportFragment
import androidx.leanback.widget.ArrayObjectAdapter
import androidx.leanback.widget.HeaderItem
import androidx.leanback.widget.ImageCardView
import androidx.leanback.widget.ListRow
import androidx.leanback.widget.ListRowPresenter
import androidx.leanback.widget.OnItemViewClickedListener
import androidx.leanback.widget.OnItemViewSelectedListener
import androidx.leanback.widget.Presenter
import androidx.leanback.widget.Row
import androidx.leanback.widget.RowPresenter
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast

import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition

/**
 * Loads a grid of cards with movies to browse.
 */
class MainFragment : BrowseSupportFragment() {

    private val mHandler = Handler()
    private lateinit var mBackgroundManager: BackgroundManager
    private var mDefaultBackground: Drawable? = null
    private lateinit var mMetrics: DisplayMetrics
    private var mBackgroundTimer: Timer? = null
    private var mBackgroundUri: String? = null
    private lateinit var rowsAdapter:ArrayObjectAdapter

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        prepareBackgroundManager()

        setupUIElements()

        loadRows()

        setupEventListeners()
    }

    override fun onDestroy() {
        super.onDestroy()
        mBackgroundTimer?.cancel()
    }

    private fun prepareBackgroundManager() {

        mBackgroundManager = BackgroundManager.getInstance(activity)
        mBackgroundManager.attach(activity!!.window)
        mDefaultBackground = ContextCompat.getDrawable(activity!!, R.drawable.default_background)
        mMetrics = DisplayMetrics()
        activity!!.windowManager.defaultDisplay.getMetrics(mMetrics)
    }

    private fun setupUIElements() {
        title = "My Custom Android TV App"
        // over title
        headersState = BrowseSupportFragment.HEADERS_ENABLED
        isHeadersTransitionOnBackEnabled = true

        // set fastLane (or headers) background color
        brandColor = ContextCompat.getColor(activity!!, R.color.fastlane_background)
        // set search icon color
        searchAffordanceColor = ContextCompat.getColor(activity!!, R.color.search_opaque)
    }

    private fun loadRows() {
//        val tag:String? = "https://img.freepik.com/free-vector/shining-circle-purple-lighting-isolated-dark-background_1441-2396.jpg?size=626&ext=jpg"

        rowsAdapter = ArrayObjectAdapter(ListRowPresenter())
        Handler().postDelayed({
            createRows()
            startEntranceTransition()
        }, 500)


    }

    private fun createRows(){

        val list = listOf<Movies>(Movies("https://images.unsplash.com/photo-1639862567638-57d4bfb03861?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&ixid=MnwyODIwNjJ8MHwxfGFsbHwzfHx8fHx8Mnx8MTYzOTk3NzEwOA&ixlib=rb-1.2.1&q=80&w=200","Good Movie","Gibrish"),Movies("https://img.freepik.com/free-vector/shining-circle-purple-lighting-isolated-dark-background_1441-2396.jpg?size=626&ext=jpg","Bad Movie","HMMMMM"))
        val cardPresenter = CardPresenter()

        for (i in 0 until 2) {

            val listRowAdapter = ArrayObjectAdapter(cardPresenter)
            for (j in 0 until 2) {
                listRowAdapter.add(list[j % 2])
            }
            val header = HeaderItem(i.toLong(), "List Item $i")
            rowsAdapter.add(ListRow(header, listRowAdapter))
        }


        adapter = rowsAdapter
    }


    private fun setupEventListeners() {
        setOnSearchClickedListener {
            Toast.makeText(activity!!, "Implement your own in-app search", Toast.LENGTH_LONG)
                .show()
        }
//
        onItemViewClickedListener = ItemViewClickedListener()
        onItemViewSelectedListener = ItemViewSelectedListener()
    }




    private inner class ItemViewClickedListener : OnItemViewClickedListener {
        override fun onItemClicked(
            itemViewHolder: Presenter.ViewHolder,
            item: Any,
            rowViewHolder: RowPresenter.ViewHolder,
            row: Row
        ) {

            if (item is Movies) {
                val intent = Intent(activity!!, DetailsActivity::class.java)
                intent.putExtra(DetailsActivity.MOVIE, item)

                val bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(
                    activity!!,
                    (itemViewHolder.view as ImageCardView).mainImageView,
                    DetailsActivity.SHARED_ELEMENT_NAME
                )
                    .toBundle()
                startActivity(intent, bundle)
            }
        }
    }

    private inner class ItemViewSelectedListener : OnItemViewSelectedListener {
        override fun onItemSelected(
            itemViewHolder: Presenter.ViewHolder?, item: Any?,
            rowViewHolder: RowPresenter.ViewHolder, row: Row
        ) {
            if (item is Movies) {
                mBackgroundUri = item.url
                updateBackground(mBackgroundUri)
            }
        }
    }

    private fun updateBackground(uri: String?) {
        val width = mMetrics.widthPixels
        val height = mMetrics.heightPixels
        Glide.with(activity!!)
            .load(uri)
            .centerCrop()
            .error(mDefaultBackground)
            .into<SimpleTarget<Drawable>>(
                object : SimpleTarget<Drawable>(width, height) {
                    override fun onResourceReady(
                        drawable: Drawable,
                        transition: Transition<in Drawable>?
                    ) {
                        mBackgroundManager.drawable = drawable
                    }
                })
        mBackgroundTimer?.cancel()
    }

//    private fun startBackgroundTimer() {
//        mBackgroundTimer?.cancel()
//        mBackgroundTimer = Timer()
//        mBackgroundTimer?.schedule(UpdateBackgroundTask(), BACKGROUND_UPDATE_DELAY.toLong())
//    }

//    private inner class UpdateBackgroundTask : TimerTask() {
//
//        override fun run() {
//            mHandler.post { updateBackground(mBackgroundUri) }
//        }
//    }

//    private inner class GridItemPresenter : Presenter() {
//        override fun onCreateViewHolder(parent: ViewGroup): Presenter.ViewHolder {
//            val view = TextView(parent.context)
//            view.layoutParams = ViewGroup.LayoutParams(GRID_ITEM_WIDTH, GRID_ITEM_HEIGHT)
//            view.isFocusable = true
//            view.isFocusableInTouchMode = true
//            view.setBackgroundColor(ContextCompat.getColor(activity!!, R.color.default_background))
//            view.setTextColor(Color.WHITE)
//            view.gravity = Gravity.CENTER
//            return Presenter.ViewHolder(view)
//        }
//
//        override fun onBindViewHolder(viewHolder: Presenter.ViewHolder, item: Any) {
//            (viewHolder.view as TextView).text = item as String
//        }
//
//        override fun onUnbindViewHolder(viewHolder: Presenter.ViewHolder) {}
//    }




}