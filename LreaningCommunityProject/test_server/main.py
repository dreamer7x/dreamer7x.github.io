from flask import Flask,url_for,redirect,request,send_file, send_from_directory,json, jsonify,make_response
import random
import string
import keeper
import account_tools
import mysql_tools
import re
import datetime
import time

lEmailLoginData = []

app = Flask(__name__)

@app.route('/picture_get',methods = ['GET','POST'])
def picture_get():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 获取图片
            print('获取图片')
            sPicturePosition = request.form.get('sPicturePosition')
            response = make_response(send_from_directory('pictures/',
                                                         sPicturePosition + '.png',
                                                         as_attachment=True))
            return response

@app.route('/clock_in',methods = ['GET','POST']) # 打卡服务 # 已检验
def clock_in():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in list(keeper.DataKeeper.mActivityIdStaticIdList.keys()):
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0':  # 提供权威时间
            print("获取权威时间")
            sDate = datetime.datetime.now().strftime('%Y %m %d')
            sWeek = datetime.datetime.now().strftime('%w')
            if sWeek == '0':
                sWeek = '7'
            sDate = sDate + ' ' + sWeek
            print(sDate)
            return '0' + sDate

        if sServeType == '1':  # 提供打卡服务
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sDate = datetime.datetime.now().strftime('%d')
            if sDate[0] == '0':
                sDate = eval(sDate[1])
            else:
                sDate = eval(sDate)
            lIdentitySign = mysql_tools.get_identity_sign(str(sStaticId))
            sClockIn = lIdentitySign[3]
            if sClockIn[sDate - 1] != 1:
                lClockIn = list(sClockIn)
                lClockIn[sDate - 1] = "1"
                sClockIn = ''.join(lClockIn)
                mysql_tools.update_identity_sign_clock_in(str(sStaticId), sClockIn)
                return '0'
            else:
                return '1'  # 打卡错误: 已打卡

@app.route('/identity_chat',methods = ['GET','POST']) # 获取聊天索引 # 已查验
def identity_chat():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 查看更新信息
            print('identity_chat 查看更新信息')
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityChat = mysql_tools.get_identity_chat(str(sStaticId))

            # 重组更新聊天与历史聊天
            lDrivingIdentityChat = eval(lIdentityChat[5])
            lPassiveIdentityChat = eval(lIdentityChat[6])
            # 获取历史聊天排列顺序信息
            sChatOrder = lIdentityChat[7]
            # 按照历史聊天排列顺序排列聊天信息
            iDrivingOrder = 0
            iPassiveOrder = 0
            sRespond = ''
            for sOrder in sChatOrder:
                if sOrder == '0':
                    sRespond = sRespond + '0 ' + str(lDrivingIdentityChat[iDrivingOrder]) + ' '
                    iDrivingOrder = iDrivingOrder + 1
                if sOrder == '1':
                    sRespond = sRespond + '1 ' + str(lPassiveIdentityChat[iPassiveOrder]) + ' '
                    iPassiveOrder = iPassiveOrder + 1
            sRespond = sRespond[0:-1]
            print(sRespond)
            return '0' + sRespond

