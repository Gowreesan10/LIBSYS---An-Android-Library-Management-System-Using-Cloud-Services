package com.code10.libsys.auth;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.LibraryDetails;
import com.code10.libsys.FCMReceiver;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateLibrarianDetails extends AppCompatActivity {
    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    int LAUNCH_SECOND_ACTIVITY = 1;
    int LAUNCH_GALLERY_ACTIVITY = 1000;
    CircleImageView imageView;
    Uri imageUri = null, storageUri;
    private String address;
    private String Latitude;
    private String Longitude;
    private FirebaseUser currentUser;
    private TextView etAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_library_details);

        currentUser = mFirebaseAuth.getCurrentUser();

        EditText etDisplayName = findViewById(R.id.libraryName);
        etAddress = findViewById(R.id.txtAddress);
        EditText tpNo = findViewById(R.id.etTelephoneNo);
        EditText faxNo = findViewById(R.id.etFaxNo);

        imageView = findViewById(R.id.imageProlile);
        Button selectImage = findViewById(R.id.btnSelectProfile);
        Button setLocation = findViewById(R.id.btnChooseLocation);
        Button updateDetails = findViewById(R.id.btnUpdate);

        selectImage.setOnClickListener(v -> {
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, LAUNCH_GALLERY_ACTIVITY);
        });
        setLocation.setOnClickListener(view -> {
            Intent i = new Intent(this, MapsActivity.class);
            startActivityForResult(i, LAUNCH_SECOND_ACTIVITY);
        });

        updateDetails.setOnClickListener(view -> {
            String DisplayName = etDisplayName.getText().toString();
            String Address = etAddress.getText().toString();
            String telephoneNo = tpNo.getText().toString();
            String fax_No = faxNo.getText().toString();

            if (isDetailsValid(DisplayName, Address)) {
                UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                        .setDisplayName(DisplayName + "." + getIntent().getExtras().getString("Role"))
                        .setPhotoUri(storageUri)
                        .build();

                currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Log.d("Sign Up profile Update", "User profile updated.");
                        Toast.makeText(this, currentUser.getDisplayName().split("\\.")[0], Toast.LENGTH_SHORT).show();
                        updateDetailsToFirestore(telephoneNo, fax_No);
                    }
                });
            }
        });
    }

    private boolean isDetailsValid(String DisplayName, String Address) {
        if (DisplayName.isEmpty()) {
            Toast.makeText(this, "Please Enter the Name", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (Address.isEmpty()) {
            Toast.makeText(this, "Please Enter the Address", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (imageUri == null) {
            return false;
        }

        return true;
    }


    public void updateDetailsToFirestore(String TeleNo, String FNo) {

        if (!TeleNo.isEmpty() && !FNo.isEmpty()) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String libName = currentUser.getDisplayName().replace(".Librarian", "");

            LibraryDetails libraryDetails = new LibraryDetails(libName, address, Latitude, Longitude, FCMReceiver.getToken(getApplicationContext()), TeleNo, FNo);

            db.collection("Libraries").document(libName).set(libraryDetails)
                    .addOnSuccessListener(documentReference -> {
                        Log.v("Activity", "Open admin from update details");
                        mFirebaseAuth.signOut();
                        startActivity(new Intent(UpdateLibrarianDetails.this, SignIn.class));
                    })
                    .addOnFailureListener(e -> Log.w("UserDetailUpdateFail", "Error adding document", e));
        } else {
            Toast.makeText(this, "Select Profile Image and Enter all Details", Toast.LENGTH_SHORT).show();
        }
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

            StorageReference ref = storageReference.child("ProfilePic/" + currentUser.getDisplayName().replace(".User", ""));

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



}