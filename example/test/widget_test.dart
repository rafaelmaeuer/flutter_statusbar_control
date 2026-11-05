// This is a basic Flutter widget test.
//
// To perform an interaction with a widget in your test, use the WidgetTester
// utility that Flutter provides. For example, you can send tap and scroll
// gestures. You can also use WidgetTester to find child widgets in the widget
// tree, read text, and verify that the values of widget properties are correct.

import 'package:flutter/material.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:flutter/services.dart';

import 'package:status_bar_control/status_bar_control.dart';
import 'package:status_bar_control_example/main.dart';

void main() {
  TestWidgetsFlutterBinding.ensureInitialized();

  const channel = MethodChannel('status_bar_control');
  final List<MethodCall> log = <MethodCall>[];

  setUp(() {
    // Mock the native side of the plugin and log all method calls
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, (MethodCall methodCall) async {
      log.add(methodCall);
      return methodCall.method == 'getHeight' ? 24.0 : true;
    });
  });

  tearDown(() {
    TestDefaultBinaryMessengerBinding.instance.defaultBinaryMessenger
        .setMockMethodCallHandler(channel, null);
    log.clear();
  });

  testWidgets('test status_bar_control example', (WidgetTester tester) async {
    // Build the app and trigger a frame.
    await tester.pumpWidget(const StatusBarControlApp());
    await tester.pumpAndSettle();

    // Verify that status bar height is read from the platform
    expect(log, contains(isMethodCall('getHeight', arguments: null)));
    expect(find.text('Status Bar Height: 24.0'), findsOneWidget);
    log.clear();

    // Select orange status bar color
    await tester.tap(find.widgetWithText(RadioListTile<Color>, 'Orange').first);
    await tester.pumpAndSettle();
    expect(
        log.single,
        isMethodCall('setColor', arguments: <String, dynamic>{
          'color': Colors.orange.toARGB32(),
          'animated': false,
        }));
    log.clear();

    // Toggle the 'hidden' switch
    final hiddenSwitch = find.widgetWithText(SwitchListTile, 'Hidden:');
    await tester.scrollUntilVisible(hiddenSwitch, 100.0);
    await tester.tap(hiddenSwitch);
    await tester.pumpAndSettle();
    expect(
        log.single,
        isMethodCall('setHidden', arguments: <String, dynamic>{
          'hidden': true,
          'animation': 'none',
        }));
    log.clear();

    // Select dark content status bar style
    final darkContent =
        find.widgetWithText(RadioListTile<StatusBarStyle>, 'DARK_CONTENT');
    await tester.scrollUntilVisible(darkContent, 100.0);
    await tester.tap(darkContent);
    await tester.pumpAndSettle();
    expect(
        log.single,
        isMethodCall('setStyle', arguments: <String, dynamic>{
          'style': 'dark-content',
        }));
  });
}
