package com.wangze.chouxiang.ui.home;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import com.wangze.chouxiang.R;

/**
 * 主页欢迎页面，向用提供识别/上传信息两个主要功能接口
 *
 * @author 王泽
 *
 * */
public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private ImageButton rec;
    private ImageButton upload;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        homeViewModel = ViewModelProviders.of(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        rec = root.findViewById(R.id.btn_recognize);
        upload = root.findViewById(R.id.btn_upload);
        rec.setOnClickListener(v->Navigation.findNavController(v).navigate(R.id.nav_gallery));
        upload.setOnClickListener(v->Navigation.findNavController(v).navigate(R.id.nav_upload));
        return root;
    }


}