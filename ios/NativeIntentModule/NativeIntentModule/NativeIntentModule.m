//
//  NativeIntentModule.m
//  NativeIntentModule
//
//  Created by linjun on 2017/3/10.
//  Copyright © 2017年 linjun. All rights reserved.
//

#import "NativeIntentModule.h"

@implementation NativeIntentModule

RCT_EXPORT_MODULE(NativeIntentModule)
//RN跳转原生界面
RCT_EXPORT_METHOD(openNativeModule:(NSString *)viewControllerName
                  data:(NSString *)data
                  callback:(RCTResponseSenderBlock)callback
                  errCallback:(RCTResponseSenderBlock)errCallback)
{
    
//    NSLog(@"RN传入原生界面的数据为:%@",data); 
    
//    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
//    [defaults setObject:data forKey: viewControllerName];
//    [defaults synchronize];
    
    //主要这里必须使用主线程发送,不然有可能失效
    dispatch_async(dispatch_get_main_queue(), ^{
        [[NSNotificationCenter defaultCenter]postNotificationName:viewControllerName object:data];
    });
    
    callback(@[[NSNull null], viewControllerName]);
}

- (NSArray<NSString *> *)supportedEvents
{
    return @[@"nativeIntentModule"];
}


RCT_EXPORT_METHOD(nativeEmitterRN:(NSString*)eventName
                                   code:(NSString*)code
                                   result:(NSString*)result){
   
   [self sendEventWithName:@"nativeIntentModule" body:@{
                                                        @"eventName": eventName,
                                                        @"result":result,
                                                        @"code":code,
                                                        }];
}

RCT_EXPORT_METHOD(getStoreForKey:(NSString *)inputValue
                  callback:(RCTResponseSenderBlock)callback
                  errCallback:(RCTResponseSenderBlock)errCallback)
{
    NSString *ret = [[NSUserDefaults standardUserDefaults]objectForKey:inputValue];
//    NSLog(@"ret = %@",ret);
    
    if(!ret){
        ret = @"";
    }
    callback(@[ret]);
}

RCT_EXPORT_METHOD(setStore:(NSString *)key
                  value:(id)inputValue
                  callback:(RCTResponseSenderBlock)callback
                  errCallback:(RCTResponseSenderBlock)errCallback)
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults setObject:inputValue forKey: key];
    
    [defaults synchronize];
    
    callback(@[[NSNull null], @"success"]);
}

RCT_EXPORT_METHOD(removeStoreForKey:(NSString *)key
                  callback:(RCTResponseSenderBlock)callback
                  errCallback:(RCTResponseSenderBlock)errCallback)
{
    NSUserDefaults *defaults = [NSUserDefaults standardUserDefaults];
    [defaults removeObjectForKey: key];
    
    [defaults synchronize];
    
    callback(@[[NSNull null], @"success"]);
}

RCT_EXPORT_METHOD(openScheme:(NSString *)scheme
                  callback:(RCTResponseSenderBlock)callback
                  errCallback:(RCTResponseSenderBlock)errCallback)
{
    scheme=[scheme stringByReplacingOccurrencesOfString:@" " withString:@""];
    UIApplication *application = [UIApplication sharedApplication];
    
    // NSString* encodingString = [scheme stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding];
    NSURL *URL = [NSURL URLWithString:scheme];
    
    @try {
        if ([application respondsToSelector:@selector(openURL:options:completionHandler:)]) {
            [application openURL:URL options:@{}
               completionHandler:^(BOOL success) {
                   NSLog(@"Open %@: %d",scheme,success);
               }];
        } else {
            BOOL success = [application openURL:URL];
            NSLog(@"Open %@: %d",scheme,success);
        }
        callback(@[[NSNull null], @"success"]);
    } @catch (NSException *exception) {
        errCallback(@[[NSNull null], @"error"]);
    }
}
@end
