# MCChecker
MCChecker is a Minecraft account authentication checker written in Java.

## Features
```  
Multi-threading: Run multiple threads at once for checking accounts faster  
Proxy Downloader: Automatically grab a large list of proxies to use, without having to collect them yourself  
HTTP and SOCKS proxies: Supports both HTTP and SOCKS proxies
```

## Known Issues
```
Microsoft verification doesn't seem to work currently
```
## How to Use
```
1. Put your accounts into the .txt files set in config.cfg. (accounts-moj.txt and accounts-msa.txt by default)
2. (Optional) Put your proxies into the .txt files set in config.cfg. (proxies-socks.txt and proxies-http.txt by default)
3. Set the threadCount, attemptCount, and proxyFailCount values in config.cfg to your desired settings
4. Open terminal and type `java -jar MCChecker.jar`
```
