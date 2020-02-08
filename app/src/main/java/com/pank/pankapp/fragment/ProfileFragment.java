package com.pank.pankapp.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pank.pankapp.R;
import com.pank.pankapp.activities.ChangePasswordActivity;
import com.pank.pankapp.activities.EditProfileActivity;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment implements View.OnClickListener {

    private View view;
    private TextInputEditText et_user_name, et_email_id, et_gender, et_dob, et_phone_number;
    private Button btnChangePassword, btnEditProfile;
    private CircleImageView ivUserPic;
    private GlobalProgressDialog progressDialog;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private EOSignUpUser signUpUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_profile, container, false);

        this.initView();
        this.setOnCLickListener();

        return this.view;
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(getActivity());
        this.et_user_name = this.view.findViewById(R.id.et_user_name);
        this.et_email_id = this.view.findViewById(R.id.et_email_id);
        this.et_gender = this.view.findViewById(R.id.et_gender);
        this.et_dob = this.view.findViewById(R.id.et_dob);
        this.et_phone_number = this.view.findViewById(R.id.et_phone_number);
        this.btnChangePassword = this.view.findViewById(R.id.btnChangePassword);
        this.btnEditProfile = this.view.findViewById(R.id.btnEditProfile);
        this.ivUserPic = this.view.findViewById(R.id.ivUserPic);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();

    }

    private void setOnCLickListener() {
        this.btnChangePassword.setOnClickListener(this);
        this.btnEditProfile.setOnClickListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.getProfileUser();
    }

    private void getProfileUser() {
        FirebaseUser firebaseUser = this.firebaseAuth.getCurrentUser();
        if (!ObjectUtil.isEmpty(this.firebaseDatabase) && !ObjectUtil.isEmpty(firebaseUser)) {
            progressDialog.showProgressBar();
            firebaseDatabase.getReference("NewUser").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    signUpUser = dataSnapshot.getValue(EOSignUpUser.class);
                    if (!ObjectUtil.isEmpty(signUpUser)) {
                        progressDialog.hideProgressBar();

                        if (!ObjectUtil.isEmpty(firebaseUser.getPhotoUrl()))
                            Picasso.get().load(firebaseUser.getPhotoUrl()).error(R.drawable.ic_user_circle).fit().into(ivUserPic);

                        et_user_name.setText(signUpUser.getfName().concat(" ").concat(signUpUser.getlName()));
                        et_email_id.setText(signUpUser.getEmail());
                        et_phone_number.setText(signUpUser.getPhoneNumber());
                        et_gender.setText(signUpUser.getGender());
                        et_dob.setText(signUpUser.getDateOfBirth());
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.hideProgressBar();
                    Toast.makeText(getActivity(), "Failed to read value.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnChangePassword:
                this.startActivity(new Intent(getActivity(), ChangePasswordActivity.class));
                Animatoo.animateSwipeLeft(Objects.requireNonNull(getActivity()));
                break;
            case R.id.btnEditProfile:
                Intent editProfileIntent = new Intent(getActivity(), EditProfileActivity.class);
                if (!ObjectUtil.isEmpty(this.signUpUser))
                    editProfileIntent.putExtra("profileData", this.signUpUser);
                this.startActivity(editProfileIntent);
                Animatoo.animateSwipeLeft(Objects.requireNonNull(getActivity()));
                break;
        }
    }


}
