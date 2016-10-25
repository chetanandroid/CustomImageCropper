Simple Custom Image Cropping View based on SimpleCropView.

It's very useful to use this activity on the devices which has landscape camera and the captured image is sometimes rotated in results.

-- Modifications added to existing library --
1. Added support to capture image from camera and also pickup from gallery (Existing library only supported picking gallery image)</br>
2. Picture quality taken by camera is lossless after cropping (Also supports rotation of image).</br>
3. User defined directory name to save cropped images.</br>
4. Send cropping shape name via intent and cropping activty will select the cropping shape depending on intent values.</br>
5. Simplified flow (Existing library was much more complex to implement and understand the flow)</br>
6. All new code (Replaced deprecated code of existing library)</br></br>

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
    
