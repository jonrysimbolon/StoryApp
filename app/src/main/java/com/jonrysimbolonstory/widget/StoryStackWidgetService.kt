package com.jonrysimbolonstory.widget

import android.content.Intent
import android.widget.RemoteViewsService
import org.koin.android.ext.android.inject

class StoryStackWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        val stackRemoteViewsFactory: StackRemoteViewsFactory by inject()
        return stackRemoteViewsFactory
    }
}