/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.hmac;

import com.google.common.io.BaseEncoding;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author mgimenes
 */
public class calcHamcOcc {

    public static void main(String[] args) {
        System.out.println(getURLHMACPayPal("pt_BR", "o1520961-pg1210072-1535318826742", "passa",
                "123", "01012018", "2020", "BRL", "payPal",
                "AF100476B9165FCEEB212B01F066785D94F6E51B8F449624FD0D56C440054872", "https://test.adyen.com/hpp/skipDetails.shtml", "paypal",

                null, null, null, null, null, null));


    }

    private static String getHMACData(String shopperLocale, String merchantReference, String merchantAccount,
                                      String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY, String brandCode) {

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
        pairs.put("brandCode", brandCode);

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


    private static String getHMACDataPaypal(String shopperLocale, String merchantReference, String merchantAccount,
                                            String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY, String brandCode,
                                            String firstName, String lastName, String dateOfBirthDayOfMonth, String dateOfBirthMonth,
                                            String dateOfBirthYear, String telephoneNumber) {

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
        pairs.put("brandCode", brandCode);
        if (null != firstName) {
            pairs.put("shopper.firstName", firstName);
        }
        if (null != lastName) {
            pairs.put("shopper.lastName", lastName);
        }
        if (null != dateOfBirthDayOfMonth) {
            pairs.put("shopper.dateOfBirthDayOfMonth", dateOfBirthDayOfMonth);
        }
        if (null != dateOfBirthMonth) {
            pairs.put("shopper.dateOfBirthMonth", dateOfBirthMonth);
        }
        if (null != dateOfBirthYear) {
            pairs.put("shopper.dateOfBirthYear", dateOfBirthYear);
        }
        if (null != telephoneNumber) {
            pairs.put("shopper.telephoneNumber", telephoneNumber.replace("(", "").replace(")", "").replace("-", "").replace(" ", "").trim());
        }

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
                                    String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY, String hppUrl, String brandCode) {
        String merchantSig = getHMACData(shopperLocale, merchantReference, merchantAccount, sessionValidity, shipBeforeDate, paymentAmount, currencyCode, skinCode, HMAC_KEY, brandCode);
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
                    + "&brandCode=" + URLEncoder.encode(brandCode, "UTF-8")
                    + "&merchantSig=" + URLEncoder.encode(merchantSig, "UTF-8");
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(calcHamcOcc.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paymentUrl;
    }

    public static String getURLHMACPayPal(String shopperLocale, String merchantReference, String merchantAccount,
                                          String sessionValidity, String shipBeforeDate, String paymentAmount, String currencyCode, String skinCode, String HMAC_KEY, String hppUrl, String brandCode, String firstName, String lastName, String dateOfBirthDayOfMonth, String dateOfBirthMonth,
                                          String dateOfBirthYear, String telephoneNumber) {
        String merchantSig = getHMACDataPaypal(shopperLocale, merchantReference, merchantAccount, sessionValidity,
                shipBeforeDate, paymentAmount, currencyCode, skinCode, HMAC_KEY, brandCode,
                firstName, lastName, dateOfBirthDayOfMonth, dateOfBirthMonth,
                dateOfBirthYear, telephoneNumber);
        String paymentUrl = "";

        if (null == firstName) {
            firstName = "";
        }

        if (null == lastName) {
            lastName = "";
        }
        if (null == dateOfBirthDayOfMonth) {
            dateOfBirthDayOfMonth = "";
        }
        if (null == dateOfBirthMonth) {
            dateOfBirthMonth = "";
        }
        if (null == dateOfBirthYear) {
            dateOfBirthYear = "";
        }
        if (null == telephoneNumber) {
            telephoneNumber = "";
        }
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
                    + "&brandCode=" + URLEncoder.encode(brandCode, "UTF-8")
                    + "&shopper.firstName=" + URLEncoder.encode(firstName, "UTF-8")
                    + "&shopper.lastName=" + URLEncoder.encode(lastName, "UTF-8")
                    + "&shopper.dateOfBirthDayOfMonth=" + URLEncoder.encode(dateOfBirthDayOfMonth, "UTF-8")
                    + "&shopper.dateOfBirthMonth=" + URLEncoder.encode(dateOfBirthMonth, "UTF-8")
                    + "&shopper.dateOfBirthYear=" + URLEncoder.encode(dateOfBirthYear, "UTF-8")
                    + "&shopper.telephoneNumber=" + URLEncoder.encode(telephoneNumber.replace("(", "").replace(")", "").replace("-", "").replace(" ", "").trim(), "UTF-8")
                    + "&merchantSig=" + URLEncoder.encode(merchantSig, "UTF-8");

            if (firstName.equals("")) {
                paymentUrl = paymentUrl.replace("&shopper.firstName=", "");
            }

            if (lastName.equals("")) {
                paymentUrl=  paymentUrl.replace("&shopper.lastName=", "");
            }
            if (dateOfBirthDayOfMonth.equals("")) {
                paymentUrl =paymentUrl.replace("&shopper.dateOfBirthDayOfMonth=", "");
            }
            if (dateOfBirthMonth.equals("")) {
                paymentUrl =paymentUrl.replace("&shopper.dateOfBirthMonth=", "");
            }
            if (dateOfBirthYear.equals("")) {
                paymentUrl = paymentUrl.replace("&shopper.dateOfBirthYear=", "");
            }
            if (telephoneNumber.equals("")) {
                paymentUrl = paymentUrl.replace("&shopper.telephoneNumber=", "");
            }


        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(calcHamcOcc.class.getName()).log(Level.SEVERE, null, ex);
        }

        return paymentUrl;
    }

}
