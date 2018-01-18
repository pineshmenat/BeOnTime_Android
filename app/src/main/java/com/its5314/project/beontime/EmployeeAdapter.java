package com.its5314.project.beontime;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static com.its5314.project.beontime.ZF_ActivityEmployeeStartWork.reqCode;

/**
 * Created by imsil on 4/1/18.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.VH> {
    private EmployeeEngine employeeEngine;
    Context context;

    public EmployeeAdapter(Context context,EmployeeEngine employeeEngine) {
        this.employeeEngine = employeeEngine;
        this.context = context;
    }

    @Override
    public EmployeeAdapter.VH onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.dsp_cell_view_employee_list, parent,
                false);
        VH vh = new VH(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(EmployeeAdapter.VH holder, int position) {
       final  Employee employee = employeeEngine.getEmployee(position);
        try{
            holder.empIdTV.setText(employee.getId());
            holder.empNameTV.setText(employee.getName());
            holder.empEmailTV.setText(employee.getEmail());
            holder.empAddressTV.setText(employee.getAddress());
            holder.v.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View view) {
                    //Toast.makeText(context, "Position = ", Toast.LENGTH_SHORT).show();
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setMessage(employee.getName()+"'s address"+'\n'+employee.getAddress());
                    dialog.show();
                }
            });
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return employeeEngine.size();
    }

    public class VH extends RecyclerView.ViewHolder {
        TextView empIdTV;
        TextView empNameTV;
        TextView empEmailTV;
        TextView empAddressTV;
        View v;

        public VH(View itemView) {
            super(itemView);
            empIdTV = itemView.findViewById(R.id.empIdTV);
            empNameTV = itemView.findViewById(R.id.empNameTV);
            empEmailTV = itemView.findViewById(R.id.empEmailTV);
            empAddressTV = itemView.findViewById(R.id.empAddressTV);
            v = itemView;
        }
    }

}
