package cn.datacharm.springboot_uploadMutiInform.controller;

import cn.datacharm.springboot_uploadMutiInform.tool.AccessBaidu;
import com.alibaba.fastjson.JSON;

import java.io.FileInputStream;
import java.util.Base64;
import java.util.Base64.Encoder;
//import jdk.nashorn.internal.ir.debug.JSONWriter;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Controller
public class FileController {
    public int num=0;
    @RequestMapping("/")
    public String index(){
        return "index";
    }
    @RequestMapping("/upload")
    public String upload(){
        return "upload";
    }

    @RequestMapping("/show")
    public String show(Model model){
        String src = "img/pig.jpg";
        model.addAttribute("src",src);
        return "show";
    }

    @RequestMapping("/test")
    public String test(){
        return "test";
    }


    @RequestMapping("/uploadImg")
    @ResponseBody
    public String upload(@RequestParam("file") MultipartFile[] mfiles,Model model)  {
        if (mfiles==null) {
            return "上传失败，请选择文件";
        }
        for(int i = 0;i < mfiles.length;i++){
            MultipartFile mfile = mfiles[i];
            //保存文件
            if (!mfile.isEmpty()){

                try {
                    File f=File.createTempFile("tmp", null);
                    mfile.transferTo(f);
                    f.deleteOnExit();     //使用完成删除文件
                    FileInputStream inputFile = new FileInputStream(f);
                    byte[] buffer = new byte[(int) f.length()];
                    inputFile.read(buffer);
                    inputFile.close();
                    //String base64=new BASE64Encoder().encode(buffer);
                    Encoder encoder = Base64.getEncoder();
                    String base64 = encoder.encodeToString(buffer);

                    // base64EncoderImg =  Base64.getEncoder().encode(bytes).toString();
                    //base64 = base64.replaceAll("[\\s*\t\n\r]", "");
                    String name = mfile.getOriginalFilename();
                    name = name.substring(0, name.indexOf("."));
                    System.out.println(base64);
                    System.out.println(name);
                    System.out.println(AccessBaidu.num);

                    AccessBaidu accessBaidu=AccessBaidu.getInstance();
                    AccessBaidu.num++;
                    if(AccessBaidu.num>1) {
                        try {
                            Thread.currentThread().sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    accessBaidu.uploadFaceInform(base64,name+"////暂无描述");


                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        //String fileName = file.getOriginalFilename();

        //File dest = null;
        //String os = System.getProperty("os.name");
        //System.out.println(os);
        //if (os.toLowerCase().startsWith("win")) {
            //String path = "G:"+File.separator+"img"+File.separator;
            //System.out.println(path);
            //dest= new File(path + fileName);
        //}else {
     /*       String path = "/webapps/img/";
            dest= new File(path + fileName);
        }
        model.addAttribute("src","img/"+fileName);
        try {
            file.transferTo(dest);
            return JSON.toJSONString("上传成功！");
        } catch (IOException e) {
            return JSON.toJSONString("上传失败！");
        }*/
     return JSON.toJSONString("上传成功");

    }

}
