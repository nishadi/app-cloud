#!/bin/bash
# $1 - file path
# $2 - request number
# $3 - file name

mkdir $1\/$2
cd $1\/$2
cp $1\/$3 .
unzip -d tmp $3
cd tmp
sed -i -e "s/<title>.*<\/title>/<title>Load Testing $2<\/title>/g" index.html
zip -r $1\/$2.war *
rm -r $1\/$2
