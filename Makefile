all:MyBot.class 

MyBot.class: MyBot.java
	javac MyBot.java

run:
	java MyBot

clean:
	rm -rf *.log *.hlt *.class

check:
	python ./run.py --cmd "java MyBot" --round 1

backup:
	scp -r ../Halite cristian.smarandoiu@fep.grid.pub.ro:.
