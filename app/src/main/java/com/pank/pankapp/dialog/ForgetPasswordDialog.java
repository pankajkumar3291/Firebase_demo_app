package com.pank.pankapp.dialog;

import android.content.Context;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.pank.pankapp.R;
import com.pank.pankapp.application.ApplicationHelper;
import com.pank.pankapp.components.FontAwesomeIcon;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.util.GlobalUtil;
import com.pank.pankapp.util.ObjectUtil;
import com.pank.pankapp.util.UIUtil;

public class ForgetPasswordDialog extends GlobalAlertDialog {

    private TextInputEditText et_email_id;
    private FirebaseAuth firebaseAuth;
    private GlobalProgressDialog progressDialog;

    public ForgetPasswordDialog(Context context, boolean isConfirmation, boolean isWarning) {
        super(context, isConfirmation, isWarning);
        this.firebaseAuth = FirebaseAuth.getInstance();
        this.progressDialog = new GlobalProgressDialog(context);
    }

    @Override
    protected int layoutID() {
        return R.layout.dialog_forget_pwd;
    }

    @Override
    protected void loadHeader() {
        super.loadHeader();
        FontAwesomeIcon wifiIcon = findViewById(R.id.titleIcon);
        wifiIcon.setText(R.string.icon_unlock_password);
        wifiIcon.setTextColor(UIUtil.getColor(android.R.color.white));
        this.findViewById(R.id.tableRow1).setBackgroundColor(ApplicationHelper.application().getContext().getResources().getColor(R.color.colorPrimary));
    }

    @Override
    protected void loadBody() {
        //super.loadBody();
        this.et_email_id = this.findViewById(R.id.et_email_id);
    }

    @Override
    protected void loadFooter() {
        super.loadFooter();
        this.okButton.setText("Reset");
        final float buttonRadius = UIUtil.getDimension(R.dimen._50sdp);
        UIUtil.setBackgroundRect(okButton, R.color.colorPrimary, buttonRadius);
        this.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetPassword();
            }
        });
    }

    private void resetPassword() {
        if (ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_email_id))) {
            Toast.makeText(ApplicationHelper.application(), "Please enter email", Toast.LENGTH_SHORT).show();
        } else if (!GlobalUtil.isValidEmail(ObjectUtil.getTextFromView(et_email_id))) {
            Toast.makeText(ApplicationHelper.application(), "Please enter valid email", Toast.LENGTH_SHORT).show();
        } else {
            progressDialog.showProgressBar();
            this.firebaseAuth.sendPasswordResetEmail(ObjectUtil.getTextFromView(et_email_id)).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    progressDialog.hideProgressBar();
                    if (task.isSuccessful()) {
                        ForgetPasswordDialog.this.dismiss();
                        Toast.makeText(ApplicationHelper.application(), "We have sent you instructions to reset your password!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(ApplicationHelper.application(), "This email is not registered.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    public void onConfirmation() {
        //super.onConfirmation();
    }

}
