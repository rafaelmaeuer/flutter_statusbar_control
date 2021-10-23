package com.foo.flutterstatusbarmanager.example;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import com.foo.flutterstatusbarmanager.FlutterStatusbarManagerPlugin;

public class EmbeddingV1Activity extends FlutterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FlutterStatusbarManagerPlugin.registerWith(registrarFor("com.foo.flutterstatusbarmanager.FlutterStatusbarManagerPlugin"));
    }
}