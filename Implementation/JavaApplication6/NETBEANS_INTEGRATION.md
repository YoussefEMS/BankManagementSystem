# NetBeans Maven Integration Guide

## How to Run in Apache NetBeans

### Method 1: NetBeans GUI (Easiest) ⭐
1. Open the project in NetBeans (File → Open Project, select folder with pom.xml)
2. Right-click the project in Project Explorer
3. Click **Run** (or press **F6**)
4. NetBeans automatically executes `mvn clean javafx:run`
5. Application window opens

**Why this works**: The `nbactions.xml` file tells NetBeans that the "run" action should invoke the `javafx:run` Maven goal.

### Method 2: NetBeans Maven Runner
1. Right-click project → **Run Maven** → **Goals...**
2. Type: `clean javafx:run`
3. Click **Execute**

### Method 3: Terminal/Command Prompt
```bash
cd /path/to/JavaApplication6
mvn clean javafx:run
```

### Method 4: NetBeans Output Window
1. View → Output → Output (shows build logs)
2. Run menu → Set Project Configuration → Customize
3. Modify the run command to include `javafx:run`

## Maven Goals Reference

### Build Project
```bash
mvn clean compile
```

### Package JAR
```bash
mvn clean package
```

### Run Application
```bash
mvn clean javafx:run
```

### Debug Mode
```bash
mvn clean javafx:run@ide-debug
```

### Run Tests (if added)
```bash
mvn test
```

### View Dependencies
```bash
mvn dependency:tree
```

## NetBeans File → nbactions.xml

The `nbactions.xml` file in the project root defines Maven actions for NetBeans:

```xml
<action>
    <actionName>run</actionName>
    <goals>
        <goal>clean</goal>
        <goal>javafx:run</goal>
    </goals>
</action>
```

This means: When you click "Run" in NetBeans, it executes `mvn clean javafx:run`.

## Troubleshooting NetBeans Maven Integration

### Issue: "Run" button is greyed out
- **Solution**: Right-click project → Properties → check it's recognized as Maven project

### Issue: NetBeans doesn't show "Run Maven" option
- **Solution**: Ensure pom.xml is in project root and project is properly opened

### Issue: "javafx:run" goal not found
- **Solution**: Check pom.xml includes:
  ```xml
  <plugin>
      <groupId>org.openjfx</groupId>
      <artifactId>javafx-maven-plugin</artifactId>
      <version>0.0.8</version>
      <configuration>
          <mainClass>com.bms.BankManagementSystemApp</mainClass>
      </configuration>
  </plugin>
  ```

### Issue: Build fails after opening in NetBeans
- **Solution**: 
  1. Right-click project → Clean and Build
  2. Or in terminal: `mvn clean install`

## Maven POM Configuration for JavaFX

The `pom.xml` includes:

```xml
<!-- OpenJFX Dependencies -->
<dependency>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-controls</artifactId>
    <version>22.0.1</version>
</dependency>

<!-- JavaFX Maven Plugin (enables javafx:run goal) -->
<plugin>
    <groupId>org.openjfx</groupId>
    <artifactId>javafx-maven-plugin</artifactId>
    <version>0.0.8</version>
    <configuration>
        <mainClass>com.bms.BankManagementSystemApp</mainClass>
    </configuration>
</plugin>
```

This allows Maven to:
1. Download JavaFX modules and native libraries
2. Execute the JavaFX Application
3. Handle module dependencies automatically

## NetBeans Project Recognition

For NetBeans to recognize this as a Maven project:
- ✅ pom.xml must be in the root directory
- ✅ File structure must be: `src/main/java`, `src/main/resources`
- ✅ GroupId, ArtifactId, Version must be defined in pom.xml
- ✅ Parent POM is optional (we don't use it)

NetBeans automatically:
- Detects the Maven project structure
- Creates `.gradle` and `target` directories for builds
- Provides Maven commands in menus
- Uses nbactions.xml for custom goal bindings

## Keyboard Shortcuts

| Action | Shortcut |
|--------|----------|
| Run | F6 |
| Debug | Ctrl+F5 |
| Build | F11 |
| Clean Build | Shift+F11 |
| Compile | F9 |

## Output and Logs

Application output appears in:
1. **NetBeans Output window** (View → Output → Output)
2. **Build log** shows Maven execution details
3. **Console output** shows System.out.println() messages from the application

## Environment Variables (Optional)

You can set these before running in NetBeans:

```bash
export MAVEN_OPTS="-Xmx512m -Xms256m"
export JAVA_HOME=/path/to/jdk17
```

Then restart NetBeans for changes to take effect.

## Summary

✅ **Recommended Way to Run**: 
- Open project in NetBeans → Press F6

✅ **Why It Works**: 
- nbactions.xml tells NetBeans to run `mvn clean javafx:run`

✅ **What Happens**: 
1. Maven downloads dependencies (first time only)
2. Compiles source code
3. Packages as JAR
4. Launches JavaFX application window
5. Application connects to PostgreSQL database

---

**All three use cases work automatically via NetBeans!**
