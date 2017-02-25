package parkjunu.finder.com.viewer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class TagAdder extends AppCompatActivity {
    EditText tagEdit;
    Button okayButton;
    Button cancelButton;

    public static final int RES_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_adder);
        tagEdit = (EditText)findViewById(R.id.edit_tag);
        okayButton = (Button)findViewById(R.id.okay);
        cancelButton = (Button)findViewById(R.id.cancel);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tag = tagEdit.getText().toString();
                if(tag.equals(""))
                    return;
                setResult(RES_CODE, new Intent().putExtra("result", tag));

                finish();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
