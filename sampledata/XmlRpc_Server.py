from xmlrpc.server import SimpleXMLRPCServer
from xmlrpc.server import SimpleXMLRPCRequestHandler
import FCMManager as fcm

# Restrict to a particular path.
class RequestHandler(SimpleXMLRPCRequestHandler):
    rpc_paths = ('/RPC2',)

# Create server and register function from FCMManager.py
with SimpleXMLRPCServer(('localhost', 8000), requestHandler=RequestHandler) as server:
    server.register_introspection_functions()

    server.register_function(fcm.request, 'notify')

    server.serve_forever()

    # def cap_function(string):
    #     return string.upper()
    # server.register_function(cap_function, 'cap')