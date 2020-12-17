#用以存储所有的静态配置资源
class ParameterKeeper:
    sEmailContentStart = "你正在尝试登录学习社区 您的验证码为 "
    sEmailContentEnd = " 若该登录操作并非来源于您当前设备,请尝试与官方进行联系"

    #mysql公式
    #mysql账户查询公式
    sMysqlSelectAccount = "select * from account where "
    #mysql账户添加公式
    sMysqlInsertAccount = "insert account(sEmail,sPassword,sActivityId,iTimeout) values"

class DataKeeper:
    #系统心跳
    iSecondTimeout = 0
    #系统运行时间]
    iMinuteTimeout = 0

    #存储验证码信息，对于已经发送的验证码 以 email checkData timeout
    mEmailCheckDataList = {}
    #动态码撞击激活
    bActivityConflict = False
    #动态码池
    lActivityPool = []
    #动态码 静态码索引列表
    mActivityIdStaticIdList = {}

