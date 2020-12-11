##########################################################################################################################
1. Goal of the Project 'Bye-On'
##########################################################################################################################

- This Android app is for people suffering from pet loss.
- A user can upload her deceased pet's photo to the server after some image processing.
  Then the app sends photo notifications of other users' beloved pets randomly from the storage, which consists of user's uploaded pet images.
- Rather than only missing the user's pet, empathizing and grieving with many people in the same boat would help cope with the pet loss.
  This app could create a virtual community for the bereaved by pet loss.
- For now, the app deals with a single device and a single user, and the server storage capacity is limited.
  Functions for multiple user handling are in progress and will be deployed in the future.


##########################################################################################################################
2. With the Java base, C++ and Python were additionally used.
##########################################################################################################################

a) C++ was used to speed up processing images and benefit from the abundant functions in the OpenCV C++ library in the Android environment.
  Also OpenCV C++ library function is more concise than implementation in Java.

    Java Native Interface was used to implement NDK so that the C++ library could be called in the Android Studio.
    refer to CameraActivity(com/example/opencv/CameraActivity.java), native-lib.cpp (src/main/cpp/native-lib.cpp).

b) Using PyFCM and Pyrebase libraries in Python are versatile tools to handle the Firebase server storage data,
 and send notifications to the app. Those offer more concise and convenient functions than API in Android.

    XmlRpc Server was created on the Python side that has a function to send a notification to the app,
    and the Java client calls the function by requesting the Python server.
    Refer to the XmlPpc_Server.py, and callXmlServer() function in the NotifyActivity in the android java class.


##########################################################################################################################
3. Deployment technology: Docker Compose + Container
##########################################################################################################################
I am sorry, I failed to build docker image of this android project.
I thought I could make it after successing on  a simple app



##########################################################################################################################
4. Where to check
##########################################################################################################################
