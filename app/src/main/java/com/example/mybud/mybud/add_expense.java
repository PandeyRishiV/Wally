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
import android.widget.Spinner;
import android.widget.Switch;
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

import static java.util.Calendar.DAY_OF_MONTH;
import static java.util.Calendar.MONTH;
import static java.util.Calendar.YEAR;
import static java.util.Calendar.getInstance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class add_expense extends AppCompatActivity {
    //Initialize Widgets;
    expense det;
    Float fl_amt;
    Calendar cal;
    Switch deduct;
    Boolean b=false;
    ImageButton edit;
    Spinner spin_type;
    Button save,update;
    FirebaseAuth mauth;
    Date date_curr,acdate;
    EditText amount, desc;
    DatabaseReference exp;
    ArrayAdapter<CharSequence> ad;
    TextView dateview,amounttxt,desctxt;
    String str_type, str_desc, child_date, curr,uid,key,am;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);

        WindowManager.LayoutParams lp=getWindow().getAttributes();
        lp.dimAmount=0.75f;
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
        getWindow().setAttributes(lp);

        //Database Reference
        exp = FirebaseDatabase.getInstance().getReference("expense");
        mauth=FirebaseAuth.getInstance();

        //Popup window
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * 0.9), (int) (height * 0.7));

        //Initialize widgets
        cal=getInstance();
        uid=mauth.getCurrentUser().getUid();
        save = (Button) findViewById(R.id.save);
        update=(Button)findViewById(R.id.update);
        edit=(ImageButton)findViewById(R.id.edit);
        desc = (EditText) findViewById(R.id.desc);
        deduct = (Switch) findViewById(R.id.deduct);
        spin_type = (Spinner) findViewById(R.id.type);
        amount = (EditText) findViewById(R.id.amount);
        dateview=(TextView)findViewById(R.id.dateview);
        desctxt=(TextView)findViewById(R.id.desctxt);
        amounttxt=(TextView)findViewById(R.id.amounttxt);
        ad = ArrayAdapter.createFromResource(this, R.array.type_spinner, androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);

        //Set Adapter
        spin_type.setAdapter(ad);

        b=getIntent().getExtras().getBoolean("b");
        if(b)
        {
            //Set Data from Expense in View Mode
            key=getIntent().getExtras().getString("key");
            curr=getIntent().getExtras().getString("date");
            dateview.setText(curr);
            int sp=ad.getPosition(getIntent().getExtras().getString("type"));
            spin_type.setSelection(sp);
            amounttxt.setText(getIntent().getExtras().getString("amount"));
            desctxt.setText(getIntent().getExtras().getString("desc"));
            deduct.setEnabled(getIntent().getExtras().getBoolean("deduct"));

            dateview.setClickable(false);
            spin_type.setEnabled(false);
            spin_type.setFocusable(false);
            amounttxt.setVisibility(View.VISIBLE);
            desctxt.setVisibility(View.VISIBLE);
            amount.setVisibility(View.INVISIBLE);
            desc.setVisibility(View.INVISIBLE);

            desctxt.setEllipsize(TextUtils.TruncateAt.MARQUEE);

            edit.setVisibility(View.VISIBLE);
            save.setVisibility(View.INVISIBLE);
        }
        else {
            curr = getIntent().getExtras().getString("curr_date");
            dateview.setText(curr);
        }

        //Calendar Method for dateview onclick listener
        final DatePickerDialog.OnDateSetListener adddate=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                cal.set(YEAR,year);
                cal.set(MONTH,month);
                cal.set(DAY_OF_MONTH,dayOfMonth);
                curr=new SimpleDateFormat("dd/MM/yyyy").format(cal.getTime());
                dateview.setText(curr);
            }
        };

        //DateView onclick Listener
        dateview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!b)
                {
                    new DatePickerDialog(add_expense.this,adddate,cal.get(YEAR),cal.get(MONTH),cal.get(DAY_OF_MONTH)).show();
                }

            }
        });

        //Save button onclickListener
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (empty() == true) {
                    //Get Text from widgets
                    str_type = spin_type.getSelectedItem().toString();
                    str_desc = desc.getText().toString().trim();
                    final String am = amount.getText().toString().trim();
                    fl_amt = Float.parseFloat(am);

                    //Date format Changed
                    try { date_curr = new SimpleDateFormat("dd/MM/yyyy").parse(curr); }
                        catch (ParseException e) { e.printStackTrace(); }
                    SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
                    child_date = form.format(date_curr);

                    String key=exp.push().getKey();
                    expense det = new expense(key,child_date, str_type, fl_amt, str_desc, deduct.isChecked());
                    exp.child("users").child(uid).child(child_date).child(key).setValue(det);

                    //Intent To Expense_page
                    Intent backpage = new Intent(add_expense.this, expense_page.class);
                    backpage.putExtra("curr", curr);
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
                dateview.setClickable(true);
                b=false;
                spin_type.setEnabled(true);
                spin_type.setFocusable(true);
                amounttxt.setVisibility(View.INVISIBLE);
                desctxt.setVisibility(View.INVISIBLE);
                amount.setVisibility(View.VISIBLE);
                desc.setVisibility(View.VISIBLE);
                edit.setVisibility(View.VISIBLE);
                update.setVisibility(View.VISIBLE);
                amount.setText(getIntent().getExtras().getString("amount"));
                desc.setText(getIntent().getExtras().getString("desc"));
            }
        });

        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(empty())
                {
                    //Get Text from widgets
                    str_type = spin_type.getSelectedItem().toString();
                    str_desc = desc.getText().toString().trim();
                    am = amount.getText().toString().trim();
                    fl_amt = Float.parseFloat(am);

                    //Date format Changed
                    try {
                        acdate=new SimpleDateFormat("dd/MM/yyyy").parse(getIntent().getExtras().getString("date"));
                        date_curr = new SimpleDateFormat("dd/MM/yyyy").parse(curr);
                    }
                    catch (ParseException e) { e.printStackTrace(); }
                    SimpleDateFormat form = new SimpleDateFormat("yyyy/MM/dd");
                    final String adate=form.format(acdate);
                    child_date = form.format(date_curr);
                    DatabaseReference e=FirebaseDatabase.getInstance().getReference("expense").child("users").child(uid).child(adate);
                    e.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            for(DataSnapshot ds: dataSnapshot.getChildren())
                            {
                                if(ds.getKey().equals(key))
                                {
                                    det = new expense(key,child_date, str_type, fl_amt, str_desc, deduct.isChecked());
                                    exp.child("users").child(uid).child(adate).child(key).setValue(null);
                                    exp.child("users").child(uid).child(child_date).child(key).setValue(det);
                                    Toast.makeText(add_expense.this, "Updated", Toast.LENGTH_SHORT).show();

                                    //Intent To Expense_page
                                    Intent backpage = new Intent(add_expense.this, expense_page.class);
                                    backpage.putExtra("curr", curr);
                                    backpage.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    startActivity(backpage);
                                    finish();
                                }

                            }
                        }
                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(add_expense.this, databaseError.getMessage()+"", Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

    }

    private boolean empty() {
        if (spin_type.getSelectedItem().toString().trim().length() <= 0) {
            Toast.makeText(this, "Enter expense type", Toast.LENGTH_SHORT).show();
            return false;
        } else if (amount.getText().toString().length() <= 0) {
            amount.setError("Enter Type Of Expense");
            return false;
        }
        if (desc.getText().toString().length() <= 0) {
            str_desc = "";
        }
        return true;
    }


}