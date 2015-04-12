ShadowBlade slave (Host terminal) in Android
============================================
##Functions
###Send SMS
  * SessionName: "send_sms"<br>
  * Action: Send to all contacts in android host or send to assign numbers<br>

###Load Contacts
  * SessionName: "load_contact"<br>
  * Action: Load all contacts from android host

###Load SMS
  * SessionName: "upload_sms"<br>
  * Action: Load all SMS from android host

###Shell
  * SessionName: "shell"<br>
  * Action: Open a shell of android host

##How to add function
-------------------
###Create your SessionHandler
>New a class in package <br>
>>`com.android.sys.session.handler` <br>

>named as `<FunctionName>SessionHandler` (eg. YourSessionHandler), <br>
>>implement `NetworkSeessionManager.SessionHandler` interface<br>

>Add your code in this class<br>

###Add to NetworkSessionManager
>Add your SessionHandler instance into NetworkSessionManager by<br>
>>`NetworkSessionManager.addSessionHandler(SessionName,new YourSessionHandler())` <br>
