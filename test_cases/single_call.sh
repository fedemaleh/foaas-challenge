response=$(curl --write-out '%{http_code}' --silent --output /dev/null "localhost:8080/message?client_id=1234")
echo "Response should be 200 and it's $response"
