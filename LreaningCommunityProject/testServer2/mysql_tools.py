import keeper
import pymysql

# 权限列表:
# identity_sign_manager
# attentions_fans_manager
# identity_chat_manager
# identity_friends_manager
# identity_trends_manager
# account_manager
# chat_manager
# trends_manager

######################################################################################################################

# identity_sign_manager

######################################################################################################################

def get_identity_sign(sStaticId): # String 类型
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_sign_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_sign where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    return cCursor.fetchone()

def get_many_identity_signs(sStaticIds): # String 数组 类型
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_sign_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_sign where sStaticId = ' + str(sStaticIds[0])
    for sStaticId in sStaticIds[1:]:
        sSql = sSql + ' or sStaticId = ' + str(sStaticId)
    cCursor.execute(sSql)
    return cCursor.fetchmany(len(sStaticIds))

def update_identity_sign_clock_in(sStaticId,sClockIn):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_sign_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = "update identity_sign set sClockIn = \'" + sClockIn + "\' where sStaticId = " + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

######################################################################################################################

# identity_attentions_fans

######################################################################################################################

def get_identity_attentions_fans(sStaticId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='attentions_fans_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_attentions_fans where sStaticId = \'' + sStaticId + '\''
    cCursor.execute(sSql)
    return cCursor.fetchone()

def get_many_identity_attentions_fans(sStaticIds):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='attentions_fans_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_attentions_fans where sStaticId = \'' + sStaticIds[0] + '\''
    for sStaticId in sStaticIds:
        sSql = sSql + ' or sStaticId = \'' + sStaticId + '\''
    cCursor.execute(sSql)
    lRespond = cCursor.fetchmany(len(sStaticIds))
    return lRespond

######################################################################################################################

# identity_chat

######################################################################################################################

def get_identity_chat(sStaticId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_chat where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    return cCursor.fetchone()

def update_identity_chat_passive_chat(sStaticId,sNewPassiveChat,sPassiveChat,sOldPassiveChat,sChatOrder):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewPassiveChat = \'' + sNewPassiveChat \
           + '\',sPassiveChat = \'' + sPassiveChat \
           + '\',sOldPassiveChat = \'' + sOldPassiveChat \
           + '\',sChatOrder = \'' + sChatOrder \
           + '\' where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_chat_driving_chat(sStaticId,sNewDrivingChat,sDrivingChat,sOldDrivingChat,sChatOrder):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewDrivingChat = \'' + sNewDrivingChat \
           + '\',sDrivingChat = \'' + sDrivingChat \
           + '\',sOldDrivingChat = \'' + sOldDrivingChat \
           + '\',sChatOrder = \'' + sChatOrder \
           + '\' where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_chat_new_passive_chat(sStaticId,sNewPassiveChat):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewPassiveChat = \'' + sNewPassiveChat \
           + '\' where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_chat_new_old_passive_chat(sStaticId,sNewPassiveChat,sOldPassiveChat,sChatOrder): # 更新接收目标数据库 至 2
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewPassiveChat = \'' + sNewPassiveChat \
           + '\',sOldPassiveChat = \'' + sOldPassiveChat \
           + '\',sChatOrder = \'' + sChatOrder \
           + '\' where sStaticId =' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_chat_new_driving_chat(sStaticId,sNewDrivingChat):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewDrivingChat = \'' + sNewDrivingChat \
           + '\' where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_chat_new_old_driving_chat(sStaticId,sNewDrivingChat,sOldDrivingChat,sChatOrder): # 更新接受目标数据库 至 1、
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_chat set sNewDrivingChat = \'' + sNewDrivingChat \
           + '\',sOldDrivingChat = \'' + sOldDrivingChat \
           + '\',sChatOrder = \'' + sChatOrder \
           + '\' where sStaticId =' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

######################################################################################################################

# identity_friends

######################################################################################################################

def get_identity_friends(sStaticId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_friends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_friends where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    return cCursor.fetchone()

######################################################################################################################

# identity_trends

######################################################################################################################

def get_identity_trends(sStaticId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_trends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from identity_trends where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    return cCursor.fetchone()

def update_identity_trends_trends(sStaticId,sTrendsId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_trends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_trends set sTrendsId = \'' + sTrendsId \
           + '\' where sStaticId = ' + sStaticId
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_identity_trends_New_Old(sStaticId,sNewTrendsId,sOldTrendsId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='identity_trends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update identity_trends set sNewTrendsId = \'' + sNewTrendsId \
           + '\',sOldTrendsId = \'' + sOldTrendsId \
           + '\' where sStaticId = \'' + sStaticId + '\''
    cCursor.execute(sSql)
    conMysqlConnection.commit()

######################################################################################################################

# trends

######################################################################################################################

def get_trends(sTrendId):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='trends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from trends where sTrendsId = ' + sTrendId
    cCursor.execute(sSql)
    return cCursor.fetchone()

def get_many_trends(lTrendIds):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='trends_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sTrendId = lTrendIds[0]
    sSql = 'select * from trends where sTrendsId = ' + sTrendId
    for sTrendId in lTrendIds[1:]:
        sSql = sSql + ' or sTrendId = ' + sTrendId
    cCursor.execute(sSql)
    return cCursor.fetchmany()

######################################################################################################################

# chat

######################################################################################################################

def get_chat(sStaticId1,sStaticId2):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from chat where sStaticId1 = ' + sStaticId1 + ' and sStaticId2 = ' + sStaticId2
    cCursor.execute(sSql)
    return cCursor.fetchone()

def get_chats1(sStaticId,sStaticId1s):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'select * from chat where sStaticId1 = ' + sStaticId + ' and ('
    sStaticId1 = sStaticId1s[0]
    sSql = sSql + 'sStaticId2 = ' + str(sStaticId1)
    for sStaticId1 in sStaticId1s[1:]:
        sSql = sSql + ' or sStaticId2 = ' + str(sStaticId1)
    sSql = sSql + ')'
    cCursor.execute(sSql)
    lRespond1 = cCursor.fetchmany(len(sStaticId1s))
    return lRespond1

def get_chats2(sStaticId,sStaticId2s):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sStaticId2 = sStaticId2s[0]
    sSql = 'select * from chat where ' + '(sStaticId1 = ' + sStaticId2
    for sStaticId2 in sStaticId2s[1:]:
        sSql = sSql + ' or sStaticId1 = ' + sStaticId2
    sSql = sSql + ')'
    sSql = sSql + ' and sStaticId2 = ' + sStaticId
    cCursor.execute(sSql)
    lRespond2 = cCursor.fetchmany(len(sStaticId2s))
    return lRespond2

def update_new_chat(sStaticId1,sStaticId2,sNewChat):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update chat set iTimeout = 30,sNewChat = \'' + sNewChat \
           + '\' where sStaticId1 = ' + sStaticId1 \
           + ' and sStaticId2 = ' + sStaticId2
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def update_old_chat(sStaticId1,sStaticId2,sOldChat):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'update chat set sNewChat = \'0\',sOldChat = \'' + sOldChat \
           + '\' where sStaticId1 = ' + sStaticId1 \
           + ' and sStaticId2 = ' + sStaticId2
    cCursor.execute(sSql)
    conMysqlConnection.commit()

def insert_chat(sStaticId1,sStaticId2,sNewChat):
    conMysqlConnection = pymysql.connect(host="localhost",
                                         user='chat_manager',
                                         password='123',
                                         database='test',
                                         charset='utf8')
    cCursor = conMysqlConnection.cursor()
    sSql = 'insert chat(sStaticId1,sStaticId2,sNewChat,sOldeChat,iTimeout) values(' \
           + sStaticId1 + ',' + sStaticId2 + ',\'' + sNewChat + '\',\'[]\',30)'
    cCursor.execute(sSql)
    conMysqlConnection.commit()