package com.worldpayment.demoapp.utility;

import android.content.Context;
import android.preference.PreferenceManager;

import com.worldpay.library.domain.Card;
import com.worldpay.library.webservices.services.ServiceRequest;
import com.worldpayment.demoapp.BuildConfig;

import static com.worldpayment.demoapp.activities.debitcredit.CreditDebitActivity.PREF_AUTH_TOKEN;

public class TokenUtility {

    public static void populateRequestHeaderFields(ServiceRequest request, Context context) {
        String authToken = PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_AUTH_TOKEN, null);
        request.setAuthToken(authToken);
        request.setMerchantId(BuildConfig.MERCHANT_ID);
        request.setMerchantKey(BuildConfig.MERCHANT_KEY);
        request.setDeveloperId(BuildConfig.DEVELOPER_ID);
        request.setApplicationVersion(BuildConfig.VERSION_NAME);
    }

    public static Card getTestCard() {
        Card card = new Card();
        card.setNumber("4111111111111111");
        card.setExpirationMonth(12);
        card.setExpirationYear(2020);
        card.setFirstName("Visa");
        card.setLastName("Test");
        card.setCvv("999");

        return card;
    }
}
