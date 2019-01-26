package com.gkpoter.graduationproject.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gkpoter.graduationproject.R;
import com.gkpoter.graduationproject.activity.ActivityContext;

/**
 * Created by "GKpoter" on 2019/1/11.
 */
public class MenuViewHolderHelper extends BaseAdapterHelper {

    private TextView collection;
    private TextView step;
    private TextView distribution;

    private RecyclerView recyclerView;

    public MenuViewHolderHelper(ActivityContext context) {
        super(context);
        recyclerView = (RecyclerView) getActivityContext().getView();
    }

    @Override
    public void initView(View itemView) {
        collection = itemView.findViewById(R.id.informationCollection);
        step = itemView.findViewById(R.id.stepEstimate);
        distribution = itemView.findViewById(R.id.nformationDistribution);
    }

    @Override
    public void doBusiness() {
        collection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(1);
            }
        });
        step.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(2);
            }
        });
        distribution.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerView.scrollToPosition(3);
            }
        });
    }
}
