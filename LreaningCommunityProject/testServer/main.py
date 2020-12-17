from flask import Flask,url_for,redirect,request
import random
import string
import keeper
import account_tools
import email_send_tools
import pymysql
import time
import email_send_tools
import _thread
import threading
from datetime import datetime

lEmailLoginData = []

app = Flask(__name__)

@app.route('/',methods = ['GET','POST'])
def title():
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sDatas = ""
        for sPosition in request.form:
            sDatas = sDatas + request.form[sPosition]
        return sDatas


@app.route('/login',methods = ['GET','POST'])
def login():
    global lEmailLoginData
    if request.method == 'GET':
        return '未开放'

    if request.method == 'POST':
        sServeType = request.form.get("sServeType")
        iServeType = int(sServeType)

        if iServeType == 0:
            sEmail = request.form.get("sEmail")
            sPassword = request.form.get("sPassword")
            sActivityId = request.form.get("sActivityId")

            lAccount = account_tools.find_email_account(sEmail)
            if lAccount != None:
                if lAccount[1] == sPassword:
                    if lAccount[2] == sActivityId:
                        sActivityId = account_tools.create_activity_id(lAccount[3])
                        account_tools.update_account_activity_id(sEmail,sActivityId)
                        account_tools.update_account_time_out(sEmail)
                        return "0" + sActivityId
                    else:
                        return "3"
                else:
                    return "2"
            else:
                return "1"

        if iServeType == 1:
            print("邮箱发送开始执行")
            sEmail = request.form.get("sEmail")
            print(keeper.DataKeeper.mEmailCheckDataList)
            if sEmail in keeper.DataKeeper.mEmailCheckDataList:
                print("邮箱冲突:" + str(keeper.DataKeeper.mEmailCheckDataList))
                dTimeSpace = keeper.DataKeeper.mEmailCheckDataList[sEmail][1] - keeper.DataKeeper.iSecondTimeout
                if dTimeSpace < 0:
                    return '1' + str(60 + dTimeSpace)
                else:
                    return '1' + str(dTimeSpace)
            sEmailCheckString = "".join(random.sample([x for x in string.ascii_letters + string.digits], 6))
            mEmailPackage = []
            mEmailPackage.append('smtp.qq.com')
            mEmailPackage.append(465)
            mEmailPackage.append('1797726938@qq.com')
            mEmailPackage.append('xdmzauzpjsqgbaej')  # 邮箱密码：需要使用授权码
            mEmailPackage.append(sEmail)  # 收件人，多个收件人用逗号隔开
            mEmailPackage.append(keeper.ParameterKeeper.sEmailContentStart + sEmailCheckString + keeper.ParameterKeeper.sEmailContentEnd)
            mEmailPackage.append("学习社区 官方邮件")
            mEmailPackage.append("官方邮箱")
            mEmailPackage.append("亲爱的用户")
            keeper.DataKeeper.mEmailCheckDataList[sEmail] = [sEmailCheckString,keeper.DataKeeper.iSecondTimeout - 1]
            email_send_tools.send_ssl_email(mEmailPackage)
            print('创建邮箱:' + str(keeper.DataKeeper.mEmailCheckDataList))
            return '0'

        if iServeType == 2:
            sEmail = request.form.get("sEmail")
            sCheckData = request.form.get("sCheckData")

            print("邮箱开始验证:" + str(keeper.DataKeeper.mEmailCheckDataList))
            if sEmail in keeper.DataKeeper.mEmailCheckDataList:
                if sCheckData == keeper.DataKeeper.mEmailCheckDataList[sEmail][0]:
                    lAccount = account_tools.find_email_account(sEmail)
                    if  lAccount == None:
                        print("创建新账户开始执行")
                        lAccountPackage = []
                        lAccountPackage.append(sEmail)
                        sPassword = "".join(random.sample([x for x in string.ascii_letters + string.digits], 6))
                        lAccountPackage.append(sPassword)
                        lAccountPackage.append("000000")
                        lAccountPackage.append(30)
                        account_tools.insert_account(lAccountPackage)
                        sStaticId = account_tools.find_email_account(sEmail)
                        sStaticId = sStaticId[3]
                        sActivityId = account_tools.create_activity_id(sStaticId)
                        account_tools.update_account_activity_id(sEmail, sActivityId)
                        return "0" + sActivityId
                    else:
                        print("老帐号创建动态码开始执行")
                        sStaticId = lAccount[3]
                        sActivityId = account_tools.create_activity_id(sStaticId)
                        account_tools.update_account_activity_id(sEmail, sActivityId)
                        account_tools.update_account_time_out(sEmail)
                        return "0" + sActivityId
                else:
                    return "2" #验证码错误
            else:
                return "1" #邮箱不存在

        if iServeType == 3:
            sActivityId = request.form.get("sActivityId")

            if sActivityId in keeper.DataKeeper.mActivityIdStaticIdList:
                sStaticId = keeper.DataKeeper.mActivityIdStaticIdList[sActivityId][0]
                print(sStaticId)
                sAccount = account_tools.find_static_id_account(sStaticId)
                return "0" + sAccount[1]
            else:
                return "1"

class DataManagerThread(threading.Thread):
    def __init__(self):
        threading.Thread.__init__(self)

    def run(self):
        print("数据管理线程: " + self.name + "启动")
        keeper.DataKeeper.iMinuteTimeout = 0
        keeper.DataKeeper.iSecondTimeout = 0
        dTimeSpace = 0
        while 1:
            dStartTimeout = time.time()
            for sKey in list(keeper.DataKeeper.mEmailCheckDataList.keys()):
                if keeper.DataKeeper.mEmailCheckDataList[sKey][1] == keeper.DataKeeper.iSecondTimeout:
                    keeper.DataKeeper.mEmailCheckDataList.pop(sKey)
                    print('删除验证码')
                    print(keeper.DataKeeper.mEmailCheckDataList)

            for sKey in list(keeper.DataKeeper.mActivityIdStaticIdList.keys()):
                if keeper.DataKeeper.mActivityIdStaticIdList[sKey][1] == keeper.DataKeeper.iMinuteTimeout:
                    keeper.DataKeeper.mActivityIdStaticIdList.pop(sKey)
                    keeper.DataKeeper.lActivityPool.append(sKey)
                    print('删除动态码')
                    print(keeper.DataKeeper.mEmailCheckDataList)

            dEndTimeout = time.time()
            dTimeSpace = dTimeSpace + dEndTimeout - dStartTimeout
            if dTimeSpace >= 1:
                keeper.DataKeeper.iSecondTimeout = keeper.DataKeeper.iSecondTimeout + 1
                print(keeper.DataKeeper.iSecondTimeout)
                dTimeSpace = 0
            if keeper.DataKeeper.iSecondTimeout >= 60:
                keeper.DataKeeper.iMinuteTimeout = keeper.DataKeeper.iMinuteTimeout + 1
                print(keeper.DataKeeper.iMinuteTimeout)
                keeper.DataKeeper.iSecondTimeout = 0
            if keeper.DataKeeper.iMinuteTimeout >= 5:
                keeper.DataKeeper.iMinuteTimeout = 0

@app.before_first_request
def initServe():
    dataManageerTread = DataManagerThread()
    dataManageerTread.start()

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)





