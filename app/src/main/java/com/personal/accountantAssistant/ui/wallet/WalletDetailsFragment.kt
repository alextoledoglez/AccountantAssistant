package com.personal.accountantAssistant.ui.wallet

import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.databinding.ActivityWalletDetailsBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.ENTITY
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.viewBinding

class WalletDetailsFragment : BaseBottomSheetDialogFragment<Nothing>() {

    override val binding by viewBinding(ActivityWalletDetailsBinding::inflate)
    //private val appDatabase: AppDatabase? by inject()

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
        binding.apply {
            model?.update(
                company = etCompany.text,
                name = etName.text,
                password = etPassword.text,
                value = etValue.text,
                isActive = scActive.isChecked
            )
        }
/*        appDatabase?.cardsDao()?.saveDataFrom(context, model) {
            showLongText(context, R.string.record_successfully_save)
            dismiss()
        }*/
    }

    companion object {
        fun newInstance(model: CardModel?) = WalletDetailsFragment().apply {
            arguments = Bundle().apply { putParcelable(String.ENTITY, model) }
        }
    }
}