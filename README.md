# DB-Project

## Technical Dependencies
- Java (guaranteed to work on version 24 and newer)
- JDBC
- MySQL 

## Creating the Database
1. Launch MySQL with appropriate login credentials for your machine.
2. Create the empty database by running Library_DB.sql. This will overwrite any existing 'library' database.
3. Put all CSV files into the file path in Library_DB_DataLoad.sql (ex: C:\db_project\data\example.csv)
4. Run the Library_DB_DataLoad file using one of the two methods below:

How to load data (method 1 - MySQL workbench)
1. Go to Services in your windows laptop and stop the MySQL service.
2. Go to the MySQL installation folder. This will look something like C:\ProgramData\MySQL\MySQL Server <version>
3. Rename the my.ini file to my.ini.bak and open it for editing in notepad.
4. Search for secure-file-priv. Remove the path mentioned inside the double quotes. It will now look like server-file-priv=""
5. Save the file as my.ini. While saving make sure the save as type is changed to "All files".
6. Go to services and start the MySQL service.
7. Now open MySQL workbench. Go to Edit -> Preferences . In the preferences pop-up select SQL Editor. Scroll to the bottom and uncheck safe updates and click the Ok button.
8. Restart the MySQL workbench. Now you should be abe to run the data load script  without any permission issues.

How to load data (method 2 - command line)
1. Go to the MySQL installation folder. This will look something like C:\ProgramData\MySQL\MySQL Server <version>
2. From there, navigate to the trusted uploads directory for the project. This will look something like ...\Uploads\db_project\data.
3. Place the CSV data files in the trusted directory.
4. In the data loading script, update the file path to match the new directory.
5. Open the command line and run the script.

## Launching the GUI
1. Before launching the interface, replace the placeholder password in Config.java with your actual MySQL password.
2. 
(ADD INSTRUCTIONS)
