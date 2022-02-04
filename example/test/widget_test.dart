// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/services.dart';

import 'package:statusbar_manager_example/main.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  testWidgets('test flutter_statusbar_manager example',
      (WidgetTester tester) async {
    // Build the app and trigger a frame.
    await tester.pumpWidget(StatusBarManager());

    // Verify that status bar heigth is not 0
    expect(find.text('Status Bar Height: 0'), findsNothing);

    // Prepare log to read results from
    final List<MethodCall> log = <MethodCall>[];
    TestDefaultBinaryMessengerBinding.instance!.defaultBinaryMessenger
        .setMockMethodCallHandler(SystemChannels.platform,
            (MethodCall methodCall) async {
      log.add(methodCall);
    });

    // The first call is a cache miss and will queue a microtask
    SystemChrome.setApplicationSwitcherDescription(
        const ApplicationSwitcherDescription(
      label: '',
      primaryColor: 4278190080, // set status bar color to black
    ));
    expect(tester.binding.microtaskCount, equals(1));

    // Flush all microtasks
    await tester.idle();
    print(log);

    // Verify result lenght and content
    expect(log, hasLength(1));
    expect(
        log.single,
        isMethodCall(
          'SystemChrome.setApplicationSwitcherDescription',
          arguments: <String, dynamic>{
            'label': '',
            'primaryColor': 4278190080, // check status bar color is black
          },
        ));

    // Clear Log
    log.clear();
    expect(tester.binding.microtaskCount, equals(0));
    expect(log.isEmpty, isTrue);

    // Tap the 'hidden' toggle and trigger a frame.
    await tester.tap(find.widgetWithText(SwitchListTile, "Hidden:"));
    await tester.pump();

    // Flush all microtasks
    await tester.idle();
    print(log);

    // Verify result lenght and content
    expect(log, hasLength(2));
    expect(
        log.last,
        isMethodCall(
          'SystemChrome.setApplicationSwitcherDescription',
          arguments: <String, dynamic>{
            'label': '',
            'primaryColor': 4280391411, // check status bar color is bg blue
          },
        ));

    // Clear Log
    log.clear();
    expect(tester.binding.microtaskCount, equals(0));
    expect(log.isEmpty, isTrue);
  });
}
