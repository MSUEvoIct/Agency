CLASSPATH=`pwd`/bin
for file in `ls lib`
do
	CLASSPATH=$CLASSPATH:`pwd`/lib/$file
done
export CLASSPATH
