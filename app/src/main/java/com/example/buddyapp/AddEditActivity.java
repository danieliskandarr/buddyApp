package com.example.buddyapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText etName, etHp, etEmail, etAddr1, etAddr2, etAddr3, etAddr4;
    RadioGroup rgGender;
    Button btnDelete;
    int friendId = -1; // -1 means new friend

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dbHelper = new DBHelper(this);

        // Initialize UI components (Make sure IDs match your XML)
        etName = findViewById(R.id.etName);
        etHp = findViewById(R.id.etHp);
        etEmail = findViewById(R.id.etEmail);
        etAddr1 = findViewById(R.id.etAddr1);
        etAddr2 = findViewById(R.id.etAddr2);
        etAddr3 = findViewById(R.id.etAddr3);
        etAddr4 = findViewById(R.id.etAddr4);
        rgGender = findViewById(R.id.rgGender);
        btnDelete = findViewById(R.id.btnDelete);

        // Check if we are Editing or Creating
        if (getIntent().hasExtra("ID")) {
            friendId = getIntent().getIntExtra("ID", -1);
            loadFriendData(friendId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void loadFriendData(int id) {
        // In a real app, query by ID. Here we just grab from search for simplicity
        // ideally, add getFriendById(int id) in DBHelper
        for(Friend f : dbHelper.getAllFriends("")) {
            if(f.getId() == id) {
                etName.setText(f.getName());
                etHp.setText(f.getHpNo());
                etEmail.setText(f.getEmail());
                etAddr1.setText(f.getAddress1());
                etAddr2.setText(f.getAddress2());
                etAddr3.setText(f.getAddress3());
                etAddr4.setText(f.getAddress4());
                if(f.getGender().equals("Male")) ((RadioButton)findViewById(R.id.rbMale)).setChecked(true);
                else ((RadioButton)findViewById(R.id.rbFemale)).setChecked(true);
                break;
            }
        }
    }

    public void saveFriend(View view) {
        String name = etName.getText().toString();
        String hp = etHp.getText().toString();
        String email = etEmail.getText().toString();
        String a1 = etAddr1.getText().toString();
        String a2 = etAddr2.getText().toString();
        String a3 = etAddr3.getText().toString();
        String a4 = etAddr4.getText().toString();

        // Get selected gender
        int selectedId = rgGender.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = (radioButton != null) ? radioButton.getText().toString() : "Male";

        if (friendId == -1) {
            dbHelper.addFriend(name, gender, hp, email, a1, a2, a3, a4);
            Toast.makeText(this, "Friend Added!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateFriend(friendId, name, gender, hp, email, a1, a2, a3, a4);
            Toast.makeText(this, "Friend Updated!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void deleteFriend(View view) {
        if (friendId != -1) {
            dbHelper.deleteFriend(friendId);
            Toast.makeText(this, "Friend Deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }
}