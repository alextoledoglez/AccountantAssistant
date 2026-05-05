package com.personal.accountantAssistant.ui.expenses

import android.app.DatePickerDialog
import android.text.SpannableStringBuilder
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.orFalse
import com.personal.accountantAssistant.extensions.orZero
import com.personal.accountantAssistant.extensions.setupNumberPickerFrom
import com.personal.accountantAssistant.extensions.showDatePickerFrom
import com.personal.accountantAssistant.extensions.toCurrencyMaskedStr
import com.personal.accountantAssistant.extensions.toDateStr
import com.personal.accountantAssistant.extensions.toRawCurrencyDigits
import com.personal.accountantAssistant.ui.common.ClickableReadOnlyField
import com.personal.accountantAssistant.ui.common.CurrencyTextField
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.theme.extendedColors
import java.util.Calendar

@Composable
fun ExpenseDetailsScreen(
    expenseModel: ExpenseModel?,
    onSave: (ExpenseModel) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var name by rememberSaveable { mutableStateOf(expenseModel?.name.orEmpty()) }
    var nameError by rememberSaveable { mutableStateOf(false) }
    var quantity by rememberSaveable {
        mutableIntStateOf(
            AlertDialogBuilder.toCurrentOrMinValue(
                expenseModel?.quantity.orZero()
            )
        )
    }
    var dateStr by rememberSaveable { mutableStateOf(expenseModel?.date.toDateStr()) }
    var valueRawDigits by rememberSaveable { mutableStateOf(expenseModel?.unitaryValue.toRawCurrencyDigits()) }
    var isActive by rememberSaveable { mutableStateOf(expenseModel?.isActive.orFalse()) }

    val isBillType = isBill(expenseModel?.type)

    val titleResId = when (expenseModel?.type) {
        ExpensesType.BUY -> R.string.buys_details
        ExpensesType.BILL -> R.string.bills_details
        else -> R.string.app_name
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.spacingMd)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(Dimens.buttonHeight),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primary,
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(titleResId).uppercase(),
                        color = MaterialTheme.colorScheme.onPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = Dimens.textLg,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it.uppercase()
                if (nameError && it.isNotBlank()) nameError = false
            },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = nameError,
            supportingText = if (nameError) {
                { Text(stringResource(R.string.field_required)) }
            } else null
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        ClickableReadOnlyField(
            value = AlertDialogBuilder.toCurrentOrMinValue(quantity).toString(),
            label = stringResource(R.string.quantity),
            onClick = {
                AlertDialogBuilder(context)
                    .setupNumberPickerFrom(quantity) { _, _, value ->
                        quantity = AlertDialogBuilder.toCurrentOrMinValue(value)
                    }
                    .create()
                    .show()
            }
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        CurrencyTextField(
            rawDigits = valueRawDigits,
            onValueChange = { valueRawDigits = it },
            label = stringResource(R.string.value),
            modifier = Modifier.fillMaxWidth()
        )

        if (isBillType) {
            Spacer(modifier = Modifier.height(Dimens.spacingSm))

            ClickableReadOnlyField(
                value = dateStr,
                label = stringResource(R.string.date),
                onClick = {
                    val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                        dateStr = Calendar.getInstance().also { it[y, m] = d }.time.toDateStr()
                    }
                    AlertDialogBuilder(context).showDatePickerFrom(expenseModel?.date, listener)
                }
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.active),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(end = Dimens.spacingSm)
            )
            Switch(
                checked = isActive,
                onCheckedChange = { isActive = it },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = MaterialTheme.extendedColors.switchCheckedThumbColor,
                    checkedTrackColor = MaterialTheme.extendedColors.switchCheckedTrackColor
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = Dimens.spacingSm)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.buttonHeight)
                    .padding(end = Dimens.spacingSm),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) { Text(stringResource(R.string.cancel), color = MaterialTheme.colorScheme.onError) }

            Button(
                onClick = {
                    if (name.isBlank()) {
                        nameError = true
                        return@Button
                    }
                    expenseModel?.update(
                        SpannableStringBuilder(name),
                        SpannableStringBuilder(quantity.toString()),
                        SpannableStringBuilder(dateStr),
                        SpannableStringBuilder(valueRawDigits.toCurrencyMaskedStr()),
                        isActive
                    )?.let { onSave(it) }
                },
                modifier = Modifier
                    .weight(1f)
                    .height(Dimens.buttonHeight)
                    .padding(start = Dimens.spacingSm),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
        }
    }
}