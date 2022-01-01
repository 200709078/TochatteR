// Adem VAROL - 200709078
package adem.example.tochatter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class ContactsFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<String> arrayContacts = new ArrayList<>();

    private DatabaseReference userPath;
    private String activeUserID = FirebaseAuth.getInstance().getUid();
    private String activeUsername;
    private EditText contactSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View userFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);
        userPath = FirebaseDatabase.getInstance().getReference().child("Users_tb");

        ListView lstView = userFragmentView.findViewById(R.id.contacts_list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, arrayContacts);
        lstView.setAdapter(arrayAdapter);

        takeUserInfo();

        listContacts();

        contactSearch=userFragmentView.findViewById(R.id.edt_contact_search);

        contactSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchContacts(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                contactSearch.setText(null);

                String selectUserName = parent.getItemAtPosition(position).toString();
                Intent contactChatActivity = new Intent(getContext(), ContactChatActivity.class);
                contactChatActivity.putExtra("selectUserName", selectUserName);
                startActivity(contactChatActivity);

            }
        });

        return userFragmentView;
    }

    private void searchContacts(String s) {
        Query query = FirebaseDatabase.getInstance().getReference("Users_tb").orderByChild("uname_tb")
                .startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("uname_tb").getValue().toString().equals(activeUsername)) {
                        set.add(snapshot.child("uname_tb").getValue().toString());
                    }
                }

                arrayContacts.clear();
                arrayContacts.addAll(set);
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void listContacts() {

        userPath.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Set<String> set = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if (!snapshot.child("uname_tb").getValue().toString().equals(activeUsername)) {
                        set.add(snapshot.child("uname_tb").getValue().toString());
                    }
                }

                arrayContacts.clear();
                arrayContacts.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
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