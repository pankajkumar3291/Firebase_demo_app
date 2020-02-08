package com.pank.pankapp.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pank.pankapp.R;
import com.pank.pankapp.activities.ChatActivity;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.util.ObjectUtil;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentsAdapter extends RecyclerView.Adapter<CommentsAdapter.ViewHolder> {

    private Activity context;
    private List<EOSignUpUser> signUpUserList;

    public CommentsAdapter(Activity context, List<EOSignUpUser> signUpUserList) {
        this.context = context;
        this.signUpUserList = signUpUserList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.comments_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        EOSignUpUser signUpUser = this.signUpUserList.get(position);

        if (ObjectUtil.isNonEmptyStr(signUpUser.getfName()) && ObjectUtil.isNonEmptyStr(signUpUser.getlName()))
            holder.tvUserName.setText(signUpUser.getfName().concat(" ").concat(signUpUser.getlName()));

        String ct = DateFormat.getDateInstance().format(new Date());
        holder.tvCurrentDate.setText(ct);

        holder.chatRowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chatIntent = new Intent(context, ChatActivity.class);
                chatIntent.putExtra("chatUser", signUpUser);
                context.startActivity(chatIntent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return ObjectUtil.isEmpty(this.signUpUserList) ? 0 : this.signUpUserList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private CircleImageView ivUserPic;
        private TextView tvUserName, tvCurrentDate;
        private LinearLayout chatRowLayout;

        private ViewHolder(View view) {
            super(view);
            ivUserPic = view.findViewById(R.id.ivUserPic);
            tvUserName = view.findViewById(R.id.tvUserName);
            tvCurrentDate = view.findViewById(R.id.tvCurrentDate);
            chatRowLayout = view.findViewById(R.id.chatRowLayout);
        }
    }

}
