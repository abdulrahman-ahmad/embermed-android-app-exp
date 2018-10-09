package com.biz4solutions.subscription.adapters;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.biz4solutions.R;
import com.biz4solutions.databinding.ItemSubscriptionOffersBinding;
import com.biz4solutions.interfaces.CustomOnItemClickListener;
import com.biz4solutions.models.SubscriptionCardDetails;

import java.util.ArrayList;

public class SubscriptionOffersRecyclerAdapter extends RecyclerView.Adapter<SubscriptionOffersRecyclerAdapter.SubscriptionOffersRecyclerHolder> {

    private ArrayList<SubscriptionCardDetails> offersList;
    private CustomOnItemClickListener onItemClickListener;

    public SubscriptionOffersRecyclerAdapter(ArrayList<SubscriptionCardDetails> offersList, CustomOnItemClickListener onItemClickListener) {
        this.offersList = offersList;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
    @Override
    public SubscriptionOffersRecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_subscription_offers, parent, false);
        return new SubscriptionOffersRecyclerHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SubscriptionOffersRecyclerHolder holder, int position) {
        holder.getBinding().setData(offersList.get(position));
    }

    @Override
    public int getItemCount() {
        if (offersList == null || offersList.size() == 0) {
            return 0;
        }
        return offersList.size();
    }

    class SubscriptionOffersRecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ItemSubscriptionOffersBinding binding;

        private SubscriptionOffersRecyclerHolder(View itemView) {
            super(itemView);
            initBinding(itemView);
        }

        private void initBinding(View itemView) {
            binding = DataBindingUtil.bind(itemView);
            if (binding != null)
                binding.btnSubscription.setOnClickListener(this);
        }

        public ItemSubscriptionOffersBinding getBinding() {
            return binding;
        }

        @SuppressWarnings("unchecked")
        @Override
        public void onClick(View v) {
            if (v.getId() == binding.btnSubscription.getId()) {
                if (onItemClickListener != null)
                    onItemClickListener.onCustomItemClick(getAdapterPosition(), offersList.get(getAdapterPosition()));
            }
        }
    }
}
