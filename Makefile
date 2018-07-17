all: CSftp.jar
CSftp.jar: CSftp.java
	javac CSftp.java
	jar cvfe CSftp.jar CSftp *.class
	javac CSftpSockets.java
	javac ftpCommands.java
	javac BytesToString.java

run: CSftp.jar  
	java -jar CSftp.jar ftp.gnu.org  21

clean:
	rm -f *.class
	rm -f CSftp.jar
