package com.example.wally;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

public class login extends AppCompatActivity {

    Button login;
    ImageButton eye;
    DisplayMetrics dm;
    FirebaseAuth mauth;
    ProgressBar progress;
    EditText email,password;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Blur Background
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int length=dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(length*0.5));

        mauth=FirebaseAuth.getInstance();
        login=(Button)findViewById(R.id.login);
        eye=(ImageButton)findViewById(R.id.eye);
        email=(EditText)findViewById(R.id.email);
        progress=(ProgressBar)findViewById(R.id.progress);
        password=(EditText)findViewById(R.id.password);

        eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    password.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    return true;
                }
                else if(event.getAction()==MotionEvent.ACTION_UP)
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    return false;
                }
                return false;
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(empty())
                {
                    progress.setVisibility(View.VISIBLE);
                    mauth.signInWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(login.this, "WELCOME", Toast.LENGTH_SHORT).show();
                                        Intent main_intent=new Intent(login.this, mainpage.class);
                                        main_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(main_intent);
                                        finishAffinity();
                                    }
                                    else
                                    {
                                        progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(login.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                }
            }
        });
    }
    public boolean empty()
    {
        if(email.getText().toString().trim().length()<=0)
        {
            email.setError("Enter Email ID");
            email.requestFocus();
            return false;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString().trim()).matches())
        {
            email.setError("Enter Valid Email");
            email.requestFocus();
        }
        else if(password.getText().toString().trim().length()<=0)
        {
            password.requestFocus();
            password.setError("Enter Password");
            return false;
        }
        return true;
    }
}
