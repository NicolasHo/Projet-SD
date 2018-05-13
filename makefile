make:
	mkdir -p server
	mkdir -p client
	javac -d ./server ServerImpl.java
	javac -d ./client Client.java

rm:
	rm ./client/*.class
	rm ./server/*.class
