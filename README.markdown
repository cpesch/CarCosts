CarCosts
========

A [project](http://github.com/cpesch/CarCosts) to track all the costs that my
cars and/or motor bikes generate. It helps me to maintain an overview of the
costs and it might help you.

You have to be somewhat disciplined to enter the costs into the program:

* all fuel costs
* all maintenance costs 

But if you do that, CarCosts calculates

* total and average fuel consumption,
* total and average mileage per year,
* average fuel price and
* cost per kilometer based on fuel costs, maintenance costs and overall costs. 

FAQ
---

Q: Why Java?  
A: I like it. That is why I had more fun developing it.

Q: Is it old?  
A: Yes, it's from somewhere around 1999.

Q: How do you develop?  
A: Currently, I'm using ant to build and IntelliJ IDEA to develop. There are
   plans to migrate the build process to Maven and get rid of the IDEA project
   files and the thirdparty directory.	
    
Q: How do I compile it?  
A: Set JAVA_HOME to a Java 5 SDK and call
       ant -f build/build.xml clean jar
   and find lots of jars in build/output/
	 
I hope you like it, feedback is always welcome  
Christian