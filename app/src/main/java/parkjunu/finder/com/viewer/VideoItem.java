package parkjunu.finder.com.viewer;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.IBinder;
import android.widget.ImageView;
import android.widget.LinearLayout;

/**
 * Created by pkjoh on 2017-01-13.
 */
public class VideoItem  {
    private String title;
    private String uploader;
    private String length;
    private String link;
    private Drawable bitmapThumbnail;
    private String VideoId;

    VideoItem(String title, String uploader, String length, Drawable thumbnail){
        this.title = title;
        this.uploader = uploader;
        this.length = length;
        this.bitmapThumbnail = thumbnail;
    }

    VideoItem(String title, String uploader, String length, String link){
        this.title = title;
        this.uploader = uploader;
        this.length = length;
        this.link = link;
    }

    VideoItem(String title, String uploader, String length, String link, Drawable thumbnail){
        this.title = title;
        this.uploader = uploader;
        this.length = length;
        this.link = link;
        this.bitmapThumbnail = thumbnail;
    }



    public String getTitle(){return title; }

    public String getUploader(){return uploader;}

    public String getLength(){return length;}

    public String getLink(){return link;}

    public Drawable getBitmapThumbnail(){return bitmapThumbnail;}

    public String getVideoId(){return VideoId;}

}
