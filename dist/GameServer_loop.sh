#!/bin/bash
while :;
do
        java -server -Dclient.encoding.override=UTF-8 -Dfile.encoding=UTF-8 -XX:-UseParallelGC -Xmx2200m -cp ./libs/*:TJServer.jar tera.gameserver.GameServer >/dev/null 2>&1
                gspid=$!
                echo ${gspid} > gameserver.pid
                [ $? -ne 2 ] && break
        sleep 10;
done
