package com.personal.accountantAssistant.ui.wallet

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.InputFilter
import android.text.InputFilter.AllCaps
import android.text.InputType
import android.view.View
import androidx.fragment.app.FragmentManager
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.bases.BottomSheetDialogFragment
import com.personal.accountantAssistant.databinding.FragmentWalletDetailsBinding
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*
import java.util.*

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
            //Available Value
            etAvailableValue.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.availableValue.toString())
            }
            //Limit Value
            etLimitValue.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.limitValue.toString())
            }
            //Password
            etPassword.apply {
                filters = arrayOf<InputFilter>(AllCaps())
                setText(model?.password.orEmpty())
            }
            //Payment date
            etPaymentDate.apply {
                inputType = InputType.TYPE_NULL
                setText(model?.date.toDateStr())
                val dialog = AlertDialogBuilder(context)
                val listener = DatePickerDialog.OnDateSetListener { _, y: Int, m: Int, d: Int ->
                    setText(Calendar.getInstance().also { it[y, m] = d }.time.toDateStr())
                }
                setOnClickListener { dialog.showDatePickerFrom(model?.date, listener) }
                onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus: Boolean ->
                    if (hasFocus) {
                        dialog.showDatePickerFrom(model?.date, listener)
                    }
                }
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
                etCompany.text,
                etName.text,
                etPaymentDate.text,
                etPassword.text,
                etAvailableValue.text,
                etLimitValue.text,
                scActive.isChecked
            )?.let {
                onEditListener?.invoke(it).also { dismiss() }
            }
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