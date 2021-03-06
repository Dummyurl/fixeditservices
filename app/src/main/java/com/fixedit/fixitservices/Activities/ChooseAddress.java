package com.fixedit.fixitservices.Activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fixedit.fixitservices.Models.AdminModel;
import com.fixedit.fixitservices.Models.OrderModel;
import com.fixedit.fixitservices.Models.ServiceCountModel;
import com.fixedit.fixitservices.Models.User;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.Services.ChooseServiceOptions;
import com.fixedit.fixitservices.Services.ListOfSubServices;
import com.fixedit.fixitservices.UserManagement.Register;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.fixedit.fixitservices.Utils.Constants;
import com.fixedit.fixitservices.Utils.NotificationAsync;
import com.fixedit.fixitservices.Utils.NotificationObserver;
import com.fixedit.fixitservices.Utils.SharedPrefs;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

public class ChooseAddress extends AppCompatActivity implements NotificationObserver {
    CheckBox googleCheckBox, addressCheckBox;
    TextView googleAddress, address;
    Button placeOrder;
    DatabaseReference mDatabase;
    ImageView back;
    RelativeLayout wholeLayout;
    long orderId = 000;
    private long finalTotalTime = 0;
    private long finalTotalCost = 0;
    private String orderDate;

    int addressOption = 0;
    String adminFcmKey;
    RelativeLayout gogleAd;
    private String number;

    @Override
    protected void onResume() {
        super.onResume();
        googleAddress.setText(SharedPrefs.getUser().getGoogleAddress());

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_address);

        googleCheckBox = findViewById(R.id.googleCheckBox);
        addressCheckBox = findViewById(R.id.addressCheckBox);
        googleAddress = findViewById(R.id.googleAddress);
        address = findViewById(R.id.address);
        placeOrder = findViewById(R.id.placeOrder);
        back = findViewById(R.id.back);
        wholeLayout = findViewById(R.id.wholeLayout);
        gogleAd = findViewById(R.id.gogleAd);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        orderDate = ChooseServiceOptions.daySelected;
        orderDate = orderDate.replace("\n", "");

        mDatabase = FirebaseDatabase.getInstance().getReference();
        getOrderCountFromDB();
        address.setText(SharedPrefs.getUser().getAddress());
//        googleAddress.setText(SharedPrefs.getUser().getGoogleAddress());


        gogleAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ChooseAddress.this, MapsActivity.class);
                startActivityForResult(i, 1);
            }
        });


        addressCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        addressOption = 1;
                        googleCheckBox.setChecked(false);

                    } else {
                        addressOption = 2;
                        googleCheckBox.setChecked(true);
                    }
                }
            }
        });
        googleCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.isPressed()) {
                    if (isChecked) {
                        addressOption = 2;
                        addressCheckBox.setChecked(false);
                    } else {
                        addressOption = 1;
                        addressCheckBox.setChecked(true);
                    }
                }
            }
        });
        calculateTotal();
        getAdminFCMkey();

        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User us = SharedPrefs.getUser();
                us.setFcmKey(FirebaseInstanceId.getInstance().getToken());
                SharedPrefs.setUser(us);
                if (addressOption == 0) {
                    CommonUtils.showToast("Please choose one address");
                } else {
                    wholeLayout.setVisibility(View.VISIBLE);
                    final OrderModel model = new OrderModel(
                            orderId,
                            System.currentTimeMillis(),
                            SharedPrefs.getUser(),
                            ListOfSubServices.orderList,
                            finalTotalCost,
                            finalTotalTime,
                            "",
                            orderDate,
                            ChooseServiceOptions.timeSelected,
                            "Pending",
                            addressOption == 1 ? SharedPrefs.getUser().getAddress() : SharedPrefs.getUser().getGoogleAddress(),
                            SharedPrefs.getUser().getLat(),
                            SharedPrefs.getUser().getLon(),
                            ChooseServiceOptions.buildingType,
                            ListOfSubServices.parentService

                    );


                    final String finalOrderDate = orderDate;
                    mDatabase.child("Orders").child("" + orderId).setValue(model).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            CommonUtils.sendMessage(number, "FIXEDIT \nNew " + model.getServiceName() + " order \nOrder Id: " + orderId
                                    + "\n\nClick to view: \n" + Constants.FIXEDIT_URL + "admin/" + orderId);
                            ;
                            CommonUtils.sendMessage(SharedPrefs.getUser().getMobile(), "FIXEDIT\n\nOrder was successfully placed\n" +
                                    "Order Id: " + model.getOrderId() + "\n\nYou will receive a call shortly for order confirmation");
                            mDatabase.child("TimeSlots")
                                    .child(CommonUtils.getYear(System.currentTimeMillis()))
                                    .child(finalOrderDate)
                                    .child(ChooseServiceOptions.timeSelected).setValue(ChooseServiceOptions.timeSelected);

                            mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Orders").child("" + orderId).setValue(orderId);
                            mDatabase.child("Users").child(SharedPrefs.getUser().getUsername()).child("Cart").removeValue()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            wholeLayout.setVisibility(View.GONE);
                                            NotificationAsync notificationAsync = new NotificationAsync(ChooseAddress.this);
                                            String notification_title = "New " + ListOfSubServices.parentService + " order from " + SharedPrefs.getUser().getFullName();
                                            String notification_message = "Click to view";
                                            notificationAsync.execute("ali", adminFcmKey, notification_title, notification_message, "Order", "" + orderId);
                                            Intent i = new Intent(ChooseAddress.this, OrderPlaced.class);
                                            i.putExtra("orderId", orderId);
                                            i.putExtra("estimatedCost", finalTotalCost);
                                            i.putExtra("estimatedTime", finalTotalTime);
                                            startActivity(i);
                                            finish();
                                        }
                                    });

                        }
                    });
                }
            }
        });

    }

    private void getAdminFCMkey() {
        mDatabase.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    AdminModel model = dataSnapshot.getValue(AdminModel.class);
                    if (model != null) {
                        adminFcmKey = model.getFcmKey();
                        number = model.getAdminNumber();
                        SharedPrefs.setAdminFcmKey(adminFcmKey);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private long calculateTotal() {
        float totalMinutes = 0;
        float hours = 0;
        for (ServiceCountModel model : ListOfSubServices.orderList) {
            totalMinutes = totalMinutes + (model.getQuantity() * (model.getService().getTimeMin() + model.getService().getTimeHour()));
        }

        hours = (totalMinutes / 60);
        int h = (int) (totalMinutes / 60);

        float dif = hours - h;

        if (dif > 0.17) {
            finalTotalTime = h + 1;
        } else {
            finalTotalTime = h;
        }

        finalTotalCost = finalTotalTime * ListOfSubServices.parentServiceModel.getServiceBasePrice();
        return finalTotalCost;
    }

    private void getOrderCountFromDB() {
        mDatabase.child("Orders").limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        String key = snapshot.getKey();
                        if (key.contains(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()))) {
                            orderId = Long.parseLong(key) + 1;
                        } else {
                            orderId = Long.parseLong(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()) + String.format("%03d", 1));
                        }

                    }
                } else {
                    orderId = Long.parseLong(CommonUtils.getFormattedDateOnly(System.currentTimeMillis()) + String.format("%03d", 1));

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onSuccess(String chatId) {

    }

    @Override
    public void onFailure() {

    }
}
