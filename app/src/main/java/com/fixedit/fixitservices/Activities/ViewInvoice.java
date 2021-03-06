package com.fixedit.fixitservices.Activities;

import android.os.Bundle;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.Adapters.InvoiceListAdapter;
import com.fixedit.fixitservices.Models.InvoiceModel;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ViewInvoice extends AppCompatActivity {
    TextView billNumber, orderNumber, date, dayChosen, timeChosen,
            customerName, mobileNumber, address, comments, totalTime, total;
    RecyclerView recycler;
    DatabaseReference mDatabase;
    String invoiceId;
    InvoiceModel model;

    InvoiceListAdapter adapter;
    ImageView back;
    RelativeLayout wholeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_invoice);

        mDatabase = FirebaseDatabase.getInstance().getReference();
        invoiceId = getIntent().getStringExtra("invoiceId");
        this.setTitle("Bill Number: " + invoiceId);

        back = findViewById(R.id.back);
        billNumber = findViewById(R.id.billNumber);
        orderNumber = findViewById(R.id.orderNumber);
        date = findViewById(R.id.date);
        dayChosen = findViewById(R.id.dayChosen);
        timeChosen = findViewById(R.id.timeChosen);
        customerName = findViewById(R.id.customerName);
        mobileNumber = findViewById(R.id.mobileNumber);
        address = findViewById(R.id.address);
        comments = findViewById(R.id.comments);
        totalTime = findViewById(R.id.totalTime);
        total = findViewById(R.id.total);
        wholeLayout = findViewById(R.id.wholeLayout);
        recycler = findViewById(R.id.recycler);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getDataFromServer();
    }

    private void getDataFromServer() {
        mDatabase.child("Invoices").child(invoiceId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    model = dataSnapshot.getValue(InvoiceModel.class);
                    if (model != null) {
//                        billNumber.setText("Bill Number: " + model.getInvoiceId());
                        orderNumber.setText("Order Number: " + model.getOrder().getOrderId());
                        date.setText("Date: " + CommonUtils.getFormattedDate(model.getOrder().getTime()));
                        dayChosen.setText("Day: " + model.getOrder().getDate());
                        timeChosen.setText("Time: " + model.getOrder().getChosenTime());
                        customerName.setText("Customer Name: " + model.getOrder().getUser().getFullName());
                        mobileNumber.setText("Cell Number: " + model.getOrder().getUser().getMobile());
                        address.setText("Customer Address: " + model.getOrder().getUser().getAddress());
                        comments.setText("Comments: " + model.getOrder().getInstructions());
                        total.setText("Total Bill: Rs " + model.getOrder().getTotalPrice());
                        totalTime.setText("Total Time: " + model.getOrder().getTotalHours()+" hours");
                        recycler.setLayoutManager(new LinearLayoutManager(ViewInvoice.this, LinearLayoutManager.VERTICAL, false));
                        adapter = new InvoiceListAdapter(ViewInvoice.this, model.getOrder().getCountModelArrayList());
                        recycler.setAdapter(adapter);
                        adapter.notifyDataSetChanged();
                        wholeLayout.setVisibility(View.GONE);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
