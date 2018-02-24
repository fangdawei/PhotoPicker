package me.fdawei.photopicker;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import me.fdawei.picker.PhotoPicker;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    TextView helloTv = findViewById(R.id.hello_world);

    helloTv.setOnClickListener(v -> {
      PhotoPicker.builder().open(this, 2000);
    });
  }
}
