package com.example.mybud.mybud;

import android.content.Intent;
import android.graphics.Paint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybud.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static android.provider.Settings.*;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    //Initialize widgets
    TextView bg,summ;
    String device_id;
    FirebaseAuth mauth;
    RelativeLayout mybud;
    DatabaseReference id;
    Button register,login;
    ImageView animationImage;
    AnimationDrawable loadingscreen;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialize Database and enable loading screen animation
        mauth=FirebaseAuth.getInstance();
        bg=(TextView)findViewById(R.id.bg);
        login=(Button)findViewById(R.id.login);
        summ=(TextView)findViewById(R.id.summ);
        register=(Button)findViewById(R.id.register);
        animationImage=(ImageView)findViewById(R.id.animationImage);
        loadingscreen=(AnimationDrawable)animationImage.getDrawable();
        device_id= Secure.getString(MainActivity.this.getContentResolver(), Secure.ANDROID_ID);
        id=FirebaseDatabase.getInstance().getReference().child("id");

        loadingscreen.start();

        //Underline label
        register.setPaintFlags(register.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                new Handler().postDelayed(new Runnable() {
            @Override
            public void run()
            {
                FirebaseUser user=mauth.getCurrentUser();
                try
                {
                user.reload().addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        loadingscreen.stop();
                        Toast.makeText(MainActivity.this, "WELCOME", Toast.LENGTH_SHORT).show();
                        Intent mainpage=new Intent(MainActivity.this,mainpage.class);
                        startActivity(mainpage);
                        finish();
                    }
                });
                }
                catch (Exception e)
                {
                    runner();
                }
            }
        },2000);


        //Button Click Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent register
                Intent register_intent=new Intent(MainActivity.this,register.class);
                register_intent.putExtra("deviceid",device_id);
                startActivity(register_intent);
            }
        });
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent Login
                Intent login_intent=new Intent(MainActivity.this,login.class);
                startActivity(login_intent);
            }
        });

    }

    @Override
    public void onBackPressed()
    {
        finishAffinity();
        super.onBackPressed();
    }

    public void runner()
    {
        loadingscreen.stop();
        animationImage.setVisibility(View.INVISIBLE);
        register.setVisibility(View.VISIBLE);
        login.setVisibility(View.VISIBLE);
    }

}