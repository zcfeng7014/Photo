package com.cfeng.photo;

import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ListView gv;
    ArrayList<File> list;
    BaseAdapter adapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = ViewGroup.inflate(getApplicationContext(),R.layout.imageview,null);
            }
            TextView tv= (TextView) convertView.findViewById(R.id.filedir);
            ImageView img= (ImageView) convertView.findViewById(R.id.img);
            Button del= (Button) convertView.findViewById(R.id.delete);
            Button open= (Button) convertView.findViewById(R.id.open);
            del.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    list.get(position).delete();
                    list.remove(position);
                    adapter.notifyDataSetChanged();
                }
            });
            open.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent("android.intent.action.VIEW");
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    Uri uri = Uri.fromFile(list.get(position));
                    intent.setDataAndType(uri, "image/*");
                    startActivity(intent);
                }
            });
            tv.setText(list.get(position).getPath());
            Glide.with(convertView.getContext()).load((File)list.get(position)).override(160,160)
                    .into(img);
            return convertView;
        }
    };
    Handler handler= new Handler(){
        @Override
        public void handleMessage(Message msg) {
            list.add((File) msg.obj);
            adapter.notifyDataSetChanged();
            super.handleMessage(msg);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        list=new ArrayList<>();
        gv= (ListView) findViewById(R.id.table);
        loadimage();
        gv.setAdapter(adapter);

    }
    private void loadimage() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                findImage(Environment.getExternalStorageDirectory());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "搜索完成 搜索到"+list.size()+"本", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }
    private void findImage(File root) {
        if (root.listFiles().length > 0) {
            for (File file : root.listFiles()) {
                if(file.isDirectory())
                    findImage(file);
                else
                {
                    String filename=file.getName();
                    int index=filename.indexOf(".");
                    String type=filename.substring(index+1);
                    try {
                        if(type.equals("jpg")){
                            Message msg=new Message();
                            msg.obj=file;
                            handler.sendMessage(msg);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
