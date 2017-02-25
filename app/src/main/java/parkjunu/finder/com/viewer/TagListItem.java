package parkjunu.finder.com.viewer;


public class TagListItem {
    private String tagTitle;
    private boolean isSelected;

    public TagListItem(String tagTitle){
        this.tagTitle = tagTitle;
        isSelected = false;
    }

    public String getTagTitle(){return tagTitle;}

    public boolean getIsSelected(){return isSelected;}

    public void setSelected(boolean state){isSelected = state;}
}
