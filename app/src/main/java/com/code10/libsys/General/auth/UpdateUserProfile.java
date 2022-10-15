package com.code10.libsys.General.auth;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateUserProfile extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    int LAUNCH_SECOND_ACTIVITY = 1;
    int LAUNCH_GALLERY_ACTIVITY = 1000;
    CircleImageView imageView;
    Uri imageUri = null, storageUri;
    TextView textbirthdate;
    ProgressDialog progressDialog;
    private FirebaseUser currentUser;
    private String address;
    private String Latitude;
    private String Longitude;
    private TextView etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_profile);
        currentUser = mFirebaseAuth.getCurrentUser();

        EditText etDisplayName = findViewById(R.id.userName);
        etAddress = findViewById(R.id.txtAddress);
        Button btnpickDate = findViewById(R.id.btnpickDate);
        textbirthdate = findViewById(R.id.textbirthdate);
        Button updateDetails = findViewById(R.id.btnUpdate);
        imageView = findViewById(R.id.imageProlile);
        Button selectImage = findViewById(R.id.btnSelectProfile);
        Button setLocation = findViewById(R.id.btnChooseLocation);

        etDisplayName.setText(currentUser.getDisplayName().replace(".User", ""));
        firebaseFirestore.collection("Users").document(currentUser.getDisplayName()).addSnapshotListener((value, error) -> {
            Map<String, Object> userData = value.getData();
            etAddress.setText(userData.get("Address").toString());
            textbirthdate.setText(userData.get("DOB").toString());
            storageUri = Uri.parse(userData.get("Pic").toString());
            Latitude = userData.get("Latitude").toString();
            Longitude = userData.get("Longitude").toString();
            Picasso.get().load(storageUri).into(imageView);
        });


        selectImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, LAUNCH_GALLERY_ACTIVITY);
        });

        setLocation.setOnClickListener(view -> {
            Intent i = new Intent(this, MapsActivity.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
        });

        btnpickDate.setOnClickListener(v -> {
            com.code10.libsys.General.DatePicker mDatePickerDialogFragment;
            mDatePickerDialogFragment = new com.code10.libsys.General.DatePicker();
            mDatePickerDialogFragment.show(getSupportFragmentManager(), "DATE PICK");
        });

        updateDetails.setOnClickListener(view -> {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Updating....");
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.show();
            String Address = etAddress.getText().toString();
            String DOB = textbirthdate.getText().toString();

            if (isDetailsValid(Address, DOB)) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(storageUri)
                        .build();

                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Sign Up profile Update", "User profile updated.");
                        updateDetailsToFirestore(Address, DOB, storageUri.toString());
                    } else {
                        progressDialog.dismiss();
                    }
                }).addOnFailureListener(e -> progressDialog.dismiss());
            } else {
                progressDialog.dismiss();
            }
        });
        ImageView imageGroup2 = findViewById(R.id.imageGroup2);
        imageGroup2.setOnClickListener(v -> finish());
    }

    private boolean isDetailsValid(String Address, String DOB) {
        if (Address.isEmpty()) {
            Toast.makeText(this, "Please Enter the Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (DOB.isEmpty()) {
            Toast.makeText(this, "Enter a Valid Age", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }


    public void updateDetailsToFirestore(String Address, String DOB, String profilepic) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Object> userDetails = new HashMap<>();
        userDetails.put("Address", Address);
        userDetails.put("Latitude", Latitude);
        userDetails.put("Longitude", Longitude);
        userDetails.put("DOB", DOB);
        userDetails.put("Pic", profilepic);

        db.collection("Users").document(currentUser.getDisplayName()).set(userDetails)
                .addOnSuccessListener(documentReference -> {
                    progressDialog.dismiss();
                    Toast.makeText(this, "Profile Update Success", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Log.w("UserDetailUpdateFail", "Error adding document", e);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LAUNCH_SECOND_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                address = data.getStringExtra("Address");
                Latitude = data.getStringExtra("Latitude");
                Longitude = data.getStringExtra("Longitude");
                etAddress.setText(address);
            }

            if (resultCode == Activity.RESULT_CANCELED) {
                // Write your code if there's no result
            }
        } else if (requestCode == LAUNCH_GALLERY_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                imageUri = data.getData();
                imageView.setImageURI(imageUri);
                uploadImage();
            }

        }
    }

    private void uploadImage() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();
        if (imageUri != null) {

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storageReference.child("ProfilePic/" + UUID.randomUUID().toString());

            ref.putFile(imageUri)
                    .addOnSuccessListener(
                            taskSnapshot -> {
                                progressDialog.dismiss();
                                Toast.makeText(this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                getImageUrl(ref);
                            })

                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    })

                    .addOnProgressListener(
                            taskSnapshot -> {
                                double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                                progressDialog.setMessage("Uploaded " + (int) progress + "%");
                            });
        }
    }

    public void getImageUrl(StorageReference ref) {
        ref.getDownloadUrl().addOnSuccessListener(uri -> {
            storageUri = uri;
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String selectedDate = DateFormat.getDateInstance(DateFormat.FULL).format(mCalendar.getTime());
        textbirthdate.setText(selectedDate);
    }
}