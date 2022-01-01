// Adem VAROL - 200709078
package adem.example.tochatter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.location.Location;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.Time;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;

public class ContactChatActivity extends AppCompatActivity {

    private EditText edtContactMessage;
    private ScrollView contactScrollView;
    private TextView txtContactChat;

    private DatabaseReference userPath;
    private DatabaseReference contactMessagesPath;

    private String activeUserID;
    private String activeUsername;
    String selectUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_chat);

        selectUserName = getIntent().getExtras().get("selectUserName").toString();
        //Toast.makeText(this, selectUserName, Toast.LENGTH_LONG).show();

        FirebaseAuth myAuth = FirebaseAuth.getInstance();
        activeUserID = myAuth.getCurrentUser().getUid();
        userPath = FirebaseDatabase.getInstance().getReference().child("Users_tb");
        contactMessagesPath = FirebaseDatabase.getInstance().getReference().child("uMessages_tb");

        Toolbar myToolbar = findViewById(R.id.contact_chat_bar_layout);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle(selectUserName.toUpperCase());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ImageButton btnSendMessage = findViewById(R.id.btn_contact_message_send);
        edtContactMessage = findViewById(R.id.edt_contact_message);
        txtContactChat = findViewById(R.id.txt_contact_chat);
        contactScrollView = findViewById(R.id.contact_scroll_view);

        takeUserInfo();

        btnSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveuMessagesDB();
                edtContactMessage.setText(null);
                contactScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        contactMessagesPath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                if (dataSnapshot.exists()) {
                    viewuMessages(dataSnapshot);
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String previousChildName) {
                viewuMessages(dataSnapshot);
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

    private void viewuMessages(DataSnapshot dataSnapshot) {
        Iterator iterator = dataSnapshot.getChildren().iterator();

        while (iterator.hasNext()) {
            String chatDateTime = (String) ((DataSnapshot) iterator.next()).getValue();
            String chatMessage = (String) ((DataSnapshot) iterator.next()).getValue();
            String selectUser = (String) ((DataSnapshot) iterator.next()).getValue();
            String sendUser = (String) ((DataSnapshot) iterator.next()).getValue();

            if (activeUsername.equals(sendUser) || activeUsername.equals(selectUser)) {
                if (selectUserName.equals(sendUser) || selectUserName.equals(selectUser)) {
                    txtContactChat.append(sendUser + "->" + selectUser + "\n" + chatMessage + "\n" + chatDateTime + "\n\n");
                }
            }

            contactScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    private void saveuMessagesDB() {
        String message = edtContactMessage.getText().toString();
        String messagesKey = contactMessagesPath.push().getKey();

        if (TextUtils.isEmpty(message)) {
            Toast.makeText(this, "Message field cannot be empty...!!!", Toast.LENGTH_LONG).show();
        } else {

            Calendar datetimeCalendar = Calendar.getInstance();
            SimpleDateFormat activeDateTimeFormat = new SimpleDateFormat("dd.MM.yy HH:mm:ss", Locale.ROOT);
            String activeDateTime = activeDateTimeFormat.format(datetimeCalendar.getTime());

            HashMap<String, Object> contactMessageKey = new HashMap<>();
            contactMessagesPath.updateChildren(contactMessageKey);

            DatabaseReference messagesKeyPath = contactMessagesPath.child(messagesKey);

            HashMap<String, Object> messageValuesMap = new HashMap<>();
            messageValuesMap.put("send_user_tb", activeUsername);
            messageValuesMap.put("select_user_tb", selectUserName);
            messageValuesMap.put("message_tb", message);
            messageValuesMap.put("date_time_tb", activeDateTime);

            messagesKeyPath.updateChildren(messageValuesMap);

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