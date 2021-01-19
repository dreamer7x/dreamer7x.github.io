"""
作用:
    公共内存数据管理,例如用户的实时登录信息,这些需要服务器所有的服务模块达成共识的信息都要求存储在以下公共空间中

使用手册:
    启动服务器要求配置ParameterKeeper中的相关静态参数,这是服务器构建所需要的一些基本信息;
    (这里只需要输入一组管理员的用户名账号信息即可,正如init.py中所说的,要求后期将权限分开)

"""

# 用以存储所有的静态配置资源
class ParameterKeeper:
    # 这个是注册页面发送验证码时 要想用户展现的信息
    sEmailContentStart = "你正在尝试登录学习社区 您的验证码为 "
    sEmailContentEnd = " 若该登录操作并非来源于您当前设备,请尝试与官方进行联系"

    # 数据库管理员用户名
    database_manager_name = ""
    # 数据库管理员密码
    database_manager_password = ""
    # 数据库名
    database_name = ""
    # 数据库ip
    mysql_ip = ""

# 用以存储动态配置资源
class DataKeeper:
    #存储验证码信息，对于已经发送的验证码 以 email checkData timeout
    mEmailCheckDataList = {}
    #动态码 静态码索引列表
    mActivityIdStaticIdList = {}