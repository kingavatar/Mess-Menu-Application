package com.kingavatar.menuapp;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Menu_itemsAdapter extends ArrayAdapter<Menu_items> {
    private Context context;
    private List<Menu_items> menu_items;

    Menu_itemsAdapter(Context context, ArrayList<Menu_items> menuItems) {
        super(context, 0, menuItems);
        this.context = context;
        menu_items = menuItems;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if (listItem == null)
            listItem = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        final Menu_items current_item = menu_items.get(position);
        TextView textView = listItem.findViewById(R.id.menu_items_list);
        textView.setText(current_item.getItems());
        RatingBar ratingBar = listItem.findViewById(R.id.rating);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                current_item.setRating(rating);
            }
        });
        if (current_item.getRating() == null) ratingBar.setRating(0);
        else ratingBar.setRating(current_item.getRating());
        return listItem;
    }
}
