## Brief Description
A simple implementation of the Unix Shell in the C Programming language. This project was coded and tested in Ubuntu 20.04.

## Detailed Description
### Implementation Details
Able to do most built-in Unix functions and run programs that accept command-line arguments. However, unable to run programs that take in repeated inputs through stdin. Has signal handlers for SIGINT, SIGTSTP, and SIGCHLD. Shell will automatically reap all children, since this shell uses fork in order to implement fg and bg.
  
  When errors occur, appropriate error messages will be displayed.
  


