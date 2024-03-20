- start first ServiceDiscoveryApplication and GatewayApplication
- after that start ServiceApplication (make sure redis instance is running on port 6379 or change application.yml)
- update port from gateway at com.example.Client#url (check startup logs for correct port) and start client application 
  it might be required to start it several times until the error occurs
