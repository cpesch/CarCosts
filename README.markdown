CarCosts
========

A [project](http://github.com/cpesch/CarCosts) to track all the costs that my
cars and/or motor bikes generate. It helps me to maintain an overview of the
costs and it might help you.

FAQ
---

Q: Why Java?  
A: I like it. That is why I had more fun developing it.

Q: Is it old?  
A: Yes, it's from somewhere around 1999.

Q: Does my music collection need to have a structure?  
A: A certain structure is not necessary. If however you'd like to use the option 
   to rename files from the meta date, MP3Tidy uses a scheme
       Name of the artist / Name of the album / Name of the title .mp3 
   If you're using RecursiveTagSetter you can declare files as an compilation 
   and MP3Tidy uses the scheme
	     Name of the compilation / Name of the title .mp3 
	 
Q: How do you develop?  
A: Currently, I'm using ant to build and IntelliJ IDEA to develop. There are
   plans to migrate the build process to Maven and get rid of the IDEA project
   files and the thirdparty directory.	
    
Q: How do I compile it?  
A: Set JAVA_HOME to a Java 6 SDK and call
       ant -f build/build.xml clean jar
   and find lots of jars in build/output/
	 
I hope you like it, feedback is always welcome  
Christian