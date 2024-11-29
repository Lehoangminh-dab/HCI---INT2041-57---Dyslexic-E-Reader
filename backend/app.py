from flask import Flask, request, jsonify, send_file
import services

app = Flask(__name__)

@app.route('/api/image', methods=['GET'])
def image():
    word = request.args.get('word')
    if word:
        img_filename = services.generate_image(word)
        try:
            return send_file(img_filename, mimetype='image/png')
        except Exception as e:
            return jsonify({"error": str(e)}), 500
    else:
        return jsonify({"error": "Missing 'word' parameter"}), 400

@app.route('/api/description', methods=['GET'])
def description():
    word = request.args.get('word')
    if word:
        description = services.generate_description(word)
        return jsonify({"description": description})
    else:
        return jsonify({"error": "Missing 'word' parameter"}), 400

@app.route('/api/pronunciation', methods=['GET'])
def pronunciation():
    word = request.args.get('word')
    if word:
        audio_filename = services.generate_pronunciation(word)
        try:
            return send_file(audio_filename, mimetype='audio/mp3')
        except Exception as e:
            return jsonify({"error": str(e)}), 500
    else:
        return jsonify({"error": "Missing 'word' parameter"}), 400

if (__name__ == '__main__'):
    app.run(host='0.0.0.0', port=5000)

