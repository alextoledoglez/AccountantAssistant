package com.personal.accountantAssistant.domain.models

import androidx.annotation.DimenRes
import com.personal.accountantAssistant.R

data class TextSizeResourcesModel(
    @DimenRes var normal: Int = R.dimen.card_title_text_size,
    @DimenRes var big: Int = R.dimen.card_title_text_big_size,
)