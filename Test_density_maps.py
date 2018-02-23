# tests.py

import unittest
import json
import Density_map_data

class TestDensityMapsMethods(unittest.TestCase):

    def test_Antwerp(self):
    	json_data = Density_map_data.getCoordinates(0)
    	js = json.loads(json_data)
    	self.assertTrue(js[0]["coordinates"] is not None)
    def test_Helsinki(self):
    	json_data = Density_map_data.getCoordinates(1)
    	js = json.loads(json_data)
    	self.assertTrue(js[0]["coordinates"] is not None)

if __name__ == '__main__':
    import xmlrunner
    unittest.main(testRunner=xmlrunner.XMLTestRunner(output='test-reports'))

# if __name__ == '__main__':
#     unittest.main()