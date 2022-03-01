# Part-I Documentation  
  
> **Note**: This repository consists of the program that was analyzed for smells (for part 1 of the assignment) . Additionally, it also contains a folder called 'Part 2 Analysis' that consists of the python script (executed after running **ArchitectureQualityEvolution** [2]) used to analyze the open source project for part 2 of the assignment. It also consists of the various plots and commit-wise metrics used to generate those plots.
  
  
## Implementation smells  
> **Magic Number**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main` \
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `DatabaseHandler` \
*method* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `populateTable`  
  
The 2 in **line 153** is a magic number. It has been hard coded to add an external ID field while adding entries to the database. In case the external ID is removed, or another external property needs to be added, there will be confusion.  
  
> **Complex Method**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main` \
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `DatabaseHandler` \
*method* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `handlerMain`  
  
This method is a complex method with cyclomatic complexity = 24. This method needs to be split into different methods without the use of command flags. The method handles very different jobs and implements all of them inside it. This also means it contains the Long Parameter List smell (9 parameters)  

## Design Smells  
> **Rebellious Hierarchy**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main` \
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `MyReader, CSVHandler, JsonHandler` \
*method* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `writeListToCSV`  
  
This is clearly a rebellious hierarchy, since JsonHandler does not support writing from memory into a CSV file and throws an UnsupportedOperationException.  
  
> **Cyclic Hierarchy**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main`  
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `MyReader, CSVHandler, JsonHandler`  
*method* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `MyReader.readData`  
  
The superclass is creating an instance of the subclass, which is clearly an example of cyclic hierarchy.  
  
> **Deficient Encapsulation**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main`  
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `DbWrapper`  
*method* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `init`  
  
This smell partially occurs because of the rebellious hierarchy. Since MyReader has to now store the data depending on the data type, DbWrapper needs to specifically access the variable associated with the data type. This means that DbWrapper will have access to variables inside of MyReader, which is an example of Deficient Encapsulation  

> **Insufficient Modularization**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main`  
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `God`  
  
  
This smell exists because the God class handles many kinds of functionality (*file reading, writing, database connections, etc.*). It has a large number of public methods.

> **Broken Hierarchy**  
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main`  
*Class* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; `CSVHandler, MyReader`


This smell exists because the `CSVHandler` class extends the `MyReader` class but does not override any of the supertype methods
  
## Architecture Smells  
>**Feature Concentration** \
*Package* &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;`com.assignment1.main`  
  
This component realizes multiple architectural concerns (*I/O, Database handling, and Query execution*) Independent sets of related classes within this component are:   
- `ConfigHandler; DbWrapperTest; CSVHandler; DbWrapper; DatabaseHandlerTest; DatabaseHandler`  
- `JsonHandler` - `MyReader` - `CSVHandlerTest`   
**LCC (Lack of Component Cohesion) = 0.44**  
  
![title](https://imgur.com/AZX7UYO.png)
<center><small>Fig 1 Designite [1] output proving the presence of smells mentioned above</small></center>


# References
[1] Tushar Sharma (2020) DesigniteJava (v1.8.6) [Source Code] [https://github.com/tushartushar/DesigniteJava](https://github.com/tushartushar/DesigniteJava)

[2] Tushar Sharma (2019) ArchitectureQualityEvolution (v1.0.0) [Source Code] [https://github.com/tushartushar/ArchitectureQualityEvolution](https://github.com/tushartushar/ArchitectureQualityEvolution)