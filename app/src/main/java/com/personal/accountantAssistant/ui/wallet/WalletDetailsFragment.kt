package com.personal.accountantAssistant.ui.wallet

import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import androidx.annotation.RequiresApi
import androidx.viewbinding.ViewBinding
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.BaseBottomSheetDialogFragment
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.viewBinding
import com.personal.accountantAssistant.data.DatabaseManager
import com.personal.accountantAssistant.data.saveDataFrom
import com.personal.accountantAssistant.databinding.ActivityWalletDetailsBinding
import com.personal.accountantAssistant.data.entities.wallet.CardEntity
import com.personal.accountantAssistant.utils.Constants
import com.personal.accountantAssistant.utils.ToastUtils.showLongText
import kotlinx.android.synthetic.main.activity_wallet_details.view.*
import kotlinx.android.synthetic.main.options_footer_bar.view.*
import org.koin.android.ext.android.inject

class WalletDetailsFragment : BaseBottomSheetDialogFragment<Nothing>() {

    override val binding: ViewBinding by viewBinding(ActivityWalletDetailsBinding::inflate)

    val databaseManager: DatabaseManager? by inject()

    lateinit var onSaveActionListener: (card: CardEntity) -> Unit

    @RequiresApi(Build.VERSION_CODES.P)
    override fun initView() {
        initializeViewComponentsFrom(getCard())
        setFullScreen()
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun initializeViewComponentsFrom(card: CardEntity?) {

        binding.root.apply {
            //Title
            tvTitle.setText(R.string.wallet_details)
            etTitle.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(card?.title.orEmpty())
            }
            //Value
            etValue.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(card?.value.orZero().toString())
            }
            //Password
            etPassword.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(card?.password.orZero().toString())
            }
            //Value and switch
            scActive.isChecked = card?.isActive ?: false

            //Footer
            lytFooter.apply {
                mbCancel.setOnClickListener { dismiss() }
                mbSave.setOnClickListener { saveCard(card) }
            }
        }
    }

    private fun getCard() = (arguments?.getSerializable(Constants.ENTITY) as? CardEntity?)

    @RequiresApi(Build.VERSION_CODES.P)
    private fun saveCard(card: CardEntity?) {
        binding.root.apply {
            card?.update(
                title = etTitle.text.toString(),
                password = etPassword.text.toString().toInt(),
                value = etValue.text.toString().toDouble(),
                isActive = scActive.isChecked
            )
        }
        databaseManager?.saveDataFrom(context, card) {
            showLongText(context, R.string.record_successfully_save)
            card?.let { onSaveActionListener.invoke(it) }
            dismiss()
        }
    }

    companion object {
        fun newInstance(card: CardEntity?) = WalletDetailsFragment().apply {
            arguments = Bundle().apply { putSerializable(Constants.ENTITY, card) }
        }
    }
}