package com.personal.accountantAssistant.ui.expenses

import android.app.DatePickerDialog
import android.text.SpannableStringBuilder
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.data.enums.ExpensesType
import com.personal.accountantAssistant.data.enums.ExpensesType.Companion.isBill
import com.personal.accountantAssistant.domain.models.ExpenseModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.common.CurrencyTextField
import com.personal.accountantAssistant.ui.theme.extendedColors
import java.util.*

@Composable
fun ExpenseDetailsScreen(
    expenseModel: ExpenseModel?,
    onSave: (ExpenseModel) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var name by remember { mutableStateOf(expenseModel?.name.orEmpty()) }
    var nameError by remember { mutableStateOf(false) }
    var quantity by remember { mutableIntStateOf(AlertDialogBuilder.toCurrentOrMinValue(expenseModel?.quantity.orZero())) }
    var dateStr by remember { mutableStateOf(expenseModel?.date.toDateStr()) }
    var valueRawDigits by remember { mutableStateOf(expenseModel?.unitaryValue.toRawCurrencyDigits()) }
    var isActive by remember { mutableStateOf(expenseModel?.isActive.orFalse()) }

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
            .padding(dimensionResource(R.dimen.default_material_margin))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
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
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        CurrencyTextField(
            rawDigits = valueRawDigits,
            onValueChange = { valueRawDigits = it },
            label = stringResource(R.string.value),
            modifier = Modifier.fillMaxWidth()
        )

        if (isBillType) {
            Spacer(modifier = Modifier.height(8.dp))

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

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = stringResource(R.string.active),
                color = MaterialTheme.colorScheme.onSurface,
                fontWeight = FontWeight.Normal,
                modifier = Modifier.padding(end = 8.dp)
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
                .padding(top = dimensionResource(R.dimen.half_material_margin))
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(end = dimensionResource(R.dimen.half_material_margin)),
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
                    .height(48.dp)
                    .padding(start = dimensionResource(R.dimen.half_material_margin)),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
            ) { Text(stringResource(R.string.save), color = MaterialTheme.colorScheme.onPrimary) }
        }
    }
}

@Composable
fun ClickableReadOnlyField(value: String, label: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .clickableNoRipple(onClick = onClick)
        )
    }
}

private fun Modifier.clickableNoRipple(onClick: () -> Unit): Modifier =
    this.then(
        Modifier.noRippleClickable(onClick)
    )

private fun Modifier.noRippleClickable(onClick: () -> Unit) = composed {
    clickable(
        indication = null,
        interactionSource = remember { MutableInteractionSource() },
        onClick = onClick
    )
}