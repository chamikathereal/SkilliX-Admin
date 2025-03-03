package com.skillix.admin.api;


import com.skillix.admin.model.TermsRequest;  // Make sure this matches the correct package

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface TermsApi {
    @POST("saveTermsAndConditions")
    Call<Void> saveTermsAndConditions(@Body TermsRequest termsRequest);
}


