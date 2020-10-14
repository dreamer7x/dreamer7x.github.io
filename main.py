from flask import Flask,url_for,redirect,session,render_template,request
import tools
from datetime import datetime

app = Flask(__name__)
key = tools.key_get()
app.config['SECRET_KEY'] = key

users = tools.users_get()
questions = tools.questions_get()
answers = tools.answers_get()
invitation = ''
searchlis = []

@app.route('/',methods = ['GET','POST'])
def title():
    global users
    global questions
    global searchlis
    if request.method == 'GET':
        return render_template('title.html',users = users,session = session,questions = questions)
    else:
        if 'invitation' in request.form.keys():
            phone = tools.find_phone(request.form.get('invitation'),users)
            users[phone][4][0].append(session['phone'])
            tools.users_save(users)
            return render_template('search.html', users=users, questions=questions, session=session, lis=searchlis)
        else:
            searchlis = tools.create_string(users, questions)
            key = request.form.get('key')
            searchlis = tools.search(key, searchlis)
            return render_template('search.html', users=users, questions=questions, session=session, lis=searchlis)

@app.route('/login/',methods = ['GET','POST'])
def login():
    global users
    if request.method == 'GET':
        return render_template('login.html',users = users,session = session)
    else:
        phone = request.form.get('phone')
        password = request.form.get('password')
        for i in users.keys():
            if i == phone:
                if users[i][1] == password:
                    session.permanent = True
                    session['phone'] = phone
                    return redirect(url_for('title'))
                else:
                    break
        return render_template('login.html',users = users,session = session)

@app.route('/regist/',methods = ['GET','POST'])
def regist():
    global users
    if request.method == 'GET':
        return render_template('regist.html',users = users,session = session)
    else:
        phone = request.form.get('phone')
        username = request.form.get('username')
        password = request.form.get('password')
        twice = request.form.get('twice')
        introduce = request.form.get('introduce')
        if twice == password:
            hidephone = [False]
            friends = [[],[],[]]
            users[phone] = [username,password,introduce,hidephone,friends]
            tools.users_save(users)
            return render_template('regist.html', users=users, session=session)
        else:
            return render_template('regist.html',users = users,session = session)

@app.route('/logout/')
def logout():
    session.clear()
    return redirect(url_for('login'))

@app.route('/question/',methods = ['GET','POST'])
def question():
    global questions
    global users
    global answers
    if 'phone' in session.keys():
        if request.method == 'GET':
            return render_template('question.html',users = users,session = session)
        else:
            title = request.form.get('title')
            content = request.form.get('content')
            now = datetime.now()
            now = now.strftime('%Y-%m-%d %H:%M:%S')
            username = users[session['phone']][0]
            questions[title] = [content,username,now]
            tools.questions_save(questions)
            answers[title] = []
            tools.answers_save(answers)
            return redirect(url_for('answer',question = title))
    else:
        return redirect(url_for('login'))

@app.route('/answer/<question>/',methods = ['GET','POST'])
def answer(question):
    global questions
    global users
    global answers
    num = len(answers[question])
    if request.method == 'GET':
        return render_template('answer.html',users = users,session = session,question = question,questions = questions,answers = answers,num = num)
    else:
        content = request.form.get('content')
        phone = session['phone']
        now = datetime.now()
        now = now.strftime('%Y-%m-%d %H:%M:%S')
        answers[question] = []
        answers[question].append([content,phone,now])
        tools.answers_save(answers)
        return render_template('answer.html',users = users,session = session,question = question,questions = questions,answers = answers,num = num)

@app.route('/change/',methods = ['GET','POST'])
def change():
    global users
    if request.method == 'GET':
        return render_template('change.html',users = users,session = session)
    else:
        newpassword = request.form.get('newpassword')
        twice = request.form.get('twice')
        if newpassword == twice:
            users[session['phone']][1] = newpassword
            return redirect(url_for('title'))
        else:
            return render_template('change.html',users = users,session = session)

@app.route('/set/',methods = ['GET','POST'])
def set():
    global users
    if request.method == 'GET':
        return render_template('set.html',users = users,session = session)
    else:
        hide = request.form.get('hide')
        print(hide)
        if hide == 'True':
            users[session['phone']][3][0] = True
            tools.users_save(users)
        return render_template('set.html',users = users,session = session)

@app.route('/friend/',methods = ['GET','POST'])
def friend():
    global users
    if 'phone' in session.keys():
        return render_template('friend.html',users = users,session = session)
    else:
        return redirect(url_for('login'))

@app.route('/friendchange/<nature>/<agreement>/<content>')
def friendchange(nature,agreement,content):
    global users
    nature = eval(nature)
    agreement = eval(agreement)
    if nature == 1:
        phone = tools.find_phone(content,users)
        if agreement == 1:
            users[session['phone']][4][2].append(phone)
            users[phone][4][2].append(session['phone'])
            users[session['phone']][4][0].remove(phone)
            tools.users_save(users)
            return redirect(url_for('friend'))
        else:
            users[session['phone']][4][0].remove(phone)
            tools.users_save(users)
            return redirect(url_for('friend'))
    else:
        if agreement == 1:
            users[session['phone']][4][1].remove(content)
            tools.users_save(users)
            return redirect(url_for('answer',question = content))
        else:
            users[session['phone']][4][1].remove(content)
            tools.users_save(users)
            return redirect(url_for('friend'))

@app.route('/user/<username>',methods = ['GET','POST'])
def user(username):
    global users
    if 'phone' in session.keys():
        if request.method == 'GET':
            phone = tools.find_phone(username,users)
            return render_template('user.html',users = users,session = session,phone = phone)
        else:
            phone = tools.find_phone(request.form.get('invitation'),users)
            users[phone][4][0].append(session['phone'])
            tools.users_save(users)
            return render_template('user.html',users = users,session = session,phone = phone)
    else:
        return redirect(url_for('login'))

@app.route('/publish/')
def publish():
    global users
    global questions
    if 'phone' in session.keys():
        username = users[session['phone']][0]
        lis = tools.find_questions(username,questions)
        return render_template('publish.html',users = users,session = session,questions = questions,lis = lis)
    else:
        return redirect(url_for('login'))

@app.route('/delete_que/<question>/')
def delete_que(question):
    global questions
    questions.pop(question)
    tools.questions_save(questions)
    return redirect(url_for('publish'))

@app.route('/delete_fri/<username>/')
def delete_fri(username):
    global users
    phone = tools.find_phone(username,users)
    users[phone][4][2].remove(session['phone'])
    users[session['phone']][4][2].remove(phone)
    tools.users_save(users)
    return redirect(url_for('friend'))

@app.route('/invite/<question>',methods = ['GET','POST'])
def invite(question):
    global users
    global invitation
    if request.method == 'GET':
        invitation = question
        return render_template('invite.html',users = users,session = session)
    else:
        for i in request.form.keys():
            phone = tools.find_phone(i,users)
            users[phone][4][1].append(invitation)
        return redirect(url_for('publish'))

if __name__ == '__main__':
    app.run(host = '0.0.0.0',port = 5000,debug = True)