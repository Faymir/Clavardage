# Clavardage

A distributed Chatting app based on sockets. It is made with Java and using JavaFX UI as GUI.
 
![alt text](./Clavardage.png "ScreenShot")


## Have tree modes:

+ A Test mode:  To open multiple clients on same machine, for testing purpose
+ Broadcast mode for local networks
+ A centralized mode with a Server on ip address http://node6669-clavadage.jcloud.ik-server.com/webapi/

## Requirements
+ Java Runtime Environment 8 is recommended. It should works on higher versions but not tested.
Please report any issue.


##Usage

+ Execute chat.jar 
+ or from console run `java -jar chat.jar`

NB: The Jar file must be in the root directory of the project because it use resources located in 
+-- _config.yml
+-- _drafts
|   +-- begin-with-the-crazy-ideas.textile
|   +-- on-simplicity-in-technology.markdown
+-- _includes
|   +-- footer.html
|   +-- header.html
+-- _layouts
|   +-- default.html
|   +-- post.html
+-- _posts
|   +-- 2007-10-29-why-every-programmer-should-play-nethack.textile
|   +-- 2009-04-26-barcamp-boston-4-roundup.textile
+-- _data
|   +-- members.yml
+-- _site
+-- index.html
+-- src
|   +-- main
|       ..
|       +-- **resources**
+-- **chat.jar**

## Development Requirements

+ JDK >= 1.8
+ Maven
