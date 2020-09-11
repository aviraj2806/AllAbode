package com.mp2.allabode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.mp2.allabode.R;
import com.mp2.allabode.databse.UserDatabase;
import com.mp2.allabode.dialog.SFirstDialog;
import com.mp2.allabode.fragment.SProfileFragment;
import com.mp2.allabode.fragment.SRequestFragment;

public class SHomeActivity extends AppCompatActivity implements SFirstDialog.StudentFirstDetails {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private RelativeLayout rlSHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_s_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationStudent);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        rlSHome = findViewById(R.id.rlSActivity);

        rlSHome.setVisibility(View.GONE);

        boolean isFirst = sharedPreferences.getBoolean("isFirst",false);

        if(isFirst){
            DialogFragment dialogFragment = new SFirstDialog(SHomeActivity.this);
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(),"LFirst");
        }else{
            loadActivity();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navs_profile){
                    openProfile();
                    return true;
                }else{
                    openAddFlat();
                    return true;
                }
            }
        });

    }

    private void loadActivity() {
        rlSHome.setVisibility(View.VISIBLE);
        openProfile();
    }

    public void openProfile(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutStudent,new SProfileFragment()).commit();
    }

    public void openAddFlat(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutStudent,new SRequestFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutStudent);
        if(fragment.getClass().equals(SProfileFragment.class)){
            super.onBackPressed();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.navs_profile);
            openProfile();
        }
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(this).inflate(R.layout.toast, null);
        Toast toast = new Toast(this);
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(this, R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    public void onUpdateStudent(String age, String uni) {
        Room.databaseBuilder(SHomeActivity.this, UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().updateStudentAge(age,sharedPreferences.getString("mobile",""));
        Room.databaseBuilder(SHomeActivity.this,UserDatabase.class,"user")
                .allowMainThreadQueries().build().userDao().updateStudentUniversity(uni,sharedPreferences.getString("mobile",""));
        sharedPreferences.edit().putString("age",age).apply();
        sharedPreferences.edit().putString("uni",uni).apply();
        sharedPreferences.edit().putBoolean("isFirst",false).apply();
        makeErrorToast("Details Updated",null,"");
        loadActivity();
    }
}
