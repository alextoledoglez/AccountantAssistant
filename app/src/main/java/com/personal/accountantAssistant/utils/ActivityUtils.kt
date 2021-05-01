package com.personal.accountantAssistant.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import com.personal.accountantAssistant.ui.payments.PaymentsDetailsActivity
import com.personal.accountantAssistant.ui.payments.entities.Payments
import com.personal.accountantAssistant.ui.payments.enums.PaymentsType
import java.util.*

object ActivityUtils {
    @JvmStatic
    fun startActivity(packageContext: Context,
                      activityClass: Class<*>?) {
        val intent = Intent(packageContext, activityClass)
        packageContext.startActivity(intent)
    }

    fun startPaymentDetailsActivity(packageContext: Context,
                                    paymentsType: PaymentsType?) {
        val activity = parse(packageContext)
        val payment = Payments()
        payment.type = paymentsType
        val activityIntent = Intent(packageContext, PaymentsDetailsActivity::class.java)
        activityIntent.putExtra(Constants.ENTITY, payment)
        activity.startActivityForResult(activityIntent, Constants.DETAIL_REQUEST_CODE)
    }

    fun refreshBy(context: Context) {
        val activity = parse(context)
        Objects.requireNonNull(activity).recreate()
    }

    @JvmStatic
    fun parse(context: Context): Activity {
        return context as Activity
    }

    @JvmStatic
    fun parse(activity: Activity?): Context? {
        return activity?.applicationContext
    }
}