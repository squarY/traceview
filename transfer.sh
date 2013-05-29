#specify:xxx.xxx.xxx
#reg:\/xxx\\\\\\\\.xxxx\\\\\\\\.xxxx.*\/
#$1 root class $2 root method $3 class for invoked $4 method for invoked
sed "s/<1>/$1/g;s/<2>/$2/g;s/<3>/$3/g;s/<4>/$4/g" CallInfoOutput.template > CallInfoOutput.java
