package parkjunu.finder.com.viewer;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.List;


public class Tag {
    private List<String> tagList;
    private SharedPreferences mPref;
    private SharedPreferences.Editor editor;

    public Tag(Context context){
        tagList = new ArrayList<>();
    }

    public Tag(List<String> tagList,Context context){
        this.tagList = tagList;
    }

    public List<String> getAllTags(){
        return tagList;
    }

    public String getTag(int position){
        return tagList.get(position);
    }

    public boolean isExistTag(String s){
        return false;
    }

    public void add(String s){this.tagList.add(s);}

    public int size(){return  tagList.size();}

    public void remove(int position){
        this.tagList.remove(position);
    }

    public void clear(){
        this.tagList.clear();
    }

}
