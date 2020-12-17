import smtplib
from email.mime.text import MIMEText

'''

send_email 方法要求将以下六行信息顺序包装为列表作为参数，从而实现邮件发送操作

sEmailServerUrl = 'smtp.qq.com'
sEmailServerPort = 465
sEmailSend = '1797726938@qq.com'
sEmailSendPassword = 'xdmzauzpjsqgbaej'  # 邮箱密码：需要使用授权码
sEmailReceiver = '1797726938@qq.com'  # 收件人，多个收件人用逗号隔开
sEmailContent = ""
sEmailTitle = ""
sEmailSendName = ""
sEmailReceiverName = ""

lEmailData = MIMEText('这是发用的邮件内容')
lEmailData['Subject'] = '这是邮件主题'
lEmailData['From'] = sEmailSendName  # 发件人
lEmailData['To'] = sEmailReceiverName  # 收件人；[]里的三个是固定写法，别问为什么，我只是代码的搬运工

# conSmtpConnection = smtplib.SMTP(mailserver, port=25)  # 连接邮箱服务器，smtp端口:25
conSmtpConnection = smtplib.SMTP_SSL('smtp.qq.com', port=465)  # QQ邮箱的服务器,端口
conSmtpConnection.login(sEmailSend,sEmailSendPassword)  # 登录邮箱
conSmtpConnection.sendmail(sEmailSend, sEmailReceiver, lEmailData.as_string())  # 发送邮箱地址，接收邮箱地址，邮件的字符串形式
conSmtpConnection.quit()  # 退出smtp

'''

def send_ssl_email(lEmailPackage):

    lEmailData = MIMEText(lEmailPackage[5])
    lEmailData['Subject'] = lEmailPackage[6]
    lEmailData['From'] = lEmailPackage[7]
    lEmailData['To'] = lEmailPackage[8]

    conSmtpConnection = smtplib.SMTP_SSL(lEmailPackage[0],port = lEmailPackage[1])
    conSmtpConnection.login(lEmailPackage[2],lEmailPackage[3]);
    conSmtpConnection.sendmail(lEmailPackage[2],lEmailPackage[4],lEmailData.as_string())
    conSmtpConnection.quit()

'''

相当于将上式sEmailSendPassword删去

'''

def send_email(lEmailPackage):
    lEmailData = MIMEText(lEmailPackage[4])
    lEmailData['Object'] = lEmailPackage[5]
    lEmailData['From'] = lEmailPackage[6]
    lEmailData['To'] = lEmailPackage[7]

    conSmtpConnection = smtplib.SMTP(lEmailPackage[0], port=lEmailPackage[1])
    conSmtpConnection.login(lEmailPackage[2]);
    conSmtpConnection.sendmail(lEmailPackage[2], lEmailPackage[3], lEmailData.as_string())
    conSmtpConnection.quit()