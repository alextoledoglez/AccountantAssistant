package com.personal.accountantAssistant.ui.payments;

import android.annotation.SuppressLint;
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
import com.personal.accountantAssistant.ui.barcode.BarcodeScanActivity;
import com.personal.accountantAssistant.ui.payments.entities.Payments;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsEnum;
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType;
import com.personal.accountantAssistant.utils.Constants;
import com.personal.accountantAssistant.utils.DataBaseUtils;
import com.personal.accountantAssistant.utils.DatePickerDialogUtils;
import com.personal.accountantAssistant.utils.DateUtils;
import com.personal.accountantAssistant.utils.NumberPickerDialogUtils;
import com.personal.accountantAssistant.utils.ParserUtils;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;

public class PaymentsDetailsActivity extends AppCompatActivity {

    @Override
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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

        final Intent intent = getIntent();
        final int idValue = intent.getIntExtra(Constants.UID, Constants.DEFAULT_UID);
        final String productValue = intent.getStringExtra(PaymentsEnum.NAME.name());
        final int quantityValue = intent.getIntExtra(PaymentsEnum.QUANTITY.name(), Constants.DEFAULT_QUANTITY_VALUE);
        final String defaultDateText = intent.getStringExtra(PaymentsEnum.DATE.name());
        final double priceValue = intent.getDoubleExtra(PaymentsEnum.UNITARY_VALUE.name(), Constants.DEFAULT_VALUE);
        final String paymentType = intent.getStringExtra(PaymentsEnum.TYPE.name());
        final boolean activeValue = intent.getBooleanExtra(PaymentsEnum.ACTIVE.name(), Constants.DEFAULT_ACTIVE_STATUS);

        Objects.requireNonNull(getSupportActionBar())
                .setTitle(getActionBarTitleFrom(paymentType));

        nameEditText.setText(productValue);
        NumberPickerDialogUtils.initializeFrom(PaymentsDetailsActivity.this, quantityEditText, quantityValue);

        dateTextView.setVisibility(getDateFieldVisibilityFrom(paymentType));
        dateEditText.setVisibility(getDateFieldVisibilityFrom(paymentType));
        DatePickerDialogUtils.setDatePickerDialogFrom(PaymentsDetailsActivity.this, dateEditText, defaultDateText);

        valueEditText.setText(String.valueOf(priceValue));
        activeSwitch.setChecked(activeValue);

        final Button cancelBtn = findViewById(R.id.cancel_button);
        cancelBtn.setOnClickListener(view -> finish());

        final Button saveBtn = findViewById(R.id.save_button);
        saveBtn.setOnClickListener(view -> {
            final Payments payment = new Payments(idValue,
                    String.valueOf(nameEditText.getText()),
                    Integer.parseInt(String.valueOf(quantityEditText.getText())),
                    DateUtils.toDate(String.valueOf(dateEditText.getText())),
                    Double.parseDouble(String.valueOf(valueEditText.getText())),
                    PaymentsType.valueOf(paymentType),
                    activeSwitch.isChecked());
            DataBaseUtils.saveDataFrom(PaymentsDetailsActivity.this, payment);
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
