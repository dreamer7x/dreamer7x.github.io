import pymysql
from data_keeper import ParameterKeeper
'''
conMysqlConnnection = pymysql.connect(host = "localhost",user = 'user4',password = '123',database = 'test',charset = 'utf8')
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

import datetime

now = datetime.datetime.now().strftime('%Y %m %d %w')
print(now) # %H:%M:%S

import re

pattren = '(^9\\s)|(\\s9\\s)|(\\s9$)'
string = ' 9 1'
print(string[1:-1])
a = re.search(pattren,string).span()
print(a)

a = ((1,2),(2))
b = ((3),(4))
a = a + b

str = 'ni ni hao'
str = '<spa>nihao nihao'
a = str.split('<spa>')
print(a)

a = ''
if a == '':
    print('a 与 空相等')

a = str.split(' ')
print(a[1:])

b = "anc<spa>"
print(b[0:-5])

a = [1,2,3]
print(a[0:0])