for i in 1 2 3 4 5
do
   response=$(curl --write-out '%{http_code}' --silent --output /dev/null "https://foaas-challenge.herokuapp.com/message?client_id=1234")
   echo "Response should be 200 and it's $response"
done

echo "Waiting 10 seconds"
sleep 10

response=$(curl --write-out '%{http_code}' --silent --output /dev/null "https://foaas-challenge.herokuapp.com/message?client_id=1234")
echo "Response should be 200 and it's $response"