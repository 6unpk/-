package parkjunu.finder.com.viewer;

import android.graphics.drawable.Drawable;


/**
 * Created by Home on 2016-02-13.
 */

public class CardViewItem {
    private Drawable drawable;
    private String sub;

    CardViewItem(Drawable image, String sub){
        drawable = image;
        this.sub =sub;
    }

    public Drawable getgrade() {
        return drawable;
    }
    public String getsub() {
        return sub;
    }


}