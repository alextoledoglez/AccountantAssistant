package com.personal.accountantAssistant.ui.wallet

import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.recyclerview.widget.ListAdapter
import com.personal.accountantAssistant.databinding.CardItemListBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.containStr
import com.personal.accountantAssistant.extensions.settingFilter
import com.personal.accountantAssistant.extensions.toLayoutInflater
import java.util.function.Predicate

class CardsListAdapter(
    private val onEditCard: (model: CardModel) -> Unit,
    private val onActiveCard: (model: CardModel) -> Unit,
    private val onRemoveCard: (model: CardModel) -> Unit,
) : ListAdapter<CardModel, CardsViewHolder>(CardModel.DIFF_UTIL_CALLBACK), Filterable {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = CardsViewHolder(
        CardItemListBinding.inflate(parent.context.toLayoutInflater(), parent, false),
        onEditCard,
        onActiveCard,
        onRemoveCard
    )

    override fun onBindViewHolder(holder: CardsViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    override fun getFilter(): Filter {
        return this.settingFilter(currentList, ::filter, ::submitList)
    }

    private fun filter(text: String) = Predicate<CardModel> {
        it.limitValue.toString().containStr(text) ||
                it.availableValue.toString().containStr(text) ||
                it.usedValue.toString().containStr(text) ||
                it.password.containStr(text) ||
                it.company.containStr(text) ||
                it.name.containStr(text)
    }
}