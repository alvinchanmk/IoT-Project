import os
import time

from flask import Flask, request, jsonify, send_file

app = Flask(__name__)

@app.route('/', methods=['GET'])
def hello():
    return 'Hello, world!'

@app.route('/get_models', methods=['GET'])
def get_models():
    path = r"C:\Users\Alvin\Desktop\iot\proj\JoJoGAN-Training-Windows\models"
    models_to_exclude = ["restyle_psp_ffhq_encode.pt","shape_predictor_68_face_landmarks(1).dat.bz2","dlibshape_predictor_68_face_landmarks.dat","e4e_ffhq_encode.pt","stylegan2-ffhq-config-f.pt"]
    model_list = {}
    all_models = os.listdir(path)
    i = 0
    for model in all_models:
        if model not in models_to_exclude:
            model_list[i] = model
            i +=1
    model_list = jsonify(model_list)
    return model_list



@app.route('/api', methods=['POST'])
def api():
    data = request.get_json()
    response_data = {'received_data': data}
    print(response_data)
    return jsonify(response_data)

@app.route('/upload_train', methods=['POST'])
def upload_train():
    file = request.files['file']
    file.save('incoming_files/to_train/' + file.filename)
    print(f"upload from {request.remote_addr} @ {time.ctime(time.time())}")

    return {'status': 'success'}

@app.route('/upload_filter', methods=['POST'])
def upload_filter():
    # model = request.data
    file = request.files['file']
    model_to_use = request.form['model']
    # request.url
    file.save(f'incoming_files/{model_to_use}/' + file.filename)
    print(f"upload from {request.remote_addr} @ {time.ctime(time.time())}")
    print(model_to_use)
    return {'status': 'success'}



@app.route('/get_image/<param>')
def get_image(param):
    outbound_image_path = r"C:\Users\Alvin\Desktop\iot\proj\web_server\to_upload"
    # Path to the image file on disk
    image_path = os.path.join(outbound_image_path,param)
    # Convert the image to bytes and create a response object

    try:
        return send_file(image_path, mimetype='image/jpeg')
    except:
        pass

if __name__ == '__main__':
    app.run(host ="192.168.142.1",port=5000)
