
#Project Overview

Most of us can relate to kicking back on the couch and enjoying a movie with friends and family. In this project, you’ll build an app to allow users to discover the most popular movies playing. We will split the development of this app in two stages. First, let's talk about stage 1. In this stage you’ll build the core experience of your movies app.

## This app:

*Present the user with a grid arrangement of movie posters upon launch.
*Allow your user to change sort order via a setting:
*The sort order can be by most popular or by highest-rated
*Allow the user to tap on a movie poster and transition to a details screen with additional information such as:
  **original title
**movie poster image thumbnail
**A plot synopsis (called overview in the api)
**user rating (called vote_average in the api)
**release date

#This Project?

*Fetch data from the Internet with theMovieDB API.
*You will use adapters and custom list layouts to populate list views.
*You will incorporate libraries to simplify the amount of code you need to write
*Ready to start building? Click "Next" for instructions!
 

## What should you know 

*We needed to use our API_KEY to build the app so I put that key in the class Constants.
*For security reasons before to zip project I have removed that  key. You can put you key in the Constants class in the api_key final static variable . 

*I included this to reduce unnecessary extra work and help you focus on applying your app development skills.
You’ll need to modify the build.gradle file for your app. These modifications will happen in the build.gradle file for your module’s directory, not the project root directory (it is the file highlighted in blue in the screenshot below).

In your app/build.gradle file, add:
 '''
 repositories {
 mavenCentral()
}
 '''
Next, add  ''' compile 'com.squareup.picasso:picasso:2.5.2' ''' to your dependencies block.

## Working with the _themoviedb.org API_

A note on resolving poster paths with themoviedb.org API

You will notice that the API response provides a relative path to a movie poster image when you request the metadata for a specific movie.
For example, the poster path return for Interstellar is “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
You will need to append a base path ahead of this relative path to build the complete url you will need to fetch the image using Picasso.
It’s constructed using 3 parts:
The base URL will look like: http://image.tmdb.org/t/p/.
Then you will need a ‘size’, which will be one of the following: "w92", "w154", "w185", "w342", "w500", "w780", or "original". For most phones we recommend using “w185”.
And finally the poster path returned by the query, in this case “/nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg”
Combining these three parts gives us a final url of http://image.tmdb.org/t/p/w185//nBNZadXqJSdt05SHLqgT0HuC5Gm.jpg 
 
This is also explained explicitly in the API documentation for /configuration.

https://lh6.googleusercontent.com/Hs2OlBBbQrGGmoPiNzXO0F2u2Pqz2rnmrSazDDdWdvCMycdmQIuEkAogSRfEe_r-sz3Ks7ziAX8XjaRE7Ukcv04rZGlPLd6VZbEBeNgL8Sdi58-CTLEz7sR9eGaItouyNtYdSGI
