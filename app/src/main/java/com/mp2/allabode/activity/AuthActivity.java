package com.mp2.allabode.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.databse.UserDatabase;
import com.mp2.allabode.databse.UserEntity;

public class AuthActivity extends AppCompatActivity {

    private CardView cvLogin;
    private TextView txtQuote,txtReg,txtLogin;
    private EditText etPass,etMobile;
    private ImageView imLogo;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        cvLogin = findViewById(R.id.cvLogin);
        txtQuote = findViewById(R.id.txtLoginQuote);
        txtReg = findViewById(R.id.txtLoginRegister);
        txtLogin = findViewById(R.id.txtLogin);
        etMobile = findViewById(R.id.etLoginMobile);
        etPass = findViewById(R.id.etLoginPass);
        imLogo = findViewById(R.id.imgLoginLogo);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);

        cvLogin.setVisibility(View.GONE);
        txtReg.setVisibility(View.GONE);
        imLogo.startAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_in));
        txtQuote.startAnimation(AnimationUtils.loadAnimation(this,R.anim.push_up_in));
        cvLogin.startAnimation(AnimationUtils.loadAnimation(this,R.anim.push_left_in));
        txtReg.startAnimation(AnimationUtils.loadAnimation(this,R.anim.push_right_in));
        cvLogin.setVisibility(View.VISIBLE);
        txtReg.setVisibility(View.VISIBLE);

        txtReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AuthActivity.this,RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = etPass.getText().toString().trim();
                String mobile = etMobile.getText().toString().trim();
                clearEditTextFocus();

                if(pass.isEmpty() || mobile.isEmpty()){
                    makeErrorToast("All fields are mandatory",null,"");
                    cvLogin.startAnimation(AnimationUtils.loadAnimation(AuthActivity.this,R.anim.shake));
                }else {
                    UserEntity getUser = Room.databaseBuilder(AuthActivity.this, UserDatabase.class,"user")
                            .allowMainThreadQueries().build().userDao().getUserByMobile(mobile);
                    if(getUser == null){
                        clearEditTextFocus();
                        AlertDialog.Builder dialog = new AlertDialog.Builder(AuthActivity.this);
                        dialog.setCancelable(false);
                        dialog.setMessage("No User Found.\nPlease Register to continue.");
                        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(AuthActivity.this,RegisterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                        dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        dialog.create();
                        dialog.show();
                    }else{
                        if(getUser.getPass().equals(pass)){

                            sharedPreferences.edit().putString("mobile", mobile).apply();
                            sharedPreferences.edit().putString("name", getUser.getName()).apply();
                            sharedPreferences.edit().putString("image", getUser.getImage()).apply();
                            sharedPreferences.edit().putString("email", getUser.getEmail()).apply();
                            sharedPreferences.edit().putString("type", getUser.getType()).apply();
                            sharedPreferences.edit().putBoolean("isLoggedIn",true).apply();

                            if(getUser.getType().equals("Student")){
                                sharedPreferences.edit().putString("age",getUser.getAge()).apply();
                                sharedPreferences.edit().putString("uni",getUser.getUni()).apply();

                                Intent intent = new Intent(AuthActivity.this,SHomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                finish();
                            }else{
                                Intent intent = new Intent(AuthActivity.this,LHomeActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.fadein,R.anim.fadeout);
                                finish();
                            }

                        }else{
                            makeErrorToast("Invalid Password",etPass,"Invalid Password");
                            clearEditTextFocus();
                        }
                    }
                }
            }
        });

    }

    private void clearEditTextFocus() {
        etPass.clearFocus();
        etMobile.clearFocus();
    }

    public void makeErrorToast(String text, EditText editText, String hint){
        View view = LayoutInflater.from(this).inflate(R.layout.toast,null);
        Toast toast = new Toast(this);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
        if(editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(this,R.anim.shake));
            editText.clearFocus();
        }
    }
}
