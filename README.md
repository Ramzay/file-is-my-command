# file-is-my-command
Execute commands using files

# Introduction
This project came to life when I wanted to control my computer using a voice recognition device (ex: Alexa, Google assist etc.) and discovered the complexity of acheiving such a simple task.

What I discovered was:
- I had to use an online file storage in order to make this project free of charge
- Use files as a mean of communication
- Use IFTTT to enable voice recognition device to interact with popular online drive storage (official website: https://ifttt.com/ )
- Have an app that would check the drive folder and execute the <strong> desired and allowed </strong> tasks

Here are the steps to make a voice recognition device interact with our PC:
- User triggers a command using his voice: "Hey google trigger pc off"
- Voice recognition device checks IFTTT for a known receipe
- If a receipe is found the receipe will be executed (ex: put a file named "test.txt" in our dropbox drive)
- On our PC we have our drive synchronized, so when something / someone adds a file in the drive it is downloaded automatically
- An app, <strong>file-is-my-command</strong> will monitor the drive folder and if it finds "test.txt" it will run a shutdown script


The goal of this app is to:

- Monitor a folder where we put files in it (FTP, synchronized drive etc.), this folder is referred as <strong> command directory </strong>
- When we detect that a file is a <strong> command </strong> we will delete the file and then perform the associated <strong> script </strong>
- All files located in the <strong> command directory </strong> are not necessarly commands, you are free to configure wich file should be considered as a command
- For security reasons command files are not executed, we only parse the file name

# Installation and setup

<h3> Requirements </h3>

- Java version >= 8
- Windows 10 (not tested for prior versions but should work)

<h3> Important notes </h3>
- Command file will be deleted before executing the associated script (otherwise we would have an infinite loop)


<h3> Windows </h3>

<h4> Setup:</h4>

- Place jar in a desired location (ex: C:/myDir)
- Create a config.json file in C:/myDir/config.json
- Create the folder that will contain the C:/myDir/scripts
- Create the folder that will be checked by the app: C:/myDir/commands
- Create a batch that will run the jar: C:/myDir/run-jar.bat
- Install the windows service that will execute the jar and start the service (the additional WinSW files should be placed in C:/myDir)

Optional: you can use WinSW to wrap the jar into a windows service thus not needing the run-jar.bat.

<h4> File hierarchy: </h4>

```
project
|   config.json
|   file.is.my.command-X.X-SNAPSHOT.jar
|   run-jar.bat
|   WinSW.NET4.exe (optional)
|   WinSW.NET4.xml (optional)
|   
|
|___scripts
	|
	|_  sleep.bat
	|_  myCustomScript.bat
|
|
|___commands (put here the files that will trigger command)
```

<h4> File example: </h4>

<h6> run-jar.bat</h6>

```batch
REM Move to the batch file location
REM For admin user default dir is in system32
REM Note: you can specify the location in the action tab in taskscheduler rendering the below line redundant
cd "%~dp0"
REM run the jar, absolute path not needed since we are in the correct dir
java -jar file.is.my.command-1.0-SNAPSHOT.jar
```

<h6> sleep.bat</h6>

```batch
@echo off
REM kill vlc
taskkill /F /IM vlc.exe /T
REM kill chrome
taskkill /F /IM chrome.exe /T
REM wait 4 seconds
ping -n 4 127.0.0.1 > nul
REM Shutdown windows machine
shutdown.exe /s /t 00
```

<h3> Configuration </h3>

Here is an example of config.json:

```json
{
	"scriptDir": "scripts",
	"commandPath" : "K:\\fileismycommand\\command",
	"commands": [		
		{"fileName": "shutdown-laptop", "script": "sleep.bat"}
	],
	"logEverything" : true
}

```
