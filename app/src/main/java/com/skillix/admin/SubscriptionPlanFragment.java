package com.skillix.admin;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.skillix.admin.adapter.ServiceAdapter;
import com.skillix.admin.model.Service;

import java.util.ArrayList;


public class SubscriptionPlanFragment extends Fragment {

    private FirebaseFirestore firestore;
    private RecyclerView addedServiceRecyclerView;
    private ArrayList<Service> serviceList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_subscription_plan, container, false);

        firestore = FirebaseFirestore.getInstance();

        addedServiceRecyclerView = view.findViewById(R.id.addedServiceRecyclerView);
        addedServiceRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        serviceList = new ArrayList<>();
        loadServices();

        Button addServiceButton = view.findViewById(R.id.addServiceButton);
        addServiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                MentorServicesDialogFragment dialogFragment = new MentorServicesDialogFragment();
                dialogFragment.show(getParentFragmentManager(), "mentorServicesDialog");
            }
        });

        return view;
    }

    private void loadServices() {
        firestore.collection("mentor_subscription")// Optional: order by a field (e.g., "title")
                .addSnapshotListener((new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                        if (querySnapshot != null) {
                            serviceList.clear();


                            for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                                String id = document.getId();
                                String title = document.getString("title");
                                String duration = document.getString("duration");
                                String price = document.getString("price");
                                String description = document.getString("description");


                                Service service = new Service(id, title,duration, price, description);
                                serviceList.add(service);
                            }


                            ServiceAdapter serviceAdapter = new ServiceAdapter(getActivity(), serviceList);
                            addedServiceRecyclerView.setAdapter(serviceAdapter);
                        }
                    }
                }));
    }
}