Android ViewPagerIndicator
==========================

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
	compile(name: 'easyFoursquare4Android_v1.1.0', ext: 'aar')	
}
```

[1]: https://github.com/yayaa/easyFoursquare4Android/blob/master/aar/easyFoursquare4Android_v1.1.0.aar
