# PostgreSQL and pgAdmin 4 Setup Guide (Ubuntu & Windows)

A beginner-friendly, step-by-step guide to installing and setting up **PostgreSQL** (the database) and **pgAdmin 4** (the visual management tool).

---

## 💡 Quick Overview
- **PostgreSQL**: The database server that runs in the background and stores your data.
- **pgAdmin 4**: A graphical desktop app (GUI) to view tables, run SQL queries, and manage your database easily.

---

## 🪟 Part 1: Windows Setup

### Step 1: Download the All-in-One Installer
1. Go to the official PostgreSQL download page:  
   👉 **[https://www.postgresql.org/download/windows/](https://www.postgresql.org/download/windows/)**
2. Click **"Download the installer"** (powered by EnterpriseDB).
3. Select the latest version for **Windows x86-64** (e.g., PostgreSQL 16 or 15) and download the `.exe` file.

> **Alternative (Terminal / winget):**  
> If you prefer using PowerShell (Admin):  
> `winget install PostgreSQL.PostgreSQL.16`

---

### Step 2: Run the Installer
1. Double-click the downloaded `.exe` file.
2. Click **Next** on the Welcome screen.
3. **Installation Directory**: Keep default (`C:\Program Files\PostgreSQL\16`) and click **Next**.
4. **Select Components**: Keep all checked:
   - ✅ PostgreSQL Server
   - ✅ pgAdmin 4
   - ✅ Command Line Tools
   - (You can uncheck *Stack Builder* at the end if prompted).
   Click **Next**.
5. **Data Directory**: Keep default and click **Next**.
6. **Set Password**:
   - Enter a password for the superuser (`postgres`).
   - ⚠️ **Important:** Write this password down! (e.g., `admin` or `root` or your own secure password). You will need it to connect.
   Click **Next**.
7. **Port**: Keep default `5432`. Click **Next**.
8. **Advanced Options (Locale)**: Keep default `[Default locale]`. Click **Next**.
9. Click **Next** to begin installation.
10. At the final screen, **uncheck "Launch Stack Builder"** and click **Finish**.

---

### Step 3: Open pgAdmin on Windows
1. Press `Win` key, search for **pgAdmin 4**, and open it.
2. On first launch, it will ask you to set a **Master Password** for pgAdmin. (Set something easy like `admin` so you don't forget).
3. Skip to [Part 3: Connecting pgAdmin to PostgreSQL](#-part-3-connecting-pgadmin-to-postgresql).

---

## 🐧 Part 2: Ubuntu Setup

### Step 1: Install PostgreSQL
Open your terminal (`Ctrl + Alt + T`) and run:

```bash
# 1. Update package list
sudo apt update

# 2. Install PostgreSQL and extra tools
sudo apt install postgresql postgresql-contrib -y
```

### Step 2: Verify PostgreSQL is Running
Check the status of the PostgreSQL service:

```bash
sudo systemctl status postgresql
```
You should see `active (running)`.  
*(Press `q` to exit the status view).*

If it is not running, start it with:
```bash
sudo systemctl enable --now postgresql
```

### Step 3: Set Password for the `postgres` User
By default on Ubuntu, PostgreSQL uses peer authentication. Set a password for the database `postgres` user:

```bash
# 1. Switch to PostgreSQL shell
sudo -u postgres psql
```

Inside the `postgres=#` prompt, type:
```sql
ALTER USER postgres WITH PASSWORD 'your_password_here';
```
*(Replace `your_password_here` with your desired password, e.g., `admin`)*.

Then exit the prompt:
```sql
\q
```

---

### Step 4: Install pgAdmin 4 on Ubuntu
Run these commands in terminal to install the desktop version of pgAdmin 4:

```bash
# 1. Install curl and gpg if not already present
sudo apt install curl gpg -y

# 2. Add pgAdmin public key
curl -fsS https://www.pgadmin.org/static/packages_pgadmin_org.pub | sudo gpg --dearmor -o /usr/share/keyrings/packages-pgadmin-org.gpg

# 3. Add the pgAdmin repository
sudo sh -c 'echo "deb [signed-by=/usr/share/keyrings/packages-pgadmin-org.gpg] https://ftp.postgresql.org/pub/pgadmin/pgadmin4/apt/$(lsb_release -cs) pgadmin4 main" > /etc/apt/sources.list.d/pgadmin4.list'

# 4. Update packages and install pgAdmin Desktop
sudo apt update
sudo apt install pgadmin4-desktop -y
```

### Step 5: Launch pgAdmin 4 on Ubuntu
- Open your Application menu and search for **pgAdmin 4**, or run in terminal:
  ```bash
  pgadmin4 &
  ```
- Set a **Master Password** when prompted on first launch.

---

## 🔌 Part 3: Connecting pgAdmin to PostgreSQL

Once pgAdmin 4 is open:

1. In the left sidebar, right-click on **Servers** ➔ **Register** ➔ **Server...**
2. In the **General** tab:
   - **Name**: Enter any friendly name, e.g., `Local PostgreSQL`
3. In the **Connection** tab:
   - **Host name/address**: `localhost`
   - **Port**: `5432`
   - **Maintenance database**: `postgres`
   - **Username**: `postgres`
   - **Password**: *(The password you set during installation / Step 3)*
   - ✅ Check **Save password?**
4. Click **Save**.

Your server will now show up in the left sidebar under **Servers**!

---

## 🧪 Part 4: Test Your Database (Quick Check)

Let's test that everything is working:

1. Click on **Servers** ➔ **Local PostgreSQL** ➔ **Databases**.
2. Right-click on **Databases** ➔ **Create** ➔ **Database...**
   - **Database**: `test_db`
   - Click **Save**.
3. Select your new `test_db` in the sidebar.
4. Click **Tools** in the top menu ➔ **Query Tool** (or press the query icon).
5. In the SQL editor, paste and run:
   ```sql
   -- Create a sample table
   CREATE TABLE students (
       id SERIAL PRIMARY KEY,
       name VARCHAR(100),
       email VARCHAR(100)
   );

   -- Insert a record
   INSERT INTO students (name, email) 
   VALUES ('John Doe', 'john@example.com');

   -- View data
   SELECT * FROM students;
   ```
6. Click the **Execute / Run (▶)** button (or press `F5`).
7. You should see your student record in the **Data Output** tab below. 🎉

---

## 🛠️ Common Beginner Issues & Fixes

### 1. "Connection refused" or Server not reachable
- **Cause:** PostgreSQL service isn't running.
- **Fix on Windows:** Press `Win + R`, type `services.msc`, locate **postgresql-x64-XX**, right-click and click **Start**.
- **Fix on Ubuntu:** Run `sudo systemctl restart postgresql`.

### 2. "FATAL: password authentication failed for user 'postgres'"
- **Cause:** Incorrect password entered in pgAdmin.
- **Fix on Windows:** Re-enter the exact password you typed during the wizard.
- **Fix on Ubuntu:** Reset the password using:
  ```bash
  sudo -u postgres psql -c "ALTER USER postgres WITH PASSWORD 'newpassword';"
  ```

### 3. Port 5432 already in use
- Another database service or older PostgreSQL version might be running.
- Check what is using port 5432:
  - **Windows (cmd):** `netstat -ano | findstr 5432`
  - **Ubuntu:** `sudo lsof -i :5432`
