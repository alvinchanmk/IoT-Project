import os

import requests

# url = 'http://localhost:5000/api'
# data = {'key': 'value'}
# response = requests.post(url, json=data)
#
# print(response.json())

def get_models():
    url = 'http://192.168.50.23:5000/get_models'
    response = requests.get(url)
    if response.status_code == 200:
        print('models retrieved successfully')
        print(response.text)
    else:
        print('Failed to models retrieved')
        print(response.text)

def get_image(filename):
    url = 'http://192.168.50.23:5000/get_image/'
    response = requests.get(url+filename)
    if response.status_code == 200:
        with open(filename, 'wb') as f:
            f.write(response.content)
        print('Image saved successfully')
    else:
        print('Failed to download image')

def upload_image(filename):
    url = 'http://192.168.50.23:5000/upload'
    files = {'file': open(filename, 'rb')}
    response = requests.post(url, files=files)

    if response.status_code == 200:
        print('Image uploaded successfully')
        print(response.text)
    else:
        print('Failed to upload image')
        print(response.text)


def upload_train(filename):
    url = 'http://192.168.50.23:5000/upload_train'
    files = {'file': open(filename, 'rb')}

    model = {'model': 'disney.pt'}
    response = requests.post(url, files=files,data =model)

    if response.status_code == 200:
        print('Image uploaded successfully')
        print(response.text)
    else:
        print('Failed to upload image')
        print(response.text)


if __name__ == "__main__":
    # get_models()
    file_path = "to_upload/arnold.jpg"
    upload_train(file_path)
    # print(os.getcwd())
    # get_image("arnold.jpg")