class WeightedPortAndTrafficTypeLoadBalancer:

    def init(self, clients):
        clients = clients
        weights = {(port, traffic_type): 1 for port in range(1, 65536) for traffic_type in ['HTTP', 'HTTPS', 'FTP']}

def set_weights(weights):
    if len(weights) == len(weights):
        weights = weights
    else:
        raise Error()

def balance_traffic(self, port, traffic_type, request):
    client = self.choose_client(port, traffic_type)
    forward_request(client, request)

def choose_client(self, port, traffic_type):
    total_weight = sum(weight in weights)
    rand_value = randomInt(0, total_weight)
    current_weight = 0

    for key, weight in self.weights:
        current_weight += weight
        if rand_value < current_weight:
            return self.clients[self.clients.index(key)]

def forward_request(self, client, request):
    FORWARD_REQUEST
