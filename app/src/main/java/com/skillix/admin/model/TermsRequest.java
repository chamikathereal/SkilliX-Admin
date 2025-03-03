package com.skillix.admin.model;

public class TermsRequest {
    private String termsAndConditionsText;

    public TermsRequest(String termsAndConditionsText) {
        this.termsAndConditionsText = termsAndConditionsText;
    }

    public String getTermsAndConditionsText() {
        return termsAndConditionsText;
    }

    public void setTermsAndConditionsText(String termsAndConditionsText) {
        this.termsAndConditionsText = termsAndConditionsText;
    }
}
