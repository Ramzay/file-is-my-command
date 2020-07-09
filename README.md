# file-is-my-command
Execute commands using file name

# Introduction
This project came to life when I wanted to control my computer using a voice recognition device (Alexa, Google assist etc.) and discovered the complexity of acheiving such a simple task.
What I discovered after some research is:
- Pay for third party apps
- Develop a free 'simple' solution and share it

# Installation and setup

1) Requirement
- Java version >= 8
- Windows 10 (not tested for prior versions but should work)

1) Windows
The goal is to run this program at the start of your computer, many solutions are available but the simplest I found was to create a custom windows service using WinSW (https://github.com/winsw/winsw/releases) but you can use whatever method you want to acheive this.
Steps:
- Place jar in a desired location (ex: C:/myDir)
- Create a config.json file in C:/myDir (see example below)
- Create the folder that will contain the scripts (name is configurable)
- Create the folder that will be checked for file / commands
- Create the scripts that you want to be executed when a command is detected
- Run the jar

# Configuration

Here is an example of configuration:
TODO
