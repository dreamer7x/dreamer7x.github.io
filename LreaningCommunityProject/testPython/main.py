import pymysql
from data_keeper import ParameterKeeper
'''
conMysqlConnnection = pymysql.connect(host = "localhost",user = 'user02',password = '123',database = 'test',charset = 'utf8')
cursor = conMysqlConnnection.cursor()
sql = "insert account(sEmail,sPassword,sActivity,iTimeout) values('')"
cursor.execute(sql)
conMysqlConnnection.commit()
data = cursor.fetchone()
ParameterKeeper.sSelectAccount = "select * from account where sStaticId = '000002'"
sql = ParameterKeeper.sSelectAccount
cursor.execute(sql)
data2 = cursor.fetchone()
if data == None:
    print("未查找到")
else:
    print(data)

if data2 == None:
    print("未查找到")
else:
    print(data2)

maps = {1:1,3:2}
for i in maps:
    print(i)

s = 1
print(str(s))
'''

