## BotWithUs Script: DisassembleWithUs
- This is an IntelliJ IDEA project.
- You can subscribe to it on our marketplace here: [BotWithUs Website](https://botwithus.net/sdn)
- Here is the script's Wiki page: Coming soon

### Description
- Disassembles a queue of items for you
- Logs out when done or when various issues are detected.

### Info
- The easiest way to get setup is to clone/download this repo, and from IntelliJ IDEA, File > open the folder.
- When you first open the code, it's usually a good idea to refresh gradle dependencies to resolve any dependency errors.
- You'll find the script itself located at ``src/main/java/net/botwithus/DisassembleWithUs.kt``
- You'll find the graphics context (which allows you to draw UI with ImGui) at ``src/main/java/net/botwithus/DisassembleWithUsGraphicsContext.kt``
- If you're having any trouble, speak up in the public-scripters channel on discord!
- Come ask us questions in our discord: [BotWithUs Discord](https://discord.gg/botwithus)

### After downloading/copying
- You should change your project name in ``settings.gradle.kts``
- You should make sure gradle is configured with JDK 20 (OpenJDK or Corretto) ``File > Settings > Build, Execution, Deployment > Build Tools > Gradle``
- Rename the script and graphics context to something appropriate.
- Update script.ini to relevant info