package com.personal.accountantAssistant.adapters

import android.util.TypedValue
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.databinding.HomeItemListBinding
import com.personal.accountantAssistant.domain.models.DashboardItemModel
import com.personal.accountantAssistant.domain.models.TextSizeResourcesModel
import com.personal.accountantAssistant.extensions.getCompatColor
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr

class HomeViewHolder(
    private val binding: HomeItemListBinding
) : RecyclerView.ViewHolder(binding.root) {

    private var textSizeResources: TextSizeResourcesModel = TextSizeResourcesModel()
    private var textSize: Int? = null

    fun bind(model: DashboardItemModel) {
        with(binding) {
            textSize = textSizeResources.normal

            ivCardImage.apply {
                setImageResource(model.drawableRes)
                setColorFilter(model.color, android.graphics.PorterDuff.Mode.SRC_IN)
                isVisible = true
            }

            tvCardTitle.apply {
                setTextColor(model.color)
                text = root.context.getString(model.strResource)
                setBackgroundColor(context.getCompatColor(R.color.colorWhite))
                setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize.orZero())
            }

            tvCardSubtitle.apply {
                setTextColor(model.color)
                text = model.value.abs().toCurrencyMaskedStr()
            }
        }
    }
}