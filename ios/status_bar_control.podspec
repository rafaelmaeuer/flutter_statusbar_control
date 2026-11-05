#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'status_bar_control'
  s.version          = '4.0.0'
  s.summary          = 'Status Bar Control'
  s.description      = <<-DESC
Status Bar Control
                       DESC
  s.homepage         = 'https://github.com/rafaelmaeuer/flutter_statusbar_control'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Rafael M.' => 'rafaelmaeuer@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'status_bar_control/Sources/status_bar_control/**/*.{h,m}'
  s.public_header_files = 'status_bar_control/Sources/status_bar_control/include/**/*.h'
  s.dependency 'Flutter'
  
  s.ios.deployment_target = '13.0'
end

