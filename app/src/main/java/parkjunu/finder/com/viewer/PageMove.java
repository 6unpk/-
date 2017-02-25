package parkjunu.finder.com.viewer;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class PageMove extends AppCompatActivity {
    EditText editMove;

    Button okayButton;
    Button cancelButton;

    public static final int RES_CODE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_move);
        editMove = (EditText)findViewById(R.id.edit_page_move);
        okayButton = (Button)findViewById(R.id.okay);
        cancelButton = (Button)findViewById(R.id.cancel);

        okayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editMove.getText().toString().length() == 0)
                    return;
                int page = Integer.parseInt(editMove.getText().toString());

                setResult(RES_CODE, new Intent().putExtra("result", page));

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
