package com.personal.accountantAssistant.ui.wallet.details

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*

class WalletDetailsFragment : BottomSheetDialogFragment() {

    val cardModel by lazy { arguments?.getParcelableCompat<CardModel>(String.ENTITY) }

    var onEditListener: ((model: CardModel) -> Unit)? = null

    override fun initComponents() {}

    override fun getActionBarTitle() = R.string.wallet_details

    override fun cancel() { dismiss() }

    override fun save() {}

    override fun initObservers() {}

    @Composable
    override fun ScreenContent() {
        WalletDetailsScreen(
            cardModel = cardModel,
            onSave = { model ->
                onEditListener?.invoke(model)
                dismiss()
            },
            onCancel = ::cancel
        )
    }

    companion object {
        fun showDialogFragment(
            model: CardModel,
            onEdit: (model: CardModel) -> Unit,
            manager: FragmentManager
        ) {
            WalletDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
                onEditListener = { onEdit(it) }
            }.show(manager, String.EMPTY)
        }
    }
}