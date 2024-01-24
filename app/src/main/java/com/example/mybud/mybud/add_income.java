package com.example.mybud.mybud;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mybud.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class add_income extends AppCompatActivity {

    int sp;
    Float fl_am;
    Calendar cal;
    Spinner source;
    Boolean b=false;
    ImageButton edit;
    DisplayMetrics dm;
    FirebaseAuth mauth;
    Button save,update;
    Date date_cur,acdate;
    EditText amount,desc;
    DatabaseReference inc;
    RelativeLayout addinc;
    TextView datev,atxt,dtxt;
    String cur,str_source,str_desc,child_date,key,uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_income);

        //Blur background
        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        //Defining Widgets
        cal=getInstance();
        mauth=FirebaseAuth.getInstance();
        uid=mauth.getCurrentUser().getUid();
        save=(Button)findViewById(R.id.save);
        desc=(EditText)findViewById(R.id.desc);
        update=(Button)findViewById(R.id.update);
        dtxt=(TextView)findViewById(R.id.destxt);
        datev=(TextView)findViewById(R.id.datev);
        edit=(ImageButton)findViewById(R.id.edit);
        source=(Spinner)findViewById(R.id.source);
        atxt=(TextView)findViewById(R.id.amountxt);
        amount=(EditText)findViewById(R.id.amount);
        inc= FirebaseDatabase.getInstance().getReference("income");
        ArrayAdapter<CharSequence> ad=ArrayAdapter.createFromResource(this,R.array.type_spin, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        source.setAdapter(ad);

        //Pop window
        dm=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width=dm.widthPixels;
        int length=dm.heightPixels;
        getWindow().setLayout((int)(width*0.9),(int)(length*0.7));
        addinc=(RelativeLayout)findViewById(R.id.addinc);
        addinc.setClipToOutline(true);

        b=getIntent().getExtras().getBoolean("b");
        if(b)
        {
            key=getIntent().getExtras().getString("key");
            cur=getIntent().getExtras().getString("date");
            datev.setText(cur);
            try {sp=ad.getPosition(getIntent().getExtras().getString("source"));}
            catch (NullPointerException e){sp=5;}
            source.setSelection(sp);
            atxt.setText(getIntent().getExtras().getString("amount"));
            dtxt.setText(getIntent().getExtras().getString("desc"));

            atxt.setVisibility(View.VISIBLE);
            dtxt.setVisibility(View.VISIBLE);
            amount.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);
            datev.setClickable(false);
            datev.setFocusable(false);

            dtxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);
            source.setFocusable(false);
            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);
        }
        else
        {
            cur=getIntent().getExtras().getString("cur_date");
            datev.setText(cur);
        }

        final DatePickerDialog.OnDateSetListener dp=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(Calendar.YEAR,year);
                cal.set(Calendar.MONTH,month);
                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                cur=new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                datev.setText(cur);
            }
        };

        datev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!b) {
                    new DatePickerDialog(add_income.this, dp, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty() == true) {
                    //Get Text from widgets
                    str_source=source.getSelectedItem().toString();
                    str_desc = desc.getText().toString().trim();
                    final String am = amount.getText().toString().trim();
                    fl_am = Float.parseFloat(am);

                    //Date format Changed
                    try {
                        date_cur = new SimpleDateFormat("dd/MM/yyyy").parse(cur);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
                    child_date = form.format(date_cur);


                    key=inc.push().getKey();
                    income det = new income(key,child_date, str_source, fl_am, str_desc);
                    inc.child("users").child(uid).child(child_date).child(key).setValue(det);


                    //Intent To Expense_page
                    Intent backpage = new Intent(add_income.this, income_page.class);
                    backpage.putExtra("cur", cur);
                    backpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(backpage);
                    finish();

                }
            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edit.setVisibility(View.INVISIBLE);
                datev.setClickable(true);
                atxt.setVisibility(View.INVISIBLE);
                dtxt.setVisibility(View.INVISIBLE);
                amount.setText(getIntent().getExtras().getString("amount"));
                desc.setText(getIntent().getExtras().getString("desc"));

                b=false;
                source.setVisibility(View.VISIBLE);
                amount.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty() == true) {
                    //Get Text from widgets
                    str_source = source.getSelectedItem().toString();
                    str_desc = desc.getText().toString().trim();
                    final String am = amount.getText().toString().trim();
                    fl_am = Float.parseFloat(am);

                    //Date format Changed
                    try {
                        acdate=new SimpleDateFormat("dd/MM/yyyy").parse(getIntent().getExtras().getString("date"));
                        date_cur = new SimpleDateFormat("dd/MM/yyyy").parse(cur);
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
                    final String adate=form.format(acdate);
                    child_date = form.format(date_cur);
                    DatabaseReference e=FirebaseDatabase.getInstance().getReference("income").child("users").child(uid).child(adate);
                    e.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                if(ds.getKey().equals(key))
                                {
                                    income det = new income(key,child_date, str_source, fl_am, str_desc);
                                    inc.child("users").child(uid).child(adate).child(key).setValue(null);
                                    inc.child("users").child(uid).child(child_date).child(key).setValue(det);
                                    Toast.makeText(add_income.this, "Updated", Toast.LENGTH_SHORT).show();

                                    //Intent To Expense_page
                                    Intent backpage = new Intent(add_income.this, income_page.class);
                                    backpage.putExtra("cur", cur);
                                    backpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(backpage);
                                    finish();
                                }
                                Toast.makeText(add_income.this, "ok", Toast.LENGTH_SHORT).show();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(add_income.this, databaseError.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
    }

    private boolean empty() {
        if(source.getSelectedItem().toString().length() <=0 )
        {
            Toast.makeText(this, "Enter Income Source", Toast.LENGTH_SHORT).show();
            return false;
        }
        else if (amount.getText().toString().length() <= 0)
        {
            amount.setError("Enter Type Of Expense");
            return false;
        }
        if (desc.getText().toString().length() <= 0) {
            str_desc = "";
        }
        return true;
    }
}
