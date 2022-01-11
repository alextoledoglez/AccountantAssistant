package com.personal.accountantAssistant.ui.wallet.adapters

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.core.extensions.EMPTY
import com.personal.accountantAssistant.core.extensions.orZero
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.deleteRecord
import com.personal.accountantAssistant.data.isDefaultRecord
import com.personal.accountantAssistant.data.isNotDefaultRecord
import com.personal.accountantAssistant.ui.MainActivity
import com.personal.accountantAssistant.ui.wallet.WalletDetailsFragment
import com.personal.accountantAssistant.ui.wallet.entities.CardEntity
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
) : RecyclerView.Adapter<CardsListAdapter.CardViewHolderData>(), Filterable {

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolderData {
        val view = LayoutInflater.from(parent.context)
            .inflate(layoutToInflate, null, false)
        return CardViewHolderData(view)
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    override fun onBindViewHolder(viewHolderData: CardViewHolderData, position: Int) {

        val card = cards?.get(position)
        card?.let {

            //INITIALIZE
            initializeWalletOptions()

            //DETAILS
            viewHolderData.apply {
                title.text = it.title
                value.text = "$ ${roundTo(it.value)}"

                //ACTIONS
                active.isChecked = it.isActive ?: false
                active.setOnClickListener { _ -> setActiveRowFrom(active.isChecked, it) }
                itemView.setOnClickListener { _ -> editRecordFrom(it) }
                deleteItem.setOnClickListener { _ -> deleteRecordFrom(it) }
            }
            setRowForegroundFrom(viewHolderData)
        }
    }

    private fun setActiveRowFrom(isActive: Boolean, card: CardEntity) {
        card.isActive = isActive
        val updateRecord = databaseManager?.updateCardRecordFrom(card)
        if (databaseManager?.isNotDefaultRecord(updateRecord) == true) {
            notifyCardsAddedOrChanged(card)
        }
    }

    private fun editRecordFrom(card: CardEntity) {
        (context as? MainActivity?)?.supportFragmentManager?.let {
            WalletDetailsFragment.newInstance(card).apply {
                onSaveActionListener = { notifyCardsAddedOrChanged(card) }
            }.show(it, String.EMPTY)
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    private fun deleteRecordFrom(card: CardEntity) {
        databaseManager?.deleteRecord(context, card) { notifyCardRemoved(card) }
    }

    private fun setRowForegroundFrom(viewHolderData: CardViewHolderData) {
        val color = if (viewHolderData.active.isChecked)
            context?.getColor(R.color.defaultFontColor)
        else
            context?.getColor(R.color.disableForegroundColor)
        color?.let {
            viewHolderData.title.setTextColor(it)
            viewHolderData.value.setTextColor(it)
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
                    cards?.stream()?.filter { contains(it.title, filterStr) }
                        ?.collect(Collectors.toList())
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

    class CardViewHolderData(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var title: TextView = itemView.findViewById(R.id.title)
        var password: TextView = itemView.findViewById(R.id.password)
        var value: TextView = itemView.findViewById(R.id.value)
        var active: androidx.appcompat.widget.SwitchCompat = itemView.findViewById(R.id.scActive)
        var deleteItem: ImageButton = itemView.findViewById(R.id.ibDelete)
    }

    fun notifyCardsAddedOrChanged(card: CardEntity) {
        cards = loadCards()
        val loadedCard = cards?.stream()?.findFirst()?.orElse(card)
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

    init {
        cards = loadCards()
    }
}