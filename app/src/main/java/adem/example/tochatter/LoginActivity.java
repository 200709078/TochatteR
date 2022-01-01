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

public class LoginActivity extends AppCompatActivity {
    private EditText edtUserMail, edtUserPassword;
    private FirebaseAuth myAuth;
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        btnLogin = findViewById(R.id.btn_login);
        edtUserMail = findViewById(R.id.edt_user_email);
        edtUserPassword = findViewById(R.id.edt_user_password);
        TextView txtNewAccount = findViewById(R.id.txt_new_account);
        myAuth = FirebaseAuth.getInstance();

        txtNewAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent registerIntent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(registerIntent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                allowUser();
            }
        });
    }

    private void allowUser() {
        String email = edtUserMail.getText().toString();
        String password = edtUserPassword.getText().toString();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, "Email cannot be empty...!!!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Password cannot be empty...!!!", Toast.LENGTH_LONG).show();
        } else {
            btnLogin.setEnabled(false);

            myAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {

                    if (task.isSuccessful()) {
                        Intent mainIntent = new Intent(LoginActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();

                        Toast.makeText(LoginActivity.this, "Login successful.\nWelcome " + email + "...!!!", Toast.LENGTH_LONG).show();

                    } else {
                        String eMessage = task.getException().getMessage();
                        Toast.makeText(LoginActivity.this, "Error: " + eMessage + "\nCheck information...!!!", Toast.LENGTH_LONG).show();
                    }
                    btnLogin.setEnabled(true);
                }
            });
        }
    }
}