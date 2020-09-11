package com.mp2.allabode.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.telephony.SmsManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mp2.allabode.R;
import com.mp2.allabode.adapter.PropertyAdapter;
import com.mp2.allabode.databse.FlatDatabase;
import com.mp2.allabode.databse.FlatEntity;
import com.mp2.allabode.dialog.ExpandImageDialog;
import com.mp2.allabode.dialog.LFirstDialog;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class LAddFlatFragment extends Fragment implements LFirstDialog.FirstFlatEntry, PropertyAdapter.OnStudentRequest {

    public LAddFlatFragment() {
        // Required empty public constructor
    }


    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    PropertyAdapter propertyAdapter;
    SharedPreferences sharedPreferences;
    List<FlatEntity> myFlat;
    TextView txtAdd;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_l_add_flat, container, false);

        recyclerView = view.findViewById(R.id.recyclerOwnerProperty);
        layoutManager = new LinearLayoutManager(getActivity());
        sharedPreferences = getActivity().getSharedPreferences(getString(R.string.pref_file), Context.MODE_PRIVATE);
        txtAdd = view.findViewById(R.id.txtAFNew);

        recyclerView.setVisibility(View.GONE);

        myFlat = Room.databaseBuilder(getActivity(), FlatDatabase.class,"flat")
                .allowMainThreadQueries().build().flatDao().getFlatByOwnerMobile(sharedPreferences.getString("mobile",""));

        propertyAdapter = new PropertyAdapter(getActivity(),myFlat,LAddFlatFragment.this);

        recyclerView.setAdapter(propertyAdapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.startAnimation(AnimationUtils.loadAnimation(getActivity(),R.anim.push_up_in));
        recyclerView.setVisibility(View.VISIBLE);

        txtAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment dialogFragment = new LFirstDialog(LAddFlatFragment.this);
                dialogFragment.setCancelable(false);
                dialogFragment.show(getActivity().getSupportFragmentManager(),"AddPro");
            }
        });

        return view;
    }

    @Override
    public void onInsertFirstFlat(FlatEntity flatEntity) {
        Room.databaseBuilder(getActivity(),FlatDatabase.class,"flat")
                .allowMainThreadQueries().build().flatDao().insertFlat(flatEntity);
        myFlat.add(flatEntity);
        propertyAdapter.notifyDataSetChanged();
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(flatEntity.getOwnerMobile(),null,"Congratulations!\nYou property has been successfully listed.",null,null);

        makeErrorToast("Property Successfully Listed.",null,"");
    }

    public void makeErrorToast(String text, EditText editText, String hint) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.toast, null);
        Toast toast = new Toast(getActivity());
        TextView textView = view.findViewById(R.id.toast_text);
        textView.setText(text);
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.show();
        if (editText != null) {
            editText.setText(null);
            editText.setHint(hint);
            editText.setHintTextColor(getResources().getColor(R.color.colorAccent));
            editText.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.shake));
            editText.clearFocus();
        }
    }

    @Override
    public void onStudentRequest(@NotNull FlatEntity flatEntity) {

    }

    @Override
    public void onImageExpand(@NotNull String url) {
        DialogFragment dialogFragment = new ExpandImageDialog(url);
        dialogFragment.show(getActivity().getSupportFragmentManager(),"ImageExpand");
    }
}
