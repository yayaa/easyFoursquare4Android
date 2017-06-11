easyFoursquare4Android
==========================
[![No Maintenance Intended](http://unmaintained.tech/badge.svg)](http://unmaintained.tech/)

Since the original library has not changed at all by 2 years, i decided to convert it into AndroidStudio Project in order to make it easier to include in applications.

To include, get aar file from [here][1] and put it into 'libs' folder, then have project's gradle file following lines:

```groovy
android {
	...

	repositories {
    	    flatDir {
        	    dirs 'libs'
        	}
    	}
	
	...
}

dependencies {
	compile(name: 'easyFoursquare4Android_v1.2.1', ext: 'aar')	
}
```

# Important

Don't forget to set your clientId & clientSecret & callbackURL as below:

```java
FoursquareConfig.setClient(CLIENT_ID, CLIENT_SECRET);
FoursquareConfig.setCallbackUrl(CALLBACK_URL);
```

# Proguard

```
# Because it uses gson, we need to keep models
-keep class br.com.condesales.models.** { *; }
-dontwarn fi.foyt.foursquare.**
```

[1]: https://github.com/yayaa/easyFoursquare4Android/blob/master/aar/
