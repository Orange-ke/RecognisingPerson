package com.wangze.chouxiang.sjx;
import android.app.Activity;
import android.graphics.Bitmap;
import androidx.fragment.app.Fragment;
import com.wangze.chouxiang.DrawerActivity;
import com.wangze.chouxiang.wangze.DetailInformationFragment;
import com.wangze.chouxiang.ui.send.SendFragment;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static com.wangze.chouxiang.wangze.ImageTools.ChangeBitmapToBase64;


/**
 * 提供发送请求到百度人脸识别服务器，处理响应结果，静态类
 *
 * @author 王泽
 * @author 邵佳鑫
 */
public class AccessBaiduManager
{
    /**
     * 百度返回的识别结果标识码，由<code>CODE_SUCCESS/CODE_UNKNOWN</code>标识
     * 实际用处不大:P
     */
    private static int errorCode;
    /**
     * 人脸列表，用于保存多识别结果，自1.0后加入，并移除单人脸识别功能
     */
    private static List<Person> personList;

    /**
     * 结果标识码<code>CODE_SUCCESS</code>标识识别成功
     */
    public static final int CODE_SUCCESS = 0;

    /**
     * 结果标识码<code>CODE_UNKNOWN</code>标识未识别到人脸
     */
    public static final int CODE_UNKNOWN = 222202;

    /**
     * 识别结果score最小值，小于该值的人脸识别结果将不被认作识别一致
     */
    public static final int minScore = 60;

