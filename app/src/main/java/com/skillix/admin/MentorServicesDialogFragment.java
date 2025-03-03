package com.skillix.admin;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MentorServicesDialogFragment extends DialogFragment {

    private EditText serviceDescriptionEditText,getDurationText, servicePriceEditText,servicePackageNameEditText;
    private FirebaseFirestore firestore;
    ArrayList<String> servicePackageArrayList;
    ImageView addCountButtonImageView, minusCountButtonImageView;

    private int count = 0;
    private String serviceId;
    AlertDialog dialog;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_mentor_services_dialog, null);

        firestore = FirebaseFirestore.getInstance();

        servicePackageNameEditText = dialogView.findViewById(R.id.servicePackageNameEditText);
        getDurationText = dialogView.findViewById(R.id.getDurationText);
        serviceDescriptionEditText = dialogView.findViewById(R.id.serviceDescriptionEditText);
        servicePriceEditText = dialogView.findViewById(R.id.servicePriceEditText);


        addCountButtonImageView = dialogView.findViewById(R.id.addCountButtonImageView);
        minusCountButtonImageView = dialogView.findViewById(R.id.minusCountButtonImageView);



        if (getArguments() != null) {
            serviceId = getArguments().getString("serviceId");
        }

        if (serviceId != null) {
            fetchServiceData(serviceId);
        }

        minusCountButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count--;

                getDurationText.setText(String.valueOf(count));
            }
        });

        addCountButtonImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                count++;
                getDurationText.setText(String.valueOf(count));
            }
        });


        builder.setView(dialogView)
                .setPositiveButton(R.string.mentor_hire, null) // Remove default behavior
                .setNegativeButton(R.string.mentor_hire_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();  // Close the dialog if canceled
                    }
                });


        dialog = builder.create();
        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialogInterface) {
                Button positiveButton = ((AlertDialog) dialogInterface).getButton(AlertDialog.BUTTON_POSITIVE);
                positiveButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (serviceId != null) {
                            updateService();
                        }else {
                            addService();
                        }

                    }
                });
            }
        });

        return dialog;
    }




    private void addService() {


        String packageName = servicePackageNameEditText.getText().toString();
        String packageDescription = serviceDescriptionEditText.getText().toString();
        String packagePrice = servicePriceEditText.getText().toString();


        if (packageName.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a package name.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (packageDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
            serviceDescriptionEditText.requestFocus();
            return;
        }

        if (packagePrice.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a price.", Toast.LENGTH_SHORT).show();
            servicePriceEditText.requestFocus();
            return;
        }

        Log.i("SkilliXLog", "packageName " + packageName);
        Log.i("SkilliXLog", "packageDescription " + packageDescription);
        Log.i("SkilliXLog", "packagePrice " + packagePrice);

        Map<String, Object> addServiceDocument = new HashMap<>();
        addServiceDocument.put("title", packageName);
        addServiceDocument.put("description", packageDescription);
        addServiceDocument.put("price", packagePrice);

        firestore.collection("mentor_subscription").add(addServiceDocument)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        // Update UI after success, ensure it's on the main thread
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Service successfully added!", Toast.LENGTH_SHORT).show();
                                Log.i("SkilliXLog", "Service successfully added!");
                                dialog.dismiss(); // Close dialog only when operation is complete
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Service adding error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("SkilliXLog", "Service adding error: " + e.getMessage());
                            }
                        });
                    }
                });
    }

    private void updateService() {

        String packageName = servicePackageNameEditText.getText().toString();
        String duration = getDurationText.getText().toString();
        String packageDescription = serviceDescriptionEditText.getText().toString();
        String packagePrice = servicePriceEditText.getText().toString();



        if (packageName.equals("Select Package")) {
            Toast.makeText(getContext(), "Please select a valid package.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (packageDescription.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a description.", Toast.LENGTH_SHORT).show();
            serviceDescriptionEditText.requestFocus();
            return;
        }

        if (packagePrice.isEmpty()) {
            Toast.makeText(getContext(), "Please enter a price.", Toast.LENGTH_SHORT).show();
            servicePriceEditText.requestFocus();
            return;
        }


        Log.i("SkilliXLog", "packageName " + packageName);
        Log.i("SkilliXLog", "packageDescription " + packageDescription);
        Log.i("SkilliXLog", "packagePrice " + packagePrice);


        Map<String, Object> updatedServiceDocument = new HashMap<>();
        updatedServiceDocument.put("title", packageName);
        updatedServiceDocument.put("duration", duration);
        updatedServiceDocument.put("description", packageDescription);
        updatedServiceDocument.put("price", packagePrice);

        firestore.collection("mentor_subscription").document(serviceId)
                .update(updatedServiceDocument)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Service successfully updated!", Toast.LENGTH_SHORT).show();
                                Log.i("SkilliXLog", "Service successfully updated!");
                                dialog.dismiss(); // Close dialog only when operation is complete
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), "Service update error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                Log.i("SkilliXLog", "Service update error: " + e.getMessage());
                            }
                        });
                    }
                });
    }

    private void fetchServiceData(String serviceId) {
        firestore.collection("mentor_subscription").document(serviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String packageName = document.getString("title");
                                String duration = document.getString("duration");
                                String packageDescription = document.getString("description");
                                String packagePrice = document.getString("price");

                                // Populate dialog fields with the fetched data
                                serviceDescriptionEditText.setText(packageDescription);
                                servicePriceEditText.setText(packagePrice);
                                servicePackageNameEditText.setText(packageName);
                                getDurationText.setText(duration);


                            } else {
                                Log.d("FirestoreLog", "No such document");
                            }
                        } else {
                            Log.d("FirestoreLog", "Error getting document: ", task.getException());
                        }
                    }
                });
    }




}

