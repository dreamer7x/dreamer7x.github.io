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
            sDate = datetime.datetime.now().strftime('%Y %m %d')
            sWeek = datetime.datetime.now().strftime('%w')
            if sWeek == '0':
                sWeek = '7'
            sDate = sDate + ' ' + sWeek
            return '0' + sDate

        if sServeType == '1':  # 提供打卡服务
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sDate = datetime.datetime.now().strftime('%d')
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
                    sOldChat = lChat[3]
                    sRespond = sOldChat
                    print(sRespond)
                    return '0' + sRespond

                lDrivingChatStaticId = eval(lIdentityChat[3])
                if sToStaticId in lDrivingChatStaticId:
                    lChat = mysql_tools.get_chat(str(sFromStaticId),str(sToStaticId))
                    sOldChat = lChat[3]
                    sRespond = sOldChat
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
                if sToStaticId in lNewPassiveChatStaticId:
                    lChat = mysql_tools.get_chat(sFromStaticId,sToStaticId)
                    lNewChat = lChat[2]
                    lNewChat = lNewChat + sSendChat + '<spa2>'
                    # 更新聊天数据
                    mysql_tools.update_new_chat(str(sFromStaticId),str(sToStaticId),lNewChat)
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
                        sNewChat = '2' + sSendChat + '<spa2>'
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
                if sToStaticId in lNewDrivingChatStaticId:
                    lChat = mysql_tools.get_chat(sFromStaticId, sToStaticId)
                    lNewChat = lChat[2]
                    lNewChat = lNewChat + sSendChat + '<spa1>'
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
                       + ' ' + sIdentitySign[4]
            print(sRespond)
            return '0' + sRespond

        if sServeType == '1':  # 获取一般用户信息
            print('获取一般用户信息')
            sStaticId = request.form.get('sStaticId')
            print(sStaticId)
            if sStaticId[0] == '[':  # 获取多个用户信息
                sStaticIds = eval(sStaticId)
                lIdentitySigns = mysql_tools.get_many_identity_signs(sStaticIds)
                if lIdentitySigns == None:
                    return '2' # 未找到相关记录
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

@app.route('/identity_attentions_fans',methods = ['GET','POST'])
def identity_attentions_fans():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 获取 一般用户 一般信息 包括:关注数 粉丝数
            print('获取基础信息')
            sStaticId = request.form.get('sStaticId')
            if sStaticId[0] == '[':
                sStaticIds = eval(sStaticId)
                lIdentitys = mysql_tools.get_many_identity_attentions_fans(sStaticIds)
                sRespond = '' + lIdentitys[0][1] + ' ' + lIdentitys[0][2]
                for lIdentity in lIdentitys:
                    sRespond = sRespond + ' ' + lIdentity[1] + ' ' + lIdentity[2]
                return '0' + sRespond
            else:
                lIdentity = mysql_tools.get_identity_attentions_fans(sStaticId)
                return '0' + lIdentity[1] + ' ' + lIdentity[2]

        if sServeType == '1': # 获取用户个人信息
            print('获取用户个人信息')
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentity = mysql_tools.get_identity_attentions_fans(str(sStaticId))
            return '0' + lIdentity[1] + ' ' + lIdentity[2] + '<spa>' + lIdentity[3] + '<spa>' + lIdentity[4]

@app.route('/identity_trends',methods = ['GET','POST'])
def identity_trends():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'
        sServeType = request.form.get('sServeType')

        if sServeType == '0': # 查看个人动态 数据制式 新数据1 新数据2……<spa>旧数据1 旧数据2……
            sStatciId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityTrends = mysql_tools.get_identity_trends(str(sStatciId))

            sNewTrendsId = lIdentityTrends[2]
            if sNewTrendsId == None:
                sOldTrendsId = lIdentityTrends[3]
                if sOldTrendsId == None:
                    return '0' # 无更新动态
                lOldTrendsId = sOldTrendsId.split(' ');
                sRespond = "" + lOldTrendsId[0]
                for sOldTrendsId in lOldTrendsId[1:]:
                    sRespond = sRespond + ' ' + sOldTrendsId
                return '0' + '<spa>' + sRespond

            lNewTrendsId = sNewTrendsId.split(' ')
            sRespond = '' + lNewTrendsId[0]
            for sTrendsId in lNewTrendsId[1:]:
                sRespond = sRespond + ' ' + sTrendsId
            sOldTrendsId = lIdentityTrends[3]
            if sOldTrendsId == None:
                mysql_tools.update_identity_trends_New_Old(sStatciId, '', sNewTrendsId + ' ' \
                                                           + sOldTrendsId)
                return '0' + sRespond + '<spa>'
            lOldTrendsId = sOldTrendsId.split(' ')
            sRespond = sRespond + '<spa>'
            for sOldTrendsId in lOldTrendsId:
                sRespond = sRespond + ' ' + sOldTrendsId
            mysql_tools.update_identity_trends_New_Old(sStatciId, '', sNewTrendsId + ' ' \
                                                       + sOldTrendsId)
            return '0' + sRespond

        if sServeType == '1': # 个人动态信息
            sStaticId = request.form.get('sStaticId')
            lIdentityTrends = mysql_tools.get_identity_trends(sStaticId)
            return '0' + lIdentityTrends[1]

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
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList(sActivityId)
            lIdentity = mysql_tools.get_identity_friends(str(sStaticId))
            return '0' + lIdentity[1] + '<spa>' + lIdentity[2]

