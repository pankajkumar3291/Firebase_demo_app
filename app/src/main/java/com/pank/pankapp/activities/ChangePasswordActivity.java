package com.pank.pankapp.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.components.PasswordViewWithEye;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.util.ObjectUtil;

import java.util.Objects;

import static com.pank.pankapp.util.Constants.IS_LOGGED_IN;
import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;

@SuppressLint("Registered")
public class ChangePasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_back;
    private PasswordViewWithEye et_old_password, et_new_password, et_confirm_password;
    private Button btnUpdate;
    private GlobalProgressDialog progressDialog;
    private SessionSecuredPreferences loginPreferences;
    private String oldPassword;
    private String newPassword;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.et_old_password = this.findViewById(R.id.et_old_password);
        this.et_new_password = this.findViewById(R.id.et_new_password);
        this.et_confirm_password = this.findViewById(R.id.et_confirm_password);
        this.btnUpdate = this.findViewById(R.id.btnUpdate);

        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        this.btnUpdate.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                ChangePasswordActivity.this.finish();
                Animatoo.animateSwipeRight(ChangePasswordActivity.this);
                break;
            case R.id.btnUpdate:
                if (this.isAllFieldsValid())
                    this.changePassword();
                break;
        }
    }

    private void changePassword() {
        if (!ObjectUtil.isEmpty(this.firebaseUser)) {
            progressDialog.showProgressBar();
            final String userEmail = this.firebaseUser.getEmail();
            AuthCredential authCredential = EmailAuthProvider.getCredential(Objects.requireNonNull(userEmail), this.oldPassword);
            this.firebaseUser.reauthenticate(authCredential).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        firebaseUser.updatePassword(newPassword).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    progressDialog.hideProgressBar();

                                    FirebaseAuth.getInstance().signOut();
                                    //todo this listener will be called when there is change in firebase user session
                                    FirebaseAuth.getInstance().addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                                        @Override
                                        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                                            FirebaseUser user = firebaseAuth.getCurrentUser();
                                            if (ObjectUtil.isEmpty(user)) {
                                                //TODO when user is logout out from app then clear shared preferences
                                                if (loginPreferences.contains(IS_LOGGED_IN))
                                                    loginPreferences.edit().clear().apply();
                                                Toast.makeText(ChangePasswordActivity.this, "Password Successfully updated.", Toast.LENGTH_SHORT).show();
                                                ChangePasswordActivity.this.finish();
                                                Intent loginIntent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                                                ChangePasswordActivity.this.startActivity(loginIntent);
                                                Animatoo.animateSwipeRight(ChangePasswordActivity.this);
                                                //TODO here finish mainActivity because it is opened while move on login activity
                                                MainActivity.mainActivity.get().finish();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.hideProgressBar();
                                    Toast.makeText(ChangePasswordActivity.this, "Failed to update password.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        progressDialog.hideProgressBar();
                        Toast.makeText(ChangePasswordActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isAllFieldsValid() {
        String errorMsg = null;
        this.oldPassword = ObjectUtil.getTextFromView(et_old_password);
        this.newPassword = ObjectUtil.getTextFromView(et_new_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(oldPassword) || ObjectUtil.isEmptyStr(newPassword) || ObjectUtil.isEmptyStr(confirmPassword)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (oldPassword.length() < 6 || newPassword.length() < 6 || confirmPassword.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        } else if (!newPassword.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            new GlobalAlertDialog(ChangePasswordActivity.this, false, true).show(errorMsg);
            return false;
        }
        return true;
    }
}
