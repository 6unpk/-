package parkjunu.finder.com.viewer;

import android.content.res.AssetManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

public class OpenSourceLicense extends AppCompatActivity {
    TextView openSource;
    InputStream inputStream;
    String s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_license);
        openSource = (TextView)findViewById(R.id.open_source_text);
        try {

            inputStream = getResources().getAssets().open("OpenSource_License.txt");
            int size = inputStream.available();

            byte[] b= new byte[size];
            inputStream.read(b);
            s = new String(b,"euc-kr");
            inputStream.close();
            openSource.setText(s);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("tag","error occur while reading open source");
        }
    }
}
