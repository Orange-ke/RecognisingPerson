package cn.datacharm.springboot_uploadMutiInform.tool;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpMethod;

import java.io.IOException;

public class AccessBaidu {
    private static AccessBaidu accessBaidu;
    public static int num=0;
    private AccessBaidu(){}

    //方法同步，调用效率低
    public static synchronized AccessBaidu getInstance(){
        if(accessBaidu==null){
            accessBaidu=new AccessBaidu();
        }
        return accessBaidu;
    }

    public  void uploadFaceInform(String base64Img, String uname) {
        String res="";
        JSONObject obj = new JSONObject();
        try {
            obj.put("image", base64Img);
            obj.put("image_type", "BASE64");
            obj.put("group_id", "group1");
            obj.put("user_id", "User" + System.currentTimeMillis());
            obj.put("user_info", uname);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            res = HttpHandler.HttpRestClient("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369", HttpMethod.POST,obj);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(res);
    }
}
