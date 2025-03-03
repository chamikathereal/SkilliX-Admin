package com.skillix.admin;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.navigation.NavigationView;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawerLayout1), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


        DrawerLayout drawerLayout1 = findViewById(R.id.drawerLayout1);
        Toolbar toolbar1 = findViewById(R.id.toolbar1);
        NavigationView navigationView1 = findViewById(R.id.navigationView1);

        View headerView = navigationView1.getHeaderView(0);

        TextView headerTextView1 = headerView.findViewById(R.id.headerTextView1);
        TextView headerTextView2 = headerView.findViewById(R.id.headerTextView2);


        headerTextView1.setText("Admin");
        headerTextView2.setText("admin@gmail.com");

        ShapeableImageView userHeaderImage = headerView.findViewById(R.id.userHeaderImage);
        userHeaderImage.setImageResource(R.drawable.profile);

        if (savedInstanceState == null) {


            loadFragment(new HomeFragment());  // Load MenteeHomeFragment if user is a mentee
            navigationView1.setCheckedItem(R.id.adminHomeFragment);


        }

        navigationView1.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                if (item.getItemId() == R.id.adminHomeFragment) {
                    loadFragment(new HomeFragment());
                } else if (item.getItemId() == R.id.adminViewMentorFragment) {
                    loadFragment(new ViewMentorFragment());
                }else if (item.getItemId() == R.id.adminViewMenteeFragment) {
                    loadFragment(new ViewMenteeFragment());
                }else if (item.getItemId() == R.id.subscriptionPlantFragment) {
                    loadFragment(new SubscriptionPlanFragment());
                }else if (item.getItemId() == R.id.termsAndCondition) {
                    loadFragment(new TermsAndConditionsFragment());
                }
                toolbar1.setTitle(item.getTitle());
                drawerLayout1.closeDrawers();

                return true;
            }
        });

    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainerView1, fragment, null)
                .setReorderingAllowed(true)
                .commit();
    }

}