@app.route('/send_chat',methods = ['GET','POST']) # 发送聊天信息
def send_chat():
    if request.method == 'GET':
        return '暂未开放'

    if request.method == 'POST':
        sServeType = request.form.get('sServeType')
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'

        sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
        lIdentityChat = mysql_tools.get_identity_chat(sStaticId)

        if sServeType == '0':
            sStaticId1 = lIdentityChat[1]
            sStaticId2 = lIdentityChat[2]
            sStaticId = request.form.get('sStaticId')
            sPattern = '(^' + sStaticId + '\\s)|(\\s' + sStaticId + '\\s)|(\\s' + sStaticId + '$)'
            tAnswer = re.search(sPattern, sStaticId1).span()
            if tAnswer != None:
                lIdentity = mysql_tools.get_chat(sStaticId, sStaticId1)
                return '0'
            tAnswer = re.search(sPattern, sStaticId2).span()
            if tAnswer != None:
                lIdentity = mysql_tools.get_chat(sStaticId2, sStaticId)
                return '0'

@app.route('/trends',methods = ['GET','POST'])
def trends():
    if request.method == 'GET':
        return '暂未开放'

    if request.method == 'POST':
        sServeType = request.form.get('sServeType')
        sActivityId = request.form.get('sActivityId')
        if sActivityId not in keeper.DataKeeper.mActivityIdStaticIdList:
            return '1'

        if sServeType == '0': # 获取动态更新信息
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            lIdentityTrends = mysql_tools.get_identity_trends(sStaticId)
            lNewTrendsIds = lIdentityTrends[2].split(' ')
            lResponds = mysql_tools.get_many_trends(lNewTrendsIds)
            sRespond = '<sTitle>' + lResponds[0][2] + '<sText>' \
                       + lResponds[0][3] + '<sPicture>' \
                       + lResponds[0][4] + '<sPraise>' \
                       + lResponds[0][5] + '<sDiscuss>' \
                       + lResponds[0][6]
            for lRespond in lResponds:
                sRespond = sRespond + '<end><sTitle>' + lRespond[2] + '<sText>' \
                       + lRespond[3] + '<sPicture>' \
                       + lRespond[4] + '<sPraise>' \
                       + lRespond[5] + '<sDiscuss>' \
                       + lRespond[6]
            return '0' + sRespond

        if sServeType == '1': # 获取是否点赞
            sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId]
            sTrendsId = request.form.get('sTrendsId')
            lIdentityTrend = mysql_tools.get_trends(sTrendsId)
            sPraiseStaticIds = lIdentityTrend[7]
            sPattern = '(^' + sStaticId + '\\s)|(\\s' + sStaticId + '\\s)|(\\s' + sStaticId + '$)'
            tAnswer = re.search(sPattern, sPraiseStaticIds).span()
            if tAnswer != None:
                return '0' + 'y'
            else:
                return '0' + 'n'

        if sServeType == '2': #获取评论
            sTrendsId = request.form.get('sTrendsId')
            lIdentityTrend = mysql_tools.get_identity_trends(sTrendsId)
            return '0' + lIdentityTrend[8]

@app.before_first_request
def init_server():
    keeper.DataKeeper.mActivityIdStaticIdList['000001'] = 9
    keeper.DataKeeper.mActivityIdStaticIdList['000002'] = 10

if __name__ == '__main__':
    app.run(host = '0.0.0.0',port = 5000,debug = True)