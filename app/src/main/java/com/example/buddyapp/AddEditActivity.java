package com.example.buddyapp;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class AddEditActivity extends AppCompatActivity {

    DBHelper dbHelper;
    EditText etName, etHp, etEmail, etAddr1, etAddr2, etAddr3;
    Spinner spinnerState;
    RadioGroup rgGender;
    Button btnDelete;
    private int userId;
    private int friendId = -1; // -1 means new friend
    private ArrayAdapter<CharSequence> stateAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DBHelper(this);

        userId = getIntent().getIntExtra("USER_ID", -1);
        if (userId == -1) {
            Toast.makeText(this, "User not identified. Please login again.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        etName = findViewById(R.id.etName);
        etHp = findViewById(R.id.etHp);
        etEmail = findViewById(R.id.etEmail);
        etAddr1 = findViewById(R.id.etAddr1);
        etAddr2 = findViewById(R.id.etAddr2);
        etAddr3 = findViewById(R.id.etAddr3);
        spinnerState = findViewById(R.id.spinnerState);
        rgGender = findViewById(R.id.rgGender);
        btnDelete = findViewById(R.id.btnDelete);

        // Setup Spinner
        stateAdapter = ArrayAdapter.createFromResource(this,
                R.array.states_array, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerState.setAdapter(stateAdapter);

        if (getIntent().hasExtra("ID")) {
            friendId = getIntent().getIntExtra("ID", -1);
            loadFriendData(userId, friendId);
            btnDelete.setVisibility(View.VISIBLE);
        } else {
            btnDelete.setVisibility(View.GONE);
        }
    }

    private void loadFriendData(int userId, int friendId) {
        Friend friend = dbHelper.getFriendById(userId, friendId);
        if (friend != null) {
            etName.setText(friend.getName());
            etHp.setText(friend.getHpNo());
            etEmail.setText(friend.getEmail());
            etAddr1.setText(friend.getAddress1());
            etAddr2.setText(friend.getAddress2());
            etAddr3.setText(friend.getAddress3());

            // Set spinner selection
            int spinnerPosition = stateAdapter.getPosition(friend.getAddress4());
            spinnerState.setSelection(spinnerPosition);

            if (friend.getGender().equals("Male")) {
                ((RadioButton) findViewById(R.id.rbMale)).setChecked(true);
            } else {
                ((RadioButton) findViewById(R.id.rbFemale)).setChecked(true);
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
        String state = spinnerState.getSelectedItem().toString();

        int selectedId = rgGender.getCheckedRadioButtonId();
        RadioButton radioButton = findViewById(selectedId);
        String gender = (radioButton != null) ? radioButton.getText().toString() : "Male";

        if (friendId == -1) {
            dbHelper.addFriend(userId, name, gender, hp, email, a1, a2, a3, state);
            Toast.makeText(this, "Friend Added!", Toast.LENGTH_SHORT).show();
        } else {
            dbHelper.updateFriend(userId, friendId, name, gender, hp, email, a1, a2, a3, state);
            Toast.makeText(this, "Friend Updated!", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    public void deleteFriend(View view) {
        if (friendId != -1) {
            dbHelper.deleteFriend(userId, friendId);
            Toast.makeText(this, "Friend Deleted", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
