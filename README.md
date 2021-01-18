# TasteIT
## TasteIT - search the food you like !
#### TasteIT is all about finding food that looks good! you first see the food and than pick the place! 
an application used to find food by search perference and location , users can take pictures of their favorite plates and upload it to TasteIT , or places will come from google  api images . build with android studio , kotlin , java , fireBase , google cloud places API

## Project status - 
development for the project have been mostly done. however, the project is currently does not available at Play Store and has been suspended due to hight costs using various apis. 

## Walkthrough - 
### Logo:
![ic_launcher-playstore](https://user-images.githubusercontent.com/51089069/104962923-2731fb00-59e2-11eb-8a08-657ad0a72840.jpg)


### Auth screen:
![WhatsApp Image 2021-01-18 at 23 01 05](https://user-images.githubusercontent.com/51089069/104962856-fbaf1080-59e1-11eb-95e8-21ca5f6eb99c.jpg)

#### Authentication available using phone number and sms verification , facebook , gmail , email and password verification 


### Near by :
#### Get data of places to eat by your current location of city according to user perference .
![WhatsApp Image 2021-01-18 at 23 19 47](https://user-images.githubusercontent.com/51089069/104964179-990b4400-59e4-11eb-8e33-9f489a0e9de0.jpg)
#### If a the image was taken by the user an extra tool bar will be appear giving the user the ability to like the image and get the distance between the user and the food image they see
![WhatsApp Image 2021-01-18 at 23 19 40](https://user-images.githubusercontent.com/51089069/104963933-2e5a0880-59e4-11eb-9e8a-b45ee8aedb33.jpg)
#### The user can always change his search perference from city to distance and new data will be Immediately fetched 
![WhatsApp Image 2021-01-18 at 23 19 54](https://user-images.githubusercontent.com/51089069/104964012-534e7b80-59e4-11eb-80a0-cbaa72c53aea.jpg)


### Search :

UI / UX example video - (notice the animations to allow the user slide up and down without toolbars Interfering him)
https://user-images.githubusercontent.com/51089069/104965926-66fbe100-59e8-11eb-92cd-f03bde2717f1.mp4

#### Search allow the user to search for his food / cousine he like to eat in every place around the world with a smooth and easy ui/ux - scrool down until you find something Appetizing and than click it 
![WhatsApp Image 2021-01-18 at 23 52 47](https://user-images.githubusercontent.com/51089069/104966287-2bade200-59e9-11eb-8612-26d0e4d22eaa.jpg)


### Details :

#### When the user click the image he like he will be navigated to details screen so he can choose if he want to save the image in his profile page for later or get details about the image and place such as - address , place name , reviews , likes , rating , navigate with waze , contact through phone or website , is the place open and show the place on map.

![WhatsApp Image 2021-01-19 at 00 10 23 (1)](https://user-images.githubusercontent.com/51089069/104967505-34ec7e00-59ec-11eb-8406-1e383f2bbb79.jpg)
![WhatsApp Image 2021-01-19 at 00 10 23 (2)](https://user-images.githubusercontent.com/51089069/104967566-5d747800-59ec-11eb-94d3-2722f56d0adf.jpg)

### Profile : 
#### Your profile page where you can go to see the images that you took and the images that you liked - click the image will send you to their details place.

![WhatsApp Image 2021-01-19 at 00 42 11 (1)](https://user-images.githubusercontent.com/51089069/104968967-ffe22a80-59ef-11eb-98e5-bb98cca4bd5d.jpg)

### Camera Screen :
#### a simple camera screen alowing the user to take any picture he want without ever leaving the app and allow the data base to check the picture and location (for example if a picture was not taken in a resteraunt the user wont be able to upload the picture)

## Reflection: 

- This project was made as a finale project for HackerU college android development class.  Project goals included using technologies learned up until this point , familiarizing myself with documentation for new features and create an app the i truly believed in.

- Originally i wanted to build an app that allow the user to find a place to eat by the cousine and the look of the food and the experience other users had in a simple straight forward way. I always found my self dissapointed with any food related app since i never got to see the food before in a simple way before i reach the resteraunt. 

- one of my main challenges i ran into was fetching all the data. I found my self fetching data from many sources and quries and i wanted to make the queries as simple as possible. At the end i manages to work with reporistory design pattern to make the queries efficent enough so all the data will be fetched quickly. Another challenge i found my self in was creating a social network UI/UX design. I decided to implement my own back stack to make sure you can navigate back and forward without losing any data. Also i worked a lot with AppBar Layout and his call back to allow all the toolbars to have specific and uniqe animations when scrolling. 

### tools:
 - Android studio 
 - FireStore 
 - FireBase Storage
 -FireBase GeoQuery 
 -kotlin coroutine 
 -smart location library 
 -Retrofit2 
 -google cloud places api
 -google material design 
 
 #### design patterns - 
 - Strategy design pattern (for uploading data to data base)
 - Observer design pattern (Network Management Callbacks)
 - Repository design pattern
 - Singelton 
 - Coroutine 
 
 

