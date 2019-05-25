package com.sihbar.netz;

import android.os.Bundle;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toolbar;

public class Home extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(this);


        loadFragment(new HomeFragment());
    }

    private boolean loadFragment(Fragment fragment){
        if(fragment != null){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        Fragment fragment = null;

        switch (menuItem.getItemId()){
            case R.id.navigation_home:
                fragment = new HomeFragment();
                break;
            case R.id.navigation_events:
                fragment = new EventsFragment();
                break;
            case R.id.navigation_contacts:
                fragment = new ContactsFragment();
                break;
            case R.id.navigation_profile:
                fragment = new ProfileFragment();
                break;
        }

        return loadFragment(fragment);
    }
}
