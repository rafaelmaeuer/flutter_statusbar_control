#
# To learn more about a Podspec see http://guides.cocoapods.org/syntax/podspec.html
#
Pod::Spec.new do |s|
  s.name             = 'status_bar_control'
  s.version          = '3.2.1'
  s.summary          = 'Status Bar Control'
  s.description      = <<-DESC
Status Bar Control
                       DESC
  s.homepage         = 'https://github.com/rafaelmaeuer/flutter_statusbar_control'
  s.license          = { :file => '../LICENSE' }
  s.author           = { 'Rafael M.' => 'rafaelmaeuer@gmail.com' }
  s.source           = { :path => '.' }
  s.source_files = 'Classes/**/*'
  s.public_header_files = 'Classes/**/*.h'
  s.dependency 'Flutter'
  
  s.ios.deployment_target = '9.0'
end

