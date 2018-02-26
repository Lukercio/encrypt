package com.adyen.pojo;


import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.adyen.encrypter.ClientSideEncrypter;
import com.adyen.encrypter.exception.EncrypterException;


/**
 * Created by andrei on 8/8/16. -  For Android
 * Converted to Java by ldlopes on 02/23/2018
 *
 * It's not recommended to encrypt data in backend
 */

public class Card {

    private static final String tag = Card.class.getSimpleName();
    private static final SimpleDateFormat GENERATION_DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private String number;
    private String expiryMonth;
    private String expiryYear;
    private String cardHolderName;
    private String cvc;
    private Date generationTime = new Date();

    static {
        GENERATION_DATE_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    /**
     * @deprecated Use {@link Card.Builder} instead.
     */

    public Card() {

    }

    public String getNumber() {
        return number;
    }

    @Deprecated
    public void setNumber(String number) {
        this.number = number;
    }

    public String getExpiryMonth() {
        return expiryMonth;
    }

    @Deprecated
    public void setExpiryMonth(String expiryMonth) {
        this.expiryMonth = expiryMonth;
    }

    public String getExpiryYear() {
        return expiryYear;
    }

    @Deprecated
    public void setExpiryYear(String expiryYear) {
        this.expiryYear = expiryYear;
    }

    public String getCardHolderName() {
        return cardHolderName;
    }

    @Deprecated
    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCvc() {
        return cvc;
    }

    @Deprecated
    public void setCvc(String cvc) {
        this.cvc = cvc;
    }

    public Date getGenerationTime() {
        return generationTime;
    }

    @Deprecated
    public void setGenerationTime(Date generationTime) {

        this.generationTime = generationTime;
    }

    /**
     * Serializes and encrypts the data from the {@link Card}.
     *
     * @param publicKey The public key to encrypt with.
     * @return The serialized and encrypted data from the {@link Card}.
     * @throws EncrypterException If the {@link Card} could not be encrypted.
     */
    private String serialize(String publicKey) throws EncrypterException {
        JSONObject cardJson = new JSONObject();
        String encryptedData = null;


        try {
            cardJson.put("generationtime", GENERATION_DATE_FORMAT.format(generationTime));
            cardJson.put("number", number);
            cardJson.put("holderName", cardHolderName);
            cardJson.put("cvc", cvc);
            cardJson.put("expiryMonth", expiryMonth);
            cardJson.put("expiryYear", expiryYear);

            encryptedData = encryptData(cardJson.toString(), publicKey);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return encryptedData;
    }

    private static Card createCard(String number, String expiryMonth, String expiryYear, String cardHolderName, String cvc ) {
        Builder card = new Builder();

        if (number != null && expiryMonth != null && expiryYear != null && cardHolderName != null && cvc != null) {
            card.setNumber(number);
            card.setExpiryMonth(expiryMonth);
            card.setExpiryYear(expiryYear);
            card.setHolderName(cardHolderName);
            card.setCvc(cvc);

        } else if (number == null && expiryMonth == null && expiryYear == null && cardHolderName == null && cvc != null) {
            card.setCvc(cvc);

        }
        return card.card;
    }


    /**
     * @return masked card number if the number is already available and the number of digits is longer than 13. Otherwise empty string.
     */
    public String toMaskedCardNumber() {
        if (number == null || number.length() < 14) {
            return "";
        }
        StringBuilder sb = new StringBuilder(number.length());

        sb.append(getMaskingChars(number.length())).append(getLastFourDigitsFromCardNumber(number));
        return sb.toString();
    }

    private String getLastFourDigitsFromCardNumber(final String fullCardNumber) {
        if (fullCardNumber != null && fullCardNumber.length() >= 14) {
            return fullCardNumber.substring(fullCardNumber.length() - 4);
        }
        return "";
    }

    private String getMaskingChars(final int totalLength) {
        int charsToMask = totalLength - 4;
        if (charsToMask <= 0) {
            return "";
        }
        char[] mask = new char[charsToMask];
        while (charsToMask > 0) {
            charsToMask--;
            mask[charsToMask] = '*';
        }
        return new String(mask);
    }

    /*
    * Helper method that calls the ClientSideEncrypter encrypt method
    * */
    private String encryptData(String data, String publicKey) throws EncrypterException {
        String encryptedData = null;

        try {
            ClientSideEncrypter encrypter = new ClientSideEncrypter(publicKey);
            encryptedData = encrypter.encrypt(data);
        } catch (EncrypterException e) {
            throw e;
        }

        return encryptedData;
    }

    @Override
    public String toString() {
        JSONObject cardJson = new JSONObject();

        try {
            cardJson.put("generationtime", GENERATION_DATE_FORMAT.format(generationTime));
            if (number.length() >= 4) {
                cardJson.put("number", number.substring(0, 3));
            }
            cardJson.put("holderName", cardHolderName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cardJson.toString();
    }

    /**
     *   Main class to encrypt data in the project
     */
    public static String getEncryptedData(String number, String expiryMonth, String expiryYear, String cardHolderName, String cvc, String publicKey){
        String encryptedData = null;
        Card cardToEncript = new Card();

        try {

            if (number != null && expiryMonth != null && expiryYear != null && cardHolderName != null && cvc != null ) {

                cardToEncript = createCard(number, expiryMonth, expiryYear, cardHolderName, cvc);

                if (cardToEncript == null) {
                    return "";

                } else {

                    encryptedData = cardToEncript.serialize(publicKey);
                    return encryptedData;
                }

            // Encrypt only the cvc data, when receiving all card data, but only cvc not null
            } else if(number == null && expiryMonth == null && expiryYear == null && cardHolderName == null && cvc != null ) {
                cardToEncript = createCard(null, null, null, null, cvc);
                encryptedData = cardToEncript.serialize(publicKey);

                return encryptedData;
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        return "";
    }

    /**
     *   Encrypt only the cvc data, when receiving only the cvc
     */
    public static String getEncryptedData(String cvc, String publicKey){
        String encryptedData = null;
        //String creditCard = "test";

        try {

            if (cvc != null ) {
                Card cardToEncrypt = new Card();

                // Encrypt only the cvc value (for one-click purchases)
                cardToEncrypt = createCard(null, null, null, null, cvc);

                if (cardToEncrypt == null) {
                    return "";

                } else {

                    encryptedData = cardToEncrypt.serialize(publicKey);

                    return encryptedData;
                }

            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return "";

    }

    /**
     *  Main Class for fast-testing, calling getEncryptedData with all
     *  card data (or only cvv), and the publicKey value
     */
    public static void main(String []args) {
        System.out.println("Test - Parameters as null but CVV - Encrypted Data: " + getEncryptedData(null, null, null, null, "000", "YourPublicKey"));
        System.out.println("Test - All parameters ok - Encrypted Data: " + getEncryptedData("123", "456", "789", "890", "000", "YourPublicKey"));
        System.out.println("Test - Only CVV passed - Encrypted Data: " + getEncryptedData( "000", "YourPublicKey"));

    }

    /**
     * Builder for {@link Card} objects.
     */
    public static final class Builder {
        private final Card card;

        public Builder() {
            card = new Card();
        }

        /**
         * Set the mandatory generation time.
         *
         * @param generationTime The generation time.
         * @return The Builder instance.
         */
        public Builder setGenerationTime(Date generationTime) {
            card.generationTime = generationTime;

            return this;
        }

        /**
         * Set the optional card number.
         *
         * @param number The card number.
         * @return The Builder instance.
         */
        public Builder setNumber(String number) {
            card.number = removeWhiteSpaces(number);

            return this;
        }

        /**
         * Set the optional card holder name.
         *
         * @param holderName The holder name.
         * @return The Builder instance.
         */
        public Builder setHolderName(String holderName) {
            card.cardHolderName = trimAndRemoveMultipleWhiteSpaces(holderName);

            return this;
        }

        /**
         * Set the optional card security code.
         *
         * @param cvc The card security code.
         * @return The Builder instance.
         */
        public Builder setCvc(String cvc) {
            card.cvc = removeWhiteSpaces(cvc);

            return this;
        }

        /**
         * Set the optional expiry month, e.g. "1" or "01" for January.
         *
         * @param expiryMonth The expiry month.
         * @return The Builder instance.
         */
        public Builder setExpiryMonth(String expiryMonth) {
            card.expiryMonth = removeWhiteSpaces(expiryMonth);

            return this;
        }

        /**
         * Set the optional expiry year, e.g. "2021".
         *
         * @param expiryYear The expiry year.
         * @return The Builder instance.
         */
        public Builder setExpiryYear(String expiryYear) {
            card.expiryYear = removeWhiteSpaces(expiryYear);

            return this;
        }

        /**
         * Performs some simple checks on the given {@link Card} object and builds it.
         *
         * @return The valid {@link Card} object.
         * @throws NullPointerException If any mandatory field is null.
         * @throws IllegalStateException If any field is in an illegal state.
         */
        public Card build() throws NullPointerException, IllegalStateException {
            requireNonNull(card.generationTime, "generationTime");
            require(card.number == null || card.number.matches("[0-9]{8,19}"), "number must be null or have 8 to 19 digits (inclusive).");
            require(card.cardHolderName == null || card.cardHolderName.length() > 0, "cardHolderName must be null or not empty.");
            require(card.cvc == null || (card.cvc.matches("[0-9]{3,4}")), "cvc must be null or have 3 to 4 digits.");
            require(card.expiryMonth == null || card.expiryMonth.matches("0?[1-9]|1[0-2]"), "expiryMonth must be null or between 1 and 12.");
            require(card.expiryYear == null
                    || card.expiryYear.matches("20\\d{2}"), "expiryYear must be in the second millennium and first century.");

            return card;
        }

        private String removeWhiteSpaces(String string) {
            return string != null ? string.replaceAll("\\s", "") : null;
        }

        private String trimAndRemoveMultipleWhiteSpaces(String string) {
            return string != null ? string.trim().replaceAll("\\s{2,}", " ") : null;
        }


        private void require(boolean condition, String message) throws IllegalStateException {
            if (!condition) {
                throw new IllegalStateException(message);
            }
        }

        private void requireNonNull(Object object, String objectName) throws IllegalStateException {
            if (object == null) {
                throw new NullPointerException(String.format("%s may not be null.", objectName));
            }
        }
    }
}
