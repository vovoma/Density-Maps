#!flask/bin/python
from flask import Flask, request, abort
import Density_map_data

import asyncio

from wasp_eureka import EurekaClient

# no spaces or underscores, this needs to be url-friendly
app_name = 'DensityMap'

port = 5000
# This needs to be an IP accessible by anyone that
# may want to discover, connect and/or use your service.
ip = '127.0.0.1'
my_eureka_url = 'http://us1.fiwoo.eu:3331/'
loop = asyncio.get_event_loop()


eureka = EurekaClient(app_name, port, ip, eureka_url=my_eureka_url,
                      loop=loop)

async def main():
    # Presuming you want your service to be available via eureka
    result = await eureka.register()
    assert result, 'Unable to register'

    # You need to provide a heartbeat to renew the lease,
    # otherwise the eureka server will expel the service.
    # The default is 90s, so any time <90s is ok
    while True:
        await asyncio.sleep(67)
        await eureka.renew()

try:
    error = loop.run_until_complete(main())
except:
    e=1
    
app = Flask(__name__)

@app.route('/api/bi/density', methods=['GET'])
def heatmaps():
    json = Density_map_data.getCoordinates('0')
    return json

@app.route('/api/bi/density/swagger', methods=['GET'])
def get_swagger():
    swagger_json = open('BI_density_mapsV1.json', 'r').read()
    return swagger_json

if __name__ == '__main__':
    app.run(debug=True,host='0.0.0.0')
