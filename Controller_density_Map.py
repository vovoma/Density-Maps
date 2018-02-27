#!flask/bin/python
from flask import Flask, request, abort
import Density_map_data


app = Flask(__name__)

@app.route('/api/bi/density', methods=['GET'])
def heatmaps():
    json = Density_map_data.getCoordinates('0')
    return json

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')