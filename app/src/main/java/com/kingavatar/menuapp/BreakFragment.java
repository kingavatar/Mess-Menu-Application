package com.kingavatar.menuapp;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

public class BreakFragment extends Fragment {
    private static String transition;
    private String text;

    public static void settransitionname(String texts) {
        transition = texts;
    }

    public void addParam(String a) {
        text = a;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.break_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        final CardView cardView = view.findViewById(R.id.card);
        final ImageView icon = view.findViewById(R.id.break_icon_card);
        final ImageView gradient = view.findViewById(R.id.break_gradient_card);
        final TextView texts = view.findViewById(R.id.break_text_card);
        icon.setTransitionName(text + "_icon");
        gradient.setTransitionName(text + "_gradient");
        texts.setTransitionName(text + "_text");
        if (Objects.equals(text, "Breakfast")) {
            icon.setImageResource(R.drawable.breakfast_icon);
            gradient.setImageResource(R.drawable.blue_gradient);
            texts.setText(text);
        } else if (Objects.equals(text, "Lunch")) {
            icon.setImageResource(R.drawable.lunch_icon);
            gradient.setImageResource(R.drawable.orange_gradient);
            texts.setText(text);
        } else if (Objects.equals(text, "Dinner")) {
            icon.setImageResource(R.drawable.dinner_icon);
            gradient.setImageResource(R.drawable.green_gradient);
            texts.setText(text);
        }
        cardView.setOnClickListener(new CardView.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cardView.animate().translationY(-700);
                ((MainActivity) getActivity()).getdashboard(text);

            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
}
