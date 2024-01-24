package com.example.wally;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wally.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class register extends AppCompatActivity {
    //Initialize Widgets and database
    Spinner spinner;
    Button register;
    ImageButton eye;
    String deviceid;
    RelativeLayout reg;
    FirebaseAuth mauth;
    ProgressBar progress;
    DatabaseReference id;
    AutoCompleteTextView type;
    EditText name,age,email,password;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Blur background
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        //Pop up window
        DisplayMetrics dm= new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int length=dm.heightPixels;
        getWindow().setLayout((int)(width*0.8),(int)(length*0.7));

        //Widgets link , spinner adapter and database
        mauth=FirebaseAuth.getInstance();
        age=(EditText)findViewById(R.id.age);
        name=(EditText)findViewById(R.id.name);
        eye=(ImageButton)findViewById(R.id.eye);
        email=(EditText)findViewById(R.id.email);
        reg=(RelativeLayout)findViewById(R.id.reg);
        spinner=(Spinner)findViewById(R.id.spinner);
        register=(Button)findViewById(R.id.register);
        password=(EditText)findViewById(R.id.password);
        progress=(ProgressBar)findViewById(R.id.progress);
        id= FirebaseDatabase.getInstance().getReference("id");
        ArrayAdapter<CharSequence> ad=ArrayAdapter.createFromResource(this, R.array.itemlist, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        //Set Spinner Adapter
        spinner.setAdapter(ad);

        deviceid= Objects.requireNonNull(getIntent().getExtras()).getString("deviceid");
        //PEEK PASSWORD
        eye.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if(event.getAction()==MotionEvent.ACTION_DOWN)
                {
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
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

        //Onclick Listener
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (emptyfields()){
                    progress.setVisibility(View.VISIBLE);
                    mauth.createUserWithEmailAndPassword(email.getText().toString().trim(),password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful())
                                    {
                                        //progress.setVisibility(View.INVISIBLE);
                                        Toast.makeText(register.this, "Registered to database", Toast.LENGTH_SHORT).show();
                                        String rno=id.push().getKey();
                                        id details=new id(email.getText().toString(),password.getText().toString(),deviceid);
                                        assert rno != null;
                                        id.child(rno).setValue(details);
                                        Intent login_intent = new Intent(register.this, login.class);
                                        startActivity(login_intent);
                                        finish();
                                    }
                                    else
                                    {
                                        if(task.getException() instanceof FirebaseAuthUserCollisionException)
                                        {
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(register.this, "Email Already Registered", Toast.LENGTH_SHORT).show();
                                        }
                                        else
                                        {
                                            progress.setVisibility(View.INVISIBLE);
                                            Toast.makeText(register.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            });

                }
            }
        });

    }

    //Check Empty fields
    public final Boolean emptyfields()
    {
        if (name.getText().toString().trim().length() == 0) {
            name.setFocusable(true);
            name.setError("Enter First Name");
            return false;
        }
        else if(password.getText().toString().trim().length() == 0)
        {
            password.setFocusable(true);
            password.setError("Enter Password");
            return false;
        } else if (email.getText().toString().trim().length() == 0) {
            email.setFocusable(true);
            email.setError("Enter Email Id");
            return false;
        } else if (age.getText().toString().trim().length() == 0) {
            age.setFocusable(true);
            age.setError("Enter Age");
            return false;
        }
        return true;
    }
}
