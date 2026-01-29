package com.example.buddyapp;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;
    RecyclerView recyclerView;
    SearchView searchView;
    FriendAdapter adapter;
    ArrayList<Friend> friendList = new ArrayList<>();
    private int userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dbHelper = new DBHelper(this);
        searchView = findViewById(R.id.searchView);
        recyclerView = findViewById(R.id.recyclerView);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not identified.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new FriendAdapter(friendList);
        recyclerView.setAdapter(adapter);

        loadFriends("");

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
    }

    private void loadFriends(String query) {
        friendList.clear();
        friendList.addAll(dbHelper.getAllFriends(userId, query));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (searchView != null) {
            loadFriends(searchView.getQuery().toString());
        }
    }

    // --- Fixed Adapter Class ---
    class FriendAdapter extends RecyclerView.Adapter<FriendAdapter.ViewHolder> {
        ArrayList<Friend> list;

        FriendAdapter(ArrayList<Friend> list) {
            this.list = list;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_friend, parent, false);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Friend f = list.get(position);
            holder.name.setText(f.getName());
            holder.address.setText(f.getAddress4());

            // Dynamic Color Logic based on Gender
            if (f.getGender() != null && f.getGender().equalsIgnoreCase("Male")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#E3F2FD")); // Light Blue
            } else if (f.getGender() != null && f.getGender().equalsIgnoreCase("Female")) {
                holder.card.setCardBackgroundColor(Color.parseColor("#FFEBEE")); // Light Pink/Red
            } else {
                holder.card.setCardBackgroundColor(Color.WHITE);
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, AddEditActivity.class);
                intent.putExtra("USER_ID", userId);
                intent.putExtra("ID", f.getId());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        // Single, Correct ViewHolder Class
        class ViewHolder extends RecyclerView.ViewHolder {
            TextView name, address;
            MaterialCardView card;

            ViewHolder(View itemView) {
                super(itemView);
                name = itemView.findViewById(R.id.txtName);
                address = itemView.findViewById(R.id.txtAddress);
                card = itemView.findViewById(R.id.friendCard);
            }
        }
    }

    public void goToAdd(View view) {
        Intent intent = new Intent(this, AddEditActivity.class);
        intent.putExtra("USER_ID", userId);
        startActivity(intent);
    }

    public void goToReports(View view) {
        Intent intent = new Intent(this, ReportActivity.class);
        intent.putExtra("USER_ID", userId);
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
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}