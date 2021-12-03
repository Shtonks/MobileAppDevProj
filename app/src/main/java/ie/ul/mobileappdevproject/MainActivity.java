package ie.ul.mobileappdevproject;


import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    FirebaseAuth auth;

    // closes sidebar when back button is pressed.
    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
    //opens fragments using the sidebar and closes the sidebar
    public void displayView(int viewId) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_home:
                fragment = new BlankFragment();
                title = "Home";

                break;
            case R.id.nav_maps:
                fragment = new MapsFragment();
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, fragment)
                        .commit();
                title = "Maps";
                break;
            case R.id.nav_todo:
                fragment = new ThirdFragment();
                title = "ToDo";
                break;
        }

        if (fragment != null) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_frame, fragment);
            ft.commit();
        }

        // set the toolbar title to whatever the title of the fragment is
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
//closes drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        displayView(item.getItemId());
        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);


        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.toolbar);


        navigationView.bringToFront();
        //allows sidebar to toggle
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setCheckedItem(R.id.nav_home);
        //displays home on start up
        displayView(R.id.nav_home);

        auth= FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = auth.getCurrentUser();
        if (user == null){
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
        }
    }
}