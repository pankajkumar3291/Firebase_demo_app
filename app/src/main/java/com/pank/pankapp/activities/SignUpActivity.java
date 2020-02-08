package com.pank.pankapp.activities;

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
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.components.PasswordViewWithEye;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.util.GlobalUtil;
import com.pank.pankapp.util.ObjectUtil;

import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText et_first_name, et_last_name, et_email_id, et_phone_number;
    private PasswordViewWithEye et_password, et_confirm_password;
    private ImageView iv_back;
    private Button btnSignUp;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private GlobalProgressDialog progressDialog;
    private SessionSecuredPreferences loginPreferences;
    private String firstName;
    private String lastName;
    private String emailId;
    private String phoneNumber;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_signup);

        this.initView();
        this.setOnClickListener();
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(SignUpActivity.this);
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_password = this.findViewById(R.id.et_password);
        this.et_confirm_password = this.findViewById(R.id.et_confirm_password);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.btnSignUp = this.findViewById(R.id.btnSignUp);
        //this.et_phone_number.addTextChangedListener(new PhoneNumberFormatter(et_phone_number, "+## ###-###-####"));

        // Initialize Firebase Auth
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.databaseReference = this.firebaseDatabase.getReference("NewUser");
    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        this.btnSignUp.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        if (!ObjectUtil.isEmpty(this.firebaseAuth)) {
            FirebaseUser currentUser = this.firebaseAuth.getCurrentUser();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                this.finish();
                Animatoo.animateSwipeRight(SignUpActivity.this);
                break;
            case R.id.btnSignUp:
                if (isValidRegistration()) {
                    registerNewUser();
                }
                break;
        }
    }

    private void registerNewUser() {
        if (!ObjectUtil.isEmpty(this.firebaseAuth)) {
            progressDialog.showProgressBar();
            this.firebaseAuth.createUserWithEmailAndPassword(this.emailId, this.password)
                    .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                FirebaseUser user = firebaseAuth.getCurrentUser();
                                if (!ObjectUtil.isEmpty(user)) {
                                    EOSignUpUser signUpUser = new EOSignUpUser(user.getUid(), firstName, lastName, emailId, phoneNumber, "", "");
                                    databaseReference.child(user.getUid())
                                            .setValue(signUpUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.hideProgressBar();
                                                Toast.makeText(SignUpActivity.this, "New User Register Successfully.", Toast.LENGTH_SHORT).show();
                                                Intent loginIntent = new Intent(SignUpActivity.this, LoginActivity.class);
                                                //loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                startActivity(loginIntent);
                                                SignUpActivity.this.finish();
                                            } else {
                                                progressDialog.hideProgressBar();
                                                Toast.makeText(SignUpActivity.this, "Unable to register new user.", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            } else {
                                progressDialog.hideProgressBar();
                                // If sign in fails, display a message to the user.
                                Toast.makeText(SignUpActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
        }
    }

    private boolean isValidRegistration() {
        String errorMsg = null;

        this.firstName = ObjectUtil.getTextFromView(et_first_name);
        this.lastName = ObjectUtil.getTextFromView(et_last_name);
        this.emailId = ObjectUtil.getTextFromView(et_email_id);
        this.phoneNumber = ObjectUtil.getTextFromView(et_phone_number);
        this.password = ObjectUtil.getTextFromView(et_password);
        String confirmPassword = ObjectUtil.getTextFromView(et_confirm_password);

        if (ObjectUtil.isEmptyStr(firstName) || ObjectUtil.isEmptyStr(lastName) || ObjectUtil.isEmptyStr(emailId)
                || ObjectUtil.isEmptyStr(phoneNumber) || ObjectUtil.isEmptyStr(password) || ObjectUtil.isEmptyStr(confirmPassword)) {
            errorMsg = this.getString(R.string.all_fields_required);
        } else if (!GlobalUtil.isValidEmail(emailId)) {
            errorMsg = this.getString(R.string.valid_email);
        } else if (phoneNumber.length() != 10) {
            errorMsg = this.getString(R.string.valid_phone_number);
        } else if (!password.equals(confirmPassword)) {
            errorMsg = this.getString(R.string.confirm_password_not_matched);
        } else if (password.length() < 6) {
            errorMsg = this.getString(R.string.password_min_character);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            new GlobalAlertDialog(SignUpActivity.this, false, true).show(errorMsg);
            return false;
        }
        return true;
    }


}
