package com.fixedit.fixitservices.Services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.fixedit.fixitservices.AddToCartCallbacks;
import com.fixedit.fixitservices.Models.ServiceCountModel;
import com.fixedit.fixitservices.R;
import com.fixedit.fixitservices.UserManagement.Login;
import com.fixedit.fixitservices.UserManagement.LoginMenu;
import com.fixedit.fixitservices.Utils.CommonUtils;
import com.fixedit.fixitservices.Utils.SharedPrefs;

import java.util.ArrayList;

public class SubServiceListAdapter extends RecyclerView.Adapter<SubServiceListAdapter.ViewHolder> {
    Context context;
    ArrayList<SubServiceModel> itemlist;

    AddToCartCallbacks addToCartInterface;
    ArrayList<ServiceCountModel> userCartServiceList;
    UpdateActivityUI updateActivityUI;

    public SubServiceListAdapter(Context context, ArrayList<SubServiceModel> itemlist,
                                 ArrayList<ServiceCountModel> userCartServiceList,
                                 AddToCartCallbacks addToCartInterface) {
        this.context = context;
        this.itemlist = itemlist;
        this.addToCartInterface = addToCartInterface;
        this.userCartServiceList = userCartServiceList;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.sub_service_item_layout, viewGroup, false);
        SubServiceListAdapter.ViewHolder viewHolder = new SubServiceListAdapter.ViewHolder(view);

        return viewHolder;
    }

    public void setUpdateActivityUI(UpdateActivityUI updateActivityUI) {
        this.updateActivityUI = updateActivityUI;
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final SubServiceModel model = itemlist.get(position);
        holder.title.setText(model.getName());
        final int[] count = {0};
        ServiceCountModel serviceCountModel = null;
        boolean flag = false;


        if (userCartServiceList.size() > 0) {
            for (int i = 0; i < userCartServiceList.size(); i++) {
                if (model.getName() != null && model.getName().equals(userCartServiceList.get(i).getService().getName())) {
                    flag = true;
                    serviceCountModel = userCartServiceList.get(i);
                    ListOfSubServices.orderList.add(userCartServiceList.get(i));
                }

            }
        } else {
            updateActivityUI.updateNow(new ArrayList<ServiceCountModel>());

        }
        updateActivityUI.updateNow(ListOfSubServices.orderList);
        if (flag) {
//            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
            holder.count.setTextColor(context.getResources().getColor(R.color.colorBlack));

            count[0] = serviceCountModel.getQuantity();
            holder.count.setText("" + count[0]);
            holder.increase.setVisibility(View.VISIBLE);

            if (count[0] > 1) {
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                holder.decrease.setVisibility(View.VISIBLE);
            } else {
                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                holder.decrease.setVisibility(View.VISIBLE);
            }
        } else

        {

//            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
            holder.count.setTextColor(context.getResources().getColor(R.color.colorBlack));
            holder.count.setText("0");
            holder.increase.setVisibility(View.VISIBLE);
            holder.decrease.setVisibility(View.VISIBLE);

        }

        flag = false;


        holder.increase.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getUser() != null) {
                    if (count[0] <= model.getMax()) {
                        if (count[0] >= 1) {
                            count[0] += 1;
                            holder.count.setText("" + count[0]);
                            holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                            addToCartInterface.quantityUpdate(model, count[0]);
                        } else {
//                            holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            holder.count.setTextColor(context.getResources().getColor(R.color.colorBlack));
                            count[0] = 1;
                            holder.count.setText("" + count[0]);
                            holder.increase.setVisibility(View.VISIBLE);
                            holder.decrease.setVisibility(View.VISIBLE);

                            addToCartInterface.addedToCart(model, count[0]);
                        }
                    } else {
                        CommonUtils.showToast("Max limit reached");
                    }

                } else {
                    CommonUtils.showToast("Please login first");
                    context.startActivity(new Intent(context,LoginMenu.class));

                }

            }
        });
        holder.decrease.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View view) {
                if (SharedPrefs.getUser() != null) {
                    if (CommonUtils.isNetworkConnected()) {
                        if (count[0] > 2) {
                            count[0] -= 1;
                            holder.count.setText("" + count[0]);
                            addToCartInterface.quantityUpdate(model, count[0]);


                        } else if (count[0] > 1) {
                            {
                                count[0] -= 1;
                                holder.count.setText("" + count[0]);
                                holder.decrease.setImageResource(R.drawable.ic_decrease_btn);
                                addToCartInterface.quantityUpdate(model, count[0]);


                            }
                        } else if (count[0] == 1) {
//                        holder.relativeLayout.setBackgroundResource(R.drawable.add_to_cart_bg_transparent);
                            holder.count.setTextColor(context.getResources().getColor(R.color.colorBlack));

                            holder.count.setText("0");
                            holder.increase.setVisibility(View.VISIBLE);
                            holder.decrease.setVisibility(View.VISIBLE);
                            addToCartInterface.deletedFromCart(model);

                        }
                    } else {
                        CommonUtils.showToast("Please connect to internet");
                    }
                }else {
                    CommonUtils.showToast("Please login first");
                    context.startActivity(new Intent(context,LoginMenu.class));

                }
            }
        });

//        holder.itemView.setOnClickListener(new View.OnClickListener()
//
//        {
//            @Override
//            public void onClick(View view) {
//                if (CommonUtils.isNetworkConnected()) {
//                    Intent i = new Intent(context, ViewProduct.class);
//                    i.putExtra("productId", model.getCategory() + "/" + model.getId());
//                    context.startActivity(i);
//                } else {
//                    CommonUtils.showToast("Please connect to internet");
//                }
//
//            }
//        });


        holder.itemView.setOnClickListener(new View.OnClickListener()

        {
            @Override
            public void onClick(View v) {

            }
        });


    }

    @Override
    public int getItemCount() {
        return itemlist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, description, price, count;
        RelativeLayout relativeLayout;
        ImageView increase, decrease;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            price = itemView.findViewById(R.id.price);
            increase = itemView.findViewById(R.id.increase);
            decrease = itemView.findViewById(R.id.decrease);
            count = itemView.findViewById(R.id.count);
            relativeLayout = itemView.findViewById(R.id.relativeLayout);


        }
    }

    public interface UpdateActivityUI {
        public void updateNow(ArrayList<ServiceCountModel> count);
    }


}
