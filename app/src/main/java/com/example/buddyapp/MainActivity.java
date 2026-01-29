package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    ListView listView;
    SearchView searchView;
    ArrayList<Friend> friendList;
    ArrayList<String> displayList;
    ArrayAdapter<String> adapter;
    private int userId; // To store the logged-in user's ID

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
        listView = findViewById(R.id.listView);
        searchView = findViewById(R.id.searchView);

        // Get the userId from the intent
        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            // If for some reason userId is not passed, handle it
            // For example, go back to login
            Toast.makeText(this, "User not identified. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        loadFriends(""); // Load all friends for the current user

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                loadFriends(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                loadFriends(newText);
                return false;
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Friend selectedFriend = friendList.get(position);
            Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
            intent.putExtra("USER_ID", userId); // Pass user ID
            intent.putExtra("ID", selectedFriend.getId()); // Pass friend ID to edit
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId != -1) {
            loadFriends(searchView.getQuery().toString()); // Refresh list with current search query
        }
    }

    private void loadFriends(String query) {
        friendList = dbHelper.getAllFriends(userId, query); // Pass userId to get specific friends
        displayList = new ArrayList<>();
        for (Friend f : friendList) {
            displayList.add(f.getName() + " (" + f.getAddress4() + ")");
        }
        if (adapter == null) {
            adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, displayList);
            listView.setAdapter(adapter);
        } else {
            adapter.clear();
            adapter.addAll(displayList);
            adapter.notifyDataSetChanged();
        }
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("USER_ID", userId); // Pass user ID
        startActivity(intent);
    }

    public void goToReports(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("USER_ID", userId); // Pass user ID
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            // Handle logout
            Intent intent = new Intent(MainActivity.this, LoginActvity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
