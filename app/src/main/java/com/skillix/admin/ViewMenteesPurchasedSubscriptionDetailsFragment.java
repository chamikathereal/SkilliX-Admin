package com.skillix.admin;

import android.app.Dialog;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class ViewMenteesPurchasedSubscriptionDetailsFragment extends DialogFragment {

    AlertDialog dialog;
    String mentorId;
    FirebaseFirestore firestore;

    TextView nameOfServiceTextView, pdateOfServiceTextView, priceOfServiceTextView, descriptionOfServiceTextView, edateOfServiceTextView2;
    ImageView closeImageView;

    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        View dialogView = inflater.inflate(R.layout.fragment_view_mentees_purchased_subscription_details, null);

        firestore = FirebaseFirestore.getInstance();


        nameOfServiceTextView = dialogView.findViewById(R.id.nameOfServiceTextView);
        pdateOfServiceTextView = dialogView.findViewById(R.id.pdateOfServiceTextView);
        edateOfServiceTextView2 = dialogView.findViewById(R.id.edateOfServiceTextView2);
        priceOfServiceTextView = dialogView.findViewById(R.id.priceOfServiceTextView);
        descriptionOfServiceTextView = dialogView.findViewById(R.id.descriptionOfServiceTextView);

        closeImageView = dialogView.findViewById(R.id.closeImageView);


        if (getArguments() != null) {
            mentorId = getArguments().getString("mentorId");

        }

        loadMentorSubscriptionDetails();

        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
            }
        });


        builder.setView(dialogView);

        dialog = builder.create();


        return dialog;
    }


    private void loadMentorSubscriptionDetails() {

        Log.i("SkilliXAdminLog", "mmmmmmmmmm: " + mentorId);

        firestore.collection("subscription_payments").whereEqualTo("mentor_id", mentorId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            QuerySnapshot querySnapshot = task.getResult();

                            for (DocumentSnapshot document : querySnapshot) {

                                String subscriptionPaymentsId = document.getId();
                                String subscription_id = document.getString("subscription_id");
                                String dateTime = document.getString("date_time");

                                Log.i("SkilliXAdminLog", "subscriptionPaymentsId: " + subscriptionPaymentsId);
                                Log.i("SkilliXAdminLog", "subscription_id: " + subscription_id);


                                firestore.collection("mentor_subscription").document(subscription_id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                                                if (task.isSuccessful()) {

                                                    DocumentSnapshot document = task.getResult();

                                                    if (document.exists()) {

                                                        String title = document.getString("title");
                                                        String price = document.getString("price");
                                                        String duration = document.getString("duration");
                                                        String description = document.getString("description");

                                                        try {

                                                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


                                                            Date purchasedDate = dateFormat.parse(dateTime);

                                                            int durationInt = Integer.parseInt(duration);

                                                            Calendar calendar = Calendar.getInstance();
                                                            calendar.setTime(purchasedDate);
                                                            calendar.add(Calendar.MONTH, durationInt);

                                                            Date ExpireDate = calendar.getTime();

                                                            SimpleDateFormat newDateFormat = new SimpleDateFormat("yyyy MMM dd");

                                                            Log.i("SkilliXAdminLog", "title: " + title);
                                                            Log.i("SkilliXAdminLog", "Purchased Date: " + newDateFormat.format(purchasedDate));
                                                            Log.i("SkilliXAdminLog", "Expire Data: " + newDateFormat.format(ExpireDate));
                                                            Log.i("SkilliXAdminLog", "price: " + price);
                                                            Log.i("SkilliXAdminLog", "description: " + description);

                                                            Log.i("SkilliXAdminLog", "duration: " + duration);

                                                            String purchasedDateText = String.valueOf(newDateFormat.format(purchasedDate));
                                                            String ExpireDateText = String.valueOf(newDateFormat.format(ExpireDate));

                                                            nameOfServiceTextView.setText(title);
                                                            pdateOfServiceTextView.setText(purchasedDateText);
                                                            edateOfServiceTextView2.setText(ExpireDateText);
                                                            priceOfServiceTextView.setText(price);
                                                            descriptionOfServiceTextView.setText(description);


                                                        } catch (Exception e) {
                                                            e.printStackTrace();
                                                            Log.e("SkilliXSubscriptionTestLog", "Error parsing date: " + dateTime);
                                                        }


                                                    }

                                                }

                                            }
                                        });

                            }

                        }

                    }
                });


    }


}