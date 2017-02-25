package parkjunu.finder.com.viewer;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfWriter;
import com.kyleduo.switchbutton.SwitchButton;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.wang.avi.AVLoadingIndicatorView;

import net.htmlparser.jericho.Element;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import me.gujun.android.taggroup.TagGroup;

public class MainActivity extends AppCompatActivity{

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    int calledCount;

    EditText searchView;
    ListView videoListView;
    ListViewAdapter listViewAdapter;
    ArrayList<VideoItem> items;
    RelativeLayout bottomLoader;
    AVLoadingIndicatorView customProgressbar;

    Toolbar toolbar;
    Drawer drawer;
    AccountHeader accountHeader;

    com.melnykov.fab.FloatingActionButton fab;
    Spinner searchSpinner;
    SwitchButton onlyBest;
    Button refresh;
    Button pageMove;

    TagGroup tagGroup;
    parkjunu.finder.com.viewer.Tag tagList = new parkjunu.finder.com.viewer.Tag(this);

    ConnectivityManager connectivityManager;
    NetworkInfo wifi;
    NetworkInfo mobile;

    ParsingDialog parsingDialog;
    ParsingMore parsingMore;
    InputMethodManager inputMethodManager;

    int listCount = 0;
    int backCount = 0; // 뒤로가기 버튼을 누른 횟수
    boolean userScrolled = false;
    // 느갤 파싱 관련
    int searchType = 0;
    int nowPage = 1;
    String beforeKeyword;
    String beforeUrl;
    // 시발 썸네일
    Drawable drawable0;
    Drawable drawable1;
    Drawable drawable2;
    Drawable drawable3;
    Drawable drawable4;
    Drawable drawable5;
    Drawable drawable6;
    Drawable drawable7;
    Drawable drawable8;
    Drawable drawable9;
    Drawable drawable10;
    Drawable drawable11;
    Drawable drawable12;
    Drawable drawable13;
    Drawable drawable14;
    Drawable drawable15;


    static List<Drawable> drawables = new ArrayList<>();
    // DB 관련

    DbHelper dbOpenHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = preferences.edit();
        calledCount = preferences.getInt("calledCount", 0);
        Log.d("tag", "calledCount:"+calledCount);
        editor.remove("calledCount");
        editor.commit();
        editor.putInt("calledCount",calledCount+1);
        editor.commit();

        searchView = (EditText)findViewById(R.id.search_view);
        videoListView = (ListView) findViewById(R.id.video_list);
        bottomLoader = (RelativeLayout)findViewById(R.id.bottom_loader);
        customProgressbar = (AVLoadingIndicatorView)findViewById(R.id.custom_bottom_progressbar);
        toolbar = (Toolbar)findViewById(R.id.toolbar);
        tagGroup = (TagGroup)findViewById(R.id.tag_group);
        searchSpinner = (Spinner)findViewById(R.id.search_type);
        onlyBest =(SwitchButton) findViewById(R.id.only_best);
        refresh = (Button)findViewById(R.id.refresh_btn);
        pageMove = (Button)findViewById(R.id.page_move);
        initTag();
        initThumbs();
        tagGroup.setTags(getRefreshTag());

        items = new ArrayList<>();
        listViewAdapter = new ListViewAdapter(this,R.layout.video_view_adapter,items);
        videoListView.setAdapter(listViewAdapter);

        fab = (com.melnykov.fab.FloatingActionButton)findViewById(R.id.fab);

