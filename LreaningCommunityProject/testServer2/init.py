import pymysql
'''
作用:
    以下是数据库的初始化程序,用以创建服务器存储数据所要用到的表;
    
使用说明:
    1.首先要求打开mysql,使用root身份创建服务器要用到的数据库,创建语句如下:
    create database <数据库名> charset utf8;
    2.创建好数据库后,要创建一个数据库的主管,创建语句如下:
    create user "<管理者名>"@"%" identified by '<密码>';
    grant create,select,update,insert on <数据库名>.* to "<管理者名>"@"%";
    (创建完以上过程后不要关闭mysql,让它继续开着)
    3.将下列参数输入,就可以启动该初始化程序来构建完整的数据库了
    4.要启动服务器,需要到keeper中配置一般参数,包括:数据库主管名
    (这里我只采用了一个管理者来管理整个数据库,这是不严谨的,之后需要相关技术人员创建多个数据库,将权限分开)

附注:
数据库详细结构:
    数据库包括:
    account:                    记录用户的密码信息
    chat:                       记录聊天数据,与下文的identity_chat不同,这里记录的是聊天数据,而不是聊天数据的索引
    identity_attentions_fans:   记录个人关注与粉丝信息
    identity_chat:              记录个人聊天信息   
    identity_friends:           记录好友信息
    identity_sign:              记录个人基础信息,包括:用户名,格言等
    identity_trends:            记录个人动态信息,仅仅只是动态索引,而不是真正的动态数据 
    trends:                     记录动态数据
    trends_parameter            记录创建动态时的一般参数
'''

# 这里要求输入新建的数据库
database_name = "test02"
# 输入你创建的数据库管理者的用户名
database_manager_name = "test02_manager"
# 输入你创建的数据库管理者的密码
database_manager_password = "123456"
# 输入mysql数据库的所在IP地址,如果mysql数据库就在本机上,那么输入localhost即可
mysql_ip = "localhost"

# 在数据库中 创建表，如果已经创建过数据表,那么将执行失败
def create_tables():
    # 与mysql建立连接
    connection = pymysql.connect(
        host=mysql_ip,
        user=database_manager_name,
        password=database_manager_password,
        database=database_name,
        charset='utf8' # 默认使用的是utf8编码,数据库默认采用的也是utf8编码
    )
    # 获取游标
    cursor = connection.cursor()
    # 以下是表内容的创建语句
    sql01 = "create table chat(sStaticId1 smallint," \
            "sStaticId2 smallint," \
            "sNewChat varchar(100)," \
            "sOldChat varchar(1000)," \
            "iTimeout int)"
    sql02 = "create table identity_attentions_fans(sStaticId smallint," \
            "iAttentionsNumber int," \
            "iFansNumber int," \
            "sAttentionsStaticId varchar(1000)," \
            "sFansStaticId varchar(1000))"
    sql03 = "create table identity_chat(sStaticId smallint," \
            "sNewDrivingChat varchar(100)," \
            "sNewPassiveChat varchar(100)," \
            "sDrivingChat varchar(1000)," \
            "sPassiveChat varchar(1000)," \
            "sOldDrivingChat varchar(100)," \
            "sOldPassiveChat varchar(100)," \
            "sChatOrder varchar(200))"
    sql04 = "create table identity_friends(sStaticId smallint," \
            "sFriendsStaticId varchar(1000)," \
            "sOldNewFriendsStaticId varchar(100)," \
            "sNewFriendsStaticId varchar(100)," \
            "sBlackList varchar(1000)," \
            "sIntroduce varchar(1000))"
    sql05 = "create table identity_sign(sStaticId smallint," \
            "sUserName char(10)," \
            "sMotto char(30)," \
            "sClockIn char(31)," \
            "sPicturePosition char(10)," \
            "sMale char(1)," \
            "sBirthDay char(10))"
    sql06 = "create table identity_trends(sStaticId smallint," \
            "sTrendsId varchar(1000)," \
            "sCollectTrendsId varchar(1000)," \
            "sNewTrendsId varchar(1000)," \
            "sOldTrendsId varchar(1000))"
    sql07 = "create table trends(sTrendsId smallint," \
            "sStaticId smallint," \
            "sTitle char(20)," \
            "sText varchar(400)," \
            "sPicturePosition varchar(40)," \
            "iPraiseNumber int," \
            "iDiscussNumber int," \
            "sPraiseStaticId varchar(1000)," \
            "sDiscuss varchar(1000))"
    sql08 = "create table account(sEmail char(20)," \
            "sPassword char(20)," \
            "sActivityId char(6)," \
            "sStaticId smallint," \
            "iTimeout int)"
    sql09 = "create table trends_parameter(iTrendsNumber int)"
    cursor.execute(sql01)
    cursor.execute(sql02)
    cursor.execute(sql03)
    cursor.execute(sql04)
    cursor.execute(sql05)
    cursor.execute(sql06)
    cursor.execute(sql07)
    cursor.execute(sql08)
    connection.commit()

if __name__ == '__main__':
    create_tables()