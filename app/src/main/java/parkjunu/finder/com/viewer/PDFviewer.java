package parkjunu.finder.com.viewer;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.itextpdf.text.Document;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;
import com.melnykov.fab.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import es.voghdev.pdfviewpager.library.PDFViewPager;
import es.voghdev.pdfviewpager.library.adapter.BasePDFPagerAdapter;
import es.voghdev.pdfviewpager.library.adapter.PDFPagerAdapter;

public class PDFviewer extends ActionBarActivity {
    String fileURL;
    String fileName;
    PDFViewPager viewPager;
    FloatingActionButton fab;
    PDFPagerAdapter pdfPagerAdapter;
    PdfReader pdf;
    int wholePage;
    @Override
    protected void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);
        fab = (FloatingActionButton)findViewById(R.id.viewer_fab);
        Intent request = getIntent();
        fileURL = request.getStringExtra("fileURL");
        fileName = request.getStringExtra("fileName");
        viewPager =  (PDFViewPager)findViewById(R.id.view_pager);
        pdfPagerAdapter = new PDFPagerAdapter(this, fileURL);
        viewPager.setAdapter(pdfPagerAdapter);
        try {
            pdf = new PdfReader(fileURL);
            wholePage =pdf.getNumberOfPages();
            pdf.close();
        }catch (Exception e){
            e.printStackTrace();
        }
        getSupportActionBar().setTitle("(1"+"/"+wholePage+")"+fileName);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                getSupportActionBar().setTitle("("+(position+1)+"/"+wholePage+")"+fileName);
                Log.d("tag","nowPage:"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pdf_viewer_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemId = item.getItemId();
        switch (itemId){
            case R.id.find_in_explorer:
                try {
                    Intent other = new Intent(Intent.ACTION_VIEW);
                    other.setType("application/pdf");
                    startActivity(other);
                }catch (Exception e){
                    Log.d("tag","error occur while 0penning pdf with other application");
                    Toast.makeText(getApplicationContext(),"PDF를 열 수 있는 앱이 없습니다.",Toast.LENGTH_SHORT);
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


}
