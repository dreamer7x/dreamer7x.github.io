
#用以存储所有的静态配置资源
class ParameterKeeper:
    sEmailContentStart = "你正在尝试登录学习社区 您的验证码为 "
    sEmailContentEnd = " 若该登录操作并非来源于您当前设备,请尝试与官方进行联系"

    database_manager_name = ""
    database_manager_password = ""
    database_name = ""

class DataKeeper:
    #存储验证码信息，对于已经发送的验证码 以 email checkData timeout
    mEmailCheckDataList = {}
    #动态码 静态码索引列表
    mActivityIdStaticIdList = {}