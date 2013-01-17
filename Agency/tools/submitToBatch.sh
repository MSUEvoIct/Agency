for REP in `ls | grep rep` 
do 
	cd $REP
	echo java -Xmx256m ec.Evolve -file rep.properties | batch
	cd ..
done