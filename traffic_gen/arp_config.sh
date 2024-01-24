#!/bin/bash

response=$(curl -X POST -H "Content-Type: application/json" -d '[

    {
        "ipv4Address": "10.0.0.1",
        "datapathId": "00:00:00:00:00:01",
        "outPortNumber": "2"
    },
    {
        "ipv4Address": "10.0.0.2",
        "datapathId": "00:00:00:00:00:02",
        "outPortNumber": "2"
    },
    {
        "ipv4Address": "10.0.0.3",
        "datapathId": "00:00:00:00:00:02",
        "outPortNumber": "3"
    }
]' http://127.0.0.1:8080/sdnlab/iptable)

echo "Response: $response"
