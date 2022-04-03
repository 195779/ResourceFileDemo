package com.example.resourcefiledemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.xmlpull.v1.XmlPullParser;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private Resources resources;/*系统资源控制*/
    private TextView displayView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button readRawButton = (Button) findViewById(R.id.read_raw);
        Button readXmlButton = (Button) findViewById(R.id.read_xml);
        Button clearButton = (Button) findViewById(R.id.clear);
        readRawButton.setOnClickListener(readRawButtonListener);
        readXmlButton.setOnClickListener(readXmlButtonListener);
        clearButton.setOnClickListener(clearButtonListener);
        this.displayView = (TextView) findViewById(R.id.display);
        this.resources = this.getResources();
    }

    View.OnClickListener readRawButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputStream inputStream = null;
            try {
                inputStream = resources.openRawResource(R.raw.raw_file);
                /*获取该xml资源的数据流，读取资源数据*/
                byte[] reader = new byte[inputStream.available()];
                /*inputStream.available()：对本地文件而言会返回字节总数
                （对网络文件可能由于每次接收数量不同而不同）*/
                while (inputStream.read(reader) != -1) {
                    /*inputStream.read(byte[])
                    * 这个方法使用一个byte的数组作为一个缓冲区，
                    * 每次从数据源中读取和缓冲区大小（二进制位）相同的数据并将其存在缓冲区中。
                    * 对于这个程序，由于前面用available把byte数组能包含的字节总数
                    * 就是我们访问的本地文件的数据流的字节总数
                    *
                    * 也就是说如果我们准备对该资源数据流的每N个字节做一样的操作，
                    * 那么就应该把byte数组的大小设置成N，
                    * 然后在这个循环里面每次都读N个字节放在reader数组里面（每次都是清空再写入）
                    * 然后再对这N个字节的数组reader来进行操作
                    *
                    * 当然了我们这里是直接读完了整个数据流，byte类型的reader数组直接包含了整个数据流的数据*/
                }
                displayView.setText(new String(reader, "utf-8"));
                /*显示一下*/
            } catch (IOException e) {
                Log.e("ResourceFileDemo", e.getMessage(), e);
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
    };

    View.OnClickListener readXmlButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            XmlPullParser parser = resources.getXml(R.xml.people);
            /*通过getResources().getXml()获的XML原始文件，得到XmlResourceParser对象，
            通过该对象来判断是文档的开头还是结尾，*/
            String msg = "";
            try {
                while (parser.next() != XmlPullParser.END_DOCUMENT) {
                    /*当没有读到最后的时候：
                    * parser.next()循环解析下一个元素：按标签为一个元素可能是？*/
                    /*以下指导书的代码被我注释掉了，自己给他换了一个*/
                    /*String people = parser.getName();
                    String name = null;
                    String age = null;
                    String height = null;
                    if ((people != null) && people.equals("person")) {
                        int count = parser.getAttributeCount();
                        for (int i = 0; i < count; i++) {
                            String attrName = parser.getAttributeName(i);
                            String attrValue = parser.getAttributeValue(i);
                            if ((attrName != null) && attrName.equals("name")) {
                                name = attrValue;
                            } else if ((attrName != null) && attrName.equals("age")) {
                                age = attrValue;
                            } else if ((attrName != null) && attrName.equals("height")) {
                                height = attrValue;
                            }
                        }
                        if ((name != null) && (age != null) && (height != null)) {
                            msg += "姓名：" + name + "，年龄：" + age + "，身高： " + height + "\n";
                        }*/
                    String people = parser.getName();
                    int count = parser.getAttributeCount();
                    msg += "标签为：" + people + " 标签里面的行数：" + count + " " + "\n";
                    if(count > 0){
                        for(int i = 0; i < count; i++){
                            String rowName = parser.getAttributeName(i);
                            String rowValues = parser.getAttributeValue(i);
                            msg += "  " + "标签" + people + "的第" + i + "行名字为：" + rowName
                                    + " 数据为：" + rowValues + "\n";
                        }
                    }
                }
            } catch (Exception e) {
                Log.e("ResourceFileDemo", e.getMessage(), e);
            }
            displayView.setText(msg);
        }
    };

    View.OnClickListener clearButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            displayView.setText("");
        }
    };
}