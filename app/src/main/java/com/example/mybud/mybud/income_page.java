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

import static java.util.Calendar.DATE;
import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.DAY_OF_YEAR;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class income_page extends AppCompatActivity {

    Calendar cal;
    float total,ap;
    Toolbar toolbar;
    TextView nothing;
    ImageView empty;
    FirebaseAuth mauth;
    ListView incomelist;
    String cur_date,uid;
    ProgressBar progress;
    TextView select_date;
    DatabaseReference incom;
    customad income_list_adapter;
    Button addincome;
    ImageButton next_date,previous_date;
    ArrayList<income> intentlist=new ArrayList<income>();
    ArrayList<incomelistview> arraylist_array=new ArrayList<incomelistview>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_page);

        //Defining Widgets
        cal = getInstance();
        mauth=FirebaseAuth.getInstance();
        uid=mauth.getCurrentUser().getUid();
        empty=(ImageView)findViewById(R.id.empty);
        nothing=(TextView)findViewById(R.id.nothing);
        toolbar = (Toolbar) findViewById(R.id.toolkit);
        next_date = (ImageButton) findViewById(R.id.next);
        progress=(ProgressBar)findViewById(R.id.progress);
        incomelist = (ListView) findViewById(R.id.incomelist);
        previous_date = (ImageButton) findViewById(R.id.previous);
        select_date = (TextView) findViewById(R.id.selected_date);
        addincome = (Button) findViewById(R.id.add_income_1);
        incom = FirebaseDatabase.getInstance().getReference("income");
        income_list_adapter = new customad(this,arraylist_array);



        //Set ListView Adapter
        incomelist.setAdapter(income_list_adapter);

        //Set Current Date
        try
        {
            cur_date=getIntent().getExtras().getString("cur");
            setdata(cur_date);
        }
        catch(Exception e)
        {
            cur_date = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
            setdata(cur_date);
        }

        //Set Toolbar as Actionbar
        setSupportActionBar(toolbar);

        //
        final DatePickerDialog.OnDateSetListener datepick = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR, year);
                cal.set(Calendar.MONTH, month);
                cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                cur_date = new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                setdata(cur_date);
            }
        };

        //Go to previous Date
        previous_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date day = format.parse(cur_date);
                    cal.setTime(day);
                    cal.add(DAY_OF_YEAR, -1);
                    cur_date = format.format(cal.getTime());
                    select_date.setText(cur_date);
                    setdata(cur_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //Go TO Next Date
        next_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                try {
                    Date day = format.parse(cur_date);
                    cal.setTime(day);
                    cal.add(DATE, +1);
                    cur_date = format.format(cal.getTime());
                    select_date.setText(cur_date);
                    setdata(cur_date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        //Select Date From a Calendar Instance
        select_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(income_page.this, datepick, cal.get(YEAR), cal.get(MONTH), cal.get(DAY_OF_MONTH)).show();
            }
        });

        //FloatingActionButton Onclick Listener
        addincome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent add_ic=new Intent(income_page.this,add_income.class);
                add_ic.putExtra("cur_date",cur_date);
                startActivity(add_ic);
            }
        });

        incomelist.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String a=intentlist.get(position).date;
                Intent addview=new Intent(income_page.this,add_income.class);
                addview.putExtra("key",intentlist.get(position).key);
                addview.putExtra("date",cur_date);
                addview.putExtra("source",intentlist.get(position).source);
                addview.putExtra("amount",intentlist.get(position).amount.toString());
                addview.putExtra("desc",intentlist.get(position).desc);
                boolean b=true;
                addview.putExtra("b",b);
                startActivity(addview);
            }
        });

    }
    public void setdata(final String setdate)
    {
        select_date.setText(setdate);
        try
        {
            Date rev=new SimpleDateFormat("dd/MM/yyyy").parse(setdate);
            final String reverse=new SimpleDateFormat("yyyy/MM/dd").format(rev);
            DatabaseReference i=FirebaseDatabase.getInstance().getReference("income").child("users").child(uid).child(reverse);
            income_list_adapter.clear();
            i.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot d: dataSnapshot.getChildren())
                    {
                        income ic=new income();
                        ic.setKey(d.getValue(income.class).getKey());
                        ic.setSource(d.getValue(income.class).getSource());
                        ic.setAmount(d.getValue(income.class).getAmount());
                        ic.setDate(d.getValue(income.class).getDate());
                        ic.setDesc(d.getValue(income.class).getDesc());
                        income e=new income(ic.getKey(),ic.getDate(),ic.getSource(),ic.getAmount(),ic.getDesc());
                        intentlist.add(e);
                        incomelistview inco=new incomelistview(ic.getSource(),ic.getAmount().toString());
                        income_list_adapter.add(inco);
                    }
                    gettotal(reverse);
                    if(arraylist_array.size()<=0)
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
                }
            });
        }
        catch(Exception e)
        {

        }
    }
    public void gettotal(final String totaldate)
    {
        total=0.0f;
        DatabaseReference expen = FirebaseDatabase.getInstance().getReference("income").child("users").child(uid).child(totaldate);
        expen.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    income ep = new income();
                    ep.setAmount(ds.getValue(income.class).getAmount());
                    ap=ep.getAmount();
                    total=Float.valueOf(new DecimalFormat("0.00").format(total+ap));
                }
                incomelistview lv=new incomelistview("Total ",String.valueOf(total));
                if(total>0)
                {
                    income_list_adapter.add(lv);
                }

            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public class customad extends ArrayAdapter<incomelistview> {
        public customad(Context context, ArrayList<incomelistview> incomelistviews) {
            super(context, 0, incomelistviews);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            incomelistview incomelistview = getItem(position);
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.income_list, parent, false);
            }
            TextView txt1 = (TextView) convertView.findViewById(R.id.txt1);
            TextView txt2 = (TextView) convertView.findViewById(R.id.txt2);

            txt1.setText(incomelistview.category);
            txt2.setText(incomelistview.amount);
            return convertView;
        }
    }
}
