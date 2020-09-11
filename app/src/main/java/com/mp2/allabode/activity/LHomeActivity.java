package com.mp2.allabode.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.room.Room;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
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
import com.mp2.allabode.databse.FlatDatabase;
import com.mp2.allabode.databse.FlatEntity;
import com.mp2.allabode.dialog.LFirstDialog;
import com.mp2.allabode.fragment.LAddFlatFragment;
import com.mp2.allabode.fragment.LProfileFragment;

public class LHomeActivity extends AppCompatActivity implements LFirstDialog.FirstFlatEntry {

    private BottomNavigationView bottomNavigationView;
    private SharedPreferences sharedPreferences;
    private RelativeLayout rlLHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_l_home);
        bottomNavigationView = findViewById(R.id.bottomNavigationLandlord);
        sharedPreferences = getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        rlLHome = findViewById(R.id.rlLActivity);

        rlLHome.setVisibility(View.GONE);

        boolean isFirst = sharedPreferences.getBoolean("isFirst",false);

        if(isFirst){
            DialogFragment dialogFragment = new LFirstDialog(LHomeActivity.this);
            dialogFragment.setCancelable(false);
            dialogFragment.show(getSupportFragmentManager(),"LFirst");
        }else{
            loadActivity();
        }

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if(item.getItemId() == R.id.navl_profile){
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
        rlLHome.setVisibility(View.VISIBLE);
        openProfile();
    }

    public void openProfile(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutLandlord,new LProfileFragment()).commit();
    }

    public void openAddFlat(){
        getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fadein,R.anim.fadeout)
                .replace(R.id.frameLayoutLandlord,new LAddFlatFragment()).commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayoutLandlord);
        if(fragment.getClass().equals(LProfileFragment.class)){
            super.onBackPressed();
        }else{
            bottomNavigationView.setSelectedItemId(R.id.navl_profile);
            openProfile();
        }
    }

    @Override
    public void onInsertFirstFlat(FlatEntity flatEntity) {
        Room.databaseBuilder(LHomeActivity.this, FlatDatabase.class,"flat")
                .allowMainThreadQueries().build().flatDao().insertFlat(flatEntity);
        makeErrorToast("Property Successfully Listed.",null,"");
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(flatEntity.getOwnerMobile(),null,"Congratulations!\nYou property has been successfully listed.",null,null);
        loadActivity();
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
}
