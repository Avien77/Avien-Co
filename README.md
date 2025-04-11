V.3:

- reworked client and server
- server now used as decryption key
- client encrypts messages before send
- server decrypts and broadcasts messages
- added username support and live name changes
- client typing field reflects current username
- timestamped messages on client and server
- server logs encrypted and decrypted message traffic
- added remote access via port forwarding

Local setup:

- Compile files by running "javac *.java" in the terminal
- Run Server by running "java Server"
- Open 2 additional terminal windows
- Connect each client by running "java Client localhost 1234"
- Chat!

Remote setup:

- Port forward TCP '1234' on host machine
- Run Server by running "java Server"
- Open 1 additional terminal window locally and run "java Client localhost 1234"
- On your friend's PC, compile Client.java and CaesarCipher.java
- Then run "java Client <your-public-ip> 1234"
- Chat!
