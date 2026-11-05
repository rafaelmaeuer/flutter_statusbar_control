import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:status_bar_control/status_bar_control.dart';

void main() => runApp(const StatusBarControlApp());

class StatusBarControlApp extends StatefulWidget {
  const StatusBarControlApp({super.key});

  factory StatusBarControlApp.forDesignTime() {
    return const StatusBarControlApp();
  }

  @override
  State<StatusBarControlApp> createState() => _StatusBarControlAppState();
}

class _StatusBarControlAppState extends State<StatusBarControlApp> {
  double? _statusBarHeight = 0.0;
  bool _statusBarColorAnimated = false;
  Color? _statusBarColor = Colors.black;
  double _statusBarOpacity = 1.0;
  bool _statusBarHidden = false;
  StatusBarAnimation _statusBarAnimation = StatusBarAnimation.NONE;
  StatusBarStyle _statusBarStyle = StatusBarStyle.DEFAULT;
  bool _statusBarTranslucent = false;
  bool _loadingIndicator = false;
  bool _fullscreenMode = false;

  bool _navBarColorAnimated = false;
  Color? _navBarColor = Colors.black;
  NavigationBarStyle? _navBarStyle = NavigationBarStyle.DEFAULT;

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    double? statusBarHeight;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      statusBarHeight = await StatusBarControl.getHeight;
    } on PlatformException {
      statusBarHeight = 0.0;
    }
    if (!mounted) return;

    setState(() {
      _statusBarHeight = statusBarHeight;
    });
  }

  Widget renderTitle(String text) {
    const textStyle = TextStyle(fontSize: 18.0, fontWeight: FontWeight.bold);
    return Text(text, style: textStyle);
  }

  Widget renderRadioGroup<T>(
      T? groupValue, Map<T, String> options, ValueChanged<T> onChanged) {
    return RadioGroup<T>(
      groupValue: groupValue,
      onChanged: (T? v) => onChanged(v as T),
      child: Column(
        children: options.entries
            .map((option) => RadioListTile<T>(
                value: option.key, title: Text(option.value), dense: true))
            .toList(),
      ),
    );
  }

  void colorBarChanged(Color val) {
    setState(() {
      _statusBarColor = val;
    });
    updateStatusBar();
  }

  void updateStatusBar() {
    StatusBarControl.setColor(
        _statusBarColor!.withValues(alpha: _statusBarOpacity),
        animated: _statusBarColorAnimated);
  }

  void statusBarAnimationChanged(StatusBarAnimation val) {
    setState(() {
      _statusBarAnimation = val;
    });
  }

  void statusBarStyleChanged(StatusBarStyle val) {
    setState(() {
      _statusBarStyle = val;
    });
    StatusBarControl.setStyle(val);
  }

  void colorNavBarChanged(Color val) {
    setState(() {
      _navBarColor = val;
    });
    updateNavBar();
  }

  void updateNavBar() {
    StatusBarControl.setNavigationBarColor(_navBarColor!,
        animated: _navBarColorAnimated);
  }

  void navigationBarStyleChanged(NavigationBarStyle val) {
    setState(() {
      _navBarStyle = val;
    });
    StatusBarControl.setNavigationBarStyle(val);
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
        home: Scaffold(
      appBar: AppBar(
        title: const Text('Statusbar Control Example'),
      ),
      body: SafeArea(
        child: Scrollbar(
          child: ListView(
            padding:
                const EdgeInsets.symmetric(vertical: 15.0, horizontal: 20.0),
            children: <Widget>[
              renderTitle("Status Bar Height: $_statusBarHeight"),
              const Divider(height: 25.0),
              renderTitle("Status Bar Color (Android):"),
              SwitchListTile(
                value: _statusBarColorAnimated,
                title: const Text("Animated:"),
                onChanged: (bool value) {
                  setState(() {
                    _statusBarColorAnimated = value;
                  });
                },
              ),
              const Text("Color:"),
              renderRadioGroup(_statusBarColor, {
                Colors.black: "Black",
                Colors.orange: "Orange",
                Colors.greenAccent: "Green",
                Colors.white30: "White",
              }, colorBarChanged),
              const Text("Opacity:"),
              Slider(
                value: _statusBarOpacity,
                min: 0.0,
                max: 1.0,
                onChanged: (double val) {
                  setState(() {
                    _statusBarOpacity = val;
                  });
                  updateStatusBar();
                },
              ),
              const Divider(height: 25.0),
              renderTitle("Status Bar Hidden:"),
              SwitchListTile(
                title: const Text("Hidden:"),
                value: _statusBarHidden,
                onChanged: (bool val) {
                  setState(() {
                    _statusBarHidden = val;
                  });
                  StatusBarControl.setHidden(_statusBarHidden,
                      animation: _statusBarAnimation);
                },
              ),
              const Text("Animation (iOS):"),
              renderRadioGroup(_statusBarAnimation, {
                StatusBarAnimation.NONE: "NONE",
                StatusBarAnimation.FADE: "FADE",
                StatusBarAnimation.SLIDE: "SLIDE",
              }, statusBarAnimationChanged),
              const Divider(height: 25.0),
              renderTitle("Status Bar Style:"),
              renderRadioGroup(_statusBarStyle, {
                StatusBarStyle.DEFAULT: "DEFAULT",
                StatusBarStyle.LIGHT_CONTENT: "LIGHT_CONTENT",
                StatusBarStyle.DARK_CONTENT: "DARK_CONTENT",
              }, statusBarStyleChanged),
              const Divider(height: 25.0),
              renderTitle("Status Bar Translucent (Android):"),
              SwitchListTile(
                title: const Text("Translucent:"),
                value: _statusBarTranslucent,
                onChanged: (bool val) {
                  setState(() {
                    _statusBarTranslucent = val;
                  });
                  StatusBarControl.setTranslucent(_statusBarTranslucent);
                },
              ),
              const Divider(height: 25.0),
              renderTitle("Status Bar Activity Indicator (iOS):"),
              SwitchListTile(
                title: const Text("Indicator:"),
                value: _loadingIndicator,
                onChanged: (bool val) {
                  setState(() {
                    _loadingIndicator = val;
                  });
                  StatusBarControl.setNetworkActivityIndicatorVisible(
                      _loadingIndicator);
                },
              ),
              const Divider(height: 25.0),
              renderTitle("Navigation Bar Color (Android):"),
              SwitchListTile(
                value: _navBarColorAnimated,
                title: const Text("Animated:"),
                onChanged: (bool value) {
                  setState(() {
                    _navBarColorAnimated = value;
                  });
                },
              ),
              const Text("Color:"),
              renderRadioGroup(_navBarColor, {
                Colors.black: "Black",
                Colors.orange: "Orange",
                Colors.greenAccent: "Green",
                Colors.white12: "white",
              }, colorNavBarChanged),
              const Divider(height: 25.0),
              renderTitle("Navigation Bar Style (Android):"),
              renderRadioGroup(_navBarStyle, {
                NavigationBarStyle.DEFAULT: "DEFAULT",
                NavigationBarStyle.LIGHT: "LIGHT",
                NavigationBarStyle.DARK: "DARK",
              }, navigationBarStyleChanged),
              const Divider(height: 25.0),
              renderTitle("Fullscreen mode:"),
              SwitchListTile(
                title: const Text("Fullscreen:"),
                value: _fullscreenMode,
                onChanged: (bool val) {
                  setState(() {
                    _fullscreenMode = val;
                  });
                  StatusBarControl.setFullscreen(_fullscreenMode);
                },
              ),
            ],
          ),
        ),
      ),
    ));
  }
}
