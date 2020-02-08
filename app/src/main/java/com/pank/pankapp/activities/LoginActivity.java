package com.pank.pankapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.components.PasswordViewWithEye;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.dialog.ForgetPasswordDialog;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.util.GlobalUtil;
import com.pank.pankapp.util.ObjectUtil;

import static com.pank.pankapp.util.Constants.IS_LOGGED_IN;
import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;
import static com.pank.pankapp.util.Constants.USER_FIRST_NAME;
import static com.pank.pankapp.util.Constants.USER_ID;
import static com.pank.pankapp.util.Constants.USER_LAST_NAME;
import static com.pank.pankapp.util.Constants.USER_MOBILE;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText et_email_id;
    private PasswordViewWithEye et_password;
    private TextView tvForgetPwd;
    private Button btnSignin;
    private LinearLayout layoutRegister;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private GlobalProgressDialog progressDialog;
    private SessionSecuredPreferences loginPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(LoginActivity.this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.tvForgetPwd = this.findViewById(R.id.tvForgetPwd);
        this.btnSignin = this.findViewById(R.id.btnSignin);
        this.layoutRegister = this.findViewById(R.id.layoutRegister);

        // Initialize Firebase Auth
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
    }

    private void setOnClickListener() {
        this.tvForgetPwd.setOnClickListener(this);
        this.btnSignin.setOnClickListener(this);
        this.layoutRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvForgetPwd:
                ForgetPasswordDialog passwordDialog = new ForgetPasswordDialog(LoginActivity.this, true, false);
                passwordDialog.show();
                break;
            case R.id.btnSignin:
                if (this.isValidLogin()) {
                    this.loginUser();
                }
                break;
            case R.id.layoutRegister:
                this.startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                Animatoo.animateSwipeLeft(LoginActivity.this);
                break;
        }
    }

    private void loginUser() {
        if (!ObjectUtil.isEmpty(firebaseAuth)) {
            progressDialog.showProgressBar();
            firebaseAuth.signInWithEmailAndPassword(ObjectUtil.getTextFromView(et_email_id), ObjectUtil.getTextFromView(et_password))
                    .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {

                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (!ObjectUtil.isEmpty(user)) {
                                    //TODO save isLogged flag and passenger info id into shared preference
                                    loginPreferences.edit().putBoolean(IS_LOGGED_IN, true).apply();
                                    loginPreferences.edit().putString(USER_ID, user.getUid()).apply();

                                    firebaseDatabase.getReference("NewUser").child(user.getUid()).addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            progressDialog.hideProgressBar();
                                            EOSignUpUser signUpUser = dataSnapshot.getValue(EOSignUpUser.class);
                                            if (!ObjectUtil.isEmpty(signUpUser)) {
                                                loginPreferences.edit().putString(USER_FIRST_NAME, signUpUser.getfName()).apply();
                                                loginPreferences.edit().putString(USER_LAST_NAME, signUpUser.getlName()).apply();
                                                loginPreferences.edit().putString(USER_MOBILE, signUpUser.getPhoneNumber()).apply();

                                                //TODO open main activity from here
                                                Toast.makeText(LoginActivity.this, "Login Successfully.", Toast.LENGTH_SHORT).show();
                                                Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                                                //mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                LoginActivity.this.startActivity(mainIntent);
                                                LoginActivity.this.finish();
                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {
                                            Toast.makeText(LoginActivity.this, "Failed to read value.", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            } else {
                                // If sign in fails, display a message to the user.
                                progressDialog.hideProgressBar();
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

    }

    private boolean isValidLogin() {
        String errorMsg = null;

        String emailId = ObjectUtil.getTextFromView(et_email_id);
        String password = ObjectUtil.getTextFromView(et_password);

        if (ObjectUtil.isEmptyStr(emailId) || ObjectUtil.isEmptyStr(password)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (password.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            new GlobalAlertDialog(LoginActivity.this, false, true).show(errorMsg);
            return false;
        }
        return true;
    }

}
