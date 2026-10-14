package com.dmm.presentation.common

import androidx.annotation.StringRes
import com.dmm.domain.common.DataError
import com.dmm.presentation.R

// Cada erro do domínio tem o seu título e descrição. Usado em todos os ecrãs de erro
@get:StringRes
val DataError.titleRes: Int
    get() = when (this) {
        DataError.NoConnection -> R.string.error_no_connection_title
        DataError.Server -> R.string.error_server_title
        DataError.Unknown -> R.string.error_unknown_title
    }

@get:StringRes
val DataError.descriptionRes: Int
    get() = when (this) {
        DataError.NoConnection -> R.string.error_no_connection_description
        DataError.Server -> R.string.error_server_description
        DataError.Unknown -> R.string.error_unknown_description
    }
