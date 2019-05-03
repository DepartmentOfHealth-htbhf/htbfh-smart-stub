package uk.gov.dhsc.htbhf.smartstub.service;

/**
 * Contains the first names that will trigger a special scenario response from the card service endpoints.
 */
public enum FirstName {

    CARD_ERROR("CardError"),
    NO_TOP_UP("NoTopup"),
    PARTIAL("Partial"),
    BALANCE_ERROR("BalanceError"),
    PAYMENT_ERROR("PaymentError");

    private String nameToMatch;

    FirstName(String nameToMatch) {
        this.nameToMatch = nameToMatch;
    }

    /**
     * Checks if the first name in the card request matches a special response
     *
     * @param firstName The first name from the card request
     * @return true if the first name matches one of the special cases
     */
    public boolean matchesFirstName(String firstName) {
        return nameToMatch.equalsIgnoreCase(firstName);
    }

}