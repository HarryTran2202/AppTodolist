package com.example.todolist;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.todolist.api.ApiService;
import com.example.todolist.model.User;
import com.example.todolist.response.MessageRes;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInfo extends AppCompatActivity {
    private EditText editTextName, editTextPhoneNumber, editTextAddress, editTextDescription, editTextEmail;
    private Button btn;
    String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        Intent i = getIntent();
        token = i.getStringExtra("accessToken");
        Log.e("null", "token on usserInfo" + token);
        init();
        displayInfo();
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editInfo();
            }
        });

    }

    private void init() {
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhoneNumber = findViewById(R.id.editTextPhoneNumber);
        editTextDescription = findViewById(R.id.editTextDescription);
        editTextAddress = findViewById(R.id.editTextAddress);
        btn = findViewById(R.id.button);
    }

    private void displayInfo() {
        ApiService.apiService.getInfo("Bearer "+token).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()) {
                    User info = response.body();
                    editTextName.setText(info.getName());
                    editTextPhoneNumber.setText(info.getPhone_number());
                    editTextDescription.setText(info.getDescription());
                    editTextAddress.setText(info.getAddress());
                    editTextEmail.setText(info.getEmail());
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(UserInfo.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(UserInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(UserInfo.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }

    private void editInfo() {
        String txtName, txtEmail, txtPhone, txtDescription, txtAddress;
        txtName = editTextName.getText().toString().trim();
        txtPhone = editTextPhoneNumber.getText().toString().trim();
        txtDescription = editTextDescription.getText().toString().trim();
        txtAddress = editTextAddress.getText().toString().trim();
        txtEmail = editTextEmail.getText().toString().trim();
        if (txtName.isEmpty() || txtPhone.isEmpty() || txtDescription.isEmpty() || txtAddress.isEmpty() || txtEmail.isEmpty()) {
            Toast.makeText(UserInfo.this, "Hãy điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }
        User info = new User(txtName, txtEmail, txtAddress, txtPhone, txtDescription);
        ApiService.apiService.editInfo("Bearer "+token, info).enqueue(new Callback<MessageRes>() {
            @Override
            public void onResponse(Call<MessageRes> call, Response<MessageRes> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(UserInfo.this, "Thay đổi " + response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        JSONObject jObjError = new JSONObject(response.errorBody().string());
                        Toast.makeText(UserInfo.this, jObjError.getString("message"), Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Toast.makeText(UserInfo.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<MessageRes> call, Throwable t) {
                System.out.println(t.getMessage());
                Toast.makeText(UserInfo.this, t.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }
        });
    }
}