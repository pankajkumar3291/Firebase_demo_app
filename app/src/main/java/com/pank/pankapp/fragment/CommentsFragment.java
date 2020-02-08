package com.pank.pankapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pank.pankapp.R;
import com.pank.pankapp.adapter.CommentsAdapter;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;
import static com.pank.pankapp.util.Constants.USER_ID;

public class CommentsFragment extends Fragment {

    private View view;
    private RecyclerView chatRecyclerView;
    private TextView tv_no_data;
    private GlobalProgressDialog progressDialog;
    private CommentsAdapter commentsAdapter;
    private List<EOSignUpUser> signUpUserList = new ArrayList<>();
    private DatabaseReference databaseReference;
    private SessionSecuredPreferences loginPreferences;
    private String savedUserId;
    private FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.view = inflater.inflate(R.layout.fragment_chat, container, false);

        this.initView();

        return this.view;
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(getActivity());
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.savedUserId = this.loginPreferences.getString(USER_ID, "");
        this.chatRecyclerView = this.view.findViewById(R.id.chatRecyclerView);
        this.tv_no_data = this.view.findViewById(R.id.tv_no_data);

        this.databaseReference = FirebaseDatabase.getInstance().getReference("NewUser");
        this.firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        this.chatRecyclerView.setHasFixedSize(true);
        this.commentsAdapter = new CommentsAdapter(getActivity(), signUpUserList);
        this.chatRecyclerView.setAdapter(commentsAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!ObjectUtil.isEmpty(signUpUserList))
            signUpUserList.clear();

        this.getAllUsersFromFirebase();
    }

    private void getAllUsersFromFirebase() {
        if (!ObjectUtil.isEmpty(databaseReference) && !ObjectUtil.isEmpty(this.firebaseUser) && ObjectUtil.isNonEmptyStr(this.savedUserId)) {
            progressDialog.showProgressBar();
            this.databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    progressDialog.hideProgressBar();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        EOSignUpUser signUpUser = snapshot.getValue(EOSignUpUser.class);

                        assert signUpUser != null;
                        if (!firebaseUser.getUid().equals(signUpUser.getUserId()))
                            signUpUserList.add(signUpUser);
                    }

                    if (!ObjectUtil.isEmpty(signUpUserList)) {
                        tv_no_data.setVisibility(View.GONE);
                        chatRecyclerView.setVisibility(View.VISIBLE);
                        commentsAdapter.notifyDataSetChanged();
                    } else {
                        tv_no_data.setVisibility(View.VISIBLE);
                        chatRecyclerView.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    progressDialog.hideProgressBar();
                    Toast.makeText(getActivity(), "Failed to reterive users.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }


}
