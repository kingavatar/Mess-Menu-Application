package com.kingavatar.menuapp;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FeedbackFragment extends Fragment {
    private String name;
    private String type;
    private String feedback;

    public FeedbackFragment() {

    }

    void set_item(Context context, String name, String type) {
        this.name = context.getResources().getString(R.string.item_name);
        this.type = context.getResources().getString(R.string.served_at);
        this.name = this.name + " " + name;
        this.type = this.type + " " + type;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.feedback, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ((TextView) view.findViewById(R.id.fd_item_name)).setText(name);
        ((TextView) view.findViewById(R.id.fd_item_type)).setText(type);
        Button btn = view.findViewById(R.id.fd_send_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback = ((EditText) v.findViewById(R.id.fd_edit)).getText().toString();

            }
        });
    }

}
