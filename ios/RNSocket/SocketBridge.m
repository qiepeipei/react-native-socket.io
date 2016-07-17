//
//  SocketBridge.m
//  
//
//  Created by qiepeipei on 16/7/15.
//
//

#import "RCTBridge.h"

@interface RCT_EXTERN_MODULE(SocketIO, NSObject)

RCT_EXTERN_METHOD(AllOff)
RCT_EXTERN_METHOD(off:(NSString*)event)
RCT_EXTERN_METHOD(initialise:(NSString*)connection config:(NSDictionary*)config)
RCT_EXTERN_METHOD(addHandlers:(NSDictionary*)handlers)
RCT_EXTERN_METHOD(connect)
RCT_EXTERN_METHOD(close)
RCT_EXTERN_METHOD(reconnect)
RCT_EXTERN_METHOD(emit:(NSString*)event items:(id)items)
RCT_EXTERN_METHOD(joinNamespace:(NSString*)nameSpace)
RCT_EXTERN_METHOD(leaveNamespace)

@end
