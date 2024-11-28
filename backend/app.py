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

if (__name__ == '__main__'):
    app.run(debug=True)
