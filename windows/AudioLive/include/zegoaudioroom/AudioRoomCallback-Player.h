//
//  AudioRoomCallback-Player.h
//  zegoaudioroom
//
//  Copyright © 2017年 ZEGO. All rights reserved.
//

#ifndef AudioRoomCallback_Player_h
#define AudioRoomCallback_Player_h

#include "./AudioRoomDefines.h"

namespace ZEGO
{
    namespace AUDIOROOM
    {
        class IAudioLivePlayerCallback
        {
        public:
            /**
             播放状态更新
             
             @param stateCode 状态码
             @param pStreamInfo 流信息
             */
            virtual void OnPlayStateUpdate(int stateCode, const ZegoStreamInfo& pStreamInfo) = 0;
            
            virtual ~IAudioLivePlayerCallback() {}
        };
    }
}

#endif /* AudioRoomCallback_Player_h */
