// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AndroidRuntimeException;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private FirebaseUser activeUser;
    private FirebaseAuth myAuth;
    private DatabaseReference usersReference;
    String activeUserID, currentUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar myToolbar = findViewById(R.id.inc_main_toolbar);
        myToolbar.setSubtitleTextColor(Color.WHITE);
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
            userisActive("ON");
        }
    }

    private void haveUsers() {
        activeUserID = myAuth.getCurrentUser().getUid();

        usersReference.child("Users_tb").child(activeUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if ((dataSnapshot.child("uname_tb").exists())) {
                    currentUserName = dataSnapshot.child("uname_tb").getValue().toString().toUpperCase(Locale.ROOT);
                } else {

                    Intent settings = new Intent(MainActivity.this, SettingsActivity.class);
                    settings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
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
        if (item.getItemId() == R.id.opt_create_groups) {
            createNewGroups();
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
        return true;
    }

    private void createNewGroups() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this, R.style.ThemeOverlay_MaterialComponents_MaterialAlertDialog);
        builder.setTitle("Enter group name...!!!");

        final EditText edtGroupName = new EditText(MainActivity.this);
        edtGroupName.setHint("Example: My Family");
        edtGroupName.setWidth(100);
        builder.setView(edtGroupName);
        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String groupName = edtGroupName.getText().toString();
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

        DatabaseReference groupPath = FirebaseDatabase.getInstance().getReference().child("Groups_tb");
        String groupKey = groupPath.push().getKey();

        HashMap<String, Object> groupMessageKey = new HashMap<>();
        groupPath.updateChildren(groupMessageKey);

        DatabaseReference messagesKeyPath = groupPath.child(groupKey);

        HashMap<String, Object> groupValuesMap = new HashMap<>();
        groupValuesMap.put("gname_tb", groupName);
        groupValuesMap.put("cuid_tb", activeUserID);

        messagesKeyPath.updateChildren(groupValuesMap);
        Toast.makeText(MainActivity.this, "Group "+groupName+" created.", Toast.LENGTH_LONG).show();

    }

    public void userisActive (String isActive_tb) {
        try {
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("Users_tb").child(activeUserID);
            HashMap<String, Object> isactiveMap = new HashMap<>();
            isactiveMap.put("isActive_tb", isActive_tb);
            db.updateChildren(isactiveMap);
        }catch (RuntimeException e){
            String emessage=e.getMessage();
            Toast.makeText(this, "Error: "+emessage, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        userisActive("OFF");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        userisActive("OFF");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        userisActive("ON");
    }
}