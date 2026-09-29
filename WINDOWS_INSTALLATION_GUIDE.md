# Campus Management System — Windows 11 Installation & Run Guide

A complete, step-by-step walkthrough to set up, verify, and run the **Campus Management System** on **Modern Windows 11 (64-bit)**.

---

## 📋 Table of Contents
1. [Project Overview & Architecture](#-project-overview--architecture)
2. [Prerequisites & System Requirements](#-prerequisites--system-requirements)
3. [Method 1: Fast-Track Setup via `winget` (Recommended for Windows 11)](#-method-1-fast-track-setup-via-winget-recommended)
4. [Method 2: Manual Installation & Environment Variables](#-method-2-manual-installation--environment-variables)
5. [Windows 11 Installation Checkpoints (Verification Checklist)](#-windows-11-installation-checkpoints-verification-checklist)
6. [Cloning & Setting Up the Project](#-cloning--setting-up-the-project)
7. [Building and Running the Web Application (Jetty 12 + Servlet 6.0)](#-building-and-running-the-web-application)
8. [Verifying the Web Interface](#-verifying-the-web-interface)
9. [Running the Console Application (`src1` - Optional)](#-running-the-console-application-src1---optional)
10. [Troubleshooting & Windows 11 Gotchas](#-troubleshooting--windows-11-gotchas)

---

## 🔍 Project Overview & Architecture

- **Project Type**: Java Web Application (WAR) + Modular Console App
- **Java Platform**: Java 17 LTS (Jakarta EE 10 / Servlet 6.0)
- **Build Tool**: Apache Maven 3.8+
- **Embedded Web Server**: Eclipse Jetty EE10 Plugin (`12.0.14`)
- **Default Port**: `8080`
- **Context Path**: `/`

---

## 💻 Prerequisites & System Requirements

| Component | Minimum Requirement | Recommended for Windows 11 | Checkpoint / Validation |
| :--- | :--- | :--- | :--- |
| **Operating System** | Windows 11 (Home / Pro / Enterprise) | Windows 11 22H2 or 23H2 (Build 22621+) | `winver` |
| **Terminal** | Windows PowerShell 5.1 / CMD | **Windows Terminal** with PowerShell 7 | `wt` or Built-in Terminal |
| **JDK** | OpenJDK 17 LTS | **Eclipse Temurin JDK 17 (Adoptium)** | `javac -version` & `java -version` |
| **Build Tool** | Apache Maven 3.8.x | **Apache Maven 3.9.x+** | `mvn -version` |
| **Version Control** | Git for Windows 2.40+ | Latest Git with Credential Manager | `git --version` |
| **Network Port** | Port `8080` free | Port `8080` open on localhost | `netstat -ano \| findstr 8080` |

---

## ⚡ Method 1: Fast-Track Setup via `winget` (Recommended)

Windows 11 includes the **Windows Package Manager (`winget`)** out of the box. You can install all prerequisites in one go.

### Step 1: Open Windows Terminal as Administrator
1. Press `Win + X` on your keyboard.
2. Select **Terminal (Admin)** or **PowerShell (Admin)**.

### Step 2: Install JDK 17, Maven, and Git
Run the following commands:

```powershell
# 1. Install Eclipse Temurin JDK 17 LTS
winget install EclipseAdoptium.Temurin.17.JDK --accept-package-agreements --accept-source-agreements

# 2. Install Apache Maven
winget install Apache.Maven --accept-package-agreements --accept-source-agreements

# 3. Install Git for Windows
winget install Git.Git --accept-package-agreements --accept-source-agreements
```

### Step 3: Refresh Your Terminal Environment
Close Windows Terminal and open a **new** standard (non-admin) Windows Terminal tab to load the newly registered PATH variables.

---

## 🛠️ Method 2: Manual Installation & Environment Variables

If you prefer installing tools manually or do not have `winget` available:

### Step 1: Install JDK 17 (Java Development Kit)
1. Download **Eclipse Temurin JDK 17 (x64 Windows MSI installer)** from:  
   👉 [https://adoptium.net/temurin/releases/?version=17](https://adoptium.net/temurin/releases/?version=17)
2. Run the `.msi` installer.
3. **Important Checkpoint**: On the custom setup screen, make sure **"Set JAVA_HOME variable"** and **"Add to PATH"** are checked.
4. Complete the installation (default location: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot\`).

### Step 2: Install Apache Maven
1. Download the **Binary zip archive** of Apache Maven (e.g., `apache-maven-3.9.x-bin.zip`) from:  
   👉 [https://maven.apache.org/download.cgi](https://maven.apache.org/download.cgi)
2. Extract the archive into:  
   `C:\Program Files\apache-maven` (or `C:\maven`).

### Step 3: Configure Windows 11 Environment Variables
1. Press `Win + R`, type **`sysdm.cpl`**, and hit `Enter`.
2. Go to the **Advanced** tab and click **Environment Variables...**.
3. Under **System variables**:
   - Check if `JAVA_HOME` exists. If not, click **New...**:
     - Variable name: `JAVA_HOME`
     - Variable value: `C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot` *(use your actual JDK path)*
   - Create `MAVEN_HOME`:
     - Variable name: `MAVEN_HOME`
     - Variable value: `C:\Program Files\apache-maven\apache-maven-3.9.x`
   - Select **`Path`**, click **Edit...**, and click **New**:
     - Add: `%JAVA_HOME%\bin`
     - Add: `%MAVEN_HOME%\bin`
4. Click **OK** on all dialog boxes to save changes.

---

## ✅ Windows 11 Installation Checkpoints (Verification Checklist)

Before proceeding to build the project, run these checkpoint commands in a **fresh PowerShell or Command Prompt window**.

### Checkpoint 1: Java Runtime & Compiler
```powershell
java -version
javac -version
```
**Expected Output:**
```text
openjdk version "17.0.x" ...
OpenJDK Runtime Environment ...
javac 17.0.x
```
> ⚠️ **Checkpoint Fail:** If you see version `1.8` or `11`, ensure `JAVA_HOME` points to JDK 17 and that `%JAVA_HOME%\bin` is listed higher in the `Path` than other Java installations.

---

### Checkpoint 2: `JAVA_HOME` Environment Variable
**In PowerShell:**
```powershell
$env:JAVA_HOME
```
**In Command Prompt:**
```cmd
echo %JAVA_HOME%
```
**Expected Output:**
```text
C:\Program Files\Eclipse Adoptium\jdk-17.x.x-hotspot
```

---

### Checkpoint 3: Apache Maven Installation
```powershell
mvn -v
```
**Expected Output:**
```text
Apache Maven 3.9.x (...)
Maven home: C:\Program Files\apache-maven\...
Java version: 17.0.x, vendor: Eclipse Adoptium, runtime: ...
Default locale: en_US, platform encoding: UTF-8
OS name: "windows 11", version: "10.0", arch: "amd64", family: "windows"
```
> ⚠️ **Checkpoint Fail:** If `'mvn' is not recognized`, close and reopen your terminal or verify that `%MAVEN_HOME%\bin` exists in your system `Path`.

---

### Checkpoint 4: Port 8080 Availability
Jetty uses port **8080** by default. Check that no other process is listening on this port:

**In PowerShell:**
```powershell
Get-NetTCPConnection -LocalPort 8080 -ErrorAction SilentlyContinue
```
**In Command Prompt:**
```cmd
netstat -ano | findstr :8080
```
**Expected Output:**
- If the command returns **empty**, port `8080` is free and ready.
- If a PID is displayed, another process (e.g., Oracle, IIS, Tomcat) is using port 8080. Terminate it using:
  ```powershell
  Stop-Process -Id <PID> -Force
  # Or in CMD:
  taskkill /F /PID <PID>
  ```

---

### Checkpoint 5: Console UTF-8 Encoding (Recommended for Windows 11)
To ensure smooth unicode handling in Windows terminal:
```powershell
chcp 65001
```

---

## 📥 Cloning & Setting Up the Project

### 1. Choose a Directory
Open Windows Terminal and navigate to your workspace folder (avoid paths with spaces or special characters):
```powershell
cd C:\
mkdir Projects -ErrorAction SilentlyContinue
cd C:\Projects
```

### 2. Clone the Repository
```powershell
git clone https://github.com/srirammurugesan/Campus_Management_System.git
cd Campus_Management_System
```

*(Alternatively, download the ZIP from GitHub, extract it to `C:\Projects\Campus_Management_System`, and `cd` into that folder).*

---

## 🚀 Building and Running the Web Application

The project uses the **Jetty 12 EE10 Maven Plugin** configured in `pom.xml`.

### Step 1: Clean and Compile
```powershell
mvn clean compile
```
**Checkpoint:** Look for `[INFO] BUILD SUCCESS`. This creates compiled bytecode inside `target\classes`.

### Step 2: Package the Application (WAR Generation)
```powershell
mvn clean package
```
**Checkpoint:**
- Look for `[INFO] Building war: ...\target\campus-student-management.war`.
- Ensure the file `target\campus-student-management.war` exists:
  ```powershell
  Test-Path target\campus-student-management.war
  # Returns True
  ```

### Step 3: Start the Embedded Jetty Web Server
```powershell
mvn jetty:run
```

**Checkpoint Indicators in Terminal:**
```text
[INFO] --- jetty:12.0.14:run (default-cli) @ campus-student-management ---
[INFO] Configuring Jetty for project: campus-student-management
[INFO] Context path = /
[INFO] Webapp directory = C:\Projects\Campus_Management_System\src\main\webapp
[INFO] Started ServerConnector@...{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
[INFO] Started oejs.Server@...
```

> 🛡️ **Windows Defender Alert:** If a "Windows Defender Firewall" popup appears, click **"Allow access"** for Private networks.

---

## 🌐 Verifying the Web Interface

Once Jetty is running, open your web browser (Microsoft Edge, Google Chrome, etc.) and visit:

| Page | URL | Description |
| :--- | :--- | :--- |
| **Student List** | [http://localhost:8080/students](http://localhost:8080/students) | Displays initial list of students (Bill, Steve, John) served by `StudentServlet.java` |
| **Add Student Form** | [http://localhost:8080/student.html](http://localhost:8080/student.html) | HTML form to add a student (Name & Course) |

### Functional Test Verification:
1. Navigate to `http://localhost:8080/student.html`.
2. Enter:
   - **Name**: `Alice`
   - **Course**: `Cloud Computing`
3. Click **Add Student**.
4. The browser redirects to `http://localhost:8080/students` and shows the newly added student:
   ```text
   • 101 - Bill - Java
   • 102 - Steve - Python
   • 103 - John - C++
   • 104 - Alice - Cloud Computing
   ```

### Stopping the Server
In the terminal running `mvn jetty:run`, press:
```powershell
Ctrl + C
```

---

## 💻 Running the Console Application (`src1` - Optional)

The repository also contains a standalone OOP console-based student management program in the `src1` directory.

### In PowerShell (Windows 11):
```powershell
# Create output directory
if (-not (Test-Path out)) { New-Item -ItemType Directory -Path out }

# Compile all source files in src1
javac -d out (Get-ChildItem -Recurse -Path src1 -Filter *.java | ForEach-Object { $_.FullName })

# Run the console Main class
java -cp out com.campus.app.Main
```

### In Command Prompt (CMD):
```cmd
if not exist out mkdir out
javac -d out src1\com\campus\model\*.java src1\com\campus\service\*.java src1\com\campus\contract\*.java src1\com\campus\app\Main.java
java -cp out com.campus.app.Main
```

---

## ❓ Troubleshooting & Windows 11 Gotchas

### 1. `'mvn'` or `'javac'` is not recognized
- **Cause**: PATH variable was updated while the current terminal was already open.
- **Fix**: Close all terminal windows and reopen a new Windows Terminal. If still failing, re-check `JAVA_HOME` and `MAVEN_HOME` in `sysdm.cpl`.

### 2. `Address already in use: bind` / `Port 8080 already in use`
- **Cause**: Another service or previous Jetty instance is occupying port 8080.
- **Fix**:
  ```powershell
  # Find PID using port 8080
  Get-NetTCPConnection -LocalPort 8080
  # Stop the process
  Stop-Process -Id <PID> -Force
  ```
  *Alternative*: Change the port in `pom.xml` under `<jetty-ee10-maven-plugin>`:
  ```xml
  <httpConnector>
      <port>8081</port>
  </httpConnector>
  ```

### 3. Java Version Incompatibility (`Unsupported class file major version` or `java.lang.UnsupportedClassVersionError`)
- **Cause**: Project bytecode requires Java 17+, but an older JDK (e.g. Java 8 or 11) is active.
- **Fix**: Run `java -version` and `javac -version`. Make sure both output version `17.x` or higher.

### 4. PowerShell Script Execution Disabled (`PSSecurityException`)
- If running automation scripts in PowerShell is blocked:
  ```powershell
  Set-ExecutionPolicy -Scope CurrentUser RemoteSigned -Force
  ```

### 5. Windows Defender SmartScreen / Long Paths
- Windows 11 allows long file paths. If you encounter file path errors when Maven unpacks dependencies, run in PowerShell (Admin):
  ```powershell
  New-ItemProperty -Path "HKLM:\SYSTEM\CurrentControlSet\Control\FileSystem" `
    -Name "LongPathsEnabled" -Value 1 -PropertyType DWORD -Force
  ```

---

## 📌 Summary Quick-Card for Windows 11

```powershell
# 1. Verification
java -version    # Must be 17+
mvn -v           # Must be 3.8+

# 2. Navigate to project
cd C:\Projects\Campus_Management_System

# 3. Build & Run
mvn clean compile
mvn jetty:run

# 4. Open in Browser
Start-Process "http://localhost:8080/students"
```
