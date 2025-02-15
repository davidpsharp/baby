
@rem simple Windows batch file to use python as a webserver
@rem (when you've torn your hair out at chocolatey and the general state of windows package mgrs)
@rem script intended to be run from the root of the repo

@rem change current working directory to be the location of this script
@rem cd /D "%~dp0"

@rem install the index.html in target folder
copy src\test\online_test\index.html target\index.html

@rem launch the web browser which will retry to connect to the web server which we start in the next step
start http://localhost:8080

@rem launch the web server, serving the root of the target folder
python3 -m http.server 8080 -d .\target\