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
import android.widget.EditText;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;;
import java.util.Set;

public class GroupsFragment extends Fragment {
    private ArrayAdapter<String> arrayAdapter;
    private final ArrayList<String> arrayGroups = new ArrayList<>();

    private String activeUserId = FirebaseAuth.getInstance().getUid();
    private DatabaseReference groupPath;
    EditText groupSearch;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View groupFragmentView = inflater.inflate(R.layout.fragment_groups, container, false);
        groupPath = FirebaseDatabase.getInstance().getReference().child("Groups_tb");

        ListView lstView = groupFragmentView.findViewById(R.id.groups_list_view);
        arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_expandable_list_item_1, arrayGroups);
        lstView.setAdapter(arrayAdapter);

        listGroups();









        lstView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String activeGroupName = parent.getItemAtPosition(position).toString();
                Intent groupChatActivity = new Intent(getContext(), GroupChatActivity.class);
                groupChatActivity.putExtra("groupName", activeGroupName);
                startActivity(groupChatActivity);
            }
        });

        return groupFragmentView;
    }

    private void listGroups() {
        groupPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Set<String> set = new HashSet<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    set.add(snapshot.getKey());
                }

                arrayGroups.clear();
                arrayGroups.addAll(set);
                arrayAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });




    }
}