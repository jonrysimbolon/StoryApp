package com.storyapp.remote.response

import android.graphics.drawable.Drawable

sealed class ResultStatus<out R> private constructor() {
    data class Success<out T>(val data: T) : ResultStatus<T>()
    //data class ErrorResponse(val errorResponse: Response) : ResultStatus<Nothing>()
    data class Error(val error: String) : ResultStatus<Nothing>()
    object Loading : ResultStatus<Nothing>()
}

sealed class ImageState {
    object Loading : ImageState()
    data class Success(val drawable: Drawable) : ImageState()
    object Error : ImageState()
}
