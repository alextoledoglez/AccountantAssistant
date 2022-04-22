package com.personal.accountantAssistant.ui.wallet

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.databinding.FragmentWalletDetailsBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*

class WalletDetailsFragment : BottomSheetDialogFragment<Nothing>() {

    override val binding by viewBinding(FragmentWalletDetailsBinding::inflate)

    var onEditListener: ((model: CardModel) -> Unit)? = null

    override fun initComponents() {
        val model = getCard()
        with(binding) {
            //Title
            tvTitle.setText(R.string.wallet_details)
            //Company
            etCompany.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.company.orEmpty())
            }
            //Name
            etName.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.name.orEmpty())
            }
            //Value
            etValue.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.value.toString())
            }
            //Password
            etPassword.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.password.orEmpty())
            }
            //Value and switch
            scActive.isChecked = model?.isActive.orFalse()
            //Footer
            lytFooter.apply {
                mbCancel.setOnClickListener { dismiss() }
                mbSave.setOnClickListener { saveCard(model) }
            }
        }
        setFullScreen()
    }

    override fun initObservers() {}

    private fun getCard() = arguments?.getParcelable<CardModel>(String.ENTITY)

    private fun saveCard(model: CardModel?) {
        binding.apply {
            val updatedModel = model?.copy(
                company = etCompany.text.toString(),
                name = etName.text.toString(),
                password = etPassword.text.toString(),
                value = etValue.text.toCurrencyBigDecimal(),
                isActive = scActive.isChecked,
            )
            updatedModel?.let { onEditListener?.invoke(updatedModel).also { dismiss() } }
        }
    }

    companion object {
        fun showDialogFragment(
            model: CardModel, onEdit: (model: CardModel) -> Unit, manager: FragmentManager
        ) {
            WalletDetailsFragment().apply {
                arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
                onEditListener = { onEdit(it) }
            }.show(manager, String.EMPTY)
        }
    }
}