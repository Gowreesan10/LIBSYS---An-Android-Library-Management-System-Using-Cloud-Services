package com.code10.libsys.auth;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.code10.libsys.Admin.AddBooks;
import com.code10.libsys.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.regex.Pattern;

//TODO: PASSWORD TYPE
//TODO: VERTIFICATION
//TODO EMSIL VERTIFICATION
//TODO FORGET PASS
public class SignUp extends AppCompatActivity {

    private final FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser;
    private String userRole = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        EditText emailEditText = findViewById(R.id.etEnterYourMail);
        EditText enterPasswordEditText = findViewById(R.id.etEnterYourPass);
        EditText conformPasswordEditText = findViewById(R.id.etConformYourPass);

        AutoCompleteTextView selectRole = findViewById(R.id.borrowSelect);
        ArrayAdapter<String> roles = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, getResources().getStringArray(R.array.Roles));
        selectRole.setAdapter(roles);
        selectRole.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                userRole = adapterView.getItemAtPosition(position).toString();
                Toast.makeText(SignUp.this, "Selected " + userRole, Toast.LENGTH_SHORT).show();
            }

            public void onNothingSelected(AdapterView<?> adapterView) {
                Toast.makeText(SignUp.this, "User Role not selected", Toast.LENGTH_SHORT).show();
            }
        });

        Button signup = findViewById(R.id.btnSignUp);
        signup.setOnClickListener(view -> {
            Toast.makeText(SignUp.this, "Sign Up", Toast.LENGTH_SHORT).show();
            String email = emailEditText.getText().toString();
            String enterPassword = enterPasswordEditText.getText().toString();
            String conformPassword = conformPasswordEditText.getText().toString();
//            if (validityCheck(email, enterPassword, conformPassword)) {
            createAccount(email, enterPassword);
//            }
        });
    }

    public void createAccount(String email, String password) {
        ProgressDialog progressDialog = new ProgressDialog(SignUp.this);
        progressDialog.setTitle("Creating Account...");
        progressDialog.show();

        mFirebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Log.d("SIGN Up CREATE ACC", "createUserWithEmail:success");
                        if (userRole.equals("User")) {
                            Intent i = new Intent(SignUp.this, UpdateUserDetails.class);
                            i.putExtra("Role", userRole);
                            Log.v("Activity", "Open update from sign up");
                            progressDialog.dismiss();
                            startActivity(i);
                        } else {
                            Intent i = new Intent(SignUp.this, UpdateLibrarianDetails.class);
                            i.putExtra("Role", userRole);
                            Log.v("Activity", "Open update from sign up");
                            progressDialog.dismiss();
                            startActivity(i);
                        }

                    } else {
                        Log.w("SIGN Up CREATE ACC", "createUserWithEmail:failure", task.getException());
                    }
                });
    }

    public boolean validityCheck(String email, String enterPassword, String conformPassword) {
        if (email.isEmpty()) {
            Toast.makeText(this, "Please Enter the Email", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (enterPassword.isEmpty()) {
            Toast.makeText(this, "Please Enter the Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (conformPassword.isEmpty()) {
            Toast.makeText(this, "Please Conform the Password", Toast.LENGTH_SHORT).show();
            return false;
        }

        if ((!userRole.equals("Librarian")) && (!userRole.equals("User"))) {
            Toast.makeText(this, "Select User Role", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!enterPassword.equals(conformPassword)) {
            Toast.makeText(this, "Passwords does not Match", Toast.LENGTH_SHORT).show();
            return false;
        }

        return ((isEmailValid(email)) && isPasswordValid(enterPassword));
    }

    private boolean isPasswordValid(String password) {
        if (!(password.length() > 8)) { //minimum chars 8
            Toast.makeText(this, "Minimum password Length 8", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.contains(" ")) { //can't contain space
            Toast.makeText(this, "Password can't Contain Space", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isPasswordContainNumber = false;
        for (int i = 0; i <= 9; i++) {   // check digits from 0 to 9
            String str1 = Integer.toString(i);
            if (password.contains(str1)) {
                isPasswordContainNumber = true;
            }
        }

        if (!isPasswordContainNumber) {
            Toast.makeText(this, "Password Should Contain Numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!(password.contains("@") || password.contains("#")
                || password.contains("!") || password.contains("~")
                || password.contains("$") || password.contains("%")
                || password.contains("^") || password.contains("&")
                || password.contains("*") || password.contains("(")
                || password.contains(")") || password.contains("-")
                || password.contains("+") || password.contains("/")
                || password.contains(":") || password.contains(".")
                || password.contains(", ") || password.contains("<")
                || password.contains(">") || password.contains("?")
                || password.contains("|"))
        ) {
            Toast.makeText(this, "Password Should Contain A special character", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isPasswordHaveCapitalLetters = false;
        for (int i = 65; i <= 90; i++) {                  // checking capital letters
            char c = (char) i;
            String str = Character.toString(c);
            if (password.contains(str)) {
                isPasswordHaveCapitalLetters = true;
            }
        }
        if (!isPasswordHaveCapitalLetters) {
            Toast.makeText(this, "Password Should Contain A Capital Letter", Toast.LENGTH_SHORT).show();
            return false;
        }

        boolean isPasswordHaveSmallLetters = false;
        for (int i = 90; i <= 122; i++) {
            char c = (char) i;
            String str = Character.toString(c);
            if (password.contains(str)) {
                isPasswordHaveSmallLetters = true;
            }
        }
        if (!isPasswordHaveSmallLetters) {
            Toast.makeText(this, "Password Should Contain A Small Letter", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isEmailValid(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\." + "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";

        Pattern pat = Pattern.compile(emailRegex);
        if (!pat.matcher(email).matches()) {
            Toast.makeText(this, "Enter A valid email", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

}
