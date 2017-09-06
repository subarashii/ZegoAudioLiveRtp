#include "ZegoStreamModel.h"

QZegoStreamModel::QZegoStreamModel(const QString &streamId, const QString &userId, const QString &userName, const QString &extraInfo, bool isCurUser) :
m_strStreamId(streamId), m_strUserId(userId), m_strUserName(userName), m_strExtraInfo(extraInfo), m_bCurUserCreated(isCurUser)
{
	m_nPlayViewId = -1;
}

QZegoStreamModel::QZegoStreamModel(const QZegoStreamModel &otherStream)
{
	m_strStreamId = otherStream.m_strStreamId;
	m_strUserId = otherStream.m_strUserId;
	m_strUserName = otherStream.m_strUserName;
	m_strExtraInfo = otherStream.m_strExtraInfo;
	m_nPlayViewId = otherStream.m_nPlayViewId;
	m_bCurUserCreated = otherStream.m_bCurUserCreated;
	m_bPrimary = otherStream.m_bPrimary;
}

QString QZegoStreamModel::getStreamId(void)
{
	return m_strStreamId;
}

QString QZegoStreamModel::getUserId(void)
{
	return m_strUserId;
}

QString QZegoStreamModel::getUserName(void)
{
	return m_strUserName;
}

QString QZegoStreamModel::getExtraInfo(void)
{
	return m_strExtraInfo;
}

void QZegoStreamModel::setPlayView(int viewId)
{
	m_nPlayViewId = viewId;
}

int QZegoStreamModel::getPlayView(void)
{
	return m_nPlayViewId;
}
//m_nPlayView����0�����Ѿ���ֱ��
bool QZegoStreamModel::isPlaying(void)
{
	return m_nPlayViewId >= 0;
}

bool QZegoStreamModel::isCurUserCreated(void)
{
	return m_bCurUserCreated;
}
