V.2:
- reworked client and server
- server now used as decryption key
- client encrypts messages before send
- added remote access via port forwarding

Local setup:
- Compile files by running "javac *.java" in the terminal
- run Server by running "java Server" in the terminal
- start 2 additional terminal windows, connect them to the server by running "java Client localhost 1234"
- chat!

Remote setup:
- Port forward TCP '1234' on host machine
- run Server by running "java Server" in the terminal
- start 1 additional terminal window, connect it to the server by running "java Client localhost 1234"
- on your friend's pc, compile the client.java and caesarcipher.java files
- run "java Client <your-public-ip" 1234
- chat!
