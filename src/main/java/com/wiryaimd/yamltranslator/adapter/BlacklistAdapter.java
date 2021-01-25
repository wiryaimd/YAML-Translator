package com.wiryaimd.yamltranslator.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.wiryaimd.yamltranslator.MainActivity;
import com.wiryaimd.yamltranslator.R;

import java.util.ArrayList;

public class BlacklistAdapter extends RecyclerView.Adapter<BlacklistAdapter.MyHolder> {

    private Context context;
    private ArrayList<String> disabledword;
    private RecyclerView recyclerView;

    public BlacklistAdapter(Context context, ArrayList<String> disabledword, RecyclerView recyclerView) {
        this.context = context;
        this.disabledword = disabledword;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public BlacklistAdapter.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_disabledword, parent, false);
        return new MyHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlacklistAdapter.MyHolder holder, int position) {
        holder.tvname.setText(disabledword.get(position));
        holder.imgclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.disabledword.remove(position);
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
                recyclerView.setAdapter(new BlacklistAdapter(context, MainActivity.disabledword, recyclerView));
            }
        });
    }

    @Override
    public int getItemCount() {
        return disabledword.size();
    }

    public class MyHolder extends RecyclerView.ViewHolder {

        public TextView tvname;
        public ImageView imgclose;

        public MyHolder(@NonNull View itemView) {
            super(itemView);

            tvname = itemView.findViewById(R.id.disabledword_name);
            imgclose = itemView.findViewById(R.id.disabledword_remove);

        }
    }
}
