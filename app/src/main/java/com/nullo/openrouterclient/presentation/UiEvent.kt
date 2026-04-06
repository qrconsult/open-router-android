package com.nullo.openrouterclient.presentation

import androidx.annotation.StringRes

sealed class UiEvent {
    data class ShowError(val error: ErrorType) : UiEvent()
    data class ShowMessage(@param:StringRes val messageStringRes: Int) : UiEvent()
    data class ShowErrorDialog(val title: String, val message: String) : UiEvent()
    data object ClearInput : UiEvent()
}
