# How To use this services:
- open this website https://chatexporter.littleappy.co/
- inspect this page (right click -> inspect)
- login using your working email
- you will find a request called -> chats?$expand=members&$top=30
- click on it and it the tab of headers will find Authorization header
- copy this token
- open postman and create GET request on http://localhost:8080/api/downloadTeamsChats
- add only one header called Authorization to this request and put the token that you have taken in it
- it will create all chats as json files in a folder named Teams Chats on Desktop

# Notes
- the service shows the progress in the logs
- token validity last for more than an hour