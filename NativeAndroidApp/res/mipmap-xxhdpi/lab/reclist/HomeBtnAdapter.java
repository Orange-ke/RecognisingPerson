package com.wangze.lab.reclist;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.wangze.lab.R;

import java.util.List;


public class HomeBtnAdapter extends RecyclerView.Adapter<HomeBtnAdapter.ContentHolder>
{

    private List<HomeBtnItem> funcList;

    class ContentHolder extends RecyclerView.ViewHolder
    {
        private ImageButton btnSelf;
        private TextView btnName;

        public ContentHolder(@NonNull View itemView)
        {
            super(itemView);
            btnSelf = itemView.findViewById(R.id.home_btn);
            btnName = itemView.findViewById(R.id.home_btn_name);
        }
    }

    public HomeBtnAdapter(List<HomeBtnItem> funcList)
    {
        this.funcList = funcList;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ContentHolder holder =
                new ContentHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_homepage_btn, parent, false));

        Log.d("Holder", "onCreateViewHolder");
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentHolder holder, int position)
    {
        HomeBtnItem homeBtnItem = funcList.get(position);
        holder.btnName.setText(homeBtnItem.getBtnName());
        holder.btnSelf.setImageResource(homeBtnItem.getImageSrc());
        holder.btnSelf.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                // run this function , such as take phone or upload image
                Toast.makeText(v.getContext(), "function run", Toast.LENGTH_SHORT).show();
            }
        });
        Log.d("Holder", "onBindViewHolder");
    }

    @Override
    public int getItemCount()
    {
        return funcList.size();
    }
}
