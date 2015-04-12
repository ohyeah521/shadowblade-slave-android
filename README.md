ShadowBlade slave (Host terminal) in Android
============================================
Functions
---------
#Send SMS
SessionName: "send_sms"
Action: Send to all contacts in android host or send to assign numbers<br>

#Load Contacts
SessionName: "send_sms"
Action: Load all contacts from android host

#Load SMS
SessionName: "upload_sms"
Action: Load all SMS from android host

#Shell
SessionName: "shell"
Action: Open a shell of android host

How to add function
-------------------
#Create your SessionHandler
New a class in package com.android.sys.session.handler named as "<FunctionName>SessionHandler" (eg. YourSessionHandler), implement NetworkSeessionManager.SessionHandler interface<br>
Add your code in this class<br>
#Add to NetworkSessionManager
Call NetworkSessionManager.addSessionHandler(SessionName,new YourSessionHandler()) to add your SessionHandler instance into NetworkSessionManager

