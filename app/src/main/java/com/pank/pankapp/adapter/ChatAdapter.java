package com.pank.pankapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.SessionSecuredPreferences;
import com.pank.pankapp.model.FirebaseChat;
import com.pank.pankapp.util.ObjectUtil;

import java.util.List;

import static com.pank.pankapp.util.Constants.LOGIN_PREFERENCE;
import static com.pank.pankapp.util.Constants.USER_ID;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private Context context;
    private List<FirebaseChat> chatList;
    private SessionSecuredPreferences loginPreferences;
    private String savedUserId;

    public ChatAdapter(Context context, List<FirebaseChat> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.loginPreferences = ApplicationHelper.application().loginPreferences(LOGIN_PREFERENCE);
        this.savedUserId = this.loginPreferences.getString(USER_ID, "");
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.chat_row, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder chatViewHolder, int position) {
        FirebaseChat firebaseChat = this.chatList.get(position);

        if (ObjectUtil.isNonEmptyStr(savedUserId) && !ObjectUtil.isEmpty(firebaseChat)) {
            if (!firebaseChat.getFirebaseId().equals(savedUserId)) {
                chatViewHolder.tvRightChat.setText(firebaseChat.getFirebaseMessage());
                chatViewHolder.tvRightChat.setVisibility(View.VISIBLE);
                chatViewHolder.tvLeftChat.setVisibility(View.GONE);
            } else {
                chatViewHolder.tvLeftChat.setText(firebaseChat.getFirebaseMessage());
                chatViewHolder.tvLeftChat.setVisibility(View.VISIBLE);
                chatViewHolder.tvRightChat.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(chatList) ? 0 : chatList.size();
    }

    public class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView tvLeftChat, tvRightChat;

        private ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            this.tvLeftChat = itemView.findViewById(R.id.tvLeftChat);
            this.tvRightChat = itemView.findViewById(R.id.tvRightChat);
        }
    }


}
