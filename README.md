# JerichoCooldowns
A lightweight plugin that applies cooldowns to ender pearls, golden apples, and enchanted golden apples. This is great for PvP servers, and is essentially a replacement for [CooldownsX](https://www.spigotmc.org/resources/cooldownsx.41981/) which stopped being maintained in 2023. Currently, it's just a proof of concept and is being used on [Torrent Network](https://www.torrentsmp.com) on our Lifesteal server. This is literally my first plugin so don't judge the code too harshly. It works and shouldn't cause lag.

If you encounter bugs, please create a Github issue.

# Roadmap  
- Make the enchanted golden apple cooldown separate
- Add optional messages when the player is on cooldown (currently it's just the cooldown overlay that the vanilla ender pearl has)
- Add configuration options for other items like totems of undying and crystals
- Command to reload the configuration files
- Make the code a bit cleaner

#Compiling  
This project uses [maven](https://maven.apache.org/) so it's obviously a dependency for compiling. I will eventually put this on SpigotMC to provide binaries and share the project, or possibly use Github releases.
1. Either `git clone` the repo then `cd JerichoCooldowns` or download the source code and extract it into a local directory, then open it in terminal/command prompt.
2. Run `mvn clean package`
3. The .jar file will appear in `JerichoCooldowns/target`
