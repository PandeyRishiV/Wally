package com.example.wally;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.wally.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class total extends AppCompatActivity {
    Calendar cal,cal2,cal3;
    Button generate;
    FirebaseAuth mauth;
    String uid,instring,tdate,fdate;
    float total=0,ap,newtotal=0.0f;
    TextView todate,fromdate,totalset;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_total);

        cal=Calendar.getInstance();
        cal2=Calendar.getInstance();
        cal3=Calendar.getInstance();
        mauth=FirebaseAuth.getInstance();
        uid=mauth.getCurrentUser().getUid();
        todate=(TextView)findViewById(R.id.todate);
        fromdate=(TextView)findViewById(R.id.fromdate);
        totalset=(TextView)findViewById(R.id.totalset);
        generate=(Button)findViewById(R.id.generate);

        fdate=new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
        tdate=new SimpleDateFormat("dd/MM/yyyy").format(cal2.getTime());
        fromdate.setText(fdate);
        todate.setText(tdate);
        totalset.setText("0.0");

        final DatePickerDialog.OnDateSetListener d1=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                fdate=new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                fromdate.setText(fdate);
            }
        };

        final DatePickerDialog.OnDateSetListener d2=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal2.set(Calendar.YEAR,year);
                cal2.set(Calendar.MONTH,month);
                cal2.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                tdate=new SimpleDateFormat("dd/MM/yyyy").format(cal2.getTime());
                todate.setText(tdate);
            }
        };

        fromdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(total.this,d1,cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        todate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(total.this, d2, cal2.get(Calendar.YEAR), cal2.get(Calendar.MONTH), cal2.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
        generate.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                try
                {
                    Date df=new SimpleDateFormat("dd/MM/yyyy").parse(fdate);
                    Date dt=new SimpleDateFormat("dd/MM/yyyy").parse(tdate);
                    String ad=new SimpleDateFormat("yyyy/MM").format(df);
                    String at=new SimpleDateFormat("yyyy/MM").format(dt);

                    final DatabaseReference db=FirebaseDatabase.getInstance().getReference("users").child("expense").child(ad);
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                        {
                            for(DataSnapshot dsa:dataSnapshot.getChildren())
                            {
                                Toast.makeText(total.this, dsa.getChildrenCount()+"", Toast.LENGTH_SHORT).show();
                                for(DataSnapshot ds:dsa.getChildren())
                                {
                                    expense e = new expense();
                                    e.setAmount(ds.getValue(expense.class).getAmount());
                                    total += e.getAmount();
                                }
                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });
                }
                catch(Exception e)
                {
                    Toast.makeText(total.this, e.getMessage()+"", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}