package com.example.myapplication;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.myapplication.Adapter.PictureAdapter;
import com.example.myapplication.Bean.Picture;
import com.example.myapplication.Util.BaseUtil;
import com.example.myapplication.Util.CustomDialog;
import com.example.myapplication.Util.DataParser;
import com.example.myapplication.Util.Url;
import com.example.myapplication.comments.PublicBean;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import static android.app.Activity.RESULT_OK;
import static com.example.myapplication.MainActivity.JSON;
import static com.example.myapplication.MainActivity.username;

public class Community_Fragment extends Fragment {
    private RecyclerView community_rec;
    private RecyclerView picture_rec;
    SwipeRefreshLayout refreshLayout;
    private EditText content_text;
    private Context context;
    private List<Picture> pictureList = new ArrayList<>();
    private ImageView addview;
    private int Album_requsetcode = 1;
    private int CAMERA_requestcode = 2;
    private File newFile;
    private Uri contenUri;
    private int index = 0;//目前图片数量
    private PictureAdapter pictureAdapter;
    private List<File> files = new ArrayList<>();
    private CommunityAdapter adapter;
    Activity activity;
    List<String> path;
    private  CustomDialog dialog;
  private   SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;
    private List<PublicBean> publicBeans = new ArrayList<>();
    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1:
                    Bundle bundle = msg.getData();
                    try {
                        JSONObject jsonObject = new JSONObject(bundle.get("publicresult").toString());
                        String info = jsonObject.getString("info");
                        String status = jsonObject.getString("status");
                        Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
                        if (status.equals("success")) {
                            content_text.setText("");
                            Toast.makeText(context, "发表成功", Toast.LENGTH_SHORT).show();
                            pictureList.clear();
                            files=new ArrayList<>();
                            pictureAdapter.notifyDataSetChanged();
                            addview.setVisibility(View.VISIBLE);
                            index=0;
                            dialog.dismiss();
                        } else {
                            Toast.makeText(context, "发表失败", Toast.LENGTH_SHORT).show();
                            pictureList.clear();
                            files=new ArrayList<>();
                            dialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(context, "出现未知错误", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                    break;
                case 2:
                    Bundle bundle1 = msg.getData();
                    publicBeans = new DataParser().PublicParser(bundle1);
                    adapter = new CommunityAdapter(publicBeans, context);
                    LinearLayoutManager manager = new LinearLayoutManager(context);
                    manager.setOrientation(RecyclerView.VERTICAL);
                    community_rec.setLayoutManager(manager);
                    community_rec.setAdapter(adapter);
                    dialog.dismiss();
                    break;
                case 3:
                    pictureAdapter.notifyDataSetChanged();
                    break;
                case 5:
                    addview.setVisibility(View.GONE);//隐藏添加照片
            }
            return false;
        }
    });

    public Community_Fragment(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        sharedPreferences= getActivity().getSharedPreferences("communityPublic",Context.MODE_PRIVATE);
        View view = inflater.inflate(R.layout.community_fragment, container, false);
        addview = view.findViewById(R.id.add_picture);
        picture_rec = view.findViewById(R.id.picture_rec);
        refreshLayout=view.findViewById(R.id.refreshlayout);
        refreshLayout.setColorSchemeColors(getResources().getColor(R.color.brown));
        pictureAdapter = new PictureAdapter(pictureList);
        LinearLayoutManager manager = new LinearLayoutManager(view.getContext());
        manager.setOrientation(RecyclerView.HORIZONTAL);
        picture_rec.setLayoutManager(manager);
        picture_rec.setAdapter(pictureAdapter);
        community_rec = view.findViewById(R.id.community_recyclerview);
        ImageView publish = view.findViewById(R.id.publish);
        content_text = view.findViewById(R.id.contet_text);
        //content_text.clearFocus();
        dialog=new CustomDialog(context,"数据获取中");
        dialog.show();
        int size= sharedPreferences.getInt("length",0);
        Log.i("size---->",Integer.toString(size));
        if (size==0) {
            GetPublicInfo();
        }
        else {
            GetLocalInfo(size);
        }
        publish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss");// HH:mm:ss//获取当前时间
                Date date = new Date(System.currentTimeMillis());
                String time = simpleDateFormat.format(date);
                String content = content_text.getText().toString();
                System.out.println(content_text.getText().toString() + time);
                if (username == null) {
                    Toast.makeText(context, "请先登陆再执行该操作", Toast.LENGTH_SHORT).show();
                } else if (content.equals("")) {
                    Toast.makeText(context, "请不要发布空白内容", Toast.LENGTH_SHORT).show();
                } else {
                     dialog=new CustomDialog(context,"发表中..");
                    dialog.show();
                    Map<String, String> map = new HashMap<>();
                    map.put("username", username);
                    map.put("content", content);
                    map.put("time", time);
                    PublicInfo(files, map);
                }
            }
        });
        addview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopueWindow();
            }
        });
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshLayout.setRefreshing(false);
                GetPublicInfo();
            }
        });

        return view;
    }
    private void GetLocalInfo(int size){
        if (size!=0){
            JSONArray jsonArray=new JSONArray();
            for (int i=0;i<size;i++){
                jsonArray.put(sharedPreferences.getString("publicBean"+i,null));
            }
            Log.i("LocalInfo",jsonArray.toString());
            Bundle bundle=new Bundle();
            bundle.putString("getpublicinfo",jsonArray.toString());
            Message msg=new Message();
            msg.setData(bundle);
            msg.what=2;
            handler.sendMessage(msg);
        }
    }
    private void GetPublicInfo() {
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder().get().url(Url.url + "community/GetAllPublicServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("getpublicinfo", response.body().string());
                Message message = new Message();
                message.what = 2;
                message.setData(bundle);
                handler.sendMessage(message);
            }
        });
    }

    //上传图片和参数
    private void PublicInfo(List<File> files, Map<String, String> map) {
        BaseUtil baseUtil = new BaseUtil();
        map.put("number", Integer.toString(files.size()));
        for (int i = 0; i < files.size(); i++) {
            map.put("image" + i, baseUtil.encodeImage(files.get(i).getPath()));
        }
        OkHttpClient okHttpClient = new OkHttpClient();
        Gson gson = new Gson();
        RequestBody requestBody = RequestBody.create(JSON, gson.toJson(map));
       /* MultipartBody.Builder multipartBodyBuilder = new MultipartBody.Builder();
        multipartBodyBuilder.setType(MultipartBody.FORM);
         if (map!=null){
             for (String key : map.keySet()){
                 multipartBodyBuilder.addFormDataPart(key, map.get(key));
             }
         }
         if (files!=null){
             for (File file : files){
                 multipartBodyBuilder.addFormDataPart("file", file.getName(), RequestBody.create(MediaType.parse("application/octet-stream"), file));
             }
         }
         RequestBody requestBody=multipartBodyBuilder.build();*/
        Request request = new Request.Builder().post(requestBody).url(Url.url + "community/PublishServlet").build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                Looper.prepare();
                Toast.makeText(context, "连接服务器失败", Toast.LENGTH_SHORT).show();
                Looper.loop();
                call.cancel();
                dialog.dismiss();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Bundle bundle = new Bundle();
                bundle.putString("publicresult", response.body().string());
                Message message = new Message();
                message.what = 1;
                message.setData(bundle);
                handler.sendMessage(message);
                call.cancel();
            }
        });
    }


    private void showPopueWindow() {
        View popView = View.inflate(context, R.layout.popupwindow, null);
        Button bt_album = popView.findViewById(R.id.btn_pop_album);
        Button bt_camera = popView.findViewById(R.id.btn_pop_camera);
        Button bt_cancle = popView.findViewById(R.id.btn_pop_cancel);
        //获取屏幕宽高
        int weight = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels * 1 / 4;
        final PopupWindow popupWindow = new PopupWindow(popView, weight, height);
        popupWindow.setAnimationStyle(R.style.popup_ani);
        popupWindow.setFocusable(true);
        //点击外部popueWindow消失
        popupWindow.setOutsideTouchable(true);
        bt_album.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MultiImageSelectorActivity.class);
                // whether show camera
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SHOW_CAMERA, false);
// max select image amount
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_COUNT, 9);
// select mode (MultiImageSelectorActivity.MODE_SINGLE OR MultiImageSelectorActivity.MODE_MULTI)
                intent.putExtra(MultiImageSelectorActivity.EXTRA_SELECT_MODE, MultiImageSelectorActivity.MODE_MULTI);
