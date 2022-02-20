package com.foo.statusbarcontrol.example;

import android.os.Bundle;
import io.flutter.app.FlutterActivity;
import com.foo.statusbarcontrol.StatusBarControlPlugin;

public class EmbeddingV1Activity extends FlutterActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarControlPlugin.registerWith(registrarFor("com.foo.statusbarcontrol.StatusBarControlPlugin"));
    }
}