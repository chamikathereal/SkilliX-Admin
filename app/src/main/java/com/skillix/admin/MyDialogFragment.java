package com.skillix.admin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class MyDialogFragment extends DialogFragment {

    private String mentorId, accountStatus;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        if (getArguments() != null) {
            mentorId = getArguments().getString("mentorId");
            accountStatus = getArguments().getString("accountStatus");
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Do you want to proceed?")
                .setPositiveButton("OK", (dialog, id) -> {
                    if (mentorId != null) {
                        performProcess(mentorId, accountStatus);
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });

        return builder.create();
    }

    private void performProcess(String mentorId, String accountStatus) {

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        Map<String, Object> updateMentorMap = new HashMap<>();

        if (accountStatus.equals("disabled")) {
            updateMentorMap.put("account_status", "active");
        } else {
            updateMentorMap.put("account_status", "disabled");
        }

        firestore.collection("users").document(mentorId).update(updateMentorMap)
                .addOnSuccessListener(aVoid -> {

                    Log.d("Firestore", "Status updated successfully!");
                })
                .addOnFailureListener(e -> {

                    Log.e("Firestore", "Error updating document", e);
                    Toast.makeText(getActivity(), "Error updating status", Toast.LENGTH_SHORT).show();
                });
    }

}
