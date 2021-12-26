// Adem VAROL - 200709078
package adem.example.tochatter;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class ContactsFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<String> arrayContacts = new ArrayList<>();

    private DatabaseReference userPath;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View userFragmentView = inflater.inflate(R.layout.fragment_contacts, container, false);
        userPath = FirebaseDatabase.getInstance().getReference().child("Users_tb");

        ListView lstView = userFragmentView.findViewById(R.id.contacts_list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, arrayContacts);
        lstView.setAdapter(arrayAdapter);

        listContacts();

        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String selectUserName = parent.getItemAtPosition(position).toString();
                Intent userChatActivity = new Intent(getContext(), ContactChatActivity.class);
                userChatActivity.putExtra("selectUserName", selectUserName);
                startActivity(userChatActivity);
            }
        });

        return userFragmentView;
    }

    private void listContacts() {
        userPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    set.add(snapshot.child("name_tb").getValue().toString());
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
}