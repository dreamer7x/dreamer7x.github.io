import os

def key_get():
    try:
        file = open('key.txt','r')
        key = file.read()
        key = eval(key)
        file.close()
    except:
        file = open('key.txt','w')
        key = os.urandom(24)
        file.write(str(key))
        file.close()
    return key

def users_get():
    maps = {}
    try:
        file = open('users.txt','r')
        data = file.read()
        maps = eval(data)
        file.close()
    except:
        file = open('users.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    return maps

def users_save(maps):
    try:
        file = open('users.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    except:
        print('用户信息保存失败')
    return

def questions_get():
    maps = {}
    try:
        file = open('questions.txt','r')
        data = file.read()
        maps = eval(data)
        file.close()
    except:
        file = open('questions.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    return maps

def questions_save(maps):
    try:
        file = open('questions.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    except:
        print('问题保存失败')
    return

def answers_save(maps):
    try:
        file = open('answers.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    except:
        print('评论保存失败')
    return

def answers_get():
    maps = {}
    try:
        file = open('answers.txt','r')
        data = file.read()
        maps = eval(data)
        file.close()
    except:
        file = open('answers.txt','w')
        data = str(maps)
        file.write(data)
        file.close()
    return maps

def find_phone(username,users):
    for i in users.keys():
        if username == users[i][0]:
            return i
    return

def find_questions(username,questions):
    lis = []
    for i in questions.keys():
        if questions[i][1] == username:
            lis.append(i)
    return lis

def create_string(users,questions):
    lis = [[],[]]
    for i in users.keys():
        lis[0].append(users[i][0])
    for i in questions.keys():
        lis[1].append(i)
    print(lis)
    return lis

def search(key,lis):
    print(key)
    lis1 = []
    lis2 = []
    status = False
    m = len(key)
    for i in lis[0]:
        n = 0
        for j in i:
            if key[n] == j:
                n = n + 1
            if m == n:
                lis1.append(i)
                break
    for i in lis[1]:
        n = 0
        for j in i:
            if key[n] == j:
                print(j)
                n = n + 1
            if m == n:
                lis2.append(i)
                break
    lis = [lis1,lis2]
    print(lis)
    return lis