@app.route('/chat',methods = ['GET','POST']) # 获取聊天信息
def chat():
    if request.method == 'GET':
        return '暂未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0':  # 查看更新数据 # 已查验
            print("查看更新数据")
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityChat = mysql_tools.get_identity_chat(str(sStaticId))
            sRespond = ''

            lNewDrivingStaticIds = eval(lIdentityChat[1])
            if len(lNewDrivingStaticIds) != 0:
                lNewChats1 = mysql_tools.get_chats1(str(sStaticId), lNewDrivingStaticIds)
                for lNewChat1 in lNewChats1: # 这里sNewChat内的信息可能包含 <spa1> <spa2> 所以使用 <spa>
                    sRespond = sRespond + str(lNewChat1[1]) + '<spa>' \
                               + lNewChat1[2][1:] + '<spa>'

            lNewPassiveStaticIds = eval(lIdentityChat[2])
            if len(lNewPassiveStaticIds) != 0:
                lNewChats2 = mysql_tools.get_chats1(str(sStaticId), lNewPassiveStaticIds)
                for lNewChat2 in lNewChats2:
                    sRespond = sRespond + str(lNewChat2[0]) + '<spa>' \
                               + lNewChat2[2][1:] + '<spa>'

            if sRespond != '':
                sRespond = sRespond[0:-5]
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1': # 获取聊天数据
            print("获取聊天数据")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get('sStaticId'))
            sDrivingPassive = request.form.get('sDrivingPassive')
            lIdentityChat = mysql_tools.get_identity_chat(str(sFromStaticId))
            # 从主动聊天信息进行查找
            if sDrivingPassive == '0':
                print("从主动聊天信息进行查找")
                lNewDrivingChatStaticId = eval(lIdentityChat[1])
                iLength = len(lNewDrivingChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewDrivingChatStaticId[iTimeout] == sToStaticId:
                        print("主动聊天信息出下载更新聊天中,尝试清除更新信息,并返回聊天数据")
                        lChat = mysql_tools.get_chat(str(sFromStaticId),str(sToStaticId))
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sFromStaticId),str(sToStaticId),sOldChat)
                        # 清除更新数据
                        lNewDrivingChatStaticId = lNewDrivingChatStaticId[0:iTimeout] \
                                                  + lNewDrivingChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_driving_chat(str(sFromStaticId),
                                                                          str(lNewDrivingChatStaticId))
                        sRespond = sOldChat
                        print(sRespond)
                        return '0' + sOldChat
                    iTimeout = iTimeout + 1

                lOldDrivingChatStaticId = eval(lIdentityChat[5])
                if sToStaticId in lOldDrivingChatStaticId:
                    lChat = mysql_tools.get_chat(str(sFromStaticId),str(sToStaticId))
                    sNewChat = lChat[2]
                    sOldChat = lChat[3]
                    sRespond = sOldChat + sNewChat[1:]
                    print(sRespond)
                    return '0' + sRespond

                lDrivingChatStaticId = eval(lIdentityChat[3])
                if sToStaticId in lDrivingChatStaticId:
                    lChat = mysql_tools.get_chat(str(sFromStaticId),str(sToStaticId))
                    sNewChat = lChat[2]
                    sOldChat = lChat[3]
                    sRespond = sOldChat + sNewChat[1:]
                    print(sRespond)
                    return '0' + sRespond

                return '0' # 聊天信息为空 可能: 好友未具有 聊天未建立
            # 从被动聊天信息进行查找
            if sDrivingPassive == '1':
                lNewPassiveChatStaticId = eval(lIdentityChat[2])
                iLength = len(lNewPassiveChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewPassiveChatStaticId[iTimeout] == sToStaticId:
                        lChat = mysql_tools.get_chat(sToStaticId,sFromStaticId)
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sToStaticId), str(sFromStaticId), sOldChat)
                        # 清除更新数据
                        lNewPassiveChatStaticId = lNewPassiveChatStaticId[0:iTimeout] \
                                                  + lNewPassiveChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_passive_chat(str(sFromStaticId),
                                                                          str(lNewPassiveChatStaticId))
                        sRespond = sOldChat
                        print(sRespond)
                        return '0' + sOldChat
                    iTimeout = iTimeout + 1

                lOldPassiveChatStaticId = eval(lIdentityChat[5])
                if sToStaticId in lOldPassiveChatStaticId:
                    lChat = mysql_tools.get_chat(sToStaticId, sFromStaticId)
                    sOldChat = lChat[3]
                    sRespond = sOldChat
                    print(sRespond)
                    return '0' + sRespond

                lPassiveChatStaticId = eval(lIdentityChat[3])
                if sToStaticId in lPassiveChatStaticId:
                    lChat = mysql_tools.get_chat(sToStaticId, sFromStaticId)
                    sOldChat = lChat[3]
                    sRespond = sOldChat
                    print(sRespond)
                    return '0' + sRespond

                return '0'  # 聊天信息为空 可能: 好友未具有 聊天未建立
            return '2' # 未提供查找方向参数

        if sServeType == '2': # 发送信息
            print("发送信息")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            sDrivingPassive = request.form.get("sDrivingPassive")
            sSendChat = request.form.get("sSendChat")
            lFromIdentityChat = mysql_tools.get_identity_chat(str(sFromStaticId))
            lToIdentityChat = mysql_tools.get_identity_chat(str(sToStaticId))

            if sDrivingPassive == '0':
                lNewDrivingChatStaticId = eval(lFromIdentityChat[1])
                if sToStaticId in lNewDrivingChatStaticId:
                    print("还未进行更新")
                    return '3' # 还未进行更新

                lNewPassiveChatStaticId = eval(lToIdentityChat[2])
                if sFromStaticId in lNewPassiveChatStaticId:
                    print("继续更新")
                    lChat = mysql_tools.get_chat(str(sFromStaticId), str(sToStaticId))
                    lNewChat = lChat[2]
                    lNewChat = lNewChat + sSendChat + '<spa1>'
                    # 更新聊天数据
                    mysql_tools.update_new_chat(str(sFromStaticId), str(sToStaticId), lNewChat)
                    return '0'

                lOldPassiveChatStaticId = eval(lToIdentityChat[6])
                iLength = len(lOldPassiveChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lOldPassiveChatStaticId[iTimeout] == sFromStaticId:
                        print("发现发送者出现在接收者聊天记录中 尝试添加更新信息")
                        iDrivingTimeout = 0
                        iPassiveTimeout = 0
                        iPosition = 0
                        for sOrder in lToIdentityChat[7]:
                            if sOrder == '0':
                                iDrivingTimeout = iDrivingTimeout + 1
                            if sOrder == '1':
                                iPassiveTimeout = iPassiveTimeout + 1
                            if iTimeout == (iPassiveTimeout - 1):
                                break
                            iPosition = iPosition + 1
                        sChatOrder = lToIdentityChat[7]
                        sChatOrder = '1' + sChatOrder[0:iPosition] \
                                     + sChatOrder[iPosition + 1:]
                        lNewPassiveChatStaticId = [lOldPassiveChatStaticId[iTimeout]] \
                                     + lNewPassiveChatStaticId
                        lOldPassiveChatStaticId = [lOldPassiveChatStaticId[iTimeout]] \
                                     + lOldPassiveChatStaticId[0:iTimeout] \
                                     + lOldPassiveChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_old_passive_chat(str(sToStaticId),
                                                                              str(lNewPassiveChatStaticId),
                                                                              str(lOldPassiveChatStaticId),
                                                                              str(sChatOrder))
                        print("成功执行更新命令 锁住聊天信息")
                        sNewChat = '1' + sSendChat + '<spa1>'
                        mysql_tools.update_new_chat(str(sFromStaticId), str(sToStaticId), sNewChat)
                        return '0'
                    iTimeout = iTimeout + 1

                lPassiveChatStaticId = eval(lToIdentityChat[4])
                iLength = len(lPassiveChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lPassiveChatStaticId[iTimeout] == sFromStaticId:
                        sChatOrder = lToIdentityChat[7]
                        sChatOrder = '1' + sChatOrder
                        lNewPassiveChatStaticId = lToIdentityChat[2]
                        lNewPassiveChatStaticId = [sFromStaticId] + lNewPassiveChatStaticId
                        lOldPassiveChatStaticId = lToIdentityChat[6]
                        lOldPassiveChatStaticId = [sFromStaticId] + lOldPassiveChatStaticId
                        mysql_tools.update_identity_chat_new_old_passive_chat(str(sToStaticId),
                                                                              str(lNewPassiveChatStaticId),
                                                                              str(lOldPassiveChatStaticId),
                                                                              str(sChatOrder))
                        sNewChat = '2' + sSendChat + '<spa2>'
                        mysql_tools.update_new_chat(str(sFromStaticId),str(sToStaticId),sNewChat)
                        return '0'
                    iTimeout = iTimeout + 1

                lFromIdentityFriends = mysql_tools.get_identity_friends(str(sFromStaticId))
                if sToStaticId in lFromIdentityFriends[1]:
                    mysql_tools.insert_chat(sFromStaticId,sToStaticId,sSendChat)
                    sChatOrder = lFromIdentityChat[7]
                    sChatOrder = '0' + sChatOrder
                    lNewDrivingChatStaticId = lFromIdentityChat[1]
                    lNewDrivingChatStaticId = [sToStaticId] + lNewDrivingChatStaticId
                    lDrivingChatStaticId = lFromIdentityChat[3]
                    lDrivingChatStaticId = [sToStaticId] + lDrivingChatStaticId
                    lOldDrivingChatStaticId = lFromIdentityChat[5]
                    lOldDrivingChatStaticId = [sToStaticId] + lOldDrivingChatStaticId
                    mysql_tools.update_identity_chat_driving_chat(str(sFromStaticId),
                                                                  str(lNewDrivingChatStaticId),
                                                                  str(lDrivingChatStaticId),
                                                                  str(lOldDrivingChatStaticId),
                                                                  str(sChatOrder))
                    sChatOrder = lToIdentityChat[7]
                    sChatOrder = '1' + sChatOrder
                    lNewPassiveChatStaticId = lToIdentityChat[2]
                    lNewPassiveChatStaticId = [sFromStaticId] + lNewPassiveChatStaticId
                    lPassiveChatStaticId = lToIdentityChat[4]
                    lPassiveChatStaticId = [sFromStaticId] + lPassiveChatStaticId
                    lOldPassiveChatStaticId = lToIdentityChat[6]
                    lOldPassiveChatStaticId = [sFromStaticId] + lOldPassiveChatStaticId
                    mysql_tools.update_identity_chat_passive_chat(str(sToStaticId),
                                                                  str(lNewPassiveChatStaticId),
                                                                  str(lPassiveChatStaticId),
                                                                  str(lOldPassiveChatStaticId),
                                                                  str(sChatOrder))
                    sNewChat = '1' + sSendChat + '<spa1>'
                    mysql_tools.update_new_chat(str(sFromStaticId),str(sToStaticId),sNewChat)
                    return '0'
                return '3' # 发送目的用户与该用户并无关联 拒绝发送数据

            if sDrivingPassive == '1':
                lNewPassiveChatStaticId = eval(lFromIdentityChat[2])
                if sToStaticId in lNewPassiveChatStaticId:
                    return '3'  # 还未进行更新

                lNewDrivingChatStaticId = eval(lToIdentityChat[1])
                if sFromStaticId in lNewDrivingChatStaticId:
                    lChat = mysql_tools.get_chat(str(sToStaticId), str(sFromStaticId))
                    lNewChat = lChat[2]
                    lNewChat = lNewChat + sSendChat + '<spa2>'
                    # 更新聊天数据
                    mysql_tools.update_new_chat(str(sToStaticId), str(sFromStaticId), lNewChat)
                    return '0'

                lOldDrivingChatStaticId = eval(lToIdentityChat[5])
                iLength = len(lOldDrivingChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lOldDrivingChatStaticId[iTimeout] == sFromStaticId:
                        iDrivingTimeout = 0
                        iPassiveTimeout = 0
                        iPosition = 0
                        for sOrder in lToIdentityChat[7]:
                            if sOrder == '0':
                                iDrivingTimeout = iDrivingTimeout + 1
                            if sOrder == '1':
                                iPassiveTimeout = iPassiveTimeout + 1
                            if iTimeout == (iPassiveTimeout - 1):
                                break
                            iPosition = iPosition + 1
                        sChatOrder = lToIdentityChat[7]
                        sChatOrder = '1' + sChatOrder[0:iPosition] \
                                     + sChatOrder[iPosition + 1:]
                        lNewDrivingChatStaticId = lOldDrivingChatStaticId[iTimeout] \
                                                  + lNewDrivingChatStaticId
                        lOldDrivingChatStaticId = lOldDrivingChatStaticId[iTimeout] \
                                                  + lOldDrivingChatStaticId[0:iTimeout] \
                                                  + lOldDrivingChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_old_passive_chat(str(sToStaticId),
                                                                              str(lNewDrivingChatStaticId),
                                                                              str(lOldDrivingChatStaticId),
                                                                              str(sChatOrder))
                        sNewChat = '2' + sSendChat + '<spa2>'
                        mysql_tools.update_new_chat(str(sFromStaticId), str(sToStaticId), sNewChat)
                        return '0'
                    iTimeout = iTimeout + 1

                lDrivingChatStaticId = eval(lToIdentityChat[3])
                iLength = len(lDrivingChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lDrivingChatStaticId[iTimeout] == sFromStaticId:
                        sChatOrder = lToIdentityChat[7]
                        sChatOrder = '1' + sChatOrder
                        lNewDrivingChatStaticId = lToIdentityChat[2]
                        lNewDrivingChatStaticId = [sFromStaticId] + lNewDrivingChatStaticId
                        lOldDrivingChatStaticId = lToIdentityChat[6]
                        lOldDrivingChatStaticId = [sFromStaticId] + lOldDrivingChatStaticId
                        mysql_tools.update_identity_chat_new_old_passive_chat(str(sToStaticId),
                                                                              str(lNewDrivingChatStaticId),
                                                                              str(lOldDrivingChatStaticId),
                                                                              str(sChatOrder))
                        sNewChat = '2' + sSendChat + '<spa2>'
                        mysql_tools.update_new_chat(str(sFromStaticId), str(sToStaticId), sNewChat)
                        return '0'
                    iTimeout = iTimeout + 1

                lFromIdentityFriends = mysql_tools.get_identity_friends(str(sFromStaticId))
                if sToStaticId in lFromIdentityFriends[1]:
                    mysql_tools.insert_chat(str(sToStaticId), str(sToStaticId), sSendChat)
                    sChatOrder = lFromIdentityChat[7]
                    sChatOrder = '0' + sChatOrder
                    lNewDrivingChatStaticId = lFromIdentityChat[1]
                    lNewDrivingChatStaticId = [sToStaticId] + lNewDrivingChatStaticId
                    lDrivingChatStaticId = lFromIdentityChat[3]
                    lDrivingChatStaticId = [sToStaticId] + lDrivingChatStaticId
                    lOldDrivingChatStaticId = lFromIdentityChat[5]
                    lOldDrivingChatStaticId = [sToStaticId] + lOldDrivingChatStaticId
                    mysql_tools.update_identity_chat_driving_chat(str(sFromStaticId),
                                                                  str(lNewDrivingChatStaticId),
                                                                  str(lDrivingChatStaticId),
                                                                  str(lOldDrivingChatStaticId),
                                                                  str(sChatOrder))
                    sChatOrder = lToIdentityChat[7]
                    sChatOrder = '1' + sChatOrder
                    lNewPassiveChatStaticId = lToIdentityChat[2]
                    lNewPassiveChatStaticId = [sFromStaticId] + lNewPassiveChatStaticId
                    lPassiveChatStaticId = lToIdentityChat[4]
                    lPassiveChatStaticId = [sFromStaticId] + lPassiveChatStaticId
                    lOldPassiveChatStaticId = lToIdentityChat[6]
                    lOldPassiveChatStaticId = [sFromStaticId] + lOldPassiveChatStaticId
                    mysql_tools.update_identity_chat_passive_chat(str(sToStaticId),
                                                                  str(lNewPassiveChatStaticId),
                                                                  str(lPassiveChatStaticId),
                                                                  str(lOldPassiveChatStaticId),
                                                                  str(sChatOrder))
                    sNewChat = '1' + sSendChat + '<spa1>'
                    mysql_tools.update_new_chat(str(sFromStaticId), str(sToStaticId), sNewChat)
                    return '0'
                return '3'  # 发送目的用户与该用户并无关联 拒绝发送数据
            return '2' # 未提供查找方向参数

        if sServeType == '3': # 指定获取更新数据
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            sDrvingPassive = request.form.get("sDrivingPassive")
            lIdentityChat = mysql_tools.get_identity_chat(str(sFromStaticId))

            if sDrvingPassive == '0':
                lNewDrivingChatStaticId = eval(lIdentityChat[1])
                iLength = len(lNewDrivingChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewDrivingChatStaticId[iTimeout] == sToStaticId:
                        lChat = mysql_tools.get_chat(str(sFromStaticId),str(sToStaticId))
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sFromStaticId),str(sToStaticId),sOldChat)
                        # 清除更新数据
                        lNewDrivingChatStaticId = lNewDrivingChatStaticId[0:iTimeout] \
                                                  + lNewDrivingChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_driving_chat(str(sFromStaticId),
                                                                          str(lNewDrivingChatStaticId))
                        sRespond = sNewChat
                        print(sRespond)
                        return '0' + sRespond
                    iTimeout = iTimeout + 1

            if sDrvingPassive == '1':
                lNewPassiveChatStaticId = eval(lIdentityChat[2])
                iLength = len(lNewPassiveChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewPassiveChatStaticId[iTimeout] == sToStaticId:
                        lChat = mysql_tools.get_chat(str(sToStaticId),str(sFromStaticId))
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sToStaticId),str(sFromStaticId),sOldChat)
                        # 清除更新数据
                        lNewPassiveChatStaticId = lNewPassiveChatStaticId[0:iTimeout] \
                                                  + lNewPassiveChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_passive_chat(str(sFromStaticId),
                                                                          str(lNewPassiveChatStaticId))
                        sRespond = sNewChat
                        print(sRespond)
                        return '0' + sRespond
                    iTimeout = iTimeout + 1
            return '0' # 无更新数据

        if sServeType == '4': # 清除更新数据
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            sDrivingPassive = request.form.get("sDrivingPassive")
            lIdentityChat = mysql_tools.get_identity_chat(str(sFromStaticId))

            if sDrivingPassive == '0':
                lNewDrivingChatStaticId = eval(lIdentityChat[1])
                iLength = len(lNewDrivingChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewDrivingChatStaticId[iTimeout] == sToStaticId:
                        lChat = mysql_tools.get_chat(str(sFromStaticId), str(sToStaticId))
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sFromStaticId), str(sToStaticId), sOldChat)
                        # 清除更新数据
                        lNewDrivingChatStaticId = lNewDrivingChatStaticId[0:iTimeout] \
                                                  + lNewDrivingChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_driving_chat(str(sFromStaticId),
                                                                          str(lNewDrivingChatStaticId))
                        return '0'
                    iTimeout = iTimeout + 1
                return '3' # 要求清除更新数据不存在

            if sDrivingPassive == '1':
                lNewPassiveChatStaticId = lIdentityChat[2]
                iLength = len(lNewPassiveChatStaticId)
                iTimeout = 0
                while iTimeout < iLength:
                    if lNewPassiveChatStaticId[iTimeout] == sToStaticId:
                        lChat = mysql_tools.get_chat(str(sFromStaticId), sToStaticId)
                        sNewChat = lChat[2][1:]
                        sOldChat = lChat[3] + sNewChat
                        # 更新聊天数据
                        mysql_tools.update_old_chat(str(sFromStaticId), sToStaticId, sOldChat)
                        # 清除更新数据
                        lNewDrivingChatStaticId = lNewPassiveChatStaticId[0:iTimeout] \
                                                  + lNewPassiveChatStaticId[iTimeout + 1:]
                        mysql_tools.update_identity_chat_new_passive_chat(str(sFromStaticId),
                                                                          str(lNewDrivingChatStaticId))
                        return '0'
                    iTimeout = iTimeout + 1
                return '3'  # 要求清楚的更新数据不存在
            return '2'  # 未提供查找方向参数

@app.route('/identity_sign',methods = ['GET','POST']) # 身份信息服务 # 已查验
def identity_sign():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in list(keeper.DataKeeper.mActivityIdStaticIdList.keys()):
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0':  # 获取个人用户信息
            print('获取个人用户信息')
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sIdentitySign = mysql_tools.get_identity_sign(str(sStaticId))
            if sIdentitySign == None:
                return '2'
            sRespond = '' + sIdentitySign[1] \
                       + ' ' + sIdentitySign[2] \
                       + ' ' + sIdentitySign[3] \
                       + ' ' + sIdentitySign[4] \
                       + ' ' + sIdentitySign[5] \
                       + ' ' + sIdentitySign[6] \
                       + ' ' + str(sIdentitySign[0])
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1':  # 获取一般用户信息
            print('获取一般用户信息')
            sStaticId = request.form.get('sStaticId')
            print(sStaticId)
            if sStaticId[0] == '[':  # 获取多个用户信息
                sStaticIds = eval(sStaticId)
                if sStaticIds == []:
                    return '0'
                lIdentitySigns = mysql_tools.get_many_identity_signs(sStaticIds)
                if lIdentitySigns == ():
                    return '2' # 未找到相关记录
                if str(lIdentitySigns)[1] != '(':
                    sRespond = '' + str(lIdentitySigns[0]) \
                           + ' ' + lIdentitySigns[1] \
                           + ' ' + lIdentitySigns[2] \
                           + ' ' + lIdentitySigns[4]
                    print(sRespond)
                    return '0' + sRespond
                sRespond = '' + str(lIdentitySigns[0][0]) \
                           + ' ' + lIdentitySigns[0][1] \
                           + ' ' + lIdentitySigns[0][2] \
                           + ' ' + lIdentitySigns[0][4]
                for lIdentitySign in lIdentitySigns[1:]:
                    sRespond = sRespond \
                               + ' ' + str(lIdentitySign[0]) \
                               + ' ' + lIdentitySign[1] \
                               + ' ' + lIdentitySign[2] \
                               + ' ' + lIdentitySign[4]
                print(sRespond)
                return '0' + sRespond
            else:
                sIdentitySign = mysql_tools.get_identity_sign(sStaticId)
                if sIdentitySign == None:
                    return '2'
                sRespond = '' + str(sIdentitySign[0]) \
                           + ' ' + sIdentitySign[1] \
                           + ' ' + sIdentitySign[2] \
                           + ' ' + sIdentitySign[4]
                print(sRespond)
                return '0' + sRespond

@app.route('/identity_friends',methods = ['GET','POST']) # 获取朋友圈索引
def identity_friends():
    if request.method == 'GET':
        return '暂未开放'

    if request.method == 'POST':
        sServeType = request.form.get('sServeType')
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'

        if sServeType == '0': # 好友列表
            print("获取好友列表")
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityFriends = mysql_tools.get_identity_friends(str(sStaticId))
            lFriendsStaticId = eval(lIdentityFriends[1])
            lOldNewFriendsStaticId = eval(lIdentityFriends[2])
            lNewFriendsStaticId = eval(lIdentityFriends[3])
            sRespond = ''
            if lNewFriendsStaticId != []:
                for sNewFriendsStaticId in lNewFriendsStaticId:
                    sRespond = sRespond + str(sNewFriendsStaticId) + ' '
            if lOldNewFriendsStaticId != []:
                for sOldNewFriendsStaticId in lOldNewFriendsStaticId:
                    sRespond  = sRespond + str(sOldNewFriendsStaticId) + ' '
                sRespond = sRespond[0:-1]
            lOldNewFriendsStaticId = lNewFriendsStaticId + lOldNewFriendsStaticId
            lNewFriendsStaticId = []
            sRespond = sRespond + '<spa>'
            if lFriendsStaticId != []:
                for sFriendsStaticId in lFriendsStaticId:
                    sRespond = sRespond + str(sFriendsStaticId) + ' '
                sRespond = sRespond[0:-1]
            mysql_tools.update_identity_friends_old_new_new(str(sStaticId),
                                                            str(lOldNewFriendsStaticId),
                                                            str(lNewFriendsStaticId))
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1': # 获取申请好友介绍
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityFriends = mysql_tools.get_identity_friends(str(sStaticId))
            mIntroduce = eval(lIdentityFriends[5])
            if mIntroduce != {}:
                sRespond = ''
                for ikey in mIntroduce:
                    sRespond = sRespond + str(ikey) + ' ' + mIntroduce[ikey] + ' '
                sRespond = sRespond[0:-1]
                print(sRespond)
                return '0' + sRespond
            return '0'

        if sServeType == '2':# 获取好友更新信息
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList(sActivityId)
            lIdentityFriends = mysql_tools.get_identity_friends(str(sStaticId))
            lNewFriendsStaticId = eval(lIdentityFriends[3])
            lOldNewFriendsStaticId = eval(lIdentityFriends[2])
            sRespond = ''
            if lNewFriendsStaticId != []:
                for sNewFriendsStaticId in lNewFriendsStaticId:
                    sRespond = sRespond + sNewFriendsStaticId + ' '
                sRespond = sRespond[0:-1]
            lOldNewFriendsStaticId = lNewFriendsStaticId + lOldNewFriendsStaticId
            lNewFriendsStaticId = []
            mysql_tools.update_identity_friends_old_new_new(str(sStaticId),
                                                            str(lOldNewFriendsStaticId),
                                                            str(lNewFriendsStaticId))
            print(sRespond)
            return '0' + sRespond

        if sServeType == '3': # 申请添加好友
            print("申请添加好友")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList(sActivityId)
            sToStaticId = eval(request.form.get('sStaticId'))
            sIntroduce = request.form.get('sIntroduce')
            lIdentityFriends = mysql_tools.get_identity_friends(str(sToStaticId))
            lNewFriendsStaticId = eval(lIdentityFriends[3])
            lBlackList = eval(lIdentityFriends[4])
            mIntroduce = eval(lIdentityFriends[5])
            if sFromStaticId in lNewFriendsStaticId:
                return '0'
            if sFromStaticId in lBlackList:
                return '2' # 好友申请失败 申请用户位于黑名单中
            lNewFriendsStaticId = [sFromStaticId] + lNewFriendsStaticId
            mIntroduce[sFromStaticId] = sIntroduce
            mysql_tools.update_identity_friends_new(str(sToStaticId),
                                                    str(lNewFriendsStaticId),
                                                    str(mIntroduce))
            return '0'

        if sServeType == '4': # 同意添加好友
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            lIdentityFriends = mysql_tools.get_identity_friends(str(sFromStaticId))
            lOldNewFriendsStaticId = eval(lIdentityFriends[2])
            iLength = len(lOldNewFriendsStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if lOldNewFriendsStaticId[iTimeout] == sToStaticId:
                    lFriendsStaticId = eval(lIdentityFriends[1])
                    lFriendsStaticId = [lOldNewFriendsStaticId[iTimeout]] + lFriendsStaticId
                    mIntroduce = eval(lIdentityFriends[5])
                    del mIntroduce[sToStaticId]
                    lOldNewFriendsStaticId = lOldNewFriendsStaticId[0:iTimeout] \
                                             + lOldNewFriendsStaticId[iTimeout + 1:]
                    mysql_tools.update_identity_friends_old_new(str(sFromStaticId),
                                                                str(lFriendsStaticId),
                                                                str(lOldNewFriendsStaticId),
                                                                str(mIntroduce))
                    return '0'
            return '2' # 同意申请好友不存在在好友申请列表中

        if sServeType == '5': # 删除添加好友
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            lIdentityFriends = mysql_tools.get_identity_friends(str(sFromStaticId))
            lOldNewFriendsStaticId = eval(lIdentityFriends[2])
            iLength = len(lOldNewFriendsStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if lOldNewFriendsStaticId[iTimeout] == sToStaticId:
                    mIntroduce = eval(lIdentityFriends[5])
                    del mIntroduce[sToStaticId]
                    lOldNewFriendsStaticId = lOldNewFriendsStaticId[0:iTimeout] \
                                             + lOldNewFriendsStaticId[iTimeout + 1:]
                    mysql_tools.update_identity_friends_old_new_new(str(sFromStaticId),
                                                                str(lOldNewFriendsStaticId),
                                                                str(mIntroduce))
                    return '0'
            return '0'

        if sServeType == '6': # 删除好友
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            lIdentityFriends = mysql_tools.get_identity_friends(str(sFromStaticId))
            lFriendsStaticId = eval(lIdentityFriends[1])
            iLength = len(lFriendsStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if lFriendsStaticId[iTimeout] == sToStaticId:
                    lFriendsStaticId = eval(lIdentityFriends[1])
                    lFriendsStaticId = lFriendsStaticId[0:iTimeout] \
                                       + lFriendsStaticId[iTimeout + 1:]
                    mysql_tools.update_identity_friends_friends(str(sFromStaticId),
                                                                str(lFriendsStaticId))
                    lIdentityFriends = mysql_tools.get_identity_friends(str(sToStaticId))
                    lFriendsStaticId = eval(lFriendsStaticId[1])
                    iLength = len(lFriendsStaticId)
                    iTimeout = 0
                    while iTimeout < iLength:
                        if lFriendsStaticId[iTimeout] == sFromStaticId:
                            lIdentityFriends = eval(lIdentityFriends[1])
                            lFriendsStaticId = lFriendsStaticId[0:iTimeout] \
                                               + lFriendsStaticId[iTimeout + 1:]
                            mysql_tools.update_identity_friends_friends(str(sFromStaticId),
                                                                        str(lFriendsStaticId))
                    return '0'
            return '2' # 删除好友失败 因为申请列表中并不存在该好友

@app.route('/identity_trends',methods = ['GET','POST'])
def identity_trends():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 查看个人动态
            print("查看个人动态")
            sStatciId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityTrends = mysql_tools.get_identity_trends(str(sStatciId))

            lTrendsId = eval(lIdentityTrends[1])
            sRespond1 = ""
            for sTrendsId in lTrendsId:
                sRespond1 = sRespond1 + str(sTrendsId) + ' '
            if sRespond1 != "":
                sRespond1 = sRespond1[0:-1]

            lCollectTrendsId = eval(lIdentityTrends[2])
            sRespond2 = ""
            for sTrendsId in lCollectTrendsId:
                sRespond2 = sRespond2 + str(sTrendsId) + ' '
            if sRespond2 != "":
                sRespond2 = sRespond2[0:-1]
            sRespond = "" + sRespond1 + '<spa>' + sRespond2
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1': # 查看历史动态
            print("查看更新动态")
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityTrends = mysql_tools.get_identity_trends(str(sStaticId))
            lOldTrendsId = eval(lIdentityTrends[4])
            lNewTrendsId = eval(lIdentityTrends[3])
            lOldTrendsId = lNewTrendsId + lOldTrendsId
            sRespond = ""
            for sOldTrendsId in lOldTrendsId:
                sRespond = sRespond + str(sOldTrendsId) + ' '
            if sRespond != "":
                sRespond = sRespond[0:-1]
            if lNewTrendsId != []:
                mysql_tools.update_identity_trends_Old(str(sStaticId),
                                                       str(lOldTrendsId))
            print(sRespond)
            return '0' + sRespond

        if sServeType == '2': # 获取更新动态信息
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityTrends = mysql_tools.get_identity_trends(str(sStaticId))
            lNewTrendsId = eval(lIdentityTrends[3])
            if lNewTrendsId == []:
                return '0'
            sRespond = ""
            for sTrendsId in lNewTrendsId:
                sRespond = sRespond + sTrendsId + ' '
            sRespond = sRespond[0:-1]
            lOldTrendsId = eval(lIdentityTrends[4])
            lOldTrendsId = lNewTrendsId + lOldTrendsId
            mysql_tools.update_identity_trends_Old(str(sStaticId),
                                                   str(lOldTrendsId))
            print(sRespond)
            return '0' + sRespond

        if sServeType == '3': # 获取其他用户动态信息
            print("获取其他用户动态信息")
            sStaticId = request.form.get("sStaticId")
            if sStaticId[0] == '[':
                sStaticIds = eval(sStaticId)
                lIdentityTrends = mysql_tools.get_many_identity_trends(sStaticIds)
                if lIdentityTrends == None:
                    return '0'
                if str(lIdentityTrends)[1] == '(':
                    sRespond = ''
                    for lIdentityTrend in lIdentityTrends:
                        sRespond = sRespond + lIdentityTrend[0] + '<spa>'
                        lTrendsId = eval(lIdentityTrend[1])
                        for sTrendsId in lTrendsId:
                            sRespond = sRespond + sTrendsId + ' '
                        if sRespond[-1] == ' ':
                            sRespond = sRespond[0:-1]
                        sRespond = sRespond + '<spa>'
                        lCollectTrendsId = eval(lIdentityTrends[2])
                        for sCollectTrendsId in lCollectTrendsId:
                            sRespond = sRespond + sCollectTrendsId + ' '
                        if sRespond[-1] == ' ':
                            sRespond = sRespond[0:-1]
                        sRespond = sRespond + '<spa1>'
                    print(sRespond)
                    return '0' + sRespond
                else:
                    sRespond = ''
                    sRespond = sRespond + lIdentityTrends[0] + '<spa>'
                    lTrendsId = eval(lIdentityTrends[1])
                    for sTrendsId in lTrendsId:
                        sRespond = sRespond + sTrendsId + ' '
                    if sRespond[-1] == ' ':
                        sRespond = sRespond[0:-1]
                    sRespond = sRespond + "<spa>"
                    lCollectTrendsId = eval(lIdentityTrends[2])
                    for sCollectTrend in lCollectTrendsId:
                        sRespond = sRespond + sCollectTrend + ' '
                    if sRespond[-1] == ' ':
                        sRespond = sRespond[0:-1]
                    print(sRespond)
                    return '0' + sRespond
            else:
                lIdentityTrend = mysql_tools.get_identity_trends(sStaticId)
                if lIdentityTrend == None:
                    return '0'
                lTrendsId = eval(lIdentityTrend[1])
                lCollectTrendsId = eval(lIdentityTrend[2])
                sRespond = ""
                for sTrendsId in lTrendsId:
                    sRespond = sRespond + str(sTrendsId) + ' '
                if sRespond[-1] == ' ':
                    sRespond = sRespond[0:-1]
                sRespond = sRespond + '<spa>'
                for sCollectTrendsId in lCollectTrendsId:
                    sRespond = sRespond + str(sCollectTrendsId) + ' '
                if sRespond[-1] == ' ':
                    sRespond = sRespond[0:-1]
                print(sRespond)
                return '0' + sRespond

@app.route('/trends',methods = ['GET','POST'])
def trends():
    if request.method == 'GET':
        return '暂未开放'

    if request.method == 'POST':
        sServeType = request.form.get('sServeType')
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'

        if sServeType == '0': # 获取动态信息
            print("获取动态信息")
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTrendsId = request.form.get("sTrendsId")
            if sTrendsId[0] == '[':
                lTrendsId = eval(sTrendsId)
                if lTrendsId == []:
                    return '0'
                lManyTrends = mysql_tools.get_many_trends(lTrendsId)
                if lManyTrends == None:
                    return '0'
                sRespond = ""
                for lTrends in lManyTrends:
                    sRespond = sRespond + str(lTrends[0]) + '<spa1>' \
                               + str(lTrends[1]) + '<spa>' \
                               + lTrends[2] + '<spa>' \
                               + lTrends[3] + '<spa>' \
                               + lTrends[4] + '<spa>' \
                               + str(lTrends[5]) + '<spa>' \
                               + str(lTrends[6]) + '<spa>'
                    if sStaticId in eval(lTrends[7]):
                        sRespond = sRespond + 'true<spa1>'
                    else:
                        sRespond = sRespond + 'false<spa1>'
                print(sRespond)
                return '0' + sRespond
            else:
                lTrends = mysql_tools.get_trends(sTrendsId)
                if lTrends == None:
                    return '0'
                sRespond = ""
                sRespond = sRespond + str(lTrends[0]) + '<spa1>' \
                           + str(lTrends[1]) + '<spa>' \
                           + lTrends[2] + '<spa>' \
                           + lTrends[3] + '<spa>' \
                           + lTrends[4] + '<spa>' \
                           + str(lTrends[5]) + '<spa>' \
                           + str(lTrends[6]) + '<spa>'
                if sStaticId in eval(lTrends[7]):
                    sRespond = sRespond + 'true<spa1>'
                else:
                    sRespond = sRespond + 'false<spa1>'
                print(sRespond)
                return '0' + sRespond

        if sServeType == '1': # 获取评论
            sTrendsId = request.form.get('sTrendsId')
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lTrend = mysql_tools.get_trends(sTrendsId)
            mDiscuss = eval(lTrend[5])
            sRespond1 = ""
            sRespond2 = ""
            # sRespond1 = staticId<spa1>discussString<spa1>praiseNumber<spa>discussNumber<spa>
            # isPraise<spa>isDiscuss<spa>
            # sRespond2 = staticId<spa>replyString<spa>isPraise<spa1>
            # sRespond = sRespond1 + sRespond2
            for sStaticId1 in mDiscuss:
                sRespond1 = sRespond1 + str(sStaticId1) + '<spa1>' \
                           + mDiscuss[sStaticId1][0] + '<spa1>'
                sRespond1 = sRespond1 + str(mDiscuss[sStaticId1][1]) + '<spa>' \
                            + str(mDiscuss[sStaticId1][2]) + '<spa>'
                lPraiseList = eval(mDiscuss[sStaticId1][3])
                if sStaticId in lPraiseList:
                    sRespond1 = sRespond1 + 'true<spa>'
                else:
                    sRespond1 = sRespond1 + 'false<spa>'
                mReplyList = eval(mDiscuss[sStaticId1][4])
                if sStaticId in mReplyList:
                    sRespond1 = sRespond1 + 'true<spa1>'
                else:
                    sRespond1 = sRespond1 + 'false<spa>'

                mReply = eval(mDiscuss[sStaticId1][4])
                for sStaticId2 in mReply:
                    sRespond2 = str(sStaticId2) + '<spa>' \
                                + mReply[sStaticId2][0] + '<spa>' \
                                + str(mReply[sStaticId2][1]) + '<spa>'
                    lReplyPraiseList = eval(mReply[sStaticId2][2])
                    if sStaticId in lReplyPraiseList:
                        sRespond2 = sRespond2 + 'true<spa>'
                    else:
                        sRespond2 = sRespond2 + 'false<spa>'
                if sRespond2 != "":
                    sRespond2 = sRespond2[0:-5]
                else:
                    sRespond2 = 'null'

                sRespond1 = sRespond1 + sRespond2 + '<spa1>'
            if sRespond1 != "":
                sRespond = sRespond1[0:-6]
            print(sRespond)
            return '0' + sRespond

        if sServeType == '2': # 发送点赞信息
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTrendsId = request.form.get('sTrendsId')
            lIdentityTrend = mysql_tools.get_trends(sTrendsId)
            if lIdentityTrend == None:
                return '2' # 未找到相关动态 点赞失败
            lPraiseStaticId = eval(lIdentityTrend[7])
            if sStaticId in lPraiseStaticId:
                return '3' # 重复点赞 点赞失败
            iPraiseNumber = lIdentityTrend[5] + 1
            lPraiseStaticId = [sStaticId] + lPraiseStaticId
            mysql_tools.update_trends_praise(sTrendsId,str(iPraiseNumber),str(lPraiseStaticId))
            return '0' # 点赞成功

        if sServeType == '3': # 取消点赞
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTrendsId = request.form.get('sTrendsId')
            lTrend = mysql_tools.get_trends(sTrendsId)
            if lTrend == None:
                return '2' # 未找到相关动态 取消点赞失败
            lPraiseStaticId = eval(lTrend[7])
            iLength = len(lPraiseStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if lPraiseStaticId[iTimeout] == sStaticId:
                    print("找到相关点赞用户 尝试执行删除操作")
                    iPraiseNumber = lTrend[5]
                    iPraiseNumber = iPraiseNumber - 1
                    if len(lPraiseStaticId) > iTimeout + 1:
                        lPraiseStaticId = lPraiseStaticId[0:iTimeout] \
                                          + lPraiseStaticId[iTimeout + 1:]
                    else:
                        lPraiseStaticId = lPraiseStaticId[0:iTimeout]
                    mysql_tools.update_trends_praise(sTrendsId,
                                                     str(iPraiseNumber),
                                                     str(lPraiseStaticId))
                    return '0' # 取消点赞成功
                iTimeout = iTimeout + 1
            return '3' # 未点赞 无需删除

        if sServeType == '4': # 发布评论
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTrendsId = request.form.get("sTrendsId")
            sIntroduce = request.form.get("sDiscuss")
            lTrend = mysql_tools.get_trends(sTrendsId)
            if lTrend == None:
                return '2' # 未找到相关动态
            iIntroduceNumber = lTrend[6]
            mIntroduce = eval(lTrend[8])
            if sStaticId in mIntroduce:
                iIntroduceNumber = iIntroduceNumber - 1
                mIntroduce[sStaticId] = [sIntroduce,0,0,'[]','{}']
                mysql_tools.update_trends_discuss(sTrendsId,
                                                    str(iIntroduceNumber),
                                                    str(mIntroduce))
                return '3' # 重复评论 已覆盖
            iIntroduceNumber = iIntroduceNumber + 1
            mIntroduce[sStaticId] = [sIntroduce,0,0,'[]','{}']
            mysql_tools.update_trends_discuss(sTrendsId,
                                                str(iIntroduceNumber),
                                                str(mIntroduce))
            return '0' # 添加评论成功

        if sServeType == '5': # 发布动态
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTitle = request.form.get("sTitle")
            sText = request.form.get("sText")
            # 上传动态图片
            # iPictureNumber = request.form.get("iPictureNumber")
            # iTimeout = 1
            # while iTimeout <= iPicTureNumber:
            #   iTimeout = iTimeout + 1
            #   bPictureData = request.form.get("Picture" + str(iTimeout))
            # ############################################################
            sPicturePosition = '000001'
            lTrendsNumber = mysql_tools.get_trends_parameter()
            iTrendsNumber = lTrendsNumber[0]
            iTrendsNumber = iTrendsNumber + 1
            mysql_tools.update_trends_parameter(str(iTrendsNumber))
            mysql_tools.insert_trends(str(iTrendsNumber),str(sStaticId),
                                      sTitle,sText,sPicturePosition)
            lIdentityTrends = mysql_tools.get_identity_trends(str(sStaticId))
            lTrendsId = eval(lIdentityTrends[1])
            lTrendsId = [iTrendsNumber] + lTrendsId
            lOldTrendsId = eval(lIdentityTrends[4])
            lOldTrendsId = [iTrendsNumber] + lOldTrendsId
            mysql_tools.update_identity_trends_trends(str(sStaticId),
                                                      str(lTrendsId),
                                                      str(lOldTrendsId))
            lAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sStaticId))
            lFansStaticId = eval(lAttentionsFans[4])
            for sFansStaticId in lFansStaticId:
                lIdentityTrends = mysql_tools.get_identity_trends(str(sFansStaticId))
                lNewTrends = eval(lIdentityTrends[3])
                lNewTrends = [iTrendsNumber] + lNewTrends
                mysql_tools.update_identity_trends_Old(str(sFansStaticId),
                                                       str(lNewTrends))
            return '0' + str(iTrendsNumber)

        if sServeType == '7': # 评论点赞
            print("评论点赞")
            sTrendsId = request.form.get("sTrendsId")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = request.form.get("sStaticId")
            lTrends = mysql_tools.get_trends(str(sTrendsId))
            mDiscuss = eval(lTrends[8])
            iDiscussNumber = lTrends[6]
            lDiscuss = mDiscuss[sToStaticId]
            if lDiscuss == None:
                return '2' # 未找到相关评论
            lDiscussPraiseStaticId = eval(lDiscuss[3])
            if sToStaticId in lDiscussPraiseStaticId:
                return '0' # 已经点过赞
            lDiscussPraiseStaticId = [sFromStaticId] + lDiscussPraiseStaticId
            iDiscussPraiseNumber = lDiscuss[1]
            iDiscussPraiseNumber = iDiscussPraiseNumber + 1
            mDiscuss[sToStaticId][1] = iDiscussPraiseNumber
            mDiscuss[sToStaticId][3] = str(lDiscussPraiseStaticId)
            mysql_tools.update_trends_discuss(sTrendsId,iDiscussNumber,str(mDiscuss))
            return '0'

        if sServeType == '7': # 删除评论点赞
            print("删除评论点赞")
            sTrendsId = request.form.get("sTrendsId")
            sStaticId = request.form.get("sStaticId")
            lTrends = mysql_tools.get_trends(sTrendsId)
            if lTrends == None:
                return '21' # 未找到相关动态
            mDiscuss = eval(lTrends[8])
            iDiscussNumber = lTrends[6]
            lDiscussPraiseStaticId = eval(mDiscuss[sStaticId][3])
            iDiscussPraiseNumber = mDiscuss[sStaticId][1]
            iLength = len(lDiscussPraiseStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if sServeType == lDiscussPraiseStaticId[iTimeout]:
                    lDiscussPraiseStaticId = lDiscussPraiseStaticId[0:iTimeout] \
                                + lDiscussPraiseStaticId[iTimeout + 1:]
                    iDiscussPraiseNumber = iDiscussPraiseNumber - 1
                    mDiscuss[sStaticId][1] = iDiscussPraiseNumber
                    mDiscuss[sStaticId][3] = str(lDiscussPraiseStaticId)
                    mysql_tools.update_trends_discuss(sTrendsId,iDiscussNumber,str(mDiscuss))
                    return '0' # 删除评论点赞成功
                iTimeout = iTimeout + 1
            return '22' # 未找到相关评论

        if sServeType == '8': # 子评论点赞
            print("子评论点赞")
            sTrendsId = request.form.get("sTrendsId")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId1 = request.form.get("sStaticId1")
            sToStaticId2 = request.form.get("sStaticId2")
            lTrends = mysql_tools.get_trends(str(sTrendsId))
            mDiscuss = eval(lTrends[8])
            iDiscussNumber = lTrends[6]
            lDiscuss = mDiscuss[sToStaticId1]
            if lDiscuss == None:
                return '2' # 评论不存在
            mReply = eval(lDiscuss[4])
            lReply = mReply[sToStaticId2]
            lReplyPraiseStaticId = eval(lReply[2])
            if sFromStaticId in lReplyPraiseStaticId:
                return '0'
            iReplyPraiseNumber = lReply[1]
            iReplyPraiseNumber = iReplyPraiseNumber + 1
            lReplyPraiseStaticId = [sFromStaticId] + lReplyPraiseStaticId
            mReply[sToStaticId2][1] = iReplyPraiseNumber
            mReply[sToStaticId2][2] = lReplyPraiseStaticId
            mDiscuss[sToStaticId1][4] = str(mReply)
            mysql_tools.update_trends_discuss(sTrendsId,iDiscussNumber,mDiscuss)
            return '0'

        if sServeType == '9': # 删除子评论点赞
            print("删除子评论点赞")
            sTrendsId = request.form.get("sTrendsId")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId1 = request.form.get("sStaticId1")
            sToStaticId2 = request.form.get("sStaticId2")
            lTrends = mysql_tools.get_trends(sTrendsId)
            mDiscuss = eval(lTrends[8])
            iDiscussNumber = lTrends[6]
            lDiscuss = mDiscuss[sToStaticId1]
            if lDiscuss == None:
                return '2' # 未找到相关评论
            mReply = eval(lDiscuss[4])
            lReply = mReply[sToStaticId2]
            lReplyPraiseStaticId = eval(lReply[2])
            iLength = len(lReplyPraiseStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if lReplyPraiseStaticId[iTimeout] == sFromStaticId:
                    lReplyPraiseStaticId = lReplyPraiseStaticId[0:iTimeout] \
                                           + lReplyPraiseStaticId[iTimeout + 1:]
                    iReplyPraiseNumber = lReply[1]
                    iReplyPraiseNumber = iReplyPraiseNumber - 1
                    mReply[sToStaticId2][1] = iReplyPraiseNumber
                    mReply[sToStaticId2][2] = str(lReplyPraiseStaticId)
                    mDiscuss[sToStaticId1][4] = str(mReply)
                    mysql_tools.update_trends_discuss(sTrendsId,iDiscussNumber,str(mDiscuss))
                    return '0'
                iTimeout = iTimeout + 1
            return '2' # 未找到相关子评论点赞信息

@app.route('/identity_attentions_fans',methods = ['GET','POST'])
def identity_attentions_fans():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 获取用户个人信息
            print('获取用户个人信息')
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sStaticId))
            sRespond = ""
            sRespond = sRespond + str(lIdentityAttentionsFans[1]) \
                       + ' ' + str(lIdentityAttentionsFans[2]) + '<spa1>'
            lAttentionsStaticId = eval(lIdentityAttentionsFans[3])
            if lAttentionsStaticId != []:
                for sAttentionsStaticId in lAttentionsStaticId:
                    sRespond = sRespond + str(sAttentionsStaticId) + ' '
                sRespond = sRespond[0:-1]
            sRespond = sRespond + '<spa2>'
            lFansStaticId = eval(lIdentityAttentionsFans[4])
            if lFansStaticId != []:
                for sFansStaticId in lFansStaticId:
                    sRespond = sRespond + str(sFansStaticId) + ' '
                sRespond = sRespond[0:-1]
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1': # 获取 一般用户 一般信息 包括:关注数 粉丝数
            print('获取基础信息')
            sStaticId = request.form.get('sStaticId')
            if sStaticId[0] == '[':
                sStaticIds = eval(sStaticId)
                if sStaticIds == []:
                    return '0'
                iLength = len(sStaticIds)
                lIdentityAttentionsFans = mysql_tools.get_many_identity_attentions_fans(sStaticIds)
                if str(lIdentityAttentionsFans)[1] == '(':
                    if iLength > 1:
                        return '2' # 数据获取不全
                else:
                    if iLength > len(lIdentityAttentionsFans):
                        return '2' # 数据获取不全
                if lIdentityAttentionsFans == None:
                    return '0'
                sRespond = ''
                for lIdentity in lIdentityAttentionsFans:
                    sRespond = sRespond + lIdentity[1] + ' ' + lIdentity[2] + ' '
                sRespond = sRespond[0:-1]
                print(sRespond)
                return '0' + sRespond
            else:
                lIdentityAttentionsFans = mysql_tools.get_identity_attentions_fans(sStaticId)
                sRespond = ''
                sRespond = sRespond + str(lIdentityAttentionsFans[1]) + ' ' + str(lIdentityAttentionsFans[2])
                print(sRespond)
                return '0' + sRespond

        if sServeType == '2': # 添加关注
            print("添加关注")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get('sStaticId'))
            lIdentityFromAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sFromStaticId))
            lAttentionsStaticId = eval(lIdentityFromAttentionsFans[3])
            if sToStaticId in lAttentionsStaticId:
                return '2' # 已经添加过关注
            lAttentionsStaticId = [sToStaticId] + lAttentionsStaticId
            mysql_tools.update_identity_attentions_fans_attentions(str(sFromStaticId),
                                                                   str(lAttentionsStaticId))
            lIdentityToAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sToStaticId))
            lFansStaticId = eval(lIdentityToAttentionsFans[4])
            lFansStaticId = [sFromStaticId] + lFansStaticId
            mysql_tools.update_identity_attentions_fans_fans(str(sToStaticId),
                                                             str(lFansStaticId))
            return '0'

        if sServeType == '3': # 删除关注
            print("删除关注")
            sFromStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sToStaticId = eval(request.form.get("sStaticId"))
            lFromAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sFromStaticId))
            lAttentionsStaticId = eval(lFromAttentionsFans[3])
            iLength = len(lAttentionsStaticId)
            iTimeout = 0
            while iTimeout < iLength:
                if sToStaticId == lAttentionsStaticId[iTimeout]:
                    lAttentionsStaticId = lAttentionsStaticId[0:iTimeout] \
                                          + lAttentionsStaticId[iTimeout + 1:]
                    mysql_tools.update_identity_attentions_fans_attentions(str(sFromStaticId),
                                                                           str(lAttentionsStaticId))
                    lToAttentionsFans = mysql_tools.get_identity_attentions_fans(str(sToStaticId))
                    lFansStaticId = eval(lToAttentionsFans[4])
                    iLength = len(lFansStaticId)
                    iTimeout = 0
                    while iTimeout < iLength:
                        if sFromStaticId == lFansStaticId[iTimeout]:
                            lFansStaticId = lFansStaticId[0:iTimeout] \
                                            + lFansStaticId[iTimeout + 1:]
                            mysql_tools.update_identity_attentions_fans_fans(str(sToStaticId),
                                                                             str(lFansStaticId))
                            return '0'
                    return '0'

@app.before_first_request
def init_server():
    keeper.DataKeeper.mActivityIdStaticIdList['000001'] = 9
    keeper.DataKeeper.mActivityIdStaticIdList['000002'] = 10
    keeper.DataKeeper.mActivityIdStaticIdList['000003'] = 11

if __name__ == '__main__':
    app.run(host = '0.0.0.0',port = 5000,debug = True)