    /**
     * 发送请求的主要方法。将图片发送至百度人脸识别服务器。
     *
     * @param activity 响应结果保存的地方
     * @param bitmap   要发送的图片，应包含人脸图像
     * @implSpec 将<code>bitmap</code>作为json的字段，发送到百度。百度响应的结果将
     * 会被调用setRecognizeResult()设置到{@link DrawerActivity}中
     */
    public static void sendPhotoToMultiRecognize(Activity activity, Bitmap bitmap)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
            obj.put("max_face_num", 10);
            obj.put("match_threshold", 10);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/multi-search?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try
        {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            ((DrawerActivity) activity).setRecognizeResult(res);
            System.out.println(res);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 发送请求的主要方法。将图片发送至百度人脸识别服务器，但仅获取人脸分析结果，如年龄/颜值/表情等。
     * 主要用于{@link DetailInformationFragment}显示人脸识别结果的详细结果
     *
     * @param activity 响应结果保存的地方
     * @param bitmap   要发送的图片，应包含人脸图像
     * @implSpec 将<code>bitmap</code>作为json的字段，发送到百度。百度响应的结果将
     * 会被调用setRecognizeResult()设置到{@link DrawerActivity}中
     */
    public static void sendFaceForMoreInformation(Activity activity, Bitmap bitmap)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id_list", "group1");
            obj.put("max_face_num", 1);
            obj.put("face_field", "age,beauty,emotion");
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());
        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/detect?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try
        {
            Response response = client.newCall(request).execute();
            String res = response.body().string();
            ((DrawerActivity) activity).setRecognizeDetailResult(res);
            System.out.println(res);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 处理人脸识别的详细结果，主要用于{@link DetailInformationFragment}显示人脸识别结果的详细结果
     *
     * @param jsonData 服务器返回的结果
     * @param fragment 请求处理该响应的fragment
     * @param theOne   被请求查看细节的人脸
     */
    public static void handleResponseOfDetect(String jsonData, Fragment fragment, Person theOne)
    {
        try
        {
            JSONObject jsonResult = new JSONObject(jsonData);
            errorCode = jsonResult.getInt("error_code");

            if (errorCode == CODE_SUCCESS)
            {
                JSONObject result = jsonResult.getJSONObject("result");
                JSONArray faceList = result.getJSONArray("face_list");
                JSONObject location = faceList.getJSONObject(0).getJSONObject("location");
                int age = (int) faceList.getJSONObject(0).getDouble("age");
                int beauty = (int) faceList.getJSONObject(0).getDouble("beauty");
                int angle = (int) faceList.getJSONObject(0).getJSONObject("angle").getDouble("roll");
                JSONObject expression = faceList.getJSONObject(0).getJSONObject("emotion");
                String emotion = expression.getString("type");
                Person he = new Person();
                he.setHeight(location.getInt("height"));
                he.setWidth(location.getInt("width"));
                he.setLeft(location.getInt("left"));
                he.setTop(location.getInt("top"));
                he.setAge(age);
                he.setBeauty(beauty);
                he.setEmotion(emotion);
                he.setName(theOne.getName());
                he.setInfo(theOne.getInfo());
                he.setRotation(angle);
                ((DetailInformationFragment) fragment).setTheUserFace(he);
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * 处理多人脸识别任务，结果暂存至 {@link DrawerActivity#setRecognizeResult(String)}
     *
     * @param jsonData 服务器返回的字符串响应
     * @param fragment 请求处理的fragment
     */
    public static void handleResponseOfSearch(String jsonData, Fragment fragment)
    {
        try
        {
            JSONObject object = new JSONObject(jsonData);
            errorCode = object.getInt("error_code");

            if (errorCode == CODE_SUCCESS)
            {
                JSONObject result = object.getJSONObject("result");
                JSONArray faceList = result.getJSONArray("face_list");

                int face_num = result.getInt("face_num");
                personList = new ArrayList<>(face_num);
                for (int idx = 0; idx < face_num; idx++)
                {
                    Person newPerson = new Person();
                    JSONObject location = faceList.getJSONObject(idx).getJSONObject("location");
                    newPerson.setHeight(location.getInt("height"));
                    newPerson.setWidth(location.getInt("width"));
                    newPerson.setLeft(location.getInt("left"));
                    newPerson.setTop(location.getInt("top"));
                    newPerson.setRotation(location.getInt("rotation"));
                    JSONArray userList = faceList.getJSONObject(idx).getJSONArray("user_list");
                    JSONObject p = userList.getJSONObject(0);
                    String str = p.getString("user_info");
                    String[] strs = str.split("////");
                    newPerson.setScore(p.getInt("score"));
                    newPerson.setId((short) idx);
                    if (p.getInt("score") < minScore)
                    {
                        newPerson.setName("第" + newPerson.getId() + "张脸");
                        newPerson.setInfo("未检测到此人信息");
                    } else
                    {
                        newPerson.setName(strs[0]);
                        newPerson.setInfo(strs[1]);
                        newPerson.setScore(p.getInt("score"));
                    }
                    System.out.println(newPerson);
                    personList.add(newPerson);
                }

                Collections.sort(personList, (p1, p2) -> p2.getScore() - p1.getScore());
            } else if (errorCode == CODE_UNKNOWN)
            {
                personList = Collections.emptyList();
            }
            switch (fragment.getTag())
            {
                case "ShowRecognizeResultFragment":
                    ((ShowRecognizeResultFragment) fragment).setPersonList(personList);
                    ((ShowRecognizeResultFragment) fragment).getShowFaceIv().setPersonList(personList);
                    ((ShowRecognizeResultFragment) fragment).setErrorCode(errorCode);
                    break;
                case "SendFragment":
                    ((SendFragment) fragment).setPersonList(personList);
                    ((SendFragment) fragment).setErrorCode(errorCode);
                    break;
                default:
                    throw new IllegalStateException("Unexpected value: " + fragment.getTag());
            }

        } catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 向服务器上传人脸信息，供search查询使用
     *
     * @param bitmap  人脸图片
     * @param uname   用户输入的用户名
     * @param uinform 用户输入的信息
     */
    public static void uploadFaceInform(Bitmap bitmap, String uname, String uinform)
    {
        OkHttpClient client = new OkHttpClient();
        String Base64Photo = ChangeBitmapToBase64(bitmap);
        JSONObject obj = new JSONObject();
        try
        {
            obj.put("image", Base64Photo);
            obj.put("image_type", "BASE64");
            obj.put("group_id", "group1");
            obj.put("user_id", "User" + System.currentTimeMillis());
            obj.put("user_info", uname + "////" + uinform);
        } catch (JSONException e)
        {
            e.printStackTrace();
        }
        RequestBody requestBody = RequestBody
                .create(MediaType.parse("application/json; charset=utf-8"), "" + obj.toString());

        Request request = new Request.Builder()
                .url("https://aip.baidubce.com/rest/2.0/face/v3/faceset/user/add?access_token=24.29062b34a7fc64f68465706de4b9a2a8.2592000.1575358784.282335-17603369")
                .post(requestBody)
                .build();

        try
        {
            client.newCall(request).execute();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }


}
