package com.personal.accountantAssistant.ui.payments;

import com.google.gson.Gson;
import com.stripe.Stripe;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;

import java.nio.file.Paths;

import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFiles;

public class StripeServer {

    private static Gson gson = new Gson();

    static int calculateOrderAmount(Object[] items) {
        // Replace this constant with a calculation of the order's amount
        // Calculate the order total on the server to prevent
        // users from directly manipulating the amount on the client
        return 1400;
    }

    public static void initialize() {
        port(4242);
        staticFiles.externalLocation(Paths.get("").toAbsolutePath().toString());
        // This is your real test secret API key.
        Stripe.apiKey = "sk_test_51HLwPeAlTtH8elLJ6wlHOsxm0THc1cXsnkrR2PwzkrhOupaL2kzSR6pmeKZ3TaxA7rfyYPXIzPh36mMWfbxVIE8d00ysKEpWMD";
        post("/create-payment-intent", (request, response) -> {
            response.type("application/json");
            CreatePayment postBody = gson.fromJson(request.body(), CreatePayment.class);
            PaymentIntentCreateParams createParams = new PaymentIntentCreateParams.Builder()
                    .setCurrency("usd")
                    .setAmount((long) calculateOrderAmount(postBody.getItems()))
                    .build();
            // Create a PaymentIntent with the order amount and currency
            PaymentIntent intent = PaymentIntent.create(createParams);
            CreatePaymentResponse paymentResponse = new CreatePaymentResponse(intent.getClientSecret());
            return gson.toJson(paymentResponse);
        });
    }
}
