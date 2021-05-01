package com.personal.accountantAssistant.ui.payments

import com.google.gson.Gson
import com.stripe.Stripe
import com.stripe.model.PaymentIntent
import com.stripe.param.PaymentIntentCreateParams
import spark.Request
import spark.Response
import spark.Spark
import java.nio.file.Paths

object StripeServer {
    private val gson = Gson()
    private fun calculateOrderAmount(): Int {
        // Replace this constant with a calculation of the order's amount
        // Calculate the order total on the server to prevent
        // users from directly manipulating the amount on the client
        return 1400
    }

    fun initialize() {
        Spark.port(4242)
        Spark.staticFiles.externalLocation(Paths.get("").toAbsolutePath().toString())
        // This is your real test secret API key.
        Stripe.apiKey = "sk_test_51HLwPeAlTtH8elLJ6wlHOsxm0THc1cXsnkrR2PwzkrhOupaL2kzSR6pmeKZ3TaxA7rfyYPXIzPh36mMWfbxVIE8d00ysKEpWMD"
        Spark.post("/create-payment-intent") { request: Request, response: Response ->
            response.type("application/json")
            val postBody = gson.fromJson(request.body(), CreatePayment::class.java)
            val createParams = PaymentIntentCreateParams.Builder()
                    .setCurrency("usd")
                    .setAmount(calculateOrderAmount().toLong())
                    .build()
            // Create a PaymentIntent with the order amount and currency
            val intent = PaymentIntent.create(createParams)
            val paymentResponse = CreatePaymentResponse(intent.clientSecret)
            gson.toJson(paymentResponse)
        }
    }
}