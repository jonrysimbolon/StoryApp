package com.story.widget

import android.appwidget.AppWidgetManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import androidx.core.os.bundleOf
import androidx.lifecycle.Observer
import com.story.R
import com.story.model.StoryModel
import com.story.utils.BitmapLoader

class StackRemoteViewsFactory(
    private val mContext: Context,
    private val stackWidgetViewModel: StackWidgetViewModel,
    private val bitmapLoader: BitmapLoader
) : RemoteViewsService.RemoteViewsFactory {
    private var widgetItems: List<StoryModel>? = null
    private var widgetItemsObserver: Observer<List<StoryModel>>? = null

    override fun onCreate() {
        widgetItemsObserver = Observer { items ->
            widgetItems = items
            val appWidgetManager = AppWidgetManager.getInstance(mContext)
            val appWidgetIds = appWidgetManager.getAppWidgetIds(
                ComponentName(
                    mContext,
                    ImagesBannerWidget::class.java
                )
            )
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.stack_view)
        }
        stackWidgetViewModel.widgetItems.observeForever(widgetItemsObserver!!)
        stackWidgetViewModel.loadWidgetItem()
    }

    override fun onDataSetChanged() {}

    override fun getViewAt(position: Int): RemoteViews {
        val rv = RemoteViews(mContext.packageName, R.layout.widget_item)
        val items = widgetItems
        if (items != null && position < items.size) {
            val item = items[position]
            val bitmap = bitmapLoader.loadBitmapFromUrl(item.photoUrl)
            rv.setImageViewBitmap(R.id.imageView, bitmap)
            val extras = bundleOf(ImagesBannerWidget.EXTRA_ITEM to position)
            val fillInIntent = Intent().putExtras(extras)
            rv.setOnClickFillInIntent(R.id.imageView, fillInIntent)
        }
        return rv
    }

    override fun getCount(): Int = widgetItems?.size ?: 0
    override fun getLoadingView(): RemoteViews? = null
    override fun getViewTypeCount(): Int = 1
    override fun getItemId(position: Int): Long = position.toLong()
    override fun hasStableIds(): Boolean = true

    override fun onDestroy() {
        Handler(Looper.getMainLooper()).post {
            widgetItemsObserver?.let {
                stackWidgetViewModel.widgetItems.removeObserver(it)
            }
        }
    }

}