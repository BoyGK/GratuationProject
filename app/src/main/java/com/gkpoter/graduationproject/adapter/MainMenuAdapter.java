package com.gkpoter.graduationproject.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.activity.ActivityContext;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class MainMenuAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_SIZE = 4;
    private static final int TYPE_MENU = 0;
    private static final int TYPE_COLLECTION = 1;
    private static final int TYPE_STEP = 2;
    private static final int TYPE_DISTRIBUTION = 3;

    private ActivityContext context;

    public MainMenuAdapter(ActivityContext context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        if (viewType == TYPE_MENU) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_menu, parent, false);
            return new MenuViewHolder(view);
        } else if (viewType == TYPE_COLLECTION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_collection, parent, false);
            return new CollectionViewHolder(view);
        } else if (viewType == TYPE_STEP) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_step, parent, false);
            return new StepViewHolder(view);
        } else if (viewType == TYPE_DISTRIBUTION) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_distribution, parent, false);
            return new DistributionViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (position == TYPE_MENU) {
            ((MenuViewHolder) holder).holderHelper.doBusiness();
        } else if (position == TYPE_COLLECTION) {
            ((CollectionViewHolder) holder).holderHelper.doBusiness();
        } else if (position == TYPE_STEP) {
            ((StepViewHolder) holder).holderHelper.doBusiness();
        } else if (position == TYPE_DISTRIBUTION) {
            ((DistributionViewHolder) holder).holderHelper.doBusiness();
        }
    }

    @Override
    public int getItemCount() {
        return TYPE_SIZE;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) {
            return TYPE_MENU;
        } else if (position == 1) {
            return TYPE_COLLECTION;
        } else if (position == 2) {
            return TYPE_STEP;
        } else if (position == 3) {
            return TYPE_DISTRIBUTION;
        } else {
            return TYPE_MENU;
        }
    }

    class MenuViewHolder extends RecyclerView.ViewHolder {
        public BaseAdapterHelper holderHelper;

        public MenuViewHolder(View itemView) {
            super(itemView);
            holderHelper = new MenuViewHolderHelper(context);
            holderHelper.initView(itemView);
        }
    }

    class CollectionViewHolder extends RecyclerView.ViewHolder {
        public BaseAdapterHelper holderHelper;

        public CollectionViewHolder(View itemView) {
            super(itemView);
            holderHelper = new CollectionViewHolderHelper(context);
            holderHelper.initView(itemView);
        }
    }

    class StepViewHolder extends RecyclerView.ViewHolder {
        public BaseAdapterHelper holderHelper;

        public StepViewHolder(View itemView) {
            super(itemView);
            holderHelper = new StepViewHolderHelper(context);
            holderHelper.initView(itemView);
        }
    }

    class DistributionViewHolder extends RecyclerView.ViewHolder {
        public BaseAdapterHelper holderHelper;

        public DistributionViewHolder(View itemView) {
            super(itemView);
            holderHelper = new DistributionViewHolderHelper(context);
            holderHelper.initView(itemView);
        }
    }
}

