package parkjunu.finder.com.viewer;

import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;


import com.melnykov.fab.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class TagListActivity extends AppCompatActivity {

    ListView listView;
    TagListViewAdapter tagListViewAdapter;
    List<TagListItem> tagListItemList;
    AbsListView.MultiChoiceModeListener multiChoiceModeListener;

    FloatingActionButton fab;

    DbHelper dbHelper;
    Cursor cursor;

    public static final int RES_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_list);

        listView= (ListView)findViewById(R.id.tag_list_listView);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE_MODAL);
        tagListItemList = new ArrayList<>();
        tagListViewAdapter = new TagListViewAdapter(this, R.layout.tag_list_view_adpater, tagListItemList);
        initMultiChoiceModeListener();
        listView.setAdapter(tagListViewAdapter);
        listView.setMultiChoiceModeListener(multiChoiceModeListener);
        getDatabaseTagList();

        dbHelper = new DbHelper(getApplicationContext(), DataBases.CreateTagDB.FILENAME);

        fab = (FloatingActionButton)findViewById(R.id.fab_tag_list);

        // 태그 리스트뷰 클릭 리스너
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                for (int i = 0; i < tagListViewAdapter.getCount(); ++i)
                    tagListViewAdapter.getItem(i).setSelected(false);

                listView.setItemChecked(position, true);
            }
        });

        // 태그 리스트뷰 스크롤 리스너
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == SCROLL_STATE_IDLE)
                    fab.hide();
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                fab.show();
            }
        });


        // 플로팅 액션 버튼
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), TagAdder.class), 1);
            }
        });


    }


    public void getDatabaseTagList(){
        try {
            cursor = DbHelper.rawQuery("select distinct tag from tagList");
            if (cursor == null){
                Log.d("tag", "cursor is null");
                return;
            }
            tagListViewAdapter.clear();
            for (int i = 0; i < cursor.getCount(); ++i){
                cursor.moveToNext();
                String tag = cursor.getString(0);
                tagListItemList.add(new TagListItem(tag));
            }
            tagListViewAdapter.notifyDataSetChanged();
        }catch (Exception e){
            e.printStackTrace();
        }
    }


    public void initMultiChoiceModeListener(){
        multiChoiceModeListener = new AbsListView.MultiChoiceModeListener() {

            @Override
            public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                tagListViewAdapter.toggleItem(position);
                int count = tagListViewAdapter.getCheckedCount();
                String title = getResources().getString(R.string.selected);
                mode.setTitle(Integer.toString(count) + " " + title);
                if(tagListViewAdapter.getCheckedCount() == 1){
                    mode.getMenu().findItem(R.id.revise).setEnabled(true);
                    mode.getMenu().findItem(R.id.revise).setVisible(true);
                    mode.getMenu().findItem(R.id.revise).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
                }
                else{
                    mode.getMenu().findItem(R.id.revise).setEnabled(false);
                    mode.getMenu().findItem(R.id.revise).setVisible(false);
                    mode.getMenu().findItem(R.id.revise).setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
                }

            }

            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                getMenuInflater().inflate(R.menu.tag_list_menu_single_choice, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return true;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                switch (item.getItemId()){
                    case R.id.delete:
                        for(int i = tagListViewAdapter.getCount() -1; i >= 0; --i){
                            if(!tagListViewAdapter.getItem(i).getIsSelected())
                                continue;
                            TagListItem target = tagListViewAdapter.getItem(i);
                            dbHelper.exeQuery("delete from "+ DataBases.CreateTagDB._TABLENAME +" where tag='"+target.getTagTitle() +"';");
                            tagListItemList.remove(target);
                        }
                        tagListViewAdapter.notifyDataSetChanged();
                        mode.finish();
                        break;
                        // 태그 삭제
                    case R.id.revise:
                        //TODO 태그 수정 구현 + db 수정
                        // 태그 수정
                        break;
                    default:
                        break;
                }
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {}
        };
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.tag_list_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.all_select){
            for (int i = 0; i < tagListViewAdapter.getCount(); ++i) {
                listView.setItemChecked(i, true);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (resultCode){
            case RES_CODE:
                Log.d("tag","TagListActivity called");
                String res = data.getExtras().getString("result");
                dbHelper.exeQuery(" insert into "+ DataBases.CreateTagDB._TABLENAME + "(tag)" + " values"+"('"+res+"');");
                getDatabaseTagList();
                break;
        }
    }
}