        initDrawer();
        drawer.openDrawer();
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(R.drawable.drawer);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawer.openDrawer();
            }
        });

        connectivityManager = (ConnectivityManager)getSystemService(CONNECTIVITY_SERVICE);
        wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

        inputMethodManager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        parsingMore = new ParsingMore();

        //네트워크 체크
        NetworkCheck();

        //비디오 리스트뷰 클릭 리스너
        videoListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                listViewAdapter.getItem(position);
                CharSequence[] sequence = {"게시물 보기", "다운 받기", "너갤 바로가기"};
                new AlertDialog.Builder(MainActivity.this).setTitle(listViewAdapter.getItem(position).getTitle())
                        .setItems(sequence, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which){
                                    case 0:
                                        ParsingContent parsingContent = new ParsingContent(listViewAdapter.getItem(position).getLink());
                                        parsingContent.execute();
                                        break;
                                    case 1:
                                        DownloadContent downloadContent = new DownloadContent(listViewAdapter.getItem(position).getLink(), listViewAdapter.getItem(position).getTitle());
                                        downloadContent.execute();
                                        break;
                                    case 2:
                                        Intent org = new Intent(Intent.ACTION_VIEW, Uri.parse(listViewAdapter.getItem(position).getLink()));
                                        startActivity(org);
                                        break;
                                }
                            }
                        }).show();

            }
        });


        //비디오 리스트뷰 스크롤 리스너
        videoListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    userScrolled = true;

                }
                if(scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            fab.hide();
                        }
                    }, 3000);
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                fab.show();
                if(firstVisibleItem + visibleItemCount == totalItemCount && totalItemCount != 0 && userScrolled){
                    userScrolled = false;
                    getMoreVideo();
                    if (items.size() > 240)
                        for (int i =0 ; i < 120; ++i) items.remove(i);
                    listViewAdapter.notifyDataSetChanged();

                }
            }
        });

        //  FloatingActionButton 리스너
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tagAdder = new Intent(getApplicationContext(), TagAdder.class);
                startActivity(tagAdder);
            }
        });

        // Tag 클릭 리스너
        tagGroup.setOnTagClickListener(new TagGroup.OnTagClickListener() {
            @Override
            public void onTagClick(String tag) {
                searchView.setText(tag);
            }
        });

        // 스피너 리스너
        searchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                searchType = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        //새로 고침 버튼 리스너
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearList();
                nowPage = 1;
                pageMove.setVisibility(View.INVISIBLE);
                listViewAdapter.notifyDataSetChanged();
            }
        });

        // 페이지 이동 버튼 리스너
        pageMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              Intent pageMove = new Intent(getApplicationContext(), PageMove.class);
                startActivityForResult(pageMove, 100);
            }
        });
    }

    @Override
    public void onResume(){
        super.onResume();
        tagGroup.setTags(getRefreshTag());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case 1:
                break;
            case 2:
                clearList();
                nowPage = data.getIntExtra("result",2);
                ParsingPage parsingPage = new ParsingPage();
                parsingPage.execute();
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.search_bar_icon, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem){
        int id = menuItem.getItemId();

        if(id == R.id.search_edit){
            if (NetworkCheck() && !searchView.getText().toString().equals("")){
                parsingDialog = new ParsingDialog(searchView.getText().toString(), searchType, onlyBest.isChecked());
                parsingDialog.execute();
                listViewAdapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(menuItem);
    }

    @Override
    public void onBackPressed(){
        parsingDialog.cancel(true);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent keyEvent){
        if(inputMethodManager.isAcceptingText()){
            if (keyEvent.KEYCODE_SPACE == keyCode && !searchView.getText().toString().equals("")){
                if (NetworkCheck()){
                    parsingDialog = new ParsingDialog(searchView.getText().toString(), searchType, onlyBest.isChecked());
                    parsingDialog.execute();
                    listViewAdapter.notifyDataSetChanged();
                }
            }
        }
        else {
            if (drawer.isDrawerOpen()) {
                drawer.closeDrawer();
                return true;
            }
            if (KeyEvent.KEYCODE_BACK == keyCode && backCount == 0) {
                ++backCount;
                Toast.makeText(getApplicationContext(), R.string.exit, Toast.LENGTH_LONG).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (backCount == 2)
                            finish();
                    }
                }, 1000);
                return true;
            } else if (KeyEvent.KEYCODE_BACK == keyCode && backCount == 1) {
                ++backCount;
                return true;
            }
        }
        backCount = 0;
        return false;
    }

    // 리스트 목록 더 불러오기
    private void getMoreVideo(){
        if (items.size() == 0) {
            Log.d("tag", "No Video to Load");
            return;
        }
        if (parsingMore.getStatus() == AsyncTask.Status.RUNNING) {
            Log.d("tag", "other task is running");
            return;
        }
        bottomLoader.setVisibility(View.VISIBLE);
        customProgressbar.show();
        parsingMore = new ParsingMore();
        parsingMore.execute();

    }

    // 네트워크 체킹
    public boolean NetworkCheck(){
        try {
            wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mobile = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if(!(mobile == null || wifi == null))
                if(wifi.isConnected())
                    return true;
                else if(mobile.isConnected())
                    return true;
                else
                    Toast.makeText(this,R.string.check_network,Toast.LENGTH_SHORT).show();
        }
        catch (Exception e){
            Log.d("tag",""+e);
        }
        return false;
    }

    public List<String> getRefreshTag(){
        tagList.clear();
        Cursor cursor = dbOpenHelper.rawQuery("select distinct tag from tagList;");

        while (cursor.moveToNext()){
            String tag= cursor.getString(0);
            tagList.add(tag);
        }

        return tagList.getAllTags();
    }

    public void initTag(){
        if(dbOpenHelper != null)
            dbOpenHelper.close();
        dbOpenHelper = new DbHelper(getApplicationContext(), DataBases.CreateTagDB.FILENAME);
        dbOpenHelper.open();

        if (calledCount == 0) {
            tagList.add("팬픽");
            tagList.add("느갤 콘테스트");
            tagList.add("핫산");
            tagList.add("재업");
            tagList.add("너의 이름은");
            tagList.add("미츠하");
            tagList.add("타키");
            tagList.add("번역");
            tagList.add("그림");
            tagList.add("소설");
            tagList.add("이토모리");
            for(int i = 0; i < tagList.size(); ++i)
                dbOpenHelper.exeQuery(" insert into "+ DataBases.CreateTagDB._TABLENAME + "(tag)" + "values"+"('"+tagList.getTag(i)+"');" );
        }


    }

    public void initDrawer(){
        PrimaryDrawerItem drawerItem1 = new PrimaryDrawerItem().withName(R.string.like).withIcon(R.drawable.like).withSetSelected(true).withSelectable(true);
        PrimaryDrawerItem drawerItem2 = new PrimaryDrawerItem().withName(R.string.setting).withIcon(R.drawable.setting).withSetSelected(true).withSelectable(true);
        PrimaryDrawerItem drawerItem3 = new PrimaryDrawerItem().withName(R.string.tag).withIcon(R.drawable.tag).withSetSelected(true).withSelectable(true);

        String version = getString(R.string.version);
        accountHeader = new AccountHeaderBuilder().
                withActivity(this).
                withHeaderBackground(R.color.background).
                withHeaderBackground(getRandomThumb()).
                withSelectionFirstLine(version).
                withSelectionSecondLine("만든이: 디시 느갤 6u").
                withTranslucentStatusBar(true).
                build();

        drawer = new DrawerBuilder().
                withActivity(this).
                addDrawerItems(drawerItem1, drawerItem2, drawerItem3).
                withAccountHeader(accountHeader).
                withSliderBackgroundColor(getResources().getColor(R.color.listBackground)).
                withDrawerWidthRes(R.dimen.material_drawer_width).build();

        drawer.setOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
            @Override
            public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                switch (position){
                    case 1:
                        // 북마크
                        startActivity(new Intent(getApplicationContext(), BookMarkList.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    case 2:
                        //설정
                        startActivity(new Intent(getApplicationContext(), SettingActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    case 3:
                        // 태그목록
                        startActivity(new Intent(getApplicationContext(), TagListActivity.class));
                        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });


    }

    // convert url to Bitmap
    public Bitmap urlToBitmap(String targetUrl){
        try {
            URL url = new URL(targetUrl);
            InputStream is = url.openStream();
            Bitmap target = BitmapFactory.decodeStream(is);
            return target;
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public void initThumbs(){
        drawable0 = getResources().getDrawable(R.drawable.df_thumb0);
        drawable1 = getResources().getDrawable(R.drawable.df_thumb1);
        drawable2 = getResources().getDrawable(R.drawable.df_thumb2);
        drawable3 = getResources().getDrawable(R.drawable.df_thumb3);
        drawable4 = getResources().getDrawable(R.drawable.df_thumb4);
        drawable5 = getResources().getDrawable(R.drawable.df_thumb5);
        drawable6 = getResources().getDrawable(R.drawable.df_thumb6);
        drawable7 = getResources().getDrawable(R.drawable.df_thumb7);
        drawable8 = getResources().getDrawable(R.drawable.df_thumb8);
        drawable9 = getResources().getDrawable(R.drawable.df_thumb9);
        drawable10 = getResources().getDrawable(R.drawable.df_thumb10);
        drawable11 = getResources().getDrawable(R.drawable.df_thumb11);
        drawable12 = getResources().getDrawable(R.drawable.df_thumb12);
        drawable13 = getResources().getDrawable(R.drawable.df_thumb13);
        drawable14 = getResources().getDrawable(R.drawable.df_thumb14);
        drawable15 = getResources().getDrawable(R.drawable.df_thumb15);

        drawables.add(drawable0);
        drawables.add(drawable1);
        drawables.add(drawable2);
        drawables.add(drawable3);
        drawables.add(drawable4);
        drawables.add(drawable5);
        drawables.add(drawable6);
        drawables.add(drawable7);
        drawables.add(drawable8);
        drawables.add(drawable9);
        drawables.add(drawable10);
        drawables.add(drawable11);
        drawables.add(drawable12);
        drawables.add(drawable13);
        drawables.add(drawable14);
        drawables.add(drawable15);
    }

    // 썸네일 랜덤으로 가져오기
    public static Drawable getRandomThumb(){
        Random random = new Random();
        return drawables.get(random.nextInt(10));
    }


    // 게시글을 pdf로 다운로드
    private void downloadKiminoContent(String targetUrl, String contentTitle) throws IOException {
        try{
            Document document = Jsoup.connect(targetUrl).get();
            Elements content = document.select(".s_write");
            Elements realContent = content.select("td");
            Elements rrContent = realContent.get(0).children(); // select all Element

            File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +"/downloads/");
            if(!folder.exists())
                folder.mkdir();

            String outFile = getExternalFilesDir(null) +"/downloads/"+contentTitle+".pdf";
            File newFile = new File(outFile);
            newFile.getParentFile().mkdir();

            com.itextpdf.text.Document pdf = new com.itextpdf.text.Document();
            PdfWriter.getInstance(pdf, new FileOutputStream(outFile));

            pdf.open();

            Paragraph paragraph;
            BaseFont basefont = BaseFont.createFont("HYGoThic-Medium", "UniKS-UCS2-H", false);
            Font font = new Font(basefont);
            Image image = null;
            Log.d("tag", targetUrl);
            boolean hasContent,hasImage;
            // 게시글 크롤링 & PDF 변환
            for(int i = 1; i < rrContent.size(); ++i) {
                hasContent = false;
                hasImage = false;
                paragraph = new Paragraph();
                if(rrContent.get(i).hasText()) {
                    // 텍스트 삽입
                    paragraph.add(new Paragraph(rrContent.get(i).text(), font));
                    hasContent = true;
                } else if(rrContent.get(i).select("*").is("br")){
                    // 개행
                    paragraph.add("\n");
                    hasContent = true;
                } else if(rrContent.get(i).select("*").is("img")){
                    // 이미지
                    Log.d("tag","image");
                    // 크기 변환
                    image = Image.getInstance(rrContent.get(i).select("img").attr("src").toString());
                    if(image.getWidth() > pdf.getPageSize().getWidth() - pdf.rightMargin() - pdf.leftMargin()) {
                        float scaler = ((pdf.getPageSize().getWidth() - pdf.leftMargin() - pdf.rightMargin()) / image.getWidth()) * 100;
                        image.scalePercent(scaler);
                    }if(image.getHeight() > pdf.getPageSize().getHeight() - pdf.topMargin() - pdf.bottomMargin()){
                        float scaler = ((pdf.getPageSize().getHeight() - pdf.topMargin() - pdf.bottomMargin()) / image.getHeight()) * 100;
                        image.scalePercent(scaler);
                    }
                    hasImage = true;
                }
                if(hasContent)
                    pdf.add(paragraph);
                if(hasImage)
                    pdf.add(image);

            }
            pdf.close();
            Intent viewer = new Intent(getApplicationContext(), PDFviewer.class);
            viewer.putExtra("fileURL",outFile);
            viewer.putExtra("fileName",contentTitle);
            startActivityForResult(viewer, 100);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    // 게시글 뷰잉
    private void getKiminoContent(String targetUrl) throws IOException{
        try {
            Document document = Jsoup.connect(targetUrl).get();
            Elements content = document.select(".s_write");
            Elements realContent = content.select("td");
            Elements rrContent = realContent.get(0).children(); // select all Element
            Log.d("tag", targetUrl);
            // 짤방 크롤링
            Log.d("tag",content.select(".zzbang_div").attr("src"));

            // 게시글 크롤링
            for(int i = 1; i < rrContent.size(); ++i) {
                if(rrContent.get(i).hasText()) {
                    // 텍스트 삽입
                    Log.d("tag", rrContent.get(i).text());
                } else if(rrContent.get(i).select("*").is("br")){
                    // 개행
                    Log.d("tag"," ");
                } else if(rrContent.get(i).select("*").is("img")){
                    // 이미지
                    Log.d("tag",rrContent.get(i).attr("src"));
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getKimiNoNaWa(String keyword, int type, boolean onlyBest) throws IOException{
      try{
          String targetUrl="";
          switch (type){
              case 0:
                  targetUrl = "http://gall.dcinside.com/board/lists/?id=yourname&s_type=all&s_keyword="+keyword;
                  break;
              case 1:
                  targetUrl = "http://gall.dcinside.com/board/lists/?id=yourname&s_type=search_subject&s_keyword="+keyword;
                  break;
              case 2:
                  targetUrl = "http://gall.dcinside.com/board/lists/?id=yourname&s_type=search_memo&s_keyword="+keyword;
                  break;
              case 3:
                  targetUrl = "http://gall.dcinside.com/board/lists/?id=yourname&s_type=search_name&s_keyword="+keyword;
                  break;
          }
          if (onlyBest)
              targetUrl += "&exception_mode=recommend";

          Document document = Jsoup.connect(targetUrl).get();
          Bitmap src;
          Elements list= document.select(".list_tbody");
          Elements listTitle = list.select(".t_subject");
          Elements listUploader = list.select(".user_nick_nm");
          Elements listDate = list.select(".t_date");
          beforeUrl = targetUrl;
          beforeKeyword = keyword;

          for(int i = 0; i <listTitle.size(); ++i){
              String title = listTitle.get(i).text();
              String uploader = listUploader.get(i).text();
              String date = listDate.get(i).text();
              String link = "http://gall.dcinside.com" + listTitle.get(i).getElementsByTag("a").attr("href");
              items.add(new VideoItem(title, "글쓴이: "+uploader,date,link, getRandomThumb()));
          }
        }catch (Exception e){
            Log.d("tag",""+e);
        }
    }

    private void getMoreKimiNoNaWa() throws IOException{
        Document document =null;
        try{
            String [] arr =  beforeUrl.split("yourname");
            String targetUrl = arr[0] + "yourname"+"&page="+(++nowPage)+arr[1];
            Log.d("tag", targetUrl);
            document = Jsoup.connect(targetUrl).get();
            Elements list= document.select(".list_tbody");
            Elements listTitle = list.select(".t_subject");
            Elements listUploader = list.select(".user_nick_nm");
            Elements listDate = list.select(".t_date");

            for(int i = 0; i <listTitle.size(); ++i){
                String title = listTitle.get(i).text();
                String uploader = listUploader.get(i).text();
                String date = listDate.get(i).text();
                String link = "http://gall.dcinside.com"+listTitle.get(i).getElementsByTag("a").attr("href");
                items.add(new VideoItem(title, "글쓴이: "+uploader,date,link, getRandomThumb()));
            }
        }catch (Exception e){
            Log.d("tag", ""+e);
            if ((""+e).equals("java.lang.IndexOutOfBoundsException: Index: 0, Size: 0")){
                beforeUrl = "http://gall.dcinside.com"+document.select("#dgn_btn_paging").select(".b_next").attr("href");
                String[] temp= beforeUrl.split("&page=1");
                beforeUrl = temp[0] +temp[1];
                nowPage = 0;
            }
        }

    }

    private void getPageKimiNoNaWa() throws IOException{
        Elements list = null;
        try{
            String [] arr =  beforeUrl.split("yourname");
            String targetUrl = arr[0] + "yourname"+"&page="+(++nowPage)+arr[1];
            Log.d("tag", targetUrl);
            Document document = Jsoup.connect(targetUrl).get();
            list= document.select(".list_tbody");
            Elements listTitle = list.select(".t_subject");
            Elements listUploader = list.select(".user_nick_nm");
            Elements listDate = list.select(".t_date");
            //search_pos=-391795

            for(int i = 0; i <listTitle.size(); ++i){
                String title = listTitle.get(i).text();
                String uploader = listUploader.get(i).text();
                String date = listDate.get(i).text();
                String link = "http://gall.dcinside.com"+listTitle.get(i).getElementsByTag("a").attr("href");
                items.add(new VideoItem(title, "글쓴이: "+uploader,date,link, getRandomThumb()));
            }
        }catch (Exception e){
            e.printStackTrace();
            Log.d("tag", ""+e);
            if ((""+e).equals(" java.lang.IndexOutOfBoundsException: Index: 0, Size: 0")){
                nowPage = 0;
                int last = list.select("#dgn_btn_paging").size();
                beforeUrl = "http://gall.dcinside.com"+list.select("#dgn_btn_paging").get(last-1).attr("href");
            }
        }

    }


    public void clearList() {
        items.clear();
        listCount =0;
    }

    private class ParsingDialog extends AsyncTask<Void, Void, Void>{
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        String keyword;
        int type;
        boolean state;

        public ParsingDialog(String s, int t, boolean st){keyword = s; type = t; state = st;}

        @Override
        public void onPreExecute(){
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.progressbar_loading));
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();

            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            try {
                clearList();
                nowPage = 1;
                getKimiNoNaWa(keyword, type, state);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            dialog.dismiss();
            listViewAdapter.notifyDataSetChanged();
            pageMove.setVisibility(View.VISIBLE);
            super.onPostExecute(result);
        }

    }

    private class ParsingMore extends AsyncTask<Void, Void, Void>{
        public ParsingMore(){}
        @Override
        public void onPreExecute(){
            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            try {
                getMoreKimiNoNaWa();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            customProgressbar.hide();
            bottomLoader.setVisibility(View.GONE);
            listViewAdapter.notifyDataSetChanged();
            super.onPostExecute(result);
        }

    }

    private class ParsingContent extends AsyncTask<Void, Void, Void>{
        String targetUrl;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        public ParsingContent(String target){targetUrl = target;}

        @Override
        public void onPreExecute(){
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.progressbar_loading));
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();

            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            try {
                getKiminoContent(targetUrl);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private class DownloadContent extends AsyncTask<Void, Void, Void>{
        String targetUrl;
        String contentTitle;
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        public DownloadContent(String target, String title){targetUrl = target; contentTitle =title;}

        @Override
        public void onPreExecute(){
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.progressbar_loading));
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();

            // 특수문자 제거 로직
            contentTitle = contentTitle.replace("/","_");
            contentTitle = contentTitle.replace("*","_");
            contentTitle = contentTitle.replace("\"\"","_");
            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            try {
                downloadKiminoContent(targetUrl, contentTitle);
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            dialog.dismiss();
            super.onPostExecute(result);
        }
    }

    private class ParsingPage extends AsyncTask<Void, Void, Void>{
        ProgressDialog dialog = new ProgressDialog(MainActivity.this);

        public ParsingPage(){}
        @Override
        public void onPreExecute(){
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setMessage(getResources().getString(R.string.progressbar_loading));
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.show();

            super.onPreExecute();
        }

        @Override
        public Void doInBackground(Void... args){
            try {
                getPageKimiNoNaWa();
            }catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void onPostExecute(Void result){
            listViewAdapter.notifyDataSetChanged();
            dialog.dismiss();
            super.onPostExecute(result);
        }

    }
}
