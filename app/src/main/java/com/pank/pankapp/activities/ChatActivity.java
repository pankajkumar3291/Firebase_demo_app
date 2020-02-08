package com.pank.pankapp.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pank.pankapp.R;
import com.pank.pankapp.adapter.ChatAdapter;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.model.FirebaseChat;
import com.pank.pankapp.util.ObjectUtil;

import java.util.ArrayList;
import java.util.List;

import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;
import static com.pank.pankapp.util.Constants.USER_ID;

public class ChatActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView ivBack, ivSendChat;
    private TextView tvUserName;
    private RecyclerView recyclerChat;
    private EditText etTypeMessage;
    private EOSignUpUser signUpUser;
    private List<FirebaseChat> firebaseChatList = new ArrayList<>();
    private ChatAdapter chatAdapter;
    private DatabaseReference databaseReference;
    private SessionSecuredPreferences loginPreferences;
    private String savedUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("chatUser"))) {
            this.signUpUser = (EOSignUpUser) this.getIntent().getSerializableExtra("chatUser");
        }

        this.initView();
        this.setOnClickListener();

    }

    private void initView() {
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.savedUserId = this.loginPreferences.getString(USER_ID, "");
        this.ivBack = this.findViewById(R.id.ivBack);
        this.tvUserName = this.findViewById(R.id.tvUserName);
        this.recyclerChat = this.findViewById(R.id.recyclerChat);
        this.etTypeMessage = this.findViewById(R.id.etTypeMessage);
        this.ivSendChat = this.findViewById(R.id.ivSendChat);

        this.databaseReference = FirebaseDatabase.getInstance().getReference();

        this.chatAdapter = new ChatAdapter(this, firebaseChatList);
        this.recyclerChat.setHasFixedSize(true);
        this.recyclerChat.setAdapter(chatAdapter);
    }

    private void setOnClickListener() {
        this.ivBack.setOnClickListener(this);
        this.ivSendChat.setOnClickListener(this);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.signUpUser)) {
            this.tvUserName.setText(this.signUpUser.getfName().concat(" ").concat(this.signUpUser.getlName()));
        }

        this.databaseReference.child("chatMessages").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                firebaseChatList.clear();

                if (!ObjectUtil.isEmpty(dataSnapshot)) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        FirebaseChat firebaseChat = snapshot.getValue(FirebaseChat.class);

                        assert firebaseChat != null;
                        if (signUpUser.getUserId().equals(firebaseChat.getFirebaseId()))
                            firebaseChatList.add(firebaseChat);
                    }
                    chatAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ChatActivity.this, "Failed to upload previous chats.", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();

        if (!ObjectUtil.isEmpty(firebaseChatList))
            firebaseChatList.clear();

        this.dataToView();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ivBack:
                ChatActivity.this.finish();
                break;
            case R.id.ivSendChat:
                this.sendMessage();
                break;
        }
    }

    private void sendMessage() {
        if (!ObjectUtil.isEmpty(etTypeMessage.getText().toString()) && !ObjectUtil.isEmpty(this.signUpUser)) {
            FirebaseChat message = new FirebaseChat();
            message.setFirebaseMessage(etTypeMessage.getText().toString());
            message.setFirebaseId(this.signUpUser.getUserId());

            DatabaseReference newDatabaseRef = FirebaseDatabase.getInstance().getReference().child("chatMessages").push();
            newDatabaseRef.setValue(message).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful())
                        etTypeMessage.setText("");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
        }

    }

}
