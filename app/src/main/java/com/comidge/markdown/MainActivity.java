package com.comidge.markdown;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MarkDownView markDownView = (MarkDownView) findViewById(R.id.markdown);
        markDownView.setMarkDownText(getString(R.string.example));
        markDownView.setOnImage(new MarkDownView.OnImage() {
            @Override
            public void onImage(ImageView imageView, String url) {

            }
        });

        TextView originalView = (TextView) findViewById(R.id.original);
        originalView.setText(getString(R.string.example));

        findViewById(R.id.btn_original).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.markdown).setVisibility(View.GONE);
                findViewById(R.id.original).setVisibility(View.VISIBLE);
            }
        });

        findViewById(R.id.btn_markdown).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.markdown).setVisibility(View.VISIBLE);
                findViewById(R.id.original).setVisibility(View.GONE);
            }
        });
    }
}
