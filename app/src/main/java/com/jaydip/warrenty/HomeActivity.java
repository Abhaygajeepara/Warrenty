package com.jaydip.warrenty;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.jaydip.warrenty.Addapters.CategoryAddapter;
import com.jaydip.warrenty.Broadcast.NotificationRecieverer;
import com.jaydip.warrenty.Models.CategoryModel;
import com.jaydip.warrenty.ViewModels.CategoryViewModel;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity {
    CategoryAddapter Caddapter;
    List<CategoryModel> list;
    RecyclerView CategoryView;
    CategoryViewModel categoryViewModel;
    FloatingActionButton fab,fab1,fab2;
    Animation fab_rotate,fab_back,fab_open,fab_close;
    TextView fab_text2,fab_text3;
    boolean isopen;
    List<String> categories;
    ImageView background,setting;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setContentView(R.layout.activity_home);

        ///////////////////////////////////////////////////////////
        Caddapter = new CategoryAddapter(this);
        list = new ArrayList<>();
        CategoryView = findViewById(R.id.category);
        CategoryView.setLayoutManager(new GridLayoutManager(this,2));
        categoryViewModel = new CategoryViewModel(getApplication());
        CategoryView.setAdapter(Caddapter);
        fab = findViewById(R.id.fab1);
        fab1 = findViewById(R.id.fab2);
        fab_rotate = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_on);
        fab_back = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_clos);
        fab2 = findViewById(R.id.fab3);
        fab_text2 = findViewById(R.id.fab_text2);
        fab_text3 = findViewById(R.id.fab_text3);
        isopen = false;
        background = findViewById(R.id.background);
        setting = findViewById(R.id.setting);


        //////////////////////////////////////////////////////////
        categoryViewModel.getListLiveData().observe(this, new Observer<List<CategoryModel>>() {
            @Override
            public void onChanged(List<CategoryModel> categoryModels) {
                Caddapter.setList(categoryModels);
            }
        });
        categoryViewModel.getAllcategory().observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> strings) {
                categories = strings;
            }
        });

        background.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideFab();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isopen){

                        showFab();

                }
                else {

                    hideFab();
                }

            }
        });
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),AddItem.class);
                startActivity(intent);
                hideFab();
            }
        });
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCategory();
            }
        });
        Toast.makeText(this,"Long press to delete Category",Toast.LENGTH_LONG).show();


        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),SettingsActivity.class);
                startActivity(i);
            }
        });

        /////////////////////////testing

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean val = preferences.getBoolean("notification_switch",false);
        Log.e("prefrence",val+"");


        FloatingActionButton floatingActionButton = findViewById(R.id.sendNotification);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("jaydip","clicked");
                ComponentName name = new ComponentName(getApplicationContext(), NotificationRecieverer.class);

                PackageManager manager = getPackageManager();
                manager.setComponentEnabledSetting(name,PackageManager.COMPONENT_ENABLED_STATE_ENABLED,PackageManager.DONT_KILL_APP);
                Intent intent = new Intent(getApplicationContext(), NotificationRecieverer.class);
                getApplicationContext().sendBroadcast(intent);
            }
        });
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(App.NOTIFICATI_ID,"Warrenty", NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("for Warrenty");
            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);
            Log.e("jaydip","channel created");
        }
    }



    ///// ad new category
    void addCategory(){
        EditText newCategory;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View v  = inflater.inflate(R.layout.add_category,null,false);
        newCategory = v.findViewById(R.id.newCategory);
        builder.setView(v)
                .setCancelable(false)
                .setTitle("Add category")
                .setNegativeButton("cancel",null)
                .setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String s = newCategory.getText().toString();
                       if(categories.contains(s) && s.length() > 0){
                           Toast.makeText(getApplicationContext(),"Category exist",Toast.LENGTH_SHORT).show();
                       }
                       else if(s.length() > 0){
                           CategoryModel model = new CategoryModel();
                           model.setTotalItem(0);
                           model.setCategoryName(s);
                           model.setIconName("default_catrgoty");
                           categoryViewModel.addCategory(model);
                           Toast.makeText(getApplicationContext(),"Category Added",Toast.LENGTH_SHORT).show();
                           hideFab();
                       }
                    }
                });
        builder.show();
    }

    /// showing fab buttons
    void showFab()  {

        background.setVisibility(View.VISIBLE);
        fab.startAnimation(fab_rotate);
        fab1.startAnimation(fab_open);
        fab1.setVisibility(View.VISIBLE);
        fab2.startAnimation(fab_open);
        fab2.setVisibility(View.VISIBLE);

        fab_text2.setVisibility(View.VISIBLE);
        fab_text3.setVisibility(View.VISIBLE);
        fab_text2.startAnimation(fab_open);
        fab_text3.startAnimation(fab_open);
        isopen = !isopen;

    }

    //hidding fab button
    void hideFab(){
        background.setVisibility(View.GONE);
        fab.startAnimation(fab_back);
        fab1.startAnimation(fab_close);
        fab1.setVisibility(View.GONE);
        fab2.startAnimation(fab_close);
        fab2.setVisibility(View.GONE);

        fab_text2.setVisibility(View.INVISIBLE);
        fab_text3.setVisibility(View.INVISIBLE);
        fab_text2.startAnimation(fab_close);
        fab_text3.startAnimation(fab_close);
        isopen = !isopen;

    }

    @Override
    public void onBackPressed() {
        if(isopen){
            hideFab();
        }
        else {
            Log.e("jaydip","backprssed");
            super.onBackPressed();
        }

    }
}