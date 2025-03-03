package com.skillix.admin.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.skillix.admin.MyDialogFragment;
import com.skillix.admin.R;
import com.skillix.admin.model.MentorCard;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MentorCardAdapter extends RecyclerView.Adapter<MentorCardAdapter.MentorCardViewHolder> {

    ArrayList<MentorCard> mentorCardArrayList;
    private Context context;

    public MentorCardAdapter(Context context, ArrayList<MentorCard> mentorCardArrayList) {
        this.context = context;
        this.mentorCardArrayList = mentorCardArrayList;
    }

    @NonNull
    @Override
    public MentorCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(R.layout.mentor_profile_card_view, parent, false);
        return new MentorCardViewHolder(inflatedView);
    }

    @Override
    public void onBindViewHolder(@NonNull MentorCardViewHolder holder, int position) {
        MentorCard mentorCard = mentorCardArrayList.get(position);

        final Uri mentorImg = Uri.parse(mentorCard.getProfileImgURL());
        final String mentorFirstName = mentorCard.getFirstName();
        final String mentorLastName = mentorCard.getLastName();
        final String mentorFullName = mentorFirstName + " " + mentorLastName;
        final String mentorCategory = mentorCard.getCareer();
        final String mentorCountry = mentorCard.getCountry();
        final String mentorId = mentorCard.getMentorId();

        // Use a final variable for accountStatus that doesn't change inside the lambda
        final String[] accountStatus = new String[1];  // Using array to hold the value

        // Initialize accountStatus from mentorCard
        accountStatus[0] = mentorCard.getAccountStatus();

        // Load image
        Picasso.get()
                .load(mentorImg)
                .placeholder(R.drawable.profile)  // Optional: Placeholder image
                .into(holder.mentorPreviewProImageView);

        // Set other text views
        holder.mentorPreviewNameTextView.setText(mentorFullName);
        holder.mentorPreviewCategoryTextView.setText(mentorCategory);
        holder.mentorPreviewCountryTextView.setText(mentorCountry);

        // Set initial button state based on account status
        updateAccountStatusButton(holder.activeUserAccountButton, accountStatus[0]);

        // Real-time Firestore listener for changes to account status
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("users").document(mentorId)
                .addSnapshotListener((documentSnapshot, e) -> {
                    if (e != null) {
                        Log.w("FirestoreError", "Listen failed.", e);
                        return;
                    }

                    if (documentSnapshot != null && documentSnapshot.exists()) {
                        // Update accountStatus inside the final array
                        accountStatus[0] = documentSnapshot.getString("account_status");
                        // Update button with the new account status
                        updateAccountStatusButton(holder.activeUserAccountButton, accountStatus[0]);
                    }
                });

        // When the button is clicked, show a dialog with updated account status
        holder.activeUserAccountButton.setOnClickListener(view -> {
            MyDialogFragment dialogFragment = new MyDialogFragment();
            Bundle args = new Bundle();
            args.putString("mentorId", mentorId);
            args.putString("accountStatus", accountStatus[0]);  // Pass updated account status
            dialogFragment.setArguments(args);

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            dialogFragment.show(fragmentManager, "MyDialogFragment");
        });
    }



    // Method to update the button's state
    private void updateAccountStatusButton(Button button, String accountStatus) {
        if (accountStatus != null) {
            if (accountStatus.equals("disabled")) {
                button.setBackgroundResource(R.drawable.outline_button_design);
                button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.color_red));
                button.setText("Disabled");
                button.setTextColor(ContextCompat.getColor(context, R.color.white));
            } else {
                button.setBackgroundResource(R.drawable.outline_button_design);
                button.setBackgroundTintList(ContextCompat.getColorStateList(context, R.color.color_green));
                button.setText("Active");
                button.setTextColor(ContextCompat.getColor(context, R.color.white));
            }
        }
    }




    @Override
    public int getItemCount() {
        return mentorCardArrayList.size();
    }

    static class MentorCardViewHolder extends RecyclerView.ViewHolder {
        ImageView mentorPreviewProImageView;
        TextView mentorPreviewNameTextView, mentorPreviewCategoryTextView, mentorPreviewCountryTextView;
        Button activeUserAccountButton;

        public MentorCardViewHolder(@NonNull View itemView) {
            super(itemView);
            mentorPreviewProImageView = itemView.findViewById(R.id.hiredMenteeProImageView);
            mentorPreviewNameTextView = itemView.findViewById(R.id.hiredMenteeNameTextView);
            mentorPreviewCategoryTextView = itemView.findViewById(R.id.hiredMenteeCategoryTextView);
            mentorPreviewCountryTextView = itemView.findViewById(R.id.mentorPreviewCountryTextView);
            activeUserAccountButton = itemView.findViewById(R.id.activeUserAccountButton);
        }
    }
}
