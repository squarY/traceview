# TraceView
A tool for tracing the method calls and rendering the information about time cost and relationship into HTML.

# Steps
1. Install BTrace and put the btrace script to your path.
2. Using tranfer.sh to generate the script for monitoring given class or pakcage. $1=The class for root of tracing. $2= The method for root of tracing. $3= The classes for the calls monitored. $4-The methodes for the calls monitored.
3. Using startTrace.sh to start the tracing. $1 = pid for the jvm which your want to trace. You can output the tracing information to file.
4. Using mvn to build the source code. `mvn assembly:assembly` will create a package including all dependencies.
5. Using generateView.sh to generate the html page for rendering the tracing information.$1=path for tracing log file. $2=path for page template $3=path for page.