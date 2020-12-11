# Code based on [https://github.com/thisbejim/Pyrebase/] [https://medium.com/devmins/firebase-cloud-messaging-api-with-python-6c0755e41eb5]
import firebase_admin
import pyrebase
import requests
import json
import random

serverToken = "AAAAdKj5nnY:APA91bFt4DcPUFPNsVTDmL-tqetLExIXUkx4pBKQaIsU5K6qfVzTat5yTmlTiyAEbhlEXAg71V-2tnHK2tDm80gdbB6xZWn8D95fgVU1JRuV2gp3T3deIkDYwgg72BFyHOB9y5t_AZEY"
deviceToken = "dwbKG6z6RGqr07qVxCZqSM:APA91bFZAkiLkPlpwRr6YefCCvmcg0mKmpKm1tKapUbej64RNUYlxp_O0hvUiNu3Cu0JEhWVhgCuos5qWER7THPOGwM49dW-JdGKAI_abHQ8QdsAHeGYe6A3S3hdTeOM6pVRRwWQkY2G"

config = {
    "apiKey": "AIzaSyB0F_1T_tBN1KGXzbqxacaAIHxi3MquyWk",
    "authDomain": "bye-on.firebaseapp.com",
    "databaseURL": "https://bye-on.firebaseio.com",
    "projectId": "bye-on",
    "storageBucket": "bye-on.appspot.com",
    "messagingSenderId": "501051137654",
    "appId": "1:501051137654:web:9a3cbfd7a9d88d653e0957",
    "measurementId": "G-T4QBY58CL4",
    "serviceAccount": "C:/Users/seape/PycharmProjects/FCMText/serviceAccountKey.json"
};

default_app = firebase_admin.initialize_app()
firebase = pyrebase.initialize_app(config)
storage = firebase.storage()

img_url_list = []
files = storage.list_files()

for file in files:
    img_url_list.append(storage.child(file.name).get_url(None))

img_url = random.choice(img_url_list)

headers = {
    'Content-Type': 'application/json',
    'Authorization': 'key=' + serverToken,
}

body = {
    'notification': {'title': 'Sending push form python script',
                     'body': img_url
                     },
    'to':
        deviceToken,
}

def request():
    response = requests.post("https://fcm.googleapis.com/fcm/send", headers=headers, data=json.dumps(body))
    print(response.status_code)
    print(response.json())
    return "succ"

    # print(type(response.status_code))
    # k = int (response.status_code)
    # print(response.json())