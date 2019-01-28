package com.clarkgarrett.websocketecho;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ViewHolder> {

    private List<String> dataList;

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvEchoed;

        ViewHolder (View itemView){
            super(itemView);
            tvEchoed = itemView.findViewById(R.id.tvEchoed);
        }
    }

    RvAdapter(List<String> dataList){
        this.dataList = dataList;
    }

    @NonNull
    @Override
    public RvAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        return new ViewHolder(inflater.inflate(R.layout.item_layout, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RvAdapter.ViewHolder viewHolder, int position) {
        viewHolder.tvEchoed.setText(dataList.get(position));
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }
}
