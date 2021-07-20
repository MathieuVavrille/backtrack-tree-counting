all: testrun

testrun: package
	mvn -q -e exec:java -D exec.mainClass=org.mvavrill.btcounting.BTCountingMain

package:
	mvn -q clean package
