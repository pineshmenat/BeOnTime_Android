package com.its5314.project.beontime;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by imsil on 4/1/18.
 */

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.VH> {
    private EmployeeEngine employeeEngine;

    public EmployeeAdapter(EmployeeEngine employeeEngine) {
        this.employeeEngine = employeeEngine;
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
        Employee employee = employeeEngine.getEmployee(position);
        try{
            holder.empIdTV.setText(employee.getId());
            holder.empNameTV.setText(employee.getName());
            holder.empEmailTV.setText(employee.getEmail());
            holder.empAddressTV.setText(employee.getAddress());
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

        public VH(View itemView) {
            super(itemView);
            empIdTV = itemView.findViewById(R.id.empIdTV);
            empNameTV = itemView.findViewById(R.id.empNameTV);
            empEmailTV = itemView.findViewById(R.id.empEmailTV);
            empAddressTV = itemView.findViewById(R.id.empAddressTV);
        }
    }

}
