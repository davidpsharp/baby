
# Mac OS shell script to copy HTML file to target, open web browser and launch a Python3 web server to test out cheerpj serving the root of target folder 
# Run this script from current working directory of: baby/src/test/online_test 

# change current working directory to be the location of this script
cd "$(dirname "$0")"

# install the index.html in target folder
cp index.html ../../../target/index.html

# launch the web browser which will retry to connect to the web server which we start in the next step
open -a open -a Google\ Chrome http://localhost:8080

# launch the web server, serving the root of the target folder
python3 -m http.server 8080 -d ../../../target/

