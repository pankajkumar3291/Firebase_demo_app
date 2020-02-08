package com.pank.pankapp.activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.fxn.pix.Pix;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.pank.pankapp.R;
import com.pank.pankapp.components.FontAwesomeIcon;
import com.pank.pankapp.components.GlobalAlertDialog;
import com.pank.pankapp.components.GlobalProgressDialog;
import com.pank.pankapp.model.EOSignUpUser;
import com.pank.pankapp.model.ImageUploadInfo;
import com.pank.pankapp.util.ObjectUtil;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.pank.pankapp.util.Constants.REQUEST_CODE;
import static java.util.Objects.requireNonNull;

public class EditProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView iv_back;
    private CircleImageView ivUserPic;
    private FontAwesomeIcon icon_click_image;
    private TextInputEditText et_first_name, et_last_name, et_email_id, et_dob, et_phone_number;
    private Button btnUpdate;
    private RadioGroup radio_btn_group;
    private RadioButton radio_male_btn, radio_female_btn;
    private GlobalProgressDialog progressDialog;
    private EOSignUpUser eoSignUpUser;
    private String gender;
    private String dateOfBirth;
    private String firstName;
    private String lastName;
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        if (!ObjectUtil.isEmpty(this.getIntent().getSerializableExtra("profileData")))
            this.eoSignUpUser = (EOSignUpUser) this.getIntent().getSerializableExtra("profileData");

        this.initView();
        this.setOnClickListener();
        this.dataToView();
    }

    private void initView() {
        this.progressDialog = new GlobalProgressDialog(this);
        this.iv_back = this.findViewById(R.id.iv_back);
        this.ivUserPic = this.findViewById(R.id.ivUserPic);
        this.icon_click_image = this.findViewById(R.id.icon_click_image);
        this.et_first_name = this.findViewById(R.id.et_first_name);
        this.et_last_name = this.findViewById(R.id.et_last_name);
        this.et_email_id = this.findViewById(R.id.et_email_id);
        this.et_dob = this.findViewById(R.id.et_dob);
        this.et_phone_number = this.findViewById(R.id.et_phone_number);
        this.btnUpdate = this.findViewById(R.id.btnUpdate);
        this.radio_btn_group = this.findViewById(R.id.radio_btn_group);
        this.radio_male_btn = this.findViewById(R.id.radio_male_btn);
        this.radio_female_btn = this.findViewById(R.id.radio_female_btn);

        this.firebaseAuth = FirebaseAuth.getInstance();
        this.firebaseDatabase = FirebaseDatabase.getInstance();
        this.firebaseUser = this.firebaseAuth.getCurrentUser();
        this.databaseReference = this.firebaseDatabase.getReference("NewUser");
        this.firebaseStorage = FirebaseStorage.getInstance();
        this.storageReference = this.firebaseStorage.getReference();

    }

    private void setOnClickListener() {
        this.iv_back.setOnClickListener(this);
        this.icon_click_image.setOnClickListener(this);
        this.btnUpdate.setOnClickListener(this);
        this.et_dob.setOnClickListener(this);
        this.radio_btn_group.setOnCheckedChangeListener(this.onCheckedChangeListener);
    }

    private void dataToView() {
        if (!ObjectUtil.isEmpty(this.eoSignUpUser) && !ObjectUtil.isEmpty(this.firebaseUser)) {
            this.et_first_name.setText(this.eoSignUpUser.getfName());
            this.et_last_name.setText(this.eoSignUpUser.getlName());
            this.et_email_id.setText(this.eoSignUpUser.getEmail());
            this.et_phone_number.setText(this.eoSignUpUser.getPhoneNumber());
            if (ObjectUtil.isNonEmptyStr(this.eoSignUpUser.getGender()) && ObjectUtil.isNonEmptyStr(this.eoSignUpUser.getDateOfBirth())) {
                this.et_dob.setText(this.eoSignUpUser.getDateOfBirth());
                if (this.eoSignUpUser.getGender().equalsIgnoreCase("Male"))
                    radio_male_btn.setChecked(true);
                else
                    radio_female_btn.setChecked(true);
            }
            if (!ObjectUtil.isEmpty(this.firebaseUser.getPhotoUrl())) {
                Picasso.get().load(firebaseUser.getPhotoUrl()).error(R.drawable.ic_user_circle).fit().into(ivUserPic);
            }
        }
    }

    private RadioGroup.OnCheckedChangeListener onCheckedChangeListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup group, int checkedId) {

            RadioButton checkedRadioButton = group.findViewById(checkedId);
            switch (checkedRadioButton.getId()) {
                case R.id.radio_male_btn:
                    gender = "Male";
                    break;
                case R.id.radio_female_btn:
                    gender = "Female";
                    break;
            }
        }
    };

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                EditProfileActivity.this.finish();
                break;
            case R.id.icon_click_image:
                Pix.start(EditProfileActivity.this, REQUEST_CODE, 1);
                break;
            case R.id.et_dob:
                this.setDateOfBirth();
                break;
            case R.id.btnUpdate:
                if (isValidFields()) {
                    updateUserProfile();
                }
                break;
        }
    }

    private void updateUserProfile() {
        if (!ObjectUtil.isEmpty(firebaseUser)) {
            progressDialog.showProgressBar();

            EOSignUpUser signUpUser = new EOSignUpUser(firebaseUser.getUid(), firstName, lastName, ObjectUtil.getTextFromView(et_email_id), ObjectUtil.getTextFromView(et_phone_number), gender, ObjectUtil.getTextFromView(et_dob));
            databaseReference.child(firebaseUser.getUid())
                    .setValue(signUpUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.hideProgressBar();
                        Toast.makeText(EditProfileActivity.this, "Profile updated Successfully.", Toast.LENGTH_SHORT).show();
                        EditProfileActivity.this.finish();
                    } else {
                        progressDialog.hideProgressBar();
                        Toast.makeText(EditProfileActivity.this, "Unable to update profile.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private boolean isValidFields() {
        String errorMsg = null;
        this.firstName = ObjectUtil.getTextFromView(et_first_name);
        this.lastName = ObjectUtil.getTextFromView(et_last_name);

        if (ObjectUtil.isEmptyStr(gender) || ObjectUtil.isEmptyStr(firstName) || ObjectUtil.isEmptyStr(lastName) || ObjectUtil.isEmpty(ObjectUtil.getTextFromView(et_dob))) {
            errorMsg = this.getString(R.string.all_fields_required);
        }

        if (ObjectUtil.isNonEmptyStr(errorMsg)) {
            new GlobalAlertDialog(EditProfileActivity.this, false, true).show(errorMsg);
            return false;
        }
        return true;
    }

    private void setDateOfBirth() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog pickerDialog = new DatePickerDialog(this, R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                dateOfBirth = dayOfMonth + "/" + (monthOfYear + 1) + "/" + year;
                et_dob.setText(dateOfBirth);
            }
        }, year, month, day);
        pickerDialog.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!ObjectUtil.isEmpty(data) && resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE) {
            ArrayList<String> resultArray = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
            File file = new File(requireNonNull(resultArray).get(0));
            Uri imageUri = Uri.fromFile(new File(file.getAbsolutePath()));
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                //imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }


            //TODO from here upload profile pic on firebase database
            uploadImageOnFirebase(imageUri);
        }
    }

    // Creating Method to get the selected image file Extension from File Path URI.
    public String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        // Returning the file Extension.
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void uploadImageOnFirebase(Uri imageUri) {
        if (!ObjectUtil.isEmpty(firebaseUser)) {
            progressDialog.showProgressBar();
            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setPhotoUri(imageUri).build();

            firebaseUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                progressDialog.hideProgressBar();
                                if (!ObjectUtil.isEmpty(firebaseUser.getPhotoUrl())) {
                                    Toast.makeText(EditProfileActivity.this, "Profile image uploaded successfully.", Toast.LENGTH_SHORT).show();
                                    Picasso.get().load(firebaseUser.getPhotoUrl()).error(R.drawable.ic_user_circle).fit().into(ivUserPic);
                                }
                            } else {
                                progressDialog.hideProgressBar();
                                Toast.makeText(EditProfileActivity.this, "Failed to upload image.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        /*if (imageUri != null) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            //storageReference = storageReference.child("images/" + UUID.randomUUID().toString());
            // Creating second StorageReference.
            StorageReference storageReference2nd = storageReference.child("images/" + UUID.randomUUID().toString());
            storageReference2nd.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            //progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Profile image uploaded successfully.", Toast.LENGTH_SHORT).show();
                            ImageUploadInfo imageUploadInfo = new ImageUploadInfo(requireNonNull(taskSnapshot.getUploadSessionUri()).toString());
                            // Getting image upload ID.
                            String ImageUploadId = databaseReference.push().getKey();
                            // Adding image upload id s child element into databaseReference.
                            assert ImageUploadId != null;
                            databaseReference.child(ImageUploadId).setValue(imageUploadInfo);


                            // Adding Add Value Event Listener to databaseReference.
                            databaseReference.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NotNull DataSnapshot snapshot) {
                                    progressDialog.dismiss();
                                    ImageUploadInfo imageUploadInfo = snapshot.getValue(ImageUploadInfo.class);
                                    assert imageUploadInfo != null;
                                    Picasso.get().load(imageUploadInfo.getImageURL()).error(R.drawable.ic_user_circle).fit().into(ivUserPic);

//                                    for (DataSnapshot postSnapshot : snapshot.getChildren()) {
//                                        ImageUploadInfo imageUploadInfo = postSnapshot.getValue(ImageUploadInfo.class);
//                                        //list.add(imageUploadInfo);
//                                        Picasso.get().load(imageUploadInfo.getImageURL()).error(R.drawable.ic_user_circle).fit().into(ivUserPic);
//                                    }
                                    // Hiding the progress dialog.
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // Hiding the progress dialog.
                                    progressDialog.dismiss();

                                }
                            });


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss();
                            Toast.makeText(EditProfileActivity.this, "Failed to upload image. " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NotNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }*/

    }


}
