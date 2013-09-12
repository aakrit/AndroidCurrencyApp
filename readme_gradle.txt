##### HOW TO SET-UP YOUR PROJECT TO USE GRADLE FOR BUILDING AND INSTRUMENTATION TESTING#######

(0) install Gradle 1.6 or higher (get the latest one)  (http://www.gradle.org/downloads)

add these lines to your .gitignore file in the root of your project.
#for gradle
build/
.gradle/

(1)  make sure that you have a file called local.properties in the root of your project.
This file must contain the path to your local android sdk, and it should point to your local
installation of your android sdk.
on PC this might look like this:
sdk.dir=C:/port/adt/sdk
on Mac it might look like this:
sdk.dir=/users/admin/adt/sdk
Open the file in this project for more details.
(you can copy this file from the reference app)

(2) make sure to add the latest version of junit jar to your libs directory.
I've used junit-4.11.jar. Once it's in the libs directory, right-click it and select 'Add as library'
(you can copy this file from the reference app)

(3) create a new text file in the root of your project called build.gradle. You can just copy what is in the reference project.
Open the file in this project for more details.
(you can copy this file from the reference app)

(3) create a directory called tests in the root of your project. Right-click that directory and select create new directory called
src. Right-click the src directory and select Mark Directory as || Test Source Root.  The src dir will turn green.
 Right-click the src dir and select new package. For each package in your main src dir, create corresponding package with
 the same name, but append a .test at the end.

(4) Within your tests dir, create a project.properties file and make sure it's using the proper build version.
Open the file in this project  for more details.
(you can copy this file from the reference app)

(5) Within your tests dir, create a AndroidManifest file.
Within the AndroidManifest file, make sure to change the package and android:targetPackage to the appropriate names in your project.
(you can copy this file from the reference app)

  package="edu.uchicago.yourproject.test"

  android:targetPackage="edu.uchicago.yourproject"

(6) save everything, close project and now re-Import project with gradle.
Import Project
Navigate to your project
select Gradle
use local gradle distribution:
set the path to your gradle installation, e.g.  c:\java\gradle-1.6\
Finish


(7) add your Instrumentation tests to your test package.
(you can copy sample instrumentation tests files from the reference app)
If you R.java file is not recognized, go to Build || Make Module yourMainProjectName

(8) copy ints.xml into your values dir from reference app.
(you can copy this file from the reference app)

(9) run instrumentationTest from the drop-down in menu-bar


For more information about Gradle see: http://www.gradle.org/docs/current/userguide/userguide_single.html
