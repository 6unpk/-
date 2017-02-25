package parkjunu.finder.com.viewer;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfObject;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfImageObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class BookMarkList extends AppCompatActivity {
    ArrayList<String> fName = new ArrayList<>();
    ArrayList<CardViewItem> cardViewItems = new ArrayList<>();
    RecyclerViewAdapter rvAdapter;
    RecyclerView recyclerView;
    RecyclerViewAdapter.MyClickListener listener;
    GettingTask gettingTask;

    CharSequence[] items ={"파일 열기", "파일 삭제"};
    File files;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_mark_list);

        File temp = new File(getExternalFilesDir(null) +"/temp/");
        if (!temp.exists())
            temp.mkdir();
        recyclerView = (RecyclerView)findViewById(R.id.recycler_view_bookmark);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        rvAdapter = new RecyclerViewAdapter(cardViewItems);
        recyclerView.setAdapter(rvAdapter);

        gettingTask = new GettingTask();
        gettingTask.execute();

        listener = new RecyclerViewAdapter.MyClickListener() {
            @Override
            public void onItemClick(final int position, View v) {
                new AlertDialog.Builder(BookMarkList.this)
                        .setTitle(((TextView)(v.findViewById(R.id.sub))).getText())
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        Intent pdf = new Intent(getApplicationContext(), PDFviewer.class);
                                        pdf.putExtra("fileURL",getExternalFilesDir(null) +"/downloads/"+fName.get(position));
                                        pdf.putExtra("fileName",fName.get(position));
                                        startActivityForResult(pdf,100);
                                        break;
                                    case 1:
                                        File file = new File(getExternalFilesDir(null) +"/downloads/"+fName.get(position));
                                        Log.d("tag",fName.get(position));
                                        file.delete();
                                        fName.remove(position);
                                        cardViewItems.remove(position);
                                        rvAdapter.notifyDataSetChanged();
                                        break;
                                }
                            }
                        })
                        .show();
            }
        };
        rvAdapter.setOnItemClickListener(listener);



    }

    private void addCards(){
        files = new File(getExternalFilesDir(null) +"/downloads/");
        if(files.listFiles() != null) {
            if (files.listFiles().length > 0)
                for (int i = 0; i < files.listFiles().length; ++i) {
                    File file = files.listFiles()[i];
                    String imagePath = getExternalFilesDir(null) +"/temp/image"+i+".jpg";

                    try{
                        Log.d("tag", file.getPath());
                        ParsePDF(file.getPath(), imagePath);
                    }catch (Exception e){
                        Log.d("tag","error occur");}
                    try {
                        Image image =Image.getInstance(imagePath);
                        float fuck =image.getHeight();
                        Log.d("tag","사이즈:"+fuck);
                        cardViewItems.add(new CardViewItem(new BitmapDrawable(imagePath), file.getName()));
                        fName.add(file.getName());
                    }catch (Exception e){
                        Log.d("tag","비정상적인 이미지");
                        cardViewItems.add(new CardViewItem(MainActivity.getRandomThumb(), file.getName()));
                        fName.add(file.getName());
                    }

                    File temp = new File(getExternalFilesDir(null) +"/temp/");
                    for(File f: temp.listFiles())f.delete();
                }
        }
    }

    public void ParsePDF(String url, String dest) throws IOException {

        PdfReader reader = new PdfReader(url);
        PdfObject object;
        for(int i = 1; i <= reader.getXrefSize(); ++i){
            object = reader.getPdfObject(i);
            if (object != null && object.isStream()) {
                PRStream stream = (PRStream)object;
                PdfImageObject imageObject = new PdfImageObject(stream);
                byte[] b;
                try {
                    b = imageObject.getImageAsBytes();
                }
                catch(Exception e) {
                    b = imageObject.getImageAsBytes();
                }

                FileOutputStream fos = new FileOutputStream(String.format(dest, i));
                fos.write(b);
                fos.flush();
                fos.close();
            }
        }

    }

    private class GettingTask extends AsyncTask<Void, Void, Void> {
        public GettingTask(){}
        ProgressDialog dialog;
        @Override
        public void onPreExecute(){
            dialog = new ProgressDialog(BookMarkList.this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.progressbar_loading));
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();
            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            addCards();
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            rvAdapter.notifyDataSetChanged();
            dialog.dismiss();
            super.onPostExecute(result);
        }

    }

}
