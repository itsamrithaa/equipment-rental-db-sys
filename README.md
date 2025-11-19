# Database Management Project: Group 10  
A Java console application for managing customers, drones, rentals, and returns, integrated with an SQLite database.


## Prerequisites 

- **JDK 17** 
- **Visual Studio Code** with the **Java Extension Pack**



## Instructions for Graders

### **1. Unzip the submission**
- Download the ZIP file (e.g., `databases-group10.zip`).
- Extract it. You should now see a folder such as:

databases-group10/
src/
data/
lib/
README.md


### **2. Open the project in VS Code**
1. Launch VS Code  
2. Go to **File → Open Folder…**  
3. Select the extracted project folder (e.g., `databases-group10`)  
4. Press **Open**


### **3. Verify the Java project loads correctly**
- In the **JAVA PROJECTS** panel (bottom left), you should see each `.java` file under the **src** directory:  
  - `Main.java`  
  - `CustomerManager.java`  
  - `Customer.java`  
  - `DroneManager.java`  
  - `Rentals.java`  
  - `Returns.java`  


### 4. Command Line Compile and Run
1. Open terminal inside VS Code and paste the following into command line. One by one. 

```bash
# compile
javac -cp 'lib/*:src' -d bin src/*.java 
# run
java -cp 'bin:lib/*' DB 
```

The program will compile and run automatically.

You should see the following message:

`The driver name is SQLite JDBC`

`The connection to the database was successful.`

`Welcome to the Database System for Group 10`

This confirms successful import.

## Repository Layout 

- src/      Java source files
- bin/      Compiled .class files 
- data/     SQLite database file (data/app.db)
- lib/      JDBC SQLite driver (sqlite-jdbc-3.32.3.2.jar)
- README.md This file