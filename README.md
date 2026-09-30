# Campus_Management_System

> 📖 **Windows 11 Users:** See the complete step-by-step setup and verification guide in [WINDOWS_INSTALLATION_GUIDE.md](WINDOWS_INSTALLATION_GUIDE.md).
> 🐘 **Database Setup:** Looking to set up PostgreSQL and pgAdmin? See [POSTGRES_PGADMIN_SETUP.md](POSTGRES_PGADMIN_SETUP.md).

## Web Application (Maven + Jetty EE10)
```shell
# Run embedded Jetty web server (Port 8080)
mvn jetty:run
```
- Access Students List: `http://localhost:8080/students`
- Add Student Form: `http://localhost:8080/student.html`

## Console Application (src1)
### run command for linux
```shell
javac -d out $(find src1 -name "*.java")
java -cp out com.campus.app.Main
```

### run command for windows
```shell
javac -d out src1\com\campus\model\*.java src1\com\campus\service\*.java src1\com\campus\contract\*.java src1\com\campus\app\Main.java
java -cp out com.campus.app.Main
```

## jdk-javaversion
```shell
javac -version
java -version
```

## my system info
```shell
openjdk version "17.0.16" 2024-09-16
OpenJDK Runtime Environment (build 17.0.16+9-LTS)
OpenJDK 64-Bit Server VM (build 17.0.16+9-LTS, mixed mode, sharing)
```
jdk --> java development kit
jre --> java runtime environment
jvm --> java virtual machine

1.data types
primitive --> byte short int long float double boolean char
non-primitive --> String array

2.array utils
java.util.Arrays-->toString,sort,binarySearch,equals,fill,copyOf,copyOfRange


3.method types
no params and no return
no params and with return
with params and no return
with params and with return



method overloading:
same method name different parameters
displayStudentInfo()
displayStudentInfo(boolean showMarks)

4.oop pillars
encapsulation
inheritance
polymorphism
abstraction

encapsulation-->data hiding-->private-->getters and setters
inheritance-->is a relationship-->extends-->super keyword
polymorphism-->many forms-->method overloading,method overriding
  --methods overloading
  --methods multilevel inheritance --> A extends B extends C -->A
  --methods multi-stage inheritance --> A extends B extends C -->C
  --methods multiple inheritance --> A extends B and C -->A
  --methods hybrid inheritance--> multiple + multilevel + hierarchical
  --methods hierarchical inheritance --> A extends B and A extends C -->A and B,A and C
  --methods cyclic inheritance --> A extends B and B extends A -->Compile time error
abstraction-->hiding implementation details-->abstract class,interface-->contracts to implement methods to achieve multiple implementations

error --> compile time(checked errors),runtime error(unchecked errors while runtime execution),logical error(programmer error, while compiling code it runs but gives wrong output),syntax error(errors in code grammar)


github.com/srirammurugesan/Campus_Management_System