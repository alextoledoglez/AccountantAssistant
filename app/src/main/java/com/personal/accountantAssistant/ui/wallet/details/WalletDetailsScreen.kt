package com.personal.accountantAssistant.ui.wallet.details

import android.app.DatePickerDialog
import android.text.SpannableStringBuilder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.common.CurrencyTextField
import com.personal.accountantAssistant.ui.expenses.ClickableReadOnlyField
import com.personal.accountantAssistant.ui.theme.Dimens
import com.personal.accountantAssistant.ui.theme.extendedColors
import java.util.*

@Composable
fun WalletDetailsScreen(
    cardModel: CardModel?,
    onSave: (CardModel) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var company by rememberSaveable { mutableStateOf(cardModel?.company.orEmpty()) }
    var companyError by rememberSaveable { mutableStateOf(false) }
    var name by rememberSaveable { mutableStateOf(cardModel?.name.orEmpty()) }
    var availableRawDigits by rememberSaveable { mutableStateOf(cardModel?.availableValue.toRawCurrencyDigits()) }
    var limitRawDigits by rememberSaveable { mutableStateOf(cardModel?.limitValue.toRawCurrencyDigits()) }
    var password by rememberSaveable { mutableStateOf(cardModel?.password.orEmpty()) }
    var dateStr by rememberSaveable { mutableStateOf(cardModel?.date.toDateStr()) }
    var isActive by rememberSaveable { mutableStateOf(cardModel?.isActive.orFalse()) }

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
                        text = stringResource(R.string.wallet_details).uppercase(),
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
            value = company,
            onValueChange = {
                company = it.uppercase()
                if (companyError && it.isNotBlank()) companyError = false
            },
            label = { Text(stringResource(R.string.card_company)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            isError = companyError,
            supportingText = if (companyError) {
                { Text(stringResource(R.string.field_required)) }
            } else null
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it.uppercase() },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        CurrencyTextField(
            rawDigits = availableRawDigits,
            onValueChange = { availableRawDigits = it },
            label = stringResource(R.string.available_card_value),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        CurrencyTextField(
            rawDigits = limitRawDigits,
            onValueChange = { limitRawDigits = it },
            label = stringResource(R.string.limit_card_value),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = password,
                onValueChange = { if (it.length <= 4) password = it.uppercase() },
                label = { Text(stringResource(R.string.card_password)) },
                modifier = Modifier.weight(1f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            Spacer(modifier = Modifier.width(Dimens.spacingSm))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = stringResource(R.string.active),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.Normal
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
        }

        Spacer(modifier = Modifier.height(Dimens.spacingSm))

        ClickableReadOnlyField(
            value = dateStr,
            label = stringResource(R.string.payment_date),
            onClick = {
                val listener = DatePickerDialog.OnDateSetListener { _, y, m, d ->
                    dateStr = Calendar.getInstance().also { it[y, m] = d }.time.toDateStr()
                }
                AlertDialogBuilder(context).showDatePickerFrom(cardModel?.date, listener)
            }
        )

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
                    if (company.isBlank()) {
                        companyError = true
                        return@Button
                    }
                    cardModel?.update(
                        SpannableStringBuilder(company.uppercase()),
                        SpannableStringBuilder(name.uppercase()),
                        SpannableStringBuilder(dateStr),
                        SpannableStringBuilder(password),
                        SpannableStringBuilder(availableRawDigits.toCurrencyMaskedStr()),
                        SpannableStringBuilder(limitRawDigits.toCurrencyMaskedStr()),
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