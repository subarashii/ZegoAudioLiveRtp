#include "ZegoAudioDemo.h"

QZegoAudioDemoApp::QZegoAudioDemoApp()
{
	
}

//ȫ��Ψһ��base����
QZegoAudioDemoApp theApp;

QZegoBase& QZegoAudioDemoApp::GetBase()
{
	return m_base;
}

QZegoSDKSignal * GetAVSignal()
{
	return theApp.GetBase().GetAVSignal();
}