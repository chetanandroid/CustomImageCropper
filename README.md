Simple Custom Image Cropping View based on SimpleCropView.

Supports **Picking Image from Camera and Gallery**.

**Must Add The Following To Get The Project Working-->>**

-- Include the following dependency in your build.gradle file. Please use the latest version available.

    repositories { 
        jcenter() 
    }

    dependencies { 
        compile 'com.isseiaoki:simplecropview:1.1.4' 
    }

-- Add below permissions to your manifest

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    
    (Android Marshmallow runtime permissions code already added)
    
-- Library used 
    
   https://github.com/IsseiAoki/SimpleCropView
    
