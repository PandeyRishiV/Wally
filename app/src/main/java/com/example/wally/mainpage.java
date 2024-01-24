package com.example.wally;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.wally.R;
import com.google.firebase.auth.FirebaseAuth;

public class mainpage extends AppCompatActivity {
    Toolbar toolbar;
    FirebaseAuth mauth;
    TextView selected_date;
    ImageButton income,expense;
    ImageButton next,previous;
    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.mainmenu,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {
        int id=menuItem.getItemId();
        if(id==R.id.logout)
        {
            mauth.signOut();
            Toast.makeText(this, "Logged Out", Toast.LENGTH_SHORT).show();
            Intent main=new Intent(mainpage.this,MainActivity.class);
            startActivity(main);
            finish();
        }
        else if(id==R.id.report)
        {
            Intent totalpage=new Intent(mainpage.this, total.class);
            startActivity(totalpage);
        }
        return true;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainpage);
        income=(ImageButton)findViewById(R.id.income);
        expense=(ImageButton)findViewById(R.id.expense);
        toolbar=(Toolbar)findViewById(R.id.toolkit);
        next=(ImageButton)findViewById(R.id.next);
        mauth=FirebaseAuth.getInstance();
        previous=(ImageButton)findViewById(R.id.previous);
        selected_date=(TextView)findViewById(R.id.selected_date);

        next.setVisibility(View.INVISIBLE);
        previous.setVisibility(View.INVISIBLE);
        selected_date.setText(R.string.app_name);

        //Set Toolbar as actionbar
        setSupportActionBar(toolbar);

        //OnClick listeners
        income.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Income Intent
                Intent income_intent=new Intent(mainpage.this,income_page.class);
                startActivity(income_intent);
            }
        });

        expense.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //Expense Intent
                Intent expense_intent=new Intent(mainpage.this,expense_page.class);
                startActivity(expense_intent);
            }
        });


    }
    public  void onBackPressed()
    {
        finishAffinity();
        super.onBackPressed();
    }

}
