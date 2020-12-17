import pymysql
import keeper
import random
import string

def find_email_account(sEmail):
    conMysqlConnection = pymysql.connect(host = "localhost",
                                         user = 'user01',
                                         password = '123',
                                         database = 'test',
                                         charset = 'utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = keeper.ParameterKeeper.sMysqlSelectAccount + 'sEmail = \'' + sEmail + '\''
    print(sSql)
    cCursor.execute(sSql)
    return cCursor.fetchone()

def find_static_id_account(sStaticId):
    conMysqlConnection = pymysql.connect(host = "localhost",
                                         user = "user01",
                                         password = '123',
                                         database = 'test',
                                         charset = 'utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = keeper.ParameterKeeper.sMysqlSelectAccount + 'sStaticId = \'' + str(sStaticId) + '\''
    cCursor.execute(sSql)
    return cCursor.fetchone()

def insert_account(lAccountPackage):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='user02',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSqlEnd = '(\'' + lAccountPackage[0] \
              + '\',\'' + lAccountPackage[1] \
              + '\',\'' + lAccountPackage[2] \
              + '\',' + str(lAccountPackage[3]) + ')'
    sSql = keeper.ParameterKeeper.sMysqlInsertAccount + sSqlEnd
    print(sSql)
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_account_activity_id(sEmail,sActivityId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user="user03",
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update account set sActivityId = \'' + sActivityId + '\'' \
           + 'where sEmail = \'' + sEmail + '\''
    print(sSql)
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_account_password(sEmail,sPassword):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user="user03",
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update account set sPassword = \'' + sPassword + '\'' \
           + ' where sEmail = \'' + sEmail + '\''
    print(sSql)
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_account_time_out(sEmail):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user="user03",
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update account set iTimeout = ' + '30' \
           + ' where sEmail = \'' + sEmail + '\''
    print(sSql)
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def create_activity_id(sStaticId):
    if keeper.DataKeeper.bActivityConflict:
        iPosition = random.randint(0,len(keeper.DataKeeper.lActivityPool) - 1)
        sActivityId = keeper.DataKeeper.lActivityPool[iPosition]
        del keeper.DataKeeper.lActivityPool[iPosition]
        keeper.DataKeeper.mActivityIdStaticIdList[sActivityId] = [sStaticId,keeper.DataKeeper.iMinuteTimeout - 1]
        return sActivityId
    else:
        sActivityId = "".join(random.sample([x for x in string.digits], 6))
        if sActivityId in keeper.DataKeeper.mActivityIdStaticIdList:
            keeper.DataKeeper.bActivityConflict = True
            iPosition = random.randint(0,len(keeper.DataKeeper.lActivityPool) - 1)
            sActivityId = keeper.DataKeeper.lActivityPool[iPosition]
            del keeper.DataKeeper.lActivityPool[iPosition]
            keeper.DataKeeper.mActivityIdStaticIdList[sActivityId] = [sStaticId, keeper.DataKeeper.iMinuteTimeout - 1]
            return sActivityId
        else:
            keeper.DataKeeper.mActivityIdStaticIdList[sActivityId] = [sStaticId,keeper.DataKeeper.iMinuteTimeout - 1]
            return sActivityId