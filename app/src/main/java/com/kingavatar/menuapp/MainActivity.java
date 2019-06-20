package com.kingavatar.menuapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    private Boolean exit = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference menu_items = FirebaseFirestore.getInstance().collection("/menu_items/Testing/testing_items");
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private final Fragment fragment1 = new AboutFragment();
    private final Fragment fragment3 = new UploadFileFragment();

    public FragmentManager getFm() {
        return fm;
    }
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment fragment2 = new DashboardFragment();
    private Fragment active = fragment2;
    private TextView mTextMessage;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            Class fragmentClass;
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
//                      findViewById(R.id.navigation).getRootView().setBackgroundResource(R.color.white);
                    fm.beginTransaction().hide(active).show(fragment1).commit();
                    active = fragment1;
                    return true;

                case R.id.navigation_dashboard:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        getWindow().getDecorView().setSystemUiVisibility(0);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
                    }
                    fm.beginTransaction().hide(active).show(fragment2).commit();
                    active = fragment2;
                    return true;

                case R.id.navigation_notifications:
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                    }
//                    findViewById(R.id.navigation).getRootView().setBackgroundResource(R.color.white);

                    fm.beginTransaction().hide(active).show(fragment3).commit();
                    active = fragment3;
                    return true;
            }
            return false;
        }
    };

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyStoragePermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    public void getViewpager(Fragment card_fragment) {
        fragment2 = card_fragment;
        fm.beginTransaction().replace(R.id.frame_layout, fragment2).addToBackStack("transition").commit();
        fm.beginTransaction().add(R.id.frame_layout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.frame_layout, fragment1, "1").hide(fragment1).commit();
        ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_dashboard);
    }
    public void getdashboard(String text) {
//        if (((DashboardFragment) fragment2).getType().equals(text)) onBackPressed();
//        else {
            fragment2 = new DashboardFragment();
            ((DashboardFragment) fragment2).setType(text);
            fm.beginTransaction()
//                    .addSharedElement(findViewById(R.id.break_icon_card), findViewById(R.id.break_icon_card).getTransitionName())
//                    .addSharedElement(findViewById(R.id.break_gradient_card), findViewById(R.id.break_gradient_card).getTransitionName())
//                    .addSharedElement(findViewById(R.id.break_text_card), findViewById(R.id.break_text_card).getTransitionName())
                    .replace(R.id.frame_layout, fragment2).addToBackStack("back_transition").commit();
            fm.beginTransaction().add(R.id.frame_layout, fragment3, "3").hide(fragment3).commit();
            fm.beginTransaction().add(R.id.frame_layout, fragment1, "1").hide(fragment1).commit();
            ((BottomNavigationView) findViewById(R.id.navigation)).setSelectedItemId(R.id.navigation_dashboard);
//        }
    }

    public void after_upload() {
        fm.beginTransaction().remove(fragment2).commit();
        fragment2 = new DashboardFragment();
        fm.beginTransaction().add(R.id.frame_layout, fragment2, "2").hide(fragment2).commit();
    }

    public String getmenu_items() {
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
        dataBaseHelper.onUpgrade(dataBaseHelper.getWritableDatabase(), DataBaseHelper.getVersionNumber(), DataBaseHelper.getVersionNumber() + 1);
        dataBaseHelper.insert_blank_rows(42);
        menu_items.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    ContentValues contentValues;
                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                        contentValues = new ContentValues();
                        contentValues.put("Type", document.getString("Day"));
                        contentValues.put("Description", document.getString("Description"));
                        contentValues.put("Monday", document.getString("Monday"));
                        contentValues.put("Tuesday", document.getString("Tuesday"));
                        contentValues.put("Wednesday", document.getString("Wednesday"));
                        contentValues.put("Thursday", document.getString("Thursday"));
                        contentValues.put("Friday", document.getString("Friday"));
                        contentValues.put("Saturday", document.getString("Saturday"));
                        contentValues.put("Sunday", document.getString("Sunday"));
                        dataBaseHelper.addItems(getApplicationContext(), contentValues, document.getId());
                    }
                }
            }
        });
        return "Database Downloaded";
    }
    @Override
    public void onBackPressed() {
        Fragment f = fm.findFragmentById(R.id.frame_layout);
        if (f instanceof CardPageringFragment) super.onBackPressed();
        else {
            if (exit) {
                finish(); // finish activity
            } else {
                Toast.makeText(this, "Press Back again to Exit",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(0x00000000);
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        verifyStoragePermissions(this);
        mTextMessage = findViewById(R.id.message);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setSelectedItemId(R.id.navigation_dashboard);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        fm.beginTransaction().add(R.id.frame_layout, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.frame_layout, fragment1, "1").hide(fragment1).commit();
        fm.beginTransaction().add(R.id.frame_layout, fragment2, "2").commit();
        //getmenu_items();
    }
//    private boolean loadFragment(Fragment fragment) {
//        //switching fragment
//        if (fragment != null) {
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .replace(R.id.frame_layout, fragment)
//                    .commit();
//            return true;
//        }
//        return false;
//    }
}
