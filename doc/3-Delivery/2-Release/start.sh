#!/bin/sh

AppDir=$(cd "$(dirname "$0")"; pwd)

for f in `ls ${AppDir}/libs/*.jar`
do
JAR_CLASSPATH=$JAR_CLASSPATH:$f 
done 
export JAR_CLASSPATH 

nohup java -Dfile.encoding=UTF-8 -Xmx1024m -Xms1024m -classpath ${AppDir}/war/WEB-INF/classes$JAR_CLASSPATH org.ccframe.app.App >> run.log 2>&1 &

