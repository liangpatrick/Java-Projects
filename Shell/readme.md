## Brief Description
A simple implementation of the Unix Shell in the C Programming language. This project was coded and tested in Ubuntu 20.04.

## Detailed Description
### Implementation Details
Able to do most Unix commands(e.g., ls, cd, sleep, etc.) and run programs that accept command-line arguments. However, unable to run programs that take in repeated inputs through stdin. Has signal handlers for SIGINT, SIGTSTP, and SIGCHLD. Shell will automatically reap all children, since this shell uses fork in order to implement fg and bg.
In order to exit shell, enter command 'exit'.
  
  When errors occur, appropriate error messages will be displayed.
  

### Commands
  Only a very small number of commands have been implemented. The commands are:
  * `bg`
  * `fg`
  * `cd`
  * `exit`
  * `jobs`
  * `kill`
