package com.kingavatar.menuapp;


import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.transition.TransitionInflater;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Objects;


public class DashboardFragment extends Fragment {
    private static Uri ExcelUri;
    private String type;

    public DashboardFragment() {
        // Required empty public constructor
    }

    //Own Methods
    public static void setExcelUri(Uri uri) {
        ExcelUri = uri;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setSharedElementEnterTransition(TransitionInflater.from(getContext()).inflateTransition(android.R.transition.move));
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_dashboard, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Objects.requireNonNull(getActivity()).getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        //Calendar
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int temp = 0;
        if (type == null) {
            if (hour < 9 || (hour == 9 && minute < 45)) {
                temp = 0;
            }
            if ((hour < 14 && hour > 9) || (hour == 9 && minute > 45)) temp = 1;
            else if ((hour < 21 && hour > 14) || (hour == 14 && minute > 30)) temp = 2;
            if (hour >= 22 && hour <= 23) {
                temp = 0;
                if (day < 6) {
                    day++;
                } else day = 0;
            }
            if (temp == 0) type = ("Breakfast");
            else if (temp == 1) type = ("Lunch");
            else if (temp == 2) type = ("Dinner");
        } else {
            if (type.equals("Breakfast")) temp = 0;
            else if (type.contains("Lunch")) temp = 1;
            else if (type.equals("Dinner")) temp = 2;
        }
        //Getting filename from shared_preference
        SharedPreferences settings = PreferenceManager.getDefaultSharedPreferences(getActivity());
        if (settings.contains("file_uri")) {
            ExcelUri = Uri.parse(settings.getString("file_uri", null));
            //Toast.makeText(getActivity(),settings.getString("file_uri","defaultString"), Toast.LENGTH_LONG).show();
        }
        final ImageView gradient = view.findViewById(R.id.break_gradient);
        final ImageView icon = view.findViewById(R.id.break_icon);
        final TextView text = view.findViewById(R.id.break_text);
        final CardView cardView = view.findViewById(R.id.dashboard_card);
        final String[] Type = {"Breakfast", "Lunch", "Dinner"};
        Log.d("tran", Type[temp]);
        gradient.setTransitionName(Type[temp] + "_gradient");
        icon.setTransitionName(Type[temp] + "_icon");
        text.setTransitionName(Type[temp] + "_text");
        if (temp == 0) {
            gradient.setImageResource(R.drawable.blue_gradient);
            icon.setImageResource(R.drawable.breakfast_icon);
            text.setText("Breakfast");
        } else if (temp == 1) {
            gradient.setImageResource(R.drawable.orange_gradient);
            icon.setImageResource(R.drawable.lunch_icon);
            text.setText("Lunch");
        } else if (temp == 2) {
            gradient.setImageResource(R.drawable.green_gradient);
            icon.setImageResource(R.drawable.dinner_icon);
            text.setText("Dinner");
        }
        final ListView menu_items = view.findViewById(R.id.menu_items);
        Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(day, 2, Type[temp]));
        if (readExceldata(day, 2, Type[temp]).isEmpty())
            arrayAdapter = new Menu_itemsAdapter(getActivity(), new ArrayList<Menu_items>(Arrays.asList(new Menu_items("Upload a Excel File"))));
        menu_items.setAdapter(arrayAdapter);
        final CardPageringFragment cardPageringFragment = new CardPageringFragment();
        cardPageringFragment.setcurrentitem(temp);
        cardView.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            @Override
            public void onSwipeDown() {
                ((MainActivity) getActivity()).getViewpager(cardPageringFragment);
                /*((MainActivity) getActivity()).getFm().beginTransaction()
//                        .addSharedElement(gradient, gradient.getTransitionName())
//                        .addSharedElement(icon, icon.getTransitionName())
//                        .addSharedElement(text, text.getTransitionName())
                        .replace(R.id.frame_layout, cardPageringFragment)
                        .addToBackStack("transition")
                        .commit();
*/
            }
        });
        final int[] record = {temp};
        //Toggle Buttons
        final ToggleButton[] toggleButton = new ToggleButton[7];
        toggleButton[0] = view.findViewById(R.id.tD);
        toggleButton[1] = view.findViewById(R.id.tL);
        toggleButton[2] = view.findViewById(R.id.tM);
        toggleButton[3] = view.findViewById(R.id.tMi);
        toggleButton[4] = view.findViewById(R.id.tJ);
        toggleButton[5] = view.findViewById(R.id.tV);
        toggleButton[6] = view.findViewById(R.id.tS);
        toggleButton[day].setChecked(true);
//        Animation listanimation = AnimationUtils.loadAnimation(getActivity(), R.anim.layout_animation_fall_up);
        toggleButton[0].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 1; i < 7; i++) {
                        toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(0, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[1].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 1) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(1, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[2].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 2) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(2, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[3].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 3) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(3, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[4].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 4) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(4, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[5].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 5) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(5, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });
        toggleButton[6].setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (int i = 0; i < 7; i++) {
                        if (i != 6) toggleButton[i].setChecked(false);
                    }
                    Menu_itemsAdapter arrayAdapter = new Menu_itemsAdapter(getActivity(), readExceldata(6, 2, Type[record[0]]));
                    menu_items.setAdapter(arrayAdapter);
                    menu_items.setVisibility(View.GONE);
                    menu_items.startLayoutAnimation();
                    menu_items.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    public ArrayList<Menu_items> readExceldata(int pos, int type, String menu_type) {

        //Calendar calendar = Calendar.getInstance(Locale.getDefault());
        ArrayList<Menu_items> stringBuilder = new ArrayList<>();
        try {
            Cursor cursor = new DataBaseHelper(getContext()).getitems(pos, menu_type);
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                if (type == 1) {
                    stringBuilder.add(new Menu_items((cursor.getString(type))));/*.add("\n");*/
                } else {
                    if (!TextUtils.isEmpty(cursor.getString(type)))
                        stringBuilder.add(new Menu_items(cursor.getString(type).substring(0, 1).toUpperCase() + cursor.getString(type).substring(1).toLowerCase()));/*.append("\n");*/
                }
                cursor.moveToNext();
            }
        } catch (InvalidFormatException e) {
            e.printStackTrace();
            stringBuilder.add(new Menu_items("InvalidFormat " + e.getMessage()));
        }
        return stringBuilder;
    }
}
