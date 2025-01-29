
# Mac OS shell script to copy HTML file to target, open web browser and launch a Python3 web server to test out cheerpj 

cp index.html ../../../target/index.html

open -a open -a Google\ Chrome http://localhost:8080

python3 -m http.server 8080 -d ../../../target/

