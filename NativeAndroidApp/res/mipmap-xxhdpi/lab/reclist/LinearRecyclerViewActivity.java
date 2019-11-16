package com.wangze.lab.reclist;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.wangze.lab.R;

import java.util.ArrayList;
import java.util.List;

class Decoration extends RecyclerView.ItemDecoration
{
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state)
    {
        outRect.set(0, RecyclerView.EdgeEffectFactory.DIRECTION_TOP, 0, 0);
    }
}

public class LinearRecyclerViewActivity extends AppCompatActivity {
    private RecyclerView recyclerView;

    private void initRecyclerView()
    {
        recyclerView = findViewById(R.id.rv);
        recyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));
        List<HomeBtnItem> lines = new ArrayList<>();
        lines.add(HomeBtnItemFactory.getBtnByName(HomeBtnItemFactory.ButtonType.CAMERA));
        lines.add(HomeBtnItemFactory.getBtnByName(HomeBtnItemFactory.ButtonType.UPLOAD));
        lines.add(HomeBtnItemFactory.getBtnByName(HomeBtnItemFactory.ButtonType.OPEN));
        lines.add(HomeBtnItemFactory.getBtnByName(HomeBtnItemFactory.ButtonType.SETTING));
        recyclerView.addItemDecoration(new Decoration());
        recyclerView.setAdapter(new HomeBtnAdapter(lines));
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_linear_recycler_view);
        initRecyclerView();
    }
}
