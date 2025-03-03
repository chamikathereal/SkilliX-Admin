package com.skillix.admin.model;

public class MentorCard {

    private String mentorId;
    private String profileImgURL;
    private String firstName;
    private String lastName;
    private String career;
    private String country;
    private String accountStatus;

    public MentorCard(String mentorId, String profileImgURL, String firstName, String lastName, String career, String country, String accountStatus) {
        this.mentorId = mentorId;
        this.profileImgURL = profileImgURL;
        this.firstName = firstName;
        this.lastName = lastName;
        this.career = career;
        this.country = country;
        this.accountStatus = accountStatus;
    }

    public String getMentorId() {
        return mentorId;
    }

    public void setMentorId(String mentorId) {
        this.mentorId = mentorId;
    }

    public String getProfileImgURL() {
        return profileImgURL;
    }

    public void setProfileImgURL(String profileImgURL) {
        this.profileImgURL = profileImgURL;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getCareer() {
        return career;
    }

    public void setCareer(String career) {
        this.career = career;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getAccountStatus() {
        return accountStatus;
    }

    public void setAccountStatus(String accountStatus) {
        this.accountStatus = accountStatus;
    }
}
