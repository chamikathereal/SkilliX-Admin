package com.skillix.admin;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.skillix.admin.api.TermsApi;
import com.skillix.admin.model.TermsRequest;
import com.skillix.admin.util.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TermsAndConditionsFragment extends Fragment {

    FirebaseFirestore firestore;
    private EditText termsAndConditionEditText;
    private Button termsAndConditionSaveButton;


    // Set the BASE_URL for Retrofit, depending on whether you're using an emulator or a real device
    private static final String BASE_URL = "http://10.0.2.2:3000/"; // For Android Emulator
    // Alternatively, use the computer's IP address if running on a real device:
    // private static final String BASE_URL = "http://192.168.x.x:3000/"; // Replace with your computer's IP address

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_terms_and_conditions, container, false);

        termsAndConditionEditText = view.findViewById(R.id.termsAndConditionEditText);
        termsAndConditionSaveButton = view.findViewById(R.id.termsAndConditionSaveButton);

        firestore = FirebaseFirestore.getInstance();

        // Initialize Retrofit with the BASE_URL
        Retrofit retrofit = RetrofitClient.getClient(BASE_URL);
        TermsApi termsApi = retrofit.create(TermsApi.class);

        loadTermsAndCondition();

        termsAndConditionSaveButton.setOnClickListener(v -> {
            String termsAndConditionsText = termsAndConditionEditText.getText().toString();

            if (!termsAndConditionsText.isEmpty()) {
                TermsRequest request = new TermsRequest(termsAndConditionsText);
                Call<Void> call = termsApi.saveTermsAndConditions(request);

                call.enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getActivity(), "Terms saved!", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(getActivity(), "Failed to save terms", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getActivity(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(getActivity(), "Please enter terms and conditions", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    private void loadTermsAndCondition(){

        firestore.collection("terms_and_conditions").document("termsDoc")
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {

                        String tmsAndcnd = documentSnapshot.getString("text");
                        Log.i("SkilliXLogAdmin",tmsAndcnd);
                        termsAndConditionEditText.setText(tmsAndcnd);

                    }
                });

    }
}
