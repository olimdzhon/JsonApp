package tj.sodiq.jsonapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;

import com.apollographql.apollo.ApolloCall;
import com.apollographql.apollo.api.Response;
import com.apollographql.apollo.exception.ApolloException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.Date;

import tj.sodiq.jsonapp.fragments.FragmentAdminPanel;
import tj.sodiq.jsonapp.fragments.FragmentAppointments;
import tj.sodiq.jsonapp.fragments.FragmentCalendar;
import tj.sodiq.jsonapp.fragments.FragmentClinics;
import tj.sodiq.jsonapp.fragments.FragmentCommunities;
import tj.sodiq.jsonapp.fragments.FragmentConciliums;
import tj.sodiq.jsonapp.fragments.FragmentEnter;
import tj.sodiq.jsonapp.fragments.FragmentEvents;

import tj.sodiq.jsonapp.fragments.FragmentFavorites;
import tj.sodiq.jsonapp.fragments.FragmentFeed;
import tj.sodiq.jsonapp.fragments.FragmentMedicPosts;
import tj.sodiq.jsonapp.fragments.FragmentNotificationPage;
import tj.sodiq.jsonapp.fragments.FragmentPapers;
import tj.sodiq.jsonapp.fragments.FragmentPatientPosts;
import tj.sodiq.jsonapp.fragments.FragmentPatients;
import tj.sodiq.jsonapp.fragments.FragmentProfile;
import tj.sodiq.jsonapp.fragments.FragmentProtocols;
import tj.sodiq.jsonapp.fragments.FragmentQuestions;
import tj.sodiq.jsonapp.fragments.FragmentSend;
import tj.sodiq.jsonapp.fragments.FragmentSettings;
import tj.sodiq.jsonapp.fragments.FragmentShare;
import tj.sodiq.jsonapp.fragments.FragmentSpecializations;
import tj.sodiq.jsonapp.services.SubscriptionService;
import tj.sodiq.jsonapp.type.UserProfileType;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    FragmentMessageUsers fragmentMessageUsers;
    FragmentProfile fragmentProfile;
    FragmentSend fragmentSend;
    FragmentShare fragmentShare;
    FragmentNotificationPage fragmentNotificationPage;
    FragmentAppointments fragmentAppointments;
    FragmentPatients fragmentPatients;
    FragmentCommunities fragmentCommunities;
    FragmentEvents fragmentEvents;
    FragmentCalendar fragmentCalendar;
    FragmentPatientPosts fragmentPatientPosts;
    FragmentMedicPosts fragmentMedicPosts;
    FragmentConciliums fragmentConciliums;
    FragmentQuestions fragmentQuestions;
    FragmentSpecializations fragmentSpecializations;
    FragmentPapers fragmentPapers;
    FragmentProtocols fragmentProtocols;
    FragmentFavorites fragmentFavorites;
    FragmentEnter fragmentEnter;
    FragmentClinics fragmentClinics;
    NavigationView navigationView;
    FragmentTransaction ft;
    FragmentSettings fragmentSettings;
    FragmentAdminPanel fragmentAdminPanel;
    FragmentFeed fragmentFeed;

    AuthorizationQueryMutation.SessionUser sessionUser1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        fragmentMessageUsers = new FragmentMessageUsers();
        fragmentProfile = new FragmentProfile();
        fragmentSend = new FragmentSend();
        fragmentShare = new FragmentShare();
        fragmentNotificationPage = new FragmentNotificationPage();
        fragmentAppointments = new FragmentAppointments();
        fragmentPatients = new FragmentPatients();
        fragmentCommunities = new FragmentCommunities();
        fragmentEvents = new FragmentEvents();
        fragmentCalendar = new FragmentCalendar();
        fragmentPatientPosts = new FragmentPatientPosts();
        fragmentMedicPosts = new FragmentMedicPosts();
        fragmentConciliums = new FragmentConciliums();
        fragmentQuestions = new FragmentQuestions();
        fragmentSpecializations = new FragmentSpecializations();
        fragmentPapers = new FragmentPapers();
        fragmentProtocols = new FragmentProtocols();
        fragmentFavorites = new FragmentFavorites();
        fragmentEnter = new FragmentEnter();
        fragmentClinics = new FragmentClinics();
        fragmentSettings = new FragmentSettings();
        fragmentAdminPanel = new FragmentAdminPanel();
        fragmentFeed = new FragmentFeed();

        SharedPreferences autorithationsPref = this.getSharedPreferences(this.getString(R.string.preference_file_key), MODE_PRIVATE);

        String __typename = autorithationsPref.getString("__typename", "No __typename");
        int userId = autorithationsPref.getInt("userId", 0);
        int sessionUserAccountId = autorithationsPref.getInt("sessionUserAccountId", 0);
        String profileType_rawValue = autorithationsPref.getString("profileType_rawValue", "No profileType_rawValue");
        Double lastActiveTime = Double.valueOf(autorithationsPref.getInt("lastActiveTime", 0));
        Boolean isLongSession = autorithationsPref.getBoolean("isLongSession", false);
        String name = autorithationsPref.getString("name", "No name");
        String identicon = autorithationsPref.getString("identicon", "No identicon");
        Boolean isModerator = autorithationsPref.getBoolean("isModerator", false);
        Boolean isDeveloper = autorithationsPref.getBoolean("isDeveloper", false);
        Boolean isVerified = autorithationsPref.getBoolean("isVerified", false);
        Date lastVerificationRequest = new Date(autorithationsPref.getLong("lastVerificationRequest", 0));
        String photo__typename = autorithationsPref.getString("photo__typename", "No photo__typename");
        int photoId = autorithationsPref.getInt("photoId", 0);
        String photoFilename = autorithationsPref.getString("photoFilename", "No photoFilename");

        AuthorizationQueryMutation.Photo photo = new AuthorizationQueryMutation.Photo(photo__typename, photoId, photoFilename);

        UserProfileType profileType = UserProfileType.safeValueOf(profileType_rawValue);

        sessionUser1 = new AuthorizationQueryMutation.SessionUser(__typename, userId, sessionUserAccountId, profileType, lastActiveTime,
                isLongSession, name, identicon, isModerator, isDeveloper, isVerified, lastVerificationRequest, photo);

        if (__typename.equals("No __typename")){
            updateNavBar(sessionUser1);

            exit();

            clearNavBar();
        } else {
            updateNavBar(sessionUser1);

            exit();

            showItem();
        }

        View header = navigationView.getHeaderView(0);
        ImageView navImg = header.findViewById(R.id.imageView);
        navImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragmentProfile);
                ft.commit();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });
        TextView navNam = header.findViewById(R.id.textView1);
        navNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.container, fragmentProfile);
                ft.commit();

                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        String type = getIntent().getStringExtra("From");
        if (type != null) {
            switch (type){
                case "notifyFrag":
                    ft = getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.container, fragmentProfile);
                    ft.commit();
                    break;

            }
        }

        Intent intent = new Intent(Main2Activity.this, SubscriptionService.class);
        startService(intent);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        ft = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_feed) {
            ft.replace(R.id.container, fragmentFeed);
        } else if (id == R.id.nav_message_users) {
            ft.replace(R.id.container, fragmentMessageUsers);
        } else if (id == R.id.nav_notification_page) {
            ft.replace(R.id.container, fragmentNotificationPage);
        } else if (id == R.id.nav_appointments) {
            ft.replace(R.id.container, fragmentAppointments);
        } else if (id == R.id.nav_patients) {
            ft.replace(R.id.container, fragmentPatients);
        } else if (id == R.id.nav_communities) {
            ft.replace(R.id.container, fragmentCommunities);
        } else if (id == R.id.nav_events) {
            ft.replace(R.id.container, fragmentEvents);
        } else if (id == R.id.nav_calendar) {
            ft.replace(R.id.container, fragmentCalendar);
        } else if (id == R.id.nav_patient_posts) {
            ft.replace(R.id.container, fragmentPatientPosts);
        } else if (id == R.id.nav_medic_posts) {
            ft.replace(R.id.container, fragmentMedicPosts);
        } else if (id == R.id.nav_conciliums) {
            ft.replace(R.id.container, fragmentConciliums);
        } else if (id == R.id.nav_questions) {
            ft.replace(R.id.container, fragmentQuestions);
        } else if (id == R.id.nav_specializations) {
            ft.replace(R.id.container, fragmentSpecializations);
        }else if (id == R.id.nav_clinics){
            ft.replace(R.id.container, fragmentClinics);
        } else if (id == R.id.nav_papers) {
            ft.replace(R.id.container, fragmentPapers);
        } else if (id == R.id.nav_protocols) {
            ft.replace(R.id.container, fragmentProtocols);
        } else if (id == R.id.nav_favorites) {
            ft.replace(R.id.container, fragmentFavorites);
        } else if (id == R.id.nav_settings) {
            ft.replace(R.id.container, fragmentSettings);
        }else  if (id == R.id.nav_adminPanel) {
            ft.replace(R.id.container, fragmentAdminPanel);

        } else if (id == R.id.nav_exit) {
            ft.replace(R.id.container, fragmentEnter);
            exit();
            logout(sessionUser1);
            clearNavBar();

        } else if (id == R.id.nav_share) {
            ft.replace(R.id.container, fragmentShare);
        } else if (id == R.id.nav_send) {
            ft.replace(R.id.container, fragmentSend);

        }
        ft.commit();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate  the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_patient_post_list, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String keyword) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(final String keyword) {

                for (int i = 0; i < fragmentPatientPosts.tabAdapter.fragmentTags.length; i++) {

                    String fragmentTag = fragmentPatientPosts.tabAdapter.fragmentTags[i];

                    PatientPostListFragment fragment = (PatientPostListFragment) fragmentPatientPosts.getChildFragmentManager().findFragmentByTag(fragmentTag);
                    fragment.patientPostFilterBuilder.keyword(keyword);
                    fragment.load(true);
                }

                return false;
            }

        });

        return true;
    }

    // Determines if Action bar item was selected. If true then do corresponding action.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_add:
                startActivity(new Intent(this, PatientPostAddActivity.class));
                break;

        }

        return super.onOptionsItemSelected(item);
    }

    public void logout(AuthorizationQueryMutation.SessionUser sessionUser) {

         AccountLogoutMutation logout = AccountLogoutMutation
                .builder()
                .build();

        MyApolloClient
                .getMyApolloClient(Main2Activity.this)
                .mutate(logout)
                .enqueue(new ApolloCall.Callback<AccountLogoutMutation.Data>() {
                    @Override
                    public void onResponse(@NotNull Response<AccountLogoutMutation.Data> response) {

                        SharedPreferences preferencesLogout = Main2Activity.this.getSharedPreferences(Main2Activity.this.getString(R.string.preference_file_key), MODE_PRIVATE);

                        if (preferencesLogout != null) {
                            SharedPreferences.Editor editor1 = preferencesLogout.edit();
                            editor1.clear();
                            editor1.apply();
                        }

                        String cookiesLogout = preferencesLogout.getString("cookie", "No cookie");
                        Log.e("cookiesLogout", cookiesLogout);
                    }

                    @Override
                    public void onFailure(@NotNull ApolloException e) {

                    }
                });

        sessionUser1 = new AuthorizationQueryMutation.SessionUser("", null, null, null, null,
                null,null,null,null,null,null,null,null);

    }

    public void showItem() {

        Menu menu = navigationView.getMenu();

        MenuItem nav_exit = menu.findItem(R.id.nav_exit);

        nav_exit.setTitle("Выход");

    }

    public void updateNavBar(AuthorizationQueryMutation.SessionUser sessionUser) {
        View header = navigationView.getHeaderView(0);

        ImageView navImageView = header.findViewById(R.id.imageView);
        if (sessionUser.photo() != null) {
            String urlSessionUser = sessionUser.photo().filename();
            Picasso.get().load("https://yakdu.tj/api/thumbnail?filename=" + urlSessionUser + "&type=attach-photo&w=920&h=280").into(navImageView);
        } else {
            navImageView.setImageResource(0);
        }

        TextView navName = header.findViewById(R.id.textView1);
        String userName = sessionUser.name();
        navName.setText(userName);

        showItem();
    }

    public void clearNavBar() {
        View header = navigationView.getHeaderView(0);

        ImageView navImageView = header.findViewById(R.id.imageView);
        navImageView.setImageResource(0);

        TextView navName = header.findViewById(R.id.textView1);
        navName.setText("");
    }

    public void exit()
    {
        Menu menu = navigationView.getMenu();

        MenuItem nav_exit = menu.findItem(R.id.nav_exit);

        nav_exit.setTitle("Вход");
    }

}
