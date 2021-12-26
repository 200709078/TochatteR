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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class SettingsActivity extends AppCompatActivity {
    private EditText edtUserName, edtUserStatus;

    private DatabaseReference dataPath;
    private String activeUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        activeUserID = myAuth.getCurrentUser().getUid();
        dataPath = FirebaseDatabase.getInstance().getReference();

        Button btnProfileUpdate = findViewById(R.id.btn_profile_update);
        edtUserName = findViewById(R.id.edt_set_username);
        edtUserStatus = findViewById(R.id.edt_set_status);

        btnProfileUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSettings();
            }
        });

        takeUserInfo();
    }

    private void takeUserInfo() {

        dataPath.child("Users_tb").child(activeUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name_tb") && (dataSnapshot.hasChild("picture_tb")))) {
                            String takeUserName = dataSnapshot.child("name_tb").getValue().toString();
                            String takeUserStatus = dataSnapshot.child("status_tb").getValue().toString();

                            edtUserName.setText(takeUserName);
                            edtUserStatus.setText(takeUserStatus);

                        } else if ((dataSnapshot.exists()) && (dataSnapshot.hasChild("name_tb"))) {
                            String takeUserName = dataSnapshot.child("name_tb").getValue().toString();
                            String takeUserStatus = dataSnapshot.child("status_tb").getValue().toString();

                            edtUserName.setText(takeUserName);
                            edtUserStatus.setText(takeUserStatus);
                        } else {
                            Toast.makeText(SettingsActivity.this, "Please set your profile information...", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void updateSettings() {
        String setUserName = edtUserName.getText().toString();
        String setUserStatus = edtUserStatus.getText().toString();

        if (TextUtils.isEmpty(setUserName)) {
            Toast.makeText(this, "Please write your name...!!!", Toast.LENGTH_LONG).show();
        } else if (TextUtils.isEmpty(setUserStatus)) {
            Toast.makeText(this, "Please write your status...!!!", Toast.LENGTH_LONG).show();
        } else {

            HashMap<String, String> profileMap = new HashMap<>();
            profileMap.put("name_tb", setUserName);
            profileMap.put("status_tb", setUserStatus);

            dataPath.child("Users_tb").child(activeUserID).setValue(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(SettingsActivity.this, "Your profile has been successfully updated...!!!", Toast.LENGTH_LONG).show();
                        Intent mainIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(mainIntent);
                        finish();
                    } else {
                        String eMessage = task.getException().toString();
                        Toast.makeText(SettingsActivity.this, "Your profile could not be update. \nError: " + eMessage, Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}