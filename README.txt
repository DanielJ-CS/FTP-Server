Eric Chan
Daniel Jiang

USAGE:
make run - automatically connects to server ftp.cs.ubc.ca on port 21

you can also manually run yourself with:

"java CSftp [ServerAddress] [ServerPort]"



CHANGELOGS:
--------------------------------------------------------------------------------------------------------
v1.01
- Finished basic cases of user and pwd, successfully connects but no commands work inside the server yet

v1.02
- Cleaned up the quit command

V1.03
- Finished DIR, learned why we need passive mode and researched how to get port# when on passive mode

V1.04
- Started on FEAT, freezes after printing out the list of features (Potentially stuck in the while loop(?))

V1.05
- Changed all our IFs and Else IFs to cases (had a problem with the order of operations so we needed to use a switch)
- Refined the PWD field so it takes a regex
- Bug: Logging in will disable every command (prints Invalid Command for some reason)

V1.06
- Fixed issue, debugged that it was the byte[] was not getting refreshed at the next time it hit the For-loop in LEN
fixed by putting a boolean inside and refreshing it if the length did not equal 0 for the bytes of array

V1.07
- Finished GET and put more booleans for the user input to make sure ArrayIndexOutOfBounds exceptions weren't being
thrown

V1.08+
- Finished all FTP commands for the assignment

-----------------------------------------------------------------------------------------------------------

V2.01
- Started Refactoring to make classes more clean and robust. Transferred over bytes to string conversion to a new class

V2.02
- Transferred over connection to ftpServer through sockets to another class. Transferred over all the ftp Commands and
helper functions for those ftp commands into a new class.
