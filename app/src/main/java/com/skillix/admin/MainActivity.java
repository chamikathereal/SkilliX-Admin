package com.skillix.admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

public class MainActivity extends AppCompatActivity {

    EditText userEmailEditText, signInEditTextPassword;
    FirebaseFirestore firestore;
    SharedPreferences sharedPreferences;

    private static final String PREFERENCES_NAME = "AdminPreferences";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        userEmailEditText = findViewById(R.id.userEmailEditText);
        signInEditTextPassword = findViewById(R.id.signInEditTextPassword);
        Button adminSignInButton = findViewById(R.id.adminSignInButton);


        firestore = FirebaseFirestore.getInstance();

        sharedPreferences = getSharedPreferences(PREFERENCES_NAME, MODE_PRIVATE);


        if (isLoggedIn()) {

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

        adminSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = userEmailEditText.getText().toString();
                String password = signInEditTextPassword.getText().toString();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Please enter both email and password.", Toast.LENGTH_SHORT).show();
                } else {

                    checkUserCredentials(email, password);
                }
            }
        });
    }

    private void checkUserCredentials(String email, String password) {
        // Query Firestore for users matching the entered email
        firestore.collection("admin")
                .whereEqualTo("email", email)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && !task.getResult().isEmpty()) {

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String storedPassword = document.getString("password");

                            if (storedPassword != null && storedPassword.equals(password)) {

                                saveLoginState(email, password);
                                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            } else {

                                Toast.makeText(MainActivity.this, "Invalid password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {

                        Toast.makeText(MainActivity.this, "No such user found.", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void saveLoginState(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();
    }


    private boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }
}
