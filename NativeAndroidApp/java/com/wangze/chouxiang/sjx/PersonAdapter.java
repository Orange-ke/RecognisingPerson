package com.wangze.chouxiang.sjx;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.fragment.NavHostFragment;

import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.R;
import com.wangze.chouxiang.wangze.DetailInformationFragment;
import com.wangze.chouxiang.ui.send.SendFragment;

import java.util.List;

public class PersonAdapter extends ArrayAdapter<Person>
{
    private List<Person> personList;
    private int rs;
    private Bitmap faceBitmap;

    public void setFaceBitmap(Bitmap faceBitmap)
    {
        this.faceBitmap = faceBitmap;
    }

    public PersonAdapter(@NonNull Context context, int resource, @NonNull List<Person> objects)
    {
        super(context, resource, objects);
        this.personList = objects;
        this.rs = resource;
    }

    @Override
    public int getCount()
    {
        return personList.size();
    }

    @Nullable
    @Override
    public Person getItem(int position)
    {
        return personList.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent)
    {
        convertView = LayoutInflater.from(this.getContext()).inflate(this.rs, parent, false);
        TextView nameTv = convertView.findViewById(R.id.person_name);
        TextView infoTv = convertView.findViewById(R.id.person_info);
        Button click = convertView.findViewById(R.id.person_btn);
        TextView score = convertView.findViewById(R.id.person_score);
        Person person = getItem(position);

        score.setText(String.valueOf(person.getScore()));
        nameTv.setText(person.getName());
        infoTv.setText(person.getInfo());

        if (person.getScore() < AccessBaiduManager.minScore)
        {
            click.setText(R.string.person_btn_unknown);
            click.setOnClickListener(v ->
            {
                DrawerActivity main = (DrawerActivity) v.getContext();
                NavHostFragment host = (NavHostFragment) main.getSupportFragmentManager().getFragments().get(0);
                SendFragment sendFragment = new SendFragment();
                Person choiceFace = personList.get(position);
                faceBitmap = Bitmap.createBitmap(faceBitmap,
                        Math.abs(choiceFace.getLeft()),
                        Math.abs(choiceFace.getTop()),
                        choiceFace.getWidth(), choiceFace.getHeight());
                host.getChildFragmentManager().beginTransaction()
                        .replace(R.id.nav_host_fragment, sendFragment, "SendFragment")
                        .addToBackStack(null)
                        .commitAllowingStateLoss();
                sendFragment.bitmap = faceBitmap;
            });
        } else
        {
            click.setText(R.string.person_btn_know);
            click.setOnClickListener(v ->
            {
                DrawerActivity main = (DrawerActivity) v.getContext();
                NavHostFragment host = (NavHostFragment) main.getSupportFragmentManager().getFragments().get(0);
                DetailInformationFragment fragment = new DetailInformationFragment();
                host.getChildFragmentManager()
                        .beginTransaction()
                        .replace(R.id.nav_host_fragment, fragment, "DetailInformationFragment")
                        .addToBackStack(null)
                        .commit();
                Person choiceFace = personList.get(position);
                int left = Math.abs(choiceFace.getLeft());
                int top = Math.abs(choiceFace.getTop());

                int edge1 = top + choiceFace.getHeight()
                        <= faceBitmap.getHeight() ?
                        choiceFace.getHeight() : faceBitmap.getHeight()-top;

                int edge2 = left + choiceFace.getWidth()
                        <= faceBitmap.getWidth() ?
                        choiceFace.getWidth(): faceBitmap.getWidth()-left;
                faceBitmap = Bitmap.createBitmap(faceBitmap, left, top, edge2, edge1);


                fragment.setBitmap(faceBitmap);
                fragment.setTheUserFace(getItem(position));
            });

        }
        return convertView;
    }

}
