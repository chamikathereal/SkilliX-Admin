package com.skillix.admin;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.skillix.admin.adapter.MentorCardAdapter;
import com.skillix.admin.model.MentorCard;

import java.util.ArrayList;


public class ViewMenteeFragment extends Fragment {

    FirebaseFirestore firestore;
    ArrayList<MentorCard> mentorCardArrayList;
    MentorCardAdapter mentorCardAdapter;
    RecyclerView loadMentorCardRecyclerView;
    EditText searchMenteeTextText;
    Spinner loadCareerSpinner, loadCertifiedSpinner;
    String searchQuery = "";
    String selectedCareer = "Select Career";
    String selectedCertified = "Select Country";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_view_mentee, container, false);

        firestore = FirebaseFirestore.getInstance();

        loadMentorCardRecyclerView = view.findViewById(R.id.loadMenteeCardRecyclerView);
        searchMenteeTextText = view.findViewById(R.id.searchMenteeTextText);
        loadCareerSpinner = view.findViewById(R.id.loadCareerSpinner);
        loadCertifiedSpinner = view.findViewById(R.id.loadCertifiedSpinner);


        loadMentorCards();


        loadMentorCards();

        loadCareerSpinner();
        loadCertifiedSpinner();


        searchMenteeTextText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int start, int before, int count) {
                searchQuery = charSequence.toString().toLowerCase();
                loadMentorCards();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });


        loadCareerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCareer = loadCareerSpinner.getSelectedItem().toString();
                loadMentorCards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        loadCertifiedSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                selectedCertified = loadCertifiedSpinner.getSelectedItem().toString();
                loadMentorCards();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        return view;
    }

    private void loadCareerSpinner() {
        ArrayList<String> careerNames = new ArrayList<>();
        careerNames.add("Select Career");

        firestore.collection("career").orderBy("career_name").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                careerNames.add(document.getString("career_name"));
                            }

                            ArrayAdapter<String> menteeOrmentorArrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.country_spinner_item,
                                    careerNames
                            );

                            loadCareerSpinner.setAdapter(menteeOrmentorArrayAdapter);

                        } else {
                            Log.d("FirestoreAccountTypeLog", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadCertifiedSpinner() {
        ArrayList<String> certifiedCategory = new ArrayList<>();
        certifiedCategory.add("Select Country");

        firestore.collection("country").orderBy("country_name").get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                certifiedCategory.add(document.getString("country_name"));
                            }

                            ArrayAdapter<String> menteeOrmentorArrayAdapter = new ArrayAdapter<>(
                                    getContext(),
                                    R.layout.country_spinner_item,
                                    certifiedCategory
                            );

                            loadCertifiedSpinner.setAdapter(menteeOrmentorArrayAdapter);

                        } else {
                            Log.d("FirestoreAccountTypeLog", "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void loadMentorCards() {
        mentorCardArrayList = new ArrayList<>();
        mentorCardAdapter = new MentorCardAdapter(getActivity(), mentorCardArrayList);


        Query query = firestore.collection("users")
                .whereEqualTo("account_type", "Mentee");



        if (!selectedCareer.equals("Select Career")) {
            query = query.whereEqualTo("career", selectedCareer);
        }


        if (!selectedCertified.equals("Select Country")) {
            query = query.whereEqualTo("country", selectedCertified);
        }

        query.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        if (task.isSuccessful()) {

                            QuerySnapshot documentSnapshots = task.getResult();

                            mentorCardArrayList.clear();
                            for (DocumentSnapshot document : documentSnapshots) {

                                String mentorId = String.valueOf(document.getId());
                                String proIMG = String.valueOf(document.get("profile_image"));
                                String firstName = String.valueOf(document.get("first_name"));
                                String lastName = String.valueOf(document.get("last_name"));
                                String career = String.valueOf(document.get("career"));
                                String country = String.valueOf(document.get("country"));
                                String accountStatus = String.valueOf(document.get("account_status"));

                                if (firstName.toLowerCase().contains(searchQuery) || lastName.toLowerCase().contains(searchQuery)) {
                                    mentorCardArrayList.add(
                                            new MentorCard(mentorId, proIMG, firstName, lastName, career, country, accountStatus)
                                    );
                                }
                            }
                            mentorCardAdapter.notifyDataSetChanged();
                        }
                    }
                });

        loadMentorCardRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        loadMentorCardRecyclerView.setAdapter(mentorCardAdapter);

    }
}