// default select images (support array list)
                //  intent.putStringArrayListExtra(MultiImageSelectorActivity.EXTRA_DEFAULT_SELECTED_LIST, defaultDataArray);
                startActivityForResult(intent, Album_requsetcode);
                popupWindow.dismiss();

            }
        });
        bt_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takeCamera(CAMERA_requestcode);
                popupWindow.dismiss();

            }
        });
        bt_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();

            }
        });
        //popupWindow消失屏幕变为不透明
        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
                lp.alpha = 1.0f;
                getActivity().getWindow().setAttributes(lp);
            }
        });
        //popupWindow出现屏幕变为半透明
        WindowManager.LayoutParams lp = getActivity().getWindow().getAttributes();
        lp.alpha = 0.5f;
        getActivity().getWindow().setAttributes(lp);
        popupWindow.showAtLocation(popView, Gravity.BOTTOM, 0, 10);

    }

    private void takeCamera(int num) {

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File imagePath = new File(context.getFilesDir(), "images");
            if (!imagePath.exists()) {
                imagePath.mkdirs();
            }
            newFile = new File(imagePath, generateFileName() + ".jpg");

            contenUri = FileProvider.getUriForFile(context, "com.example.myapplication", newFile);

            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contenUri);
            takePictureIntent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            // photoFile = createImageFile();
            // Continue only if the File was successfully created
            //if (photoFile != null) {

            //     System.out.println("photoFile不为空");
            //  }
        }
        startActivityForResult(takePictureIntent, num);//跳转界面传回拍照所得数据
    }

    private String generateFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPG_" + timeStamp;
        System.out.println(".........." + imageFileName);
        return imageFileName;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_requestcode) {
                try {
                    System.out.println("拍摄的图片路径" + contenUri);
                    Picture picture = new Picture();
                    if (index < 7) {
                        picture.setBitmap(getBitmapFormUri(contenUri, true));
                        pictureList.add(picture);
                        handler.sendEmptyMessage(3);
                        index++;
                    }

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else if (requestCode == Album_requsetcode) {
                List<String> path = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                Log.i("selectPhoto", path.toString());
                for (String url : path) {
                    try {
                        // System.out.println(uri);
                        Picture picture = new Picture();
                        if (index < 7) {
                            //  Log.i("files","添加成功"+files.size());
                            picture.setBitmap(getBitmapFormUri(Uri.parse(url), false));

                            pictureList.add(picture);
                            handler.sendEmptyMessage(3);
                            index++;
                            if (index == 8) {
                                handler.sendEmptyMessage(5);
                            }
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }


    public Bitmap getBitmapFormUri(Uri uri, boolean iscamera) throws IOException {
        InputStream input;
        File file;
        if (iscamera)
            input = getActivity().getContentResolver().openInputStream(uri);
        else {
            file = new File(uri.toString());
            input = new FileInputStream(file);
        }
        // InputStream input = getActivity().getContentResolver().openInputStream(uri);
        // InputStream input=getActivity().openFileInput(uri.toString());
        //这一段代码是不加载文件到内存中也得到bitmap的真是宽高，主要是设置inJustDecodeBounds为true
        BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
        onlyBoundsOptions.inJustDecodeBounds = true;//不加载到内存
        onlyBoundsOptions.inDither = true;//optional
        onlyBoundsOptions.inPreferredConfig = Bitmap.Config.RGB_565;//optional
        BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
        input.close();
        int originalWidth = onlyBoundsOptions.outWidth;
        int originalHeight = onlyBoundsOptions.outHeight;
        if ((originalWidth == -1) || (originalHeight == -1))
            return null;

        //图片分辨率以480x480为标准
        float hh = 600f;//这里设置高度为600f
        float ww = 600f;//这里设置宽度为600f
        //缩放比，由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
        int be = 1;//be=1表示不缩放
        if (originalWidth > originalHeight && originalWidth > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (originalWidth / ww);
        } else if (originalWidth < originalHeight && originalHeight > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (originalHeight / hh);
        }
        if (be <= 0)
            be = 1;
        //比例压缩
        BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
        bitmapOptions.inSampleSize = be;//设置缩放比例
        bitmapOptions.inDither = true;
        bitmapOptions.inPreferredConfig = Bitmap.Config.RGB_565;
        if (iscamera)
            input = getActivity().getContentResolver().openInputStream(uri);
        else {
            file = new File(uri.toString());
            input = new FileInputStream(file);
        }
        Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
        input.close();

        return compressImage(bitmap);//再进行质量压缩
    }

    //图片压缩
    public Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            //第一个参数 ：图片格式 ，第二个参数： 图片质量，100为最高，0为最差  ，第三个参数：保存压缩后的数据的流
            image.compress(Bitmap.CompressFormat.PNG, options, baos);//这里压缩options，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
            if (options <= 0)
                break;
        }
        BitmapToFile(baos);
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    private void BitmapToFile(ByteArrayOutputStream baos) {
        File file = new File(context.getFilesDir(), generateFileName() + ".png");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        files.add(file);
        Log.i("files", "添加成功" + files.size());
    }

    @Override
    public void onStop() {
        super.onStop();
        editor=sharedPreferences.edit();
        Gson gson = new Gson();
        editor.putInt("length",publicBeans.size());
        int i=0;
        for (PublicBean publicBean:publicBeans){
            editor.putString("publicBean"+i,gson.toJson(publicBean));
            i+=1;
        }
        editor.apply();
        Log.i("CommunityFragment","已经存储");
    }
}
