package com.example.tools.Activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tools.Adapter.NineGridAdapter;
import com.example.tools.Adapter.OnAddPicturesListener;
import com.example.tools.tools.MyData;
import com.example.tools.R;
import com.example.tools.tools.ImgResponse;
import com.example.tools.tools.PostNewsResponse;
import com.example.tools.tools.Regex;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class WriteActivity extends AppCompatActivity {
    private String token;
    private EditText edit_title;
    private EditText edit_content;
    private ImageView back;
    private Button post;
    private Button edit_tag;
    private RecyclerView recyclerView;
    private NineGridAdapter nineGridAdapter;
    private String paper_title=null;
    private String paper_content=null;
    private int paper_tag=0;
    private Uri photoUri;   //相机拍照返回图片路径
    private File outputImage;
    private static final int GET_BACKGROUND_FROM_CAPTURE_RESOULT = 1;
    private static final int RESULT_REQUEST_CODE = 2;
    private static final int TAKE_PHOTO = 3;
    final List<Map<String,Object>> list = new ArrayList<>();
    private List<String> IdList=new ArrayList<>();

    private void selectCamera() {

        //创建file对象，用于存储拍照后的图片，这也是拍照成功后的照片路径
        outputImage = new File(this.getExternalCacheDir(), "camera_photos.jpg");
        try {
            //判断文件是否存在，存在删除，不存在创建
            if (outputImage.exists()) {
                outputImage.delete();
            }
            outputImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        photoUri = Uri.fromFile(outputImage);

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        startActivityForResult(intent, TAKE_PHOTO);

    }
    public static final String STR_IMAGE = "image/*";
    //选择相册
    private void selectPhoto(){
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, STR_IMAGE);
        startActivityForResult(intent, GET_BACKGROUND_FROM_CAPTURE_RESOULT);

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        switch (requestCode) {

            case RESULT_REQUEST_CODE:   //相册返回
                final String selectPhoto = getRealPathFromUri(this,cropImgUri);
                Thread thread=new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            MediaType mediaType = MediaType.parse("text/plain");
                            RequestBody body = new MultipartBody.Builder().setType(MultipartBody.FORM)
                                    .addFormDataPart("img",selectPhoto,
                                            RequestBody.create(MediaType.parse("application/octet-stream"),
                                                    new File(selectPhoto)))
                                    .addFormDataPart("type", "2")
                                    .build();
                            Request request = new Request.Builder()
                                    .url("http://101.43.145.51:10002/itnews/api/img-upload")
                                    .method("POST", body)
                                    .addHeader("Authorization", token)
                                    .build();

                            Response response = client.newCall(request).execute();
                            String responseData = response.body().string();
                            Gson gson=new Gson();
                            ImgResponse imgResponse=gson.fromJson(responseData,ImgResponse.class);
                            String url=imgResponse.getData().getImg_url();
                            int id=imgResponse.getData().getImg_id();
                            int size=list.size();
                            list.remove(size-1);
                            Map<String,Object> map=new HashMap<>();
                            map.put("type",1);
                            map.put("uri",url);
                            list.add(map);
                            IdList.add(String.valueOf(id));
                            if(list.size()!=9) {
                                Map<String, Object> map2 = new HashMap<>();
                                map2.put("type", 1);
                                list.add(map2);}
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                nineGridAdapter.notifyDataSetChanged();
                            }
                        });

                    }
                });
                thread.start();


                break;

            case TAKE_PHOTO://   拍照返回
                cropRawPhoto(photoUri);

                break;
            case GET_BACKGROUND_FROM_CAPTURE_RESOULT:
                photoUri = data.getData();
                cropRawPhoto(photoUri);


        }





    }
    private Uri cropImgUri;
    public void cropRawPhoto(Uri uri) {
        //创建file文件，用于存储剪裁后的照片
        File cropImage = new File(Environment.getExternalStorageDirectory(), "crop_image.jpg");
        String path = cropImage.getAbsolutePath();
        try {
            if (cropImage.exists()) {
                cropImage.delete();
            }
            cropImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        cropImgUri = Uri.fromFile(cropImage);
        Intent intent = new Intent("com.android.camera.action.CROP");
//设置源地址uri
        intent.setDataAndType(photoUri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 200);
        intent.putExtra("aspectY", 200);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("scale", true);
//设置目的地址uri
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropImgUri);
//设置图片格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("return-data", false);
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, RESULT_REQUEST_CODE);
    }
    public static String getRealPathFromUri(Context context, Uri uri) {
        if(context == null || uri == null) {
            return null;
        }
        if("file".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Byfile(context,uri);
        } else if("content".equalsIgnoreCase(uri.getScheme())) {
            return getRealPathFromUri_Api11To18(context,uri);
        }
//        int sdkVersion = Build.VERSION.SDK_INT;
//        if (sdkVersion < 11) {
//            // SDK < Api11
//            return getRealPathFromUri_BelowApi11(context, uri);
//        }

//        // SDK > 19
        return getRealPathFromUri_AboveApi19(context, uri);//没用到
    }
    private static String getRealPathFromUri_Byfile(Context context,Uri uri){
        String uri2Str = uri.toString();
        String filePath = uri2Str.substring(uri2Str.indexOf(":") + 3);
        return filePath;
    }
    @SuppressLint("NewApi")
    private static String getRealPathFromUri_AboveApi19(Context context, Uri uri) {
        String filePath = null;
        String wholeID = null;

        wholeID = DocumentsContract.getDocumentId(uri);

        // 使用':'分割
        String id = wholeID.split(":")[1];

        String[] projection = { MediaStore.Images.Media.DATA };
        String selection = MediaStore.Images.Media._ID + "=?";
        String[] selectionArgs = { id };

        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                selection, selectionArgs, null);
        int columnIndex = cursor.getColumnIndex(projection[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }
        cursor.close();
        return filePath;
    }

    /**
     * //适配api11-api18,根据uri获取图片的绝对路径。
     * 针对图片URI格式为Uri:: content://media/external/images/media/1028
     */
    private static String getRealPathFromUri_Api11To18(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };

        CursorLoader loader = new CursorLoader(context, uri, projection, null,
                null, null);
        Cursor cursor = loader.loadInBackground();

        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }

    /**
     * 适配api11以下(不包括api11),根据uri获取图片的绝对路径
     */
    private static String getRealPathFromUri_BelowApi11(Context context, Uri uri) {
        String filePath = null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection,
                null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            filePath = cursor.getString(cursor.getColumnIndex(projection[0]));
            cursor.close();
        }
        return filePath;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        post=findViewById(R.id.edit_post);
        MyData data=new MyData(WriteActivity.this);
        edit_title=findViewById(R.id.edit_title);
        edit_content=findViewById(R.id.edit_content);
        edit_tag=findViewById(R.id.edit_tag);
        back=findViewById(R.id.edit_back);
        recyclerView=findViewById(R.id.edit_image);
        token=data.load_token();
        Map<String,Object> map=new HashMap<>();
        map.put("type",1);
        list.add(map);
        nineGridAdapter=new NineGridAdapter(WriteActivity.this,list);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        builder.detectFileUriExposure();
        edit_tag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String tag[]=new String[]{"        表白","        交友","        约饭","        吐槽"};
                AlertDialog.Builder builder =new AlertDialog.Builder(WriteActivity.this);
                builder.setIcon(R.drawable.ic_launcher_foreground);
                builder.setTitle("选择新闻标签:");
                builder.setItems(tag, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(which==0)
                        {
                            edit_tag.setText("表白");
                            paper_tag=1;

                        }
                        else if (which==1)
                        {
                            edit_tag.setText("交友");
                            paper_tag=2;
                        }
                        else if (which==2)
                        {
                            edit_tag.setText("约饭");
                            paper_tag=3;
                        }
                        else
                        {
                            edit_tag.setText("吐槽");
                            paper_tag=4;
                        }
                    }
                });
                builder.create().show();
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setAdapter(nineGridAdapter);
        GridLayoutManager manager=new GridLayoutManager(this,3){
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        };

        recyclerView.setLayoutManager(manager);
        nineGridAdapter.setOnAddPicturesListener(new OnAddPicturesListener() {
            @Override
            public void onAdd() {
                final String way[] = new String[]{"        相机", "        相册"};
                AlertDialog.Builder builder1 = new AlertDialog.Builder(WriteActivity.this);
                builder1.setIcon(R.drawable.ic_launcher_foreground);
                builder1.setTitle("选择上传方式:");
                builder1.setItems(way, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0)
                        {
                            selectCamera();
                        }
                        else if(which==1)
                        {
                            selectPhoto();
                        }
                    }
                });
                builder1.create().show();
            }

        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paper_title=edit_title.getText().toString();
                paper_content=edit_content.getText().toString();

                if(paper_title.equals(""))
                {
                    Toast.makeText(WriteActivity.this,"标题不能为空",Toast.LENGTH_SHORT).show();
                }
                else if(paper_content.equals(""))
                {
                    Toast.makeText(WriteActivity.this,"内容不能为空",Toast.LENGTH_SHORT).show();
                }
                else if (paper_tag==0)
                {
                    Toast.makeText(WriteActivity.this,"未选择动态标签",Toast.LENGTH_SHORT).show();
                }
                else if(IdList.size()==0)
                {
                    Toast.makeText(WriteActivity.this,"动态发布至少需要添加一张图片",Toast.LENGTH_SHORT).show();
                } else if  (Regex.checkContinuous(paper_content)){
                    Toast.makeText(WriteActivity.this,"动态内容有连续空白符",Toast.LENGTH_SHORT).show();
                }
                else  if (Regex.checkPaperLenth(paper_content)){
                    Toast.makeText(WriteActivity.this,"动态内容空白字符过多",Toast.LENGTH_SHORT).show();
                }
                else
                {
                     String ids=IdList.toString();
                    String s=ids.substring(1,ids.length()-1);
                    final String img_ids=s;
                    Thread thread=new Thread(new Runnable() {
                        @Override
                        public void run() {
                            OkHttpClient client = new OkHttpClient().newBuilder()
                                    .build();
                            MediaType mediaType = MediaType.parse("application/x-www-form-urlencoded");
                            RequestBody body = RequestBody.create(mediaType, "title="+paper_title+"&content="+paper_content+"&tag="+paper_tag+"&img_ids="+img_ids);
                            Request request = new Request.Builder()
                                    .url("http://101.43.145.51:10002/itnews/api/news/release")
                                    .method("POST", body)
                                    .addHeader("Authorization", token)
                                    .build();
                            try {
                                Response response = client.newCall(request).execute();
                                String responseData = response.body().string();
                                Gson gson=new Gson();
                                PostNewsResponse postNewsResponse=gson.fromJson(responseData, PostNewsResponse.class);
                                final int code=postNewsResponse.getCode();
                                final String tip=postNewsResponse.getMsg();
                                {

                                        runOnUiThread(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(code==200)
                                                {
                                                    Toast.makeText(WriteActivity.this,"发布成功",Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                                else
                                                {
                                                    Toast.makeText(WriteActivity.this,tip,Toast.LENGTH_SHORT).show();
                                                    finish();
                                                }
                                            }
                                        });

                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Toast.makeText(WriteActivity.this,"网络连接好像断开了哦",Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        }
                    });
                    thread.start();
                }
            }
        });
    }
}