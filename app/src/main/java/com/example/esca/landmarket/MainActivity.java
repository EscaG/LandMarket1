package com.example.esca.landmarket;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.esca.landmarket.adapters.MyRecyclerAdapter;
import com.example.esca.landmarket.fragments.FragmentFirstMap;
import com.example.esca.landmarket.fragments.FragmentLandForBuyer;
import com.example.esca.landmarket.fragments.FragmentLogin;
import com.example.esca.landmarket.fragments.FragmentMap;
import com.example.esca.landmarket.fragments.FragmentProfile;
import com.example.esca.landmarket.fragments.FragmentProfileEdit;
import com.example.esca.landmarket.fragments.FragmentRegistration;
import com.example.esca.landmarket.fragments.FragmentSectionEdit;
import com.example.esca.landmarket.fragments.FragmentSections;
import com.example.esca.landmarket.models.LandInfo;
import com.example.esca.landmarket.models.Seller;
import com.example.esca.landmarket.provider.SellerInfo;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static android.R.attr.bitmap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, FragmentLogin.FragmentListenerLogin, FragmentRegistration.FragmentListenerRegistration, FragmentSections.NextFromFragmentSections, View.OnClickListener, FragmentProfile.FragmentListenerProfile, FragmentProfileEdit.FragmentListenerFromProfileEdit, SellerInfo.DownloadClient, FragmentFirstMap.ShowLoadLand, FragmentLandForBuyer.FragmentListenerLandForBuyer {
    private final int SELECT_PHOTO = 1;
    private FragmentManager manager;
    private FragmentTransaction transaction;
    private FrameLayout containerFragment;
    private LinearLayout mapContainer;
    private boolean goBack = false;
    private FloatingActionButton fab;
    private Toolbar toolbar;
    private TextView name, email;
    private Handler handler;
    private Seller seller;
    private ImageView imageview;
    private Button btnSelectImage;
    private Bitmap bitmap;
    private File destination = null;
    private InputStream inputStreamImg;
    private String imgPath = null;
    private final int PICK_IMAGE_CAMERA = 1, PICK_IMAGE_GALLERY = 2;
    private String statusFragment = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        manager = getSupportFragmentManager();
        //////////////////////////////////////////
//        FragmentManager manager = getSupportFragmentManager();
//        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        FragmentFirstMap fragment = new FragmentFirstMap();
        fragment.setSelectedMasterListene(this);
        transaction = manager.beginTransaction();
        transaction.replace(R.id.map_container, fragment, "FIRST_MAP");
        transaction.commit();
        ////////////////////////////////////
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        fab.setVisibility(View.INVISIBLE);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        containerFragment = (FrameLayout) findViewById(R.id.frag_container);
        mapContainer = (LinearLayout) findViewById(R.id.map_container);

        handler = new Handler();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        new ClientInfo(this, this, handler).execute();
        new SellerInfo(this, this, handler).execute();
        LinearLayout before = (LinearLayout) findViewById(R.id.nav_header_container_before_login);
        LinearLayout after = (LinearLayout) findViewById(R.id.nav_header_container_after_login);
        Button signIn = (Button) findViewById(R.id.nav_header_btn_signIn);
        Button signUp = (Button) findViewById(R.id.nav_header_btn_signUp);
        name = (TextView) findViewById(R.id.nav_header_view_name);
        email = (TextView) findViewById(R.id.nav_header_view_email);
        ImageView logo = (ImageView) findViewById(R.id.nav_header_input_image);
        signUp.setOnClickListener(this);
        signIn.setOnClickListener(this);
        SharedPreferences sharedPreferences = getSharedPreferences("ACTION", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean result = sharedPreferences.getBoolean("ACTION", false);
        if (!result) {
            after.setVisibility(View.GONE);
            before.setVisibility(View.VISIBLE);
            logo.setImageResource(R.mipmap.ic_launcher);
        } else {
            before.setVisibility(View.GONE);
            after.setVisibility(View.VISIBLE);
            logo.setImageResource(R.mipmap.cat);

        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (goBack) {
            super.onBackPressed();
            goBack = false;
        } else if (statusFragment.equals("DontShowLand")) {
            toolbar.setTitle("Sections");
            Log.d("TAG", "onBackPressed: " + statusFragment);
//            mapContainer.setVisibility(View.INVISIBLE);
            statusFragment = "";
            super.onBackPressed();
        } else {
//            fab.setVisibility(View.VISIBLE);
            mapContainer.setVisibility(View.VISIBLE);
            toolbar.setTitle("Land Market");
            super.onBackPressed();
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings) {
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        transaction = manager.beginTransaction();
        SharedPreferences sharedPreferences = getSharedPreferences("ACTION", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean result = sharedPreferences.getBoolean("ACTION", false);
        Intent intent = null;
        int id = item.getItemId();

        switch (id) {
            case R.id.nav_menu_active_sections:
//                clearBackStack();
//                if (!result) {
//                    startLoginFragment();
//                } else {
//
//                }
                break;
            case R.id.nav_menu_my_sections:
                clearBackStack();
                if (!result) {
                    startLoginFragment();
                } else {
                    toolbar.setTitle("Sections");
                    FragmentSections fragmentSections = new FragmentSections();
                    fragmentSections.setFragmentListener(this);
                    transaction.replace(R.id.frag_container, fragmentSections, "SECTIONS");
                    transaction.addToBackStack("SECTIONS");
                }
                break;
            case R.id.nav_menu_my_profile:
                clearBackStack();
                if (!result) {
                    startLoginFragment();
                } else {
                    toolbar.setTitle("Profile");
                    FragmentProfile fragmentProfile = new FragmentProfile();
                    fragmentProfile.setFragmentListener(this);
                    transaction.replace(R.id.frag_container, fragmentProfile, "PROFILE");
                    transaction.addToBackStack("PROFILE");
                }
                break;
            case R.id.nav_menu_login:
                clearBackStack();
                toolbar.setTitle("Login");
                FragmentLogin fragmentLogin = new FragmentLogin();
                fragmentLogin.setFragmentListener(this);
                transaction.replace(R.id.frag_container, fragmentLogin, "LOGIN");
                transaction.addToBackStack("LOGIN");

                break;
            case R.id.nav_menu_log_out:
                editor.putBoolean("ACTION", false);
                intent = new Intent(MainActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mapContainer.setVisibility(View.VISIBLE);
                startActivity(intent);
                break;
        }
        editor.commit();
        transaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void clearBackStack() {
        goBack = false;
        mapContainer.setVisibility(View.INVISIBLE);
        fab.setVisibility(View.INVISIBLE);
        FragmentManager manager = getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();
        if (count > 0) {
            for (int i = 0; i < count; ++i) {
                manager.popBackStackImmediate();
            }
        }
    }

    private void startLoginFragment() {
        toolbar.setTitle("Login");
        FragmentManager manager = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = manager.beginTransaction();
        FragmentLogin fragmentLogin = new FragmentLogin();
        fragmentLogin.setFragmentListener(this);
        transaction.replace(R.id.frag_container, fragmentLogin, "FRAG_LOGIN");
        transaction.addToBackStack("FRAG_LOGIN");
        transaction.commit();
    }

    public void startRegistrationFragment() {
        toolbar.setTitle("Registration");
        SharedPreferences sharedPreferences = getSharedPreferences("ACTION", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("REGISTR_ACT", true).commit();
        editor.commit();
        transaction = manager.beginTransaction();
        FragmentRegistration fragment = new FragmentRegistration();
        fragment.setFragmentListener(this);
        transaction.replace(R.id.frag_container, fragment, "CREATE_ACC");
        transaction.addToBackStack("CREATE_ACC");
        transaction.commit();
    }

    @Override
    public void onClickNextFromLogin(boolean bool) {
        goBack = true;
        if (bool) {
            SharedPreferences sharedPreferences = getSharedPreferences("ACTION", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("ACTION", true).commit();
            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (!bool) {
            FragmentRegistration fragmentRegistration = new FragmentRegistration();
            fragmentRegistration.setFragmentListener(this);
            transaction = manager.beginTransaction();
            transaction.replace(R.id.frag_container, fragmentRegistration, "CREATE_ACC");
            transaction.addToBackStack("CREATE_ACC");
            transaction.commit();
            toolbar.setTitle("Registration");
        }
    }

    @Override
    public void onClickNextFromRegistration(boolean bool) {
        SharedPreferences sharedPreferences = getSharedPreferences("ACTION", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean registrAct = sharedPreferences.getBoolean("REGISTR_ACT", false);
        goBack = true;
        if (bool) {
            editor.putBoolean("ACTION", true).commit();
//            editor.commit();
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        } else if (!bool) {
            if (registrAct) {
                clearBackStack();
                mapContainer.setVisibility(View.VISIBLE);
                toolbar.setTitle("Land Market");
                editor.putBoolean("REGISTR_ACT", false).commit();
                editor.commit();
            } else {
                toolbar.setTitle("Login");
                goBack = false;
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    @Override
    public void onClickNextFromSections(MyRecyclerAdapter adapter, int position, LandInfo landInfo, boolean bool) {
        if (bool && landInfo == null) {
            Intent intent = new Intent(MainActivity.this, ActivityAddSection.class);
            startActivity(intent);
        } else if (!bool) {
            statusFragment = "DontShowLand";
            toolbar.setTitle("Show land");

            FragmentMap fragment = new FragmentMap();
            fragment.setAddress(landInfo.getAddress());
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.frag_container, fragment, "SHOW_ON_THE_MAP")
                    .addToBackStack("SHOW_ON_THE_MAP")
                    .commit();
        } else if (bool && landInfo != null) {
            goBack = true;
            FragmentSectionEdit fragmentSectionEdit = new FragmentSectionEdit();
            fragmentSectionEdit.setAdapterPosition(adapter, position, landInfo);

            transaction = manager.beginTransaction();
            transaction.replace(R.id.frag_container, fragmentSectionEdit, "SECTION_EDIT");
            transaction.addToBackStack("SECTION_EDIT");
            transaction.commit();
        }
    }

    @Override
    public void onClickNextFromMyProfile(boolean bool, Seller seller) {
        this.seller = seller;
        goBack = true;
        if (bool && seller != null) {
            FragmentProfileEdit fragmentProfileEdit = new FragmentProfileEdit();
            fragmentProfileEdit.setFragmentListener(this);
            fragmentProfileEdit.setClientInfo(seller);
            transaction = manager.beginTransaction();
            transaction.replace(R.id.frag_container, fragmentProfileEdit, "PROFILE_EDIT");
            transaction.addToBackStack("PROFILE_EDIT");
            transaction.commit();
        } else if (!bool) {
            mapContainer.setVisibility(View.VISIBLE);
//            fab.setVisibility(View.VISIBLE);
            getSupportFragmentManager().popBackStack();
            toolbar.setTitle("Land Market");
        }
    }

    @Override
    public void onClickNextFromProfileEdit(String back, String save, String logo, Seller seller) {
        if (back.equals("back")) {
            getSupportFragmentManager().popBackStack();
        } else if (save.equals("save")) {
//            new ClientInfo(this,this,handler);
            if (seller != null) {
                name.setText(seller.getPassport());
                email.setText(seller.getEmail());
            }
            getSupportFragmentManager().popBackStack();
        } else if (logo.equals("logo")) {
            selectImage();
//            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
//            photoPickerIntent.setType("image/*");
//            startActivityForResult(photoPickerIntent, SELECT_PHOTO);
        }
    }


    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.nav_header_btn_signIn) {
            clearBackStack();
            startLoginFragment();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        } else if (id == R.id.nav_header_btn_signUp) {
            goBack = true;
            clearBackStack();
            startRegistrationFragment();
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            }
        }
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
//        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);
//        if (requestCode == SELECT_PHOTO && resultCode == RESULT_OK) {
//            try {
//                Uri imageUri = imageReturnedIntent.getData();
//                InputStream imageStream = getContentResolver().openInputStream(imageUri);
//                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
//                CompleteManager.callComplete(selectedImage);
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private LandInfo landInfo;

    @Override
    public void showLand(LandInfo landInfo) {
        this.landInfo = landInfo;
        transaction = manager.beginTransaction();
        FragmentLandForBuyer fragmentLandForBuyer = new FragmentLandForBuyer();
        fragmentLandForBuyer.setFragmentListener(this);
        transaction.replace(R.id.map_container, fragmentLandForBuyer, "FRAG_SELLER");
        fragmentLandForBuyer.setLand(this.landInfo);
        transaction.addToBackStack("FRAG_SELLER");
        transaction.commit();
    }

    @Override
    public void onClickNextFromLandForBuyer(boolean bool) {

    }

    private void selectImage() {
        try {
            PackageManager pm = getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.CAMERA, getPackageName());
            if (hasPerm == PackageManager.PERMISSION_GRANTED) {
                final CharSequence[] options = {"Take Photo", "Choose From Gallery", "Cancel"};
                android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(this);
                builder.setTitle("Select Option");
//                builder.setView(R.layout.custom_dialog);
//                builder.setIcon(R.mipmap.ic_launcher);
                builder.setItems(options, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (options[item].equals("Take Photo")) {
                            dialog.dismiss();
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            startActivityForResult(intent, PICK_IMAGE_CAMERA);
                        } else if (options[item].equals("Choose From Gallery")) {
                            dialog.dismiss();
                            Intent pickPhoto = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickPhoto, PICK_IMAGE_GALLERY);
                        } else if (options[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();
            } else
                Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Toast.makeText(this, "Camera Permission error", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_CAMERA) {
            try {
                Uri selectedImage = data.getData();
                bitmap = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);

                Log.e("Activity", "Pick from Camera::>>> ");

                String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
                destination = new File(Environment.getExternalStorageDirectory() + "/" +
                        getString(R.string.app_name), "IMG_" + timeStamp + ".jpg");
                FileOutputStream fo;
                try {
                    destination.createNewFile();
                    fo = new FileOutputStream(destination);
                    fo.write(bytes.toByteArray());
                    fo.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                imgPath = destination.getAbsolutePath();
                CompleteManager.callComplete(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == PICK_IMAGE_GALLERY) {
            try {
                Uri selectedImage = data.getData();
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                Log.e("Activity", "Pick from Gallery::>>> ");

                imgPath = getRealPathFromURI(selectedImage);
                destination = new File(imgPath.toString());
                CompleteManager.callComplete(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    @Override
    public void onLoadClient(Seller seller) {
        this.seller = seller;
        name.setText(seller.getConfirm());
        email.setText(seller.getEmail());
    }


    public interface IImageCompleteListener {
        void onComplete(Bitmap bitmap);
    }

    public static class CompleteManager {
        public static IImageCompleteListener listener;

        public static void callComplete(Bitmap bitmap) {
            if (listener != null) {
                listener.onComplete(bitmap);
            }
        }
    }
}
