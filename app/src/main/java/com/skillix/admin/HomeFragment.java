package com.skillix.admin;

import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class HomeFragment extends Fragment {

    TextView menteeCountTextView, mentorCountTextView, onGoingServiceTextView, revenueTextView;
    FirebaseFirestore firestore;
    PieChart mentorCareerPieChart, menteerCareerPieChart;
    View revenueView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home, container, false);

        firestore = FirebaseFirestore.getInstance();

        menteeCountTextView = view.findViewById(R.id.menteeCountTextView);
        mentorCountTextView = view.findViewById(R.id.mentorCountTextView);
        onGoingServiceTextView = view.findViewById(R.id.onGoingServiceTextView);
        revenueTextView = view.findViewById(R.id.revenueTextView);
        mentorCareerPieChart = view.findViewById(R.id.mentorCareerPieChart);
        menteerCareerPieChart = view.findViewById(R.id.menteerCareerPieChart);
        revenueView = view.findViewById(R.id.revenueView);

        loadMenteeCount();
        loadMentorCount();
        loadOnGoingServiceCount();
        loadRevenue();

        loadMentorCareerPieChart();
        loadMenteeCareerPieChart();

        revenueView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView1, new SubscribedMentorsFragment(), null);
                fragmentTransaction.addToBackStack(null); // Optional: Enables back navigation
                fragmentTransaction.commit();

            }
        });

        return view;
    }

    private void loadMenteeCount() {
        firestore.collection("users")
                .whereEqualTo("account_type", "Mentee")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error if needed
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            int menteeCount = queryDocumentSnapshots.size();
                            menteeCountTextView.setText(String.valueOf(menteeCount));
                        }
                    }
                });
    }

    private void loadMentorCount() {
        firestore.collection("users")
                .whereEqualTo("account_type", "Mentor")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error if needed
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            int mentorCount = queryDocumentSnapshots.size();
                            mentorCountTextView.setText(String.valueOf(mentorCount));
                        }
                    }
                });
    }

    private void loadOnGoingServiceCount() {
        firestore.collection("mentor_hired")
                .whereEqualTo("status", "onGoing")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error if needed
                            return;
                        }

                        if (queryDocumentSnapshots != null) {
                            int onGoingServices = queryDocumentSnapshots.size();
                            onGoingServiceTextView.setText(String.valueOf(onGoingServices));
                        }
                    }
                });
    }

    private void loadRevenue() {
        firestore.collection("subscription_payments")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("SkilliXAdminLog", "Listen failed.", e);
                            return;
                        }

                        // Wrap totalRevenue in an array to make it effectively final
                        final double[] totalRevenue = {0.0};  // Initialize total revenue as a double array

                        if (querySnapshot != null) {
                            // Loop through all subscription payments
                            for (DocumentSnapshot document : querySnapshot) {

                                String subscription_payments = document.getId();
                                String subscription_id = document.getString("subscription_id");
                                Log.i("SkilliXAdminLog", "subscription_id: " + subscription_id);

                                firestore.collection("mentor_subscription").document(subscription_id)
                                        .get()
                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    String priceStr = document.getString("price");

                                                    // If price exists and is valid
                                                    if (priceStr != null) {
                                                        try {
                                                            double price = Double.parseDouble(priceStr);  // Parse the price as a double
                                                            totalRevenue[0] += price;  // Add it to the total revenue
                                                            Log.i("SkilliXAdminLog", "price: " + priceStr);
                                                        } catch (NumberFormatException e) {
                                                            Log.e("SkilliXAdminLog", "Invalid price format: " + priceStr);
                                                        }
                                                    }

                                                    // Log total revenue after processing each document
                                                    Log.i("SkilliXAdminLog", "Total Revenue: " + totalRevenue[0]);

                                                    // Convert totalRevenue to a String and update the TextView
                                                    String totalRevenueStr = String.format("%.2f", totalRevenue[0]);
                                                    revenueTextView.setText(totalRevenueStr);

                                                }
                                            }
                                        });
                            }
                        }
                    }
                });
    }


    private void loadMentorCareerPieChart() {
        firestore.collection("users")
                .whereEqualTo("account_type", "Mentor")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            // Handle error if needed
                            return;
                        }

                        if (documentSnapshots != null) {
                            Map<String, Integer> careerCounts = new HashMap<>();

                            for (DocumentSnapshot document : documentSnapshots) {
                                String career = document.getString("career");
                                if (career != null) {
                                    careerCounts.put(career, careerCounts.containsKey(career) ? careerCounts.get(career) + 1 : 1);
                                }
                            }

                            // Prepare Pie Entry Data
                            ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();
                            ArrayList<Integer> colorArrayList = new ArrayList<>();

                            for (Map.Entry<String, Integer> entry : careerCounts.entrySet()) {
                                String career = entry.getKey();
                                int count = entry.getValue();
                                pieEntryArrayList.add(new PieEntry((float) count, career));

                                Random random = new Random();
                                int color = Color.rgb(
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(256)
                                );
                                colorArrayList.add(color);
                            }

                            PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "Career Distribution");
                            pieDataSet.setColors(colorArrayList);


                            PieData pieData = new PieData(pieDataSet);
                            pieData.setValueTextSize(14);

                            mentorCareerPieChart.setData(pieData);
                            mentorCareerPieChart.animateY(2000, Easing.EaseInCirc);

                            mentorCareerPieChart.setCenterText("Career Distribution");

                            if (isAdded()) {
                                mentorCareerPieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.color_accent));
                            }

                            mentorCareerPieChart.setCenterTextSize(14);
                            mentorCareerPieChart.setDescription(null);

                            mentorCareerPieChart.invalidate();


                            // If you have a TextView to display the text data:
                            StringBuilder sb = new StringBuilder();
                            for (Map.Entry<String, Integer> entry : careerCounts.entrySet()) {
                                String career = entry.getKey();
                                int count = entry.getValue();
                                sb.append("Count: ").append(count).append(" Career: ").append(career).append("\n");
                            }
                            Log.i("SkilliXLog", "ccc:" + sb.toString());
                        }
                    }
                });
    }

    private void loadMenteeCareerPieChart() {
        firestore.collection("users")
                .whereEqualTo("account_type", "Mentee")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot documentSnapshots, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }

                        if (documentSnapshots != null) {
                            Map<String, Integer> careerCounts = new HashMap<>();

                            for (DocumentSnapshot document : documentSnapshots) {
                                String career = document.getString("career");
                                if (career != null) {
                                    careerCounts.put(career, careerCounts.containsKey(career) ? careerCounts.get(career) + 1 : 1);
                                }
                            }


                            ArrayList<PieEntry> pieEntryArrayList = new ArrayList<>();
                            ArrayList<Integer> colorArrayList = new ArrayList<>();

                            for (Map.Entry<String, Integer> entry : careerCounts.entrySet()) {
                                String career = entry.getKey();
                                int count = entry.getValue();
                                pieEntryArrayList.add(new PieEntry((float) count, career));

                                Random random = new Random();
                                int color = Color.rgb(
                                        random.nextInt(256),
                                        random.nextInt(256),
                                        random.nextInt(256)
                                );
                                colorArrayList.add(color);
                            }

                            PieDataSet pieDataSet = new PieDataSet(pieEntryArrayList, "Career Distribution");
                            pieDataSet.setColors(colorArrayList);


                            PieData pieData = new PieData(pieDataSet);
                            pieData.setValueTextSize(14);

                            menteerCareerPieChart.setData(pieData);
                            menteerCareerPieChart.animateY(2000, Easing.EaseInCirc);

                            menteerCareerPieChart.setCenterText("Career Distribution");

                            if (isAdded()) {
                                menteerCareerPieChart.setCenterTextColor(ContextCompat.getColor(requireContext(), R.color.color_accent));
                            }

                            menteerCareerPieChart.setCenterTextSize(16);
                            menteerCareerPieChart.setDescription(null);

                            menteerCareerPieChart.invalidate();


                            StringBuilder sb = new StringBuilder();
                            for (Map.Entry<String, Integer> entry : careerCounts.entrySet()) {
                                String career = entry.getKey();
                                int count = entry.getValue();
                                sb.append("Count: ").append(count).append(" Career: ").append(career).append("\n");
                            }
                            Log.i("SkilliXLog", "career data: " + sb.toString());
                        }
                    }
                });
    }


}