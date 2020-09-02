package com.personal.accountantAssistant.ui.payments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.personal.accountantAssistant.R;
import com.personal.accountantAssistant.utils.ActivityUtils;
import com.personal.accountantAssistant.utils.MenuHelper;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

public class RemittancesFragment extends Fragment {
    private Context context;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {

        context = getContext();
        MenuHelper.initializeHomeOptions();
        ActivityUtils.startActivity(context, CheckoutActivity.class);
        return inflater.inflate(R.layout.fragment_remittances, container, Boolean.FALSE);
    }
}
