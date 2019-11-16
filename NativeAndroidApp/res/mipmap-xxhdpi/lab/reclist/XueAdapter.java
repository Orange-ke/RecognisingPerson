package com.wangze.lab.reclist;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangze.lab.R;

import java.util.List;


public class XueAdapter extends RecyclerView.Adapter<XueAdapter.LinearHolder>
{
    private List<Line> haofan;

    public XueAdapter(List<Line> haofan)
    {
        this.haofan = haofan;
    }

    @NonNull
    @Override
    public LinearHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new LinearHolder(LayoutInflater.
                from(parent.getContext()).
                inflate(R.layout.layout_linear, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull final LinearHolder holder, int position)
    {
        holder.textView.setText(haofan.get(position).getS1());
        holder.textView2.setText(haofan.get(position).getS2());
        holder.btn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Toast.makeText(v.getContext(), "ok", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount()
    {
        return haofan.size();
    }


    class LinearHolder extends RecyclerView.ViewHolder
    {
        private TextView textView;
        private TextView textView2;
        private Button btn;

        public LinearHolder(@NonNull View itemView)
        {
            super(itemView);
            textView = itemView.findViewById(R.id.d1);
            textView2 = itemView.findViewById(R.id.d2);
            btn = itemView.findViewById(R.id.btn);
        }
    }
}
