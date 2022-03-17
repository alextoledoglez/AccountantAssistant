package com.personal.accountantAssistant.ui.wallet

import android.content.DialogInterface
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.databinding.FragmentWalletDetailsBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*

class WalletDetailsFragment : BaseBottomSheetDialogFragment<WalletDetailsViewModel>() {

    override val binding by viewBinding(FragmentWalletDetailsBinding::inflate)

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
                setText(model?.value.orZero().toString())
            }
            //Password
            etPassword.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.password.orZero().toString())
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
        model?.let {
            binding.apply {
                it.update(
                    company = etCompany.text,
                    name = etName.text,
                    password = etPassword.text,
                    value = etValue.text,
                    isActive = scActive.isChecked
                )
                viewModel.saveCard(it)
                dismiss()
            }
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        context?.showToastLongText(
            if (viewModel.isSaved.value.orFalse())
                R.string.record_successfully_save
            else
                R.string.error_saving_your_data
        )
    }

    companion object {
        fun newInstance(model: CardModel?) = WalletDetailsFragment().apply {
            arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
        }
    }
}