#!/bin/bash

cat test.txt
echo ====
sed	-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\|\);$/new Object[]{ \1 }\2);/' test.txt
echo ====
sed	-e 's/(Object\[\])(\(.*\)))\(\[[0-9]\]\)\?;$/new Object[]{ \1 }\2);/' test.txt
echo ====
sed -E 's/\(Object\[\]\)\((.*)\)\)(\[[0-9]\])?;$/new Object[]{ \1 }\2);/' test.txt
