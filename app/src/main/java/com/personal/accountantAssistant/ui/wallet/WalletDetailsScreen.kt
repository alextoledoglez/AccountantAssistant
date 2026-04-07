package com.personal.accountantAssistant.ui.wallet

import android.app.DatePickerDialog
import android.text.SpannableStringBuilder
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.personal.accountantAssistant.R
import com.personal.accountantAssistant.bases.AlertDialogBuilder
import com.personal.accountantAssistant.domain.models.CardModel
import com.personal.accountantAssistant.extensions.*
import com.personal.accountantAssistant.ui.expenses.ClickableReadOnlyField
import java.util.*

@Composable
fun WalletDetailsScreen(
    cardModel: CardModel?,
    onSave: (CardModel) -> Unit,
    onCancel: () -> Unit
) {
    val context = LocalContext.current

    var company by remember { mutableStateOf(cardModel?.company.orEmpty()) }
    var name by remember { mutableStateOf(cardModel?.name.orEmpty()) }
    var availableRawDigits by remember { mutableStateOf(cardModel?.availableValue.toRawCurrencyDigits()) }
    var limitRawDigits by remember { mutableStateOf(cardModel?.limitValue.toRawCurrencyDigits()) }
    var password by remember { mutableStateOf(cardModel?.password.orEmpty()) }
    var dateStr by remember { mutableStateOf(cardModel?.date.toDateStr()) }
    var isActive by remember { mutableStateOf(cardModel?.isActive.orFalse()) }

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
                color = colorResource(R.color.primaryColor),
                shape = MaterialTheme.shapes.extraSmall
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Text(
                        text = stringResource(R.string.wallet_details).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = company,
            onValueChange = { company = it.uppercase() },
            label = { Text(stringResource(R.string.card_company)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = name,
            onValueChange = { name = it.uppercase() },
            label = { Text(stringResource(R.string.name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = availableRawDigits.toCurrencyMaskedStr().trim(),
            onValueChange = { availableRawDigits = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.available_card_value)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = limitRawDigits.toCurrencyMaskedStr().trim(),
            onValueChange = { limitRawDigits = it.filter { c -> c.isDigit() } },
            label = { Text(stringResource(R.string.limit_card_value)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        Spacer(modifier = Modifier.height(8.dp))

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

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(stringResource(R.string.active), fontWeight = FontWeight.Bold)
                Switch(
                    checked = isActive,
                    onCheckedChange = { isActive = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = colorResource(R.color.primaryColor),
                        checkedTrackColor = colorResource(R.color.primaryColor).copy(alpha = 0.5f)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

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
                .padding(top = dimensionResource(R.dimen.half_material_margin))
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .padding(end = dimensionResource(R.dimen.half_material_margin)),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.redColor))
            ) { Text(stringResource(R.string.cancel), color = Color.White) }

            Button(
                onClick = {
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
                    .height(48.dp)
                    .padding(start = dimensionResource(R.dimen.half_material_margin)),
                colors = ButtonDefaults.buttonColors(containerColor = colorResource(R.color.primaryColor))
            ) { Text(stringResource(R.string.save), color = Color.White) }
        }
    }
}