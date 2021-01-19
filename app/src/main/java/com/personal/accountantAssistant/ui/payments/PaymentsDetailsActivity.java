package com.personal.accountantAssistant.ui.payments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.Constants;
import com.personal.accountantAssistant.utils.DataBaseUtils;
import com.personal.accountantAssistant.utils.DatePickerDialogUtils;
import com.personal.accountantAssistant.utils.DateUtils;
import com.personal.accountantAssistant.utils.NumberPickerDialogUtils;
import com.personal.accountantAssistant.utils.ParserUtils;
import com.personal.accountantAssistant.utils.ToastUtils;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentsDetailsActivity extends AppCompatActivity {

    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Payments payment;
        final Intent intent = getIntent();
        final Context context = PaymentsDetailsActivity.this;
        final Activity activity = PaymentsDetailsActivity.this;

        if (ParserUtils.isNotNullObject(intent)) {
            final Object entity = intent.getSerializableExtra(Constants.ENTITY);
            payment = ParserUtils.toPayments(entity);
        } else {
            payment = null;
        }

        setContentView(R.layout.activity_payments_details);

        final EditText nameEditText = findViewById(R.id.payment_name);
        nameEditText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        final EditText quantityEditText = findViewById(R.id.payment_quantity);
        quantityEditText.setInputType(InputType.TYPE_NULL);
        final TextView dateTextView = findViewById(R.id.payment_date_label);
        final EditText dateEditText = findViewById(R.id.payment_date);
        dateEditText.setInputType(InputType.TYPE_NULL);
        final EditText valueEditText = findViewById(R.id.payment_value);
        final Switch activeSwitch = findViewById(R.id.payment_active);

        if (ParserUtils.isNotNullObject(payment)) {

            final String paymentType = payment.getType().name();
            Objects.requireNonNull(getSupportActionBar())
                    .setTitle(getActionBarTitleFrom(paymentType));

            nameEditText.setText(payment.getName());
            NumberPickerDialogUtils.initializeFrom(context, quantityEditText, payment.getQuantity());

            dateTextView.setVisibility(getDateFieldVisibilityFrom(paymentType));
            dateEditText.setVisibility(getDateFieldVisibilityFrom(paymentType));
            final String dateStr = DateUtils.toString(payment.getDate());
            DatePickerDialogUtils.setDatePickerDialogFrom(context, dateEditText, dateStr);

            valueEditText.setText(String.valueOf(payment.getUnitaryValue()));
            activeSwitch.setChecked(payment.isActive());
        }

        final Button cancelBtn = findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(view -> finish());

        final Button saveBtn = findViewById(R.id.save_button);
        saveBtn.setOnClickListener(view -> {

            if (ParserUtils.isNotNullObject(payment)) {
                payment.setId(payment.getId());
                payment.setName(String.valueOf(nameEditText.getText()));
                payment.setQuantity(Integer.parseInt(String.valueOf(quantityEditText.getText())));
                payment.setDate(DateUtils.toDate(String.valueOf(dateEditText.getText())));
                payment.setUnitaryValue(Double.parseDouble(String.valueOf(valueEditText.getText())));
                payment.setType(PaymentsType.valueOf(payment.getType().name()));
                payment.setActive(activeSwitch.isChecked());
            }

            DataBaseUtils.saveDataFrom(activity, payment, () -> {
                final Intent resultIntent = new Intent();
                resultIntent.putExtra(Constants.ENTITY, payment);
                setResult(Activity.RESULT_OK, resultIntent);
                finish();
                ToastUtils.showLongText(activity, R.string.record_successfully_save);
            });

        });

/*        final Button barCodeScanButton = findViewById(R.id.bar_code_scan_button);
        barCodeScanButton.setOnClickListener(v -> {
            //final int REQUEST_CODE = 0;
            final Intent barcodeScanIntent = new Intent(PaymentsDetailsActivity.this, BarcodeScanActivity.class);
            startActivity(barcodeScanIntent);
            //startActivityForResult(barcodeScanIntent, REQUEST_CODE);
            //TODO something
        });*/
    }

    private int getActionBarTitleFrom(final String paymentType) {

        if (PaymentsType.isBuy(paymentType)) {
            return R.string.buys_details;
        }

        if (PaymentsType.isBill(paymentType)) {

            return R.string.bills_details;
        }

        return R.string.app_name;
    }

    private int getDateFieldVisibilityFrom(final String paymentType) {
        return PaymentsType.isBuy(paymentType) ? View.GONE : View.VISIBLE;
    }
}
