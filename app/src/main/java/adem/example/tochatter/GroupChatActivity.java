// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class GroupChatActivity extends AppCompatActivity {

    private EditText edtGroupMessage;
    private ScrollView groupScrollView;
    private TextView txtGroupChat;

    private DatabaseReference userPath;
    private DatabaseReference groupMessagesPath;

    private String activeUserID;
    private String activeUsername, activeGroupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        activeGroupName = getIntent().getExtras().get("groupName").toString();
        //Toast.makeText(this, activeGroupName, Toast.LENGTH_LONG).show();

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        activeUserID = myAuth.getCurrentUser().getUid();
        userPath = FirebaseDatabase.getInstance().getReference().child("Users_tb");
        groupMessagesPath = FirebaseDatabase.getInstance().getReference().child("gMessages_tb");

        Toolbar myToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(activeGroupName.toUpperCase() + " GROUP");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton btnSendMessage = findViewById(R.id.btn_group_message_send);
        edtGroupMessage = findViewById(R.id.edt_group_message);
        txtGroupChat = findViewById(R.id.txt_group_chat);
        groupScrollView = findViewById(R.id.group_scroll_view);

        takeUserInfo();

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                savegMessagesDB();
                edtGroupMessage.setText(null);
                groupScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        groupMessagesPath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (dataSnapshot.exists()) {
                    viewgMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                viewgMessages(dataSnapshot);
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void viewgMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {

            String chatDateTime = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String groupName = (String) ((DataSnapshot) iterator.next()).getValue();
            String sendUser = (String) ((DataSnapshot) iterator.next()).getValue();


            if (activeGroupName.equals(groupName)) {
                txtGroupChat.append(sendUser + "\n" + chatMessage + "\n" + chatDateTime + "\n\n");
            }

            groupScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void savegMessagesDB() {

        String messageGroup = edtGroupMessage.getText().toString();
        String messageGroupKey = groupMessagesPath.push().getKey();

        if (TextUtils.isEmpty(messageGroup)) {
            Toast.makeText(this, "Message field cannot be empty...!!!", Toast.LENGTH_LONG).show();
        } else {

            Calendar datetimeCalendar = Calendar.getInstance();
            SimpleDateFormat activeDateTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.ROOT);
            String activeDateTime = activeDateTimeFormat.format(datetimeCalendar.getTime());

            HashMap<String, Object> groupMessageKey = new HashMap<>();
            groupMessagesPath.updateChildren(groupMessageKey);

            DatabaseReference messagesKeyPath = groupMessagesPath.child(messageGroupKey);

            HashMap<String, Object> gmessagesValuesMap = new HashMap<>();
            gmessagesValuesMap.put("send_uname_tb", activeUsername);
            gmessagesValuesMap.put("select_gname_tb", activeGroupName);
            gmessagesValuesMap.put("gmessage_tb", messageGroup);
            gmessagesValuesMap.put("date_time_tb", activeDateTime);

            messagesKeyPath.updateChildren(gmessagesValuesMap);
        }
    }

    private void takeUserInfo() {
        userPath.child(activeUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    activeUsername = dataSnapshot.child("uname_tb").getValue().toString();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}