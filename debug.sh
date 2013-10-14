#!/bin/bash
mvn -pl environment/standalone -Dmaven.surefire.debug="-Xdebug -Xrunjdwp:transport=dt_socket,server=y,suspend=n,address=8000" exec:java -Dexec.mainClass="pl.doa.DOAStarter"