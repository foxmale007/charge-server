#/bin/sh
ps -ef| grep "org.ccframe.app.App"|grep -v grep|awk '{print $2}'| xargs kill
