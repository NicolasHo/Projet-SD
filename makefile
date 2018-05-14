make:
	mkdir -p server
	mkdir -p client
	mkdir -p simulation
	javac -d ./server ServerImpl.java
	javac -d ./client Client.java
	javac -d ./simulation Simulation.java

rm:
	rm ./client/*.class
	rm ./server/*.class
	rm ./simulation/*.class
