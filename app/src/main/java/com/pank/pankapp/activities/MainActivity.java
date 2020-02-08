package com.pank.pankapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.fragment.CommentsFragment;
import com.pank.pankapp.fragment.DashboardFragment;
import com.pank.pankapp.fragment.ProfileFragment;
import com.pank.pankapp.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pank.pankapp.util.Constants.IS_LOGGED_IN;
import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;
import static com.pank.pankapp.util.Constants.USER_FIRST_NAME;
import static com.pank.pankapp.util.Constants.USER_LAST_NAME;
import static com.pank.pankapp.util.Constants.USER_MOBILE;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private CircleImageView iv_circular_user_icon;
    private TextView tv_page_title, tvUserName, tvUserContact, tvHome, tvProfile, tvChat, tvLogout;
    boolean isClickedDoubleTap = false;
    private SessionSecuredPreferences loginPreferences;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    public static WeakReference<MainActivity> mainActivity;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        this.initView();
        this.setOnClickListener();
        this.dataToView();

        //TODO add dashboard fragment by default when open the app
        if (savedInstanceState == null) {
            addDashboardFragment(new DashboardFragment());
        }
    }

    private void initView() {
        mainActivity = new WeakReference<>(this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.firstName = this.loginPreferences.getString(USER_FIRST_NAME, "");
        this.lastName = this.loginPreferences.getString(USER_LAST_NAME, "");
        this.phoneNumber = this.loginPreferences.getString(USER_MOBILE, "");

        this.toolbar = this.findViewById(R.id.toolbar);
        this.drawerLayout = this.findViewById(R.id.drawerLayout);
        this.iv_circular_user_icon = this.findViewById(R.id.iv_circular_user_icon);
        this.tv_page_title = this.findViewById(R.id.tv_page_title);
        this.tvUserName = this.findViewById(R.id.tvUserName);
        this.tvUserContact = this.findViewById(R.id.tvUserContact);
        this.tvHome = this.findViewById(R.id.tvHome);
        this.tvProfile = this.findViewById(R.id.tvProfile);
        this.tvChat = this.findViewById(R.id.tvChat);
        this.tvLogout = this.findViewById(R.id.tvLogout);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void dataToView() {
        if (ObjectUtil.isNonEmptyStr(firstName) && ObjectUtil.isNonEmptyStr(lastName) && ObjectUtil.isNonEmptyStr(phoneNumber)) {
            this.tvUserName.setText(firstName.concat(" ").concat(lastName));
            this.tvUserContact.setText(phoneNumber);
        }
        if (!ObjectUtil.isEmpty(firebaseUser.getPhotoUrl()))
            Picasso.get().load(firebaseUser.getPhotoUrl()).error(R.drawable.ic_user_circle).fit().into(iv_circular_user_icon);
    }

    @SuppressLint("WrongConstant")
    private void setOnClickListener() {
        this.tvHome.setOnClickListener(this);
        this.tvChat.setOnClickListener(this);
        this.tvProfile.setOnClickListener(this);
        this.tvLogout.setOnClickListener(this);

        this.toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(Gravity.START));
    }

    @SuppressLint("WrongConstant")
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        this.toolbar = this.findViewById(R.id.toolbar);
        this.drawerLayout = this.findViewById(R.id.drawerLayout);
        this.iv_circular_user_icon = this.findViewById(R.id.iv_circular_user_icon);
        this.tv_page_title = this.findViewById(R.id.tv_page_title);
        this.tvUserName = this.findViewById(R.id.tvUserName);
        this.tvUserContact = this.findViewById(R.id.tvUserContact);
        this.tvHome = this.findViewById(R.id.tvHome);
        this.tvProfile = this.findViewById(R.id.tvProfile);
        this.tvChat = this.findViewById(R.id.tvChat);
        this.tvLogout = this.findViewById(R.id.tvLogout);

        this.tvHome.setOnClickListener(this);
        this.tvChat.setOnClickListener(this);
        this.tvProfile.setOnClickListener(this);
        this.tvLogout.setOnClickListener(this);

        this.toolbar.setNavigationOnClickListener(view -> drawerLayout.openDrawer(Gravity.START));


        super.onSaveInstanceState(outState);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvHome:
                if (!checkDashboardFragment()) {
                    this.tv_page_title.setText(R.string.dashboard_title);
                    this.replaceFragment(new DashboardFragment(), "dashboard");
                    this.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    this.drawerLayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.tvChat:
                Fragment fragment = getSupportFragmentManager().findFragmentByTag("chatFragment");
                if (fragment != null && fragment.isVisible()) {
                    this.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    this.tv_page_title.setText(R.string.chat);
                    this.replaceFragment(new CommentsFragment(), "chatFragment");
                    this.drawerLayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.tvProfile:
                Fragment profileFragment = getSupportFragmentManager().findFragmentByTag("profileFragment");
                if (profileFragment != null && profileFragment.isVisible()) {
                    this.drawerLayout.closeDrawer(Gravity.START);
                } else {
                    this.tv_page_title.setText(R.string.my_profile);
                    this.replaceFragment(new ProfileFragment(), "profileFragment");
                    this.drawerLayout.closeDrawer(Gravity.START);
                }
                break;
            case R.id.tvLogout:
                this.logoutFromApp();
                break;
        }
    }

    private void addDashboardFragment(DashboardFragment dashboardFragment) {
        this.replaceFragment(dashboardFragment, "dashboard");
    }

    public void replaceFragment(Fragment fragment, String tag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment, tag);
        fragmentTransaction.commitAllowingStateLoss();
    }

    private boolean checkDashboardFragment() {
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("dashboard");
        if (fragment != null && fragment.isVisible())
            return true;
        else
            return false;
    }

    @Override
    public void onBackPressed() {
        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            this.drawerLayout.closeDrawers();
        }

        if (isClickedDoubleTap) {
            super.onBackPressed();
            return;
        }

        if (checkDashboardFragment()) {
            this.isClickedDoubleTap = true;
            Snackbar snackbar = Snackbar.make(findViewById(R.id.fragmentContainer), "Please Click BACK again to exit!", Snackbar.LENGTH_SHORT);
            snackbar.show();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    isClickedDoubleTap = false;
                }
            }, 2000);
        } else {
            this.tv_page_title.setText(R.string.dashboard_title);
            replaceFragment(new DashboardFragment(), "dashboard");
        }
    }

    private void logoutFromApp() {
        new GlobalAlertDialog(this, true, false) {
            @SuppressLint("WrongConstant")
            @Override
            public void onConfirmation() {
                super.onConfirmation();

                FirebaseAuth.getInstance().signOut();
                // this listener will be called when there is change in firebase user session
                FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                    @Override
                    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        if (ObjectUtil.isEmpty(user)) {
                            //TODO when user is logout out from app then clear shared preferences
                            if (loginPreferences.contains(IS_LOGGED_IN)) {
                                loginPreferences.edit().clear().apply();
                                if (drawerLayout.isDrawerOpen(Gravity.END)) {
                                    drawerLayout.closeDrawer(Gravity.END);
                                }
                            }
                            //Toast.makeText(MainActivity.this, "User is logout from firebase.", Toast.LENGTH_SHORT).show();
                            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
                            MainActivity.this.startActivity(loginIntent);
                            MainActivity.this.finish();
                        }
                    }
                });
            }
        }.show(R.string.are_you_sure_you_want_to_logout);
    }

    @SuppressLint("WrongConstant")
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (drawerLayout.isDrawerOpen(Gravity.END)) {
            drawerLayout.closeDrawer(Gravity.END);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View drawerView, float slideOffset) {
            }

            @Override
            public void onDrawerOpened(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerClosed(@NonNull View drawerView) {
            }

            @Override
            public void onDrawerStateChanged(int newState) {
                dataToView();
            }
        });
    }


}
