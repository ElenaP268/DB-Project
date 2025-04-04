# DB-Project
- Put all CSV files into the file path (ex: C:\db_project\data\example.csv)
- Run the Library_DB_DataLoad file

How to load the data:
1. Go to Services in your windows laptop and stop the MySQL service.
2. Go to the MySQL installation folder. This will something like C:\ProgramData\MySQL\MySQL Server <version>
3. Rename the my.ini file to my.ini.bak and open it for editing in notepad.
4. Search for secure-file-priv. Remove the path mentioned inside the double quotes. It will now look like server-file-priv=""
5. Save the file as my.ini. While saving make sure the save as type is changed to "All files".
6. Go to services and start the MySQL service.
7. Now open MySQL workbench. Go to Edit -> Preferences . In the preferences pop-up select SQL Editor. Scroll to the bottom and uncheck safe updates and click the Ok button.
8. Restart the MySQL workbench. Now you should be abe to run the data load script  without any permission issues.
