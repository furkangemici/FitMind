package com.example.fitmind.api;

import com.example.fitmind.api.model.GroqRequest;
import com.example.fitmind.api.model.GroqResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface GroqApiService {
    @POST("chat/completions")
    Call<GroqResponse> generateContent(
        @Header("Authorization") String authHeader,
        @Body GroqRequest request
    );
}
