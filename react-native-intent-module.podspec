Pod::Spec.new do |s|
  s.name         = "react-native-intent-module"
  s.version      = "1.0.0"
  s.summary      = "react-native-intent-module for react-native"

  s.homepage     = "https://github.com/"

  s.license      = "MIT"
  s.authors      = { "lj" => "lj@strongsoft.net" }
  s.platform     = :ios, "7.0"

  s.source       = { :git => "https://github.com/" }

  s.source_files  = "ios/**/*.{h,m}"

  s.dependency 'React'
end
