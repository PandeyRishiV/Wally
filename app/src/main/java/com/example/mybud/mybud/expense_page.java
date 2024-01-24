package com.example.mybud.mybud;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybud.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class expense_page extends AppCompatActivity {
    //Intialize Widgets
    int i;
    Calendar cal;
    float total,ap;
    ImageView empty;
    Toolbar toolbar;
    TextView nothing;
    FirebaseAuth mauth;
    ProgressBar progress;
    ListView expense_logs;
    DatabaseReference getexp;
    Button add;
    ImageButton previous,next;
    TextView selected_date,blur;
    String curr_date,s,uid,dbdate;
    ArrayAdapter<expenselistview> logs;
    ArrayList<expense> intentlist=new ArrayList<expense>();
    ArrayList<expenselistview> array_logs=new ArrayList<expenselistview>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_page);

        //Create Calendar Instance
        cal = getInstance();

        //Define Widgets
        mauth=FirebaseAuth.getInstance();
        blur=(TextView)findViewById(R.id.blur);
        empty=(ImageView)findViewById(R.id.empty);
        next=(ImageButton)findViewById(R.id.next);
        toolbar=(Toolbar)findViewById(R.id.toolkit);
        nothing=(TextView)findViewById(R.id.nothing);
        progress=(ProgressBar)findViewById(R.id.progress);
        previous=(ImageButton)findViewById(R.id.previous);
        add=(Button) findViewById(R.id.fab_add);
        expense_logs=(ListView)findViewById(R.id.expense_logs);
        selected_date=(TextView)findViewById(R.id.selected_date);

        //Set toolbar as actionbar
        setSupportActionBar(toolbar);

        //Array Adapter For Listview
        logs=new custom(this,array_logs);

        //Set adapter for listview
        expense_logs.setAdapter(logs);

        //Set date from add_expense page
        try{
            curr_date=getIntent().getExtras().getString("curr"); }
        catch (Exception e){
            curr_date= new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        }

        setdata(curr_date);

        //Date Picker
        final DatePickerDialog.OnDateSetListener d=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(YEAR,year);
                cal.set(MONTH,month);
                cal.set(DATE,dayOfMonth);
                curr_date=new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                setdata(curr_date);

            }
        };

        //FAB click listener
        add.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                //Intent for add_expense
                blur.setVisibility(View.VISIBLE);
                Intent add_expense=new Intent(expense_page.this, add_expense.class);
                add_expense.putExtra("curr_date",curr_date.toString());
                startActivity(add_expense);
            }
        });

        //Go to previous Date
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
                try
                {
                    cal.setTime(format.parse(curr_date));
                    cal.add(DAY_OF_YEAR,-1);
                    curr_date=format.format(cal.getTime());
                    setdata(curr_date);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Go TO Next Date
        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v)
            {
                SimpleDateFormat format=new SimpleDateFormat("dd/MM/yyyy");
                try
                {
                    cal.setTime(format.parse(curr_date));
                    cal.add(DATE,+1);
                    curr_date=format.format(cal.getTime());
                    setdata(curr_date);
                }
                catch (ParseException e)
                {
                    e.printStackTrace();
                }
            }
        });

        //Select Date From a Calendar Instance
        selected_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(expense_page.this,d,cal.get(YEAR),cal.get(MONTH),cal.get(DAY_OF_MONTH)).show();
            }
        });

        expense_logs.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position!=array_logs.size()-1)
                {
                Intent addview=new Intent(expense_page.this,add_expense.class);
                addview.putExtra("key",intentlist.get(position).key);
                addview.putExtra("date",curr_date);
                addview.putExtra("type",intentlist.get(position).type);
                addview.putExtra("amount",intentlist.get(position).amount.toString());
                addview.putExtra("desc",intentlist.get(position).description);
                addview.putExtra("deduct",intentlist.get(position).deduct);
                Boolean b=true;
                addview.putExtra("b",b);
                startActivity(addview);
                }
            }
        });
    }

    //Method for setting date and logs of that date
    public void setdata(String database_date)
    {
        //Set Date on Label
        selected_date.setText(database_date);

        //Change date format for database
        try
        {
            Date getdata=new SimpleDateFormat("dd/MM/yyyy").parse(database_date);
            database_date=new SimpleDateFormat("yyyy/MM/dd").format(getdata);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        //Database Reciever SnapShot and Setting data
        uid=mauth.getCurrentUser().getUid();
        try {
            getexp = FirebaseDatabase.getInstance().getReference("expense").child("users").child(uid).child(database_date);
            logs.clear();
            final String finalDatabase_date = database_date;
            getexp.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    array_logs.clear();
                    for (DataSnapshot exp : dataSnapshot.getChildren()) {
                        expense ep = new expense();
                        ep.setKey(exp.getValue(expense.class).getKey());
                        ep.setDate(exp.getValue(expense.class).getDate());
                        ep.setType(exp.getValue(expense.class).getType());
                        ep.setAmount(exp.getValue(expense.class).getAmount());
                        ep.setDescription(exp.getValue(expense.class).getDescription());
                        ep.setDeduct(exp.getValue(expense.class).getDeduct());
                        expense i = new expense(ep.getKey(),ep.getDate(), ep.getType(), ep.getAmount(), ep.getDescription(), ep.getDeduct());
                        intentlist.add(i);
                        expenselistview er = new expenselistview(ep.getType(), ep.getAmount().toString());
                        logs.add(er);
                    }
                    gettotal(finalDatabase_date);
                    if(array_logs.size()<=0)
                    {
                        empty.setVisibility(View.VISIBLE);
                        nothing.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        empty.setVisibility(View.INVISIBLE);
                        nothing.setVisibility(View.INVISIBLE);
                    }
                    progress.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(expense_page.this,  "Connection Error", Toast.LENGTH_SHORT).show();
                }
            });
        }
        catch (Exception e)
        {
            Toast.makeText(this, "DATABASE ERROR", Toast.LENGTH_SHORT).show();
        }
    }
    public void gettotal(final String totaldate)
    {
        total=0.0f;
            DatabaseReference expen = FirebaseDatabase.getInstance().getReference("expense").child("users").child(uid).child(totaldate);
            expen.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for (DataSnapshot ds : dataSnapshot.getChildren()) {
                        expense ep = new expense();
                        ep.setAmount(ds.getValue(expense.class).getAmount());
                        ap=ep.getAmount();
                        total=Float.valueOf(new DecimalFormat("0.00").format(total+ap));
                    }
                    expenselistview lv=new expenselistview("Total ",String.valueOf(total));
                    if(total>0)
                    {
                        logs.add(lv);
                    }
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }
    public class custom extends ArrayAdapter<expenselistview> {
        public custom(Context context, ArrayList<expenselistview>expenselistviews) {
            super(context, 0,expenselistviews);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            expenselistview expenselistview=getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.income_list, parent, false);
            }
            TextView txt1 = (TextView) convertView.findViewById(R.id.txt1);
            TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);

            txt1.setText(expenselistview.type);
            txt2.setText(expenselistview.amount);
            return convertView;
        }
    }
}
