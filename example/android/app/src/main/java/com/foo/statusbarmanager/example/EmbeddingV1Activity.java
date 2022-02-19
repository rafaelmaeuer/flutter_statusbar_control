package com.foo.statusbarmanager.example;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import com.foo.statusbarmanager.StatusBarManagerPlugin;

public class EmbeddingV1Activity extends FlutterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarManagerPlugin.registerWith(registrarFor("com.foo.statusbarmanager.StatusBarManagerPlugin"));
    }
}