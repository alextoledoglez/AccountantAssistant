package com.personal.accountantAssistant.adapters

import android.util.TypedValue
import android.view.View
import androidx.annotation.ColorRes
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.TextSizeResourcesModel
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr

class HomeViewHolderData(
    private val binding: HomeItemListBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var textSizeResources: TextSizeResourcesModel = TextSizeResourcesModel()
    private var cardTitle: String? = String.EMPTY
    private var hideImageView: Boolean? = false
    private var imageResource: Int? = null
    private var textSize: Int? = null

    @ColorRes
    private var fontColorResource = R.color.colorBlack

    @ColorRes
    private var colorResource = R.color.colorWhite

    fun bind(model: DashboardItemModel) {
        with(binding) {
            hideImageView = false
            cardTitle = String.EMPTY
            imageResource = model.drawableRes
            textSize = textSizeResources.normal
            model.colorResource?.let { fontColorResource = it }
            cardTitle = model.strResource?.let { binding.root.context.getString(it) }

            ivCardImage.apply {
                imageResource?.let { setImageResource(it) }
                setColorFilter(
                    context.getColor(fontColorResource),
                    android.graphics.PorterDuff.Mode.SRC_IN
                )
                visibility = if (hideImageView == true) View.GONE else View.VISIBLE
            }

            tvCardTitle.apply {
                text = cardTitle.orEmpty()
                setTextColor(context.getColor(fontColorResource))
                setBackgroundColor(context.getColor(colorResource))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.orZero())
            }

            tvCardSubtitle.apply {
                text = model.value?.abs().toCurrencyMaskedStr()
                setTextColor(context.getColor(fontColorResource))
            }
        }
    }
}