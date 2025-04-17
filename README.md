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
- Run src.Server by running "java src.Server"
- Open 2 additional terminal windows
- Connect each client by running "java src.Client localhost 1234"
- Chat!

Remote setup:

- Port forward TCP '1234' on host machine
- Run src.Server by running "java src.Server"
- Open 1 additional terminal window locally and run "java src.Client localhost 1234"
- On your friend's PC, compile src.Client.java and src.CaesarCipher.java
- Then run "java src.Client <your-public-ip> 1234"
- Chat!
