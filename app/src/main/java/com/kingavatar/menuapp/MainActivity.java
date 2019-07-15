package com.kingavatar.menuapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.util.ExtraConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.ActionCodeSettings;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Source;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.Nullable;

public class MainActivity extends AppCompatActivity {
    private Boolean exit = false;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private static final int RC_SIGN_IN = 1403;
    private final Fragment fragment1 = new ProfileFragment();
    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };
    private DocumentReference menu_items = FirebaseFirestore.getInstance().collection("menu_items").document("menu_items");
    private final Fragment fragment3 = new UploadFileFragment();
    private FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
            .setPersistenceEnabled(true)
            .build();
    public FragmentManager getFm() {
        return fm;
    }
    private final FragmentManager fm = getSupportFragmentManager();
    private Fragment fragment2 = new DashboardFragment();

    public void setActive(Fragment active) {
        this.active = active;
    }

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

    public void checkCurrentUser() {
        // [START check_current_user]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // User is signed in
            Toast.makeText(this, " " + user, Toast.LENGTH_LONG).show();
            getProviderData();
        } else {
            // No user is signed in
            //startActivity(new Intent(this,AuthActivity.class));
            FirebaseDynamicLinks.getInstance()
                    .getDynamicLink(getIntent())
                    .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                        @Override
                        public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                            // Get deep link from result (may be null if no link is found)
                            Uri deepLink = null;
                            if (pendingDynamicLinkData != null) {
                                deepLink = pendingDynamicLinkData.getLink();
                                Log.d("firebase1", String.valueOf(deepLink));
                                //if (AuthUI.canHandleIntent(getIntent())) {
                                if (getIntent().getExtras() != null) {
                                    String link = getIntent().getExtras().getString(ExtraConstants.EMAIL_LINK_SIGN_IN);
                                    Log.d("firebase1", "Link : " + link);
                                }
                                if (deepLink != null) {
                                    startActivityForResult(
                                            AuthUI.getInstance()
                                                    .createSignInIntentBuilder()
                                                    .setEmailLink(deepLink.toString())
                                                    //.setAvailableProviders(providers)
                                                    .build(),
                                            RC_SIGN_IN);
                                }
                                //Objects.requireNonNull(user).reload();
                                //}
                            } else {
                                Log.d("firebase1", "creating");
                                createSignInIntent();
                            }

                            // Handle the deep link. For example, open the linked
                            // content, or apply promotional credit to the user's
                            // account.
                            // ...

                            // ...
                        }
                    })
                    .addOnFailureListener(this, new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w("firebase1", "getDynamicLink:onFailure", e);
                        }
                    });

        }
        // [END check_current_user]
    }

    public void createSignInIntent() {
        String url = "https://menuapp.page.link/SignIn";// + String.format(Locale.getDefault(),"%04d", new Random().nextInt(1001));;
        ActionCodeSettings actionCodeSettings = ActionCodeSettings.newBuilder()
                .setAndroidPackageName("com.kingavatar.menuapp", /*installIfNotAvailable*/true, /*minimumVersion*/null)
                .setHandleCodeInApp(true)
                .setUrl(url) // This URL needs to be whitelisted
                .build();
        // [START auth_fui_create_intent]
        // Choose authentication providers
        final List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().enableEmailLinkSignIn()
                        .setActionCodeSettings(actionCodeSettings).build(),
                //new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        //new AuthUI.IdpConfig.FacebookBuilder().build(),
        //new AuthUI.IdpConfig.TwitterBuilder().build());


        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setIsSmartLockEnabled(false)
                        .setLogo(R.mipmap.ic_launcher)
                        .setTheme(R.style.AuthenticationTheme)
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_create_intent]

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                Log.d("firebase1", "finish");
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));

            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }
        }

    }

    public void getProviderData() {
        // [START get_provider_data]
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            for (UserInfo profile : user.getProviderData()) {
                // Id of the provider (ex: google.com)
                String providerId = profile.getProviderId();

                // UID specific to the provider
                String uid = profile.getUid();

                // Name, email address, and profile photo Url
                String name = profile.getDisplayName();
                String email = profile.getEmail();
                Uri photoUrl = profile.getPhotoUrl();
                Log.d("firebase1", "Details " + providerId + " " + uid + " " + name + " " + email);
            }
        }
        // [END get_provider_data]
    }

    public void signOut() {
        // [START auth_fui_signout]
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        // ...
                    }
                });
        // [END auth_fui_signout]
    }

    public void getTokenId() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w("firebase1", "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Map<String, Object> data = new HashMap<>();
                        if (user != null) {
                            data.put(user.getEmail(), token);
                            Log.d("firebase1", "send Registration to server : " + user.getEmail());
                            FirebaseFirestore.getInstance().collection("users").document("fcm_tokens").set(data, SetOptions.merge());
                        }
                        // Log and toast
                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.d("firebase1", msg);
                    }
                });
        FirebaseMessaging.getInstance().subscribeToTopic("menu")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = getString(R.string.msg_subscribed);
                        if (!task.isSuccessful()) {
                            msg = getString(R.string.msg_subscribe_failed);
                        }
                        Log.d("firebase1", msg);
                        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
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
        //fm.beginTransaction().remove(fragment2).commit();
        //fragment2 = new DashboardFragment();
        //fm.beginTransaction().add(R.id.frame_layout, fragment2, "2").hide(fragment2).commit();
        ((DashboardFragment) fragment2).setmenuadapter();
    }

    public void getreal_time_menu_items() {
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
        //dataBaseHelper.insert_blank_rows(42);
        final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        // Source can be CACHE, SERVER, or DEFAULT.
        Source source = Source.CACHE;
        menu_items.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("firestore", "Listen failed.", e);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Log.d("firestore", "Current data: " + documentSnapshot.getData());
                    ContentValues contentValues;
                    dataBaseHelper.onUpgrade(dataBaseHelper.getWritableDatabase(), DataBaseHelper.getVersionNumber(), DataBaseHelper.getVersionNumber() + 1);
                    Map<String, Object> doc = documentSnapshot.getData();
                    Map<String, Object> info = (Map<String, Object>) doc.get("Info");
                    dataBaseHelper.insert_blank_rows(((Long) Objects.requireNonNull(Objects.requireNonNull(info).get("rows"))).intValue());
                    contentValues = new ContentValues();
                    ArrayList<Object> menu = (ArrayList<Object>) doc.get("Menu");
                    int id = 1;
                    for (Object items : Objects.requireNonNull(menu)) {
                        Map<String, String> item = (Map<String, String>) items;
                        contentValues.put("Type", item.get("Type"));
                        contentValues.put("Description", item.get("Description"));
                        for (String day : days) {
                            contentValues.put(day, item.get(day));
                        }
                        dataBaseHelper.addItems(getApplicationContext(), contentValues, String.valueOf(id));
                        id++;
                    }
                    ((DashboardFragment) fragment2).setmenuadapter();
                    dataBaseHelper.close();
                } else {
                    Log.d("firestore", "Current data: null");
                }
            }
        });

    }

    public String getmenu_items() {
        final DataBaseHelper dataBaseHelper = new DataBaseHelper(getApplicationContext());
        dataBaseHelper.onUpgrade(dataBaseHelper.getWritableDatabase(), DataBaseHelper.getVersionNumber(), DataBaseHelper.getVersionNumber() + 1);
        final String[] days = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
        menu_items.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    ContentValues contentValues;
                    DocumentSnapshot document = Objects.requireNonNull(task.getResult());
                    Map<String, Object> doc = document.getData();
                    Map<String, Object> info = (Map<String, Object>) doc.get("Info");
                    dataBaseHelper.insert_blank_rows(((Long) Objects.requireNonNull(Objects.requireNonNull(info).get("rows"))).intValue());
                        contentValues = new ContentValues();
                    ArrayList<Object> menu = (ArrayList<Object>) doc.get("Menu");
                    int id = 1;
                    for (Object items : Objects.requireNonNull(menu)) {
                        Map<String, String> item = (Map<String, String>) items;
                        contentValues.put("Type", item.get("Type"));
                        contentValues.put("Description", item.get("Description"));
                        for (String day : days) {
                            contentValues.put(day, item.get(day));
                        }
                        dataBaseHelper.addItems(getApplicationContext(), contentValues, String.valueOf(id));
                        id++;
                    }
                    dataBaseHelper.close();
                } else {
                    Log.d("firestore", "Unsucessfull");
                }
            }
        });
        return "Database Downloaded";
    }
    @Override
    public void onBackPressed() {
        Fragment f = fm.findFragmentById(R.id.frame_layout);
        if (f instanceof CardPageringFragment || f instanceof FeedbackFragment)
            super.onBackPressed();
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
        checkCurrentUser();
        getTokenId();
        getreal_time_menu_items();
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
