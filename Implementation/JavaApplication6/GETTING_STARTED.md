# GETTING STARTED - Step by Step

## 🎯 Your Goal
Run the Bank Management System in NetBeans and test two features.

---

## 📋 STEP 1: Prepare Your Database (5 minutes)

### On Windows with PostgreSQL Installed:

**Open Command Prompt (cmd.exe) and run:**

```bash
# Create the database
createdb -U postgres bank_management_system
```

**Then connect and load the schema:**

```bash
# Load schema (creates tables)
psql -U postgres -d bank_management_system -f "C:\Users\Youssef\Documents\IAcceptDefeat\JavaApplication6\src\main\resources\db\schema.sql"

# Load sample data
psql -U postgres -d bank_management_system -f "C:\Users\Youssef\Documents\IAcceptDefeat\JavaApplication6\src\main\resources\db\seed.sql"
```

**Verify the database was created:**
```bash
psql -U postgres -l
# You should see "bank_management_system" in the list
```

✅ **Database is ready when you see no errors**

---

## 🖥️ STEP 2: Open Project in NetBeans (2 minutes)

1. **Launch NetBeans**

2. **Click**: File → Open Project

3. **Navigate to**: `C:\Users\Youssef\Documents\IAcceptDefeat\JavaApplication6`

4. **Select the folder** (not a file)

5. **Click**: Open Project

6. **Wait** for NetBeans to:
   - Recognize it as a Maven project
   - Download dependencies (first time takes 1-2 minutes)
   - Index source code

✅ **Project is loaded when you see the files in the left panel**

---

## ▶️ STEP 3: Run the Application (1 minute)

### Easiest Way:
- **Press F6** or go to Run → Run Project

### Alternative:
- Right-click project → Run

### What Happens:
1. NetBeans compiles the project
2. Runs `mvn clean javafx:run` (automatic)
3. Application window opens (wait 5-10 seconds for startup)

✅ **Application is running when you see the menu bar with "Operations"**

---

## ✅ STEP 4: Test Feature #1 - View Account Balance (UC-02)

**In the application window:**

1. **Click Menu**: Operations → View Account Balance (UC-02)

2. **You see**: Input section with "Account Number:" field

3. **Type**: `ACC001`

4. **Click**: View button

5. **You should see**:
   - Account Number: ACC001
   - Status: ACTIVE
   - Balance: 4030.00
   - Currency: USD

**Try an invalid account:**
- Type: `INVALID999`
- Click: View
- You see: "No account found" message (no error popup)

✅ **UC-02 works when you see the balance displayed**

---

## ✅ STEP 5: Test Feature #2 - View Transaction History (UC-04)

**In the application window:**

1. **Click Menu**: Operations → View Transaction History (UC-04)

2. **You see**: Input section with fields for:
   - Account Number
   - Start Date (optional)
   - End Date (optional)
   - Transaction Type (optional)

3. **Type Account**: `ACC001`

4. **Leave dates and type empty** (or select "All" for type)

5. **Click**: Search button

6. **You should see**: Table with 10 transactions showing:
   - Timestamp (most recent first)
   - Type (Deposit, Withdrawal, InterestPosting, etc.)
   - Amount (e.g., 2000.00, 500.00)
   - Note (description)
   - Balance After

**Try filtering:**
- Account: `ACC001`
- Type: Select "Deposit"
- Click: Search
- You see: Only 1 transaction (the deposits)

**Try invalid account:**
- Account: `INVALID999`
- Click: Search
- You see: Empty table with "No transactions found" (no error popup)

✅ **UC-04 works when you see the transaction table**

---

## 🎉 Congratulations!

You have successfully:
- ✅ Set up the database
- ✅ Opened the project in NetBeans
- ✅ Run the application
- ✅ Tested both use cases
- ✅ Verified the "no errors" behavior

---

## 📚 Next Steps

### To Learn More:
- Read `README.md` for architecture explanation
- Read `PROJECT_STRUCTURE.md` for code organization
- Review source code in `src/main/java/com/bms/`

### To Modify:
- Edit `src/main/resources/application.properties` for different database
- Edit screens for UI changes
- Add new domain controllers for new features

### To Build for Distribution:
```bash
mvn clean package
# Creates JAR in target/ folder
```

---

## 🆘 Quick Troubleshooting

### "Cannot connect to database"
- Check PostgreSQL is running
- Check database exists: `psql -U postgres -l`
- Check username/password in `application.properties`

### "Build fails with dependency errors"
- Delete `.m2` folder and try again (forces re-download)
- Or in terminal: `mvn clean compile`

### "Application window doesn't open"
- Check Java version: `java -version` (should be 17+)
- Wait longer (first startup can take 10 seconds)
- Check console for error messages

### "No transactions appear"
- Verify seed.sql was loaded
- Check database: `psql -U postgres -d bank_management_system -c "SELECT COUNT(*) FROM transaction;"`

---

## 📞 Need Help?

1. **See QUICKSTART.md** for quick reference
2. **See README.md** for comprehensive documentation
3. **See NETBEANS_INTEGRATION.md** for NetBeans-specific help
4. **Check the Troubleshooting section** in README.md

---

## ✨ Sample Data for Testing

| Account | Customer | Balance | Sample |
|---------|----------|---------|--------|
| **ACC001** | Ahmed Hassan | 4030.00 | **Try This First** |
| ACC002 | Ahmed Hassan | 14470.00 | Savings account |
| ACC003 | Fatima Ali | 3000.00 | Smaller balance |
| ACC004 | Mohamed Ibrahim | 25604.50 | Large balance |

---

## 🚀 You're Ready!

Everything is set up and ready to go. Start with the quick test above, then explore the code at your own pace.

**Enjoy the Bank Management System!**
