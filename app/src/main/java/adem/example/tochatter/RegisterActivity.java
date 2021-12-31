// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {

    private EditText regUserName, regUserEmail, regUserPassword, regUserRepassword;
    private DatabaseReference rootReference;
    private FirebaseAuth myAuth;
    private Button btnCreateRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        myAuth = FirebaseAuth.getInstance();
        rootReference = FirebaseDatabase.getInstance().getReference();
        btnCreateRegister = findViewById(R.id.btn_register);
        regUserName = findViewById(R.id.edt_register_username);
        regUserEmail = findViewById(R.id.edt_register_email);
        regUserPassword = findViewById(R.id.edt_register_password);
        regUserRepassword = findViewById(R.id.edt_register_re_password);
        TextView haveAccount = findViewById(R.id.txt_have_account);

        haveAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent loginActivityIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(loginActivityIntent);
            }
        });
        btnCreateRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewAccount();
            }
        });
    }

    private void createNewAccount() {
        String username = regUserName.getText().toString();
        String email = regUserEmail.getText().toString();
        String password = regUserPassword.getText().toString();
        String rePassword = regUserRepassword.getText().toString();

        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, "Username cannot be empty...!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty...!!!", Toast.LENGTH_SHORT).show();
        } else if (TextUtils.isEmpty(password) || TextUtils.isEmpty(rePassword)) {
            Toast.makeText(this, "Password fields cannot be empty...!!!", Toast.LENGTH_SHORT).show();
        } else if (!password.equals(rePassword)) {
            Toast.makeText(this, "Passwords aren't the same", Toast.LENGTH_SHORT).show();
        } else {

            btnCreateRegister.setEnabled(false);
            myAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        String activeUserID = myAuth.getCurrentUser().getUid();
                        String username = regUserName.getText().toString();
                        String email = regUserEmail.getText().toString();


                        HashMap<String, String> profileMap = new HashMap<>();
                        profileMap.put("uid_tb", activeUserID);
                        profileMap.put("name_tb", username);
                        profileMap.put("mail_tb", email);
                        profileMap.put("status_tb", "");

                        rootReference.child("Users_tb").child(activeUserID).setValue(profileMap);

                        Intent mainIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();

                        Toast.makeText(RegisterActivity.this, "New account creation successful.\nWELCOME " + username + "...!!!", Toast.LENGTH_SHORT).show();
                    } else {
                        String eMessage = task.getException().getMessage();
                        Toast.makeText(RegisterActivity.this, "Error :" + eMessage + "\nCheck your information...!!!", Toast.LENGTH_SHORT).show();
                    }
                    btnCreateRegister.setEnabled(true);
                }
            });
        }

    }
}