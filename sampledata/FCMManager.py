# Code based on [https://github.com/thisbejim/Pyrebase/] [https://medium.com/devmins/firebase-cloud-messaging-api-with-python-6c0755e41eb5]
import firebase_admin
import pyrebase
import requests
import json
import random
from firebase_admin import credentials

# This script manages Firebase Service account information to get images source from the server storage,
# then send data payload to the Java client, so the Android device can receive photo notification.

serverToken = "AAAAdKj5nnY:APA91bFt4DcPUFPNsVTDmL-tqetLExIXUkx4pBKQaIsU5K6qfVzTat5yTmlTiyAEbhlEXAg71V-2tnHK2tDm80gdbB6xZWn8D95fgVU1JRuV2gp3T3deIkDYwgg72BFyHOB9y5t_AZEY"
# deviceToken =     "fU-sFWEcQzmj5gsIA0jZnL:APA91bHQfvMPOSZPrx6kXUTUGdeNXDojQaM5u1sftNI-RIQztxc2Lo8D22q2hEPzKz1gUm0b7wHJhTfF2pr2oRL0DtFj8sA78nNNffuhcrbs8OAWWIhEJiZq3xHE1j-E7Owym5QzobOo"

config = {
    "apiKey": "AIzaSyB0F_1T_tBN1KGXzbqxacaAIHxi3MquyWk",
    "authDomain": "bye-on.firebaseapp.com",
    "databaseURL": "https://bye-on.firebaseio.com",
    "projectId": "bye-on",
    "storageBucket": "bye-on.appspot.com",
    "messagingSenderId": "501051137654",
    "appId": "1:501051137654:web:9a3cbfd7a9d88d653e0957",
    "measurementId": "G-T4QBY58CL4",
    "serviceAccount": "C:/Users/seape/AndroidStudioProjects/383polygot/sampledata/serviceAccountKey.json"
}

# Initialize firebase storage server by using given credential
cred = credentials.Certificate("C:/Users/seape/AndroidStudioProjects/383polygot/sampledata/serviceAccountKey.json")
firebase_admin.initialize_app(cred)
firebase = pyrebase.initialize_app(config)
initialized = False


# This function iterate image files in the server storage, then pick a random one, and send the url to the client
def request(deviceToken):
    storage = firebase.storage()
    files = storage.list_files()
    img_url_list = []
    for file in files:
        img_url_list.append(storage.child(file.name).get_url(None))

    img_url = random.choice(img_url_list)

    headers = {
        'Content-Type': 'application/json',
        'Authorization': 'key=' + serverToken,
    }

    body = {
        'notification': {'title': 'Bye-On',
                         'body': img_url
                         },
        'to':
            deviceToken,
    }

    response = requests.post("https://fcm.googleapis.com/fcm/send", headers=headers, data=json.dumps(body))
    print(response.status_code)
    print(img_url)
    print(response.json())
    return "succ"
