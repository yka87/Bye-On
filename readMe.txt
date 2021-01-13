#########################################################################################################
Project 'Bye-On'
#########################################################################################################

- This Android app is for people suffering from pet loss.
- A user can upload her deceased pet's photo to the server after some image processing.
  Then the app sends photo notifications of other users' beloved pets randomly from the storage, which consists of user's uploaded pet images.
- Rather than only missing the user's pet, empathizing and grieving with many people in the same boat would help cope with the pet loss.
  This app could create a virtual community for the bereaved by pet loss.

##########################################################################################################################
2. With the Java base, C++ and Python were additionally used.
##########################################################################################################################

1) C++ was used to speed up processing images and benefit from the abundant functions in the OpenCV C++ library 
   in the Android environment. Refer to ImageActivity(app/src/main/java/com/example/opencv/ImageActivity.java), native-lib.cpp (app/src/main/cpp/native-lib.cpp) files. 
   The setting was done in the CMakeLists.txt and sdk imported module.

2) Firebase_admin and Pyrebase libraries in Python are versatile tools to handle the Firebase server storage data. 
    XmlRpc Server was created on the Python side that has a function to send a notification to the app,
    and the Java client calls the function by requesting the Python server.
    Refer to the XmlPpc_Server.py, FCMManager.py (/sampledata). And in Java side, CallServerWorker.java, 
    MyFirebaseMessagingService.java class (app/src/main/java/com/example/opencv/utils/) and NotifyActivity. 

##########################################################################################################################
3. Not yet to be deployed
##########################################################################################################################

- For now, the app is in progress and yet to be deployed. The current version deals with a single device and a single user, 
  and the server storage capacity is limited. Functions for multiple user handling are in progress and will be deployed in the future.
