package com.jonrysimbolonstory.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.os.Build
import android.widget.RemoteViews
import androidx.core.net.toUri
import com.jonrysimbolonstory.R
import com.jonrysimbolonstory.main.MainActivity

class ImagesBannerWidget : AppWidgetProvider() {

    companion object {

        private const val TOAST_ACTION = "com.jonrysimbolonstory.widget.TOAST_ACTION"
        const val EXTRA_ITEM = "com.jonrysimbolonstory.widget.EXTRA_ITEM"

        private fun updateAppWidget(
            context: Context, appWidgetManager: AppWidgetManager, appWidgetId: Int
        ) {
            val intent = Intent(context, StoryStackWidgetService::class.java).apply {
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
                data = this.toUri(Intent.URI_INTENT_SCHEME).toUri()
            }

            val views = RemoteViews(context.packageName, R.layout.image_banner_widget).apply {
                setRemoteAdapter(R.id.stack_view, intent)
                setEmptyView(R.id.stack_view, R.id.empty_view)
            }

            val toastIntent = Intent(context, ImagesBannerWidget::class.java).apply {
                action = TOAST_ACTION
                putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            }

            PendingIntent.getBroadcast(
                context,
                0,
                toastIntent,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_MUTABLE
                else 0
            ).also {
                views.setPendingIntentTemplate(R.id.stack_view, it)
            }

            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onUpdate(
        context: Context, appWidgetManager: AppWidgetManager, appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        if (intent.action != null) {
            if (intent.action == TOAST_ACTION) {
                Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                }.also {
                    context.startActivity(it)
                }
            }
        }
    }
}

