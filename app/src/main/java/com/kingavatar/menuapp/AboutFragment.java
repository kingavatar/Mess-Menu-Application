package com.kingavatar.menuapp;


import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class AboutFragment extends Fragment {


    public AboutFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.about_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        //((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar().hide();
        TextView text = view.findViewById(R.id.about_txt);
        //language=HTML
        String txt = "Created and Developed by<b><font color=#673AB7> KingAvatar  </font></b>(<font color =#673AB7>Saikiran</font>)\n\nVersion\n <b><font color=#000000> 1.0.1</font>" +
                "</b>\n\nSource Code \n<a href='https://github.com/kingavatar/What-The-Food'>https://github.com/kingavatar/What-The-Food</a>";
        txt = txt.replace("\n", "<br>");
        text.setText(Html.fromHtml(txt));
        TextView rag = view.findViewById(R.id.anurag);
        //language=HTML
        String rag_txt = "<font color=#4f5051>App Logo designed by </font><b><font color=#0033cc>Anurag Pendyala</font></b>";
        rag.setText(Html.fromHtml(rag_txt));
        super.onViewCreated(view, savedInstanceState);
    }
}
