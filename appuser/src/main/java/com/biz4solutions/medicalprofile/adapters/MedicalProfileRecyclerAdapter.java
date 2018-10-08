package com.biz4solutions.medicalprofile.adapters;

import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.ItemMedicalProfileBinding;
import com.biz4solutions.interfaces.CustomOnItemClickListener;
import com.biz4solutions.models.MedicalDisease;

import java.util.ArrayList;

public class MedicalProfileRecyclerAdapter extends RecyclerView.Adapter<MedicalProfileRecyclerAdapter.MedicalProfileRecyclerViewHolder> {

    private ArrayList<MedicalDisease> medicalList;
    private CustomOnItemClickListener onItemClickListener;
    private boolean isInViewMode;

    public MedicalProfileRecyclerAdapter(ArrayList<MedicalDisease> medicalList, CustomOnItemClickListener onItemClickListener, boolean isInViewMode) {
        this.medicalList = medicalList;
        this.onItemClickListener = onItemClickListener;
        this.isInViewMode = isInViewMode;
    }

    public ArrayList<MedicalDisease> getMedicalList() {
        return medicalList;
    }

    @NonNull
    @Override
    public MedicalProfileRecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_medical_profile, null, false);
        return new MedicalProfileRecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicalProfileRecyclerViewHolder holder, int position) {
        holder.getBinding().setData(medicalList.get(position));
    }

    public void addItems(MedicalDisease disease) {
        boolean isPresent = false;
        for (MedicalDisease medicalDisease : medicalList) {
            if (disease.getId().equals(medicalDisease.getId())) {
                isPresent = true;
            }
        }
        if (!isPresent) {
            medicalList.add(disease);
            notifyDataSetChanged();
        }
    }

    public void setNewMedicalData(ArrayList<MedicalDisease> arrayList) {
        if (arrayList != null) {
            medicalList.clear();
            medicalList = new ArrayList<>();
            medicalList.addAll(arrayList);
            notifyDataSetChanged();
        }
    }


    @Override
    public int getItemCount() {
        if (medicalList == null || medicalList.size() == 0) {
            return 0;
        }
        return medicalList.size();
    }

    class MedicalProfileRecyclerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemMedicalProfileBinding binding;

        private MedicalProfileRecyclerViewHolder(View itemView) {
            super(itemView);
            initBinding(itemView);
        }

        private void initBinding(View itemView) {
            binding = DataBindingUtil.bind(itemView);
            if (isInViewMode) {
                binding.ivItemMedicalProfileCancel.setVisibility(View.GONE);
            } else {
                binding.ivItemMedicalProfileCancel.setVisibility(View.VISIBLE);
            }
            if (binding != null) {
                binding.ivItemMedicalProfileCancel.setOnClickListener(this);
            }

        }

        public ItemMedicalProfileBinding getBinding() {
            return binding;
        }

        @Override
        public void onClick(View v) {
            if (v.getId() == binding.ivItemMedicalProfileCancel.getId()) {
                medicalList.remove(getAdapterPosition());
                notifyDataSetChanged();
                if (onItemClickListener != null)
                    onItemClickListener.onCustomItemClick(getAdapterPosition(), null);
            }
        }
    }
}
