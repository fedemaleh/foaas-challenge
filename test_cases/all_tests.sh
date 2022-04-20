echo "Single Call Test Case"
./single_call.sh

echo "Waiting 11 seconds for quota to be restored (extra second for the sliding window algorithm)"
sleep 11

echo "5 Calls Test Case"
./5_calls.sh

echo "Waiting 11 seconds for quota to be restored (extra second for the sliding window algorithm)"
sleep 11

echo "6 Calls Test Case"
./6_calls.sh

echo "Waiting 11 seconds for quota to be restored (extra second for the sliding window algorithm)"
sleep 11

echo "6 Calls Different User Test Case"
./6_calls_different_user.sh

echo "Waiting 11 seconds for quota to be restored (extra second for the sliding window algorithm)"
sleep 11

echo "6 Calls With Wait Test Caese"
./6_calls_with_wait.sh