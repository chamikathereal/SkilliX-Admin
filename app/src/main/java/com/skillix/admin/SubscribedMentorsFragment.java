package com.skillix.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.skillix.admin.adapter.SubscribedMentorAdapter;
import com.skillix.admin.model.SubscribedMentor;

import java.util.ArrayList;


public class SubscribedMentorsFragment extends Fragment {
    FirebaseFirestore firestore;
    RecyclerView loadSubscribedMentoRecyclerView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
// Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_subscribed_mentors, container, false);
        firestore = FirebaseFirestore.getInstance();

        loadSubscribedMentoRecyclerView = view.findViewById(R.id.loadSubscribedMentoRecyclerView);

        loadSubscribedMentor();


        return view;
    }

    private void loadSubscribedMentor() {

        ArrayList<SubscribedMentor> subscribedMentorsArrayList = new ArrayList<>();
        SubscribedMentorAdapter hiredMenteeAdapter = new SubscribedMentorAdapter(getActivity(), subscribedMentorsArrayList);
        loadSubscribedMentoRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadSubscribedMentoRecyclerView.setAdapter(hiredMenteeAdapter);

        firestore.collection("users").whereEqualTo("account_type", "Mentor")
                .whereEqualTo("subscription_status", "active")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("SkilliXAdminLog", "Listen failed.", e);
                            return;
                        }

                        if (querySnapshot != null) {
                            subscribedMentorsArrayList.clear();
                            for (DocumentSnapshot document : querySnapshot) {
                                String mentorId = document.getId();

                                Log.i("SkilliXAdminLog", "mentorId: " + mentorId);

                                firestore.collection("users").document(mentorId)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot documentSnapshot = task.getResult();

                                                    if (documentSnapshot.exists()) {
                                                        String hiredmenteeId = documentSnapshot.getString("user_id");
                                                        String proImgURL = documentSnapshot.getString("profile_image");
                                                        String firstName = documentSnapshot.getString("first_name");
                                                        String lastName = documentSnapshot.getString("last_name");
                                                        String career = documentSnapshot.getString("career");

                                                        String fullName = firstName + " " + lastName;

                                                        Log.i("SkilliXAdminLog", "Hired mentees Data: " +
                                                                "proImgURL: " + proImgURL +
                                                                "fullName: " + fullName +
                                                                "career: " + career);

                                                        firestore.collection("subscription_payments").whereEqualTo("mentor_id", hiredmenteeId)
                                                                .get()
                                                                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                                                                        if (task.isSuccessful()){

                                                                            QuerySnapshot queryDocumentSnapshots = task.getResult();

                                                                            for (DocumentSnapshot document: queryDocumentSnapshots){

                                                                                String subscriptionId = document.getString("subscription_id");
                                                                                Log.i("SkilliXAdminLog", "subscriptionId: " + subscriptionId);

                                                                                firestore.collection("mentor_subscription").document(subscriptionId)
                                                                                        .get()
                                                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                                            @Override
                                                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                                                                if (task.isSuccessful()) {
                                                                                                    DocumentSnapshot subscriptionDocument = task.getResult();

                                                                                                    if (subscriptionDocument.exists()) {
                                                                                                        String subscriptionName = subscriptionDocument.getString("title");
                                                                                                        Log.i("SkilliXAdminLog", "subscriptionName: " + subscriptionName);

                                                                                                        // Add data to the ArrayList
                                                                                                        subscribedMentorsArrayList.add(new SubscribedMentor(
                                                                                                                hiredmenteeId,
                                                                                                                proImgURL,
                                                                                                                firstName,
                                                                                                                lastName,
                                                                                                                career,
                                                                                                                subscriptionName
                                                                                                        ));

                                                                                                        hiredMenteeAdapter.notifyDataSetChanged();

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
                                            }
                                        });
                            }
                        }
                    }
                });
    }


}