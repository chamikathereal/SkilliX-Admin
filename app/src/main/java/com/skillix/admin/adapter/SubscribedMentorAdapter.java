package com.skillix.admin.adapter;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.skillix.admin.R;
import com.skillix.admin.ViewMenteesPurchasedSubscriptionDetailsFragment;
import com.skillix.admin.model.SubscribedMentor;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class SubscribedMentorAdapter extends RecyclerView.Adapter<SubscribedMentorAdapter.SubscribedMentorViewHolder> {

    ArrayList<SubscribedMentor> hiredMenteeArrayList;

    private Context context;
    public SubscribedMentorAdapter(Context context, ArrayList<SubscribedMentor> hiredMenteeArrayList) {
        this.context = context;
        this.hiredMenteeArrayList = hiredMenteeArrayList;
    }

    @NonNull
    @Override
    public SubscribedMentorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View inflatedView = layoutInflater.inflate(com.skillix.admin.R.layout.subscribed_mentor_view, parent, false);

        SubscribedMentorViewHolder hiredMenteeViewHolder = new SubscribedMentorViewHolder(inflatedView);

        return hiredMenteeViewHolder;

    }

    @Override
    public void onBindViewHolder(@NonNull SubscribedMentorViewHolder holder, int position) {

        SubscribedMentor subscribedMentor = hiredMenteeArrayList.get(position);
        final String mentorId = subscribedMentor.getMenteeId();
        final String firstName = subscribedMentor.getFirstName();
        final String lastName = subscribedMentor.getLastName();
        final String career = subscribedMentor.getCareer();
        final String packageName = subscribedMentor.getPackageName();

        final String menteeFullName = firstName + " " + lastName;

        String profileImgURL = subscribedMentor.getProfileImgURL();
        Uri mentorImg = null;

        if (profileImgURL != null && !profileImgURL.isEmpty()) {
            mentorImg = Uri.parse(profileImgURL);

            Picasso.get()
                    .load(mentorImg)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.menteeProfile);
        } else {
            Log.w("SubscribedMentorAdapter", "Profile image URL is null or empty for " + menteeFullName);
            Picasso.get()
                    .load(R.drawable.profile)
                    .placeholder(R.drawable.profile)
                    .error(R.drawable.profile)
                    .into(holder.menteeProfile);

        }

        holder.menteeName.setText(menteeFullName);
        holder.menteeCareer.setText(career);
        holder.hiredMenteePackageNameTextView.setText(packageName);

        holder.seeMoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("SkilliXAdminLog", "MenteeID: " + mentorId);

                ViewMenteesPurchasedSubscriptionDetailsFragment dialogFragment = new ViewMenteesPurchasedSubscriptionDetailsFragment();

                Bundle args = new Bundle();
                args.putString("mentorId", mentorId);
                dialogFragment.setArguments(args);

                FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
                dialogFragment.show(fragmentManager, "ViewMentorSubscriptionDetails");

            }
        });



    }

    @Override
    public int getItemCount() {
        return hiredMenteeArrayList.size();
    }

    static class SubscribedMentorViewHolder extends RecyclerView.ViewHolder {

        ShapeableImageView menteeProfile;
        TextView menteeName, menteeCareer,hiredMenteePackageNameTextView;
        Button seeMoreButton;

        public SubscribedMentorViewHolder(@NonNull View itemView) {
            super(itemView);

            menteeProfile = itemView.findViewById(R.id.hiredMenteeProImageView);
            menteeName = itemView.findViewById(R.id.hiredMenteeNameTextView);
            menteeCareer = itemView.findViewById(R.id.hiredMenteeCategoryTextView);
            seeMoreButton = itemView.findViewById(R.id.hiredMenteeProfileButton);
            hiredMenteePackageNameTextView = itemView.findViewById(R.id.hiredMenteePackageNameTextView);
        }

    }
}
