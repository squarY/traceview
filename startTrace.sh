#1 pid for jvm you want to monitor.#2 claspath for the process your specified in $1
btrace -cp lib:$2 $1 CallInfoOutput.java
