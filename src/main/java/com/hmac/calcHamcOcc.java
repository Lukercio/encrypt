/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hmac;

import com.google.common.io.BaseEncoding;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;

/**
 *
 * @author mgimenes
 */
public class calcHamcOcc {

    public static void main(String[] args) {
        // TODO code application logic here

    }

    private static String getHMACData(String shopperLocale, String merchantReference, String merchantAccount,
            String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY) {

        //HMAC_KEY = "44782DEF547AAA06C910C43932B1EB0C71FC68D9D0C057550C48EC2ACF6BA056";
        Map<String, String> pairs = new HashMap<>();
        pairs.put("shopperLocale", shopperLocale);
        pairs.put("merchantReference", merchantReference);
        pairs.put("merchantAccount", merchantAccount);
        pairs.put("sessionValidity", sessionValidity);
        pairs.put("shipBeforeDate", shipBeforeDate);
        pairs.put("paymentAmount", paymentAmount);
        pairs.put("currencyCode", currencyCode);
        pairs.put("skinCode", skinCode);

        SortedMap<String, String> sortedPairs = new TreeMap<>(pairs);

        SortedMap<String, String> escapedPairs
                = sortedPairs.entrySet().stream()
                        .collect(Collectors.toMap(
                                e -> e.getKey(),
                                e -> (e.getValue() == null) ? "" : e.getValue().replace("\\", "\\\\").replace(":", "\\:"),
                                (k, v) -> k,
                                TreeMap::new
                        ));

        String signingString = Stream.concat(escapedPairs.keySet().stream(), escapedPairs.values().stream())
                .collect(Collectors.joining(":"));

// import from com.google.common.io.BaseEncoding;
        byte[] binaryHmacKey = BaseEncoding.base16().decode(HMAC_KEY);

// Create an HMAC SHA-256 key from the raw key bytes
        SecretKeySpec signingKey = new SecretKeySpec(binaryHmacKey, "HmacSHA256");

// Get an HMAC SHA-256 Mac instance and initialize with the signing key
        Mac mac = null;
        try {
            mac = Mac.getInstance("HmacSHA256");
            mac.init(signingKey);
        } catch (InvalidKeyException ex) {

            Logger.getLogger(calcHamcOcc.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        } catch (NoSuchAlgorithmException ex) {

            Logger.getLogger(calcHamcOcc.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

// calculate the hmac on the binary representation of the signing string
        byte[] binaryHmac = mac.doFinal(signingString.getBytes(Charset.forName("UTF8")));

        String signature = Base64.getEncoder().encodeToString(binaryHmac);

        return signature;

    }

    public static String getURLHMAC(String shopperLocale, String merchantReference, String merchantAccount,
            String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY, String hppUrl) {
        String merchantSig = getHMACData(shopperLocale, merchantReference, merchantAccount, sessionValidity, shipBeforeDate, paymentAmount, currencyCode, skinCode, HMAC_KEY);
        String paymentUrl = "";
        try {
            paymentUrl = hppUrl
                    + "?merchantReference=" + URLEncoder.encode(merchantReference, "UTF-8")
                    + "&paymentAmount=" + URLEncoder.encode(paymentAmount, "UTF-8")
                    + "&currencyCode=" + URLEncoder.encode(currencyCode, "UTF-8")
                    + "&shipBeforeDate=" + URLEncoder.encode(shipBeforeDate, "UTF-8")
                    + "&skinCode=" + URLEncoder.encode(skinCode, "UTF-8")
                    + "&merchantAccount=" + URLEncoder.encode(merchantAccount, "UTF-8")
                    + "&sessionValidity=" + URLEncoder.encode(sessionValidity, "UTF-8")
                    + "&shopperLocale=" + URLEncoder.encode(shopperLocale, "UTF-8")
                    + "&merchantSig=" + URLEncoder.encode(merchantSig, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(calcHamcOcc.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paymentUrl;
    }

}
