#!/bin/bash

CATALINA_HOME=${CATALINA_HOME?"CATALINA_HOME should be set."}

cp war/* $CATALINA_HOME/webapps/
