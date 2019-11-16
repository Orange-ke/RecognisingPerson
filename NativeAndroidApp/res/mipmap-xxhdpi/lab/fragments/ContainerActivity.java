package com.wangze.lab.fragments;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.wangze.lab.R;

public class ContainerActivity extends AppCompatActivity
{
    private AFragment afragment;
    private BFragment bFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_container);
        afragment = AFragment.newFragment("Argument Title");
        getSupportFragmentManager().beginTransaction().add(R.id.fl_container, afragment).commitAllowingStateLoss();
        Button cg = findViewById(R.id.btn_change_fg);
        cg.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {

                getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, new BFragment()).commitAllowingStateLoss();
            }
        });
    }


}
