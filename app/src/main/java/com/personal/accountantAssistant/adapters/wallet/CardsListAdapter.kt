package com.personal.accountantAssistant.adapters.wallet

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.deleteRecord
import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.data.isDefaultRecord
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.extensions.EMPTY
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.ui.MainActivity
import com.personal.accountantAssistant.ui.wallet.WalletDetailsFragment
import com.personal.accountantAssistant.utils.CalculatorUtils
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.EditableTextsUtils.contains
import com.personal.accountantAssistant.utils.MenuHelper.initializeWalletOptions
import com.personal.accountantAssistant.utils.NumberUtils.roundTo
import java.util.function.Consumer
import java.util.stream.Collectors

@RequiresApi(Build.VERSION_CODES.P)
class CardsListAdapter constructor(
    private val context: Context?,
    private val databaseManager: DatabaseManager?
) : RecyclerView.Adapter<CardsViewHolderData>(), Filterable {

    private var cards: MutableList<CardEntity>? = null

    fun loadCards() = databaseManager?.getSortedCardRecords() as? MutableList<CardEntity>?

    fun setAllCardsRecordsActiveFrom(isActive: Boolean) {
        cards?.forEach(Consumer { card: CardEntity -> setActiveRowFrom(isActive, card) })
    }

    private val layoutToInflate: Int
        get() = R.layout.card_item_list

    val totalValue: Double
        get() = roundTo(
            cards?.stream()
                ?.filter { obj: CardEntity -> obj.isActive == true }
                ?.map { obj: CardEntity -> obj.value.orZero() }
                ?.reduce(Constants.DEFAULT_VALUE, CalculatorUtils.accumulatedDoubleSum)
        )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardsViewHolderData {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutToInflate, null, false)
        return CardsViewHolderData(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onBindViewHolder(viewHolderData: CardsViewHolderData, position: Int) {

        val card = cards?.get(position)

        card?.let {
            //INITIALIZE
            initializeWalletOptions()
            //DETAILS
            viewHolderData.apply {
                tvCompany.text = it.company
                tvName.text = it.name
                tvValue.text = "$ ${roundTo(it.value)}"
                //ACTIONS
                scActive.isChecked = it.isActive.orFalse()
                scActive.setOnClickListener { _ -> setActiveRowFrom(scActive.isChecked, it) }
                ibDelete.setOnClickListener { _ -> deleteRecordFrom(it) }
                itemView.setOnClickListener { _ -> editRecordFrom(it) }
            }
            setRowForegroundFrom(viewHolderData)
        }
    }

    private fun setActiveRowFrom(isActive: Boolean, cardEntity: CardEntity) {
        cardEntity.isActive = isActive
        val updateRecord = databaseManager?.updateCardRecordFrom(cardEntity)
        if (databaseManager?.isNotDefaultRecord(updateRecord) == true) {
            notifyCardsAddedOrChanged(cardEntity)
        }
    }

    private fun editRecordFrom(cardEntity: CardEntity) {
        (context as? MainActivity?)?.supportFragmentManager?.let {
            WalletDetailsFragment.newInstance(cardEntity).apply {
                onSaveActionListener = { notifyCardsAddedOrChanged(cardEntity) }
            }.show(it, String.EMPTY)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun deleteRecordFrom(cardEntity: CardEntity) {
        databaseManager?.deleteRecord(context, cardEntity) { notifyCardRemoved(cardEntity) }
    }

    private fun setRowForegroundFrom(viewHolder: CardsViewHolderData) {
        val isActive = viewHolder.scActive.isChecked
        context?.getColor(if (isActive) R.color.fontColor else R.color.disableFontColor)?.let {
            viewHolder.tvCompany.setTextColor(it)
            viewHolder.tvName.setTextColor(it)
            viewHolder.tvValue.setTextColor(it)
        }
        context?.getColor(if (isActive) R.color.chipColor else R.color.disableChipColor)?.let {
            viewHolder.ivChip.setColorFilter(it, android.graphics.PorterDuff.Mode.MULTIPLY)
        }
    }

    override fun getItemCount(): Int {
        return cards?.size.orZero()
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterStr = charSequence.toString()
                cards = if (filterStr.isEmpty()) {
                    loadCards()
                } else {
                    cards?.stream()?.filter {
                        contains(it.company, filterStr) || contains(it.name, filterStr)
                    }?.collect(Collectors.toList())
                }
                return FilterResults().also { it.values = cards }
            }

            @Suppress("UNCHECKED_CAST")
            override fun publishResults(charSequence: CharSequence, filterResults: FilterResults?) {
                cards = filterResults?.values as MutableList<CardEntity>?
                notifyDataSetChanged()
            }
        }
    }

    fun notifyCardsAddedOrChanged(cardEntity: CardEntity) {
        cards = loadCards()
        val loadedCard = cards?.stream()?.filter {
            it.equalsTo(cardEntity)
        }?.findFirst()?.orElse(cardEntity)
        val loadedCardId = loadedCard?.id?.toLong()
        if (databaseManager?.isDefaultRecord(loadedCardId) == true) {
            loadedCard?.let { cards?.add(it) }
            val position = itemCount - 1
            notifyItemInserted(position)
        } else {
            cards?.let {
                for (position in 0 until itemCount) {
                    val item = it[position]
                    if (loadedCardId?.equals(item.id.toLong()) == true) {
                        it[position] = loadedCard
                        notifyItemChanged(position, loadedCard)
                        break
                    }
                }
            }
        }
        notifyDataSetChanged()
    }

    private fun notifyCardRemoved(card: CardEntity) = cards?.apply {
        val position = indexOf(card)
        val wasRemoved = remove(card)
        if (wasRemoved)
            notifyItemRemoved(position)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun notifyCleanCards() {
        cards = mutableListOf()
        notifyDataSetChanged()
    }

    init {
        cards = loadCards()
    }
}