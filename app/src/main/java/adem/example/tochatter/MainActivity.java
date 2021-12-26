// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser activeUser;
    private FirebaseAuth myAuth;
    private DatabaseReference usersReference;
    private String activeUserID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.inc_main_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("TochatteR");

        ViewPager myViewPager = findViewById(R.id.vp_main_tabs);
        TabsAdapter myTabsAdapter = new TabsAdapter(getSupportFragmentManager());
        myViewPager.setAdapter(myTabsAdapter);

        TabLayout myTabLayout = findViewById(R.id.main_tabs);
        myTabLayout.setupWithViewPager(myViewPager);

        myAuth = FirebaseAuth.getInstance();
        activeUser = myAuth.getCurrentUser();
        usersReference = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (activeUser == null) {
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            loginIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finish();
            startActivity(loginIntent);
        } else {
            haveUsers();
        }
    }

    private void haveUsers() {
        activeUserID = myAuth.getCurrentUser().getUid();

        usersReference.child("Users_tb").child(activeUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("name_tb").exists())) {
                    String currentUserName = dataSnapshot.child("name_tb").getValue().toString().toUpperCase(Locale.ROOT);
                    Toast.makeText(MainActivity.this, "WELCOME " + currentUserName, Toast.LENGTH_LONG).show();
                } else {
                    String email=getIntent().getExtras().get("email").toString();
                    Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    settings.putExtra("email",email);
                    startActivity(settings);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);
        if (item.getItemId() == R.id.opt_find_friends) {
            Toast.makeText(MainActivity.this, "Find Friend option selected.", Toast.LENGTH_LONG).show();
        }
        if (item.getItemId() == R.id.opt_settings) {
            Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(settings);
        }
        if (item.getItemId() == R.id.opt_logout) {
            myAuth.signOut();
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
        }
        if (item.getItemId() == R.id.opt_create_groups) {
            createNewGroups();
        }
        return true;
    }

    private void createNewGroups() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);
        builder.setTitle("Enter group name...!!!");

        final EditText txtGroupName = new EditText(MainActivity.this);
        txtGroupName.setHint("Example: My Family");
        builder.setView(txtGroupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = txtGroupName.getText().toString();
                if (TextUtils.isEmpty(groupName)) {
                    Toast.makeText(MainActivity.this, "Group name cannot be empty", Toast.LENGTH_LONG).show();
                } else {
                    createNewGroup(groupName);
                }
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void createNewGroup(String groupName) {
        usersReference.child("Groups_tb").child(groupName).setValue("").addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "The group named " + groupName + " has been created.", Toast.LENGTH_LONG).show();
                } else {
                    String eMessage = task.getException().toString();
                    Toast.makeText(MainActivity.this, "Error: " + eMessage, Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}